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
package org.bcia.javachain.examples.smartcontract.java.smartcontract_example02;

import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContract;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/29/18
 * @company Dingxuan
 */
public class Example02 implements ISmartContract{
    @Override
    public Response init(ISmartContractStub stub) {
        return null;
    }

    @Override
    public Response invoke(ISmartContractStub stub) {
        return null;
    }

    @Override
    public String getSmartContractID() {
        return "Example02";
    }

    @Override
    public String getSmartContractStrDescription() {
        return "An Example";
    }
}
