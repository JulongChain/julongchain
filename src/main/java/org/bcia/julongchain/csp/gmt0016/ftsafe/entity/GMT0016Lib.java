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

/**
 * Class description
 *
 * @author
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

    public GMT0016Lib() {
        this.sLibrary = "/root/Desktop/libes_3000gm.so";
        this.sKeyLabel = "ePass Token";
        this.sKeySn = "0328470216020518";
        this.sAdminPin = "rockey";
        this.sUserPin = "123456a";
        this.sAppName = "ENTERSAFE-ESPK";
    }


    public GMT0016Lib(String slib, String sLabel, String sSn, String sAdminPin,
                      String sUserPin, String sAppName) {
        if (slib == null || slib.equals(""))
            this.sLibrary = "/root/Desktop/libes_3000gm.so";
        else
            this.sLibrary = slib;

        if (sLabel == null || sLabel.equals(""))
            this.sKeyLabel = "ePass2003";
        else
            this.sKeyLabel = sLabel;

        if (sSn == null || sSn.equals(""))
            this.sKeySn = "0328470216020518";
        else
            this.sKeySn = sSn;

        if (sAdminPin == null)
            this.sAdminPin = "rockey";
        else
            this.sAdminPin = sAdminPin;

        if (sUserPin == null)
            this.sUserPin = "12345678";
        else
            this.sUserPin = sUserPin;


        this.sAppName = sAppName;
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
