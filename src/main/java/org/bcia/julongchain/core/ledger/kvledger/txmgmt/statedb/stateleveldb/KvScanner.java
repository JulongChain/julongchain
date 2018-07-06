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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.StatedDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

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

    public KvScanner(String nameSpace, Iterator dbItr) {
        this.nameSpace = nameSpace;
        this.dbItr = dbItr;
    }

    @Override
    public QueryResult next() throws LedgerException {
        if(!dbItr.hasNext()){
            return null;
        }
        Map.Entry<byte[], byte[]> iterator = (Map.Entry<byte[], byte[]>) dbItr.next();
        byte[] dbKey = iterator.getKey();
	    String s = new String(dbKey);
	    byte[] dbVal = iterator.getValue();
        byte[] dbValCpy = Arrays.copyOf(dbVal, dbVal.length);
        String key = VersionedLevelDB.splitCompositeKeyToKey(dbKey);
        byte[] value = StatedDB.decodeValueToBytes(dbValCpy);
        LedgerHeight version = StatedDB.decodeValueToHeight(dbValCpy);
        return new QueryResult(
                new VersionedKV(
                        new CompositeKey(nameSpace, key),
                        new VersionedValue(version, value)));
    }

    @Override
    public void close() throws LedgerException {

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
}
