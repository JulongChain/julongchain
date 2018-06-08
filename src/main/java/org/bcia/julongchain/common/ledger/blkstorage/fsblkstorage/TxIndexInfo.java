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
package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

/**
 * 封装交易索引信息
 * 包含交易ID、交易位置
 *
 * @author sunzongyu
 * @date 2018/04/12
 * @company Dingxuan
 */
public class TxIndexInfo {

    private String txID;
    private LocPointer loc;

    public TxIndexInfo() {
    }

    public TxIndexInfo(String txID, LocPointer loc) {
        this.txID = txID;
        this.loc = loc;
    }

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public LocPointer getLoc() {
        return loc;
    }

    public void setLoc(LocPointer loc) {
        this.loc = loc;
    }
}
