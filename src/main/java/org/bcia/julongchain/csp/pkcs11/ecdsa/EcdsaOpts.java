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
package org.bcia.julongchain.csp.pkcs11.ecdsa;

import org.bcia.julongchain.csp.intfs.opts.IKeyDerivOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyGenOpts;
import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;

/**
 * Class description
 *
 * @author XuYing
 * @date 4/19/18
 * @company FEITIAN
 */
public class EcdsaOpts {

    public static class ECDSAKeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public ECDSAKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }
    
    public static class ECDSA192KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public ECDSA192KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA192;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class ECDSA256KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public ECDSA256KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA256;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class ECDSA384KeyGenOpts implements IKeyGenOpts {
        private boolean bTemporary;

        public ECDSA384KeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA384;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class ECDSAPublicKeyImportOpts implements IKeyImportOpts {
        private boolean bTemporary;

        public ECDSAPublicKeyImportOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }

    public static class ECDSAPrivateKeyImportOpts implements IKeyImportOpts{
        private boolean bTemporary;

        public ECDSAPrivateKeyImportOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }
    
    public static class ECDSAReRandKeyOpts implements IKeyDerivOpts{
    	private boolean bTemporary;

        public ECDSAReRandKeyOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }
    
    public static class EcdsaHardPubKeyOpts implements IKeyDerivOpts{
    	private boolean bTemporary;

        public EcdsaHardPubKeyOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }
    
    public static class EcdsaHardPriKeyOpts implements IKeyDerivOpts{
    	private boolean bTemporary;

        public EcdsaHardPriKeyOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;
        }

        @Override
        public String getAlgorithm() {
            return PKCS11CSPConstant.ECDSA;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }
    }
}
