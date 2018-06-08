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
package org.bcia.julongchain.csp.pkcs11.util;

import org.bcia.julongchain.csp.intfs.IKey;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class SymmetryKey {
    public static class DESedePriKey implements IKey{
        private byte[] raw;
        private boolean exportable;
        private byte[] ski;

        public DESedePriKey(byte[] raw, byte[] ski,  boolean exportable) {
            this.raw = raw;
            this.exportable = exportable;
            this.ski = new byte[ski.length + 1];
        	this.ski[0] = 0x04;
            System.arraycopy(ski, 0, this.ski, 1, ski.length);
        }

        public byte[] toBytes(){

            if(exportable)
                return raw;
            else
                return null;
        }

        public byte[] ski() {

            return ski;
        }

        public boolean isSymmetric() {
            return true;
        }

        public boolean isPrivate() {
            return true;
        }

        public IKey getPublicKey() {
            return null;
        }
    }


    public static class AESPriKey implements IKey{
        private byte[] raw;
        private boolean exportable;
        private byte[] ski;

        public AESPriKey(byte[] raw, byte[] ski, boolean exportable) {
            this.raw = raw;
            this.exportable = exportable;
            this.ski = new byte[ski.length + 1];
        	this.ski[0] = 0x03;
            System.arraycopy(ski, 0, this.ski, 1, ski.length);
        }

        public byte[] toBytes(){

            if(exportable)
                return raw;
            else
                return null;
        }

        public byte[] ski() {
            return ski;
        }

        public boolean isSymmetric() {
            return true;
        }

        public boolean isPrivate() {
            return true;
        }

        public IKey getPublicKey() {
            return null;
        }
    }
}
