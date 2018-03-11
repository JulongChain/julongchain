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
package org.bcia.javachain.core.ledger.kvledger.history.historydb;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.common.Common;

/**
 * HistoryDB - an interface that a history database should implement
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public interface HistoryDB {

    IHistoryQueryExecutor newHistoryQueryExecutor(BlockStore blockStore) throws LedgerException;
    void commit(Common.Block block) throws LedgerException;
    Height getLastSavepoint() throws LedgerException;
    Boolean shouldRecover(Long lastAvailableBlock) throws LedgerException;
    void commitLostBlock(Common.Block block) throws  LedgerException;

}
