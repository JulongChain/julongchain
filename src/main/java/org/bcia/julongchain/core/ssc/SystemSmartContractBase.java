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
package org.bcia.julongchain.core.ssc;

import org.bcia.julongchain.core.smartcontract.shim.SmartContractBase;

/**
 * 系统智能合约的抽象类
 *
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
public abstract  class SystemSmartContractBase extends SmartContractBase implements ISystemSmartContract {
    private SystemSmartContractDescriptor descriptor;
    public String getSmartContractStrDescription() {
        return "系统智能合约";
    }
    public void setSystemSmartContractDescriptor(SystemSmartContractDescriptor descriptor) {
        this.id=descriptor.getSSCName();
        this.descriptor=descriptor;
    }

    public SystemSmartContractDescriptor getSystemSmartContractDescriptor() {
        return this.descriptor;
    }



}
