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
import org.bcia.javachain.common.ledger.QueryResult;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.ResultsIterator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.VersionedKV;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class PvtdataResultsItr implements org.bcia.javachain.common.ledger.ResultsIterator {
    private String ns;
    private String coll;
    private ResultsIterator dbItr;

    @Override
    public QueryResult next() throws LedgerException {
        QueryResult queryResult = dbItr.next();
        if(queryResult == null){
            return null;
        }
        return queryResult;
    }

    @Override
    public void close() throws LedgerException {
        dbItr.close();
    }
}
