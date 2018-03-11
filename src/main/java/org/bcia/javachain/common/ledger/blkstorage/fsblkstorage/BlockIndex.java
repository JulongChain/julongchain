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

import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.leveldbhelper.DBHandle;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class BlockIndex {

    public static final String BLOCK_NUM_IDX_KEY_PREFIX           = "n";
    public static final String BLOCK_HASH_IDX_KEY_PREFIX          = "h";
    public static final String TX_ID_IDX_KEY_PREFIX               = "t";
    public static final String BLOCK_NUM_TRAN_NUM_IDX_KEY_PREFIX    = "a";
    public static final String BLOCK_TX_ID_IDX_KEY_PREFIX          = "b";
    public static final String TX_VALIDATION_RESULT_IDX_KEY_PREFIX = "v";
    public static final String INDEX_CHECK_POINT_KEY_STR          = "indexCheckpointKey";

    BlockIndex newBlockIndex(IndexConfig indexConfig, DBHandle db) {
        return null;
    }

    Long getLastBlockIndexed() {
        return null;
    }

    void indexBlock(BlockIdxInfo blockIdxInfo) {
        return;
    }

    FileLocPointer getBlockLocByHash(byte[] blockHash) {
        return null;
    }

    FileLocPointer getBlockLocByBlockNum(Long blockNum) {
        return null;
    }

    FileLocPointer getTxLoc(String txID) {
        return null;
    }

    FileLocPointer getBlockLocByTxID(String txID) {
        return null;
    }

    FileLocPointer getTXLocByBlockNumTranNum(Long blockNum, Long tranNum) {
        return null;
    }

    TransactionPackage.TxValidationCode getTxValidationCodeByTxID(String txID) {
        return null;
    }

    byte[] constructBlockNumKey(Long blockNum) {
        return null;
    }

    byte[] constructBlockHashKey(byte[] blockHash) {
        return null;
    }

    byte[] constructTxIDKey(String txID) {
        return null;
    }

    byte[] constructBlockTxIDKey(String txID) {
        return null;
    }

    byte[] constructTxValidationCodeIDKey(String txID) {
        return null;
    }

    byte[] constructBlockNumTranNumKey(Long blockNum, Long txNum) {
        return null;
    }

    byte[] encodeBlockNum(Long blockNum) {
        return null;
    }

    Long decodeBlockNum(byte[] blockNumBytes) {
        return null;
    }

    FileLocPointer newFileLocationPointer(Integer fileSuffixNum, Integer beginningOffset, LocPointer relativeLP) {
        return null;
    }

}
