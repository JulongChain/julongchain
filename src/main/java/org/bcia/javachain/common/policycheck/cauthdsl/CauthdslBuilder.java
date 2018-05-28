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

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.policies.SignaturePolicy;
import org.bcia.javachain.common.policycheck.bean.SignaturePolicyEnvelope;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.common.Policies;

import java.util.Arrays;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class CauthdslBuilder {
    private Policies.SignaturePolicyEnvelope AcceptAllPolicy;
    private byte[] MarshaledAcceptAllPolicy;
    private Policies.SignaturePolicyEnvelope RejectAllPolicy;
    private byte[] MarshaledRejectAllPolicy;


    public void init(){}
    public Policies.SignaturePolicy envelope(SignaturePolicy policy,byte[][] identities){
       // identities.length;
        return null;
    }
    public static Policies.SignaturePolicy signedBy(int index){
        return Policies.SignaturePolicy.newBuilder().setSignedBy(index).build();
    }
    public static Policies.SignaturePolicyEnvelope signedByMspMember(String mspId){
        //构建MspPrincipal.MSPRole对象
        MspPrincipal.MSPRole.Builder mspRoleBuild = MspPrincipal.MSPRole.newBuilder();
        mspRoleBuild.setRole(MspPrincipal.MSPRole.MSPRoleType.MEMBER);
        mspRoleBuild.setMspIdentifier(mspId);
        MspPrincipal.MSPRole mspRole = mspRoleBuild.build();
        //构建MspPrincipal.MSPPrincipal对象
        MspPrincipal.MSPPrincipal.Builder builder = MspPrincipal.MSPPrincipal.newBuilder();
        builder.setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE);
        builder.setPrincipal(mspRole.toByteString());
        return null;
    }
    public static Policies.SignaturePolicyEnvelope signedByMspAdmin(String mspId){
        return null;
    }

    private static Policies.SignaturePolicyEnvelope signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType role, String[] ids){
        if(ids == null){
            return Policies.SignaturePolicyEnvelope.getDefaultInstance();
        }
        Arrays.sort(ids);
        MspPrincipal.MSPPrincipal[] principals = new MspPrincipal.MSPPrincipal[ids.length];
        Policies.SignaturePolicy[] sigspolicy = new Policies.SignaturePolicy[ids.length];
        for (int i = 0; i < ids.length; i++) {
            principals[i] = MspPrincipal.MSPPrincipal.newBuilder()
                    .setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE)
                    .setPrincipal(ByteString.copyFrom(ProtoUtils.marshalOrPanic(MspPrincipal.MSPRole.newBuilder()
                            .setRole(role)
                            .setMspIdentifier(ids[i])
                            .build())))
                    .build();
            sigspolicy[i] = signedBy(i);
        }
        Policies.SignaturePolicyEnvelope.Builder builder = Policies.SignaturePolicyEnvelope.newBuilder()
                .setVersion(0)
                .setRule(nOutOf(1, sigspolicy));
        for (MspPrincipal.MSPPrincipal principal : principals) {
            builder.addIdentities(principal);
        }
        return builder.build();
    }

    public static Policies.SignaturePolicyEnvelope signedByAnyMember(String[] ids){
        return CauthdslBuilder.signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType.MEMBER,ids);
    }

    public static Policies.SignaturePolicyEnvelope signedByAnyAdmin(String[] ids){
        return CauthdslBuilder.signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType.ADMIN,ids);
    }

    public static Policies.SignaturePolicy and(SignaturePolicy lhs,SignaturePolicy rhs){
        SignaturePolicy[] signaturePolicies = new SignaturePolicy[2];
        signaturePolicies[0] = lhs;
        signaturePolicies[1] = rhs;
        return null;
    }
    public static Policies.SignaturePolicy or(SignaturePolicy lhs,SignaturePolicy rhs){
        SignaturePolicy[] signaturePolicies = new SignaturePolicy[2];
        signaturePolicies[0] = lhs;
        signaturePolicies[1] = rhs;
        return null;
    }
    public static Policies.SignaturePolicy nOutOf(int n, Policies.SignaturePolicy[] policies){
        Policies.SignaturePolicy.NOutOf.Builder builder = Policies.SignaturePolicy.NOutOf.newBuilder();
        for (Policies.SignaturePolicy policy : policies) {
            builder.addRules(policy);
        }
        builder.setN(n);
        return Policies.SignaturePolicy.newBuilder()
                .setNOutOf(builder)
                .build();
    }

}
