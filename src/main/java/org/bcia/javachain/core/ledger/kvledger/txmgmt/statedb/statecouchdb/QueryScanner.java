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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.statecouchdb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/05/22
 * @company Dingxuan
 */
public class QueryScanner implements ResultsIterator {
    private static final JavaChainLog log = JavaChainLogFactory.getLog(QueryResult.class);

    private int cursor;
    private String ns;

    public static QueryScanner newQueryScanner(String ns){
        QueryScanner qs = new QueryScanner();
        qs.setCursor(-1);
        qs.setNs(ns);
        return qs;
    }

    @Override
    public QueryResult next() throws LedgerException {
        return null;
    }

    @Override
    public void close() throws LedgerException {

    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }
}
