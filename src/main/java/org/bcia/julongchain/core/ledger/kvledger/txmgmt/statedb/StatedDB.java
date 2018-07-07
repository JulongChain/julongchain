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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.NsIterator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.NsUpdates;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.UpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class StatedDB {

    public static NsUpdates newNsUpdates() {
        return null;
    }

    /** NewUpdateBatch constructs an instance of a Batch
     *
     * @return
     */
    public static UpdateBatch newUpdateBatch() {
        return null;
    }

    public static NsIterator newNsIterator(String ns, String startKey, String endKey, UpdateBatch batch) {
        return null;
    }

    public static byte[] encodeValue(byte[] value, LedgerHeight version){
        byte[] encodeValue = version.toBytes();
        if(value != null){
            encodeValue = ArrayUtils.addAll(encodeValue, value);
        }
        return encodeValue;
    }

    public static LedgerHeight decodeValueToHeight(byte[] encodeValue){
        return new LedgerHeight(encodeValue);
    }
    public static byte[] decodeValueToBytes(byte[] encodeValue){
        byte[] result = new byte[encodeValue.length - 16];
        System.arraycopy(encodeValue, 16, result, 0, result.length);
        return result;
    }

}
