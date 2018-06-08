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
package org.bcia.julongchain.core.ledger;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;

/**
 * 模拟结果
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class TxSimulationResults {
    private Rwset.TxReadWriteSet publicReadWriteSet;
    private Rwset.TxPvtReadWriteSet privateReadWriteSet;
    private long blockHeight;

    public TxSimulationResults() {
    }

    public TxSimulationResults(Rwset.TxReadWriteSet publicReadWriteSet, Rwset.TxPvtReadWriteSet privateReadWriteSet, long blockHeight) {
        this.publicReadWriteSet = publicReadWriteSet;
        this.privateReadWriteSet = privateReadWriteSet;
        this.blockHeight = blockHeight;
    }

    public Rwset.TxReadWriteSet getPublicReadWriteSet() {
        return publicReadWriteSet;
    }

    public void setPublicReadWriteSet(Rwset.TxReadWriteSet publicReadWriteSet) {
        this.publicReadWriteSet = publicReadWriteSet;
    }

    public Rwset.TxPvtReadWriteSet getPrivateReadWriteSet() {
        return privateReadWriteSet;
    }

    public void setPrivateReadWriteSet(Rwset.TxPvtReadWriteSet privateReadWriteSet) {
        this.privateReadWriteSet = privateReadWriteSet;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    /**
     * 获取序列化公共读写集
     */
    public ByteString getPubReadWriteByteString(){
        return publicReadWriteSet.toByteString();
    }

    /**
     * 获取序列化私有读写集
     */
    public ByteString getPrivateReadWriteByteString(){
        if (containsPvtWrites()) {
        	return null;
        } else {
            return privateReadWriteSet.toByteString();
        }
    }

    /**
     * 判断是否存在PvtWrites
     */
     public boolean containsPvtWrites(){
        return privateReadWriteSet != null;
     }
}

