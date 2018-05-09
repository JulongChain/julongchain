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
import org.bcia.javachain.common.policies.SignaturePolicy;
import org.bcia.javachain.common.policycheck.SignaturePolicy_NOutOf;
import org.bcia.javachain.common.policycheck.SignaturePolicy_SignedBy;
import org.bcia.javachain.common.policycheck.bean.MSPPrincipal;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.msp.mgmt.MspManager;

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
    public static List<SignedData> deduplicate(List<SignedData> signedDatas){
        IIdentity iIdentity = null;
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
    public static boolean compile(SignaturePolicy policy, MSPPrincipal identities, Msp msp){
    List<SignedData> signedDatas = new ArrayList<SignedData>();

    if(policy==null){
        log.info("Empty policy element");
    }
    if(policy.isSignaturePolicy_type instanceof SignaturePolicy_NOutOf){

    }
    if(policy.isSignaturePolicy_type instanceof SignaturePolicy_SignedBy){

    }
        //TODO 待完善
        return false;
    }

    public boolean confirmSignedData(List<SignedData> signedDatas,List<Boolean> bool){
        Long grepkey = new Date().getTime();
        return false;
    }

}
