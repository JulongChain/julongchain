 /**
 * Copyright SDT. All Rights Reserved.
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
 package org.bcia.julongchain.csp.gm.sdt.sm2;

/**
 * GM SM2 公私钥对
 *
 * @author tengxiumin
 * @date 2016/05/16
 * @company SDT
 */
public class SM2KeyPair {

    private byte[] publicKey;
    private byte[] privateKey;

    public SM2KeyPair(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * 获取公钥数据
     * @return 公钥数据
     */
    public byte[] getPublicKey() {
        return publicKey;
    }

    /**
     * 获取私钥数据
     * @return 私钥数据
     */
    public byte[] getPrivateKey() {
        return privateKey;
    }
}
