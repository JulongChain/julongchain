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

import org.bcia.javachain.common.cauthdsl.PolicyProvider;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentityDeserializer;
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

    private IIdentityDeserializer deserializer;
    private Boolean evalutor;

    public IIdentityDeserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(IIdentityDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public Boolean getEvalutor() {
        return evalutor;
    }

    public void setEvalutor(Boolean evalutor) {
        this.evalutor = evalutor;
    }

    @Override
    /**
     * 签名策略评估
     */
    public void evaluate(List<SignedData> signatureList) throws PolicyException {
        Boolean[] bool = new Boolean[signatureList.size()];
        if(this == null){
            log.info("No sEvaluateuch policy");
        }
            Boolean ok = this.evalutor(Cauthdsl.deduplicate(signatureList,this.deserializer),bool);//评估这组签名是否满足策略
            if(!ok){
                log.info("Failed to authenticate policy");
            }
    }
    public Boolean evalutor(List<SignedData> signedDatas,Boolean[] bools){
        return true;
    }

    /*  *//**
     * 为cauthdsl类型策略提供策略生成器
     * @param deserializer
     * @return
     *//*
    public PolicyProvider NewPolicyProvider(IIdentityDeserializer deserializer){
        PolicyProvider policyProvider = new PolicyProvider(deserializer);
        //policyProvider.setDeserializer(deserializer);
        return policyProvider;

    }*/


}
