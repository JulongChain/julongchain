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
package org.bcia.julongchain.common.ledger;

import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;

/**
 * 保存Ledger的相关配置
 *
 * @author sunzongyu
 * @date 2018/05/24
 * @company Dingxuan
 */
public class Config {
    private String ledgerRootPath;
    private String idStorePath;
    private String historyDBPath;
    private String versionedDBPath;
    private String pvtDataStorePath;
    private String indexPath;
    private String chainPath;
    private int maxBlockfileSize;

    public Config(){
        this.ledgerRootPath = LedgerConfig.getRootPath();
        this.idStorePath = LedgerConfig.getLedgerProviderPath();
        this.historyDBPath = LedgerConfig.getHistoryLevelDBPath();
        this.versionedDBPath = LedgerConfig.getStateLevelDBPath();
        this.pvtDataStorePath = LedgerConfig.getPvtDataStorePath();
        this.indexPath = LedgerConfig.getIndexPath();
        this.chainPath = LedgerConfig.getChainsPath();
        this.maxBlockfileSize = LedgerConfig.getMaxBlockfileSize();
    }

    public String getLedgerRootPath() {
        return ledgerRootPath;
    }

    public void setLedgerRootPath(String ledgerRootPath) {
        this.ledgerRootPath = ledgerRootPath;
    }

    public String getIdStorePath() {
        return idStorePath;
    }

    public void setIdStorePath(String idStorePath) {
        this.idStorePath = idStorePath;
    }

    public String getHistoryDBPath() {
        return historyDBPath;
    }

    public void setHistoryDBPath(String historyDBPath) {
        this.historyDBPath = historyDBPath;
    }

    public String getVersionedDBPath() {
        return versionedDBPath;
    }

    public void setVersionedDBPath(String versionedDBPath) {
        this.versionedDBPath = versionedDBPath;
    }

    public String getPvtDataStorePath() {
        return pvtDataStorePath;
    }

    public void setPvtDataStorePath(String pvtDataStorePath) {
        this.pvtDataStorePath = pvtDataStorePath;
    }

    public int getMaxBlockfileSize() {
        return maxBlockfileSize;
    }

    public void setMaxBlockfileSize(int maxBlockfileSize) {
        this.maxBlockfileSize = maxBlockfileSize;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public String getChainPath() {
        return chainPath;
    }

    public void setChainPath(String chainPath) {
        this.chainPath = chainPath;
    }
}
