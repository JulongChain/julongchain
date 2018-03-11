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

/**
 * 类描述
 *
 * @author
 * @date 2018/3/8
 * @company Dingxuan
 */
public class CheckpointInfo {

    private Integer lastestFileChunkSuffixNum;
    private Integer latestFileChunksize;
    private Boolean isChainEmpty;
    private Long lastBlockNumber;

    byte[] marshal() {
        return null;
    }

    void unmarshal(byte[] b) {
        return;
    }

    public Integer getLastestFileChunkSuffixNum() {
        return lastestFileChunkSuffixNum;
    }

    public void setLastestFileChunkSuffixNum(Integer lastestFileChunkSuffixNum) {
        this.lastestFileChunkSuffixNum = lastestFileChunkSuffixNum;
    }

    public Integer getLatestFileChunksize() {
        return latestFileChunksize;
    }

    public void setLatestFileChunksize(Integer latestFileChunksize) {
        this.latestFileChunksize = latestFileChunksize;
    }

    public Boolean getChainEmpty() {
        return isChainEmpty;
    }

    public void setChainEmpty(Boolean chainEmpty) {
        isChainEmpty = chainEmpty;
    }

    public Long getLastBlockNumber() {
        return lastBlockNumber;
    }

    public void setLastBlockNumber(Long lastBlockNumber) {
        this.lastBlockNumber = lastBlockNumber;
    }
}
