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
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.javachain.core.container.scintf.ISmartContractStream;
import org.bcia.javachain.core.endorser.MockTxSimulator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IQueryResult;
import org.bcia.javachain.core.smartcontract.shim.fsm.Event;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.SmartcontractShim;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.HashMap;

import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage.Type.*;
import static org.mockito.Mockito.when;

/**
 * Peer侧链码处理器单元测试
 *
 * @author sunzongyu
 * @date 2018/03/23
 * @company Dingxuan
 */
public class HandlerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    public Handler handler = null;

    @Spy
    TransactionContext txContext;

    @Before
    public void before(){
        handler = Handler.newSmartContractSupportHandler(null, null);
    }

    @Test
    public void shorttxid() {
        String value = Handler.shorttxid("123456789");
        String test = "12345678";

        Assert.assertEquals(value, test);
    }

    @Test
    public void decomposeRegisteredName(){
        String query = "chaincode-name";
        String value = handler.getSmartContractInstance("chaincode-name:version/channel-name").getSmartContractName();
        Assert.assertEquals(value, query);
    }

    @Test
    public void getSmartContractInstance(){
        SmartContractInstance smartContractInstance = Handler.getSmartContractInstance("chaincode-name:version/channel-name");
        String value = smartContractInstance.getSmartContractName();
        String test = "chaincode-name";
        Assert.assertEquals(value, test);
    }

    @Test
    public void getSmartContractRootName(){
        String expected = "chaincode";
        SmartContractInstance ins = new SmartContractInstance();
        ins.setSmartContractName("chaincode");
        when(handler.getSmartContractInstance()).thenReturn(ins);
        String actual = handler.getSmartContractRootName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void serialSend(){

    }

    @Test
    public void serialSendAsync(){

    }

    @Test
    public void getTxCtxId(){
        String actual = handler.getTxCtxId("chainID", "txid");
        String expected = "chainIDtxid";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void createTxContext(){
        TransactionContext value = handler.createTxContext(null, null, null, null, null);
        Assert.assertNotNull(value);
    }

    @Test
    public void getTxContext(){
        TransactionContext excepted = new TransactionContext();
        handler.getTxCtxs().put("chainIDtxid", excepted);
        TransactionContext actual = handler.getTxContext("chainID", "txid");
        Assert.assertEquals(excepted, actual);
    }

    @Test
    public void deleteTxContext(){
        TransactionContext test = new TransactionContext();
        handler.getTxCtxs().put("chainIDtxid", test);
        Assert.assertEquals(handler.getTxCtxs().size(), 1);
        handler.deleteTxContext("chainID", "txid");
        Assert.assertEquals(handler.getTxCtxs().size(), 0);
    }

    @Test
    public void initializeQueryContext(){
        TransactionContext txContext = new TransactionContext();
        txContext.setQueryIteratorMap(new HashMap<>());
        String queryID = "queryID";
        IResultsIterator queryIterator = new IResultsIterator() {
            @Override
            public IQueryResult next() throws LedgerException {
                return null;
            }

            @Override
            public void close() throws LedgerException {

            }
        };
        handler.initializeQueryContext(txContext, queryID, queryIterator);
        int expected = 1;
        int actual = txContext.getQueryIteratorMap().size();
        Assert.assertSame(expected, actual);
    }

    @Test
    public void getQueryIteator(){
        TransactionContext txContext = new TransactionContext();
        txContext.setQueryIteratorMap(new HashMap<>());
        String queryID = "queryID";
        IResultsIterator queryIterator = new IResultsIterator() {
            @Override
            public IQueryResult next() throws LedgerException {
                return null;
            }

            @Override
            public void close() throws LedgerException {

            }
        };
        handler.initializeQueryContext(txContext, queryID, queryIterator);
        IResultsIterator expected = handler.getQueryIterator(txContext, queryID);
        Assert.assertNotNull(expected);
    }

    @Test
    public void cleanupQueryContext(){
        TransactionContext txContext = new TransactionContext();
        txContext.setQueryIteratorMap(new HashMap<>());
        txContext.setPendingQueryResults(new HashMap<>());
        txContext.getQueryIteratorMap().put("123", new IResultsIterator() {
            @Override
            public IQueryResult next() throws LedgerException {
                return null;
            }

            @Override
            public void close() throws LedgerException {

            }
        });
        txContext.getPendingQueryResults().put("123", null);
        Assert.assertSame(1, txContext.getQueryIteratorMap().size());
        Assert.assertSame(1, txContext.getPendingQueryResults().size());
        handler.cleanupQueryContext(txContext, "123");
        Assert.assertSame(0, txContext.getQueryIteratorMap().size());
        Assert.assertSame(0, txContext.getPendingQueryResults().size());
    }

    @Test
    public void chenkACL(){

    }

    @Test
    public void triggerNextState() throws InterruptedException {
        Channel<NextStateInfo> channel = handler.getNextState();
        handler.triggerNextState(null, false);
        NextStateInfo value = channel.take();
        Assert.assertFalse(value.getSendToCC());
        Assert.assertFalse(value.getSendSync());
    }

    @Test
    public void triggerNextStateSync() throws InterruptedException{
        Channel<NextStateInfo> channel = handler.getNextState();
        handler.triggerNextStateSync(null);
        NextStateInfo value = channel.take();
        Assert.assertTrue(value.getSendToCC());
        Assert.assertTrue(value.getSendSync());
    }

    @Test
    public void procssStream() throws Exception {
        handler.getTxCtxs().put("GroupIdTxId", new TransactionContext());
        handler.setChatStream(new ISmartContractStream() {
            int i = 0;
            @Override
            public void send(SmartcontractShim.SmartContractMessage msg) {

            }

            @Override
            public SmartcontractShim.SmartContractMessage recv(){
                SmartcontractShim.SmartContractMessage in = null;
                if (i == 0) {
                    in = SmartcontractShim.SmartContractMessage.newBuilder()
                            .setGroupId("GroupId")
                            .setTxid("TxId")
                            .setPayload(Smartcontract.SmartContractID.newBuilder().setName("root").build().toByteString())
                            .setType(GET_STATE)
                            .build();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                i++;
                return in;
            }
        });
        handler.processStream();
//        Assert.assertEquals("established", handler.getFsm().current());
    }

    @Test
    public void handleChaincodeStream(){

    }

    @Test
    public void createTXIDEntry(){
        Assert.assertTrue(handler.createTXIDEntry("groupID", "txid"));
    }

    @Test
    public void deleteTXIDEntry(){
        String groupID = "groupID";
        String txid = "txid";
        handler.createTXIDEntry(groupID, txid);
        Assert.assertSame(1, handler.getTxidMap().size());
        handler.deleteTXIDEntry(groupID, txid);
        Assert.assertSame(0, handler.getTxidMap().size());
    }

    @Test
    public void notifyDuringStartup() throws InterruptedException{
        handler.notifyDuringStartup(Boolean.FALSE);
        handler.notifyDuringStartup(Boolean.TRUE);
        SmartcontractShim.SmartContractMessage.Type expected = READY;
        SmartcontractShim.SmartContractMessage.Type actual = handler.getNextState().take().getMsg().getType();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void befortRegisterEvent(){
        handler.setChatStream(new ISmartContractStream() {
            @Override
            public void send(SmartcontractShim.SmartContractMessage msg) {
                System.out.println("send");
            }

            @Override
            public SmartcontractShim.SmartContractMessage recv() {
                return null;
            }
        });
        Smartcontract.SmartContractID id = Smartcontract.SmartContractID.newBuilder().build();
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setPayload(id.toByteString())
                .build();
        Event event = new Event(handler.getFsm(), "test", "test", "test", null, false, false, msg);

        handler.beforeRegisterEvent(event, ERROR.toString());
        Assert.assertEquals(id, handler.getSmartContractID());
    }

    @Test
    public void notifyTest() {
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setGroupId("123")
                .setTxid("456")
                .setType(READY)
                .build();
        handler.notify(msg);
    }

    @Test
    public void beforeCompletedEvent(){
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setTxid("123")
                .build();
        Event event = new Event(handler.getFsm(), "test", "test", "test", null, false, false, msg);
        handler.beforeCompletedEvent(event, ERROR.toString());
    }

    @Test
    public void handleGetState(){
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setGroupId("GroupId")
                .setTxid("Txid")
                .build();
        Smartcontract.SmartContractID id = Smartcontract.SmartContractID.newBuilder()
                .setName("root")
                .build();
        TransactionContext txContext = new TransactionContext();
        txContext.setTxSimulator(new MockTxSimulator());
        handler.decomposeRegisteredName(id);
        Mockito.when(handler.getTxContext(msg.getGroupId(), msg.getTxid())).thenReturn(txContext);
        handler.handleGetState(msg);
    }

    private TransactionContext getTransactionContextBeforeQuery(){
        TransactionContext txContext = new TransactionContext();
        PendingQueryResult pendingQueryResult = new PendingQueryResult();
        String iterID = "iterID";
        txContext.setQueryIteratorMap(new HashMap<>());
        txContext.setPendingQueryResults(new HashMap<>());
        txContext.getPendingQueryResults().put(iterID, pendingQueryResult);
        IResultsIterator iter = new IResultsIterator() {
            @Override
            public IQueryResult next() throws LedgerException {
                return null;
            }

            @Override
            public void close() throws LedgerException {

            }
        };
        txContext.getQueryIteratorMap().put(iterID, iter);
        Smartcontract.SmartContractID id = Smartcontract.SmartContractID.newBuilder()
                .setName("root")
                .build();
        txContext.setTxSimulator(new MockTxSimulator());
        handler.getTxCtxs().put(handler.getTxCtxId("GroupId", "txid"), txContext);
        handler.decomposeRegisteredName(id);
        return txContext;
    }

    @Test
    public void getQueryResponse() throws LedgerException {
        String iterID = "iterID";
        IResultsIterator iter = new IResultsIterator() {
            @Override
            public IQueryResult next() throws LedgerException {
                return null;
            }

            @Override
            public void close() throws LedgerException {

            }
        };
        txContext = getTransactionContextBeforeQuery();
        SmartcontractShim.QueryResponse actual = handler.getQueryResponse(txContext, iter, iterID);
        String expected = "iterID";
        Assert.assertEquals(expected, actual.getId());
        Assert.assertFalse(actual.getHasMore());
    }

    @Test
    public void handleGetStateByRange(){
        txContext = getTransactionContextBeforeQuery();

        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setGroupId("GroupId")
                .setTxid("txid")
                .build();
        handler.handleGetStateByRange(msg);
    }

    @Test
    public void handleQueryStateNext(){
        txContext =getTransactionContextBeforeQuery();
        String groupId = "GroupId";
        String txid = "txid";

        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setTxid(txid)
                .setGroupId(groupId)
                .setPayload(SmartcontractShim.QueryStateNext.newBuilder().setId("iterID").build().toByteString())
                .build();
        handler.handleQueryStateNext(msg);
    }

    @Test
    public void handleQueryStateClose(){
        String groupId = "GroupId";
        String txid = "txid";
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setGroupId(groupId)
                .setTxid(txid)
                .setPayload(SmartcontractShim.QueryStateClose.newBuilder().setId("iterID").build().toByteString())
                .build();
        txContext = getTransactionContextBeforeQuery();
        handler.handleQueryStateClose(msg);
    }

    @Test
    public void handleGetQueryResult(){
        String groupId = "GroupId";
        String txid = "txid";
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setGroupId(groupId)
                .setTxid(txid)
                .setPayload(SmartcontractShim.GetQueryResult.newBuilder().build().toByteString())
                .build();
        txContext = getTransactionContextBeforeQuery();
        handler.handleGetQueryResult(msg);
    }

    @Test
    public void handleGetHistoryForKey(){
        String groupId = "GroupId";
        String txid = "txid";
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setGroupId(groupId)
                .setTxid(txid)
                .setPayload(SmartcontractShim.GetQueryResult.newBuilder().build().toByteString())
                .build();
        txContext = getTransactionContextBeforeQuery();
        handler.handleGetHistoryForKey(msg);
    }

    @Test
    public void getSmartContractMessageForMessage(){
        String msgType = INVOKE_SMARTCONTRACT.toString();
        String errStr = "errStr";
        Smartcontract.SmartContractID id = Smartcontract.SmartContractID.newBuilder()
                .setName("root1")
                .build();
        ByteString payload =  Smartcontract.SmartContractSpec.newBuilder().setSmartContractId(id)
                .build().toByteString();
        txContext = getTransactionContextBeforeQuery();
        handler.getTxContractForMessage("", "txid", msgType, payload, errStr);
    }

    @Test
    public void enterBusyState(){
        Object[] args = new Object[1];
        args[0] = SmartcontractShim.SmartContractMessage.newBuilder()
                .setGroupId("GroupId")
                .setTxid("Txid")
                .setType(INVOKE_SMARTCONTRACT)
                .build();
        Smartcontract.SmartContractID id = Smartcontract.SmartContractID.newBuilder()
                .setName("root")
                .build();
        Event event = new Event(handler.getFsm(), "event", "", "", null, false, true, args);
        handler.getTxCtxs().put(handler.getTxCtxId("GroupId", "Txid"), txContext);
        txContext.setQueryIteratorMap(new HashMap<>());
        txContext.setPendingQueryResults(new HashMap<>());
        txContext.setTxSimulator(new MockTxSimulator());
        handler.decomposeRegisteredName(id);
        handler.enterBusyState(event, PUT_STATE.toString());
    }

    @Test
    public void ready(){
        handler.ready(null, "GroupId", "TxId", null, null);
    }


    @Test
    public void handleMessate(){
        SmartcontractShim.SmartContractMessage msg = SmartcontractShim.SmartContractMessage.newBuilder()
                .setType(GET_STATE)
                .build();
        handler.ready(null, "GroupId", "TxId", null, null);
        handler.handleMessage(msg);
    }
}
