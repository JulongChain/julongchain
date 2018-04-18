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
import org.bcia.javachain.common.ledger.ILedger;
import org.bcia.javachain.common.ledger.PrunePolicy;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.List;

/**
 * 结点账本
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public interface INodeLedger extends ILedger {

    /**
     * 通过id获取交易
     */
    TransactionPackage.ProcessedTransaction getTransactionByID(String txID) throws LedgerException;

    /**
     * 通过区块hash获取区块
     */
    Common.Block getBlockByHash(byte[] blockHash) throws LedgerException;

    /**
     * 通过交易ID获取区块
     */
    Common.Block getBlockByTxID(String txID) throws LedgerException;

    /**
     * 通过交易Id获取交易可行性代码
     */
    TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) throws LedgerException;

    /**
     * 处理交易模拟
     */
    ITxSimulator newTxSimulator(String txId) throws LedgerException;

    /**
     * 获取查询器
     */
    IQueryExecutor newQueryExecutor() throws LedgerException;

    /**
     * 获取HistoryDB查询器
     */
    IHistoryQueryExecutor newHistoryQueryExecutor() throws LedgerException;

    /**
     * 通过给出的政策修剪区块
     */

    BlockAndPvtData getPvtDataAndBlockByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException;

    List<TxPvtData> getPvtDataByNum(long blockNum, PvtNsCollFilter filter) throws LedgerException;

    void purgePrivateData(long maxBlockNumToRetain) throws LedgerException;

    long privateDataMinBlockNum() throws LedgerException;

    void prune(PrunePolicy policy) throws LedgerException;

    void close() throws LedgerException;

    void CommitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException;
}
