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
package org.bcia.julongchain.msp.mgmt;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.protos.common.MspPrincipal;

import static org.bcia.julongchain.msp.mgmt.GlobalMspManagement.getLocalMsp;

/**
 * 类描述
 *
 * @author sunianle
 * @date 4/2/18
 * @company Dingxuan
 */
public class MSPPrincipalGetter implements IMspPrincipalGetter {
    public static final String Admins = "Admins";
    public static final String Members = "Members";

    public MSPPrincipalGetter() {
    }

    @Override
    public MspPrincipal.MSPPrincipal get(String role) throws MspException {
        String mspid = getLocalMsp().getIdentifier();

        switch (role) {
            case Admins:
                MspPrincipal.MSPRole mspAdmin = MspPrincipal.MSPRole.newBuilder()
                        .setMspIdentifier(mspid)
                        .setRole(MspPrincipal.MSPRole.MSPRoleType.ADMIN).build();
                byte[] principalBytes = mspAdmin.toByteArray();
                MspPrincipal.MSPPrincipal mspPrincipal = MspPrincipal.MSPPrincipal.newBuilder()
                        .setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE)
                        .setPrincipal(ByteString.copyFrom(principalBytes)).build();
                return mspPrincipal;
            case Members:
                MspPrincipal.MSPRole mspMembers = MspPrincipal.MSPRole.newBuilder()
                        .setMspIdentifier(mspid)
                        .setRole(MspPrincipal.MSPRole.MSPRoleType.ADMIN).build();
                byte[] principalMemberBytes = mspMembers.toByteArray();
                MspPrincipal.MSPPrincipal mspMembersPrincipal = MspPrincipal.MSPPrincipal.newBuilder()
                        .setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE)
                        .setPrincipal(ByteString.copyFrom(principalMemberBytes)).build();
                return mspMembersPrincipal;
            default:
                throw new MspException(String.format("MSP MSPPrincipalGetter role [%s] not recognized",role));
        }
    }
}
