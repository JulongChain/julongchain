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
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * BlockStore - an interface for persisting and retrieving blocks
 * An implementation of this interface is expected to take an argument
 * of type `IndexConfig` which configures the block store on what items should be indexed
 *
 * @author wanliangbing
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface BlockStore {

    void addBlock(Common.Block block) throws LedgerException;

    Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException;

    ResultsIterator retrieveBlocks(Long startNum) throws LedgerException;

    Common.Block retrieveBlockByHash(byte[] blockHash) throws LedgerException;

    Common.Block retrieveBlockByNumber(Long blockNum) throws LedgerException;

    Common.Envelope retrieveTxByID(String txID) throws LedgerException;

    Common.Envelope retrieveTxByBlockNumTranNum(Long blockNum, Long tranNum) throws LedgerException;

    Common.Block retrieveBlockByTxID(String txID) throws LedgerException;

    TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID (String txID) throws LedgerException;

}
