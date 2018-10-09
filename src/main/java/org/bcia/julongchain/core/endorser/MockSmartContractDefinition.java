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
package org.bcia.julongchain.core.endorser;

import org.bcia.julongchain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.julongchain.common.resourceconfig.Validation;
import org.bcia.julongchain.common.util.CommConstant;

/**
 * 模拟智能合约定义
 *
 * @author zhouhui
 * @date 2018/3/21
 * @company Dingxuan
 */
public class MockSmartContractDefinition implements ISmartContractDefinition {
    @Override
    public String getSmartContractName() {
        return null;
    }

    @Override
    public byte[] hash() {
        return new byte[0];
    }

    @Override
    public String getSmartContractVersion() {
        return "1.0";
    }

    @Override
    public Validation getValidation() {
        return null;
    }

    @Override
    public String getEndorsement() {
        return CommConstant.ESSC;
    }
}
