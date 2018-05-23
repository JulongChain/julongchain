/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.common.ledger;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IQueryResult;

/**
 * IResultsIterator - an iterator for query result set
 *
 * @author wanliangbing
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface IResultsIterator {

    /**
     * 返回下一个实体,当全部返回时,返回空
     */
    IQueryResult next() throws LedgerException;

    /**
     * 关闭当前迭代器
     */
    void close() throws LedgerException;

}
