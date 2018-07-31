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
package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStoreProvider;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.util.List;

/**
 * 操作blockchain文件系统类
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class FsBlockStoreProvider implements IBlockStoreProvider {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(FsBlockStoreProvider.class);

    private IndexConfig indexConfig;
    private IDBProvider leveldbProvider;
    private Config config;

    /**
     * 创建文件系统操作类
     */
    public FsBlockStoreProvider(Config config, IndexConfig indexConfig) throws LedgerException {
        this.indexConfig = indexConfig;
        this.leveldbProvider = new LevelDBProvider(config.getIndexDir());
        this.config = config;
        logger.debug("Creating fsBlockStore using path = " + config.getChainsDir());
    }

    /**
     * 创建(打开)一个文件系统
     */
    @Override
    public IBlockStore createBlockStore(String ledgerID) throws LedgerException {
        return openBlockStore(ledgerID);
    }

    /**
     * 打开一个文件系统,不存在则会创建.
     * 仅调用一次
     */
    @Override
    public IBlockStore openBlockStore(String ledgerID) throws LedgerException {
        return FsBlockStore.newFsBlockStore(ledgerID, config, indexConfig, ((LevelDBProvider) leveldbProvider).getDBHandle(ledgerID));
    }

    /**
     * 给出的ledgerid是否存在文件系统
     */
    @Override
    public Boolean exists(String ledgerID) {
         return IoUtil.fileExists(config.getLedgerBlockDir(ledgerID)) >= 0;
    }

    /**
     * 列出存在的文件系统id
    */
    @Override
    public List<String> list() {
        return IoUtil.listSubdirs(config.getChainsDir());
    }

    /**
     * 关闭文件系统
     */
    @Override
    public void close() throws LedgerException {
        leveldbProvider.close();
    }

    public IndexConfig getIndexConfig() {
        return indexConfig;
    }

    public void setIndexConfig(IndexConfig indexConfig) {
        this.indexConfig = indexConfig;
    }

    public IDBProvider getLeveldbProvider() {
        return leveldbProvider;
    }

    public void setLeveldbProvider(IDBProvider leveldbProvider) {
        this.leveldbProvider = leveldbProvider;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
