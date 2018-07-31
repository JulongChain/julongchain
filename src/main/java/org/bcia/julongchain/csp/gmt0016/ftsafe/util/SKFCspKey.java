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

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class SKFCspKey {

    public static class ECCPublicKeyBlob {
        private byte[] xCoordinate;
        private byte[] yCoordinate;
        private long lBit;

        public ECCPublicKeyBlob() {
            this.xCoordinate = new byte[(int)(ECC_MAX_XCOORDINATE_BITS_LEN)/8];
            this.yCoordinate = new byte[(int)(ECC_MAX_YCOORDINATE_BITS_LEN)/8];
            this.lBit = 256;
        }

        public ECCPublicKeyBlob(byte[] xCoordinate, byte[] yCoordinate, long lBit) {
            this.xCoordinate = new byte[xCoordinate.length];
            System.arraycopy(xCoordinate, 0, this.xCoordinate, 0, xCoordinate.length);
            this.yCoordinate = new byte[yCoordinate.length];
            System.arraycopy(yCoordinate, 0, this.yCoordinate, 0, yCoordinate.length);
            this.lBit = lBit;
        }

        public byte[] getxCoordinate() {
            return xCoordinate;
        }

        public byte[] getyCoordinate() {
            return yCoordinate;
        }

        public long getBit() {
            return lBit;
        }

        public void setxCoordinate(byte[] xCoordinate) {
            System.arraycopy(xCoordinate, 0, this.xCoordinate, 0, xCoordinate.length);
        }

        public void setyCoordinate(byte[] yCoordinate) {
            System.arraycopy(yCoordinate, 0, this.yCoordinate, 0, yCoordinate.length);
        }

        public void setBit(long lBit) {
            this.lBit = lBit;
        }
    }

    public static class ECCPrivateKeyBlob{
        private long lBit;
        private byte[] privateKey;

        public ECCPrivateKeyBlob() {
            this.lBit = 256L;
            this.privateKey = new byte[(int)ECC_MAX_MODULUS_BITS_LEN/8];
        }

        public ECCPrivateKeyBlob(long lBit, byte[] privateKey) {
            this.lBit = lBit;
            this.privateKey = new byte[privateKey.length];
            System.arraycopy(privateKey, 0, this.privateKey, 0, privateKey.length);
        }

        public byte[] getPrivateKey() {
            return privateKey;
        }

        public long getBit() {
            return lBit;
        }
    }

    public static class RSAPublicKeyBlob{
        private long lAlgID;
        private long lBitLen;
        private byte[] modulus;
        private byte[] publicExponent;

        public RSAPublicKeyBlob() {
            this.modulus = new byte[(int)MAX_RSA_MODULUS_LEN];
            this.publicExponent = new byte[(int)MAX_RSA_EXPONENT_LEN];
            this.lBitLen = 0L;
            this.lAlgID = 0L;
        }

        public RSAPublicKeyBlob(long lAlgID, long lBit, byte[] modulus, byte[] publicExponent) {
            this.lBitLen = lBit;
            this.lAlgID = lAlgID;
            this.modulus = new byte[modulus.length];
            this.publicExponent = new byte[publicExponent.length];
            System.arraycopy(modulus, 0, this.modulus, 0, modulus.length);
            System.arraycopy(publicExponent, 0, this.publicExponent, 0, publicExponent.length);
        }

        public void setModulus(byte[] modulus) {
            this.modulus = modulus;
        }

        public void setpublicExponent(byte[] publicExponent) {
            this.publicExponent = publicExponent;
        }

        public void setBitLen(long bitlen) {
            this.lBitLen = bitlen;
        }

        public byte[] getModulus() {
            return modulus;
        }

        public byte[] getPublicExponent() {
            return publicExponent;
        }

        public long getAlgID() {
            return lAlgID;
        }

        public long getBitLen() {
            return lBitLen;
        }
    }

    public static class RSAPrivateKeyBlob{
        private long lAlgID;
        private long lBitLen;
        private byte[] modulus;
        private byte[] publicExponent;
        private byte[] privateExponent;
        private byte[] prime1;
        private byte[] prime2;
        private byte[] prime1Exponent;
        private byte[] prime2Exponent;
        private byte[] coefficient;

        public RSAPrivateKeyBlob() {
            this.lBitLen = 0L;
            this.lAlgID = 0L;
            this.modulus = new byte[(int)MAX_RSA_MODULUS_LEN];
            this.publicExponent = new byte[(int)MAX_RSA_EXPONENT_LEN];
            this.privateExponent = new byte[(int)MAX_RSA_MODULUS_LEN];
            this.prime1 = new byte[(int)MAX_RSA_MODULUS_LEN/2];
            this.prime2 = new byte[(int)MAX_RSA_MODULUS_LEN/2];
            this.prime1Exponent = new byte[(int)MAX_RSA_MODULUS_LEN/2];
            this.prime2Exponent = new byte[(int)MAX_RSA_MODULUS_LEN/2];
            this.coefficient = new byte[(int)MAX_RSA_MODULUS_LEN/2];
        }

        public RSAPrivateKeyBlob(long lAlgID, long lBitLen, byte[] modulus,
                                 byte[] publicExponent,byte[] privateExponent,byte[] prime1,
                                 byte[] prime2,byte[] prime1Exponent,byte[] prime2Exponent,byte[] coefficient) {
            this.lBitLen = lAlgID;
            this.lAlgID = lBitLen;
            this.modulus = new byte[modulus.length];
            this.publicExponent = new byte[publicExponent.length];
            this.privateExponent = new byte[privateExponent.length];
            this.prime1 = new byte[prime1.length];
            this.prime2 = new byte[prime2.length];
            this.prime1Exponent = new byte[prime1Exponent.length];
            this.prime2Exponent = new byte[prime2Exponent.length];
            this.coefficient = new byte[coefficient.length];
            System.arraycopy(modulus, 0, this.modulus, 0, modulus.length);
            System.arraycopy(publicExponent, 0, this.publicExponent, 0, publicExponent.length);
            System.arraycopy(privateExponent, 0, this.privateExponent, 0, privateExponent.length);
            System.arraycopy(prime1, 0, this.prime1, 0, prime1.length);
            System.arraycopy(prime2, 0, this.prime2, 0, prime2.length);
            System.arraycopy(prime1Exponent, 0, this.prime1Exponent, 0, prime1Exponent.length);
            System.arraycopy(prime2Exponent, 0, this.prime2Exponent, 0, prime2Exponent.length);
            System.arraycopy(coefficient, 0, this.coefficient, 0, coefficient.length);

        }

        public byte[] getModulus() {
            return modulus;
        }

        public byte[] getPublicExponent() {
            return publicExponent;
        }

        public long getAlgID() {
            return lAlgID;
        }

        public long getBitLen() {
            return lBitLen;
        }

        public byte[] getPrivateExponent() {
            return privateExponent;
        }

        public byte[] getPrime1() {
            return prime1;
        }

        public byte[] getPrime2() {
            return prime2;
        }

        public byte[] getPrime1Exponent() {
            return prime1Exponent;
        }

        public byte[] getPrime2Exponent() {
            return prime2Exponent;
        }

        public byte[] getCoefficient() {
            return coefficient;
        }
    }
}
