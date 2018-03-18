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

import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.QueryResult;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.javachain.core.container.scintf.ISmartContractStream;
import org.bcia.javachain.core.smartcontract.shim.fsm.Event;
import org.bcia.javachain.core.smartcontract.shim.fsm.FSM;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.SmartcontractShim;

import javax.naming.Context;
import java.util.Map;

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

    private ISmartContractStream chatStream;
    private FSM fsm;
    private Smartcontract.SmartContractID smartContractID;
    private SmartContractInstance smartContractInstance;
    private SmartContractSupport smartContractSupport;
    private Boolean registered;
    private Boolean readyNotify;
    private Map<String, TransactionContext> txCtxs;
    private Map<String, Boolean> txidMap;
    private NextStateInfo nextState;

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

    public NextStateInfo getNextState() {
        return nextState;
    }

    public void setNextState(NextStateInfo nextState) {
        this.nextState = nextState;
    }

    public static String shorttxid(String txid) {
        if(txid.length() < 8) {
            return txid;
        }
        return txid.substring(0, 8);
    }

    public static SmartContractInstance getSmartContractInstance(String smartContractName) {
        SmartContractInstance instance = new SmartContractInstance();
        instance.setSmartContractID("");
        instance.setSmartContractName("");
        instance.setSmartContractVersion("");
        return instance;
    }

    public void decomposeRegisteredName(Smartcontract.SmartContractID smartContractID) {
        this.setSmartContractInstance(getSmartContractInstance(smartContractID.getName()));
    }

    public String getSmartContractRootName() {
        return getSmartContractInstance().getSmartContractName();
    }

    public void serialSend(SmartcontractShim.SmartContractMessage msg) {
        getChatStream().send(msg);
    }

    public void serialSendAsync(SmartcontractShim.SmartContractMessage msg) {
        this.serialSend(msg);
    }

    public String getTxCtxId(String chainID, String txid) {
        return chainID + txid;
    }

    public TransactionContext createTxContext(Context ctxt, String chainID, String txid, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
        String txCtxID = getTxCtxId(chainID, txid);

        TransactionContext txctx = new TransactionContext();
        txctx.setChainID(chainID);
        txctx.setSignedProp(signedProp);
        txctx.setProposal(prop);

        return txctx;
    }

    public TransactionContext getTxContext(String chainID, String txid) {
        String txCtxID = getTxCtxId(chainID, txid);
        return txCtxs.get(txCtxID);
    }

    public void deleteTxContext(String chainID, String txid) {
        String txCtxID = getTxCtxId(chainID, txid);
        txCtxs.remove(txCtxID);
    }

    public void initializeQueryContext(TransactionContext txContext, String queryID,
                                       ResultsIterator queryIterator) {
        txContext.getQueryIteratorMap().put(queryID, queryIterator);
    }

    public ResultsIterator getQueryIterator(TransactionContext txContext, String queryID) {
        return txContext.getQueryIteratorMap().get(queryID);
    }

    public void cleanupQueryContext(TransactionContext txContext, String queryID) throws LedgerException{
        txContext.getQueryIteratorMap().get(queryID).close();
        txContext.getQueryIteratorMap().remove(queryID);
        txContext.getPendingQueryResults().remove(queryID);
    }

    // Check if the transactor is allow to call this chaincode on this channel
    public void checkACL(ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal proposal, SmartContractInstance ccIns) {
        return;
    }

    public void deregister() {
        return;
    }

    public void triggerNextState(SmartcontractShim.SmartContractMessage msg, Boolean send) {
        return;
    }

    public void triggerNextStateSync(SmartcontractShim.SmartContractMessage msg) {
        return;
    }

    public void waitForKeepaliveTimer() {
        return;
    }

    public void processStream() {
        return;
    }

    /** HandleChaincodeStream Main loop for handling the associated Chaincode stream
     *
     * @param chaincodeSupport
     * @param ctxt
     * @param stream
     */
    public static void handleChaincodeStream(SmartContractSupport chaincodeSupport, Context ctxt, ISmartContractStream stream) {
        return;
    }

    public static Handler newChaincodeSupportHandler(SmartContractSupport chaincodeSupport, ISmartContractStream peerChatStream) {
        Handler handler = new Handler();
        handler.setChatStream(peerChatStream);
        handler.setSmartContractSupport(chaincodeSupport);
        FSM fsm = new FSM("created");
        handler.setFsm(fsm);
        return handler;
    }

    public Boolean createTXIDEntry(String channelID, String txid) {
        return null;
    }

    public void deleteTXIDEntry(String channelID, String txid) {
        return;
    }

    public void notifyDuringStartup(Boolean val) {
        return;
    }

    /** beforeRegisterEvent is invoked when chaincode tries to register.
     *
     * @param e
     * @param state
     */
    public void beforeRegisterEvent(Event e, String state) {
        return;
    }

    public void notify(Smartcontract msg) {
        return;
    }

    /** beforeCompletedEvent is invoked when chaincode has completed execution of init, invoke.
     *
     * @param e
     * @param state
     */
    public static void beforeCompletedEvent(Event e, String state) {
        return;
    }

    /** afterGetState handles a GET_STATE request from the chaincode.
     *
     * @param e
     * @param state
     */
    public void afterGetState(Event e, String state) {
        return;
    }

    /** is this a txid for which there is a valid txsim
     *
     * @param channelID
     * @param txid
     * @param fmtStr
     * @return
     */
    public SmartcontractShim.SmartContractMessage isValidTxSim(String channelID, String txid, String fmtStr) {
        return null;
    }

    /** Handles query to ledger to get state
     *
     * @param msg
     */
    public void handleGetState(SmartcontractShim.SmartContractMessage msg) {
        return;
    }

    /** afterGetStateByRange handles a GET_STATE_BY_RANGE request from the chaincode.
     *
     * @param e
     * @param state
     */
    public void afterGetStateByRange(Event e, String state) {
        return;
    }

    /** Handles query to ledger to rage query state
     *
     * @param msg
     */
    public void handleGetStateByRange(SmartcontractShim.SmartContractMessage msg) {
        return;
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
    public static SmartcontractShim.QueryResponse getQueryResponse(Handler handler, TransactionContext txContext, ResultsIterator iter,
                                                                   String iterID){
        return null;
    }

    public SmartcontractShim.QueryResultBytes cut() {
        return null;
    }

    public void add(QueryResult queryResult) {
        return;
    }

    /** afterQueryStateNext handles a QUERY_STATE_NEXT request from the chaincode.
     *
     * @param e
     * @param state
     */
    public void afterQueryStateNext(Event e, String state) {
        return;
    }

    /** Handles query to ledger for query state next
     *
     * @param msg
     */
    public void handleQueryStateNext(Smartcontract msg) {
        return;
    }

    /** afterQueryStateClose handles a QUERY_STATE_CLOSE request from the chaincode.
     *
     * @param e
     * @param state
     */
    public void afterQueryStateClose(Event e, String state) {
        return;
    }

    /** Handles the closing of a state iterator
     *
     * @param msg
     */
    public void handleQueryStateClose(Smartcontract msg) {
        return;
    }

    /** afterGetQueryResult handles a GET_QUERY_RESULT request from the chaincode.
     *
     * @param e
     * @param state
     */
    public void afterGetQueryResult(Event e, String state) {
        return;
    }

    /** Handles query to ledger to execute query state
     *
     * @param msg
     */
    public void handleGetQueryResult(SmartcontractShim.SmartContractMessage msg) {
        return;
    }

    /** afterGetHistoryForKey handles a GET_HISTORY_FOR_KEY request from the chaincode.
     *
     * @param e
     * @param state
     */
    public void afterGetHistoryForKey(Event e, String state) {
        return;
    }

    /** Handles query to ledger history db
     *
     * @param msg
     */
    public void handleGetHistoryForKey(SmartcontractShim.SmartContractMessage msg) {
        return;
    }

    public static Boolean isCollectionSet(String collection) {
        return StringUtils.isEmpty(collection);
    }

    public TransactionContext getTxContextForMessage(String channelID, String txid, String msgType, byte[] payload, String fmtStr) {
        return null;
    }

    /** Handles request to ledger to put state
     *
     * @param e
     * @param state
     */
    public void enterBusyState(Event e, String state) {
        return;
    }

    public void enterEstablishedState(Event e, String state) {
        return;
    }

    public void enterReadyState(Event e, String state) {
        return;
    }

    public void enterEndState(Event e, String state) {
        return;
    }

    public void setChaincodeProposal(ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop, SmartcontractShim.SmartContractMessage msg) {
        return;
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
        return null;
    }

    /** handleMessage is the entrance method for Peer's handling of Chaincode messages.
     *
     * @param msg
     */
    public void handleMessage(SmartcontractShim.SmartContractMessage msg) {
        return;
    }

    public SmartcontractShim.SmartContractMessage sendExecuteMessage(Context ctxt, String chainID, SmartcontractShim.SmartContractMessage msg, ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal prop) {
        return null;
    }

    public Boolean isRunning() {
        String current = getFsm().current();
        return !StringUtils.equals(current, CREATED_STATE) && !StringUtils.equals(current, ESTABLISHED_STATE);
    }}
