/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.smartcontract.node;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.TxSimulationResults;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.julongchain.core.node.util.NodeUtils;
import org.bcia.julongchain.core.smartcontract.client.SmartContractSupportClient;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.node.*;

import java.util.*;

import static org.bcia.julongchain.core.smartcontract.node.SmartContractRunningUtil.*;
import static org.bcia.julongchain.core.smartcontract.node.TransactionRunningUtil.*;
import static org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage;

/**
 * 智能合约service，负责接收和处理gRPC消息
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportService
        extends SmartContractSupportGrpc.SmartContractSupportImplBase {

    private static Log logger = LogFactory.getLog(SmartContractSupportService.class);

    /**
     * 以smartContractId为key,保存gRPC客户端
     */
    public static Map<String, StreamObserver<SmartContractMessage>>
            smartContractIdAndStreamObserverMap =
            Collections.synchronizedMap(new HashMap<String, StreamObserver<SmartContractMessage>>());

    /**
     * 处理智能合约register信息（命令）
     *
     * @param message        智能合约发送过来的信息（命令）
     * @param streamObserver 智能合约gRPC通道
     */
    private void handleRegister(
            SmartContractMessage message, StreamObserver<SmartContractMessage> streamObserver) {
        try {
            // 保存智能合约编号
            saveSmartContractStreamObserver(message, streamObserver);

            String smartContractId = getSmartContractId(message);

            // 发送注册成功命令
            SmartContractMessage responseMessage =
                    SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.REGISTERED).build();
            streamObserver.onNext(responseMessage);

            // 发送ready命令
            responseMessage =
                    SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.READY).build();
            streamObserver.onNext(responseMessage);

            // 发送init命令
            if (BooleanUtils.isTrue(
                    SmartContractSupportClient.checkSystemSmartContract(smartContractId))) {
                Common.GroupHeader groupHeader =
                        Common.GroupHeader.newBuilder()
                                .setType(Common.HeaderType.ENDORSER_TRANSACTION.getNumber())
                                .build();
                Common.Header header =
                        Common.Header.newBuilder().setGroupHeader(groupHeader.toByteString()).build();
                ProposalPackage.Proposal proposal =
                        ProposalPackage.Proposal.newBuilder().setHeader(header.toByteString()).build();
                ProposalPackage.SignedProposal signedProposal =
                        ProposalPackage.SignedProposal.newBuilder()
                                .setProposalBytes(proposal.toByteString())
                                .build();
                SmartContractEventPackage.SmartContractEvent smartContractEvent =
                        SmartContractEventPackage.SmartContractEvent.newBuilder()
                                .setSmartContractId(smartContractId)
                                .build();
                responseMessage =
                        SmartContractMessage.newBuilder()
                                .setType(SmartContractMessage.Type.INIT)
                                .setProposal(signedProposal)
                                .setSmartContractEvent(smartContractEvent)
                                .build();
                streamObserver.onNext(responseMessage);
            }

            // 设置状态ready
            updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_SEND_INIT);

        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public StreamObserver<SmartContractMessage> register(
            StreamObserver<SmartContractMessage> responseObserver) {

        return new StreamObserver<SmartContractMessage>() {

            @Override
            public void onNext(SmartContractMessage message) {

                String txId = message.getTxid();
                String groupId = message.getGroupId();
                String smartContractId = getSmartContractIdByTxId(txId);

                logger.info(
                        String.format(
                                "Got message: smartContractId[%s] groupId[%s] txId[%s] messageStr[%s]",
                                smartContractId, groupId, txId, message.toString()));

                logger.info(
                        String.format(
                                "smart contract status: smartContractId[%s] smartContractStatus[%s]",
                                smartContractId, getSmartContractStauts(smartContractId)));

                logger.info(
                        String.format(
                                "transaction status: txId[%s] smartContractId[%s] txStatus[%s]",
                                txId, smartContractId, getTxStatusById(smartContractId, txId)));

                // 收到error信息
                if (message.getType().equals(SmartContractMessage.Type.ERROR)) {
                    addTxMessage(smartContractId, txId, message);
                    updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_ERROR);
                    updateTxStatus(smartContractId, txId, TX_STATUS_ERROR);
                    return;
                }

                // 收到register消息
                if (message.getType().equals(SmartContractMessage.Type.REGISTER)) {
                    handleRegister(message, responseObserver);
                    return;
                }

                // 收到complete信息
                if (message.getType().equals(SmartContractMessage.Type.COMPLETED)) {
                    addTxMessage(smartContractId, txId, message);
                    updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_READY);
                    updateTxStatus(smartContractId, txId, TX_STATUS_COMPLETE);
                    return;
                }

                // 收到keepalive信息
                if (message.getType().equals(SmartContractMessage.Type.KEEPALIVE)) {
                    responseObserver.onNext(message);
                    return;
                }

                // 收到getState信息
                if (message.getType().equals(SmartContractMessage.Type.GET_STATE)) {

                    // shim传过来的key
                    String key = message.getPayload().toStringUtf8();

                    logger.info("===============>" + key);

                    try {
                        INodeLedger nodeLedger = NodeUtils.getLedger(groupId);
                        ITxSimulator txSimulator = nodeLedger.newTxSimulator(txId);
                        byte[] worldStateBytes = txSimulator.getState(smartContractId, key);

                        if (worldStateBytes == null) {
                            worldStateBytes = new byte[]{};
                            // 发送读取结果到shim端
                            SmartContractMessage responseMessage =
                                    SmartContractMessage.newBuilder()
                                            .mergeFrom(message)
                                            .setType(SmartContractMessage.Type.RESPONSE)
                                            .setPayload(ByteString.copyFrom(worldStateBytes))
                                            .setTxid(txId)
                                            .setGroupId(groupId)
                                            .build();

                            responseObserver.onNext(responseMessage);
                        } else {
                            // 发送读取结果到shim端
                            SmartContractMessage responseMessage =
                                    SmartContractMessage.newBuilder()
                                            .mergeFrom(message)
                                            .setType(SmartContractMessage.Type.RESPONSE)
                                            .setPayload(ByteString.copyFrom(worldStateBytes))
                                            .setTxid(txId)
                                            .setGroupId(groupId)
                                            .build();

                            responseObserver.onNext(responseMessage);
                        }


                    } catch (LedgerException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                if (message.getType().equals(SmartContractMessage.Type.GET_STATE_BY_RANGE)) {
                    try {
                        SmartContractShim.GetStateByRange getStateByRange = SmartContractShim.GetStateByRange.parseFrom(message.getPayload());
                        String startKey = getStateByRange.getStartKey();
                        String endKey = getStateByRange.getEndKey();

                        INodeLedger nodeLedger = NodeUtils.getLedger(groupId);
                        ITxSimulator txSimulator = nodeLedger.newTxSimulator(txId);
                        IResultsIterator iterator = txSimulator.getStateRangeScanIterator(smartContractId, startKey, endKey);

                        SmartContractShim.QueryResponse.Builder queryResponseBuilder = SmartContractShim.QueryResponse.newBuilder();

                        while (true) {
                            QueryResult queryResult = iterator.next();
                            if (queryResult == null) {
                                break;
                            }
                            VersionedKV kv = (VersionedKV) queryResult.getObj();
                            if(kv.getCompositeKey().getKey().compareTo(endKey) == 1){
                                break;
                            }

                            String key = kv.getCompositeKey().getKey();
                            String namespace = kv.getCompositeKey().getNamespace();
                            byte[] value = kv.getVersionedValue().getValue();

                            KvQueryResult.KV kvProto = KvQueryResult.KV.newBuilder().setKey(key).setNamespace(namespace).setValue(ByteString.copyFrom(value)).build();

                            queryResponseBuilder.addResults(SmartContractShim.QueryResultBytes.newBuilder().setResultBytes(kvProto.toByteString()).build());
                        }


                        SmartContractShim.QueryResponse queryResponse = queryResponseBuilder.build();

                        SmartContractMessage responseMessage =
                                SmartContractMessage.newBuilder()
                                        .mergeFrom(message)
                                        .setType(SmartContractMessage.Type.RESPONSE)
                                        .setPayload(queryResponse.toByteString())
                                        .setTxid(txId)
                                        .setGroupId(groupId)
                                        .build();

                        responseObserver.onNext(responseMessage);

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    } catch (LedgerException e) {
                        e.printStackTrace();
                    }
                    return;
                }


                // 收到putState信息
                if (message.getType().equals(SmartContractMessage.Type.PUT_STATE)) {
                    try {
                        SmartContractShim.PutState putState = SmartContractShim.PutState.parseFrom(message.getPayload());
                        INodeLedger nodeLedger = NodeUtils.getLedger(groupId);
                        ITxSimulator txSimulator = nodeLedger.newTxSimulator(txId);
                        txSimulator.setState(smartContractId, putState.getKey(), putState.getValue().toByteArray());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                    SmartContractMessage responseMessage =
                            SmartContractMessage.newBuilder()
                                    .mergeFrom(message)
                                    .setType(SmartContractMessage.Type.RESPONSE)
                                    .setTxid(txId)
                                    .setGroupId(groupId)
                                    .build();

                    responseObserver.onNext(responseMessage);
                    return;
                }

                // 收到delState信息
                if (message.getType().equals(SmartContractMessage.Type.DEL_STATE)) {
                    return;
                }

                // 收到getHistoryForKey信息
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
    private void saveSmartContractStreamObserver(
            SmartContractMessage message, StreamObserver<SmartContractMessage> streamObserver)
            throws InvalidProtocolBufferException {
        // 只有注册时才保存
        if (!message.getType().equals(SmartContractMessage.Type.REGISTER)) {
            return;
        }
        // 从message的payload中获取smartContractID
        SmartContractPackage.SmartContractID smartContractID =
                SmartContractPackage.SmartContractID.parseFrom(message.getPayload());
        String name = smartContractID.getName();
        if (name == null || name.length() == 0) {
            return;
        }
        // 保存gRPC客户端
        smartContractIdAndStreamObserverMap.put(name, streamObserver);
        // 设置状态为new
        updateSmartContractStatus(name, SMART_CONTRACT_STATUS_NEW);
        logger.info(
                String.format(
                        "add SmartContract streamObserver: name[%s] streamObserver[%s]",
                        name, streamObserver.toString()));
    }

    /**
     * 发送消息给gRPC客户端
     *
     * @param smartContractId 智能合约编号
     * @param message         消息
     */
    public static void send(String smartContractId, SmartContractMessage message) {
        StreamObserver<SmartContractMessage> streamObserver =
                smartContractIdAndStreamObserverMap.get(smartContractId);
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
        SmartContractMessage message =
                SmartContractMessage.newBuilder()
                        .mergeFrom(smartContractMessage)
                        .setType(SmartContractMessage.Type.INIT)
                        .build();
        send(smartContractId, message);
    }

    /**
     * invoke智能合约
     *
     * @param smartContractId      智能合约编号
     * @param smartContractMessage 消息
     */
    public static SmartContractMessage invoke(
            String smartContractId, SmartContractMessage smartContractMessage) {
        logger.info("invoke " + smartContractId);

        // 修改消息的type为TRANSACTION
        SmartContractMessage message =
                SmartContractMessage.newBuilder()
                        .mergeFrom(smartContractMessage)
                        // .setType(SmartContractMessage.Type.TRANSACTION)
                        .build();

        updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_BUSY);
        String txId = smartContractMessage.getTxid();
        addTxId(txId, smartContractId);
        updateTxStatus(smartContractId, txId, TX_STATUS_START);

        send(smartContractId, message);

        while (!StringUtils.equals(getTxStatusById(smartContractId, txId), TX_STATUS_COMPLETE)
                && !StringUtils.equals(getTxStatusById(smartContractId, txId), TX_STATUS_ERROR)) {
        }

        SmartContractMessage receiveMessage = getTxMessage(smartContractId, txId);
        return receiveMessage;
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
            smartContractIdStr = SmartContractPackage.SmartContractID.parseFrom(message.getPayload()).getName();
        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        }
        return smartContractIdStr;
    }

    public static void main(String[] args) throws Exception {
        INodeLedger l = NodeUtils.getLedger("mytestgroupid2");
        ITxSimulator txSimulator = l.newTxSimulator("mytestgroupid2");
        byte[] state = txSimulator.getState("mytestgroupid2", "key1");
//	    System.out.println(new String(state));
        txSimulator.setState("mytestgroupid1", "key", "value".getBytes());
        TxSimulationResults txSimulationResults = txSimulator.getTxSimulationResults();
        Rwset.TxReadWriteSet publicReadWriteSet = txSimulationResults.getPublicReadWriteSet();
        Rwset.NsReadWriteSet nsRwset = publicReadWriteSet.getNsRwset(0);
        ByteString rwset = nsRwset.getRwset();
        System.out.println(rwset.toStringUtf8());
    }

}
