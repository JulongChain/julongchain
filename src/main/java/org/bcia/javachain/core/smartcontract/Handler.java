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
package org.bcia.javachain.core.smartcontract;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.QueryResult;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.javachain.core.container.scintf.ISmartContractStream;
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.core.smartcontract.shim.fsm.CBDesc;
import org.bcia.javachain.core.smartcontract.shim.fsm.Event;
import org.bcia.javachain.core.smartcontract.shim.fsm.EventDesc;
import org.bcia.javachain.core.smartcontract.shim.fsm.FSM;
import org.bcia.javachain.core.smartcontract.shim.fsm.exceptions.*;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.SmartContractEventPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.SmartcontractShim;

import javax.naming.Context;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.bcia.javachain.core.smartcontract.shim.fsm.CallbackType.AFTER_EVENT;
import static org.bcia.javachain.core.smartcontract.shim.fsm.CallbackType.BEFORE_EVENT;
import static org.bcia.javachain.core.smartcontract.shim.fsm.CallbackType.ENTER_STATE;
import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage.Type.*;

/**
 * Handler responsible for management of Peer's side of chaincode stream
 *
 * @author wanliangbing
 * @date 2018/3/18
 * @company Dingxuan
 */
public class Handler {

    /**
     * start state
     */
    public static final String CREATED_STATE     = "created";
    /**
     * in: CREATED, rcv:  REGISTER, send: REGISTERED, INIT
     */
    public static final String ESTABLISHED_STATE = "established";
    /**
     * in:ESTABLISHED,TRANSACTION, rcv:COMPLETED
     */
    public static final String READY_STATE       = "ready";
    /**
     * in:INIT,ESTABLISHED, rcv: error, terminate container
     */
    public static final String END_STATE         = "end";

    private static JavaChainLog logger = JavaChainLogFactory.getLog(Handler.class);

    private ISmartContractStream chatStream;
    private FSM fsm;
    private Smartcontract.SmartContractID smartContractID;
    private SmartContractInstance smartContractInstance;
    private SmartContractSupport smartContractSupport;
    private Boolean registered;
    private Boolean readyNotify;
    private Map<String, TransactionContext> txCtxs;
    private Map<String, Boolean> txidMap;
    private Channel<NextStateInfo> nextState;

    //newChaincodeSupportHandler
    public Handler(SmartContractSupport chaincodeSupport, ISmartContractStream peerChatStream){
        this.chatStream  = peerChatStream;
        this.smartContractSupport = chaincodeSupport;

//        this.smartContractID = Smartcontract.SmartContractID.newBuilder()
//                .build();
//        this.smartContractInstance = new SmartContractInstance();
        this. registered = Boolean.FALSE;
//        this.readyNotify = Boolean.FALSE;
        this.txCtxs = new HashMap<>();
        this.txidMap = new HashMap<>();
        this.nextState = new Channel<>();

        this.fsm = new FSM("created");

        fsm.addEvents(
            //            Event Name                        From                To
            new EventDesc(REGISTERED.toString(),            "created",      "established"),
            new EventDesc(READY.toString(),                 "established",  "ready"),
            new EventDesc(PUT_STATE.toString(),             "ready",        "ready"),
            new EventDesc(DEL_STATE.toString(),             "ready",        "ready"),
            new EventDesc(INVOKE_CHAINCODE.toString(),      "ready",        "ready"),
            new EventDesc(COMPLETED.toString(),             "ready",        "ready"),
            new EventDesc(GET_STATE.toString(),             "ready",        "ready"),
            new EventDesc(GET_STATE_BY_RANGE.toString(),    "ready",        "ready"),
            new EventDesc(GET_QUERY_RESULT.toString(),      "ready",        "ready"),
            new EventDesc(GET_HISTORY_FOR_KEY.toString(),   "ready",        "ready"),
            new EventDesc(QUERY_STATE_NEXT.toString(),      "ready",        "ready"),
            new EventDesc(QUERY_STATE_CLOSE.toString(),     "ready",        "ready"),
            new EventDesc(ERROR.toString(),                 "ready",        "ready"),
            new EventDesc(RESPONSE.toString(),              "ready",        "ready"),
            new EventDesc(INIT.toString(),                  "ready",        "ready"),
            new EventDesc(TRANSACTION.toString(),           "ready",        "ready")
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
            new CBDesc(AFTER_EVENT,     INVOKE_CHAINCODE.toString(),    (event) -> enterBusyState(event, fsm.current())),
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

    public Smartcontract.SmartContractID getSmartContractID() {
        return smartContractID;
    }

    public void setSmartContractID(Smartcontract.SmartContractID smartContractID) {
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
     * return firest 8 chars in txid
     * @param txid
     * @return
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
     * @param smartContractID
     */
    public void decomposeRegisteredName(Smartcontract.SmartContractID smartContractID) {
        this.setSmartContractInstance(getSmartContractInstance(smartContractID.getName()));
    }

    /**
     * get smart contract with smartContractName
     * @param smartContractName name:version/id
     * @return
     */
    public static SmartContractInstance getSmartContractInstance(String smartContractName) {
        SmartContractInstance instance = new SmartContractInstance();

        //计算后缀（ie, chain name)
        int i = smartContractName.indexOf("/");
        if(i >= 0){
            if(i < smartContractName.length() - 1){
                instance.setSmartContractID(smartContractName.substring(i + 1, smartContractName.length()));
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
     * @return
     */
    public String getSmartContractRootName() {
        return getSmartContractInstance().getSmartContractName();
    }

    /**
     * send smart contract message with gRPC channel "chatStream" sychronized
     * @param msg
     */
    public synchronized void serialSend(SmartcontractShim.SmartContractMessage msg) {
        chatStream.send(msg);
    }

    /**
     * send smart contract message with gRPC channel "chatStream" asychronized
     * @param msg
     */
    public void serialSendAsync(SmartcontractShim.SmartContractMessage msg) {

        new Thread(() -> {
            serialSend(msg);
        }).start();
    }

    /**
     * get transaction context id which is chainid + txid
     * @param chainID
     * @param txid
     * @return
     */
    public String getTxCtxId(String chainID, String txid) {
        if(chainID != null && txid != null){
            return chainID + txid;
        } else {
            return null;
        }
    }

    /**
     * create transaction context with following args
     * @param ctxt
     * @param chainID
     * @param txid
     * @param signedProp
     * @param prop
     * @return
     */
    public TransactionContext createTxContext(Context ctxt, String chainID, String txid, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
        if(txCtxs == null) {
           return null;
        }

        String txCtxID = getTxCtxId(chainID, txid);
        //if transaction context which's id is txCtxID is exist
        if(txCtxs.get(txCtxID) != null){
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

        return txctx;
    }

    /**
     * get transcation context with transcation id "chainID + txid"
     * @param chainID
     * @param txid
     * @return
     */
    public synchronized TransactionContext getTxContext(String chainID, String txid) {
        String txCtxID = getTxCtxId(chainID, txid);
        return txCtxs.get(txCtxID);
    }

    /**
     * delete transcation context with transcation id "chainID + txid" if exist
     * @param chainID
     * @param txid
     */
    public synchronized void deleteTxContext(String chainID, String txid) {
        String txCtxID = getTxCtxId(chainID, txid);
        if(txCtxID != null) {
            txCtxs.remove(txCtxID);
        }
    }

    /**
     * put current txContext into txContext's queryIteratorMap for init it.
     * @param txContext
     * @param queryID
     * @param queryIterator
     */
    public synchronized void initializeQueryContext(TransactionContext txContext, String queryID,
                                       ResultsIterator queryIterator) {
        txContext.getQueryIteratorMap().put(queryID, queryIterator);
    }

    /**
     * get a transcation context which's query id is "queryID" in cxContext's queryIteratorMap
     * @param txContext
     * @param queryID
     * @return
     */
    public synchronized ResultsIterator getQueryIterator(TransactionContext txContext, String queryID) {
        return txContext.getQueryIteratorMap().get(queryID);
    }

    /**
     * close and remove current ResultIterator which's query id is "queryID"
     * @param txContext
     * @param queryID
     * @throws LedgerException
     */
    public synchronized void cleanupQueryContext(TransactionContext txContext, String queryID){
        try {
            txContext.getQueryIteratorMap().get(queryID).close();
            txContext.getQueryIteratorMap().remove(queryID);
            txContext.getPendingQueryResults().remove(queryID);
        } catch (LedgerException e) {
            throw new RuntimeException("Got error when clean up query context");
        }
    }

    /**
     * Check if the transactor is allow to call this chaincode on this channel
     * @param signedProp
     * @param proposal
     * @param ccIns
     */
    public void checkACL(ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal proposal, SmartContractInstance ccIns) {

        //need syssmartcontractprovider
    }

    /**
     * deregister this handler if it is registed
     */
    public void deregister() {
        if(this.getRegistered()){
            //need ssSupport.deregister
        }
        return;
    }

    public void triggerNextState(SmartcontractShim.SmartContractMessage msg, Boolean send) {
        NextStateInfo nsInfo = new NextStateInfo();
        nsInfo.setMsg(msg);
        nsInfo.setSendToCC(send);
        nsInfo.setSendSync(Boolean.FALSE);
        nextState.add(nsInfo);
    }

    public void triggerNextStateSync(SmartcontractShim.SmartContractMessage msg) {
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
        return;
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
                    SmartcontractShim.SmartContractMessage in = null;
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
                        logger.info("Received KEEPALIVE Response");
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

                    SmartcontractShim.SmartContractMessage in = nsInfo.getMsg();
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
     *
     * @param chaincodeSupport
     * @param ctxt
     * @param stream
     */
    public static void handleChaincodeStream(SmartContractSupport chaincodeSupport, Context ctxt, ISmartContractStream stream) {
        //check deadline
        logger.info("Handle current context");
        Handler handler = new Handler(chaincodeSupport, stream);
        handler.processStream();
    }

    /**
     * put txCtxID into txidMap
     * @param channelID
     * @param txid
     * @return
     */
    public synchronized Boolean createTXIDEntry(String channelID, String txid) {
        if(txidMap == null){
            return Boolean.FALSE;
        }
        String txCtxID = getTxCtxId(channelID, txid);
        if(txCtxID == null){
            logger.info(String.format("[%s]Transcation context id is null", shorttxid(txid)));
            throw new RuntimeException("Transcation context id is null");
        }
        if (txidMap.get(txCtxID) == null || txidMap.get(txCtxID)) {
            return Boolean.FALSE;
        }
        txidMap.put(txCtxID, Boolean.TRUE);

        return txidMap.get(txCtxID);
    }

    /**
     * remove teCtxID from txidMap
     * @param channelID
     * @param txid
     */
    public synchronized void deleteTXIDEntry(String channelID, String txid) {
        String txCtxID = getTxCtxId(channelID, txid);
        if(txidMap != null){
            txidMap.remove(txCtxID);
        } else {
            logger.error(String.format("TXID %s is not found", txCtxID));
        }
    }

    /**
     * notiry READY message as next state
     * @param val
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
                    SmartcontractShim.SmartContractMessage ccMsg = SmartcontractShim.SmartContractMessage.newBuilder()
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
     *
     * @param event
     * @param state
     */
     public void beforeRegisterEvent(Event event, String state) {
         try {
             logger.info(String.format("Received %s in state %s", event, state));
             SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
             //throws InvalidProtocolBufferException
             Smartcontract.SmartContractID id = Smartcontract.SmartContractID.parseFrom(msg.getPayload());

             //Register with chaincodeSupptort
             smartContractID = id;
//             requires function smartContractSupport.registerHandler
//             smartContractSupport.registerHandler(this);

//             if catch exception during registerHandler, do notifyDuringStartup(false)
//             notifyDuringStartup(false);

             //now we have all component sothat we can have root name of chaincode
             decomposeRegisteredName(smartContractID);
             logger.info(String.format("Got %s for chaincodeID = %s, sending back %s", event, id, REGISTERED.toString()));

             //send msg with msgType REGISTED
             serialSend(SmartcontractShim.SmartContractMessage.newBuilder()
                     .setType(REGISTERED)
                     .build());
         } catch (InvalidProtocolBufferException | RuntimeException e){
             final RuntimeException error = new RuntimeException(String.format("error in received %s", REGISTER.toString()));
             event.cancel(error);
             throw error;
         }
    }

    /**
     * notify msg
     * @param msg
     * @throws LedgerException
     */
    public synchronized void notify(SmartcontractShim.SmartContractMessage msg) {
        String txCtxId = getTxCtxId(msg.getChannelId(), msg.getTxid());
        TransactionContext tctx = txCtxs.get(txCtxId);
        if (tctx == null) {
            logger.info(String.format("Notifier Tid: %s, channelID: %s does not exist", msg.getTxid(), msg.getChannelId()));
        } else {
            logger.info(String.format("Notifing Tid: %s, channelID: %s", msg.getTxid(), msg.getChannelId()));
//            require ResponseNotifier's notify function to notify msg
//            tctx.getResponseNotifier().notify(msg);

            logger.debug("This is notify()");
            //clean up queryIteratorMap
            tctx.getQueryIteratorMap().forEach((k, v) -> {
                try {
                    v.close();
                } catch (LedgerException e){
                    logger.error("Got error when close iterator");
                }
            });
        }
    }

    /** beforeCompletedEvent is invoked when chaincode has completed execution of init, invoke.
     *
     * @param event
     * @param state
     */
    public void beforeCompletedEvent(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("[%s]beforeCompleted - not in ready state will notify when in readystate", shorttxid(msg.getTxid())));
    }

    /** afterGetState handles a GET_STATE request from the chaincode.
     *
     * @param event
     * @param state
     */
    public void afterGetState(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("[%s]Received %s, invoking get state from ledger", shorttxid(msg.getTxid()), GET_STATE.toString()));
        handleGetState(msg);
    }
    /** is this a txid for which there is a valid txsim
     *  if this txid for which txsim is not valid, return a msg with error
     *  else return null
     * @param channelID
     * @param txid
     * @param fmtStr
     * @return
     */
    public SmartcontractShim.SmartContractMessage isValidTxSim(String channelID, String txid, String fmtStr) {
        TransactionContext txContext = getTxContext(channelID, txid);
        SmartcontractShim.SmartContractMessage msg = null;

        //if txContext is null or its txSimulator is null, create a SmartContextMessage obj with error message
        if(txContext == null || txContext.getTxSimulator() == null){
            msg = newEventMessage(ERROR, channelID, txid, ByteString.copyFromUtf8(fmtStr), null);
        }

        //if getTxContext success, return null
        return msg;
    }

    /** Handles query to ledger to get state
     *
     * @param msg
     */
    public void handleGetState(SmartcontractShim.SmartContractMessage msg) {
        new Thread(() -> {
            //serialSendMsg is used to send error msg
            SmartcontractShim.SmartContractMessage serialSendMsg = null;
            //flag of if do "defer function"
            boolean flag = false;
            TransactionContext txContext = null;
            String key = null;
            SmartcontractShim.GetState getState = null;
            String chaincodeID = null;
            ByteString res = null;
            try {
                //judge if put txId into query map is success
                boolean uniqueReq = createTXIDEntry(msg.getChannelId(), msg.getTxid());
                if (!uniqueReq) {
                    logger.error(String.format("[%s]Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid())));
                    return;
                }
                // after this, before function return txidEntry should be deleted and send serialSendMsg
                flag = true;
                //create transaction context
                txContext = getTxContext(msg.getChannelId(), msg.getTxid());
                if (txContext == null || txContext.getTxSimulator() == null) {
                    String errStr = String.format("[%s]No ledger context for GetState. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                    logger.error(errStr);
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(errStr), null);
                    return;
                }
                //create key
                key = msg.getPayload().toStringUtf8();
                //create get state
                try {
                    getState = SmartcontractShim.GetState.parseFrom(msg.getPayload());
                } catch (InvalidProtocolBufferException e){
                    logger.error(String.format("[%s]Failed to create get state. Sending %s", ERROR.toString(), shorttxid(msg.getTxid())));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create chaincode id
                chaincodeID = getSmartContractRootName();
                logger.info(String.format("[%s] getting state for chaincode %s, key %s, channel %s",
                        shorttxid(msg.getTxid()), chaincodeID, getState.getKey(), txContext.getChainID()));
                //create ByteString res
                try {
                    if(isCollectionSet(getState.getCollection())){
//                    if isCollectionSet(getState.Collection) {
//                        res, err = txContext.txsimulator.GetPrivateData(chaincodeID, getState.Collection, getState.Key)
//                    } else {
//                        res, err = txContext.txsimulator.GetState(chaincodeID, getState.Key)
//                    }
                    }
                    if (res == null) {
                        //The state object being requested does not exist
                        logger.info(String.format("[%s]No state associated with key: %s. Sending %s with an empty payload", shorttxid(msg.getTxid()), key, RESPONSE.toString()));
                        serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), res, null);
                    } else {
                        //success, send response msg back to chaincode. GetState will not trigger event
                        logger.info(String.format("[%s]Got state. Sending %s", shorttxid(msg.getTxid()), RESPONSE.toString()));
                        serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), res, null);
                    }
                } catch (java.lang.Exception e) {
                    //Get error when create ByteString res, send error msg back to chaincode. GetState will not trigger event
                    logger.error(String.format("[%s]Failed to get chaincode state(%s). Sending %s", shorttxid(msg.getTxid()), printStackTrace(e), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(),msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
            } finally {
                if (flag) {
                    //do followed functions before retun
                    //delete transaction context
                    deleteTXIDEntry(msg.getChannelId(), msg.getTxid());
                    logger.info(String.format("[%s]handlerGetState serial send %s", shorttxid(serialSendMsg.getTxid()), serialSendMsg.getType()));
                    //send msg
                    serialSendAsync(serialSendMsg);
                }
            }
        }).start();
    }

    /** afterGetStateByRange handles a GET_STATE_BY_RANGE request from the chaincode.
     *
     * @param event
     * @param state
     */
    public void afterGetStateByRange(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("Received %s, invoking get state from ledger", GET_STATE_BY_RANGE.toString()));
        //query ledger for state
        handleGetStateByRange(msg);
        logger.info("Exiting GET_STATE_BY_RANGE");
    }

    /** Handles query to ledger to rage query state
     *
     * @param msg
     */
    public void handleGetStateByRange(SmartcontractShim.SmartContractMessage msg) {
        new Thread(() -> {
            //serialSendMsg is used to send error msg
            SmartcontractShim.SmartContractMessage serialSendMsg = null;
            //flag of if do "defer function"
            boolean flag = false;
            SmartcontractShim.GetStateByRange getStateByRange = null;
            ResultsIterator rangeIter = null;
            String chainCodeID = null;
            String iterID = null;
            TransactionContext txContext = null;
            SmartcontractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            try {
                //judge if put txId into query map is success
                boolean uniqueReq = createTXIDEntry(msg.getChannelId(), msg.getTxid());
                if (!uniqueReq) {
                    logger.error(String.format("[%s]Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid())));
                    return;
                }
                // after this, before function return txidEntry should be deleted and send serialSendMsg
                flag = true;
                //create get state by range
                try {
                    getStateByRange = SmartcontractShim.GetStateByRange.parseFrom(msg.getPayload());
                } catch (InvalidProtocolBufferException e){
                    logger.error(String.format("[%s]Fail to create get state by range. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create iterId
                //iteId = util.GenerateUUID();
                //create transaction context
                txContext = getTxContext(msg.getChannelId(), msg.getTxid());
                if (txContext == null || txContext.getTxSimulator() == null){
                    String errStr = String.format("[%s]No ledger context for GetStateByRange. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                    logger.error(errStr);
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(errStr), null);
                    return;
                }
                //create chaincode id
                chainCodeID = getSmartContractRootName();
                //create ResultsIterator rangeIter
                try {
                    if(isCollectionSet(getStateByRange.getCollection())){
        //                rangeIter, err = txContext.txsimulator.GetPrivateDataRangeScanIterator(chaincodeID, getStateByRange.Collection, getStateByRange.StartKey, getStateByRange.EndKey)
                    } else {
        //                rangeIter, err = txContext.txsimulator.GetStateRangeScanIterator(chaincodeID, getStateByRange.StartKey, getStateByRange.EndKey)
                    }
                } catch (Exception e) {
                    logger.error("[%s]Got error when get ledger scan iterator. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                initializeQueryContext(txContext, iterID, rangeIter);
                //create QueryResponse payload
                try {
                    payload = getQueryResponse(this, txContext, rangeIter, iterID);
                } catch (LedgerException e) {
                    if(rangeIter != null){
                        cleanupQueryContext(txContext, iterID);
                    }
                    logger.error(String.format("[%s]Failed to get query result in HandlerGetStateByRange. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create ByteString payloadBytes
                payloadBytes = payload.toByteString();
                //success
                logger.info(String.format("[%s]Got keys and values. Sending %s", shorttxid(msg.getTxid()), RESPONSE.toString()));
                serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), payloadBytes, null);
            } finally {
                if(flag){
                    //do followed functions before retun
                    //delete transaction context
                    deleteTXIDEntry(msg.getChannelId(), msg.getTxid());
                    logger.info(String.format("[%s]handlerGetStateByRange serial send %s", shorttxid(serialSendMsg.getTxid()), serialSendMsg.getType()));
                    //send msg
                    serialSendAsync(serialSendMsg);
                }
            }
        }).start();
    }

    public static Integer maxResultLimit = 100;

    /** getQueryResponse takes an iterator and fetch state to construct QueryResponse
     *
     * @param handler
     * @param txContext
     * @param iter
     * @param iterID
     * @return
     */
    public SmartcontractShim.QueryResponse getQueryResponse(Handler handler, TransactionContext txContext, ResultsIterator iter,
                                                                   String iterID) throws LedgerException {
        try {
            PendingQueryResult pendingQueryResults = txContext.getPendingQueryResults().get(iterID);
            while(true){
                QueryResult queryResult = iter.next();
                if(queryResult == null){
                    //no response from iterator sothat it is end of query results
                    SmartcontractShim.QueryResultBytes[] batch = cut(pendingQueryResults);
                    cleanupQueryContext(txContext, iterID);
                    return setQueryResponseReuslt(SmartcontractShim.QueryResponse.newBuilder()
                            .setId(iterID)
                            .setHasMore(false), batch).build();
                } else if(maxResultLimit.equals(pendingQueryResults.getCount())){
                    //max count of results queued up, cut batch, then add current result to pending batch
                    SmartcontractShim.QueryResultBytes[] batch = cut(pendingQueryResults);
                    try {
                        add(pendingQueryResults, queryResult);
                    } catch (java.lang.Exception e) {
                        cleanupQueryContext(txContext, iterID);
                        return null;
                    }
                    return setQueryResponseReuslt(SmartcontractShim.QueryResponse.newBuilder()
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
     * @param p
     * @return
     */
    public SmartcontractShim.QueryResultBytes[] cut(PendingQueryResult p) {
        SmartcontractShim.QueryResultBytes[] batch = p.getBatch();
        p.setBatch(null);
        p.setCount(0);
        return batch;
    }

    public void add(PendingQueryResult p, QueryResult q) {

    }

    /** afterQueryStateNext handles a QUERY_STATE_NEXT request from the chaincode.
     *
     * @param event
     * @param state
     */
    public void afterQueryStateNext(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("Received %s, invoking query state next from ledger", QUERY_STATE_NEXT.toString()));
        handleQueryStateNext(msg);
        logger.info("Exiiting QUERY_STATE_NEXT");
    }

    /** Handles query to ledger for query state next
     *
     * @param msg
     */
    public void handleQueryStateNext(SmartcontractShim.SmartContractMessage msg) {
        new Thread(() -> {
            //serialSendMsg is used to send error msg
            SmartcontractShim.SmartContractMessage serialSendMsg = null;
            //flag of if do "defer function"
            boolean flag = false;
            SmartcontractShim.QueryStateNext queryStateNext = null;
            TransactionContext txContext = null;
            ResultsIterator queryIter = null;
            SmartcontractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            try {
                //judge if put txId into query map is success
                boolean uniqueReq = createTXIDEntry(msg.getChannelId(), msg.getTxid());
                if (!uniqueReq) {
                    logger.error(String.format("[%s]Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid())));
                    return;
                }
                // after this, before function return txidEntry should be deleted and send serialSendMsg
                flag = true;
                //create query state next
                try{
                    queryStateNext = SmartcontractShim.QueryStateNext.parseFrom(msg.getPayload());
                } catch (InvalidProtocolBufferException e){
                    logger.error(String.format("[%s]Failed to creqte query state next request. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //creqte transaction context
                txContext = getTxContext(msg.getChannelId(), msg.getTxid());
                if(txContext == null){
                    logger.error(String.format("[%s]Failed to get transaction context. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    String errStr = String.format("[%s]Transcation context cannot found", shorttxid(msg.getTxid()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(errStr), null);
                    return;
                }
                //create ResultIterator query iter
                queryIter = getQueryIterator(txContext, queryStateNext.getId());
                if(queryIter == null){
                    logger.error(String.format("[%s]query iterator no found. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8("Query iterator cannot found"), null);
                    return;
                }
                //create QueryResponse payload
                try {
                    payload = getQueryResponse(this, txContext, queryIter, queryStateNext.getId());
                } catch (LedgerException e) {
                    cleanupQueryContext(txContext, queryStateNext.getId());
                    logger.error(String.format("Fail to get query result in HandlerQueryStateNext. Sending %s", ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create ByteString payloadBytes
                payloadBytes = payload.toByteString();
                logger.info(String.format("Got key and values. Sending %s", RESPONSE));
                serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), payloadBytes, null);
            } finally {
                if(flag){
                    //do followed functions before retun
                    //delete transaction context
                    deleteTXIDEntry(msg.getChannelId(), msg.getTxid());
                    logger.info(String.format("[%s]handlerQueryStateNext serial send %s", shorttxid(serialSendMsg.getTxid()), serialSendMsg.getType()));
                    //send msg
                    serialSendAsync(serialSendMsg);
                }
            }
        }).start();
    }

    /** afterQueryStateClose handles a QUERY_STATE_CLOSE request from the chaincode.
     *
     * @param event
     * @param state
     */
    public void afterQueryStateClose(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String.format("Received %s, invoking query state close from ledger", QUERY_STATE_CLOSE.toString()));

        handleQueryStateClose(msg);
        logger.info("Exiting QUERY_STATE_CLOSE");
    }

    /** Handles the closing of a state iterator
     *
     * @param msg
     */
    public void handleQueryStateClose(SmartcontractShim.SmartContractMessage msg) {
        new Thread(() -> {
            //serialSendMsg is used to send error msg
            SmartcontractShim.SmartContractMessage serialSendMsg = null;
            //flag of if do "defer function"
            boolean flag = false;
            SmartcontractShim.QueryStateClose queryStateClose = null;
            TransactionContext txContext = null;
            ResultsIterator iter = null;
            SmartcontractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            try {
                //judge if put txId into query map is success
                boolean uniqueReq = createTXIDEntry(msg.getChannelId(), msg.getTxid());
                if (!uniqueReq) {
                    logger.error(String.format("[%s]Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid())));
                    return;
                }
                // after this, before function return txidEntry should be deleted and send serialSendMsg
                flag = true;
                //create query state close
                try {
                    queryStateClose = SmartcontractShim.QueryStateClose.parseFrom(msg.getPayload());
                } catch (InvalidProtocolBufferException e) {
                    logger.error(String.format("[%s]Failed to get state query close request. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create transaction context
                txContext = getTxContext(msg.getChannelId(), msg.getTxid());
                if(txContext == null){
                    logger.error(String.format("[%s]Failed to get transaction context. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    String errStr = String.format("[%s]Transaction context not found", shorttxid(msg.getTxid()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(errStr), null);
                    return;
                }
                //create ResultsIterator iter
                iter = getQueryIterator(txContext, queryStateClose.getId());
                if(iter == null){
                    cleanupQueryContext(txContext, queryStateClose.getId());
                }
                //create QueryResponse payload
                payload = SmartcontractShim.QueryResponse.newBuilder()
                        .setHasMore(false)
                        .setId(queryStateClose.getId())
                        .build();
                //create ByteString payloadByte
                payloadBytes = payload.toByteString();
                logger.info(String.format("[%s]Closed. Sending %s", shorttxid(msg.getTxid()), RESPONSE.toString()));
                serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), payloadBytes, null);
            } finally {
                //do followed functions before retun
                //delete transaction context
                deleteTXIDEntry(msg.getChannelId(), msg.getTxid());
                logger.info(String.format("[%s]handlerQueryStateClose serial send %s", shorttxid(serialSendMsg.getTxid()), serialSendMsg.getType()));
                //send msg
                serialSendAsync(serialSendMsg);
            }
        }).start();
    }

    /** afterGetQueryResult handles a GET_QUERY_RESULT request from the chaincode.
     *
     * @param event
     * @param state
     */
    public void afterGetQueryResult(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String .format("Received %s, invoking get state from ledger", GET_QUERY_RESULT.toString()));

        handleGetQueryResult(msg);
        logger.info("Exiting GET_QUERY_RESULT");
    }

    /** Handles query to ledger to execute query state
     *
     * @param msg
     */
    public void handleGetQueryResult(SmartcontractShim.SmartContractMessage msg) {
        new Thread(() -> {
            //serialSendMsg is used to send error msg
            SmartcontractShim.SmartContractMessage serialSendMsg = null;
            //flag of if do "defer function"
            boolean flag = false;
            TransactionContext txContext = null;
            String iterID = null;
            SmartcontractShim.GetQueryResult getQueryResult = null;
            String chaincodeID = null;
            ResultsIterator executeIter = null;
            SmartcontractShim.QueryResponse payload = null;
            ByteString payloadBytes = null;
            try {
                //judge if put txId into query map is success
                boolean uniqueReq = createTXIDEntry(msg.getChannelId(), msg.getTxid());
                if (!uniqueReq) {
                    logger.error(String.format("[%s]Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid())));
                    return;
                }
                // after this, before function return txidEntry should be deleted and send serialSendMsg
                flag = true;
                //create get query result
                try {
                    getQueryResult = SmartcontractShim.GetQueryResult.parseFrom(msg.getPayload());
                } catch (InvalidProtocolBufferException e) {
                    logger.error(String.format("[%s]Failed to unmarshall query request. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(),  ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create iterID
                //iterID = util.GenerateUUID();
                //create transaction context
                txContext = getTxContext(msg.getChannelId(), msg.getTxid());
                if(txContext == null || txContext.getTxSimulator() == null){
                    String errStr = String.format("[%s]No ledger context for GetQueryResult. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                    logger.error(errStr);
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(errStr), null);
                    return;
                }
                //create chaincode id
                chaincodeID = getSmartContractRootName();
                //create ResultesIterator executerIter
                try {
                    if(isCollectionSet(getQueryResult.getCollection())){
    //                    executeIter, err = txContext.txsimulator.ExecuteQueryOnPrivateData(chaincodeID, getQueryResult.Collection, getQueryResult.Query)
                    } else {
    //                    executeIter, err = txContext.txsimulator.ExecuteQuery(chaincodeID, getQueryResult.Query)
                    }
                } catch (Exception e) {
                    logger.error(String.format("[%s]Failed to get ledger query iterator. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                initializeQueryContext(txContext, iterID, executeIter);
                //create QueryResponse payload
                try {
                    payload = getQueryResponse(this, txContext, executeIter, iterID);
                } catch (LedgerException e) {
                    if(executeIter != null){
                        cleanupQueryContext(txContext, iterID);
                    }
                    logger.error(String.format("[%s]Failed to get query result in HandlerGetQueryResult. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create ByteString payloadBytes
                payloadBytes = payload.toByteString();
                logger.info(String.format("[%s]Got keys and values. Send %s", shorttxid(msg.getTxid()), RESPONSE.toString()));
                serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), payloadBytes, null);
            } finally {
                if(flag){
                    //do followed functions before retun
                    //delete transaction context
                    deleteTXIDEntry(msg.getChannelId(), msg.getTxid());
                    logger.info(String.format("[%s]handlerGetQueryResult serial send %s", shorttxid(serialSendMsg.getTxid()), serialSendMsg.getType()));
                    //send msg
                    serialSendAsync(serialSendMsg);
                }
            }
        }).start();
    }

    /** afterGetHistoryForKey handles a GET_HISTORY_FOR_KEY request from the chaincode.
     *
     * @param event
     * @param state
     */
    public void afterGetHistoryForKey(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        logger.info(String .format("Received %s, invoking get state from ledger", GET_HISTORY_FOR_KEY.toString()));

        handleGetQueryResult(msg);
        logger.info("Exiting GET_HISTORY_FOR_KEY");
    }

    /** Handles query to ledger history db
     *
     * @param msg
     */
    public void handleGetHistoryForKey(SmartcontractShim.SmartContractMessage msg) {
        new Thread(() -> {
            //serialSendMsg is used to send error msg
            SmartcontractShim.SmartContractMessage serialSendMsg = null;
            //flag of if do "defer function"
            boolean flag = false;
            TransactionContext txContext = null;
            SmartcontractShim.GetHistoryForKey getHistoryForKey = null;
            String iterID = null;
            String chaincodeID = null;
            ResultsIterator historyIterator = null;
            SmartcontractShim.QueryResponse payload = null;
            ByteString payloadByte = null;
            try {
                //judge if put txId into query map is success
                boolean uniqueReq = createTXIDEntry(msg.getChannelId(), msg.getTxid());
                if (!uniqueReq) {
                    logger.error(String.format("[%s]Anoter state request pending for this Txid. Cannot process.", shorttxid(msg.getTxid())));
                    return;
                }
                // after this, before function return txidEntry should be deleted and send serialSendMsg
                flag = true;
                //create get history for key
                try {
                    getHistoryForKey = SmartcontractShim.GetHistoryForKey.parseFrom(msg.getPayload());
                } catch (InvalidProtocolBufferException e) {
                    logger.error(String.format("[%s]Failed to create query result. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create iterID
                //iterID = util.GenerateUUID();
                //create transaction context
                txContext = getTxContext(msg.getChannelId(), msg.getTxid());
                if(txContext == null || txContext.getTxSimulator() == null){
                    String errStr = String.format("[%s]No ledger context for GetHistoryForKey. Sending %s", shorttxid(msg.getTxid()), ERROR.toString());
                    logger.error(errStr);
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(errStr), null);
                    return;
                }
                //create chaincodeID
                chaincodeID = getSmartContractRootName();
                //create ResultsIterator history iterator
                try {
                    historyIterator = txContext.getHistoryQueryExecutor().getHistoryForKey(chaincodeID, getHistoryForKey.getKey());
                } catch (LedgerException e) {
                    logger.info(String.format("[%s]Failed to get ledger history iterator. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                initializeQueryContext(txContext, iterID, historyIterator);
                //create QueryResponse payload
                try {
                    payload = getQueryResponse(this, txContext, historyIterator, iterID);
                } catch (LedgerException e) {
                    if(historyIterator != null){
                        cleanupQueryContext(txContext, iterID);
                    }
                    logger.error(String.format("[%s]Failed to get query result in HandleGetHistoryForKey. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                    serialSendMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                    return;
                }
                //create ByteString payloadByte
                payloadByte = payload.toByteString();
                logger.info(String.format("[%s]Got keys and values. Sending %s", shorttxid(msg.getTxid()), RESPONSE.toString()));
                serialSendMsg = newEventMessage(RESPONSE, msg.getChannelId(), msg.getTxid(), payloadByte, null);
            } finally {
                if(flag){
                    //do followed functions before retun
                    //delete transaction context
                    deleteTXIDEntry(msg.getChannelId(), msg.getTxid());
                    logger.info(String.format("[%s]handlerGetHistoryForKey serial send %s", shorttxid(serialSendMsg.getTxid()), serialSendMsg.getType()));
                    //send msg
                    serialSendAsync(serialSendMsg);
                }
            }
        }).start();
    }

    public static Boolean isCollectionSet(String collection) {
        return StringUtils.isEmpty(collection);
    }

    public SmartcontractShim.SmartContractMessage getSmartContractMessageForMessage(String channelID, String txid, String msgType, ByteString payload, String errStr) {
        TransactionContext txContext = getTxContext(channelID, txid);
        //if we do not have channelID or INVOKE_CHAINCODE
        if(!"".equals(channelID) || !INVOKE_CHAINCODE.toString().equals(msgType)){
            if (txContext == null || txContext.getTxSimulator() == null){
                logger.error(String.format(errStr));
                return newEventMessage(ERROR, channelID, txid, ByteString.copyFromUtf8(errStr), null);
            }
            return null;
        }

        //any other msgType except INVOKE_CHAINCODE has handled
        //now handle the situation we do have channelID
        SmartContractInstance calledCcIns = null;
        Smartcontract.SmartContractSpec chainCodeSpec = null;

        try {
            chainCodeSpec = Smartcontract.SmartContractSpec.parseFrom(payload);
        } catch (InvalidProtocolBufferException e) {
            errStr = String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(txid), ERROR.toString());
            logger.error(errStr);
            return newEventMessage(ERROR, null, txid, ByteString.copyFromUtf8(errStr), null);
        }

        //Got the chaincodeID to invoke. The chaincodeID to be called may
        //contain composite info like "chaincode-name:version/channel-name"
        //now version is not used
        calledCcIns = getSmartContractInstance(chainCodeSpec.getSmartContractId().getName());
        if(calledCcIns == null){
            errStr = String.format("[%s]Could not get chaincode name for INVOKE_CHAINCODE. Sending %s", shorttxid(txid), ERROR.toString());
            logger.error(errStr);
            return newEventMessage(ERROR, null, txid, ByteString.copyFromUtf8(errStr), null);
        }

        boolean isScc = true;
//      boolean isScc = smartContractSupport.getSystemChaincodeProvider().isSysCC(calledCcIns.ChaincodeName);
        if(!isScc) {
            txContext = getTxContext("", txid);
            if (txContext == null || txContext.getTxSimulator() == null){
                logger.error(String.format(errStr));
                return newEventMessage(ERROR, "", txid, ByteString.copyFromUtf8(errStr), null);
            }
            return null;
        }
        return null;
    }

    /** Handles request to ledger to put state
     *
     * @param event
     * @param state
     */
    public void enterBusyState(Event event, String state) {
        new Thread(() -> {
            SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
            logger.info(String.format("[%s]state i %s", shorttxid(msg.getTxid()), state));

            SmartcontractShim.SmartContractMessage triggerNextStateMsg = null;
            TransactionContext txContext = null;
            boolean flag = false;
            String chaincodeID = null;

            try {
                //judge if put txId into query map is success
                boolean uniqueReq = createTXIDEntry(msg.getChannelId(), msg.getTxid());
                if (!uniqueReq) {
                    logger.error(String.format("[%s]Anoter state request pending for this CC: %s, Txid: %s. Cannot process."
                            , shorttxid(msg.getTxid()), smartContractID.getName(), msg.getTxid()));
                    return;
                }
                //check to get triggerNextStateMsg
                triggerNextStateMsg = getSmartContractMessageForMessage(msg.getChannelId(), msg.getTxid(),msg.getType().toString(), msg.getPayload()
                        , String.format("[%s]No ledger context for %s. Sending %s", shorttxid(msg.getTxid()), msg.getType().toString(), ERROR.toString()));
                //if triggerNextStateMsg is null means txContext is vaild
                if(triggerNextStateMsg == null){
                    txContext = getTxContext(msg.getChannelId(), msg.getTxid());
                }
                // after this, before function return txidEntry should be deleted and send serialSendMsg
                flag = true;
                //check transaction context txContext
                if(txContext == null){
                    return;
                }
                //create chaincodeID
                chaincodeID = getSmartContractRootName();
                if(PUT_STATE.equals(msg.getType())){
                    //handle PUT_STATE
                    try {
                        SmartcontractShim.PutState putState = SmartcontractShim.PutState.parseFrom(msg.getPayload());

                        if(isCollectionSet(putState.getCollection())){
//                        txContext.getTxSimulator().setPrivateDate(chaincodeID, putState.getCollection(), putState.getKey(), putState.getValue());
                        } else {
                            txContext.getTxSimulator().setState(chaincodeID, putState.getKey(), putState.getValue().toByteArray());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error(String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                        triggerNextStateMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                        return;
                    } catch (LedgerException e){
                        logger.error(String.format("[%s]Unable to set state. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                        triggerNextStateMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                        return;
                    }
                } else if(DEL_STATE.equals(msg.getType())){
                    //handle DEL_STATE
                    try {
                        SmartcontractShim.DelState delState = SmartcontractShim.DelState.parseFrom(msg.getPayload());

                        if(isCollectionSet(delState.getCollection())){
//                        txContext.getTxSimulator().deletePrivateDate(chaincodeID, putState.getCollection(), putState.getKey(), putState.getValue());
                        } else {
                            txContext.getTxSimulator().deleteState(chaincodeID, delState.getKey());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error(String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                        triggerNextStateMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                        return;
                    } catch (LedgerException e){
                        logger.error(String.format("[%s]Unable to delete state. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                        triggerNextStateMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                        return;
                    }
                } else if (INVOKE_CHAINCODE.equals(msg.getType())){
                    //handle INVOKE_CHAINCODE
                    Smartcontract.SmartContractSpec chaincodeSpec = null;
                    SmartContractInstance calledCcIns = null;
                    Smartcontract.SmartContractID scID = null;
                    try {
                        logger.info(String.format("[%s] C-call-C", shorttxid(msg.getTxid())));
                        chaincodeSpec = Smartcontract.SmartContractSpec.parseFrom(msg.getPayload());
                    } catch (InvalidProtocolBufferException e) {
                        logger.error(String.format("[%s]Unable to decipher payload. Sending %s", shorttxid(msg.getTxid()), ERROR.toString()));
                        triggerNextStateMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                        return;
                    }
                    // Get the chaincodeID to invoke. The chaincodeID to be called may
                    // contain composite info like "chaincode-name:version/channel-name"
                    // We are not using version now but default to the latest
                    calledCcIns = getSmartContractInstance(chaincodeSpec.getSmartContractId().getName());
                    scID = chaincodeSpec.getSmartContractId().toBuilder()
                            .setName(calledCcIns.getSmartContractName())
                            .build();
                    chaincodeSpec = chaincodeSpec.toBuilder()
                            .setSmartContractId(scID)
                            .build();
                    if("".equals(calledCcIns.getSmartContractID())){
                        calledCcIns.setSmartContractID(txContext.getChainID());
                    }
                    logger.info(String.format("[%s] C-call-C %s on channel %s"
                            , shorttxid(msg.getTxid()), calledCcIns.getSmartContractName(), calledCcIns.getSmartContractID()));
                    try{
                        //unrealized function, throws RuntionException
                        checkACL(triggerNextStateMsg.getProposal(), txContext.getProposal(), calledCcIns);
                    } catch (RuntimeException e){
                        logger.error(String.format("[%s] C-call-C %s on channel %s failed check ACL [%s]. Sending %s"
                                , shorttxid(msg.getTxid()), calledCcIns.getSmartContractName(), calledCcIns.getSmartContractID(), txContext.getSignedProp(), printStackTrace(e)));
                        triggerNextStateMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), ByteString.copyFromUtf8(printStackTrace(e)), null);
                        return;
                    }

                    // Set up a new context for the called chaincode if on a different channel
                    // We grab the called channel's ledger simulator to hold the new state
                    TransactionContext ctxt = new TransactionContext();
                    ITxSimulator txsim = txContext.getTxSimulator();
                    IHistoryQueryExecutor historyQueryExecutor = txContext.getHistoryQueryExecutor();

                    if(calledCcIns.getSmartContractID() != txContext.getChainID()){
//                        lgr := peer.GetLedger(calledCcIns.ChainID)
                        NodeConfig.Ledger lgr = new NodeConfig.Ledger();
                        if(lgr == null){
                            ByteString payload = ByteString.copyFromUtf8("Failed to find ledger for called channel " + calledCcIns.getSmartContractID());
                            triggerNextStateMsg = newEventMessage(ERROR, msg.getChannelId(), msg.getTxid(), payload, null);
                            return;
                        }

                        ITxSimulator txsim2 = null;
//                        txsim2 = lgr.newTxSimulator(msg.getTxid());
//                        if err2 != nil {
//                            triggerNextStateMsg = &pb.ChaincodeMessage{Type: pb.ChaincodeMessage_ERROR,
//                                    Payload: []byte(err2.Error()), Txid: msg.Txid, ChannelId: msg.ChannelId}
//                            return
//                        }

                        txsim = txsim2;
                    }
//                    ctxt = context.WithValue(ctxt, TXSimulatorKey, txsim)
//                    ctxt = context.WithValue(ctxt, HistoryQueryExecutorKey, historyQueryExecutor)
                    logger.info("[%s] getting chaincode data for %s on channel %s",
                            shorttxid(msg.getTxid()), calledCcIns.getSmartContractName(), calledCcIns.getSmartContractID());

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
//                triggerNextStateMsg = &pb.ChaincodeMessage{Type: pb.ChaincodeMessage_RESPONSE, Payload: res, Txid: msg.Txid, ChannelId: msg.ChannelId}
            } finally {
                if(flag){
                    //do followed functions before retun
                    //delete transaction context
                    deleteTXIDEntry(msg.getChannelId(), msg.getTxid());
                    logger.info(String.format("[%s]enterBusyState trigger event %s", shorttxid(triggerNextStateMsg.getTxid()), triggerNextStateMsg.getType()));
                    //trigger next state
                    triggerNextState(triggerNextStateMsg, true);
                }
            }
        }).start();
    }

    public void enterEstablishedState(Event e, String state) {
        notifyDuringStartup(true);
    }

    public void enterReadyState(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
        notify(msg);
        logger.info(String.format("[%s]Entered state %s", shorttxid(msg.getTxid()), state));
    }

    public void enterEndState(Event event, String state) {
        SmartcontractShim.SmartContractMessage msg = extractMessageFromEvent(event);
            logger.info(String.format("[%s]Entered state %s"), shorttxid(msg.getTxid()), state);
            notify(msg);
            deregister();
    }

    public SmartcontractShim.SmartContractMessage setChaincodeProposal(ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop, SmartcontractShim.SmartContractMessage msg) {
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
     *
     * @param ctxt
     * @param chainID
     * @param txid
     * @param signedProp
     * @param prop
     * @return
     */
    public SmartcontractShim.SmartContractMessage ready(Context ctxt, String chainID, String txid, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
        TransactionContext txctx = createTxContext(ctxt, chainID, txid, signedProp, prop);

        logger.info("sending READY");
        SmartcontractShim.SmartContractMessage msg = newEventMessage(READY, chainID, txid, null, null);

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
     *
     * @param msg
     */
    public void handleMessage(SmartcontractShim.SmartContractMessage msg) {
        logger.info(String.format("[%s]Handling message of type: %s in state %s", shorttxid(msg.getTxid()), msg.getType(), fsm.current()));

        if((COMPLETED.equals(msg.getType()) || ERROR.equals(msg.getType()) && "ready".equals(fsm.current()))){
            logger.info("[%s]Handle message - COMPLETED. Notify", msg.getTxid());
            notify(msg);
        }
        if(fsm.eventCannotOccur(msg.getType().toString())){
            logger.error(String.format("[%s]Chaincode handler validator FSM cannot handle message (%s) while in state: %s"
                    , msg.getTxid(), msg.getType(), fsm.current()));
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

    public SmartcontractShim.SmartContractMessage sendExecuteMessage(Context ctxt, String chainID, SmartcontractShim.SmartContractMessage msg, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
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
     * @param event
     * @return
     */
    private SmartcontractShim.SmartContractMessage extractMessageFromEvent(Event event) {
        try {
            return (SmartcontractShim.SmartContractMessage) event.args[0];
        } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
            final RuntimeException error = new RuntimeException("No chaincode message found in event", e);
            event.cancel(error);
            throw error;
        }
    }

    /**
     * create new event msg
     * @param type
     * @param channelId
     * @param txId
     * @param payload
     * @param event
     * @return
     */
    private static SmartcontractShim.SmartContractMessage newEventMessage(final SmartcontractShim.SmartContractMessage.Type type
            , final  String channelId, final String txId, final ByteString payload, final SmartContractEventPackage.SmartContractEvent event){
        if (event == null){
            return SmartcontractShim.SmartContractMessage.newBuilder()
                    .setType(type)
                    .setChannelId(channelId)
                    .setTxid(txId)
                    .setPayload(payload)
                    .build();
        } else {
            return SmartcontractShim.SmartContractMessage.newBuilder()
                    .setType(type)
                    .setChannelId(channelId)
                    .setTxid(txId)
                    .setPayload(payload)
                    .setSmartcontractEvent(event)
                    .build();
        }
    }

    /**
     * throwable to string
     * @param throwable
     * @return
     */
    private static String printStackTrace(Throwable throwable) {
        if (throwable == null) return null;
        final StringWriter buffer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(buffer));
        return buffer.toString();
    }

    /**
     * set QueryResponse's results
     * @param builder
     * @param batch
     * @return
     */
    private static SmartcontractShim.QueryResponse.Builder setQueryResponseReuslt(SmartcontractShim.QueryResponse.Builder builder, SmartcontractShim.QueryResultBytes[] batch){
        for (int i = 0; i < builder.getResultsCount(); i++) {
            builder.addResults(batch[i]);
        }
        return builder;
    }
}


