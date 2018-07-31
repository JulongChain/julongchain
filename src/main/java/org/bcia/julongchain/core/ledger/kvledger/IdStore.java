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
package org.bcia.julongchain.core.ledger.kvledger;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.util.IDBProvider;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.julongchain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.common.Common;

import java.util.*;

/**
 * 存储创建的账本ID
 * 使用ConstructionFlag标记账本是否正在建立
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public class IdStore {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(IdStore.class);

    private IDBProvider provider = null;
	private static final byte[] TIME_PREFIX= new byte[]{0};
    private static final byte[] UNDER_CONSTRUCTION_LEDGER_KEY = "underConstructionLedgerKey".getBytes();
    private static final byte[] LEDGER_KEY_PREFIX = "l".getBytes();

    /**
     * 开启idStore
     */
    public static IdStore openIDStore() throws LedgerException{
        IdStore idStore = new IdStore();
        String dbPath = LedgerConfig.getLedgerProviderPath();
        idStore.setProvider(new LevelDBProvider(dbPath));
        logger.debug("Create idstore using path = " + idStore.getProvider().getDBPath());
        return idStore;
    }

    /**
     * 为ledgerID设置ConstructionFlag
     */
    public void setUnderConstructionFlag(String ledgerID) throws LedgerException {
        provider.put(UNDER_CONSTRUCTION_LEDGER_KEY, ledgerID.getBytes(), true);
    }

    /**
     * 为ledgerID删除ConstructionFlag
     */
    public void unsetUnderConstructionFlag() throws LedgerException {
        provider.delete(UNDER_CONSTRUCTION_LEDGER_KEY, true);
    }

    /**
     * 获取ledgerID的ConstructionFlag
     */
    public String getUnderConstructionFlag() throws LedgerException {
        byte[] value = provider.get(UNDER_CONSTRUCTION_LEDGER_KEY);
        return value == null ? null : new String(value);
    }

    /**
     * 创建账本索引
     */
    public void createLedgerID(String ledgerID) throws LedgerException {
        byte[] key = encodeLedgerKey(ledgerID);
        byte[] val = provider.get(key);
	    if(val != null && isTime(val)){
            throw new LedgerException("Ledger is already exists!");
        }
	    // TODO: 6/13/18 保存账本创建时间
	    // TODO: 6/13/18 时间戳的获取，暂时先用系统时间代替
        val = encodeTime();
        UpdateBatch batch = new UpdateBatch();
        batch.put(key, val);
        batch.delete(UNDER_CONSTRUCTION_LEDGER_KEY);
        provider.writeBatch(batch, true);
    }

    /**
     * 开始创建账本, 将block完整存入idstore, 以备恢复使用
     */
    public void creatingLedgerID(String ledgerID, Common.Block gb) throws LedgerException {
        byte[] key = encodeLedgerKey(ledgerID);
        byte[] val = provider.get(key);
        if(val != null){
            throw new LedgerException("Ledger is already exists!");
        }
        try {
            val = gb.toByteArray();
        } catch (Exception e) {
            throw new LedgerException(e);
        }
        provider.put(key, val, true);
    }

    /**
     * 获取未完成创建的ledger的block
     */
    public Common.Block getCreatingBlock(String ledgerID) throws LedgerException {
        try {
            byte[] key = encodeLedgerKey(ledgerID);
            byte[] val = provider.get(key);
            if(val == null){
                logger.debug("NO CREATING LEDGER EXISTS");
                return null;
            }
            return Common.Block.parseFrom(provider.get(key));
        } catch (InvalidProtocolBufferException e) {
            throw new LedgerException(e);
        }
    }


    /**
     * 判断账本是否存在
     */
    public boolean ledgerIDExists(String ledgerID) throws LedgerException{
        byte[] key = encodeLedgerKey(ledgerID);
        byte[] val = provider.get(key);
        return val != null;
    }

    /**
     * 迭代获取全部账本id
     */
    public List<String> getAllLedgerIDs() throws LedgerException {
        Map<Long, String> ids = new TreeMap<>();
        Iterator<Map.Entry<byte[], byte[]>> itr = provider.getIterator(null);
        while(itr.hasNext()){
            Map.Entry<byte[], byte[]> entry = itr.next();
	        if(Arrays.equals(entry.getKey(), UNDER_CONSTRUCTION_LEDGER_KEY)){
		        continue;
	        }
            String ledgerID = decodeLedgerID(entry.getKey());
            Long timeStamp  = decodeTime(entry.getValue());
            ids.put(timeStamp, ledgerID);
        }
        return new ArrayList<>(ids.values());
    }

    /**
     * 关闭数据库
     */
    public void close() throws LedgerException {
        provider.close();
    }

    /**
     * 为ledgerID添加LedgerKeyPrefix前缀
     */
    private byte[] encodeLedgerKey(String ledgerID){
        return ArrayUtils.addAll(LEDGER_KEY_PREFIX, ledgerID.getBytes());
    }

    /**
     * 删除key的LedgerKeyPrefix前缀
     */
    private String decodeLedgerID(byte[] key){
        String keyStr = new String(key);
        return keyStr.substring(1, key.length);
    }

	/**
	 * 添加时间前缀
	 */
	private byte[] encodeTime(){
    	return ArrayUtils.addAll(TIME_PREFIX, Util.longToBytes(System.currentTimeMillis(), 8));
    }

	/**
	 * 解析时间
	 */
	private long decodeTime(byte[] encodeTime){
		return Util.bytesToLong(encodeTime, 1, 8);
    }

	/**
	 * 判断是否为编码后的时间
	 */
	private boolean isTime(byte[] b){
		if(b.length == 0){
			return false;
		}
		return b[0] == (byte) 0 && b.length == 9;
    }

    public IDBProvider getProvider() {
        return provider;
    }

    public void setProvider(IDBProvider provider) {
        this.provider = provider;
    }
}
