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
package org.bcia.julongchain.csp.gmt0016.ftsafe.entity;

import org.bcia.julongchain.common.exception.JulongChainException;

/**
 * HardWare Information
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public class GMT0016Lib {
    private String sLibrary;
    private String sKeyLabel;
    private String sKeySn;
    private String sUserPin;
    private String sAdminPin;
    private String sAppName;


    public GMT0016Lib(String slib, String sLabel, String sSn, String sAdminPin,
                      String sUserPin, String sAppName) throws JulongChainException {

        checkParam(sLabel);
        checkParam(slib);
        checkParam(sSn);
        checkParam(sAdminPin);
        checkParam(sUserPin);
        //checkParam(sAppName);

        this.sLibrary = slib;
        this.sKeyLabel = sLabel;
        this.sKeySn = sSn;
        this.sAdminPin = sAdminPin;
        this.sUserPin = sUserPin;
        this.sAppName = sAppName;
    }

    private void checkParam(String str) throws JulongChainException {
        if (str == null || "".equals(str))
        {
            throw new JulongChainException("[JC_SKF]:Param Err!");
        }
    }


    public String getLibrary() {
        return sLibrary;
    }

    public String getKeyLabel() {
        return sKeyLabel;
    }

    public String getKeySN() {
        return sKeySn;
    }

    public String getUserPin() {
        return sUserPin;
    }

    public String getAdminPin() {
        return sAdminPin;
    }
}
