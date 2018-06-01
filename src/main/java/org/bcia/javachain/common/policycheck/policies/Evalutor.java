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

package org.bcia.javachain.common.policycheck.policies;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.common.Policies;

import java.util.Date;
import java.util.List;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 31/05/18
 * @company Aisino
 */
public class Evalutor implements IEvalutor{
    private static JavaChainLog log = JavaChainLogFactory.getLog(Evalutor.class);
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
    public boolean evalutor(List<SignedData> signedDatas, Boolean[] used) {
        if(policy.getTypeCase().getNumber() == 2){
            Long grepKey = new Date().getTime();
            log.debug(signedDatas+"gate"+grepKey+" evaluation starts");
            int verified = 0;
            Boolean[] _used = new Boolean[used.length];
            for(int i = 0; i < policies.size(); i++){
                System.arraycopy(used,0,_used,0,used.length);
                if(policies.get(i).evalutor(signedDatas,_used)){
                    verified++;
                    System.arraycopy(_used,0,used,0,used.length);
                }
            }
            int N = policy.getNOutOf().getN();
            if(verified >= N){
                log.info(signedDatas+"gate"+ grepKey+"evaluation succeeds");
            }else{
                log.info(signedDatas+"gate"+ grepKey+"evaluation  fails");
            }
            return verified >= N;
        }
        else{
            log.debug(signedDatas+"signed by "+policy.getSignedBy()+" principal evaluation starts (used %v)");

            for(int i=0;i<signedDatas.size();i++){
                if(used[i]){
                    log.info(signedDatas.get(i)+"skipping identity"+i+"because it has already been used");
                    continue;
                }
                // identity, err := deserializer.DeserializeIdentity(sd.Identity)
                IIdentity iIdentity = null;
                try {
                    iIdentity = deserializer.deserializeIdentity(signedDatas.get(i).getIdentity());
                }catch (Exception e){
                    log.info("Principal deserialization failure  for identity "+signedDatas.get(i).getIdentity());
                }
                try {
                    iIdentity.satisfiesPrincipal(signedByID);
                }catch (Exception e){
                    log.info(signedDatas+"identity %d does not satisfy principal: "+i);
                }
                log.debug(signedDatas+" principal matched by identity "+i);
                try {
                    iIdentity.verify(signedDatas.get(i).getData(),signedDatas.get(i).getSignature());
                }catch (Exception e){
                    log.info(signedDatas+"signature for identity "+i+" is invalid: ");
                }
                log.debug(signedDatas+" principal evaluation succeeds for identity "+i);
                used[i] = true;
                return true;
            }
            return false;
        }



    }
}
