/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.core.smartcontract.node;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.javachain.protos.node.SmartContractSupportGrpc;
import org.bcia.javachain.protos.node.Smartcontract;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.bcia.javachain.protos.common.Common.*;
import static org.bcia.javachain.protos.node.ProposalPackage.Proposal;
import static org.bcia.javachain.protos.node.ProposalPackage.SignedProposal;
import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage;

/**
 * 智能合约service，负责接收和处理gRPC消息
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportService extends SmartContractSupportGrpc.SmartContractSupportImplBase {

    private static Log logger = LogFactory.getLog(SmartContractSupportService.class);

    /**
     * 以smartContractId为key,保存gRPC客户端
     */
    private static Map<String, StreamObserver<SmartContractMessage>> smartContractIdAndStreamObserverMap =
            Collections.synchronizedMap(new HashMap<String, StreamObserver<SmartContractMessage>>());

    /**
     * 处理智能合约register信息（命令）
     *
     * @param message        智能合约发送过来的信息（命令）
     * @param streamObserver 智能合约gRPC通道
     */
    private void handleRegister(SmartContractMessage message, StreamObserver<SmartContractMessage> streamObserver) {
        try {
            // 保存智能合约编号
            saveChaincodeStreamObserver(message, streamObserver);

            // 发送注册成功命令
            SmartContractMessage responseMessage = SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.REGISTERED).build();
            streamObserver.onNext(responseMessage);

            // 暂停
            Thread.sleep(100);

            // 发送ready命令
            responseMessage = SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.READY).build();
            streamObserver.onNext(responseMessage);

            // 暂停
            Thread.sleep(100);

            // 发送init命令
            GroupHeader groupHeader = GroupHeader.newBuilder().setType(HeaderType.ENDORSER_TRANSACTION.getNumber()).build();
            Header header = Header.newBuilder().setGroupHeader(groupHeader.toByteString()).build();
            Proposal proposal = Proposal.newBuilder().setHeader(header.toByteString()).build();
            SignedProposal signedProposal = SignedProposal.newBuilder().setProposalBytes(proposal.toByteString()).build();
            responseMessage = SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.INIT).setProposal(signedProposal).build();
            streamObserver.onNext(responseMessage);
        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public StreamObserver<SmartContractMessage> register(StreamObserver<SmartContractMessage> responseObserver) {

        return new StreamObserver<SmartContractMessage>() {

            @Override
            public void onNext(SmartContractMessage message) {
                logger.info(String.format("Got message: groupId[%s] txId[%s] messageStr[%s]", message.getGroupId(), message.getTxid(), message.toString()));

                if (message.getType().equals(SmartContractMessage.Type.ERROR)) {
                    return;
                }

                // 处理register消息
                if (message.getType().equals(SmartContractMessage.Type.REGISTER)) {
                    handleRegister(message, responseObserver);
                    return;
                }

                if (message.getType().equals(SmartContractMessage.Type.COMPLETED)) {
                    return;
                }

                if (message.getType().equals(SmartContractMessage.Type.KEEPALIVE)) {
                    responseObserver.onNext(message);
                    return;
                }

                if (message.getType().equals(SmartContractMessage.Type.GET_STATE)) {
                    SmartContractMessage responseMessage = SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.RESPONSE).setPayload(ByteString.copyFrom("aaa".getBytes())).build();
                    responseObserver.onNext(responseMessage);
                    return;
                }

                if (message.getType().equals(SmartContractMessage.Type.PUT_STATE)) {
                    SmartContractMessage responseMessage = SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.RESPONSE).build();
                    responseObserver.onNext(responseMessage);
                    return;
                }

                if (message.getType().equals(SmartContractMessage.Type.DEL_STATE)) {
                    return;
                }

                if (message.getType().equals(SmartContractMessage.Type.GET_HISTORY_FOR_KEY)) {
                    return;
                }

            }

            @Override
            public void onError(Throwable throwable) {
                logger.error(throwable.getMessage(), throwable);
            }

            @Override
            public void onCompleted() {
                logger.info("SmartContract completed");
            }

        };

    }

    /**
     * 保存gRPC客户端
     *
     * @param message        接收到的消息
     * @param streamObserver gRPC客户端
     * @throws InvalidProtocolBufferException
     */
    private void saveChaincodeStreamObserver(SmartContractMessage message, StreamObserver<SmartContractMessage> streamObserver) throws InvalidProtocolBufferException {
        // 只有注册时才保存
        if (!message.getType().equals(SmartContractMessage.Type.REGISTER)) {
            return;
        }
        // 从message的payload中获取smartContractID
        Smartcontract.SmartContractID smartContractID = Smartcontract.SmartContractID.parseFrom(message.getPayload());
        String name = smartContractID.getName();
        if (name == null || name.length() == 0) {
            return;
        }
        // 保存gRPC客户端
        smartContractIdAndStreamObserverMap.put(name, streamObserver);
        logger.info(String.format("add SmartContract streamObserver: name[%s] streamObserver[%s]", name, streamObserver.toString()));
    }

    /**
     * 发送消息给gRPC客户端
     *
     * @param smartContractId 智能合约编号
     * @param message         消息
     */
    public static void send(String smartContractId, SmartContractMessage message) {
        String str = "";
        Set<Map.Entry<String, StreamObserver<SmartContractMessage>>> entries = smartContractIdAndStreamObserverMap.entrySet();
        for (Map.Entry<String, StreamObserver<SmartContractMessage>> entry : entries) {
            str = str + " " + entry.getKey();
        }
        logger.info("key : " + str);
        StreamObserver<SmartContractMessage> streamObserver = smartContractIdAndStreamObserverMap.get(smartContractId);
        if (streamObserver == null) {
            logger.info(String.format("no stream observer for %s", smartContractId));
            return;
        }
        streamObserver.onNext(message);
    }

    /**
     * 初始化智能合约
     *
     * @param smartContractId      智能合约编号
     * @param smartContractMessage 发送的消息
     */
    public static void init(String smartContractId, SmartContractMessage smartContractMessage) {
        logger.info("init " + smartContractId);
        // 设置消息的type为INIT
        SmartContractMessage message = SmartContractMessage.newBuilder().mergeFrom(smartContractMessage).setType(SmartContractMessage.Type.INIT).build();
        send(smartContractId, message);
    }

    /**
     * invoke智能合约
     *
     * @param smartContractId      智能合约编号
     * @param smartContractMessage 消息
     */
    public static void invoke(String smartContractId, SmartContractMessage smartContractMessage) {
        logger.info("invoke " + smartContractId);
        // 修改消息的type为TRANSACTION
        SmartContractMessage message = SmartContractMessage.newBuilder().mergeFrom(smartContractMessage).setType(SmartContractMessage.Type.TRANSACTION).build();
        send(smartContractId, message);
    }

    /**
     * 从message中获取智能合约编号
     *
     * @param message 消息
     * @return
     */
    private String getSmartContractId(SmartContractMessage message) {
        String smartContractIdStr = "";
        try {
            smartContractIdStr = Smartcontract.SmartContractID.parseFrom(message.getPayload()).getName();
        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        }
        return smartContractIdStr;
    }

    public static void main(String[] args) {
        SmartContractMessage build = SmartContractMessage.newBuilder().setTxid("t1").setGroupId("g1").build();
        SmartContractMessage build1 = SmartContractMessage.newBuilder().mergeFrom(build).build();

        build = SmartContractMessage.newBuilder().setTxid("t2").setGroupId("g2").build();

        System.out.println(build.getTxid() + " " + build.getGroupId());
        System.out.println(build1.getTxid() + " " + build1.getGroupId());
    }

}
