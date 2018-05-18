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
    public static Boolean compile(IsSignaturePolicyType policy,MspPrincipal[] identities,IIdentityDeserializer deserializer) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        SignedData[] signedDatas = new SignedData[0];//先写成0
        Boolean[] used = new Boolean[0];
        if(policy == null){
            log.info("Empty policy element");
        }
        if(policy instanceof SignaturePolicyNOutOf){
            Boolean[] polices = new Boolean[((SignaturePolicyNOutOf) policy).getRules().length];
            for(int i=0;i<polices.length;i++){
                Boolean compiledPolicy = compile(policy,identities,deserializer);
                polices[i] = compiledPolicy;
            }
            return Cauthdsl.confirmSignedData(signedDatas,used,polices,policy);
        }
        if(policy instanceof SignaturePolicySignedBy){
            if(((SignaturePolicySignedBy) policy).SignedBy<0 ||((SignaturePolicySignedBy) policy).SignedBy>=identities.length){
                log.info("identity index out of range, requested "+((SignaturePolicySignedBy) policy).SignedBy+", but identies length is"+identities.length);
            }
            MspPrincipal signedByID = identities[((SignaturePolicySignedBy) policy).SignedBy];
            SignaturePolicySignedBy spolicy = (SignaturePolicySignedBy) policy;
            return Cauthdsl.confirmSignedData1(signedDatas,used,signedByID,deserializer,spolicy);
        }else{
            log.info("Unknown type");
            return null;
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
    public static boolean confirmSignedData(SignedData[] signedDatas,Boolean[] used,Boolean[] policies,IsSignaturePolicyType policy) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Long grepKey = new Date().getTime();
        log.debug(signedDatas+"gate"+grepKey+" evaluation starts");
        int verified = 0;
        Boolean[] _used = new Boolean[used.length];
        for(int i=0;i<policies.length;i++){
            System.arraycopy(used,0,_used,0,used.length);
            if(policies[i]){       //TODO 待定
                verified++;
                System.arraycopy(_used,0,used,0,used.length);
            }
        }
        Method getN = policy.getClass().getMethod("getN");
        int N = (int) getN.invoke(policy);
        if(verified>=N){
           log.info(signedDatas+"gate"+ grepKey+"evaluation succeeds");
        }else{
            log.info(signedDatas+"gate"+ grepKey+"evaluation  fails");
        }
        return verified>=N;
    }

    public static boolean confirmSignedData1(SignedData[] signedDatas, Boolean[] used, MspPrincipal signedByID, IIdentityDeserializer deserializer,SignaturePolicySignedBy policy){
        log.debug(signedDatas+"signed by "+policy.SignedBy+" principal evaluation starts (used %v)");

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
