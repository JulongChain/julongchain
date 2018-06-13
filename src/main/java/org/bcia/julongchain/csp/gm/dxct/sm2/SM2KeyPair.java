package org.bcia.julongchain.csp.gm.dxct.sm2; /**
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

/**
 * @author zhangmingyang
 * @Date: 2018/4/24
 * @company Dingxuan
 */
public class SM2KeyPair {
    private byte[] publickey;
    private byte[] privatekey;

    public SM2KeyPair(byte[] publickey, byte[] privatekey) {
        this.publickey = publickey;
        this.privatekey = privatekey;
    }

    public byte[] getPublickey() {
        return publickey;
    }

    public byte[] getPrivatekey() {
        return privatekey;
    }
}
