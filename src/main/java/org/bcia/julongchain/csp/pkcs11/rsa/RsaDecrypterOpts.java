/**
 * Copyright Dingxuan. All Rights Reserved.
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

package org.bcia.julongchain.csp.pkcs11.rsa;

import org.bcia.julongchain.csp.intfs.opts.IDecrypterOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;
import sun.security.pkcs11.wrapper.PKCS11Constants;

/**
 * RSA Decrypter Opts
 *
 * @author xuying
 * @date 2018/05/20
 * @company FEITIAN
 */
public enum RsaDecrypterOpts implements IDecrypterOpts {

    oPad_Pub("NoPadding", PKCS11Constants.CKM_RSA_X_509, true, 1),
    NoPad_Prv("NoPadding", PKCS11Constants.CKM_RSA_X_509, false, 2),
    OAEP_Pub("OAEPPadding", PKCS11Constants.CKM_RSA_PKCS_OAEP, true, 3),
    OAEP_Prv("OAEPPadding", PKCS11Constants.CKM_RSA_PKCS_OAEP, false, 4),
    PKCS1_Pub("PKCS1Padding", PKCS11Constants.CKM_RSA_PKCS, true, 5),
    PKCS1_Prv("PKCS1Padding", PKCS11Constants.CKM_RSA_PKCS, false, 6),
    //ISO9796"ISO9796Padding", PKCS11Constants.CKM_RSA_9796, 4),
    Normal_Pub("", PKCS11Constants.CKM_RSA_PKCS, true, 7),
    Normal_Prv("", PKCS11Constants.CKM_RSA_PKCS, false, 8);

    private String padding;
    private long mechanism;
    private boolean flagpub;

    private RsaDecrypterOpts(String padding, long mechanism, boolean flagpub, int index) {
        this.mechanism = mechanism;
        this.padding = padding;
        this.flagpub = flagpub;
    }

    @Override
    public String getAlgorithm() {
        return PKCS11CSPConstant.RSA;
    }

    public String getMode() {
        return "ECB";
    }

    public String getPadding() {
        return padding;
    }

    public long getMechanism() {
        return mechanism;
    }

    public boolean getFlagpub () {
        return flagpub;
    }
}
