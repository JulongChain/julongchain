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
package org.bcia.javachain.common.localmsp.impl;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.msp.mgmt.Identity;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.msp.mgmt.MspManager;
import org.bcia.javachain.protos.common.Common;
import java.io.UnsupportedEncodingException;

/**
 * @author zhangmingyang
 * @Date: 2018/3/6
 * @company Dingxuan
 */
public class LocalSigner implements ILocalSigner {
    @Override
    public Common.SignatureHeader newSignatureHeader() {
        try {
            Identity identity= (Identity) MspManager.getLocalMsp().getDefaultSigningIdentity();
            byte[] creatorIdentityRaw=identity.serialize();
            Common.SignatureHeader.Builder signatureHeader=Common.SignatureHeader.newBuilder();
            //TODO 随机数实现
            byte[] none="test".getBytes();
            signatureHeader.setNonce(ByteString.copyFrom(none));
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
        return  MspManager.getLocalMsp().getDefaultSigningIdentity().sign(message);
    }
}
