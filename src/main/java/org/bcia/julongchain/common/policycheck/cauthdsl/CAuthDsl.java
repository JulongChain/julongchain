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
import org.bcia.julongchain.common.policycheck.policies.Evalutor;
import org.bcia.julongchain.common.policycheck.policies.IEvalutor;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.msp.IIdentityDeserializer;

import org.bcia.julongchain.msp.mgmt.Identity;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.common.Policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 31/05/18
 * @company Aisino
 */
public class CAuthDsl {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CAuthDsl.class);
    /**signedProposal
     * 删除重复身份，保留身份顺序
     * @param signedDatas
     * @return
     */
    public static List<SignedData> deduplicate(List<SignedData> signedDatas, IIdentityDeserializer deserializer) throws PolicyException {
        Map<String,Object> ids = new HashMap<String,Object>(16);
        List<SignedData> result = new ArrayList<SignedData>();
        for(int i=0;i<signedDatas.size();i++){
            Identity identity = null;
            try {
                identity = (Identity) deserializer.deserializeIdentity(signedDatas.get(i).getIdentity());
            }catch (Exception e){
                String msg=String.format("Principal deserialization failure  %s for [%s]",e.getMessage(),signedDatas.get(i).getIdentity());
                throw new PolicyException(msg);
            }
            String key = identity.getIdentityIdentifier().getMspid()+identity.getIdentityIdentifier().getId();
            if(ids.get(key) != null){
                log.warn("De-duplicating identity [%s] at index [%s] in signature set",signedDatas.get(i).getIdentity(),i);
            }else{
                result.add(signedDatas.get(i));
                ids.put(key,null);
            }
        }
        return result;
    }

    public static IEvalutor compile(Policies.SignaturePolicy policy, List<MspPrincipal.MSPPrincipal> identities, IIdentityDeserializer deserializer){
        if(policy == null){
            log.error("Empty policy element");
        }
        switch (policy.getTypeCase()) {
            case N_OUT_OF:
                List<IEvalutor> polices = new ArrayList<IEvalutor>();
                Policies.SignaturePolicy signaturePolicy = null;
                for(int i=0; i < policy.getNOutOf().getRulesList().size(); i++){
                    signaturePolicy = policy.getNOutOf().getRulesList().get(i);
                    IEvalutor compiledPolicy = compile(signaturePolicy,identities,deserializer);
                    polices.add(compiledPolicy);
                }
                return new Evalutor(polices,policy,deserializer,null);
            case SIGNED_BY:
                if(policy.getSignedBy() < 0 || policy.getSignedBy()>identities.size()){
                    log.error("identity index out of range, requested [%s], but identies length is [%s]",policy.getSignedBy(),identities.size());
                }
                MspPrincipal.MSPPrincipal signedByID = identities.get(policy.getSignedBy());
                return new Evalutor(null,policy,deserializer,signedByID);
            default:
                log.error("Unknown type : [%s]",policy);
                return null;

        }
    }
}
