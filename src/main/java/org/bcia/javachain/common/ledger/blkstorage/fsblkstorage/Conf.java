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

import java.io.File;

/**
 * 封装区块文件信息
 *
 * @author
 * @date 2018/3/7
 * @company Dingxuan
 */
public class Conf {

    private String blockStorageDir;
    private Integer maxBlockfileSize;

    public String getIndexDir() {
        return blockStorageDir + File.separator + Config.INDEX_DIR;
    }

    public String getChainsDir() {
        return blockStorageDir + File.separator + Config.CHAINS_DIR;
    }

    public String getLedgerBlockDir(String ledgerid) {
        return getChainsDir() + File.separator + ledgerid;
    }

    public String getBlockStorageDir() {
        return blockStorageDir;
    }

    public void setBlockStorageDir(String blockStorageDir) {
        this.blockStorageDir = blockStorageDir;
    }

    public Integer getMaxBlockfileSize() {
        return maxBlockfileSize;
    }

    public void setMaxBlockfileSize(Integer maxBlockfileSize) {
        this.maxBlockfileSize = maxBlockfileSize;
    }
}
