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

import org.bcia.julongchain.csp.intfs.opts.ISignerOpts;
import sun.security.pkcs11.wrapper.PKCS11Constants;

/**
 * Rsa Sign Opts
 *
 * @author xuying
 * @date 2018/05/20
 * @company FEITIAN
 */
public enum RsaSignOpts implements ISignerOpts {

    //SHA1WithRSA/MD2withRSA/MD5withRSA/SHA1withRSA/SHA256withRSA
    RSA("NONEwithRSA", PKCS11Constants.CKM_RSA_PKCS, 1),
    MD2("MD2withRSA", PKCS11Constants.CKM_MD2_RSA_PKCS, 2),
    MD5("MD5withRSA", PKCS11Constants.CKM_MD5_RSA_PKCS, 3),
    SHA1("SHA1withRSA ", PKCS11Constants.CKM_SHA1_RSA_PKCS, 4),
    SHA256("SHA256withRSA", PKCS11Constants.CKM_SHA256_RSA_PKCS, 5),
    SHA384("SHA384withRSA", PKCS11Constants.CKM_SHA384_RSA_PKCS, 6);


    private String name;
    private int index;
    private long mechanism;

    private RsaSignOpts(String name, long mechanism, int index) {
        this.name = name;
        this.index = index;
        this.mechanism = mechanism;
    }

    @Override
    public String getAlgorithm() {
        return this.name;
    }

    @Override
    public String hashFunc() {
        // TODO Auto-generated method stub
        return this.name;
    }

    public long getmechanism() {
        return this.mechanism;
    }

}
