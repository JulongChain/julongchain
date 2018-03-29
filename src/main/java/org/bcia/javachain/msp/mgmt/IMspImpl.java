/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.javachain.msp.mgmt;

import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.entity.GmSoftConf;
import org.bcia.javachain.msp.entity.IdentityIdentifier;
import org.bcia.javachain.msp.gmsoft.GmSoftMsp;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.msp.Identities;
import org.bcia.javachain.protos.msp.MspConfigPackage;

/**
 * @author zhangmingyang
 * @Date: 2018/3/27
 * @company Dingxuan
 */
public class IMspImpl implements IMsp {
    @Override
    public void setup(MspConfigPackage.MSPConfig config) {

    }

    @Override
    public void load(GmSoftConf gmSoftConf) {

    }

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
        return new ISigningIdentityImpl();
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
}
