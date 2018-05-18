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
package org.bcia.javachain.common.cauthdsl;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.IPolicy;
import org.bcia.javachain.common.policycheck.bean.SignaturePolicyEnvelope;
import org.bcia.javachain.common.policycheck.cauthdsl.Cauthdsl;
import org.bcia.javachain.common.policycheck.cauthdsl.Policy;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.IMspManager;

import java.lang.reflect.InvocationTargetException;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/21/18
 * @company Dingxuan
 */
public class PolicyProvider {
    private static JavaChainLog log = JavaChainLogFactory.getLog(PolicyProvider.class);
    private IIdentityDeserializer deserializer;

    public IIdentityDeserializer getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(IIdentityDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    /*public PolicyProvider(IMspManager manager){

    }*/

    /**
     * 根据字节传创建一个新策略
     * @param policyBytes
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public IPolicy newPolicy(byte[] policyBytes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Policy policy = new Policy();
        SignaturePolicyEnvelope sigPolicy = new SignaturePolicyEnvelope();
        //proto.Unmarshal(data, sigPolicy)
        if(sigPolicy.getVersion() != 0){
            log.info("Error unmarshaling to SignaturePolicy");
            return null;
        }
        Boolean compiled = Cauthdsl.compile(sigPolicy.getRule().isSignaturePolicy_type,sigPolicy.getiIdentitys(),deserializer);
        policy.setEvalutor(compiled);
        policy.setDeserializer(this.deserializer);
        return policy;
    }
}
