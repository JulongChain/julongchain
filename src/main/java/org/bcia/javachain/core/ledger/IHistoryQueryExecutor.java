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
package org.bcia.javachain.core.ledger;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;

/**
 * HistoryQueryExecutor executes the history queries
 *
 * @author wanliangbing
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface IHistoryQueryExecutor {

    /**
     * GetHistoryForKey retrieves the history of values for a key.
     * The returned ResultsIterator contains results of type *KeyModification which is defined in protos/ledger/queryresult.
     *
     * @param namespace
     * @param key
     * @return
     * @throws LedgerException
     */
    ResultsIterator getHistoryForKey(String namespace, String key) throws LedgerException;

}
