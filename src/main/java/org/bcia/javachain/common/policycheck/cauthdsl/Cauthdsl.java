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

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policycheck.SignaturePolicyNOutOf;
import org.bcia.javachain.common.policycheck.SignaturePolicySignedBy;
import org.bcia.javachain.common.policycheck.common.IsSignaturePolicyType;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.mgmt.Identity;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.common.Policies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class Cauthdsl {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Cauthdsl.class);
    private int MSPPrincipal_Classification;

    /**
     * 删除重复身份，保留身份顺序
     * @param signedDatas
     * @return
     */
    public static List<SignedData> deduplicate(List<SignedData> signedDatas,IIdentityDeserializer deserializer){

        //SignedData[] result = new SignedData[signedDatas.length];
        Map<String,Object> ids = new HashMap<String,Object>();
        List<SignedData> result = new ArrayList<SignedData>();
        IIdentity identity = new Identity();
        for(int i=0;i<signedDatas.size();i++){
            identity = deserializer.deserializeIdentity(signedDatas.get(i).getIdentity());
            String key = identity.getMSPIdentifier();
            if(ids.get(key) != null){
                log.warn("De-duplicating identity "+identity+" at index "+i+" in signature set");
            }else{
                result.add(signedDatas.get(i));
                ids.put(key,new Object());
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
        SignedData[] signedDatas = new SignedData[policy.getNOutOf().getRulesList().size()];
        Boolean[] used = new Boolean[policy.getNOutOf().getRulesList().size()];
        if(policy == null){
            log.info("Empty policy element");
        }
        if(policy.hasNOutOf()){
            Boolean[] polices = new Boolean[policy.getNOutOf().getRulesList().size()];
            for(int i=0;i<polices.length;i++){
                Boolean compiledPolicy = compile(policy,identities,deserializer);
                polices[i] = compiledPolicy;
            }
            return Cauthdsl.confirmSignedData(signedDatas,used,polices,policy);
        }else{
            if(policy.getSignedBy()<0 || policy.getSignedBy()>identities.size()){
                log.info("identity index out of range, requested "+policy.getSignedBy()+", but identies length is"+identities.size());
            }
            MspPrincipal.MSPPrincipal signedByID = identities.get(policy.getSignedBy());
            return Cauthdsl.confirmSignedData1(signedDatas,used,signedByID,deserializer,policy);
        }


    }

    /**
     * 参照fabric里compile里的返回函数
     * @param signedDatas
     * @param policies
     * @param policy
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean confirmSignedData(SignedData[] signedDatas,Boolean[] used,Boolean[] policies,Policies.SignaturePolicy policy) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Long grepKey = new Date().getTime();
        log.debug(signedDatas+"gate"+grepKey+" evaluation starts");
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
           log.info(signedDatas+"gate"+ grepKey+"evaluation succeeds");
        }else{
            log.info(signedDatas+"gate"+ grepKey+"evaluation  fails");
        }
        return verified >= N;
    }

    public static boolean confirmSignedData1(SignedData[] signedDatas, Boolean[] used, MspPrincipal.MSPPrincipal signedByID, IIdentityDeserializer deserializer,Policies.SignaturePolicy policy){
        log.debug(signedDatas+"signed by "+policy.getSignedBy()+" principal evaluation starts (used %v)");

        for(int i=0;i<signedDatas.length;i++){
            if(used[i]){
                log.info(signedDatas[i]+"skipping identity"+i+"because it has already been used");
                continue;
            }
           // identity, err := deserializer.DeserializeIdentity(sd.Identity)
            IIdentity iIdentity = null;
            try {
                iIdentity = deserializer.deserializeIdentity(signedDatas[i].getIdentity());
            }catch (Exception e){
                log.info("Principal deserialization failure  for identity "+signedDatas[i].getIdentity());
            }
            try {
                iIdentity.satisfiesPrincipal(signedByID);
            }catch (Exception e){
                log.info(signedDatas+"identity %d does not satisfy principal: "+i);
            }
            log.debug(signedDatas+" principal matched by identity "+i);
            try {
                iIdentity.verify(signedDatas[i].getData(),signedDatas[i].getSignature());
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
