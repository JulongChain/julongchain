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

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.util.DBProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDbProvider;
import org.bcia.javachain.common.ledger.util.leveldbhelper.UpdateBatch;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.csp.gmt0016.excelsecu.bean.Version;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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
        idStore.setProvider(LevelDbProvider.newProvider(dbPath));
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
    public void createLedgerID(String ledgerID, Common.Block gb) throws LedgerException {
        byte[] key = encodeLedgerKey(ledgerID);
        byte[] val = null;
        try {
            val = gb.toByteArray();
        } catch (Exception e) {
            throw new LedgerException(e);
        }
        val = provider.get(key);
        if(val != null && val.length != 0){
            throw new LedgerException("Ledger is already exists!");
        }
        val = new byte[0];
        UpdateBatch batch = LevelDbProvider.newUpdateBatch();
        batch.put(key, val);
        batch.delete(UNDER_CONSTRUCTION_LEDGER_KEY);
        provider.writeBatch(batch, true);
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
        //TODO
        Iterator<Map.Entry<byte[], byte[]>> itr = provider.getIterator(null);
        while(itr.hasNext()){
            Map.Entry<byte[], byte[]> entry = itr.next();
            if(entry.getKey().equals(UNDER_CONSTRUCTION_LEDGER_KEY)){
                continue;
            }
            String id = new String(entry.getKey());
            ids.add(id);
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
