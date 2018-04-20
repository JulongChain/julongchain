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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.IQueryExecutor;

import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class LockBasedQueryExecutor implements IQueryExecutor {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(LockBasedQueryExecutor.class);

    private QueryHelper helper;
    private String txID;

    public static LockBasedQueryExecutor newQueryExecutor(LockBasedTxManager txMgr, String txID){
        LockBasedQueryExecutor executor = new LockBasedQueryExecutor();
        QueryHelper helper = new QueryHelper();
        helper.setTxMgr(txMgr);
        executor.setHelper(helper);
        executor.setTxID(txID);
        logger.debug("Constructing new query executor txid = " + txID);
        return executor;
    }

    @Override
    public byte[] getState(String namespace, String key) throws LedgerException {
        return null;

    }

    @Override
    public List<byte[]> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator getStateRangeScanIterator(String namespace, String collection, String startKey, String endKey) throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator executeQuery(String namespace, String query) throws LedgerException {
        return null;
    }

    @Override
    public byte[] getPrivateData(String namespace, String collection, String key) throws LedgerException {
        return new byte[0];
    }

    @Override
    public List<byte[]> getPrivateDataMultipleKeys(String namespace, String collection, List<String> keys) throws LedgerException {
        return null;
    }

    @Override
    public ResultsIterator getPrivateDataRangeScanIterator(String namespace, String collection, String startKey, String endKey) throws LedgerException {
        return null;
    }

    @Override
    public void done() throws LedgerException {

    }

    public QueryHelper getHelper() {
        return helper;
    }

    public void setHelper(QueryHelper helper) {
        this.helper = helper;
    }

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }
}
