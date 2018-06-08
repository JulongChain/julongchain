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
package org.bcia.javachain.core.ssc.lssc;

import org.bcia.javachain.protos.node.SmartContractDataPackage;
import org.bcia.javachain.protos.node.Smartcontract;

/**
 * 智能合约代码的描述及数据
 *
 * @author sunianle
 * @date 4/24/18
 * @company Dingxuan
 */
public class SmartContractCode {
    SmartContractDataPackage.SmartContractData smartcontractData;
    Smartcontract.SmartContractDeploymentSpec depSpec;
    byte[] depSpecBytes;

    public SmartContractCode(SmartContractDataPackage.SmartContractData smartcontractData, Smartcontract.SmartContractDeploymentSpec depSpec, byte[] depSpecBytes) {
        this.smartcontractData = smartcontractData;
        this.depSpec = depSpec;
        this.depSpecBytes = depSpecBytes;
    }

    public SmartContractDataPackage.SmartContractData getSmartcontractData() {
        return smartcontractData;
    }

    public void setSmartcontractData(SmartContractDataPackage.SmartContractData smartcontractData) {
        this.smartcontractData = smartcontractData;
    }

    public Smartcontract.SmartContractDeploymentSpec getDepSpec() {
        return depSpec;
    }

    public void setDepSpec(Smartcontract.SmartContractDeploymentSpec depSpec) {
        this.depSpec = depSpec;
    }

    public byte[] getDepSpecBytes() {
        return depSpecBytes;
    }

    public void setDepSpecBytes(byte[] depSpecBytes) {
        this.depSpecBytes = depSpecBytes;
    }
}
