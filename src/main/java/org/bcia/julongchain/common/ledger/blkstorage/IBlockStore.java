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
package org.bcia.julongchain.common.ledger.blkstorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blockledger.IFileLedgerBlockStore;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 区块文件接口
 *
 * @author sunzongyu
 * @date 2018/4/7
 * @company Dingxuan
 */
public interface IBlockStore extends IFileLedgerBlockStore {

    /**
     * 在区块文件中添加block
     * @param block 所须添加的block
     * @throws LedgerException
     */
    @Override
    void addBlock(Common.Block block) throws LedgerException;

    /**
     * 获取区块链文件信息
     * @return
     * @throws LedgerException
     */
    @Override
    Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException;

    /**
     * 查询区块
     * @param startNum 起始区块编号
     * @return
     * @throws LedgerException
     */
    @Override
    IResultsIterator retrieveBlocks(long startNum) throws LedgerException;

    /**
     * 根据区块Hash查询区块
     * @param blockHash 区块Hash
     * @return
     * @throws LedgerException
     */
    Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException;

    /**
     * 根据区块编号查询区块
     * @param blockNum 区块编号
     * @return
     * @throws LedgerException
     */
    Common.Block retrieveBlockByNumber(long blockNum) throws LedgerException;

    /**
     * 根据交易ID查询交易
     * @param txID 交易ID
     * @return
     * @throws LedgerException
     */
    Common.Envelope retrieveTxByID(String txID) throws LedgerException;

    /**
     * 根据区块ID，区块编号查询交易所在区块
     * @param blockNum 区块ID
     * @param tranNum 交易在区块中编号
     * @return
     * @throws LedgerException
     */
    Common.Envelope retrieveTxByBlockNumTranNum(long blockNum, long tranNum) throws LedgerException;

    /**
     * 根据交易ID查询交易所在区块
     * @param txID 交易ID
     * @return
     * @throws LedgerException
     */
    Common.Block retrieveBlockByTxID(String txID) throws LedgerException;

    /**
     * 根据交易ID获取交易验证信息
     * @param txID 交易ID
     * @return
     * @throws LedgerException
     */
    TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID (String txID) throws LedgerException;

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 提交区块
     * @param blockAndPvtData
     * @throws LedgerException
     */
    void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException;
}
