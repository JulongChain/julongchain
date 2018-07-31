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
package org.bcia.julongchain.core.ledger.ledgermgmt;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.INodeLedgerProvider;
import org.bcia.julongchain.core.ledger.IStateListener;
import org.bcia.julongchain.core.ledger.customtx.CustomTx;
import org.bcia.julongchain.core.ledger.customtx.IProcessor;
import org.bcia.julongchain.core.ledger.kvledger.KvLedgerProvider;
import org.bcia.julongchain.core.ledger.sceventmgmt.KVLedgerLSSCStateListener;
import org.bcia.julongchain.core.ledger.sceventmgmt.ScEventManager;
import org.bcia.julongchain.protos.common.Common;

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
    private static final Map<String, IStateListener> KV_LEDGER_STATE_LISTENERS = new HashMap<String, IStateListener>(){{
        put("lscc", new KVLedgerLSSCStateListener());
    }};

    private static JavaChainLog log = JavaChainLogFactory.getLog(LedgerManager.class);
    private static Map<String, INodeLedger> openedLedgers = new HashMap<>();
    private static INodeLedgerProvider ledgerProvider = null;
    private static boolean initialized = false;

    /**
     * 初始化
     */
    public static synchronized void initialize(Map<Common.HeaderType, IProcessor> processors) throws LedgerException{
        log.info("Initializing ledger mgmt");
        initialized = true;
        CustomTx.initialize(processors);

        ScEventManager.initialize();

        INodeLedgerProvider provider;
        try {
            provider = new KvLedgerProvider();
        } catch (LedgerException e) {
            log.error(String.format("Got error [%s] in initializing LedgerManager", e.getMessage()));
            throw e;
        }
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
        String id;
        //获取区块id
        try {
            id = BlockUtils.getGroupIDFromBlock(genesisBlock);
        } catch (JavaChainException e) {
            throw new LedgerException(e);
        }
        log.info(String.format("Creating ledger [%s] with genesis block", id));
        INodeLedger l = ledgerProvider.create(genesisBlock);
        openedLedgers.put(id, l);
        log.info(String.format("Created ledger [%s] with genesis block", id));
        return l;
    }

    /**
     * 打开现有账本
     */
    public synchronized static INodeLedger openLedger(String id) throws LedgerException {
        if(!initialized){
            throw ERR_LEDGER_MANAGER_NOT_INITIALIZED;
        }
        log.info("Opening ledger with id = " + id);
        INodeLedger l = openedLedgers.get(id);
        if(l != null){
            return l;
        }
        l = ledgerProvider.open(id);
        openedLedgers.put(id, l);
        log.info(String.format("Opened ledger with id = %s", id));
        return l;
    }

    /**
     * 获取已经创建的全部账本ID
     */
    public synchronized static List<String> getLedgerIDs() throws LedgerException {
        if(!initialized){
            throw ERR_LEDGER_MANAGER_NOT_INITIALIZED;
        }
        return ledgerProvider.list();
    }

    /**
     * 关闭LedgerManager
     */
    public synchronized static void close() throws LedgerException {
        log.info("Closing ledger manager");
        if(!initialized){
            throw ERR_LEDGER_MANAGER_NOT_INITIALIZED;
        }
        for(INodeLedger l : openedLedgers.values()){
            l.close();
        }
        ledgerProvider.close();
        openedLedgers.clear();
        log.info("Ledger manager closed");
    }

    public synchronized static void initializeTestEnvWithCustomProcessors(Map<Common.HeaderType, IProcessor> processors) throws LedgerException {
    	initialize(processors);
	}
}
