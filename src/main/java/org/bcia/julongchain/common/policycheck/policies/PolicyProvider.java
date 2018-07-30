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

package org.bcia.julongchain.common.policycheck.policies;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.IPolicyProvider;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.policycheck.cauthdsl.CAuthDsl;
import org.bcia.julongchain.common.policycheck.cauthdsl.Policy;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.protos.common.Policies;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 02/05/18
 * @company Aisino
 */
public class PolicyProvider implements IPolicyProvider{
    private static JavaChainLog log = JavaChainLogFactory.getLog(PolicyProvider.class);
    private IIdentityDeserializer deserializer;
    public PolicyProvider(IIdentityDeserializer deserializer) {
        this.deserializer = deserializer;
    }


    @Override
    public IPolicy makePolicy(byte[] data) throws PolicyException {

        Policies.SignaturePolicyEnvelope signaturePolicyEnvelope = null;
        try {
            signaturePolicyEnvelope = Policies.SignaturePolicyEnvelope.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error unmarshaling to SignaturePolicy");
            throw new PolicyException(e);
        }
        if(signaturePolicyEnvelope.getVersion() != 0){
            log.error("This evaluator only understands messages of version 0, but version was %d",signaturePolicyEnvelope.getVersion());
            return null;
        }
        IEvalutor compiled = CAuthDsl.compile(signaturePolicyEnvelope.getRule(),signaturePolicyEnvelope.getIdentitiesList(),deserializer);
        Policy policy = new Policy(deserializer,compiled);
        return policy;
    }
}
