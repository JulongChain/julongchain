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

import org.bcia.julongchain.csp.intfs.IKey;

/**
 * RSA IKey implements
 *
 * @author xuying
 * @date 2018/05/20
 * @company FEITIAN
 */
public class RsaKeyOpts {
    public static class RsaPriKey implements IKey {
        private byte[] byski;
        private byte[] raw;
        private RsaPubKey rsapubkey;

        public RsaPriKey(byte[] ski, byte[] prider, RsaPubKey pubkey) {
            this.byski = new byte[ski.length + 1];
            this.raw = new byte[prider.length];
            System.arraycopy(prider, 0, this.raw, 0, prider.length);
            this.byski[0] = 0x01;
            System.arraycopy(ski, 0, this.byski, 1, ski.length);
            this.rsapubkey = pubkey;
        }

        @Override
        public byte[] toBytes() {
            // TODO Auto-generated method stub
            // private key der code
            return raw;
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

            return rsapubkey;
        }
    }

    public static class RsaPubKey implements IKey{
        private byte[] byski;
        private byte[] raw;
        public RsaPubKey(byte[] ski, byte[] pubder) {
            this.byski = new byte[ski.length + 1];
            this.raw = new byte[pubder.length];
            System.arraycopy(pubder, 0, this.raw, 0, pubder.length);
            this.byski[0] = 0x01;
            System.arraycopy(ski, 0, this.byski, 1, ski.length);
        }

        @Override
        public byte[] toBytes() {
            // TODO Auto-generated method stub
            // public key der code
            return raw;
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
