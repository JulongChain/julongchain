/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger.ledgerstorage;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.ledger.blkstorage.BlockStoreProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

/**
 * 提供文件系统以及pvtdata操作类
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class Provider {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(Provider.class);

    private BlockStoreProvider blkStoreProvider;
    private org.bcia.javachain.core.ledger.pvtdatastorage.Provider pvtDataStoreProvider;

    public Store open(String ledgerID) throws LedgerException {
        BlockStore blockStore = blkStoreProvider.openBlockStore(ledgerID);
        org.bcia.javachain.core.ledger.pvtdatastorage.Store pvtDataStore = pvtDataStoreProvider.openStore(ledgerID);
        Store store = new Store();
        store.setBlkStorage(blockStore);
        store.setPvtdataStore(pvtDataStore);
        store.init();
        return store;
    }

    public void close() throws LedgerException{
        blkStoreProvider.close();
        pvtDataStoreProvider.close();
    }

    public BlockStoreProvider getBlkStoreProvider() {
        return blkStoreProvider;
    }

    public void setBlkStoreProvider(BlockStoreProvider blkStoreProvider) {
        this.blkStoreProvider = blkStoreProvider;
    }

    public org.bcia.javachain.core.ledger.pvtdatastorage.Provider getPvtDataStoreProvider() {
        return pvtDataStoreProvider;
    }

    public void setPvtDataStoreProvider(org.bcia.javachain.core.ledger.pvtdatastorage.Provider pvtDataStoreProvider) {
        this.pvtDataStoreProvider = pvtDataStoreProvider;
    }
}
