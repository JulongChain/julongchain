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
package org.bcia.javachain.common.ledger.blkstorage;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.common.ledger.blockledger.FileLedgerBlockStore;
import org.bcia.javachain.core.ledger.BlockAndPvtData;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 区块文件接口
 *
 * @author sunzongyu
 * @date 2018/4/7
 * @company Dingxuan
 */
public interface BlockStore extends FileLedgerBlockStore {
    @Override
    void addBlock(Common.Block block) throws LedgerException;

    @Override
    Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException;

    @Override
    ResultsIterator retrieveBlocks(long startNum) throws LedgerException;

    Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException;

    Common.Block retrieveBlockByNumber(Long blockNum) throws LedgerException;

    Common.Envelope retrieveTxByID(String txID) throws LedgerException;

    Common.Envelope retrieveTxByBlockNumTranNum(Long blockNum, Long tranNum) throws LedgerException;

    Common.Block retrieveBlockByTxID(String txID) throws LedgerException;

    TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID (String txID) throws LedgerException;

    void shutdown();

    void commitWithPvtData(BlockAndPvtData blockAndPvtData) throws LedgerException;
}
