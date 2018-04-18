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
package org.bcia.javachain.common.ledger.blkstorage.fsblkstorage;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.ledger.blkstorage.BlockStoreProvider;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.IoUtil;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBHandle;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class FsBlockStoreProvider implements BlockStoreProvider {

    private Conf conf;
    private IndexConfig indexConfig;
    private LevelDbProvider leveldbProvider;

    /**
     * NewProvider constructs a filesystem based block store provider
     *
     * @return
     */
    public static BlockStoreProvider newProvider(Conf conf, IndexConfig indexConfig) throws LedgerException {
        BlockStoreProvider provider = new FsBlockStoreProvider();
        ((FsBlockStoreProvider) provider).setLeveldbProvider(LevelDbProvider.newProvider());
        ((FsBlockStoreProvider) provider).setConf(conf);
        ((FsBlockStoreProvider) provider).setIndexConfig(indexConfig);
        return provider;
    }

    /**
     * CreateBlockStore simply calls OpenBlockStore*
     *
     */
    public BlockStore createBlockStore(String ledgerid) throws LedgerException {
        return openBlockStore(ledgerid);
    }

    /**
     * OpenBlockStore opens a block store for given ledgerid.
     * If a blockstore is not existing, this method creates one
     * This method should be invoked only once for a particular ledgerid
     */
    public BlockStore openBlockStore(String ledgerid) throws LedgerException {
        return FsBlockStore.newFsBlockStore(ledgerid, conf, indexConfig, leveldbProvider);
    }

    /**
     * Exists tells whether the BlockStore with given id exists
     */
    public Boolean exists(String ledgerid) {
         return IoUtil.fileExists(conf.getLedgerBlockDir(ledgerid)) >= 0;
    }

    /**
     * List lists the ids of the existing ledgers
     */
    public String[] list() {
        return IoUtil.listSubdirs(conf.getChainsDir());
    }

    /**
     * Close closes the FsBlockstoreProvider
     */
    public void close() throws LedgerException {
        leveldbProvider.close();
    }

    public Conf getConf() {
        return conf;
    }

    public void setConf(Conf conf) {
        this.conf = conf;
    }

    public IndexConfig getIndexConfig() {
        return indexConfig;
    }

    public void setIndexConfig(IndexConfig indexConfig) {
        this.indexConfig = indexConfig;
    }

    public LevelDbProvider getLeveldbProvider() {
        return leveldbProvider;
    }

    public void setLeveldbProvider(LevelDbProvider leveldbProvider) {
        this.leveldbProvider = leveldbProvider;
    }
}
