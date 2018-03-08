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
package org.bcia.javachain.core.ssc.vssc;

import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.core.ssc.SystemSmartContractDescriptor;
import org.springframework.stereotype.Component;

/**
 * 验证系统智能合约　Validator System Smart Contract,VSSC
 *
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class VSSC extends SystemSmartContractBase {

    @Override
    public Response init(ISmartContractStub stub) {
        return null;
    }

    @Override
    public Response invoke(ISmartContractStub stub) {
        return null;
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与验证相关的系统智能合约";
    }
}
