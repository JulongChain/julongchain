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
package org.bcia.javachain.csp.pkcs11.entity;

/**
 * Class description
 *
 * @author
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11Lib {

    private String Library;
    private String KeyLabel;
    private String KeySn;
    private String KeyPin;

    public PKCS11Lib(String lib, String Label, String Sn, String Pin) {
        if (lib == null || lib.equals(""))
            this.Library = "";
        else
            this.Library = lib;

        if (Label == null || Label.equals(""))
            this.KeyLabel = "KEY";
        else
            this.KeyLabel = Label;

        if (Sn == null || Sn.equals(""))
            this.KeySn = "249181BB80750017";
        else
            this.KeySn = Sn;

        if (Pin == null)
            this.KeyPin = "12345678";
        else
            this.KeyPin = Pin;
    }

    public String getLibrary() {
        return Library;
    }

    public String getKeyLabel() {
        return KeyLabel;
    }

    public String getKeySN() {
        return KeySn;
    }

    public String getKeyPin() {
        return KeyPin;
    }
}
