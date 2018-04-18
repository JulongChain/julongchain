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

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.INodeLedgerProvider;
import org.bcia.javachain.core.ledger.StateListener;
import org.bcia.javachain.core.ledger.customtx.CustomTx;
import org.bcia.javachain.core.ledger.customtx.IProcessor;
import org.bcia.javachain.core.ledger.kvledger.KvLedgerProvider;
import org.bcia.javachain.core.ledger.sceventmgmt.KVLedgerLSSCStateListener;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作Ledger主要类
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class LedgerManager {
    private static final LedgerException ERR_LEDGER_MANAGER_NOT_INITIALIZED = new LedgerException("ledger mgmt should be initialized before using");
    private static final LedgerException ERR_LEDGER_ALREADY_OPENEND = new LedgerException("ledger already openend");
    private static final Map<String, StateListener> KV_LEDGER_STATE_LISTENERS = new HashMap<String, StateListener>(){{
        put("lscc", new KVLedgerLSSCStateListener());
    }};

    private static JavaChainLog log = JavaChainLogFactory.getLog(LedgerManager.class);
    private static Map<String, INodeLedger> openedLedgers = new HashMap<>();
    private static INodeLedgerProvider ledgerProvider = null;
    private static boolean initialized = false;

    public static synchronized void initialize(Map<Common.HeaderType, IProcessor> processors) throws LedgerException {
        log.info("Initializing ledger mgmt");
        initialized = true;
        CustomTx.initialize(processors);

        //TODO sceventmgmt.initialize()

        INodeLedgerProvider provider = KvLedgerProvider.newProvider();
        provider.initialize(KV_LEDGER_STATE_LISTENERS);

        ledgerProvider = provider;
        log.info("ledger mgmt initialized");
    }

    /**
     * 根据创世纪区块创建账本
     */
    public static synchronized INodeLedger createLedger(Common.Block genesisBlock) throws LedgerException {
        if(!initialized){
            throw ERR_LEDGER_MANAGER_NOT_INITIALIZED;
        }

        String id = null;
        //获取区块id
        try {
            id = BlockUtils.getGroupIDFromBlock(genesisBlock);
        } catch (JavaChainException e) {
            throw new LedgerException(e);
        }
        log.info(String.format("Creating ledger [%s] with genesis block", id));
        INodeLedger l = ledgerProvider.create(genesisBlock);
        l = wrapLedger(id, l);
        openedLedgers.put(id, l);
        log.info(String.format("Created ledger [%s] with genesis block", id));
        return l;
    }

    /** OpenLedger returns a ledger for the given id
     *
     * @param id
     * @return
     */
    public synchronized static INodeLedger openLedger(String id) throws LedgerException {
        if(!initialized){
            throw ERR_LEDGER_MANAGER_NOT_INITIALIZED;
        }
        log.info("Opening ledger with id = %s", id);
        INodeLedger l = openedLedgers.get(id);
        if(l != null){
            throw ERR_LEDGER_ALREADY_OPENEND;
        }
        l = ledgerProvider.open(id);
        l = wrapLedger(id, l);
        openedLedgers.put(id, l);
        log.info("Opened ledger with id = %s" + id);
        return l;
    }

    /** GetLedgerIDs returns the ids of the ledgers created
     *
     * @return
     */
    public synchronized static List<String> getLedgerIDs() throws LedgerException {
        if(!initialized){
            throw ERR_LEDGER_MANAGER_NOT_INITIALIZED;
        }
        return ledgerProvider.list();
    }

    /** Close closes all the opened ledgers and any resources held for ledger management
     *
     */
    public synchronized static void close() throws LedgerException {
        log.info("Closing ledger mgmt");
        if(!initialized){
            throw ERR_LEDGER_MANAGER_NOT_INITIALIZED;
        }
        for(INodeLedger l : openedLedgers.values()){
            ((ClosableLedger) l).closeWithoutLock();
        }
        ledgerProvider.close();
        openedLedgers = new HashMap<>();
        log.info("ledger mgmt closed");
    }

    public static INodeLedger wrapLedger(String id, INodeLedger l){
        ClosableLedger cl = new ClosableLedger();
        cl.setId(id);
        cl.setNodeLedger(l);
        return cl;
    }

    public void initializeTestEnvWithCustomProcessors(Map<Common.HeaderType, IProcessor> customTxProcessors) throws LedgerException {
        initialize(customTxProcessors);
    }


}