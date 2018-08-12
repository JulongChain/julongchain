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

import java.io.File;

/**
 * 封装区块文件及索引数据库位置
 *
 * @author sunzongyu
 * @date 2018/4/7
 * @company Dingxuan
 */
public class Config {
    public static final String CHAINS_DIR = "chains";
    public static final String INDEX_DIR = "index";
    public static final int DEFAULT_MAX_BLOCKFILE_SIZE = 64 * 1024 * 1024;

    private String blockStorageDir;
    private int maxBlockFileSize;

    public Config(){}

    public Config(String blockStorageDir, int maxBlockFileSize){
        this.blockStorageDir = blockStorageDir;
        this.maxBlockFileSize = maxBlockFileSize <=0 ? DEFAULT_MAX_BLOCKFILE_SIZE : maxBlockFileSize;
    }

    public String getIndexDir(){
        return blockStorageDir + File.separator + INDEX_DIR;
    }

    public String getChainsDir(){
        return blockStorageDir + File.separator + CHAINS_DIR;
    }

    public String getLedgerBlockDir(String ledgerID){
        return getChainsDir() + File.separator + ledgerID;
    }

    public String getBlockStorageDir() {
        return blockStorageDir;
    }

    public void setBlockStorageDir(String blockStorageDir) {
        this.blockStorageDir = blockStorageDir;
    }

    public int getMaxBlockFileSize() {
        return maxBlockFileSize;
    }

    public void setMaxBlockFileSize(int maxBlockFileSize) {
        this.maxBlockFileSize = maxBlockFileSize;
    }
}
