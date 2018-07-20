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
package org.bcia.julongchain.csp.gmt0016.ftsafe.rsa;

import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class RSAOpts {

    public static class RSAKeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSAKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        public String getAlgorithm() {
            return GMT0016CspConstant.RSA;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }


    }

    public static class RSA1024KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA1024KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        public String getAlgorithm() {
            return GMT0016CspConstant.RSA1024;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSA2048KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA2048KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        public String getAlgorithm() {
            return GMT0016CspConstant.RSA2048;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSA3072KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA3072KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        public String getAlgorithm() {
            return GMT0016CspConstant.RSA3072;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class RSA4096KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public RSA4096KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        public String getAlgorithm() {
            return GMT0016CspConstant.RSA4096;
        }

        public boolean isEphemeral() {
            return bTemporary;
        }
    }
}
