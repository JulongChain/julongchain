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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.version;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.core.ledger.util.Util;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class Height {

    private long blockNum;
    private long txNum;

    public long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(long blockNum) {
        this.blockNum = blockNum;
    }

    public long getTxNum() {
        return txNum;
    }

    public void setTxNum(long txNum) {
        this.txNum = txNum;
    }

    public static Height newHeight(long blockNum, long txNum){
        Height height = new Height();
        height.setTxNum(txNum);
        height.setBlockNum(blockNum);
        return height;
    }

    public static Height newHeightFromBytes(byte[] b){
        long blockNum = Util.bytesToLong(b, 0, 8);
        long txNum = Util.bytesToLong(b, 8, 8);
        return newHeight(blockNum, txNum);
    }

    public byte[] toBytes(){
        byte[] blockNumBytes = Util.longToBytes(blockNum, 8);
        byte[] txNumBytes = Util.longToBytes(txNum, 8);
        return ArrayUtils.addAll(blockNumBytes, txNumBytes);
    }

    public int compare(Height h){
        int res = 0;
        if(this.blockNum != h.blockNum){
            res = (int) (this.blockNum - h.blockNum);
        } else if(this.txNum != h.txNum) {
            res = (int) (this.txNum - h.txNum);
        } else {
            return 0;
        }
        return res > 0 ? 1 : -1;
    }

    public static boolean areSame(Height h1, Height h2){
        if(h1 == null){
            return h2 == null;
        }
        if(h2 == null){
            return false;
        }
        return h1.compare(h2) == 0;
    }
}
