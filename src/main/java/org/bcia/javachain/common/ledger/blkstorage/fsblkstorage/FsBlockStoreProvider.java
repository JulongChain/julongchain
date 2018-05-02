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
import org.bcia.javachain.common.ledger.util.DBProvider;
import org.bcia.javachain.common.ledger.util.IoUtil;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

/**
 * 操作blockchain文件系统类
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class FsBlockStoreProvider implements BlockStoreProvider {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(FsBlockStoreProvider.class);

    private Conf conf;
    private IndexConfig indexConfig;
    private DBProvider leveldbProvider;

    /**
     * 创建文件系统操作类
     */
    public static BlockStoreProvider newProvider(Conf conf, IndexConfig indexConfig) throws LedgerException {
        FsBlockStoreProvider provider = new FsBlockStoreProvider();
        provider.setLeveldbProvider(LevelDBProvider.newProvider(conf.getIndexDir()));
        provider.setConf(conf);
        provider.setIndexConfig(indexConfig);
        logger.debug("Createing fsBlockStore using path = " + conf.getChainsDir());
        return provider;
    }

    /**
     * 创建(打开)一个文件系统
     */
    @Override
    public BlockStore createBlockStore(String ledgerid) throws LedgerException {
        return openBlockStore(ledgerid);
    }

    /**
     * 打开一个文件系统,不存在则会创建.
     * 仅调用一次
     */
    @Override
    public BlockStore openBlockStore(String ledgerid) throws LedgerException {
        return FsBlockStore.newFsBlockStore(ledgerid, conf, indexConfig, leveldbProvider);
    }

    /**
     * 给出的ledgerid是否存在文件系统
     */
    @Override
    public Boolean exists(String ledgerid) {
         return IoUtil.fileExists(conf.getLedgerBlockDir(ledgerid)) >= 0;
    }

    /**
     * 列出存在的文件系统id
    */
    @Override
    public String[] list() {
        return IoUtil.listSubdirs(conf.getChainsDir());
    }

    /**
     * 关闭文件系统
     */
    @Override
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

    public DBProvider getLeveldbProvider() {
        return leveldbProvider;
    }

    public void setLeveldbProvider(DBProvider leveldbProvider) {
        this.leveldbProvider = leveldbProvider;
    }
}
