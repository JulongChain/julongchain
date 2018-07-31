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
package org.bcia.julongchain.core.commiter;

import org.bcia.julongchain.core.common.sysscprovider.SmartContractInstance;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public class BlockValidationResult {
    private int txIndex;
    private TransactionPackage.TxValidationCode txValidationCode;
    private SmartContractInstance smartContractInstance;
    private SmartContractInstance smartContractUpdateInstance;
    private String txId;

    public int getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(int txIndex) {
        this.txIndex = txIndex;
    }

    public TransactionPackage.TxValidationCode getTxValidationCode() {
        return txValidationCode;
    }

    public void setTxValidationCode(TransactionPackage.TxValidationCode txValidationCode) {
        this.txValidationCode = txValidationCode;
    }

    public SmartContractInstance getSmartContractInstance() {
        return smartContractInstance;
    }

    public void setSmartContractInstance(SmartContractInstance smartContractInstance) {
        this.smartContractInstance = smartContractInstance;
    }

    public SmartContractInstance getSmartContractUpdateInstance() {
        return smartContractUpdateInstance;
    }

    public void setSmartContractUpdateInstance(SmartContractInstance smartContractUpdateInstance) {
        this.smartContractUpdateInstance = smartContractUpdateInstance;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
