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

package org.bcia.julongchain.csp.pkcs11.ecdsa;

import org.bcia.julongchain.csp.intfs.opts.ISignerOpts;
import sun.security.pkcs11.wrapper.PKCS11Constants;

/**
 * Ecdsa Sign Opts
 *
 * @author xuying
 * @date 2018/05/21
 * @company FEITIAN
 */
public enum EcdsaSignOpts implements ISignerOpts {
    ECDSA("NONEwithECDSA", PKCS11Constants.CKM_ECDSA, 1),
    SHA1("SHA1withECDSA", /*PKCS11Constants.CKM_ECDSA_SHA1*/PKCS11Constants.CKM_ECDSA, 2),
    SHA256("SHA256withECDSA", PKCS11Constants.CKM_ECDSA, 3),
    SHA384("SHA384withECDSA", PKCS11Constants.CKM_ECDSA, 4);


    private String name;
    private int index;
    private long mechanism;


    private EcdsaSignOpts(String name, long mechanism, int index) {
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
        return ""+this.index;
    }

    public long getmechanism() {
        return this.mechanism;
    }
}
