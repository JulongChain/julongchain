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
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;

/**
 * pvtdata查询迭代器
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class PvtdataIResultsItr implements IResultsIterator {
    private String ns;
    private String coll;
    private IResultsIterator dbItr;

    public PvtdataIResultsItr(String ns, String coll, IResultsIterator dbItr) {
        this.ns = ns;
        this.coll = coll;
        this.dbItr = dbItr;
    }

    @Override
    public QueryResult next() throws LedgerException {
        QueryResult queryResult = dbItr.next();
        if(queryResult == null){
            return null;
        }
        VersionedKV versionedQueryRecord = (VersionedKV) queryResult.getObj();
        KvQueryResult.KV kv = KvQueryResult.KV.newBuilder()
                .setNamespace(ns)
                .setKey(versionedQueryRecord.getCompositeKey().getKey())
                .setValue(ByteString.copyFrom(versionedQueryRecord.getVersionedValue().getValue()))
                .build();
        queryResult.setObj(kv);
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

    public String getColl() {
        return coll;
    }

    public void setColl(String coll) {
        this.coll = coll;
    }

    public IResultsIterator getDbItr() {
        return dbItr;
    }

    public void setDbItr(IResultsIterator dbItr) {
        this.dbItr = dbItr;
    }
}
