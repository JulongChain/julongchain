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
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;

/**
 * Ledger captures the methods that are common across the 'PeerLedger', 'OrdererLedger', and 'ValidatedLedger'
 *
 * @author wanliangbing
 * @date 2018/3/2
 * @company Dingxuan
 */
public interface ILedger {

    /**
     * returns basic info about blockchain
     *
     * @return
     * @throws LedgerException
     */
    Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException;

    /**
     * returns block at a given height
     * blockNumber of  math.MaxUint64 will return last block
     *
     * @param blockNumber
     * @return
     * @throws LedgerException
     */
    Common.Block getBlockByNumber(Long blockNumber) throws LedgerException;

    /**
     * returns an iterator that starts from `startBlockNumber`(inclusive).
     * The iterator is a blocking iterator i.e., it blocks till the next block gets available in the ledger
     * ResultsIterator contains type BlockHolder
     *
     * @param startBlockNumber
     * @return
     * @throws LedgerException
     */
    ResultsIterator getBlocksIterator(Long startBlockNumber) throws LedgerException;

    /**
     * closes the ledger
     */
    void close() throws LedgerException;

    /**
     * adds a new block
     *
     * @param block
     * @throws LedgerException
     */
    void commit(Common.Block block) throws LedgerException;

}
