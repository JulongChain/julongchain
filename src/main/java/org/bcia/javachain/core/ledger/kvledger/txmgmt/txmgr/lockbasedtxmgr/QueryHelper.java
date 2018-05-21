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
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.Util;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedValue;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.csp.gm.sm3.SM3;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 辅助查询statedb
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class QueryHelper {
    private LockBasedTxManager txMgr;
    private RWSetBuilder rwSetBuilder;
    private List<ResultsItr> itrs;
    private boolean doneInvoked = true;

    public byte[] getState(String ns, String key) throws LedgerException{
        checkDone();
        VersionedValue versionedValue = txMgr.getDb().getState(ns, key);
        if(versionedValue == null){
            return null;
        }
        byte[] val = versionedValue.getValue();
        Height ver = versionedValue.getVersion();
        addToReadSet(ns, key, ver);
        return val;
    }

    public List<byte[]> getStateMultipleKeys(String ns, List<String> keys) throws LedgerException{
        checkDone();
        List<VersionedValue> versionedValues = txMgr.getDb().getStateMultipleKeys(ns, keys);
        List<byte[]> values = new ArrayList<>();
        for (int i = 0; i < versionedValues.size(); i++) {
            byte[] val = versionedValues.get(i).getValue();
            Height ver = versionedValues.get(i).getVersion();
            addToReadSet(ns, keys.get(i), ver);
            values.add(val);
        }
        return values;
    }

    public ResultsIterator getStateRangeScanIterator(String ns, String startKey, String endKey) throws LedgerException{
        checkDone();
        ResultsItr itr = ResultsItr.newResultsItr(ns, startKey, endKey, txMgr.getDb(), rwSetBuilder, false, LedgerConfig.getMaxDegreeQueryReadsHashing());
        itrs.add(itr);
        return itr;
    }

    /**
     * 富查询 leveldb不支持
     */
    public ResultsIterator executeQuery(String ns, String query) throws LedgerException {
        checkDone();
        ResultsIterator dbItr = txMgr.getDb().executeQuery(ns, query);
        QueryResultsItr itr = new QueryResultsItr();
        itr.setDbItr(dbItr);
        itr.setRwSetBuilder(rwSetBuilder);
        return itr;
    }

    public byte[] getPrivateData(String ns, String coll, String key) throws LedgerException {
        checkDone();
        VersionedValue versionedValue = txMgr.getDb().getPrivateData(ns, coll, key);
        byte[] val = versionedValue.getValue();
        Height ver = versionedValue.getVersion();
        //TODO SM3 hash
        byte[] keyHash = Util.getHashBytes(key.getBytes());
        Height hashVersion = txMgr.getDb().getKeyHashVersion(ns, coll, keyHash);
        if(!Height.areSame(ver, hashVersion)){
            throw new LedgerException(String.format("Private data matching public hash version is not available.Pub version %s. Pvt version %s",
                    ver, hashVersion));
        }
        addToHashedReadSet(ns, coll, key, ver);
        return val;
    }

    public List<byte[]> getPrivateDataMultipleKeys(String ns, String coll, List<String> keys) throws LedgerException {
        checkDone();
        List<VersionedValue> versionedValues = txMgr.getDb().getPrivateDataMultipleKeys(ns, coll, keys);
        List<byte[]> values = new ArrayList<>();
        for (int i = 0; i < versionedValues.size() ; i++) {
            byte[] val = versionedValues.get(i).getValue();
            Height ver = versionedValues.get(i).getVersion();
            addToHashedReadSet(ns, coll, keys.get(i), ver);
            values.add(val);
        }
        return values;
    }

    public ResultsIterator getPrivateDataRangeScanIterator(String ns, String coll, String startKey, String endKey) throws LedgerException {
        checkDone();
        ResultsIterator dbitr = txMgr.getDb().getPrivateDataRangeScanIterator(ns, coll, startKey, endKey);
        PvtdataResultsItr itr = new PvtdataResultsItr();
        itr.setNs(ns);
        itr.setColl(coll);
        itr.setDbItr(dbitr);
        return itr;
    }

    /**
     * 针对pvtdata富查询 leveldb不支持
     */
    public ResultsIterator executeQueryOnPrivateData(String ns, String coll, String query) throws LedgerException {
        checkDone();
        ResultsIterator dbitr = txMgr.getDb().executeQueryOnPrivateData(ns, coll, query);
        PvtdataResultsItr itr = new PvtdataResultsItr();
        itr.setNs(ns);
        itr.setColl(coll);
        itr.setDbItr(dbitr);
        return itr;
    }

    public void done() throws LedgerException{
        if(doneInvoked){
            return;
        }
        try {
            for(ResultsItr itr : itrs){
                if (rwSetBuilder != null){
                    Map.Entry<List<KvRwset.KVRead>, KvRwset.QueryReadsMerkleSummary> entry = itr.getRangeQueryResultsHelper().done();
                    if(entry.getKey() != null){
                        KvRwset.RangeQueryInfo rqi = itr.getRangeQueryInfo();
                        KvRwset.QueryReads.Builder builder = rqi.getRawReads().toBuilder();
                        builder = setKvReads(builder, entry.getKey());
                        rqi.toBuilder().setRawReads(builder);
                        itr.setRangeQueryInfo(rqi);
                    }
                    if(entry.getValue() != null){
                        itr.setRangeQueryInfo(itr.getRangeQueryInfo().toBuilder().setReadsMerkleHashes(entry.getValue()).build());
                    }
                    rwSetBuilder.addToRangeQuerySet(itr.getNs(), itr.getRangeQueryInfo());
                }
            }
        } finally {
            doneInvoked = true;
            for(ResultsIterator itr : itrs){
                itr.close();
            }
        }
    }

    /**
     * grpc set各种list貌似只能一个一个添加:( ...
     */
    private KvRwset.QueryReads.Builder setKvReads(KvRwset.QueryReads.Builder builder, List<KvRwset.KVRead> list){
        for (int i = 0; i < list.size(); i++) {
            builder.addKvReads(i, list.get(i));
        }
        return builder;
    }

    private void addToReadSet(String ns, String key, Height ver){
        if(rwSetBuilder != null){
            rwSetBuilder.addToReadSet(ns, key, ver);
        }
    }

    private void addToHashedReadSet(String ns, String coll, String key, Height ver){
        if(rwSetBuilder != null){
            rwSetBuilder.addToHashedReadSet(ns, coll, key, ver);
        }
    }

    public void checkDone() throws LedgerException{
        if(!doneInvoked){
            throw new LedgerException("This instance should not be used after calling Done()");
        }
    }

    public LockBasedTxManager getTxMgr() {
        return txMgr;
    }

    public void setTxMgr(LockBasedTxManager txMgr) {
        this.txMgr = txMgr;
    }

    public RWSetBuilder getRwSetBuilder() {
        return rwSetBuilder;
    }

    public void setRwSetBuilder(RWSetBuilder rwSetBuilder) {
        this.rwSetBuilder = rwSetBuilder;
    }

    public List<ResultsItr> getItrs() {
        return itrs;
    }

    public void setItrs(List<ResultsItr> itrs) {
        this.itrs = itrs;
    }

    public boolean isDoneInvoked() {
        return doneInvoked;
    }

    public void setDoneInvoked(boolean doneInvoked) {
        this.doneInvoked = doneInvoked;
    }
}
