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

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policycheck.policies.Evalutor;
import org.bcia.javachain.common.policycheck.policies.IEvalutor;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.common.Policies;

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
    public static List<SignedData> deduplicate(List<SignedData> signedDatas, IIdentityDeserializer deserializer){
        Map<String,Object> ids = new HashMap<String,Object>();
        List<SignedData> result = new ArrayList<SignedData>();
        for(int i=0;i<signedDatas.size();i++){
            IIdentity identity = deserializer.deserializeIdentity(signedDatas.get(i).getIdentity());
            String key = identity.getMSPIdentifier();
            if(ids.get(key) != null){
                log.warn("De-duplicating identity "+identity+" at index "+i+" in signature set");
            }else{
                result.add(signedDatas.get(i));
                ids.put(key,null);
            }
        }
        return result;
    }

    public static IEvalutor compile(Policies.SignaturePolicy policy, List<MspPrincipal.MSPPrincipal> identities, IIdentityDeserializer deserializer){
        if(policy == null){
            log.info("Empty policy element");
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
                    log.info("identity index out of range, requested "+policy.getSignedBy()+", but identies length is"+identities.size());
                }
                MspPrincipal.MSPPrincipal signedByID = identities.get(policy.getSignedBy());
                return new Evalutor(null,policy,deserializer,signedByID);
            default:
                log.error("Unknown type");
                return null;

        }
    }
}
