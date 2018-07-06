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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.version;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.BlockFileManager;
import org.bcia.julongchain.core.ledger.util.Util;

/**
 * 封装版本信息
 * 版本既区块号+交易号
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class LedgerHeight implements Comparable<LedgerHeight> {

    private long blockNum;
    private long txNum;

    public LedgerHeight(long blockNum, long txNum) {
        this.blockNum = blockNum;
        this.txNum = txNum;
    }

    public LedgerHeight(byte[] b){
        this.blockNum = Util.bytesToLong(b, 0, BlockFileManager.PEEK_BYTES_LEN);
        this.txNum = Util.bytesToLong(b, 8, BlockFileManager.PEEK_BYTES_LEN);
    }

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

    public byte[] toBytes(){
        byte[] blockNumBytes = Util.longToBytes(blockNum, BlockFileManager.PEEK_BYTES_LEN);
        byte[] txNumBytes = Util.longToBytes(txNum, BlockFileManager.PEEK_BYTES_LEN);
        return ArrayUtils.addAll(blockNumBytes, txNumBytes);
    }

    @Override
    public int compareTo(LedgerHeight h){
        int res;
        if(this.blockNum != h.blockNum){
            res = (int) (this.blockNum - h.blockNum);
        } else if(this.txNum != h.txNum) {
            res = (int) (this.txNum - h.txNum);
        } else {
            return 0;
        }
        return res > 0 ? 1 : -1;
    }

    public static boolean areSame(LedgerHeight h1, LedgerHeight h2){
        if(h1 == null){
            return h2 == null;
        }
        if(h2 == null){
            return false;
        }
        return h1.compareTo(h2) == 0;
    }
}
