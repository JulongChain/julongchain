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
package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.BlockFileManager;
import org.bcia.julongchain.core.ledger.util.Util;

/**
 * 提供构造HistoryDB key的方法
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class HistoryDBHelper {
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};
    private static final byte[] COMPOSITE_END_KEY = {Byte.MAX_VALUE};

    /**
     * 将namespace, key, blockNum, tranNum组装为HistoryDB key
     */
    public static byte[] constructCompositeHistoryKey(String ns, String key, long blocNum, long tranNum){
        byte[] compositeKey = ns.getBytes();
        compositeKey = ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        compositeKey = ArrayUtils.addAll(compositeKey, key.getBytes());
        compositeKey = ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        compositeKey = ArrayUtils.addAll(compositeKey, Util.longToBytes(blocNum, BlockFileManager.PEEK_BYTES_LEN));
        compositeKey = ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        compositeKey = ArrayUtils.addAll(compositeKey, Util.longToBytes(tranNum, BlockFileManager.PEEK_BYTES_LEN));
        return compositeKey;
    }

    /**
     * 将namespace, key组装为查询HistoryDB的StartKey
     * 将namespace, key, {0xff}组装为查询HistoryDB的StartKey
     */
    public static byte[] constructPartialCompositeHistoryKey(String ns, String key, boolean endKey){
        byte[] compositeKey = ns.getBytes();
        compositeKey = ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        compositeKey = ArrayUtils.addAll(compositeKey, key.getBytes());
        if(endKey){
            compositeKey = ArrayUtils.addAll(compositeKey, COMPOSITE_END_KEY);
        } else {
	        compositeKey = ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        }
        return compositeKey;
    }

	public static byte[] removeLedgerIDFromHistoryKey(String ledgerID, byte[] historykey){
		byte[] ledgerIDBytes = ledgerID.getBytes();
		byte[] reuslt = new byte[historykey.length - 1 - ledgerIDBytes.length];
		System.arraycopy(historykey, ledgerIDBytes.length + 1, reuslt, 0, reuslt.length);
		String s = new String(reuslt);
		return reuslt;
	}

    /**
     * 解析HistoryDB key中的blockNum
     * @param byteToSplit 将要解析的bytes
     * @return blockNum
     */
    public static long splitCompositeHistoryKeyForBlockNum(byte[] byteToSplit){
    	int length = byteToSplit.length;
	    if (length <= 19) {
		    return -1;
	    }
        return Util.bytesToLong(byteToSplit, length - 17, BlockFileManager.PEEK_BYTES_LEN);
    }

    /**
     * 解析HistoryDB key中的tranNum
     * @param byteToSplit 将要解析的bytes
     * @return blockNum
     */
    public static long splitCompositeHistoryKeyForTranNum(byte[] byteToSplit){
	    int length = byteToSplit.length;
	    if (length <= 19) {
		    return -1;
	    }
        return Util.bytesToLong(byteToSplit, length - 8, BlockFileManager.PEEK_BYTES_LEN);
    }

    /**
     * 获取History数据头部
     */
    public static byte[] splitCompositeHistoryKeyForHeader(byte[] byteToSplit){
        if(byteToSplit.length <= 19){
            return null;
        }
        byte[] header = new byte[byteToSplit.length - 18];
        System.arraycopy(byteToSplit, 0, header, 0 , header.length);
        return header;
    }

    /**
     * 检查数据是否以制定字符数组起始
     * @param bytes 目标数组
     * @param start 起始数组
     */
    public static boolean checkStart(byte[] bytes, byte[] start){
        return new String(bytes).startsWith(new String(start));
    }
}
