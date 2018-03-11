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
import org.bcia.javachain.protos.common.Common;

/**
 * provides handle to ledger instances
 *
 * @author wanliangbing
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface INodeLedgerProvider {

    /**
     * creates a new ledger with the given genesis block.
     * This function guarantees that the creation of ledger and committing the genesis block would an atomic action
     * The chain id retrieved from the genesis block is treated as a ledger id
     *
     * @param genesisBlock
     * @return
     * @throws LedgerException
     */
    INodeLedger create(Common.Block genesisBlock) throws LedgerException;

    /**
     * opens an already created ledger
     *
     * @param ledgerID
     * @return
     * @throws LedgerException
     */
    INodeLedger open(String ledgerID) throws LedgerException;

    /**
     * tells whether the ledger with given id exists
     *
     * @param ledgerID
     * @return
     * @throws LedgerException
     */
    Boolean exists(String ledgerID) throws LedgerException;

    /**
     * lists the ids of he existing ledgers
     *
     * @return
     * @throws LedgerException
     */
    String[] list() throws LedgerException;

    /**
     * closes the NodeLedgerProvider
     *
     * @throws LedgerException
     */
    void close() throws LedgerException;

}
