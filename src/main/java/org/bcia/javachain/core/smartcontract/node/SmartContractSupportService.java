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
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.SmartContractSupportGrpc;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.SmartcontractShim;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportService extends SmartContractSupportGrpc
        .SmartContractSupportImplBase {

    private static Log logger = LogFactory.getLog(SmartContractSupportService.class);

    private static Map<String, StreamObserver<SmartcontractShim.SmartContractMessage>> chaincodeIdAndStreamObserverMap = new
            HashMap<>();

    @Override
    public StreamObserver<SmartcontractShim.SmartContractMessage> register(StreamObserver<SmartcontractShim.SmartContractMessage>
                                                                           responseObserver) {

        return new StreamObserver<SmartcontractShim.SmartContractMessage>() {

            @Override
            public void onNext(SmartcontractShim.SmartContractMessage message) {
                logger.info("Got message:" + message.toString());

                if (message.getType().equals(SmartcontractShim.SmartContractMessage.Type.ERROR)) {
                    return;
                }

                if (message.getType().equals(SmartcontractShim.SmartContractMessage.Type.REGISTER)) {

                    try {
                        saveChaincodeStreamObserver(message, responseObserver);
                    } catch (InvalidProtocolBufferException e) {
                        logger.error(e.getMessage(), e);
                    }

                    SmartcontractShim.SmartContractMessage responseMessage = SmartcontractShim.SmartContractMessage.newBuilder()
                            .setType(SmartcontractShim.SmartContractMessage.Type.REGISTERED).build();
                    responseObserver.onNext(responseMessage);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    responseMessage = SmartcontractShim.SmartContractMessage.newBuilder().setType(SmartcontractShim.SmartContractMessage.Type.READY).build();
                    responseObserver.onNext(responseMessage);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Common.GroupHeader groupHeader = Common.GroupHeader.newBuilder().setType(Common.HeaderType.ENDORSER_TRANSACTION.getNumber()).build();
                    Common.Header header = Common.Header.newBuilder().setGroupHeader(groupHeader.toByteString()).build();
                    ProposalPackage.Proposal proposal = ProposalPackage.Proposal.newBuilder().setHeader(header
                            .toByteString()).build();
                    ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
                            .setProposalBytes(proposal.toByteString()).build();
                    responseMessage = SmartcontractShim.SmartContractMessage.newBuilder().setType(SmartcontractShim.SmartContractMessage.Type.INIT).setProposal(signedProposal).build();
                    responseObserver.onNext(responseMessage);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                if (message.getType().equals(SmartcontractShim.SmartContractMessage.Type.COMPLETED)) {
                    return;
                }

                if (message.getType().equals(SmartcontractShim.SmartContractMessage.Type.KEEPALIVE)) {
                    return;
                }

                if (message.getType().equals(SmartcontractShim.SmartContractMessage.Type.GET_STATE)) {
                    SmartcontractShim.SmartContractMessage responseMessage = SmartcontractShim.SmartContractMessage.newBuilder().setType
                            (SmartcontractShim.SmartContractMessage.Type.RESPONSE).setPayload(ByteString.copyFrom("aaa"
                            .getBytes()))
                            .build();
                    responseObserver.onNext(responseMessage);
                    return;
                }

                if (message.getType().equals(SmartcontractShim.SmartContractMessage.Type.PUT_STATE)) {
                    SmartcontractShim.SmartContractMessage responseMessage = SmartcontractShim.SmartContractMessage.newBuilder().setType
                            (SmartcontractShim.SmartContractMessage.Type.RESPONSE)
                            .build();
                    responseObserver.onNext(responseMessage);
                    return;
                }

                if(message.getType().equals(SmartcontractShim.SmartContractMessage.Type.DEL_STATE)) {
                    return;
                }

                if(message.getType().equals(SmartcontractShim.SmartContractMessage.Type.GET_HISTORY_FOR_KEY)) {
                    return;
                }

            }

            @Override
            public void onError(Throwable throwable) {
                logger.error(throwable.getMessage(), throwable);
            }

            @Override
            public void onCompleted() {
                logger.info("Finished Register.");
            }

        };

    }

    private void saveChaincodeStreamObserver(SmartcontractShim.SmartContractMessage message,
                                             StreamObserver<SmartcontractShim.SmartContractMessage> streamObserver) throws
            InvalidProtocolBufferException {
        if (!message.getType().equals(SmartcontractShim.SmartContractMessage.Type.REGISTER)) {
            return;
        }
        Smartcontract.SmartContractID smartContractID = Smartcontract.SmartContractID.parseFrom(message.getPayload());
        String name = smartContractID.getName();
        if (name == null || name.length() == 0) {
            return;
        }
        chaincodeIdAndStreamObserverMap.put(name, streamObserver);
        logger.info("add chaincode streamObserver: " + name + " " + streamObserver.toString());
    }

    public static void send(String smartcontractId, SmartcontractShim.SmartContractMessage message) {
        StreamObserver<SmartcontractShim.SmartContractMessage> chaincodeMessageStreamObserver =
                chaincodeIdAndStreamObserverMap.get(smartcontractId);
        if (chaincodeMessageStreamObserver == null) {
            logger.info("no stream observer for " + smartcontractId);
            return;
        }
        chaincodeMessageStreamObserver.onNext(message);
    }

    public static void init(String chaincodeId, ProposalPackage.SignedProposal signedProposal) {
        SmartcontractShim.SmartContractMessage message = SmartcontractShim.SmartContractMessage.newBuilder().setType(SmartcontractShim.SmartContractMessage.Type.INIT).setProposal(signedProposal).build();
        send(chaincodeId, message);
    }

    public static void invoke(String chaincodeId, ProposalPackage.SignedProposal signedProposal) {
        logger.info("invoke " + chaincodeId);
        SmartcontractShim.SmartContractMessage message = SmartcontractShim.SmartContractMessage.newBuilder().setType(SmartcontractShim.SmartContractMessage.Type.TRANSACTION).setProposal(signedProposal).build();
        send(chaincodeId, message);
    }

    private String getChaincodeId(SmartcontractShim.SmartContractMessage message) {
        String chaincodeIdStr = "";
        try {
            chaincodeIdStr = Smartcontract.SmartContractID.parseFrom(message.getPayload()).getName();
        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        } finally {
            return chaincodeIdStr;
        }
    }

}
