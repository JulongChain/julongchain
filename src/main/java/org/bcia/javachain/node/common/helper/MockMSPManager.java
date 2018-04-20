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
package org.bcia.javachain.node.common.helper;

import org.bcia.javachain.core.common.validation.MockIdentityDeserializer;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.entity.IdentityIdentifier;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.msp.Identities;
import org.bcia.javachain.protos.msp.MspConfigPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/3/10
 * @company Dingxuan
 */
public class MockMSPManager {
    public static MspConfigPackage.MSPConfig getVerifyingMspConfig(String mspDir, String orgId, String mspType) {
        if (mspType.equals("csp")) {
            return MspConfigPackage.MSPConfig.newBuilder().build();
        }

        return null;
    }

    public static IIdentityDeserializer getIdentityDeserializer(String groupId) {
        return new MockIdentityDeserializer();
    }

    public static IMsp getLocalMSP() {
        return new IMsp() {
            @Override
            public IMsp setup(MspConfigPackage.MSPConfig config) {
                return null;
            }

//            @Override
//            public IMsp load(GmSoftConf GmSoftConf) {
//                return null;
//            }

            @Override
            public int getVersion() {
                return 0;
            }

            @Override
            public int getType() {
                return 0;
            }

            @Override
            public String getIdentifier() {
                return null;
            }

            @Override
            public ISigningIdentity getSigningIdentity(IdentityIdentifier identityIdentifier) {
                return null;
            }

            @Override
            public ISigningIdentity getDefaultSigningIdentity() {
                return null;
            }

            @Override
            public byte[][] getTLSRootCerts() {
                return new byte[0][];
            }

            @Override
            public byte[][] getTLSIntermediateCerts() {
                return new byte[0][];
            }

            @Override
            public void validate(IIdentity id) {

            }

            @Override
            public void satisfiesPrincipal(IIdentity id, MspPrincipal.MSPPrincipal principal) {

            }

            @Override
            public IIdentity deserializeIdentity(byte[] serializedIdentity) {
                return null;
            }

            @Override
            public void isWellFormed(Identities.SerializedIdentity identity) {

            }
        };
    }
}
