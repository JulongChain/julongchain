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
 * HistoryDB查询器
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public interface IHistoryQueryExecutor {

    /**
     * 根据key统计HistoryDB中数据
     * ResultsIterator包含结果, 定义在protos/ledger/queryresult.
     */
    ResultsIterator getHistoryForKey(String namespace, String key) throws LedgerException;

}
