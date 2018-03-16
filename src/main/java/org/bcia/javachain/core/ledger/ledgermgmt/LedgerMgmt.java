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
package org.bcia.javachain.core.ledger.ledgermgmt;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.customtx.IProcessor;
import org.bcia.javachain.protos.common.Common;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public class LedgerMgmt {

    private static JavaChainLog log = JavaChainLogFactory.getLog(LedgerMgmt.class);

    public static void initialize(IProcessor processor) {
        log.info("Initializing ledger mgmt");
        log.info("ledger mgmt initialized");
    }

    /** CreateLedger creates a new ledger with the given genesis block.
     * This function guarantees that the creation of ledger and committing the genesis block would an atomic action
     * The chain id retrieved from the genesis block is treated as a ledger id
     *
     * @return
     */
    public static INodeLedger createLedger(Common.Block genesisBlock) {
        String id = "";
        log.info("Creating ledger [%s] with genesis block", id);
        log.info("Created ledger [%s] with genesis block", id);
        return null;
    }

    /** OpenLedger returns a ledger for the given id
     *
     * @param id
     * @return
     */
    public static INodeLedger openLedger(String id) {
        log.info("Opening ledger with id = %s", id);
        log.info("Opened ledger with id = %s", id);
        return null;
    }

    /** GetLedgerIDs returns the ids of the ledgers created
     *
     * @return
     */
    public static String[] getLedgerIDs() {
        return null;
    }

    /** Close closes all the opened ledgers and any resources held for ledger management
     *
     */
    public static void close() {
        log.info("Closing ledger mgmt");
        log.info("ledger mgmt closed");
    }

    public static INodeLedger wrapLedger(String id, INodeLedger ledger) {
        return null;
    }

}
