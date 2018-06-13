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
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.RWSetBuilder;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;

/**
 * 富查询迭代器
 * leveldb不支持
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class QueryResultsItr implements IResultsIterator {
    private static final JavaChainLog loggger  = JavaChainLogFactory.getLog(QueryResultsItr.class);

    private IResultsIterator dbItr;
    private RWSetBuilder rwSetBuilder;

    public QueryResultsItr(IResultsIterator dbItr, RWSetBuilder rwSetBuilder) {
        this.dbItr = dbItr;
        this.rwSetBuilder = rwSetBuilder;
    }

    @Override
    public QueryResult next() throws LedgerException {
        QueryResult queryResult = dbItr.next();
        if(queryResult == null){
            return null;
        }
        VersionedKV versionedQueyrRecord = (VersionedKV) queryResult.getObj();
        loggger.debug("queryResultItr.Next() returned a record " + new String(versionedQueyrRecord.getVersionedValue().getValue()));
        if(rwSetBuilder != null){
            rwSetBuilder.addToReadSet(versionedQueyrRecord.getCompositeKey().getNamespace(),
                    versionedQueyrRecord.getCompositeKey().getKey(),
                    versionedQueyrRecord.getVersionedValue().getVersion());
        }
        return queryResult;
    }

    @Override
    public void close() throws LedgerException {
        dbItr.close();
    }

    public IResultsIterator getDbItr() {
        return dbItr;
    }

    public void setDbItr(IResultsIterator dbItr) {
        this.dbItr = dbItr;
    }

    public RWSetBuilder getRwSetBuilder() {
        return rwSetBuilder;
    }

    public void setRwSetBuilder(RWSetBuilder rwSetBuilder) {
        this.rwSetBuilder = rwSetBuilder;
    }
}
