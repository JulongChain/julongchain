/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.javachain.common.cauthdsl;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.policies.IPolicyProvider;
import org.bcia.javachain.common.policies.MockPolicyProvider;
import org.bcia.javachain.common.policies.SignaturePolicy;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.common.Policies;

import java.util.Arrays;

/**
 * 类描述
 *
 * @author sunianle
 * @date 4/3/18
 * @company Dingxuan
 * @deprecated hangtianxinxi weihu
 */
public class CAuthDslBuilder {
    public static Policies.SignaturePolicy signedBy(int index){
        //TODO implement by sunzongyu, support for lssc. data: 2018-05-09
        return Policies.SignaturePolicy.newBuilder()
                .setSignedBy(index)
                .build();
    }

    public static Policies.SignaturePolicyEnvelope signedByAnyMember(String[] ids){
        //TODO implement by sunzongyu, support for lssc. data: 2018-05-09
        return signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType.MEMBER, ids);
    }

    public static Policies.SignaturePolicyEnvelope signedByAnyAdmin(String[] ids){
        //TODO implement by sunzongyu, support for lssc. data: 2018-05-10
        return signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType.ADMIN, ids);
    }

    public static IPolicyProvider createPolicyProvider(IMspManager mspManager) {
        //TODO implement by sunzongyu, support for lssc. data: 2018-05-10
        //TODO policy not completed
        //TODO for test lssc, return MockPolicyProvider
         return new MockPolicyProvider();
    }

    private static Policies.SignaturePolicyEnvelope signedByAnyOfGivenRole(MspPrincipal.MSPRole.MSPRoleType role, String[] ids){
        //TODO implement by sunzongyu, support for lssc. data: 2018-05-09
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

    public static Policies.SignaturePolicy nOutOf(int n, Policies.SignaturePolicy[] policies){
        //TODO implement by sunzongyu, support for lssc. data: 2018-05-09
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
