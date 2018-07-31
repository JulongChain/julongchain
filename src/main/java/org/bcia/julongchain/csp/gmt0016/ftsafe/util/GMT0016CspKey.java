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
package org.bcia.julongchain.csp.gmt0016.ftsafe.util;

import org.bcia.julongchain.csp.intfs.IKey;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class GMT0016CspKey {
    public static class RSAPrivateCspKey implements IKey{

        private byte[] raw;
        private byte[] ski;
        private byte[] pubraw;

        public RSAPrivateCspKey(byte[] ski, byte[] priraw, byte[] pubraw) {
            this.ski = new byte[ski.length];
            this.raw = new byte[priraw.length];
            this.pubraw = new byte[pubraw.length];
            System.arraycopy(ski, 0, this.ski, 0, ski.length);
            System.arraycopy(priraw, 0, this.raw, 0, priraw.length);
            System.arraycopy(pubraw, 0, this.pubraw, 0, pubraw.length);
        }

        public RSAPrivateCspKey(byte[] ski, byte[] pubraw) {
            this.raw = null;
            this.ski = new byte[ski.length];
            this.pubraw = new byte[pubraw.length];
            System.arraycopy(ski, 0, this.ski, 0, ski.length);
            System.arraycopy(pubraw, 0, this.pubraw, 0, pubraw.length);
        }

        @Override
        public byte[] toBytes() {
            return raw;
        }

        @Override
        public byte[] ski() {
            return ski;
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
            RSAPublicCspKey publicCspKey = new RSAPublicCspKey(ski, pubraw);
            return publicCspKey;
        }
    }

    public static class RSAPublicCspKey implements IKey{

        private byte[] raw;
        private byte[] ski;

        public RSAPublicCspKey(byte[] ski, byte[] raw) {
            this.ski = new byte[ski.length];
            this.raw = new byte[raw.length];
            System.arraycopy(ski, 0, this.ski, 0, ski.length);
            System.arraycopy(raw, 0, this.raw, 0, raw.length);
        }

        @Override
        public byte[] toBytes() {
            return raw;
        }

        @Override
        public byte[] ski() {
            return ski;
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

    public static class ECCPrivateCspKey implements IKey{


        private byte[] ski;
        private byte[] raw;

        public ECCPrivateCspKey(byte[] ski, byte[] raw) {
            this.ski = new byte[ski.length];
            this.raw = new byte[raw.length];

            System.arraycopy(ski, 0, this.ski, 0, ski.length);
            System.arraycopy(raw, 0, this.raw, 0, raw.length);
        }

        @Override
        public byte[] toBytes() {
            return null;
        }

        @Override
        public byte[] ski() {
            return ski;
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
            ECCPublicCspKey publicCspKey = new ECCPublicCspKey(ski, raw);
            return publicCspKey;
        }


    }

    public static class ECCPublicCspKey implements IKey{

        private byte[] raw;
        private byte[] ski;

        public ECCPublicCspKey(byte[] ski, byte[] raw) {
            this.ski = new byte[ski.length];
            this.raw = new byte[raw.length];

            System.arraycopy(ski, 0, this.ski, 0, ski.length);
            System.arraycopy(raw, 0, this.raw, 0, raw.length);
        }

        @Override
        public byte[] toBytes() {
            return raw;
        }

        @Override
        public byte[] ski() {
            return ski;
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
