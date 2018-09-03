package org.bcia.julongchain.csp.gm.dxct.sm3;

/**
 * Copyright BCIA. All Rights Reserved.
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

import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2;
import org.bcia.julongchain.csp.gm.dxct.util.GmCspConstant;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * 国密sm3实现
 *
 * @author zhanglin, zhangmingyang
 * @date 2018-01-25
 * @company Dingxuan
 */

public class SM3 {
    private static JulongChainLog log = JulongChainLogFactory.getLog(SM3.class);
    public SM3() {
    }

    public byte[] hash(byte[] msg) throws NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(GmCspConstant.SM3);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
          throw new NoSuchAlgorithmException(e.getMessage());
        }
        messageDigest.update(msg);
        byte[] digest = messageDigest.digest();
        return digest;
    }
}
