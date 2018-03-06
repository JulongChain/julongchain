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
package org.bcia.javachain.common.ledger;

/**
 * BlockchainInfo
 * Contains information about the blockchain ledger such as height, current
 * block hash, and previous block hash.
 * @author wanliangbing
 * @date 2018/3/2
 * @company Dingxuan
 */
public class BlockchainInfo {

    /** current block height */
    private Integer height;

    /** current block hash */
    private byte[] currentBlockHash;

    /** previous block hash */
    private byte[] previousBlockHash;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public byte[] getCurrentBlockHash() {
        return currentBlockHash;
    }

    public void setCurrentBlockHash(byte[] currentBlockHash) {
        this.currentBlockHash = currentBlockHash;
    }

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(byte[] previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

}
