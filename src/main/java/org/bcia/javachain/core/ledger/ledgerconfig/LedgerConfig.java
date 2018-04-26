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
package org.bcia.javachain.core.ledger.ledgerconfig;


import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Config;

import java.io.File;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/23
 * @company Dingxuan
 */
public class LedgerConfig {
    private static final String CONF_PEER_FILE_SYSTEM_PATH = "peer.fileSystemPath";
    private static final String CONF_LEDGERS_DATA = "ledgersData";
    private static final String CONF_LEDGER_PROVIDER = "ledgerProvider";
    private static final String CONF_STATE_LEVELDB = "stateLeveldb";
    private static final String CONF_HISTORY_LEVELDB = "historyLeveldb";
    private static final String CONF_PVT_WRITESET_STORE = "pvtWritesetStore";
    private static final String CONF_CHAINS = "chains";
    private static final String CONF_PVTDATA_STORE = "pvtdataStore";
    private static final String CONF_QUERY_LIMIT = "ledger.state.couchDBconfig.queryLimit";
    private static final String CONF_ENABLE_HISTORY_DATABASE = "ledger.history.enableHistoryDatabase";
    private static final String CONF_MAX_BATCH_SIZE = "ledger.state.couchDBconfig.maxBatchUpdateSize";
    private static final String CONF_AUTO_WARM_INDEXES = "ledger.state.couchDBconfig.autoWarmIndexes";
    private static final String CONF_WARM_INDEXES_AFTER_N_BLOCKS = "ledger.state.couchDBConfig.warmIndexesAfterNBlocks";

    public static boolean isCouchDBEnable(){
        return false;
    }

    public static String getRootPath(){
        return join(Config.getPath(), CONF_LEDGERS_DATA);
    }

    public static String getLedgerProviderPath(){
        return join(getRootPath(), CONF_LEDGER_PROVIDER);
    }

    public static String getStateLevelDBPath(){
        return join(getRootPath(), CONF_STATE_LEVELDB);
    }

    public static String getHistoryLevelDBPath(){
        return join(getRootPath(), CONF_HISTORY_LEVELDB);
    }

    public static String getPvtWritesetStorePath(){
        return join(getRootPath(), CONF_PVT_WRITESET_STORE);
    }

    public static String getBlockStorePath(){
        return join(getRootPath(), CONF_CHAINS);
    }

    public static String getPvtDataStorePath(){
        return join(getRootPath(), CONF_PVTDATA_STORE);
    }

    public static int getMaxBlockfileSize(){
        return Config.DEFAULT_MAX_BLOCKFILE_SIZE;
    }

    public static int getMaxDegreeQueryReadsHashing(){
        return 50;
    }

    public static boolean isHistoryDBEnabled(){
        return true;
    }

    private static String join(String... itms){
        StringBuffer buffer = new StringBuffer("");
        for(String itm : itms){
            buffer.append(itm);
            buffer.append(File.separator);
        }
        String result = buffer.toString();
        return result.substring(0, result.length() - 1);
    }
}
