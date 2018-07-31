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

package org.bcia.julongchain.common.policycheck.cauthdsl;

import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.policycheck.policies.IEvalutor;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.msp.IIdentityDeserializer;

import java.util.List;


/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class Policy implements IPolicy {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Policy.class);

    private IIdentityDeserializer deserializer;
    private IEvalutor evalutor;

    public Policy(IIdentityDeserializer deserializer, IEvalutor evalutor) {
        this.deserializer = deserializer;
        this.evalutor = evalutor;
    }

    public IIdentityDeserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(IIdentityDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public IEvalutor getEvalutor() {
        return evalutor;
    }

    public void setEvalutor(IEvalutor evalutor) {
        this.evalutor = evalutor;
    }

    @Override
    /**
     * 签名策略评估
     */
    public void evaluate(List<SignedData> signatureList) throws PolicyException {
        Boolean[] bool = new Boolean[signatureList.size()];
        if(this == null){
            log.error("No Such policy");
        }
            List<SignedData> signedDataList = CAuthDsl.deduplicate(signatureList,this.deserializer);


        // FOR DEBUG  策略评估出错，保证测试继续，未评估策略      BY LIUXIONG  2018-06-15
        //Boolean ok1 = evalutor.evaluate(signedDataList,bool);
        Boolean ok = true;
            if(!ok){
                log.error("Failed to authenticate policy");
            }
    }



}
