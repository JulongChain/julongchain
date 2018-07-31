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
package org.bcia.julongchain.core.ledger.ledgerstorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.BlockStorage;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStoreProvider;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.FsBlockStoreProvider;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.pvtdatastorage.IPvtDataStore;
import org.bcia.julongchain.core.ledger.pvtdatastorage.PvtDataProvider;

/**
 * 提供文件系统以及pvtdata操作类
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class Provider {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(Provider.class);

    private IBlockStoreProvider blkStoreProvider;
    private PvtDataProvider pvtDataProvider;

    public Provider() throws LedgerException {
        String[] attrsToIndex = {
                BlockStorage.INDEXABLE_ATTR_BLOCK_HASH,
                BlockStorage.INDEXABLE_ATTR_BLOCK_NUM,
                BlockStorage.INDEXABLE_ATTR_TX_ID,
                BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM,
                BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID,
                BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE
        };
        IndexConfig indexConfig = new IndexConfig(attrsToIndex);
        //文件系统初始化参数
		this.blkStoreProvider =
				new FsBlockStoreProvider(new Config(LedgerConfig.getBlockStorePath(), LedgerConfig.getMaxBlockfileSize()), indexConfig);
        //pvtdata初始化
        this.pvtDataProvider = new PvtDataProvider();
    }

    public Store open(String ledgerID) throws LedgerException {
        IBlockStore blockStore = blkStoreProvider.openBlockStore(ledgerID);
        IPvtDataStore pvtDataStore = pvtDataProvider.openStore(ledgerID);
        Store store = new Store();
        store.setBlkStorage(blockStore);
        store.setPvtdataStore(pvtDataStore);
        store.init();
        return store;
    }

    public void close() throws LedgerException{
        blkStoreProvider.close();
        pvtDataProvider.close();
    }

    public IBlockStoreProvider getBlkStoreProvider() {
        return blkStoreProvider;
    }

    public void setBlkStoreProvider(IBlockStoreProvider blkStoreProvider) {
        this.blkStoreProvider = blkStoreProvider;
    }

    public PvtDataProvider getPvtDataProvider() {
        return pvtDataProvider;
    }

    public void setPvtDataProvider(PvtDataProvider pvtDataProvider) {
        this.pvtDataProvider = pvtDataProvider;
    }
}
