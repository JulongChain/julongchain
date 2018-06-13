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
package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.kvledger.IRecoverable;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.julongchain.protos.common.Common;

/**
 * HistoryDB接口
 * HistoryDB只储存block中有效交易的Key
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public interface IHistoryDB extends IRecoverable {
    /**
     * HistoryDB检索器
     */
    IHistoryQueryExecutor newHistoryQueryExecutor(IBlockStore blockStore) throws LedgerException;

    /**
     * 完成HistoryDB更新
     */
    void commit(Common.Block block) throws LedgerException;

    /**
     * 获取最新存储点
     */
    Height getLastSavepoint() throws LedgerException;

    /**
     * 判断是否需要恢复数据库
     */
    @Override
    long shouldRecover() throws LedgerException;

    /**
     * 恢复位置
     */
    long recoverPoint(long lastAvailableBlock) throws LedgerException;

    /**
     * 提交丢失的区块
     */
    @Override
    void commitLostBlock(BlockAndPvtData blockAndPvtData) throws  LedgerException;

}
