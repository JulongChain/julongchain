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

import org.bcia.julongchain.csp.intfs.IKey;

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
        private byte[] raw;

        public EcdsaPriKey(byte[] byski, byte[] prider, EcdsaPubKey ecdsaPubkey) {
        	this.byski = new byte[byski.length + 1];
        	this.byski[0] = 0x02;
            System.arraycopy(byski, 0, this.byski, 1, byski.length);
            this.ecdsapubkey = ecdsaPubkey;
            this.raw = prider;
        }

        @Override
        public byte[] toBytes() {
            // TODO Auto-generated method stub
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

            return ecdsapubkey;
        }

    }

    public static class EcdsaPubKey implements IKey{
        private byte[] byski;
        private byte[] raw;

        public EcdsaPubKey(byte[] byski, byte[] pubder) {
        	this.byski = new byte[byski.length + 1];
        	this.byski[0] = 0x02;
            System.arraycopy(byski, 0, this.byski, 1, byski.length);
            this.raw = pubder;
        }

        @Override
        public byte[] toBytes() {
            // TODO Auto-generated method stub
            //raw
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
