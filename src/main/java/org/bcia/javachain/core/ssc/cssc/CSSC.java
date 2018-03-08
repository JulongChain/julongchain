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
package org.bcia.javachain.core.ssc.cssc;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.springframework.stereotype.Component;

/**
 * 配置系统智能合约　Configure System Smart Contract,CSSC
 *
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */

@Component
public class CSSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CSSC.class);

    public CSSC(){
        log.debug("Construct CSSC");
    }

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
        return "与配置相关的系统智能合约";
    }
}
