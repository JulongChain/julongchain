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
package org.bcia.julongchain.csp.gmt0016.ftsafe.symmetry;

import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.BlockCipherParam;
import org.bcia.julongchain.csp.intfs.opts.IDecrypterOpts;
import org.bcia.julongchain.csp.intfs.opts.IEncrypterOpts;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class SM4Opts {

    public static class SM4ECBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SM4ECBKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM4_ECB;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SMS4_ECB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SM4CBCKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SM4CBCKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM4_CBC;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SMS4_CBC;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SM4CFBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SM4CFBKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM4_CFB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SMS4_CFB;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SM4OFBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SM4OFBKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM4_OFB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SMS4_OFB;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SM4MACKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SM4MACKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM4_MAC;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SMS4_MAC;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SM4EncrypterOpts implements IEncrypterOpts {

        private byte[] byteIV;
        private long lPadding;
        private long lFeedBitLen;

        public SM4EncrypterOpts(byte[] byteIV, long lPadding, long lfeedbit) {
            this.byteIV = byteIV;
            this.lPadding = lPadding;
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM4;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, lPadding, lFeedBitLen);
            return blockcipher;
        }

    }

    public static class SM4DecrypterOpts implements IDecrypterOpts {

        private byte[] byteIV;
        private long lPadding;
        private long lFeedBitLen;

        public SM4DecrypterOpts(byte[] byteIV, long lPadding, long lfeedbit) {
            this.byteIV = byteIV;
            this.lPadding = lPadding;
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM4;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, lPadding, lFeedBitLen);
            return blockcipher;
        }

    }
}