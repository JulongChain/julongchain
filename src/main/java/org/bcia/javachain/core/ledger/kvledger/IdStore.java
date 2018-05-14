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
package org.bcia.javachain.core.ledger.kvledger;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.DBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.protos.common.Common;

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

    private DBProvider provider = null;
    private static final byte[] UNDER_CONSTRUCTION_LEDGER_KEY = "underConstructionLedgerKey".getBytes();
    private static final byte[] LEDGER_KEY_PREFIX = "l".getBytes();

    /**
     * 开启idStore
     */
    public static IdStore openIDStore() throws LedgerException{
        IdStore idStore = new IdStore();
        String dbPath = LedgerConfig.getLedgerProviderPath();
        idStore.setProvider(LevelDBProvider.newProvider(dbPath));
        logger.debug("Create idstore using path = " + idStore.getProvider().getDbPath());
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
        byte[] val = null;
        val = provider.get(key);
        if(val != null && val.length == 0){
            throw new LedgerException("Ledger is already exists!");
        }
        val = new byte[0];
        UpdateBatch batch = LevelDBProvider.newUpdateBatch();
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
        if(val != null && val.length == 0){
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
     * 创建完成后, 删除创建标记以及保存的区块信息
     */
    public void createdLedgerID(String ledgerID) throws LedgerException {
        byte[] key = encodeLedgerKey(ledgerID);
        if(provider.get(key) == null){
            throw new LedgerException("Ledger id" + ledgerID + " is not creating");
        }
        byte[] val = new byte[0];
        UpdateBatch batch = LevelDBProvider.newUpdateBatch();
        batch.put(key, val);
        batch.delete(UNDER_CONSTRUCTION_LEDGER_KEY);
        provider.writeBatch(batch, true);
    }

    /**
     * 获取未完成创建的ledger的block
     */
    public Common.Block getCreatingBlock(String ledgerID) throws LedgerException {
        try {
            byte[] key = encodeLedgerKey(ledgerID);
            byte[] val = provider.get(key);
            if(val != null && val.length == 0){
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
        List<String> ids = new ArrayList<>();
        Iterator<Map.Entry<byte[], byte[]>> itr = provider.getIterator(null);
        while(itr.hasNext()){
            Map.Entry<byte[], byte[]> entry = itr.next();
            if(Arrays.equals(entry.getKey(), UNDER_CONSTRUCTION_LEDGER_KEY)){
                continue;
            }
            ids.add(decodeLedgerID(entry.getKey()));
        }
        return ids;
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
    public byte[] encodeLedgerKey(String ledgerID){
        return ArrayUtils.addAll(LEDGER_KEY_PREFIX, ledgerID.getBytes());
    }

    /**
     * 删除key的LedgerKeyPrefix前缀
     */
    public String decodeLedgerID(byte[] key){
        String keyStr = new String(key);
        return keyStr.substring(1, key.length);
    }

    public DBProvider getProvider() {
        return provider;
    }

    public void setProvider(DBProvider provider) {
        this.provider = provider;
    }

}
