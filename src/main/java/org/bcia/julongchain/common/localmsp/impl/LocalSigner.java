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
package org.bcia.julongchain.common.localmsp.impl;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.msp.mgmt.Identity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.protos.common.Common;


/**
 * 本地msp接口实现
 *
 * @author zhangmingyang
 * @Date: 2018/3/6
 * @company Dingxuan
 */
public class LocalSigner implements ILocalSigner {
    private static JulongChainLog log = JulongChainLogFactory.getLog(LocalSigner.class);

    public LocalSigner() {
    }

    @Override
    public Common.SignatureHeader newSignatureHeader() {
        try {
            Identity identity = (Identity) GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity().getIdentity();
            byte[] creatorIdentityRaw = identity.serialize();
            Common.SignatureHeader.Builder signatureHeader = Common.SignatureHeader.newBuilder();
            byte[] nonce = null;
            nonce = identity.getMsp().getCsp().rng(24, null);
            signatureHeader.setNonce(ByteString.copyFrom(nonce));
            signatureHeader.setCreator(ByteString.copyFrom(creatorIdentityRaw));
            return signatureHeader.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] sign(byte[] message) {
        //通过获取msp实例,从实例中
        return GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity().sign(message);
    }
}
