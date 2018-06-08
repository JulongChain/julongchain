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
package org.bcia.julongchain.node.entity;

import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.entity.OUIdentifier;
import org.bcia.julongchain.protos.common.MspPrincipal;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/3/13
 * @company Dingxuan
 */
public class MockSigningIdentity implements ISigningIdentity {
    @Override
    public byte[] sign(byte[] msg) {
        return new byte[0];
    }

    @Override
    public IIdentity getPublicVersion() {
        return null;
    }

    @Override
    public String getMSPIdentifier() {
        return null;
    }

    @Override
    public void validate() {

    }

    @Override
    public OUIdentifier[] getOrganizationalUnits() {
        return new OUIdentifier[0];
    }

    @Override
    public void verify(byte[] msg, byte[] sig) {

    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    @Override
    public void satisfiesPrincipal(MspPrincipal.MSPPrincipal principal) {

    }
}
