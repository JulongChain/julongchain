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
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.RWSetBuilder;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.RangeQueryResultsHelper;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.RwSetUtil;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

/**
 * 用于getStateRangeScan
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class ResultsItr implements ResultsIterator {
    private String ns;
    private String endKey;
    private ResultsIterator dbItr;
    private RWSetBuilder rwSetBuilder;
    private KvRwset.RangeQueryInfo rangeQueryInfo;
    private RangeQueryResultsHelper rangeQueryResultsHelper;

    public static ResultsItr newResultsItr(String ns,
                                           String startKey,
                                           String endKey,
                                           IVersionedDB db,
                                           RWSetBuilder rwSetBuilder,
                                           boolean enableHashing,
                                           int maxDegree) throws LedgerException {
        ResultsIterator dbItr = db.getStateRangeScanIterator(ns, startKey, endKey);
        ResultsItr itr = new ResultsItr();
        itr.setNs(ns);
        itr.setDbItr(dbItr);
        if(rwSetBuilder != null){
            itr.setRwSetBuilder(rwSetBuilder);
            itr.setEndKey(endKey);
            KvRwset.RangeQueryInfo rqi = KvRwset.RangeQueryInfo.newBuilder()
                    .setStartKey(startKey)
                    .build();
            itr.setRangeQueryInfo(rqi);
            RangeQueryResultsHelper rangeQueryResultsHelper = RangeQueryResultsHelper.newRangeQueryResultsHelper(enableHashing, maxDegree);
            itr.setRangeQueryResultsHelper(rangeQueryResultsHelper);
        }
        return itr;
    }

    private void updateRangeQueryInfo(QueryResult queryResult){
        if(rwSetBuilder == null){
            return;
        }
        if(queryResult == null){
            rangeQueryInfo = rangeQueryInfo.toBuilder()
                    .setEndKey(endKey)
                    .setItrExhausted(true)
                    .build();
        }
        VersionedKV versionedKV = (VersionedKV) queryResult;
        rangeQueryResultsHelper.addResult(RwSetUtil.newKVRead(versionedKV.getCompositeKey().getKey(), versionedKV.getVersionedValue().getVersion()));
        rangeQueryInfo = rangeQueryInfo.toBuilder().setEndKey(versionedKV.getCompositeKey().getKey()).build();
    }

    @Override
    public QueryResult next() throws LedgerException {
        QueryResult queryResult = dbItr.next();
        updateRangeQueryInfo(queryResult);
        if(queryResult == null){
            return null;
        }
        return queryResult;
    }

    @Override
    public void close() throws LedgerException {
        dbItr.close();
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public String getEndKey() {
        return endKey;
    }

    public void setEndKey(String endKey) {
        this.endKey = endKey;
    }

    public ResultsIterator getDbItr() {
        return dbItr;
    }

    public void setDbItr(ResultsIterator dbItr) {
        this.dbItr = dbItr;
    }

    public RWSetBuilder getRwSetBuilder() {
        return rwSetBuilder;
    }

    public void setRwSetBuilder(RWSetBuilder rwSetBuilder) {
        this.rwSetBuilder = rwSetBuilder;
    }

    public KvRwset.RangeQueryInfo getRangeQueryInfo() {
        return rangeQueryInfo;
    }

    public void setRangeQueryInfo(KvRwset.RangeQueryInfo rangeQueryInfo) {
        this.rangeQueryInfo = rangeQueryInfo;
    }

    public RangeQueryResultsHelper getRangeQueryResultsHelper() {
        return rangeQueryResultsHelper;
    }

    public void setRangeQueryResultsHelper(RangeQueryResultsHelper rangeQueryResultsHelper) {
        this.rangeQueryResultsHelper = rangeQueryResultsHelper;
    }
}
