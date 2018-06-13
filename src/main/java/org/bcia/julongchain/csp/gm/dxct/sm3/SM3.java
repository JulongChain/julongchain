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

import org.bouncycastle.crypto.digests.SM3Digest;

/**
 * @author zhanglin
 * @purpose Define the class, SM3
 * @date 2018-01-25
 * @company Dingxuan
 */

public class SM3 {
    private SM3Digest sm3Digest;
    /**
     *摘要长度
     */
    private int digestSize;

    public SM3() {
        sm3Digest = new SM3Digest();
        digestSize = sm3Digest.getDigestSize();
    }

    public byte[] hash(byte[] msg) {
        byte[] resbuf = new byte[digestSize];
        sm3Digest.update(msg, 0, msg.length);
        sm3Digest.doFinal(resbuf, 0);
        return resbuf;

    }
}
