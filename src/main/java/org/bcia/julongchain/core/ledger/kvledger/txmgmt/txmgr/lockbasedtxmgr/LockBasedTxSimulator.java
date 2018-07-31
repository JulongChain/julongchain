/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.TxSimulationResults;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.RWSetBuilder;

import java.util.List;
import java.util.Map;

/**
 * 交易模拟器
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class LockBasedTxSimulator implements ITxSimulator {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(LockBasedTxSimulator.class);

    private LockBasedQueryExecutor queryExecutor;
    private RWSetBuilder rwSetBuilder;
    private boolean writePreformed;
    private boolean pvtdataQueriesPerformed;

    public LockBasedTxSimulator(LockBasedTxManager txMgr, String txid) {
        RWSetBuilder rwSetBuilder = new RWSetBuilder();

        QueryHelper queryHelper = new QueryHelper(txMgr, rwSetBuilder);

        this.queryExecutor = new LockBasedQueryExecutor(queryHelper, txid);
        this.rwSetBuilder = rwSetBuilder;
        this.writePreformed = false;
        this.pvtdataQueriesPerformed = false;
    }

    @Override
    public void setState(String namespace, String key, byte[] value) throws LedgerException {
        queryExecutor.getHelper().checkDone();
        checkBeforeWrite();
        queryExecutor.getHelper().getTxMgr().getDb().validateKeyValue(key, value);
        rwSetBuilder.addToWriteSet(namespace, key, ByteString.copyFrom(value));
    }

    @Override
    public void deleteState(String namespace, String key) throws LedgerException {
        setState(namespace, key, new byte[0]);
    }

    @Override
    public void setStateMultipleKeys(String namespace, Map<String, byte[]> kvs) throws LedgerException {
        for(Map.Entry<String, byte[]> entry : kvs.entrySet()){
            String k = entry.getKey();
            byte[] v = entry.getValue();
            setState(namespace, k ,v);
        }
    }

    @Override
    public void executeUpdate(String query) throws LedgerException {

    }

    @Override
    public TxSimulationResults getTxSimulationResults() throws LedgerException {
        done();
        return rwSetBuilder.getTxSimulationResults();
    }

    @Override
    public void setPrivateData(String namespace, String collection, String key, byte[] value) throws LedgerException {
        queryExecutor.getHelper().checkDone();
        checkBeforeWrite();
        queryExecutor.getHelper().getTxMgr().getDb().validateKeyValue(key, value);
        writePreformed = true;
        rwSetBuilder.addToPvtAndHashedWriteSet(namespace, collection, key, ByteString.copyFrom(value));
    }

    @Override
    public void setPirvateDataMultipleKeys(String namespace, String collection, Map<String, byte[]> kvs) throws LedgerException {
        for(Map.Entry<String, byte[]> entry : kvs.entrySet()){
            String k = entry.getKey();
            byte[] v = entry.getValue();
            setPrivateData(namespace, collection, k, v);
        }
    }

    @Override
    public void deletePrivateData(String namespace, String collection, String key) throws LedgerException {
        setPrivateData(namespace, collection, key, new byte[0]);
    }

    @Override
    public byte[] getState(String namespace, String key) throws LedgerException {
        return queryExecutor.getHelper().getState(namespace ,key);
    }

    @Override
    public List<byte[]> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException {
        return queryExecutor.getHelper().getStateMultipleKeys(namespace, keys);
    }

    @Override
    public IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException {
        return queryExecutor.getStateRangeScanIterator(namespace, startKey, endKey);
    }

    @Override
    public IResultsIterator executeQuery(String namespace, String query) throws LedgerException {
        return queryExecutor.getHelper().executeQuery(namespace, query);
    }

    @Override
    public byte[] getPrivateData(String namespace, String collection, String key) throws LedgerException {
    	return queryExecutor.getHelper().getPrivateData(namespace, collection, key);
    }

    @Override
    public List<byte[]> getPrivateDataMultipleKeys(String namespace, String collection, List<String> keys) throws LedgerException {
        return queryExecutor.getHelper().getPrivateDataMultipleKeys(namespace, collection, keys);
    }

    @Override
    public IResultsIterator getPrivateDataRangeScanIterator(String namespace, String collection, String startKey, String endKey) throws LedgerException {
	    checkBeforePvtdataQueries();
        return queryExecutor.getHelper().getPrivateDataRangeScanIterator(namespace, collection, startKey, endKey);
    }

    @Override
    public void done() {
		queryExecutor.done();
    }

    public void checkBeforeWrite() throws LedgerException {
        if(pvtdataQueriesPerformed){
            throw new LedgerException(String.format("Tx %s, Transaction has already performed queries on pvt data. Writes are not allowed", queryExecutor.getTxID()));
        }
        writePreformed = true;
    }

    public void checkBeforePvtdataQueries() throws LedgerException {
        if(writePreformed){
            throw new LedgerException(String.format("Tx %s, Queries on pvt data is supported only in a read-only transaction", queryExecutor.getTxID()));
        }
    }

    public LockBasedQueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    public void setQueryExecutor(LockBasedQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public RWSetBuilder getRwSetBuilder() {
        return rwSetBuilder;
    }

    public void setRwSetBuilder(RWSetBuilder rwSetBuilder) {
        this.rwSetBuilder = rwSetBuilder;
    }

    public boolean isWritePreformed() {
        return writePreformed;
    }

    public void setWritePreformed(boolean writePreformed) {
        this.writePreformed = writePreformed;
    }

    public boolean isPvtdataQueriesPerformed() {
        return pvtdataQueriesPerformed;
    }

    public void setPvtdataQueriesPerformed(boolean pvtdataQueriesPerformed) {
        this.pvtdataQueriesPerformed = pvtdataQueriesPerformed;
    }
}
