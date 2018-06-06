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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.StatedDB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * kv查询类
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class KvScanner implements IResultsIterator {

    private String nameSpace;
    private Iterator dbItr;

    public static KvScanner newKVScanner(String nameSpace, Iterator dbItr){
        KvScanner kvScanner = new KvScanner();
        kvScanner.setNameSpace(nameSpace);
        kvScanner.setDbItr(dbItr);
        return kvScanner;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Iterator getDbItr() {
        return dbItr;
    }

    public void setDbItr(Iterator dbItr) {
        this.dbItr = dbItr;
    }

    @Override
    public QueryResult next() throws LedgerException {
        if(!dbItr.hasNext()){
            return null;
        }
        Map.Entry<byte[], byte[]> iterator = (Map.Entry<byte[], byte[]>) dbItr.next();
        byte[] dbKey = iterator.getKey();
        byte[] dbVal = iterator.getValue();
        byte[] dbValCpy = Arrays.copyOf(dbVal, dbVal.length);
        String key = VersionedLevelDB.splitCompositeKeyToKey(dbKey);
        byte[] value = StatedDB.decodeValueToBytes(dbValCpy);
        Height version = StatedDB.decodeValueToHeight(dbValCpy);
        CompositeKey compositeKey = new CompositeKey();
        compositeKey.setKey(key);
        compositeKey.setNamespace(nameSpace);
        VersionedValue versionedValue = new VersionedValue();
        versionedValue.setVersion(version);
        versionedValue.setValue(value);
        VersionedKV kv = new VersionedKV();
        kv.setCompositeKey(compositeKey);
        kv.setVersionedValue(versionedValue);
        return new QueryResult(kv);
    }

    @Override
    public void close() throws LedgerException {

    }
}
