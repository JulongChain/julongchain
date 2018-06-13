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
package org.bcia.julongchain.csp.gmt0016.excelsecu.algorithm;

import org.bcia.julongchain.csp.intfs.IHash;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class SHA1 implements IHash {

    int h0 = 0x67452301;
    int h1 = 0xEFCDAB89;
    int h2 = 0x98BADCFE;
    int h3 = 0x10325476;
    int h4 = 0xC3D2E1F0;

    @Override
    public int write(byte[] p) {
        return 0;
    }

    @Override
    public byte[] sum(byte[] b) {
        return new byte[0];
    }

    @Override
    public void reset() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int blockSize() {
        return 0;
    }
}
