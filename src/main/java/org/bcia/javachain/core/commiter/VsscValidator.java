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
package org.bcia.javachain.core.commiter;

import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.core.common.smartcontractprovider.ISmartContractProvider;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractProvider;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractProviderFactory;
import org.bcia.javachain.core.common.sysscprovider.ISystemSmartContractProvider;
import org.bcia.javachain.core.common.sysscprovider.SystemSmartContractProvider;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public class VsscValidator implements IVsscValidator {
    private ICommitterSupport committerSupport;
    private ISmartContractProvider smartContractProvider;
    private ISystemSmartContractProvider systemSmartContractProvider;

    public VsscValidator(ICommitterSupport committerSupport) {
        this.committerSupport = committerSupport;

        //TODO
//        this.smartContractProvider = new SmartContractProvider();
//        this.systemSmartContractProvider = new SystemSmartContractProvider();
    }

    @Override
    public TransactionPackage.TxValidationCode vsscValidateTx(Common.Payload payload, byte[] envBytes, Common.Envelope envelope) throws ValidateException {
        return null;
    }
}
