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
 * 硬件相关信息
 *
 * @author Ying Xu
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11Lib {

    private String strLibrary;
    private String strKeyLabel;
    private String strKeySn;
    private String strKeyPin;

    public PKCS11Lib(String strLib, String strLabel, String strSn, String strPin) throws JulongChainException{

        checkParam(strLabel);
        checkParam(strLib);
        checkParam(strSn);
        checkParam(strPin);

        this.strLibrary = strLib;
        this.strKeyLabel = strLabel;
        this.strKeySn = strSn;
        this.strKeyPin = strPin;
    }

    private void checkParam(String str) throws JulongChainException{
        if (str == null || "".equals(str))
        {
            throw new JulongChainException("[JC_PKCS]:Param Err!");
        }
    }

    public String getLibrary() {
        return strLibrary;
    }

    public String getKeyLabel() {
        return strKeyLabel;
    }

    public String getKeySN() {
        return strKeySn;
    }

    public String getKeyPin() {
        return strKeyPin;
    }
}
