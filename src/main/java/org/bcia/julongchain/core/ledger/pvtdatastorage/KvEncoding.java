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
package org.bcia.julongchain.core.ledger.pvtdatastorage;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.BlockFileManager;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.julongchain.core.ledger.util.Util;

/**
 * 序列化kv
 * 用于序列化pvtDataStore
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class KvEncoding  {
    public static final byte[] PENDING_COMMIT_KEY = {0};
    public static final byte[] LAST_COMMITTED_BLK_KEY = {1};
    public static final byte[] PVT_DATA_KEY_PREFIX = {2};
    public static final byte[] EMPTY_VALUE = {3};

    public static byte[] encodePK(long blockNum, long tranNum) {
        return  ArrayUtils.addAll(PVT_DATA_KEY_PREFIX, new Height(blockNum, tranNum).toBytes());
    }

    public static long decodePKToBlockNum(byte[] key) {
        byte[] heightBytes = new byte[16];
        System.arraycopy(key, 1, heightBytes, 0, 16);
        Height height = new Height(heightBytes);
        return height.getBlockNum();
    }

    public static long decodePKToTranNum(byte[] key) {
        byte[] heightBytes = new byte[16];
        System.arraycopy(key, 1, heightBytes, 0, 16);
        Height height = new Height(heightBytes);
        return height.getTxNum();
    }

    public static byte[] getStartKeyForRangeScanByBlockNum(long blockNum){
        return encodePK(blockNum, 0);
    }

    public static byte[] getEndKeyForRangeScanByBlockNum(long blockNum){
        return encodePK(blockNum, Long.MAX_VALUE);
    }

    public static byte[] encodeBlockNum(long blockNum){
        return Util.longToBytes(blockNum, BlockFileManager.PEEK_BYTES_LEN);
    }

    public static long decodeBlockNum(byte[] blockNumBytes){
        return Util.bytesToLong(blockNumBytes, 0, BlockFileManager.PEEK_BYTES_LEN);
    }

    public static byte[] getPendingCommitKey(String ledgerId){
        return ArrayUtils.addAll(PENDING_COMMIT_KEY, ledgerId.getBytes());
    }

    public static byte[] getLastCommittedBlkKey(String ledgerId){
        return ArrayUtils.addAll(LAST_COMMITTED_BLK_KEY, ledgerId.getBytes());
    }
}
