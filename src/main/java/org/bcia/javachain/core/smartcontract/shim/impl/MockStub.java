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
package org.bcia.javachain.core.smartcontract.shim.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.Utils;
import org.bcia.javachain.core.ledger.CompositeKey;
import org.bcia.javachain.core.ledger.IKeyModification;
import org.bcia.javachain.core.ledger.IKeyValue;
import org.bcia.javachain.core.ledger.IQueryResultsIterator;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContract;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.SmartContractEventPackage;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockStub is an implementation of ChaincodeStubInterface for unit testing chaincode.
*  Use this instead of ChaincodeStub in your chaincode's unit test calls to Init or Invoke.
 * @author sunianle
 * @date 3/27/18
 * @company Dingxuan
 */
public class MockStub implements ISmartContractStub{
    private static JavaChainLog log = JavaChainLogFactory.getLog(MockStub.class);
    private List<ByteString> args;
    private String name;
    private ISmartContract smartContract;
    private HashMap<String,Object> state;
    private List keys;
    String txID;
    Timestamp txTimeStamp;
    ProposalPackage.SignedProposal signedProposal;
    String groupID;

    public MockStub(String name,ISmartContract smartContract){
        log.debug("MockStub({},{})",name,smartContract.getSmartContractID());
        this.name=name;
        this.smartContract=smartContract;
        state=new HashMap<String,Object>();
        keys=new LinkedList();
    }

    @Override
    public List<byte[]> getArgs() {
        return args.stream().map(x -> x.toByteArray()).collect(Collectors.toList());
    }

    @Override
    public List<String> getStringArgs() {
        return args.stream().map(x -> x.toStringUtf8()).collect(Collectors.toList());
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public List<String> getParameters() {
        return null;
    }

    @Override
    public String getTxId() {
        return null;
    }

    @Override
    public String getGroupId() {
        return null;
    }

    @Override
    public Response invokeSmartContract(String smartContractName, List<byte[]> args, String group) {
        return null;
    }

    @Override
    public byte[] getState(String key) {
        return new byte[0];
    }

    @Override
    public void putState(String key, byte[] value) {

    }

    @Override
    public void delState(String key) {

    }

    @Override
    public IQueryResultsIterator<IKeyValue> getStateByRange(String startKey, String endKey) {
        return null;
    }

    @Override
    public IQueryResultsIterator<IKeyValue> getStateByPartialCompositeKey(String compositeKey) {
        return null;
    }

    @Override
    public CompositeKey createCompositeKey(String objectType, String... attributes) {
        return null;
    }

    @Override
    public CompositeKey splitCompositeKey(String compositeKey) {
        return null;
    }

    @Override
    public IQueryResultsIterator<IKeyValue> getQueryResult(String query) {
        return null;
    }

    @Override
    public IQueryResultsIterator<IKeyModification> getHistoryForKey(String key) {
        return null;
    }

    @Override
    public void setEvent(String name, byte[] payload) {

    }

    @Override
    public Response invokeSmartContract(String smartContractName, List<byte[]> args) {
        return null;
    }

    @Override
    public Response invokeSmartContractWithStringArgs(String smartContractName, List<String> args, String group) {
        return null;
    }

    @Override
    public Response invokeSmartContractWithStringArgs(String smartContractName, List<String> args) {
        return null;
    }

    @Override
    public Response invokeSmartContractWithStringArgs(String smartContractName, String... args) {
        return null;
    }

    @Override
    public String getStringState(String key) {
        return null;
    }

    @Override
    public void putStringState(String key, String value) {

    }

    @Override
    public SmartContractEventPackage.SmartContractEvent getEvent() {
        return null;
    }

    @Override
    public ProposalPackage.SignedProposal getSignedProposal() {
        return signedProposal;
    }

    @Override
    public Instant getTxTimestamp() {
        return null;
    }

    @Override
    public byte[] getCreator() {
        return new byte[0];
    }

    @Override
    public Map<String, byte[]> getTransient() {
        return null;
    }

    @Override
    public byte[] getBinding() {
        return new byte[0];
    }

    public Response mockInit(String uuid,List<ByteString> args){
        this.args=args;
        mockTransactionStart(uuid);
        Response response=this.smartContract.init(this);
        mockTransactionEnd(uuid);
        return response;
    }

    public Response mockInvoke(String uuid,List<ByteString> args){
        this.args=args;
        mockTransactionStart(uuid);
        Response response=this.smartContract.invoke(this);
        mockTransactionEnd(uuid);
        return response;
    }

    public Response mockInvokeWithSignedProposal(String uuid, List<ByteString> args,
                                                 ProposalPackage.SignedProposal proposal){
        this.args=args;
        mockTransactionStart(uuid);
        this.signedProposal=proposal;
        Response response=this.smartContract.invoke(this);
        mockTransactionEnd(uuid);
        return response;
    }

    private void mockTransactionEnd(String uuid) {
        this.signedProposal=null;
        txID="";
    }

    private void mockTransactionStart(String uuid) {
        this.txID=uuid;
        this.signedProposal=ProposalPackage.SignedProposal.newBuilder().build();
        this.txTimeStamp=Utils.createUtcTimeStamp();
    }
}
