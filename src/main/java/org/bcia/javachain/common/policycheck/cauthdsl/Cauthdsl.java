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
import org.bcia.javachain.msp.mgmt.Identity;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.protos.common.MspPrincipal;

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
    public static List<SignedData> deduplicate(List<SignedData> signedDatas,Msp msp){
        List<SignedData> result = new ArrayList<SignedData>();
        IIdentity identity = new Identity();
        for(int i=0;i<signedDatas.size();i++){
            identity = msp.deserializeIdentity(signedDatas.get(i).getIdentity());
            //key := identity.GetIdentifier().Mspid + identity.GetIdentifier().Id
        }
        String key = "";
        for (SignedData signedData : signedDatas) {
            return null;
        }
        //TODO 待完善
        return null;
    }

    /**
     * 评估函数方法
     * @param policy
     * @return
     */
    public static boolean compile(IsSignaturePolicyType policy, MspPrincipal[] identities, Msp msp) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    List<SignedData> signedDatas = new ArrayList<SignedData>();
    List<Boolean> policies = new ArrayList<Boolean>();
    if(policy==null){
        log.info("Empty policy element");
    }
    if(policy instanceof SignaturePolicyNOutOf){



        Method rules = policy.getClass().getMethod("getRules");
        List<IsSignaturePolicyType> policys = (List<IsSignaturePolicyType>) rules.invoke(policy);
        for (int i =0;i<policys.size();i++){
            boolean compiledPolicy = compile(policys.get(i),identities,msp);
            policies.add(compiledPolicy);
        }
        return Cauthdsl.confirmSignedData(signedDatas,policies,policy);

    }
    if(policy instanceof SignaturePolicySignedBy){
        if(((SignaturePolicySignedBy) policy).SignedBy<0 ||((SignaturePolicySignedBy) policy).SignedBy>=identities.length){
            log.info("identity index out of range, requested "+((SignaturePolicySignedBy) policy).SignedBy+", but identies length is"+identities.length);
        }
        MspPrincipal signedByID = identities[((SignaturePolicySignedBy) policy).SignedBy];
        return Cauthdsl.confirmSignedData1(signedDatas,policies,signedByID,msp);
    }
        //TODO 待完善
        return false;
    }

    /**
     * 参照fabric里compile里的返回函数
     * @param signedDatas
     * @param policies
     * @return
     */
    public static boolean confirmSignedData(List<SignedData> signedDatas,List<Boolean> policies,IsSignaturePolicyType policy) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Long grepKey = new Date().getTime();
        log.info(signedDatas+"gate"+grepKey+" evaluation starts");
        int verified = 0;
        List<Boolean> _used = new ArrayList<Boolean>();
        Method getN = policy.getClass().getMethod("getN");
        int N = (int) getN.invoke(policy);
        for (Boolean polocy: policies) {
            //TODO 待完善
        }

        if(verified>=N){
           log.info(signedDatas+"gate"+ grepKey+"evaluation succeeds");
        }else{
            log.info(signedDatas+"gate"+ grepKey+"evaluation  fails");
        }
        return verified>=N;
    }

    public static boolean confirmSignedData1(List<SignedData> signedDatas, List<Boolean> used, MspPrincipal signedByID, Msp msp){
        for(int i=0;i<signedDatas.size();i++){
            if(used.get(i)){
                log.info(signedDatas.get(i)+"skipping identity"+i+"because it has already been used");
                continue;
            }
           // identity, err := deserializer.DeserializeIdentity(sd.Identity)
            IIdentity iIdentity = msp.deserializeIdentity(signedDatas.get(i).getIdentity());
            iIdentity.satisfiesPrincipal(signedByID);
            iIdentity.verify(signedDatas.get(i).getData(),signedDatas.get(i).getSignature());
            used.set(i,true);
            return true;
        }
        return false;
    }

}
