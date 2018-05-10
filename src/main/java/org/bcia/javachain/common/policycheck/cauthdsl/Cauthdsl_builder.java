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

import org.bcia.javachain.common.policies.SignaturePolicy;
import org.bcia.javachain.common.policycheck.bean.SignaturePolicyEnvelope;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class Cauthdsl_builder {
    private SignaturePolicyEnvelope AcceptAllPolicy;
    private byte[] MarshaledAcceptAllPolicy;
    private SignaturePolicyEnvelope RejectAllPolicy;
    private byte[] MarshaledRejectAllPolicy;
    public void init(){}
    public void Envelope(SignaturePolicy policy,byte[][] identities){

    }
    public void SignedBy(int index){}
    public void SignedByMspMember(String mspId){}
    public void SignedByMspAdmin(String mspId){}
    public void signedByAnyOfGivenRole(){

    }
    public void SignedByAnyMember(){}
    public void SignedByAnyAdmin(String[] ids){

    }
    public SignaturePolicy And(SignaturePolicy lhs,SignaturePolicy rhs){
        SignaturePolicy[] signaturePolicies = new SignaturePolicy[2];
        signaturePolicies[0] = lhs;
        signaturePolicies[1] = rhs;
        return NOutOf(2,signaturePolicies);
    }
    public SignaturePolicy Or(SignaturePolicy lhs,SignaturePolicy rhs){
        SignaturePolicy[] signaturePolicies = new SignaturePolicy[2];
        signaturePolicies[0] = lhs;
        signaturePolicies[1] = rhs;
        return NOutOf(1,signaturePolicies);
    }
    public SignaturePolicy NOutOf(int n, SignaturePolicy[] signaturePolicys){
        return signaturePolicys[0];
    }

}
