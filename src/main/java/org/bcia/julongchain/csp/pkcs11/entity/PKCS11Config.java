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
package org.bcia.julongchain.csp.pkcs11.entity;

import org.bcia.julongchain.common.exception.JulongChainException;

/**
 * PKCS11 配置信息 (Maybe useful)
 *
 * @author Ying Xu
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11Config {
    private static int securityLevel;
    private static String hashFamily;
    private static boolean sfVer;
    private static boolean noKImport;
    //private boolean useecx963encodeing;
    private static String path;

    public PKCS11Config(int level, String hashfamily, boolean softVerify, boolean noKeyImport)  throws JulongChainException {

        switch (level){
            case 1:
            case 5:
            case 256:
            case 384:
                PKCS11Config.securityLevel = level;
                break;
            default:
                throw new JulongChainException("param level invalid");
        }

        switch (hashfamily) {
            case "MD":
            case "SHA1":
            case "SHA2":
            case "SHA3":
                PKCS11Config.hashFamily = hashfamily;
                break;
            default:
                throw new JulongChainException("param hashfamily invalid");
        }

        PKCS11Config.sfVer = softVerify;
        PKCS11Config.noKImport = noKeyImport;
    }

    public void setPath(String path) {
        PKCS11Config.path = path;
    }

    public String getPath() {
        return path;
    }

    public int getLevel() {
        return securityLevel;
    }

    public String getHashFamily() {
        return hashFamily;
    }

    public boolean getSoftVerify() {
        return sfVer;
    }

    public boolean getnoKImport() {
        return noKImport;
    }

}
