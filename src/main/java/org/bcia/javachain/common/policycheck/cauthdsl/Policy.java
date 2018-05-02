/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.javachain.common.policycheck.cauthdsl;

import org.apache.commons.logging.Log;
import org.apache.zookeeper.proto.ErrorResponse;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.msp.mgmt.MspManager;

import java.util.List;


/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class Policy implements IPolicy{

    private static JavaChainLog log = JavaChainLogFactory.getLog(Policy.class);
    @Override
    public void evaluate(List<SignedData> signatureList) throws PolicyException {
        List<SignedData> signedDatas = Cauthdsl.deduplicate(signatureList);   //删除重复身份
        Msp msp = new Msp();
        MspManager mspManager = new MspManager();
        try {
            boolean b = Cauthdsl.compile(signedDatas,mspManager,msp);                                        //评估这组签名是否满足策略
        }catch (Exception e){
            log.info("Failed to authenticate policy");
            throw new PolicyException(e);
        }

    }
}
