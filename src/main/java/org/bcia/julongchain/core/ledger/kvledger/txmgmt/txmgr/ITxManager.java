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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.txmgr;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.kvledger.IRecoverable;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

/**
 * 交易管理者接口
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public interface ITxManager extends IRecoverable {

    /**
     * 创建查询器
     * @param txid 交易id
     * @return 查询器
     * @throws LedgerException
     */
    IQueryExecutor newQueryExecutor(String txid) throws LedgerException;

    /**
     * 创建交易模拟器
     * @param txid 交易id
     * @return 交易模拟器
     * @throws LedgerException
     */
    ITxSimulator newTxSimulator(String txid) throws LedgerException;

    /**
     * 校验数据
     * @param blockAndPvtData
     * @param doMVCCValidation
     * @throws LedgerException
     */
    void validateAndPrepare(BlockAndPvtData blockAndPvtData, Boolean doMVCCValidation) throws LedgerException;

    /**
     * 获取保存点信息
     * @return
     * @throws LedgerException
     */
    LedgerHeight getLastSavepoint() throws LedgerException;

    /**
     * 账本是否需要被恢复
     * @return
     * @throws LedgerException
     */
    @Override
    long shouldRecover() throws LedgerException;

    /**
     * 提交丢失的区块
     * @param blockAndPvtData
     * @throws LedgerException
     */
    @Override
    void commitLostBlock(BlockAndPvtData blockAndPvtData) throws LedgerException;

    /**
     * 提交区块
     * @throws LedgerException
     */
    void commit() throws LedgerException;

    /**
     * 回滚
     * @throws LedgerException
     */
    void rollback() throws LedgerException;

    /**
     * 关闭
     * @throws LedgerException
     */
    void shutdown() throws LedgerException;
}
