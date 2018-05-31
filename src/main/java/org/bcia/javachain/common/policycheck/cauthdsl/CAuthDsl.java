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
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.common.Policies;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class CAuthDsl {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CAuthDsl.class);

    /**signedProposal
     * 删除重复身份，保留身份顺序
     * @param signedDatas
     * @return
     */
    public static List<SignedData> deduplicate(List<SignedData> signedDatas,IIdentityDeserializer deserializer){

        //SignedData[] result = new SignedData[signedDatas.length];
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

    /**
     * 评估函数方法，判断策略类型
     * @param policy
     * @return
     */
    public static Boolean compile(Policies.SignaturePolicy policy,List<MspPrincipal.MSPPrincipal> identities,IIdentityDeserializer deserializer) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        //SignedData[] signedDatas = new SignedData[policy.getNOutOf().getRulesList().size()];
        List<SignedData> signedDatas = new ArrayList<SignedData>();
        Boolean[] used = new Boolean[policy.getNOutOf().getRulesList().size()];
        if(policy == null){
            log.info("Empty policy element");
        }
        switch (policy.getTypeCase()) {
            case N_OUT_OF:
                Boolean[] polices = new Boolean[policy.getNOutOf().getRulesList().size()];
                Policies.SignaturePolicy signaturePolicy = null;
                for(int i=0;i<polices.length;i++){
                    signaturePolicy = policy.getNOutOf().getRulesList().get(i);
                    Boolean compiledPolicy = compile(signaturePolicy,identities,deserializer);
                    polices[i] = compiledPolicy;
                }
                return CAuthDsl.confirmSignedData(signedDatas,used,polices,policy);
            case SIGNED_BY:
                if(policy.getSignedBy() < 0 || policy.getSignedBy()>identities.size()){
                    log.info("identity index out of range, requested "+policy.getSignedBy()+", but identies length is"+identities.size());
                }
                MspPrincipal.MSPPrincipal signedByID = identities.get(policy.getSignedBy());
                return CAuthDsl.confirmSignedData1(signedDatas,used,signedByID,deserializer,policy);
             default:
                 log.error("Unknown type");
                 return null;

        }




    }


    /**
     * 参照fabric里compile里的返回函数
     * @param signedDataList
     * @param policies
     * @param policy
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean confirmSignedData(List<SignedData> signedDataList,Boolean[] used,Boolean[] policies,Policies.SignaturePolicy policy) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Long grepKey = new Date().getTime();
        log.debug(signedDataList+"gate"+grepKey+" evaluation starts");
        int verified = 0;
        Boolean[] _used = new Boolean[used.length];
        for(int i = 0; i < policies.length; i++){
            System.arraycopy(used,0,_used,0,used.length);
            if(policies[i]){       //TODO 待定
                verified++;
                System.arraycopy(_used,0,used,0,used.length);
            }
        }
        int N = policy.getNOutOf().getN();
        if(verified >= N){
           log.info(signedDataList+"gate"+ grepKey+"evaluation succeeds");
        }else{
            log.info(signedDataList+"gate"+ grepKey+"evaluation  fails");
        }
        return verified >= N;
    }



    public static boolean confirmSignedData1(List<SignedData> signedDataList, Boolean[] used, MspPrincipal.MSPPrincipal signedByID, IIdentityDeserializer deserializer,Policies.SignaturePolicy policy){
        log.debug(signedDataList+"signed by "+policy.getSignedBy()+" principal evaluation starts (used %v)");

        for(int i=0;i<signedDataList.size();i++){
            if(used[i]){
                log.info(signedDataList.get(i)+"skipping identity"+i+"because it has already been used");
                continue;
            }
           // identity, err := deserializer.DeserializeIdentity(sd.Identity)
            IIdentity iIdentity = null;
            try {
                iIdentity = deserializer.deserializeIdentity(signedDataList.get(i).getIdentity());
            }catch (Exception e){
                log.info("Principal deserialization failure  for identity "+signedDataList.get(i).getIdentity());
            }
            try {
                iIdentity.satisfiesPrincipal(signedByID);
            }catch (Exception e){
                log.info(signedDataList+"identity %d does not satisfy principal: "+i);
            }
            log.debug(signedDataList+" principal matched by identity "+i);
            try {
                iIdentity.verify(signedDataList.get(i).getData(),signedDataList.get(i).getSignature());
            }catch (Exception e){
                log.info(signedDataList+"signature for identity "+i+" is invalid: ");
            }
            log.debug(signedDataList+" principal evaluation succeeds for identity "+i);
            used[i] = true;
            return true;
        }
        return false;
    }


}
