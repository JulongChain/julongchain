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
package org.bcia.julongchain.common.ledger.blockledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;

/**
 * 迭代器接口
 *
 * @author sunzongyu
 * @date 2018/04/26
 * @company Dingxuan
 */
public interface IIterator extends IResultsIterator {

    /**
     * 返回下一个block
     */
    @Override
    QueryResult next() throws LedgerException;

    void readyChain() throws LedgerException;

    @Override
    void close() throws LedgerException;
}
