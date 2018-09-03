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

import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.common.Policies;

import java.util.List;

/**
 * 类描述
 * 策略评估的具体实现类
 * @author yuanjun
 * @date 31/05/18
 * @company Aisino
 */
public class Evalutor implements IEvalutor{
    private static JulongChainLog log = JulongChainLogFactory.getLog(Evalutor.class);
    private static final int TYPE = 2;
    private List<IEvalutor> policies ;
    private Policies.SignaturePolicy policy;
    private IIdentityDeserializer deserializer;
    private MspPrincipal.MSPPrincipal signedByID;

    public Evalutor(List<IEvalutor> policies, Policies.SignaturePolicy policy, IIdentityDeserializer deserializer, MspPrincipal.MSPPrincipal signedByID) {
        this.policies = policies;
        this.policy = policy;
        this.deserializer = deserializer;
        this.signedByID = signedByID;
    }

    @Override
    public boolean evaluate(List<SignedData> signedDatas, Boolean[] used) throws PolicyException {
        if(policy.getTypeCase().getNumber() == TYPE){
            Long grepKey = System.currentTimeMillis();
            log.debug("[%s] entrance [%s] evaluation starts",signedDatas,grepKey);
            int verified = 0;
            Boolean[] newused = new Boolean[used.length];
            for(int i = 0; i < policies.size(); i++){
                System.arraycopy(used,0,newused,0,used.length);
                if(policies.get(i).evaluate(signedDatas,newused)){
                    verified++;
                    System.arraycopy(newused,0,used,0,used.length);
                }
            }
            int n = policy.getNOutOf().getN();
            if(verified >= n){
                log.debug("[%p] entrance [%s] evaluation succeeds",signedDatas,grepKey);
            }else{
                log.debug("[%p] entrance [%s] evaluation  fails",signedDatas,grepKey);
            }
            return verified >= n;
        }
        else{
            log.debug("[%p] signed by [%d] principal evaluation starts (used %v)",signedDatas,policy.getSignedBy(),used);

            for(int i=0;i<signedDatas.size();i++){
                if(used[i]){
                    log.debug("[%p] ignoring identity [%d] because it has already been used", signedDatas, i);
                    continue;
                }
                IIdentity iIdentity = null;
                try {
                    iIdentity = deserializer.deserializeIdentity(signedDatas.get(i).getIdentity());
                }catch (Exception e){
                    String msg=String.format("Principal deserialization failure as message %s for [%s]",e.getMessage(),signedDatas.get(i).getIdentity());
                    throw new PolicyException(msg);
                }
                try {
                    iIdentity.satisfiesPrincipal(signedByID);
                }catch (Exception e){
                    String msg=String.format("%p identity %d does not satisfy principal: %s",signedDatas,i,e.getMessage());
                    throw new PolicyException(msg);
                }
                log.debug("%p principal matched by identity %d",signedDatas,i);
                try {
                    iIdentity.verify(signedDatas.get(i).getData(),signedDatas.get(i).getSignature());
                }catch (Exception e){
                    String msg=String.format("%p signature for identity %d is invalid: %s",signedDatas,i,e.getMessage());
                    throw new PolicyException(msg);
                }
                log.debug("%p principal evaluation succeeds for identity %d",signedDatas,i);
                used[i] = true;
                return true;
            }
            return false;
        }



    }
}
