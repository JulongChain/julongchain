/**
 * Copyright Dingxuan. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.bcia.julongchain.csp.pkcs11.rsa;

import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;

/**
 * gen/import rsa keypair opts
 *
 * @author xuying
 * @date 2018/5/21
 * @company FEITIAN
 */
public class RsaOpts {

    public static class RSAKeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSAKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.RSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSA1024KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA1024KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.RSA1024;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSA2048KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA2048KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.RSA2048;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSA3072KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA3072KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.RSA3072;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSA4096KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA4096KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.RSA4096;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSAPublicKeyImportOpts implements IKeyImportOpts {
        private boolean bTemporary;

        public RSAPublicKeyImportOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return  PKCS11CSPConstant.RSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSAPrivateKeyImportOpts implements IKeyImportOpts {
        private boolean bTemporary;

        public RSAPrivateKeyImportOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return  PKCS11CSPConstant.RSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }
}
