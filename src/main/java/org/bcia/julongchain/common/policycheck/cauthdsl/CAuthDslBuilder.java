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

package org.bcia.julongchain.common.policycheck.cauthdsl;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.common.Policies;

import java.util.Arrays;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class CAuthDslBuilder {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CAuthDslBuilder.class);
    // AcceptAllPolicy always evaluates to true
    //AcceptAllPolicy总是评估为true
    private Policies.SignaturePolicyEnvelope AcceptAllPolicy;
    // MarshaledAcceptAllPolicy is the Marshaled version of AcceptAllPolicy.
    // arshaledAcceptAllPolicy是AcceptAllPolicy的Marshaled版本
    private byte[] MarshaledAcceptAllPolicy;
    // RejectAllPolicy always evaluates to false
    private Policies.SignaturePolicyEnvelope RejectAllPolicy;
    // MarshaledRejectAllPolicy is the Marshaled version of RejectAllPolicy
    // RejectAllPolicy总是评估为false
    private byte[] MarshaledRejectAllPolicy;


    public void init() throws PolicyException {
        Policies.SignaturePolicy[] signaturePolicy = {};
        byte[][] b = {};

        try {
            AcceptAllPolicy = CAuthDslBuilder.envelope(nOutOf(0,signaturePolicy),b);
            MarshaledAcceptAllPolicy = ProtoUtils.marshalOrPanic(AcceptAllPolicy);
        }catch (Exception e){
            log.error("Error marshaling trueEnvelope");
            throw new PolicyException(e);
            //或者throw new PolicyException(e.getMessage());
        }
        try {
            RejectAllPolicy = CAuthDslBuilder.envelope(nOutOf(1,signaturePolicy),b);
            MarshaledRejectAllPolicy = ProtoUtils.marshalOrPanic(RejectAllPolicy);
        }catch (Exception e){
            log.error("marshaling falseEnvelope");
            throw new PolicyException(e);
        }


    }
    // Envelope builds an envelope message embedding a SignaturePolicy
    // 信封生成嵌入SignaturePolicy的信封消息
    public static Policies.SignaturePolicyEnvelope envelope(Policies.SignaturePolicy policy,byte[][] identities){
        MspPrincipal.MSPPrincipal[] ids = new MspPrincipal.MSPPrincipal[identities.length];
        MspPrincipal.MSPPrincipal.Builder builder = MspPrincipal.MSPPrincipal.newBuilder();
        Policies.SignaturePolicyEnvelope.Builder speBuilder = Policies.SignaturePolicyEnvelope.newBuilder();
        for (int i = 0; i<ids.length; i++){
            builder.setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.IDENTITY);
            builder.setPrincipal(ByteString.copyFrom(identities[i]));
            ids[i] = builder.build();
            speBuilder.addIdentities(ids[i]);
        }

        speBuilder.setVersion(0);
        speBuilder.setRule(policy);
        return speBuilder.build();
    }

    /**
     *  SignedBy creates a SignaturePolicy requiring a given signer's signature
        SignedBy创建一个需要给定签名者签名的SignaturePolicy
     * @param index
     * @return
     */

    public static Policies.SignaturePolicy signedBy(int index){
        return Policies.SignaturePolicy.newBuilder().setSignedBy(index).build();
    }

    /**
     * SignedByMspMember creates a SignaturePolicyEnvelope
      requiring 1 signature from any member of the specified MSP
     SignedByMspMember创建一个SignaturePolicyEnvelope
     需要来自指定MSP的任何成员的1个签名
     * @param mspId
     * @return
     */
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
        MspPrincipal.MSPPrincipal principal = builder.build();
        Policies.SignaturePolicyEnvelope.Builder speBuilder = Policies.SignaturePolicyEnvelope.newBuilder();
        Policies.SignaturePolicy[] signaturePolicies = {CAuthDslBuilder.signedBy(0)};
        speBuilder.setVersion(0);
        speBuilder.setRule(CAuthDslBuilder.nOutOf(1,signaturePolicies));
        speBuilder.addIdentities(principal);
        return speBuilder.build();
    }

    /**
     *  SignedByMspAdmin creates a SignaturePolicyEnvelope
      requiring 1 signature from any admin of the specified MSP
      SignedByMspAdmin创建一个SignaturePolicyEnvelope
     需要来自指定MSP的任何管理员的1个签名
     * @param mspId
     * @return
     */
    public static Policies.SignaturePolicyEnvelope signedByMspAdmin(String mspId){
        //构建MspPrincipal.MSPRole对象
        MspPrincipal.MSPRole.Builder mspRoleBuild = MspPrincipal.MSPRole.newBuilder();
        mspRoleBuild.setRole(MspPrincipal.MSPRole.MSPRoleType.ADMIN);
        mspRoleBuild.setMspIdentifier(mspId);
        MspPrincipal.MSPRole mspRole = mspRoleBuild.build();
        //构建MspPrincipal.MSPPrincipal对象
        MspPrincipal.MSPPrincipal.Builder builder = MspPrincipal.MSPPrincipal.newBuilder();
        builder.setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE);
        builder.setPrincipal(mspRole.toByteString());
        MspPrincipal.MSPPrincipal principal = builder.build();
        Policies.SignaturePolicyEnvelope.Builder speBuilder = Policies.SignaturePolicyEnvelope.newBuilder();
        Policies.SignaturePolicy[] signaturePolicies = {CAuthDslBuilder.signedBy(0)};
        speBuilder.setVersion(0);
        speBuilder.setRule(CAuthDslBuilder.nOutOf(1,signaturePolicies));
        speBuilder.setIdentities(0,principal);
        return speBuilder.build();
    }

    /**
     * wrapper for generating "any of a given role" type policies
     用于生成“任何给定角色”类型策略的包装器
     * @param role
     * @param ids
     * @return
     */
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

    /**
     *
      SignedByAnyMember returns a policy that requires one valid
      signature from a member of any of the orgs whose ids are
      listed in the supplied string array
      SignedByAnyMember返回一个需要一个有效的策略
     从任何id的组织的成员签名
     列在提供的字符串数组中
     * @param ids
     * @return
     */
    public static Policies.SignaturePolicyEnvelope signedByAnyMember(String[] ids){
        return CAuthDslBuilder.signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType.MEMBER,ids);
    }

    /**
     * SignedByAnyAdmin returns a policy that requires one valid
       signature from a admin of any of the orgs whose ids are
      listed in the supplied string array
      SignedByAnyAdmin返回一个需要一个有效的策略
     从任何id的组织的管理员签名
     列在提供的字符串数组中
     * @param ids
     * @return
     */
    public static Policies.SignaturePolicyEnvelope signedByAnyAdmin(String[] ids){
        return CAuthDslBuilder.signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType.ADMIN,ids);
    }

    /**
     * And is a convenience method which utilizes NOutOf to produce And equivalent behavior
     是一种利用NOutOf生成等价行为的方便方法
     * @param lhs
     * @param rhs
     * @return
     */
    public static Policies.SignaturePolicy and(Policies.SignaturePolicy lhs,Policies.SignaturePolicy rhs){
        Policies.SignaturePolicy[] sps = {lhs,rhs};
        return CAuthDslBuilder.nOutOf(2,sps);
    }

    /**
     *  Or is a convenience method which utilizes NOutOf to produce Or equivalent behavior
      or是一种利用NOutOf产生或等同行为的便利方法
     * @param lhs
     * @param rhs
     * @return
     */
    public static Policies.SignaturePolicy or(Policies.SignaturePolicy lhs,Policies.SignaturePolicy rhs){
        Policies.SignaturePolicy[] sps = {lhs,rhs};
        return CAuthDslBuilder.nOutOf(1,sps);
    }

    /**
     *  NOutOf creates a policy which requires N out of the slice of policies to evaluate to true
      NOutOf创建一个策略，要求N从策略切片中评估为true
     * @param n
     * @param policies
     * @return
     */
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
