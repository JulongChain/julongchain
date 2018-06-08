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

import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class AesOpts {
    public static class AESKeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public AESKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }
        public String getAlgorithm() {
            return PKCS11CSPConstant.AES;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class AES128KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public AES128KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }
        public String getAlgorithm() {
            return PKCS11CSPConstant.AES128;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class AES192KeyGenOpts  implements IKeyGenOpts {
        private boolean bTemporary;

        public AES192KeyGenOpts (boolean bTemporary) {
            this.bTemporary = bTemporary;
        }
        public String getAlgorithm() {
            return PKCS11CSPConstant.AES192;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class AES256KeyGenOpts  implements IKeyGenOpts {
        private boolean bTemporary;

        public AES256KeyGenOpts (boolean bTemporary) {
            this.bTemporary = bTemporary;
        }
        public String getAlgorithm() {
            return PKCS11CSPConstant.AES256;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }
    
    public static class AESKeyImportOpts implements IKeyImportOpts {
    	private boolean bTemporary;

        public AESKeyImportOpts (boolean bTemporary) {
            this.bTemporary = bTemporary;
        }
        public String getAlgorithm() {
            return PKCS11CSPConstant.AES;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }
}
