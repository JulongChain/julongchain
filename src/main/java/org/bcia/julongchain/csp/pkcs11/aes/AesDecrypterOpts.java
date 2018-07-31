/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.pkcs11.aes;

import org.bcia.julongchain.csp.intfs.opts.IDecrypterOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;
import sun.security.pkcs11.wrapper.PKCS11Constants;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public enum AesDecrypterOpts implements IDecrypterOpts {

    /*
     算法/模式/填充                 16字节加密后数据长度       		不满16字节加密后长度
    AES/CBC/NoPadding                   16                          不支持
    AES/CBC/PKCS5Padding                32                          16
    AES/CBC/ISO10126Padding             32                          16
    AES/CFB/NoPadding                   16                          原始数据长度
    AES/CFB/PKCS5Padding                32                          16
    AES/CFB/ISO10126Padding             32                          16
    AES/ECB/NoPadding                   16                          不支持
    AES/ECB/PKCS5Padding                32                          16
    AES/ECB/ISO10126Padding             32                          16
    AES/OFB/NoPadding                   16                          原始数据长度
    AES/OFB/PKCS5Padding                32                          16
    AES/OFB/ISO10126Padding             32                          16
    AES/PCBC/NoPadding                  16                          不支持
    AES/PCBC/PKCS5Padding               32                          16
    AES/PCBC/ISO10126Padding            32                          16
     */
    NoPad_CBC("NoPadding","CBC", PKCS11Constants.CKM_AES_CBC, 1),
    PKCS5_CBC("PKCS5Padding","CBC", PKCS11Constants.CKM_AES_CBC_PAD, 2),
    NoPad_ECB("NoPadding","ECB", PKCS11Constants.CKM_AES_CBC, 3),
    PKCS5_ECB("PKCS5Padding","ECB", PKCS11Constants.CKM_AES_ECB, 4);


    private String padding;
    private String mode;
    private long mechanism;

    private AesDecrypterOpts(String padding,String mode, long mechanism, int index) {
        this.mechanism = mechanism;
        this.padding = padding;
        this.mode = mode;
    }

    @Override
    public String getAlgorithm() {
        return PKCS11CSPConstant.AES;
    }

    public String getMode() {
        return mode;
    }

    public String getPadding() {
        return padding;
    }

    public long getMechanism() {
        return mechanism;
    }

}

