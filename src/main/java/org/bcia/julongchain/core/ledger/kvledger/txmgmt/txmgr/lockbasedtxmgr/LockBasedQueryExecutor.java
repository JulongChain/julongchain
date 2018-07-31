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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.IQueryExecutor;

import java.util.List;

/**
 * 交易查询器类
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class LockBasedQueryExecutor implements IQueryExecutor {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(LockBasedQueryExecutor.class);

    private QueryHelper helper;
    private String txID;

    public LockBasedQueryExecutor(QueryHelper helper, String txID) {
        this.helper = helper;
        this.txID = txID;
    }

    public LockBasedQueryExecutor(LockBasedTxManager txMgr, String txID) {
        this.helper = new QueryHelper(txMgr, null);
        this.txID = txID;
        logger.debug("Constructing new query executor txid = " + txID);
    }

    @Override
    public byte[] getState(String namespace, String key) throws LedgerException {
        return helper.getState(namespace, key);
    }

    @Override
    public List<byte[]> getStateMultipleKeys(String namespace, List<String> keys) throws LedgerException {
        return helper.getStateMultipleKeys(namespace, keys);
    }

    @Override
    public IResultsIterator getStateRangeScanIterator(String namespace, String startKey, String endKey) throws LedgerException {
        return helper.getStateRangeScanIterator(namespace, startKey, endKey);
    }

    @Override
    public IResultsIterator executeQuery(String namespace, String query) throws LedgerException {
        return helper.executeQuery(namespace, query);
    }

    @Override
    public byte[] getPrivateData(String namespace, String collection, String key) throws LedgerException {
        return helper.getPrivateData(namespace, collection, key);
    }

    @Override
    public List<byte[]> getPrivateDataMultipleKeys(String namespace, String collection, List<String> keys) throws LedgerException {
        return helper.getPrivateDataMultipleKeys(namespace, collection, keys);
    }

    @Override
    public IResultsIterator getPrivateDataRangeScanIterator(String namespace, String collection, String startKey, String endKey) throws LedgerException {
        return helper.getPrivateDataRangeScanIterator(namespace, collection, startKey, endKey);
    }

    @Override
    public void done(){
        logger.debug("Done with transaction simulation " + txID);
        try {
            helper.done();
        } catch (LedgerException e) {
            logger.error("Can not done query helper");
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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
