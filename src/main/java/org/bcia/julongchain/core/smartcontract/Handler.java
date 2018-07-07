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
package org.bcia.julongchain.core.smartcontract;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.julongchain.core.container.scintf.ISmartContractStream;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.node.NodeConfig;
import org.bcia.julongchain.core.smartcontract.shim.fsm.CBDesc;
import org.bcia.julongchain.core.smartcontract.shim.fsm.Event;
import org.bcia.julongchain.core.smartcontract.shim.fsm.EventDesc;
import org.bcia.julongchain.core.smartcontract.shim.fsm.FSM;
import org.bcia.julongchain.core.smartcontract.shim.fsm.exceptions.*;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.protos.node.*;

import javax.naming.Context;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.bcia.julongchain.core.smartcontract.shim.fsm.CallbackType.AFTER_EVENT;
import static org.bcia.julongchain.core.smartcontract.shim.fsm.CallbackType.BEFORE_EVENT;
import static org.bcia.julongchain.core.smartcontract.shim.fsm.CallbackType.ENTER_STATE;
import static org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage.Type.*;

/**
 * Handler responsible for management of Peer's side of chaincode stream
 *
 * @author sunzongyu
 * @date 2018/3/18
 * @company Dingxuan
 */
public class Handler {

    /**
     * start state
     */
    private static final String CREATED_STATE     = "created";
    /**
     * in: CREATED, rcv:  REGISTER, send: REGISTERED, INIT
     */
    private static final String ESTABLISHED_STATE = "established";
    /**
     * in:ESTABLISHED,TRANSACTION, rcv:COMPLETED
     */
    private static final String READY_STATE       = "ready";
    /**
     * in:INIT,ESTABLISHED, rcv: error, terminate container
     */
    private static final String END_STATE         = "end";

    private static JavaChainLog logger = JavaChainLogFactory.getLog(Handler.class);

    private ISmartContractStream chatStream;
    private FSM fsm;
    private SmartContractPackage.SmartContractID smartContractID;
    private SmartContractInstance smartContractInstance;
    private SmartContractSupport smartContractSupport;
    private Boolean registered;
    private Boolean readyNotify;
    private Map<String, TransactionContext> txCtxs;
    private Map<String, Boolean> txidMap;
    private Channel<NextStateInfo> nextState;

    public Handler(){

    }

    private Handler(SmartContractSupport chaincodeSupport, ISmartContractStream peerChatStream){
        this.chatStream  = peerChatStream;
        this.smartContractSupport = chaincodeSupport;
        this.registered = Boolean.FALSE;
        this.txCtxs = new HashMap<>();
        this.txidMap = new HashMap<>();
        this.nextState = new Channel<>();
        this.fsm = new FSM(CREATED_STATE);

        fsm.addEvents(
            //            Event Name                        From                To
            new EventDesc(REGISTERED.toString(),            CREATED_STATE,      ESTABLISHED_STATE),
            new EventDesc(READY.toString(),                 ESTABLISHED_STATE,  READY_STATE),
            new EventDesc(PUT_STATE.toString(),             READY_STATE,        READY_STATE),
            new EventDesc(DEL_STATE.toString(),             READY_STATE,        READY_STATE),
            new EventDesc(INVOKE_SMARTCONTRACT.toString(),  READY_STATE,        READY_STATE),
            new EventDesc(COMPLETED.toString(),             READY_STATE,        READY_STATE),
            new EventDesc(GET_STATE.toString(),             READY_STATE,        READY_STATE),
            new EventDesc(GET_STATE_BY_RANGE.toString(),    READY_STATE,        READY_STATE),
            new EventDesc(GET_QUERY_RESULT.toString(),      READY_STATE,        READY_STATE),
            new EventDesc(GET_HISTORY_FOR_KEY.toString(),   READY_STATE,        READY_STATE),
            new EventDesc(QUERY_STATE_NEXT.toString(),      READY_STATE,        READY_STATE),
            new EventDesc(QUERY_STATE_CLOSE.toString(),     READY_STATE,        READY_STATE),
            new EventDesc(ERROR.toString(),                 READY_STATE,        READY_STATE),
            new EventDesc(RESPONSE.toString(),              READY_STATE,        READY_STATE),
            new EventDesc(INIT.toString(),                  READY_STATE,        READY_STATE),
            new EventDesc(TRANSACTION.toString(),           READY_STATE,        READY_STATE)
        );

        fsm.addCallbacks(
            //         Type             Trigger                         ICallbakc
            new CBDesc(BEFORE_EVENT,    REGISTERED.toString(),          (event) -> beforeRegisterEvent(event, fsm.current())),
            new CBDesc(BEFORE_EVENT,    COMPLETED.toString(),           (event) -> beforeCompletedEvent(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     GET_STATE.toString(),           (event) -> afterGetState(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     GET_STATE_BY_RANGE.toString(),  (event) -> afterGetStateByRange(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     GET_QUERY_RESULT.toString(),    (event) -> afterGetQueryResult(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     GET_HISTORY_FOR_KEY.toString(), (event) -> afterGetHistoryForKey(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     QUERY_STATE_NEXT.toString(),    (event) -> afterQueryStateNext(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     QUERY_STATE_CLOSE.toString(),   (event) -> afterQueryStateClose(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     PUT_STATE.toString(),           (event) -> enterBusyState(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     DEL_STATE.toString(),           (event) -> enterBusyState(event, fsm.current())),
            new CBDesc(AFTER_EVENT,     INVOKE_SMARTCONTRACT.toString(),(event) -> enterBusyState(event, fsm.current())),
            new CBDesc(ENTER_STATE,     ESTABLISHED_STATE,              (event) -> enterEstablishedState(event, fsm.current())),
            new CBDesc(ENTER_STATE,     READY_STATE,                    (event) -> enterReadyState(event, fsm.current())),
            new CBDesc(ENTER_STATE,     END_STATE,                      (event) -> enterEndState(event, fsm.current()))
        );
    }

    public ISmartContractStream getChatStream() {
        return chatStream;
    }

    public void setChatStream(ISmartContractStream chatStream) {
        this.chatStream = chatStream;
    }

    public FSM getFsm() {
        return fsm;
    }

    public void setFsm(FSM fsm) {
        this.fsm = fsm;
    }

    public SmartContractPackage.SmartContractID getSmartContractID() {
        return smartContractID;
    }

    public void setSmartContractID(SmartContractPackage.SmartContractID smartContractID) {
        this.smartContractID = smartContractID;
    }

    public SmartContractInstance getSmartContractInstance() {
        return smartContractInstance;
    }

    public void setSmartContractInstance(SmartContractInstance smartContractInstance) {
        this.smartContractInstance = smartContractInstance;
    }

    public SmartContractSupport getSmartContractSupport() {
        return smartContractSupport;
    }

    public void setSmartContractSupport(SmartContractSupport smartContractSupport) {
        this.smartContractSupport = smartContractSupport;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public Boolean getReadyNotify() {
        return readyNotify;
    }

    public void setReadyNotify(Boolean readyNotify) {
        this.readyNotify = readyNotify;
    }

    public Map<String, TransactionContext> getTxCtxs() {
        return txCtxs;
    }

    public void setTxCtxs(Map<String, TransactionContext> txCtxs) {
        this.txCtxs = txCtxs;
    }

    public Map<String, Boolean> getTxidMap() {
        return txidMap;
    }

    public void setTxidMap(Map<String, Boolean> txidMap) {
        this.txidMap = txidMap;
    }

    public Channel<NextStateInfo> getNextState() {
        return nextState;
    }

    public void setNextState(Channel<NextStateInfo> nextState) {
        this.nextState = nextState;
    }

    /**
     * return new handler instance
     */
    public static Handler newSmartContractSupportHandler(ISmartContractStream peerChatStream, SmartContractSupport smartContractSupport){
        return new Handler(smartContractSupport, peerChatStream);
    }


    /**
     * return firest 8 chars in txid
     */
    public static String shorttxid(String txid) {
        if(txid == null){
            return null;
        }
        if(txid.length() < 8) {
            return txid;
        }
        return txid.substring(0, 8);
    }

    /**
     * set smart contract instance which's id is smartContractID
     */
    public void decomposeRegisteredName(SmartContractPackage.SmartContractID smartContractID) {
        this.setSmartContractInstance(getSmartContractInstance(smartContractID.getName()));
    }

    /**
     * get smart contract with smartContractName
     * @param smartContractName name:version/id
     */
    public static SmartContractInstance getSmartContractInstance(String smartContractName) {
        SmartContractInstance instance = new SmartContractInstance();

        //计算后缀（ie, chain name)
        int i = smartContractName.indexOf("/");
        if(i >= 0){
            if(i < smartContractName.length() - 1){
                instance.setGroupId(smartContractName.substring(i + 1, smartContractName.length()));
            }
            smartContractName = smartContractName.substring(0, i);
        }

        //计算版本 version
        i = smartContractName.indexOf(":");
        if(i >= 0 ){
            if(i < smartContractName.length() - 1){
                instance.setSmartContractVersion(smartContractName.substring(i + 1, smartContractName.length()));
            }
            smartContractName = smartContractName.substring(0, i);
        }

        //剩余的是chaincode name
        instance.setSmartContractName(smartContractName);

        return instance;
    }

    /**
     * get smart contract's name
     */
    public String getSmartContractRootName() {
        return smartContractInstance.getSmartContractName();
    }

    /**
     * send smart contract message with gRPC channel "chatStream" sychronized
     */
    public synchronized void serialSend(SmartContractShim.SmartContractMessage msg) {
        try {
            chatStream.send(msg);
            logger.info(String.format("[%s]Serialsend %s", msg.getTxid() ,msg.getPayload().toStringUtf8()));
        } catch (Exception e) {
            logger.error(String.format("[%s]Got error when serial send %s", msg.getTxid(), msg.getPayload()));
        }
    }

    /**
     * send smart contract message with gRPC channel "chatStream" asychronized
     */
    public void serialSendAsync(SmartContractShim.SmartContractMessage msg) {
        new Thread(() -> {
            serialSend(msg);
        }).start();
    }

    /**
     * get transaction context id which is chainid + txid
     */
    public String getTxCtxId(String groupId, String txId) {
        if(groupId != null && txId != null){
            return groupId + txId;
        } else {
            return null;
        }
    }

    /**
     * create transaction context with following args
     */
    public TransactionContext createTxContext(Context ctxt, String chainID, String txid, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
        if(txCtxs == null) {
            logger.error("Cannot create transaction because Map of transaction context is null");
           return null;
        }

        String txCtxID = getTxCtxId(chainID, txid);
        //if transaction context which's id is txCtxID is exist
        if(txCtxs.get(txCtxID) != null){
            logger.error("Cannot create transaction because current transaction context is already exist");
            return null;
        }

        TransactionContext txctx = new TransactionContext();
        txctx.setChainID(chainID);
        txctx.setSignedProp(signedProp);
        txctx.setProposal(prop);
        txCtxs.put(txCtxID, txctx);
        if (smartContractSupport != null) {
            txctx.setTxSimulator(smartContractSupport.getTxSimulator(ctxt));
            txctx.setHistoryQueryExecutor(smartContractSupport.getHistoryQueryExecutor(ctxt));
        }
        logger.info("Create transaction success");
        return txctx;
    }

    /**
     * get transcation context with transcation id "chainID + txid"
     */
    public synchronized TransactionContext getTxContext(String groupId, String txId) {
        String txCtxID = getTxCtxId(groupId, txId);
        return txCtxs.get(txCtxID);
    }

    /**
     * delete transcation context with transcation id "chainID + txid" if exist
     */
    public synchronized void deleteTxContext(String chainID, String txid) {
        String txCtxID = getTxCtxId(chainID, txid);
        if(txCtxID != null) {
            logger.info(String.format("Remove transaction id %s", txCtxID));
            txCtxs.remove(txCtxID);
        } else {
            logger.error("Cannot remove transaction because of null id");
        }
    }

    /**
     * put current txContext into txContext's queryIteratorMap for init it.
     */
    public synchronized void initializeQueryContext(TransactionContext txContext, String queryID,
                                       IResultsIterator queryIterator) {
        if(txContext.getQueryIteratorMap() != null && queryID != null){
            logger.info(String.format("Put queryID: %s", queryID));
            txContext.getQueryIteratorMap().put(queryID, queryIterator);
        } else if(txContext.getQueryIteratorMap() == null) {
            logger.error(String.format("Cannot initialize queryID: %s, because QueryIteratorMap is null", queryID));
        } else {
            logger.error("Cannot initialize because queryID is null");
        }
    }

    /**
     * get a transcation context which's query id is "queryID" in cxContext's queryIteratorMap
     */
    public synchronized IResultsIterator getQueryIterator(TransactionContext txContext, String queryID) {
        if(txContext.getQueryIteratorMap() != null && queryID != null){
            return txContext.getQueryIteratorMap().get(queryID);
        } else {
            return null;
        }
    }

    /**
     * close and remove current ResultIterator which's query id is "queryID"
     */
    public synchronized void cleanupQueryContext(TransactionContext txContext, String queryID){
        try {
            txContext.getQueryIteratorMap().get(queryID).close();
            txContext.getQueryIteratorMap().remove(queryID);
            txContext.getPendingQueryResults().remove(queryID);
        } catch (NullPointerException | LedgerException e) {
            logger.error("Got error when clean up query context");
        }
    }

    /**
     * Check if the transactor is allow to call this chaincode on this channel
     */
    public boolean checkACL(ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal proposal, SmartContractInstance ccIns) {

        return true;
    }

    /**
     * deregister this handler if it is registed
     */
    public void deregister() {
        if(this.registered){

        }
    }

    public void triggerNextState(SmartContractShim.SmartContractMessage msg, Boolean send) {
        NextStateInfo nsInfo = new NextStateInfo();
        nsInfo.setMsg(msg);
        nsInfo.setSendToCC(send);
        nsInfo.setSendSync(Boolean.FALSE);
        nextState.add(nsInfo);
    }

    public void triggerNextStateSync(SmartContractShim.SmartContractMessage msg) {
        NextStateInfo nsInfo = new NextStateInfo();
        nsInfo.setMsg(msg);
        nsInfo.setSendToCC(Boolean.TRUE);
        nsInfo.setSendSync(Boolean.TRUE);
        nextState.add(nsInfo);
    }

    /**
     * return this channel's left alive time.
     * if it is 0, wait others to signal this channel
     */
    public void waitForKeepaliveTimer() {
        //need ssSupport.keepalive
    }

    /**
     * 4个频道 1.ChaincodeMessage 频道
     *      2.NextState 频道
     *      3.KeepaliveTime 频道
     *      4.err 频道
     */
    public void processStream() {
        //ChaincodeMessage 频道
        new Thread(() -> {
            //阻塞接受消息配合循环，持续接受消息
            while(true){
                try{
                    //接受容器侧传递的SmartContractMessage
                    SmartContractShim.SmartContractMessage in = null;
                    if (chatStream != null) {
                        //阻塞方法
                        in = chatStream.recv();
                    }
                    if(in == null){
                        logger.error("Received null message, ending chaincode support stream");
                        return;
                    }
                    logger.info(String.format("[%s]Received message %s from shim", shorttxid(in.getTxid()), in.getType()));
                    //传递信息为KEEPLIVE
                    if(in.getType().equals(KEEPALIVE)){
                        logger.info("Received KEEPALIVE SmartContractResponse");
                        continue;
                    }
                    //传递信息为ERROR
                    if(in.getType().equals(ERROR)){
                        logger.error(String.format("Got error: %s", in.getPayload().toStringUtf8()));
                    }
                    //处理消息
                    handleMessage(in);
                } catch(RuntimeException e){
                    logger.error("Error handling message, ending stream");
                } finally {
                    deregister();
                }
            }
        }).start();

        //NextState 频道
        new Thread(() -> {
            //阻塞接受消息配合循环，持续接受消息
            while(true) {
                try {
                    //获取下一个状态信息,阻塞进程
                    NextStateInfo nsInfo = nextState.take();
                    if(nsInfo == null){
                        logger.error("next state null message, ending chaincode support stream");
                        return;
                    }

                    SmartContractShim.SmartContractMessage in = nsInfo.getMsg();
                    if (in == null) {
                        logger.error("next state null message, ending chaincode support stream");
                        return;
                    }

                    logger.info(String.format("[%s]Move state message %s", shorttxid(in.getTxid()), in.getType()));
                    handleMessage(in);
                    if (nsInfo.getSendToCC()) {
                        logger.info(String.format("[%s]sending state message %s", shorttxid(in.getTxid()), in.getType()));
                        if(nsInfo.getSendSync()){
                            if(!in.getType().equals(READY)){
                                logger.error(String.format("[%s]Sync send can only be for READY state %s", shorttxid(in.getTxid()), in.getType()));
                                deregister();
                                System.exit(-1);
                            }
                            serialSend(in);
                        }
                        else {
                            serialSendAsync(in);
                        }
                    }
                } catch (InterruptedException e) {
                    logger.error("next state error message, ending chaincode support stream");
                    return;
                } catch(RuntimeException e) {
                    logger.error("Error handling message, ending stream");
                } finally {
                    deregister();
                }
            }
        }).start();

        //Keepalive 频道
//        new Thread(() -> {
//
//        }).start();
    }

    /** HandleChaincodeStream Main loop for handling the associated Chaincode stream
     */
    public static void handleChaincodeStream(SmartContractSupport chaincodeSupport, Context ctxt, ISmartContractStream stream) {
        //check deadline
        logger.info("Handle current context");
        Handler handler = new Handler(chaincodeSupport, stream);
        handler.processStream();
    }

    /**
     * put txCtxID into txidMap
     */
    public synchronized Boolean createTXIDEntry(String GroupId, String txid) {
        if(txidMap == null){
            return Boolean.FALSE;
        }
        String txCtxID = getTxCtxId(GroupId, txid);
        if(txCtxID == null){
            logger.info(String.format("[%s]Transcation context id is null", shorttxid(txid)));
            return Boolean.FALSE;
        }
        txidMap.putIfAbsent(txCtxID, Boolean.TRUE);
        return txidMap.get(txCtxID);
    }

    /**
     * remove teCtxID from txidMap
     */
    public synchronized void deleteTXIDEntry(String groupId, String txid) {
        String txCtxID = getTxCtxId(groupId, txid);
        if(txidMap != null){
            txidMap.remove(txCtxID);
        } else {
            logger.error(String.format("TXID %s is not found", txCtxID));
        }
    }

    /**
     * notiry READY message as next state
     */
    public void notifyDuringStartup(Boolean val) {
        if(readyNotify != null){
            logger.info("Notifying during startup");
            readyNotify = val;
        } else {
            logger.info("Nothing to notify (dev mode ?)");
//            if(smartContractSupport.userRunCC){
            if(true){
                if(val){
                    logger.info("sending READY");
                    SmartContractShim.SmartContractMessage ccMsg = SmartContractShim.SmartContractMessage.newBuilder()
                            .setType(READY)
                            .build();
                    new Thread(() -> {
                        triggerNextState(ccMsg, Boolean.TRUE);
                    }).start();
                } else {
                    logger.error("Error during startup .. not sending READY");
                }
            } else {
                logger.info("trying to manually run chaincode when not in devmode ?");
            }
        }
    }

    /** beforeRegisterEvent is invoked when chaincode tries to register.
     */
     public void beforeRegisterEvent(Event event, String state) {
         //在event中提取msg
         SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
         try {
             logger.info(String.format("Received event in state %s", state));
             //在msg payload中提取id
             SmartContractPackage.SmartContractID id = null;
             id = SmartContractPackage.SmartContractID.parseFrom(msg.getPayload());
             smartContractID = id;
             //注册handler
//             smartContractSupport.registerHandler(this);
             //实例化链码
             decomposeRegisteredName(smartContractID);
             logger.info(String.format("[%s]Got %s for chaincodeID = %s, sending back %s", msg.getTxid(), event, id, REGISTERED.toString()));
             //发送REGISTERED消息
             serialSend(SmartContractShim.SmartContractMessage.newBuilder()
                     .setType(REGISTERED)
                     .build());
         } catch (Exception e) {
             event.cancel(e);
             logger.error(String.format("[%s]Got error when regist handler", msg.getTxid()));
         }
    }

    /**
     * notify msg
     */
    public synchronized void notify(SmartContractShim.SmartContractMessage msg) {
        String txCtxId = getTxCtxId(msg.getGroupId(), msg.getTxid());
        //获取交易
        TransactionContext tctx = txCtxs.get(txCtxId);
        if (tctx == null) {
            logger.info(String.format("Notifier Tid: %s, GroupId: %s does not exist", msg.getTxid(), msg.getGroupId()));
        } else {
            logger.info(String.format("Notifing Tid: %s, GroupId: %s", msg.getTxid(), msg.getGroupId()));
            tctx.setResponseNotifier(msg);

            if (tctx.getQueryIteratorMap() != null && tctx.getQueryIteratorMap().size() > 0) {
                tctx.getQueryIteratorMap().forEach((k, v) -> {
                    try {
                        v.close();
                    } catch (Exception e){
                        logger.error("Got error when close iterator");
                    }
                });
            }
        }
    }

    /** beforeCompletedEvent is invoked when chaincode has completed execution of init, invoke.
     */
    public void beforeCompletedEvent(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("[%s]beforeCompleted - not in ready state will notify when in readystate", shorttxid(msg.getTxid())));
    }

    /**
     * after fsm receive READY, send INIT to user chaincode
     */
    public void afterReady(Event event, String state){
        //发送INIT给用户链码
        logger.info("Send INIT to user chaincode");
        serialSend(SmartContractShim.SmartContractMessage.newBuilder()
                .setType(INIT)
                .build());
    }

    /** afterGetState handles a GET_STATE request from the chaincode.
     */
    public void afterGetState(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("[%s]Received %s, invoking get state from ledger", shorttxid(msg.getTxid()), GET_STATE.toString()));
        handleGetState(msg);
    }

    /** Handles query to ledger to get state
     */
    public void handleGetState(SmartContractShim.SmartContractMessage msg) {
        new Thread(() -> {
            TransactionContext txContext = null;
            SmartContractShim.GetState getState = null;
            String smartContractId = null;
            ByteString res = null;
            //创建交易实体
            boolean uniqueReq = createTXIDEntry(msg.getGroupId(), msg.getTxid());
            if (!uniqueReq) {
                String errStr = String.format("[%s]HandleGetState. Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid()));
                logger.error(errStr);
                return;
            }
            //获取交易
            txContext = getTxContext(msg.getGroupId(), msg.getTxid());
            //未获取到交易或交易无法模拟执行
            if (txContext == null || txContext.getTxSimulator() == null) {
                String errStr = String.format("[%s]HandleGetState. No ledger context for GetState. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //在msg的payload中提取GetState
            try {
                getState = SmartContractShim.GetState.parseFrom(msg.getPayload());
            } catch (Exception e){
                String errStr = String.format("[%s]HandleGetState. Failed to create GetState. Sending %s", ERROR.toString(), shorttxid(msg.getTxid()));
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取链码id
            try {
                smartContractId = getSmartContractRootName();
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetState. Failed to create chaincodeID. Sending %s", ERROR.toString(), shorttxid(msg.getTxid()));
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            logger.info(String.format("[%s] getting state for chaincode %s, key %s, channel %s",
                    shorttxid(msg.getTxid()), smartContractId, getState.getKey(), txContext.getChainID()));
            //在账本中获取状态
            try {
                if(isCollectionSet(getState.getCollection())){
//                    res = txContext.getTxSimulator().getState(smartContractId, getState.getKey());
//                    } else {
//                        res, err = txContext.txsimulator.GetState(chaincodeID, getState.Key)
//                    }

                }
                if (res != null) {
                    //The state object being requested does not exist
                    String infoStr = String.format("[%s]HandleGetState. No state associated with key: %s. Sending %s with an empty payload"
                            , shorttxid(msg.getTxid()), msg.getPayload().toStringUtf8(), RESPONSE.toString());
                    logger.info(infoStr);
                    successReturn(msg, res, RESPONSE);
                } else {
                    //success, send response msg back to chaincode. GetState will not trigger event
                    String infoStr = String.format("[%s]HandleGetState. Got state. Sending %s"
                            , shorttxid(msg.getTxid()), RESPONSE.toString());
                    logger.info(infoStr);
                    successReturn(msg, res, RESPONSE);
                }
            } catch (Exception e) {
                //Get error when create ByteString res, send error msg back to chaincode. GetState will not trigger event
                String errStr = String.format("[%s]HandleGetState. Failed to get chaincode state(%s). Sending %s"
                        , shorttxid(msg.getTxid()), printStackTrace(e), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
            }
        }).start();
    }

    /** afterGetStateByRange handles a GET_STATE_BY_RANGE request from the chaincode.
     */
    public void afterGetStateByRange(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("Received %s, invoking get state from ledger"
                , GET_STATE_BY_RANGE.toString()));
        //query ledger for state
        handleGetStateByRange(msg);
        logger.info("Exiting GET_STATE_BY_RANGE");
    }

    /** Handles query to ledger to rage query state
     */
    public void handleGetStateByRange(SmartContractShim.SmartContractMessage msg) {
        new Thread(() -> {
            SmartContractShim.GetStateByRange getStateByRange = null;
            IResultsIterator rangeIter = null;
            String smartContractID = null;
            String iterID = null;
            TransactionContext txContext = null;
            SmartContractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            //创建交易实体
            boolean uniqueReq = createTXIDEntry(msg.getGroupId(), msg.getTxid());
            if (!uniqueReq) {
                logger.error(String.format("[%s]Anoter state request pending for this Txid. Cannot process."
                        , shorttxid(msg.getTxid())));
                return;
            }
            //在msg的payload中获取GetStateByRange
            try {
                getStateByRange = SmartContractShim.GetStateByRange.parseFrom(msg.getPayload());
            } catch (InvalidProtocolBufferException e){
                String errStr = String.format("[%s]HandleGetStateByRange. Fail to create get state by range. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString()); logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取迭代器id
            //iteId = util.GenerateUUID();
            iterID = "iterID";
            //获取交易
            txContext = getTxContext(msg.getGroupId(), msg.getTxid());
            //无法获取交易或交易无法模拟执行
            if (txContext == null || txContext.getTxSimulator() == null){
                String errStr = String.format("[%s]HandleGetStateByRange. No ledger context for GetStateByRange. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取关联的链码ID
            try {
                smartContractID = getSmartContractRootName();
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetStateByRange. Failed to create chaincodeID. Sending %s" , shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取迭代器
            try {
                if(isCollectionSet(getStateByRange.getCollection())){
    //                rangeIter, err = txContext.txsimulator.GetPrivateDataRangeScanIterator(chaincodeID, getStateByRange.Collection, getStateByRange.StartKey, getStateByRange.EndKey)
                } else {
    //                rangeIter, err = txContext.txsimulator.GetStateRangeScanIterator(chaincodeID, getStateByRange.StartKey, getStateByRange.EndKey)
                }
                //TODO： 测试数据
                rangeIter = new IResultsIterator() {
                    @Override
                    public QueryResult next() throws LedgerException {
                        return null;
                    }

                    @Override
                    public void close() throws LedgerException {

                    }
                };
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetStateByRange. Got error when get ledger scan iterator. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }

            initializeQueryContext(txContext, iterID, rangeIter);
            //迭代获取State
            try {
                payload = getQueryResponse(txContext, rangeIter, iterID);
            } catch (Exception e) {
                if(rangeIter != null){
                    cleanupQueryContext(txContext, iterID);
                }
                String errStr = String.format("[%s]HandleGetStateByRange. Failed to get query result in HandlerGetStateByRange. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //将获取到的State转换为payload
            try {
                payloadBytes = payload.toByteString();
            } catch (Exception e) {
                if(rangeIter != null){
                    cleanupQueryContext(txContext, iterID);
                }
                String errStr = String.format("[%s]HandleGetStateByRange. Failed to get response in HandlerGetStateByRange. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //转换成功并发送RESPONSE消息
            logger.info(String.format("[%s]Got keys and values. Sending %s", shorttxid(msg.getTxid()), RESPONSE.toString()));
            successReturn(msg, payloadBytes, RESPONSE);
        }).start();
    }



    public static Integer maxResultLimit = 100;

    /** getQueryResponse takes an iterator and fetch state to construct QueryResponse
     */
    public SmartContractShim.QueryResponse getQueryResponse(TransactionContext txContext, IResultsIterator iter,
                                                                   String iterID){
        try {
            PendingQueryResult pendingQueryResults = txContext.getPendingQueryResults().get(iterID);
            while(true){
                QueryResult queryResult = iter.next();
                if(queryResult == null){
                    //完成迭代
                    SmartContractShim.QueryResultBytes[] batch = cut(pendingQueryResults);
                    cleanupQueryContext(txContext, iterID);
                    return setQueryResponseReuslt(SmartContractShim.QueryResponse.newBuilder()
                            .setId(iterID)
                            .setHasMore(false), batch).build();
                } else if(maxResultLimit.equals(pendingQueryResults.getCount())){
                    //超过最大数量
                    SmartContractShim.QueryResultBytes[] batch = cut(pendingQueryResults);
                    try {
                        add(pendingQueryResults, queryResult);
                    } catch (java.lang.Exception e) {
                        cleanupQueryContext(txContext, iterID);
                        return null;
                    }
                    return setQueryResponseReuslt(SmartContractShim.QueryResponse.newBuilder()
                            .setId(iterID)
                            .setHasMore(true), batch).build();
                }
            }
        } catch (LedgerException e){
            logger.error("Failed to get query result from iterator");
            cleanupQueryContext(txContext, iterID);
            return null;
        }
    }

    /**
     * get p.batch and set batch as null, count as 0
     */
    public SmartContractShim.QueryResultBytes[] cut(PendingQueryResult p) {
        SmartContractShim.QueryResultBytes[] batch = new SmartContractShim.QueryResultBytes[0];
        if (p != null) {
            batch = p.getBatch();
            p.setBatch(null);
            p.setCount(0);
            return batch;
        } else {
            throw new RuntimeException("Got error when cut PendingQueryResult");
        }
    }

    public void add(PendingQueryResult pendingQueryResult, QueryResult queryResult) {
        try{
            ByteString queryResultsBytes = ((Message)queryResult).toByteString();
            SmartContractShim.QueryResultBytes[] arr = pendingQueryResult.getBatch();
            arr = Arrays.copyOf(arr, arr.length + 1);
            arr[arr.length - 1] = SmartContractShim.QueryResultBytes.newBuilder()
                    .setResultBytes(queryResultsBytes)
                    .build();
            pendingQueryResult.setBatch(arr);
            pendingQueryResult.setCount(arr.length);
        } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
            final RuntimeException error = new RuntimeException("No chaincode message found in event", e);
            logger.error("Failed to get encode query result as bytes");
            throw error;
        }
    }

    /** afterQueryStateNext handles a QUERY_STATE_NEXT request from the chaincode.
     */
    public void afterQueryStateNext(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("Received %s, invoking query state next from ledger", QUERY_STATE_NEXT.toString()));
        handleQueryStateNext(msg);
        logger.info("Exiiting QUERY_STATE_NEXT");
    }

    /** Handles query to ledger for query state next
     */
    public void handleQueryStateNext(SmartContractShim.SmartContractMessage msg) {
        new Thread(() -> {
            SmartContractShim.QueryStateNext queryStateNext = null;
            TransactionContext txContext = null;
            IResultsIterator queryIter = null;
            SmartContractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            //创建交易实体
            boolean uniqueReq = createTXIDEntry(msg.getGroupId(), msg.getTxid());
            if (!uniqueReq) {
                String errStr = String.format("[%s]HandleQueryStateNext. Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid()));
                logger.error(errStr);
                return;
            }
            //在msg的payload中读取QueryStateNext
            try{
                queryStateNext = SmartContractShim.QueryStateNext.parseFrom(msg.getPayload());
            } catch (InvalidProtocolBufferException e){
                String errStr = String.format("[%s]HandleQueryStateNext. Failed to create query state next request. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //构建交易
            txContext = getTxContext(msg.getGroupId(), msg.getTxid());
            if(txContext == null){
                String errStr = String.format("[%s]HandleQueryStateNext. Failed to get transaction context. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取查询迭代器
            queryIter = getQueryIterator(txContext, queryStateNext.getId());
            if(queryIter == null){
                String errStr = String.format("[%s]HandleQueryStateNext. Query iterator no found. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //执行迭代查询
            try {
                payload = getQueryResponse(txContext, queryIter, queryStateNext.getId());
            } catch (Exception e) {
                cleanupQueryContext(txContext, queryStateNext.getId());
                String errStr = String.format("[%s]HandleQueryStateNext. Fail to get query result in HandlerQueryStateNext. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //将查询结果封装到msg的payload中
            try {
                payloadBytes = payload.toByteString();
            } catch (Exception e) {
                cleanupQueryContext(txContext, queryStateNext.getId());
                String errStr = String.format("[%s]HandleQueryStateNext. Fail to get response HandlerQueryStateNext. Sending %s",shorttxid(msg.getTxid()), ERROR.toString());
                logger.error(errStr);
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //成功并返回RESPONSE消息
            logger.info(String.format("Got key and values. Sending %s", RESPONSE));
            successReturn(msg, payloadBytes, RESPONSE);
        }).start();
    }

    /** afterQueryStateClose handles a QUERY_STATE_CLOSE request from the chaincode.
     */
    public void afterQueryStateClose(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("Received %s, invoking query state close from ledger"
                , QUERY_STATE_CLOSE.toString()));

        handleQueryStateClose(msg);
        logger.info("Exiting QUERY_STATE_CLOSE");
    }

    /** Handles the closing of a state iterator
     */
    public void handleQueryStateClose(SmartContractShim.SmartContractMessage msg) {
        new Thread(() -> {
            SmartContractShim.QueryStateClose queryStateClose = null;
            TransactionContext txContext = null;
            IResultsIterator iter = null;
            SmartContractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            //构建交易实体
            boolean uniqueReq = createTXIDEntry(msg.getGroupId(), msg.getTxid());
            if (!uniqueReq) {
                logger.error(String.format("[%s]HandleQueryStateClose. Anoter state request pending for this Txid. Cannot process."
                        , shorttxid(msg.getTxid())));
                return;
            }
            //在msg的payload中获取QueryStateClose
            try {
                queryStateClose = SmartContractShim.QueryStateClose.parseFrom(msg.getPayload());
            } catch (InvalidProtocolBufferException e) {
                String errStr = String.format("[%s]HandleQueryStateClose. Failed to get state query close request. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取交易
            txContext = getTxContext(msg.getGroupId(), msg.getTxid());
            if(txContext == null){
                String errStr = String.format("[%s]HandleQueryStateClose. Failed to get transaction context. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取查询迭代器
            iter = getQueryIterator(txContext, queryStateClose.getId());
            //关闭查询迭代器
            if(iter != null){
                cleanupQueryContext(txContext, queryStateClose.getId());
            }
            //构造QueryResponse
            payload = SmartContractShim.QueryResponse.newBuilder()
                    .setHasMore(false)
                    .setId(queryStateClose.getId())
                    .build();
            //封装为msg
            try {
               payloadBytes = payload.toByteString();
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleQueryStateClose. Failed to get payload. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //成功并发送RESPONSE
            logger.info(String.format("[%s]Closed. Sending %s", shorttxid(msg.getTxid()), RESPONSE.toString()));
            successReturn(msg, payloadBytes, RESPONSE);
        }).start();
    }

    /** afterGetQueryResult handles a GET_QUERY_RESULT request from the chaincode.
     */
    public void afterGetQueryResult(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String .format("Received %s, invoking get state from ledger"
                , GET_QUERY_RESULT.toString()));

        handleGetQueryResult(msg);
        logger.info("Exiting GET_QUERY_RESULT");
    }

    /** Handles query to ledger to execute query state
     */
    public void handleGetQueryResult(SmartContractShim.SmartContractMessage msg) {
        new Thread(() -> {
            TransactionContext txContext = null;
            String iterID = null;
            SmartContractShim.GetQueryResult getQueryResult = null;
            String smartContractID = null;
            IResultsIterator executeIter = null;
            SmartContractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            //获取交易实体
            boolean uniqueReq = createTXIDEntry(msg.getGroupId(), msg.getTxid());
            if (!uniqueReq) {
                logger.error(String.format("[%s]HandleGetQueryResult. Anoter state request pending for this Txid. Cannot process."
                        , shorttxid(msg.getTxid())));
                return;
            }
            //在msg的payload中提取GetQueryResult
            try {
                getQueryResult = SmartContractShim.GetQueryResult.parseFrom(msg.getPayload());
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetQueryResult. Failed to unmarshall query request. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取迭代器id
            //iterID = util.GenerateUUID();
            iterID = "iterID";
            //获取交易
            txContext = getTxContext(msg.getGroupId(), msg.getTxid());
            if(txContext == null || txContext.getTxSimulator() == null){
                String errStr = String.format("[%s]HandleGetQueryResult. No ledger context for GetQueryResult. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取链码实例id
            smartContractID = getSmartContractRootName();
            //获取查询结果迭代器（ResultIterator）
            try {
                if(isCollectionSet(getQueryResult.getCollection())){
//                    executeIter, err = txContext.txsimulator.ExecuteQueryOnPrivateData(chaincodeID, getQueryResult.Collection, getQueryResult.Query)
                } else {
//                    executeIter, err = txContext.txsimulator.ExecuteQuery(chaincodeID, getQueryResult.Query)
                }
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetQueryResult. Failed to get ledger query iterator. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //TODO: for test
            executeIter = new IResultsIterator() {
                @Override
                public QueryResult next() throws LedgerException {
                    return null;
                }

                @Override
                public void close() throws LedgerException {

                }
            };
            initializeQueryContext(txContext, iterID, executeIter);
            //执行查询
            try {
                payload = getQueryResponse(txContext, executeIter, iterID);
            } catch (Exception e) {
                if(executeIter != null){
                    cleanupQueryContext(txContext, iterID);
                }
                String errStr = String.format("[%s]HandleGetQueryResult. Failed to get query result in HandlerGetQueryResult. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //封装为msg
            try {
                payloadBytes = payload.toByteString();
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetQueryResult. Failed to get payload. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //成功并发送RESPONSE
            logger.info(String.format("[%s]HandleGetQueryResult. Got keys and values. Send %s"
                    , shorttxid(msg.getTxid()), RESPONSE.toString()));
            successReturn(msg, payloadBytes, RESPONSE);
        }).start();
    }

    /** afterGetHistoryForKey handles a GET_HISTORY_FOR_KEY request from the chaincode.
     */
    public void afterGetHistoryForKey(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String .format("Received %s, invoking get state from ledger", GET_HISTORY_FOR_KEY.toString()));

        handleGetHistoryForKey(msg);
        logger.info("Exiting GET_HISTORY_FOR_KEY");
    }

    /** Handles query to ledger history db
     */
    public void handleGetHistoryForKey(SmartContractShim.SmartContractMessage msg) {
        new Thread(() -> {
            TransactionContext txContext = null;
            SmartContractShim.GetHistoryForKey getHistoryForKey = null;
            String iterID = null;
            String smartContractID = null;
            IResultsIterator historyIterator = null;
            SmartContractShim.QueryResponse payload = null;
            ByteString payloadByte = null;
            //获取交易实体
            boolean uniqueReq = createTXIDEntry(msg.getGroupId(), msg.getTxid());
            if (!uniqueReq) {
                logger.error(String.format("[%s]HandleGetHistoryForKey. Anoter state request pending for this Txid. Cannot process."
                        , shorttxid(msg.getTxid())));
                return;
            }
            //在payload中提取GetHistoryForKey
            try {
                getHistoryForKey = SmartContractShim.GetHistoryForKey.parseFrom(msg.getPayload());
            } catch (InvalidProtocolBufferException e) {
                String errStr = String.format("[%s]Failed to create query result. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取迭代器id
            //iterID = util.GenerateUUID();
            iterID = "iterID";
            //获取交易
            txContext = getTxContext(msg.getGroupId(), msg.getTxid());
            if(txContext == null || txContext.getTxSimulator() == null){
                String errStr = String.format("[%s]HandleGetHistoryForKey. No ledger context for GetHistoryForKey. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //获取链码id
            smartContractID = getSmartContractRootName();
            //获取查询迭代器
            try {
//                historyIterator = txContext.getHistoryQueryExecutor().getHistoryForKey(smartContractID, getHistoryForKey.getKey());
                //TODO: for test
                historyIterator = new IResultsIterator() {
                    @Override
                    public QueryResult next() throws LedgerException {
                        return null;
                    }

                    @Override
                    public void close() throws LedgerException {

                    }
                };
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetHistoryForKey. Failed to get ledger history iterator. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            initializeQueryContext(txContext, iterID, historyIterator);
            //获取查询结果
            try {
                payload = getQueryResponse(txContext, historyIterator, iterID);
            } catch (Exception e) {
                if(historyIterator != null){
                    cleanupQueryContext(txContext, iterID);
                }
                String errStr = String.format("[%s]HandleGetHistoryForKey. Failed to get query result in HandleGetHistoryForKey. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            //封装为msg
            try {
                payloadByte = payload.toByteString();
            } catch (Exception e) {
                String errStr = String.format("[%s]HandleGetHistoryForKey. Failed to get payload. Sending %s"
                        , shorttxid(msg.getTxid()), ERROR.toString());
                errorReturn(msg, ByteString.copyFromUtf8(errStr));
                return;
            }
            logger.info(String.format("[%s]HandleGetHistoryForKey. Got keys and values. Sending %s"
                    , shorttxid(msg.getTxid()), RESPONSE.toString()));
            successReturn(msg, payloadByte, RESPONSE);
        }).start();
    }

    public static Boolean isCollectionSet(String collection) {
        return StringUtils.isEmpty(collection);
    }

    public SmartContractShim.SmartContractMessage getTxContractForMessage(String groupId, String txid
            , String msgType, ByteString payload, String errStr) {
        TransactionContext txContext = getTxContext(groupId, txid);
        //if we do not have GroupId or INVOKE_CHAINCODE
        if(!"".equals(groupId) || !INVOKE_SMARTCONTRACT.toString().equals(msgType)){
            if (txContext == null || txContext.getTxSimulator() == null){
                logger.error(errStr);
                return newEventMessage(ERROR, groupId, txid, ByteString.copyFromUtf8(errStr));
            }
            return null;
        }

        //any other msgType except INVOKE_CHAINCODE has handled
        //now handle the situation we do have GroupId
        SmartContractInstance calledCcIns = null;
        SmartContractPackage.SmartContractSpec chainCodeSpec = null;

        try {
            chainCodeSpec = SmartContractPackage.SmartContractSpec.parseFrom(payload);
        } catch (InvalidProtocolBufferException e) {
            errStr = String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(txid), ERROR.toString());
            logger.error(errStr);
            return newEventMessage(ERROR, null, txid, ByteString.copyFromUtf8(errStr));
        }

        //Got the chaincodeID to invoke. The chaincodeID to be called may
        //contain composite info like "chaincode-name:version/channel-name"
        //now version is not used
        calledCcIns = getSmartContractInstance(chainCodeSpec.getSmartContractId().getName());
        if(calledCcIns == null){
            errStr = String.format("[%s]Could not get chaincode name for INVOKE_CHAINCODE. Sending %s", shorttxid(txid), ERROR.toString());
            logger.error(errStr);
            return newEventMessage(ERROR, null, txid, ByteString.copyFromUtf8(errStr));
        }

        boolean isScc = false;
//      boolean isScc = smartContractSupport.getSystemChaincodeProvider().isSysCC(calledCcIns.ChaincodeName);
        if(!isScc) {
            txContext = getTxContext("", txid);
            if (txContext == null || txContext.getTxSimulator() == null){
                logger.error(String.format(errStr));
                return newEventMessage(ERROR, "", txid, ByteString.copyFromUtf8(errStr));
            }
            return null;
        }

        // Calling SCC without a  ChainID, then the assumption this is an external SCC called by the client (special case) and no UCC involved,
        // so no Transaction Simulator validation needed as there are no commits to the ledger, get the txContext directly if it is not nil
        txContext = getTxContext(groupId, txid);
        if(txContext == null){
            return newEventMessage(ERROR, txid, groupId, ByteString.copyFromUtf8(errStr));
        }
        return null;
    }

    /** Handles request to ledger to put state
     */
    public void enterBusyState(Event event, String state) {
        new Thread(() -> {
            SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
            logger.info(String.format("[%s]state i %s", shorttxid(msg.getTxid()), state));

            SmartContractShim.SmartContractMessage triggerNextStateMsg = null;
            TransactionContext txContext = null;
            String chaincodeID = null;
            ByteString res = null;

            //judge if put txId into query map is success
            boolean uniqueReq = createTXIDEntry(msg.getGroupId(), msg.getTxid());
            if (!uniqueReq) {
                logger.error(String.format("[%s]Anoter state request pending for this CC: %s, Txid: %s. Cannot process."
                        , shorttxid(msg.getTxid()), smartContractID.getName(), msg.getTxid()));
                return;
            }
            //check to get triggerNextStateMsg
            triggerNextStateMsg = getTxContractForMessage(msg.getGroupId(), msg.getTxid(),msg.getType().toString(), msg.getPayload()
                    , String.format("[%s]No ledger context for %s. Sending %s", shorttxid(msg.getTxid()), msg.getType().toString(), ERROR.toString()));
            //if triggerNextStateMsg is null means txContext is vaild
            if(triggerNextStateMsg == null){
                txContext = getTxContext(msg.getGroupId(), msg.getTxid());
            }
            //check transaction context txContext
            if(txContext == null){
                String errStr = String.format("[%s]EnterBysySate. No ledger context for GetHistoryForKey. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                triggerNextStateMsg = SmartContractShim.SmartContractMessage.newBuilder()
                        .setType(ERROR)
                        .setPayload(ByteString.copyFromUtf8(errStr))
                        .setTxid(msg.getTxid())
                        .setGroupId(msg.getGroupId())
                        .build();
                returnTriggerNextState(msg, triggerNextStateMsg);
                return;
            }
            //create chaincodeID
            chaincodeID = getSmartContractRootName();
            if(PUT_STATE.equals(msg.getType())){
                //handle PUT_STATE
                try {
                    SmartContractShim.PutState putState = SmartContractShim.PutState.parseFrom(msg.getPayload());

                    if(isCollectionSet(putState.getCollection())){
//                        txContext.getTxSimulator().setPrivateDate(chaincodeID, putState.getCollection(), putState.getKey(), putState.getValue());
                    } else {
                        txContext.getTxSimulator().setState(chaincodeID, putState.getKey(), putState.getValue().toByteArray());
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.error(String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    triggerNextStateMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)));
                    returnTriggerNextState(msg, triggerNextStateMsg);
                } catch (LedgerException e){
                    logger.error(String.format("[%s]Unable to set state. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    triggerNextStateMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)));
                    returnTriggerNextState(msg, triggerNextStateMsg);
                }
            } else if(DEL_STATE.equals(msg.getType())){
                //handle DEL_STATE
                try {
                    SmartContractShim.DelState delState = SmartContractShim.DelState.parseFrom(msg.getPayload());

                    if(isCollectionSet(delState.getCollection())){
//                        txContext.getTxSimulator().deletePrivateDate(chaincodeID, putState.getCollection(), putState.getKey(), putState.getValue());
                    } else {
                        txContext.getTxSimulator().deleteState(chaincodeID, delState.getKey());
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.error(String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    triggerNextStateMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)));
                    returnTriggerNextState(msg, triggerNextStateMsg);
                } catch (LedgerException e){
                    logger.error(String.format("[%s]Unable to delete state. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    triggerNextStateMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)));
                    returnTriggerNextState(msg, triggerNextStateMsg);
                }
            } else if (INVOKE_SMARTCONTRACT.equals(msg.getType())){
                //1.构造CS结构
                SmartContractPackage.SmartContractSpec chaincodeSpec = null;
                SmartContractInstance calledCcIns = null;
                SmartContractPackage.SmartContractID scID = null;
                try {
                    logger.info(String.format("[%s] C-call-C", shorttxid(msg.getTxid())));
                    chaincodeSpec = SmartContractPackage.SmartContractSpec.parseFrom(msg.getPayload());
                } catch (InvalidProtocolBufferException e) {
                    logger.error(String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    triggerNextStateMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)));
                    returnTriggerNextState(msg, triggerNextStateMsg);
                    return;
                }
                //2.实例化Chaincode
                //3.操作账本
                //4.判断是否为系统链码Scc
                //5.获取版本version
                //6.获取timeout
                //7.封装为res
                calledCcIns = getSmartContractInstance(chaincodeSpec.getSmartContractId().getName());
                scID = chaincodeSpec.getSmartContractId().toBuilder()
                        .setName(calledCcIns.getSmartContractName())
                        .build();
                chaincodeSpec = chaincodeSpec.toBuilder()
                        .setSmartContractId(scID)
                        .build();
                if("".equals(calledCcIns.getGroupId())){
                    calledCcIns.setGroupId(txContext.getChainID());
                }
                logger.info(String.format("[%s] C-call-C %s on channel %s"
                        , shorttxid(msg.getTxid()), calledCcIns.getSmartContractName(), calledCcIns.getGroupId()));
                try{
                    //unrealized function, throws RuntionException
                    checkACL(txContext.getSignedProp(), txContext.getProposal(), calledCcIns);
                } catch (RuntimeException e){
                    logger.error(String.format("[%s] C-call-C %s on channel %s failed check ACL [%s]. Sending %s"
                            , shorttxid(msg.getTxid()), calledCcIns.getSmartContractName(), calledCcIns.getGroupId(), txContext.getSignedProp(), printStackTrace(e)));
                    triggerNextStateMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)));
                    returnTriggerNextState(msg, triggerNextStateMsg);
                    return;
                }

                // Set up a new context for the called chaincode if on a different channel
                // We grab the called channel's ledger simulator to hold the new state
                TransactionContext ctxt = new TransactionContext();
                ITxSimulator txsim = txContext.getTxSimulator();
                IHistoryQueryExecutor historyQueryExecutor = txContext.getHistoryQueryExecutor();

//                if(!calledCcIns.getSmartContractID().equals(txContext.getChainID())){
//                        lgr := peer.GetLedger(calledCcIns.ChainID)
                    NodeConfig.Ledger lgr = new NodeConfig.Ledger();
                    if(lgr == null){
                        ByteString payload = ByteString.copyFromUtf8("Failed to find ledger for called channel " + calledCcIns.getGroupId());
                        triggerNextStateMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), payload);
                        return;
                    }

                    ITxSimulator txsim2 = null;
//                        txsim2 = lgr.newTxSimulator(msg.getTxid());
//                        if err2 != nil {
//                            triggerNextStateMsg = &pb.ChaincodeMessage{Type: pb.ChaincodeMessage_ERROR,
//                                    Payload: []byte(err2.Error()), Txid: msg.Txid, GroupId: msg.GroupId}
//                            return
//                        }

                    txsim = txsim2;
//                }
//                    ctxt = context.WithValue(ctxt, TXSimulatorKey, txsim)
//                    ctxt = context.WithValue(ctxt, HistoryQueryExecutorKey, historyQueryExecutor)
                logger.info(String.format("[%s] getting chaincode data for %s on channel %s"
                        , shorttxid(msg.getTxid()), calledCcIns.getSmartContractName(), calledCcIns.getGroupId()));

                boolean isscc = true;
//                    isscc := sysccprovider.GetSystemChaincodeProvider().IsSysCC(calledCcIns.ChaincodeName)
                String version = null;
                if(!isscc){
                    //if its a user chaincode, get the details
//                        cd, err := GetChaincodeDefinition(ctxt, msg.Txid, txContext.signedProp, txContext.proposal, calledCcIns.ChainID, calledCcIns.ChaincodeName)
//                        if err != nil {
//                            errHandler([]byte(err.Error()), "[%s]Failed to get chaincode data (%s) for invoked chaincode. Sending %s", shorttxid(msg.Txid), err, pb.ChaincodeMessage_ERROR)
//                            return
//                        }
//
//                        version = cd.CCVersion()
//
//                        err = ccprovider.CheckInstantiationPolicy(calledCcIns.ChaincodeName, version, cd.(*ccprovider.ChaincodeData))
//                        if err != nil {
//                            errHandler([]byte(err.Error()), "[%s]CheckInstantiationPolicy, error %s. Sending %s", shorttxid(msg.Txid), err, pb.ChaincodeMessage_ERROR)
//                            return
//                        }
                } else {
//                        //this is a system cc, just call it directly
//                        version = util.GetSysCCVersion()
                }


//                    cccid := ccprovider.NewCCContext(calledCcIns.ChainID, calledCcIns.ChaincodeName, version, msg.Txid, false, txContext.signedProp, txContext.proposal)
//
//                    // Launch the new chaincode if not already running
//                    chaincodeLogger.Debugf("[%s] launching chaincode %s on channel %s",
//                            shorttxid(msg.Txid), calledCcIns.ChaincodeName, calledCcIns.ChainID)
//                    cciSpec := &pb.ChaincodeInvocationSpec{ChaincodeSpec: chaincodeSpec}
//                    _, chaincodeInput, launchErr := handler.chaincodeSupport.Launch(ctxt, cccid, cciSpec)
//                    if launchErr != nil {
//                        errHandler([]byte(launchErr.Error()), "[%s]Failed to launch invoked chaincode. Sending %s", shorttxid(msg.Txid), pb.ChaincodeMessage_ERROR)
//                        return
//                    }
//
//                    // TODO: Need to handle timeout correctly
//                    timeout := time.Duration(30000) * time.Millisecond
//
//                    ccMsg, _ := createCCMessage(pb.ChaincodeMessage_TRANSACTION, calledCcIns.ChainID, msg.Txid, chaincodeInput)
//
//                    // Execute the chaincode... this CANNOT be an init at least for now
//                    response, execErr := handler.chaincodeSupport.Execute(ctxt, cccid, ccMsg, timeout)
//
//                    //payload is marshalled and send to the calling chaincode's shim which unmarshals and
//                    //sends it to chaincode
//                    res = nil
//                    if execErr != nil {
//                        err = execErr
//                    } else {
//                        res, err = proto.Marshal(response)
//                    }
            }
//                if err != nil {
//                    errHandler([]byte(err.Error()), "[%s]Failed to handle %s. Sending %s", shorttxid(msg.Txid), msg.Type.String(), pb.ChaincodeMessage_ERROR)
//                    return
//                }
//
//                // Send response msg back to chaincode.
//                chaincodeLogger.Debugf("[%s]Completed %s. Sending %s", shorttxid(msg.Txid), msg.Type.String(), pb.ChaincodeMessage_RESPONSE)
//                triggerNextStateMsg = &pb.ChaincodeMessage{Type: pb.ChaincodeMessage_RESPONSE, Payload: res, Txid: msg.Txid, GroupId: msg.GroupId}
        res = ByteString.copyFromUtf8("");
        triggerNextStateMsg = SmartContractShim.SmartContractMessage.newBuilder()
                .setType(RESPONSE)
                .setPayload(res)
                .setGroupId(msg.getGroupId())
                .setTxid(msg.getTxid())
                .build();
        }).start();
    }

    public void enterEstablishedState(Event e, String state) {
        notifyDuringStartup(true);
    }

    public void enterReadyState(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        notify(msg);
        logger.info(String.format("[%s]Entered state %s", shorttxid(msg.getTxid()), state));
    }

    public void enterEndState(Event event, String state) {
        SmartContractShim.SmartContractMessage msg = extractMessageFromEvent(event);
            logger.info(String.format("[%s]Entered state %s", shorttxid(msg.getTxid()), state));
            notify(msg);
            deregister();
    }

    public SmartContractShim.SmartContractMessage setChaincodeProposal(ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop, SmartContractShim.SmartContractMessage msg) {
        logger.info("Setting chaincode proposal context...");
        if(prop != null){
            logger.info("Proposal different from null. Creating chaincode proposal context...");
            //Check that also signedProp is different from null
            if(signedProp == null){
                logger.error("failed getting proposal context. Signed proposal is null");
                return null;
            }
            msg = msg.toBuilder().setProposal(signedProp).build();
        }
        return msg;
    }

    /**move to ready
     */
    public SmartContractShim.SmartContractMessage ready(Context ctxt, String chainID, String txid, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
        TransactionContext txctx = createTxContext(ctxt, chainID, txid, signedProp, prop);

        logger.info("sending READY");
        SmartContractShim.SmartContractMessage msg = newEventMessage(READY, chainID, txid, null);

        msg = setChaincodeProposal(signedProp, prop, msg);
        if(msg == null){
            return null;
        }

        //send the ready synchronously as the
        //ready message is during launch and needs
        //to happen before any init/invokes can sneak in
        triggerNextStateSync(msg);
        return  txctx.getResponseNotifier();
    }

    /** handleMessage is the entrance method for Peer's handling of Chaincode messages.
     */
    public void handleMessage(SmartContractShim.SmartContractMessage msg) {
        logger.info(String.format("[%s]Handling message of type: %s in state %s", shorttxid(msg.getTxid()), msg.getType(), fsm.current()));

        //msg cannot be null in processStream
        if((COMPLETED.equals(msg.getType()) || ERROR.equals(msg.getType())) && READY_STATE.equals(fsm.current())){
            logger.info("[%s]Handle message - COMPLETED. Notify", msg.getTxid());
            notify(msg);
            return;
        }
        if(fsm.eventCannotOccur(msg.getType().toString())){
            logger.error(String.format("[%s]Chaincode handler validator FSM cannot handle message (%s) while in state: %s"
                    , msg.getTxid(), msg.getType(), fsm.current()));
            return;
        }
        try {
            fsm.raiseEvent(msg.getType().toString(), msg);
        } catch (Exception e) {
            if(filterError(e)){
                logger.error("[%s]Failed to trigger FSM event %s: %s", msg.getTxid(), msg.getType().toString(), printStackTrace(e));
            }
        }
    }

    private boolean filterError(Throwable throwable){
        if(throwable != null){
            if(throwable instanceof NoTransitionException){
                return true;
            }
            logger.info(String.format("Ignoring NoTransitionException: %s", throwable));
        }
        if(throwable != null){
            if(throwable instanceof CancelledException){
                return true;
            }
            logger.info(String.format("Ignoring CancelledException: %s", throwable));
        }
        return false;
    }

    public SmartContractShim.SmartContractMessage sendExecuteMessage(Context ctxt, String chainID, SmartContractShim.SmartContractMessage msg, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
        TransactionContext txctx = createTxContext(ctxt, chainID, msg.getTxid(), signedProp, prop);
        if(txctx != null){
            return null;
        }
        logger.info("[%s]Inside sendExecuteMessage. Message %s", shorttxid(msg.getTxid()), msg.getType().toString());
        msg = setChaincodeProposal(signedProp, prop, msg);
        if(msg == null){
            return null;
        }

        logger.info("[%s]sendExecuteMsg trigger event %s", shorttxid(msg.getTxid()), msg.getType().toString());
        triggerNextState(msg, true);

        return txctx.getResponseNotifier();
    }

    public Boolean isRunning() {
        String current = getFsm().current();
        return !StringUtils.equals(current, CREATED_STATE) && !StringUtils.equals(current, ESTABLISHED_STATE);
    }

    /**
     * get chaincode message from event
     */
    private SmartContractShim.SmartContractMessage extractMessageFromEvent(Event event) {
        try {
            return (SmartContractShim.SmartContractMessage) event.args[0];
        } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
            final RuntimeException error = new RuntimeException("No chaincode message found in event", e);
            event.cancel(error);
            throw error;
        }
    }

    /**
     * create new event msg
     */
    private static SmartContractShim.SmartContractMessage newEventMessage(final SmartContractShim.SmartContractMessage.Type type
            , final  String GroupId, final String txId, final ByteString payload){
        if (payload == null){
            return SmartContractShim.SmartContractMessage.newBuilder()
                    .setType(type)
                    .setGroupId(GroupId)
                    .setTxid(txId)
                    .build();
        } else {
            return SmartContractShim.SmartContractMessage.newBuilder()
                    .setType(type)
                    .setGroupId(GroupId)
                    .setTxid(txId)
                    .setPayload(payload)
                    .build();
        }
    }

    /**
     * throwable to string
     */
    private static String printStackTrace(Throwable throwable) {
        if (throwable == null) return null;
        final StringWriter buffer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(buffer));
        return buffer.toString();
    }

    /**
     * set QueryResponse's results
     */
    private static SmartContractShim.QueryResponse.Builder setQueryResponseReuslt(SmartContractShim.QueryResponse.Builder builder, SmartContractShim.QueryResultBytes[] batch){
        for (int i = 0; i < builder.getResultsCount(); i++) {
            builder.addResults(batch[i]);
        }
        return builder;
    }

    private void errorReturn(SmartContractShim.SmartContractMessage msg, ByteString payload){
        //do followed functions before retun
        //delete transaction context
        deleteTXIDEntry(msg.getGroupId(), msg.getTxid());
        logger.error(payload.toStringUtf8());
        //send msg
        SmartContractShim.SmartContractMessage serialSendMsg = newEventMessage(ERROR, msg.getGroupId(), msg.getTxid(), payload);
        serialSendAsync(serialSendMsg);
    }

    private void successReturn(SmartContractShim.SmartContractMessage msg, ByteString payload, SmartContractShim.SmartContractMessage.Type type){
        //do followed functions before retun
        //delete transaction context
        deleteTXIDEntry(msg.getGroupId(), msg.getTxid());
        //send msg
        SmartContractShim.SmartContractMessage serialSendMsg = newEventMessage(type, msg.getGroupId(), msg.getTxid(), payload);
        serialSendAsync(serialSendMsg);
    }

    private void returnTriggerNextState(SmartContractShim.SmartContractMessage msg, SmartContractShim.SmartContractMessage triggerNextStateMsg){
        //do followed functions before retun
        //delete transaction context
        deleteTXIDEntry(msg.getGroupId(), msg.getTxid());
        logger.error(String.format("[%s]enterBusyState trigger event %s", shorttxid(msg.getTxid()), triggerNextStateMsg.getType().toString()));
        //trigger next state
        triggerNextState(triggerNextStateMsg, true);
    }
}


