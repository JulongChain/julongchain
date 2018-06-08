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
package org.bcia.julongchain.common.ledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;

/**
 * Ledger captures the methods that are common across the 'PeerLedger', 'OrdererLedger', and 'IValidatedLedger'
 *
 * @author sunzongyu
 * @date 2018/04/03
 * @company Dingxuan
 */
public interface ILedger {

    /**
     * 返回BlockchainInfo
     */
    Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException;

    /**
     * 返回给定的block
     */
    Common.Block getBlockByNumber(long blockNumber) throws LedgerException;

    /**
     * 返回起始位置为startBlockNumber的迭代器ResultIterator
     */
    IResultsIterator getBlocksIterator(long startBlockNumber) throws LedgerException;

    /**
     * 关闭账本
     */
    void close() throws LedgerException;

    /**
     * 创建新的区块
     */
    void commit(Common.Block block) throws LedgerException;

}
