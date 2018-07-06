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
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.RWSetBuilder;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

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
    private List<ResultsItr> itrs = new ArrayList<>();
    private boolean doneInvoked = false;

    public QueryHelper(LockBasedTxManager txMgr, RWSetBuilder rwSetBuilder) {
        this.txMgr = txMgr;
        this.rwSetBuilder = rwSetBuilder;
    }

    public byte[] getState(String ns, String key) throws LedgerException{
        checkDone();
        VersionedValue versionedValue = txMgr.getDb().getState(ns, key);
        if(versionedValue == null){
            return null;
        }
        byte[] val = versionedValue.getValue();
        LedgerHeight ver = versionedValue.getVersion();
        addToReadSet(ns, key, ver);
        return val;
    }

    public List<byte[]> getStateMultipleKeys(String ns, List<String> keys) throws LedgerException{
        checkDone();
        List<VersionedValue> versionedValues = txMgr.getDb().getStateMultipleKeys(ns, keys);
        List<byte[]> values = new ArrayList<>();
        for (int i = 0; i < versionedValues.size(); i++) {
            byte[] val = versionedValues.get(i).getValue();
            LedgerHeight ver = versionedValues.get(i).getVersion();
            addToReadSet(ns, keys.get(i), ver);
            values.add(val);
        }
        return values;
    }

    public IResultsIterator getStateRangeScanIterator(String ns, String startKey, String endKey) throws LedgerException{
        checkDone();
        ResultsItr itr = new ResultsItr(ns, startKey, endKey, txMgr.getDb(), rwSetBuilder, false, LedgerConfig.getMaxDegreeQueryReadsHashing());
        itrs.add(itr);
        return itr;
    }

    /**
     * 富查询 leveldb不支持
     */
    public IResultsIterator executeQuery(String ns, String query) throws LedgerException {
        checkDone();
        IResultsIterator dbItr = txMgr.getDb().executeQuery(ns, query);
        return new QueryResultsItr(dbItr ,rwSetBuilder);
    }

    public byte[] getPrivateData(String ns, String coll, String key) throws LedgerException {
        checkDone();
        VersionedValue versionedValue = txMgr.getDb().getPrivateData(ns, coll, key);
        byte[] val = versionedValue.getValue();
        LedgerHeight ver = versionedValue.getVersion();
        //TODO SM3 hash
        byte[] keyHash = Util.getHashBytes(key.getBytes());
        LedgerHeight hashVersion = txMgr.getDb().getKeyHashVersion(ns, coll, keyHash);
        if(!LedgerHeight.areSame(ver, hashVersion)){
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
            LedgerHeight ver = versionedValues.get(i).getVersion();
            addToHashedReadSet(ns, coll, keys.get(i), ver);
            values.add(val);
        }
        return values;
    }

    public IResultsIterator getPrivateDataRangeScanIterator(String ns, String coll, String startKey, String endKey) throws LedgerException {
        checkDone();
        IResultsIterator dbitr = txMgr.getDb().getPrivateDataRangeScanIterator(ns, coll, startKey, endKey);
        return new PvtdataIResultsItr(ns, coll, dbitr);
    }

    /**
     * 针对pvtdata富查询 leveldb不支持
     */
    public IResultsIterator executeQueryOnPrivateData(String ns, String coll, String query) throws LedgerException {
        checkDone();
        IResultsIterator dbitr = txMgr.getDb().executeQueryOnPrivateData(ns, coll, query);
        return new PvtdataIResultsItr(ns, coll, dbitr);
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
                        //add all kvReads
                        builder.addAllKvReads(entry.getKey());
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
            for(IResultsIterator itr : itrs){
                itr.close();
            }
        }
    }

    private void addToReadSet(String ns, String key, LedgerHeight ver){
        if(rwSetBuilder != null){
            rwSetBuilder.addToReadSet(ns, key, ver);
        }
    }

    private void addToHashedReadSet(String ns, String coll, String key, LedgerHeight ver) throws LedgerException{
        if(rwSetBuilder != null){
            rwSetBuilder.addToHashedReadSet(ns, coll, key, ver);
        }
    }

    public void checkDone() throws LedgerException{
        if(doneInvoked){
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
