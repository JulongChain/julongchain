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

import org.bcia.javachain.common.ledger.blkstorage.BlockStore;
import org.bcia.javachain.common.ledger.blkstorage.BlockStoreProvider;
import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.leveldbhelper.Provider;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class FsBlockStoreProvider {

    private Conf conf;
    private IndexConfig indexConfig;
    private Provider leveldbProvider;

    /**
     * NewProvider constructs a filesystem based block store provider
     *
     * @return
     */
    public static BlockStoreProvider newProvider(Conf conf, IndexConfig indexConfig) {
        return null;
    }

    /**
     * CreateBlockStore simply calls OpenBlockStore*
     *
     */
    public BlockStore createBlockStore(String ledgerid) {
        return null;
    }

    /**
     * OpenBlockStore opens a block store for given ledgerid.
     * If a blockstore is not existing, this method creates one
     * This method should be invoked only once for a particular ledgerid
     */
    public BlockStore openBlockStore(String ledgerid) {
        return null;
    }

    /**
     * Exists tells whether the BlockStore with given id exists
     */
    public Boolean exists(String ledgerid) {
        return null;
    }

    /**
     * List lists the ids of the existing ledgers
     */
    public String[] list() {
        return null;
    }

    /**
     * Close closes the FsBlockstoreProvider
     */
    void close() {

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

    public Provider getLeveldbProvider() {
        return leveldbProvider;
    }

    public void setLeveldbProvider(Provider leveldbProvider) {
        this.leveldbProvider = leveldbProvider;
    }
}
