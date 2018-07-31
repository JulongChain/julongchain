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
package org.bcia.julongchain.core.ledger.leveldb;

import org.bcia.julongchain.common.exception.LevelDBException;

import java.io.File;
import java.nio.charset.Charset;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/28
 * @company Dingxuan
 */
public class StateLevelDBFactory {

    private static final String ROOT_PATH = File.separator + "var" + File.separator + "hyperledger" + File.separator + "production" + File.separator + "ledgersData" + File.separator + "stateLeveldb";

    public static byte[] getState(String smartContractId, String key) throws LevelDBException {
        LevelDB db = LevelDBUtil.getDB(ROOT_PATH);
        return LevelDBUtil.get(db, newKey(smartContractId, key).getBytes(Charset.forName("utf-8")), false);
    }

    public static void putState(String smartContractId, String key, byte[] value) throws LevelDBException {
        LevelDB db = LevelDBUtil.getDB(ROOT_PATH);
        LevelDBUtil.add(db, newKey(smartContractId, key).getBytes(Charset.forName("utf-8")), value, true);
    }

    public static void deleteState(String smartContractId, String key) throws LevelDBException {
        LevelDB db = LevelDBUtil.getDB(ROOT_PATH);
        LevelDBUtil.delete(db, newKey(smartContractId, key).getBytes(Charset.forName("utf-8")), true);
    }

    public static String newKey(String smartContractId, String key) {
        return "smartContractId:" + smartContractId + ";key:" + key;
    }

}
