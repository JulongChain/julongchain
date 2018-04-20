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
package org.bcia.javachain.csp.pkcs11.ecdsa;

import org.bcia.javachain.csp.intfs.IKey;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

/**
 * ECDSA KEY
 *
 * @author XuYing
 * @date 4/19/18
 * @company FEITIAN
 */

public class EcdsaKeyOpts {

    public static class EcdsaPriKey implements IKey{
        private byte[] byski;
        private EcdsaPubKey ecdsapubkey;
        private ECPrivateKey ecprivatekey;

        public EcdsaPriKey(byte[] byski, ECPrivateKey privatekey, EcdsaPubKey ecdsaPubkey) {
            this.byski = byski;
            this.ecdsapubkey = ecdsaPubkey;
            this.ecprivatekey = privatekey;
        }

        @Override
        public byte[] toBytes() {
            // TODO Auto-generated method stub
            return ecprivatekey.getEncoded();
        }

        @Override
        public byte[] ski() {
            return byski;
        }

        @Override
        public boolean isSymmetric() {
            return false;
        }

        @Override
        public boolean isPrivate() {
            return true;
        }

        @Override
        public IKey getPublicKey() {

            return ecdsapubkey;
        }
    }

    public static class EcdsaPubKey implements IKey{
        private byte[] byski;
        private ECPublicKey ecPubKey;

        public EcdsaPubKey(byte[] byski, ECPublicKey pubkey) {
            this.byski = byski;
            this.ecPubKey = pubkey;
        }

        @Override
        public byte[] toBytes() {
            // TODO Auto-generated method stub

            //raw
            return ecPubKey.getEncoded();
        }

        @Override
        public byte[] ski() {
            return byski;
        }

        @Override
        public boolean isSymmetric() {
            return false;
        }

        @Override
        public boolean isPrivate() {
            return false;
        }

        @Override
        public IKey getPublicKey() {
            return null;
        }
    }
}
