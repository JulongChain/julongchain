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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.statebasedval;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.*;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.CompositeKey;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.UpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;

/**
 * 组合的迭代器
 * updatesItr   遍历更新包   A
 * dbItr        遍历db中的键 B
 *
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class CombinedIterator implements IResultsIterator {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(CombinedIterator.class);

    private String ns;
    private IVersionedDB db;
    private UpdateBatch updates;
    private String endKey;
    private boolean includeEndKey;
    private IResultsIterator dbItr;
    private IResultsIterator updatesItr;
    private QueryResult dbItm;
    private QueryResult updatesItm;
    private boolean endKeyServed;

    public CombinedIterator(IVersionedDB db,
                            UpdateBatch updates,
                            String ns,
                            String startKey,
                            String endKey,
                            boolean includeEndKey) throws LedgerException {
        IResultsIterator dbItr = db.getStateRangeScanIterator(ns, startKey, endKey);
        IResultsIterator updatesItr = updates.getRangeScanIterator(ns, startKey, endKey);
        QueryResult dbItm = dbItr.next();
        QueryResult updatesItm = updatesItr.next();
        logger.debug("Combined iterator initialized");
        this.ns = ns;
        this.db = db;
        this.updates = updates;
        this.endKey = endKey;
        this.includeEndKey = includeEndKey;
        this.dbItr = dbItr;
        this.updatesItr = updatesItr;
        this.dbItm = dbItm;
        this.updatesItm = updatesItm;
        this.endKeyServed = false;
    }

    @Override
    public QueryResult next() throws LedgerException {
        if(dbItm == null && updatesItm == null){
            logger.debug("dbItm and updatesItm both are null");
            return serveEndKeyIfNeeded();
        }
        int compareResult = compareKey(dbItm, updatesItm);
        QueryResult selectedItm = null;
        boolean moveDBItr = false;
        boolean moveUpdatesItr = false;
        logger.debug("compareResult = " + compareResult);
        switch (compareResult){
            case -1:
                selectedItm = dbItm;
                moveDBItr = true;
                break;
            case 0:
                selectedItm = updatesItm;
                moveDBItr = true;
                moveUpdatesItr = true;
                break;
            case 1:
                selectedItm = updatesItm;
                moveUpdatesItr = true;
                break;
                default:
        }
        if(moveDBItr){
            dbItm = dbItr.next();
        }
        if(moveUpdatesItr){
            updatesItm = updatesItr.next();
        }
        if(isDelete(selectedItm)){
            return next();
        }
        logger.debug("Returning.");
        return selectedItm;
    }

    @Override
    public void close() throws LedgerException {
        dbItr.close();
    }

    private boolean isDelete(QueryResult itm){
        return  ((VersionedKV) itm.getObj()).getVersionedValue().getValue() == null;
    }

    private int compareKey(QueryResult o1, QueryResult o2){
        if(o1 == null){
            if(o2 == null){
                return 0;
            }
            return 1;
        }
        if(o2 == null){
            return -1;
        }
        return ((VersionedKV) o1.getObj()).getCompositeKey().getKey()
                .compareTo(((VersionedKV) o2.getObj()).getCompositeKey().getKey());
    }

    private QueryResult serveEndKeyIfNeeded() throws LedgerException {
        if(!includeEndKey || endKeyServed){
            logger.debug("Endkey not be served... Returning null");
            return null;
        }
        logger.debug("Serving the endKey");
        VersionedValue vv = updates.get(ns, endKey);
        logger.debug("endKey value from updates " + vv);
        if(vv == null){
            vv = db.getState(ns, endKey);
            logger.debug("endKey value of statedb " + vv);
        }
        endKeyServed = true;
        if(vv == null){
            return null;
        }
        return new QueryResult(new VersionedKV(new CompositeKey(ns, endKey), vv));
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public IVersionedDB getDb() {
        return db;
    }

    public void setDb(IVersionedDB db) {
        this.db = db;
    }

    public UpdateBatch getUpdates() {
        return updates;
    }

    public void setUpdates(UpdateBatch updates) {
        this.updates = updates;
    }

    public String getEndKey() {
        return endKey;
    }

    public void setEndKey(String endKey) {
        this.endKey = endKey;
    }

    public boolean isIncludeEndKey() {
        return includeEndKey;
    }

    public void setIncludeEndKey(boolean includeEndKey) {
        this.includeEndKey = includeEndKey;
    }

    public IResultsIterator getDbItr() {
        return dbItr;
    }

    public void setDbItr(IResultsIterator dbItr) {
        this.dbItr = dbItr;
    }

    public IResultsIterator getUpdatesItr() {
        return updatesItr;
    }

    public void setUpdatesItr(IResultsIterator updatesItr) {
        this.updatesItr = updatesItr;
    }

    public QueryResult getDbItm() {
        return dbItm;
    }

    public void setDbItm(QueryResult dbItm) {
        this.dbItm = dbItm;
    }

    public QueryResult getUpdatesItm() {
        return updatesItm;
    }

    public void setUpdatesItm(QueryResult updatesItm) {
        this.updatesItm = updatesItm;
    }

    public boolean isEndKeyServed() {
        return endKeyServed;
    }

    public void setEndKeyServed(boolean endKeyServed) {
        this.endKeyServed = endKeyServed;
    }
}
