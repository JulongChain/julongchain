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
public class SSF33Opts {

    public static class SSF33ECBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SSF33ECBKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SSF33_ECB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SSF33_ECB;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SSF33CBCKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SSF33CBCKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SSF33_CBC;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SSF33_CBC;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SSF33CFBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SSF33CFBKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SSF33_CFB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SSF33_CFB;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SSF33OFBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SSF33OFBKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SSF33_OFB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SSF33_OFB;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SSF33MACKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
        private int bit;

        public SSF33MACKeyGenOpts(boolean bTemporary, int bit) {
            this.bTemporary = bTemporary;
            this.bit = bit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SSF33_MAC;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        public long getAlgID() {
            return GMT0016CspConstant.SGD_SSF33_MAC;
        }

        public int getBitLen() {
            return bit;
        }
    }

    public static class SSF33EncrypterOpts implements IEncrypterOpts {

        private byte[] byteIV;
        private long lPadding;
        private long lFeedBitLen;

        public SSF33EncrypterOpts(byte[] byteIV, long lPadding, long lfeedbit) {
            this.byteIV = byteIV;
            this.lPadding = lPadding;
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SSF33;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, lPadding, lFeedBitLen);
            return blockcipher;
        }

    }

    public static class SSF33DecrypterOpts implements IDecrypterOpts {

        private byte[] byteIV;
        private long lPadding;
        private long lFeedBitLen;

        public SSF33DecrypterOpts(byte[] byteIV, long lPadding, long lfeedbit) {
            this.byteIV = byteIV;
            this.lPadding = lPadding;
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SSF33;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, lPadding, lFeedBitLen);
            return blockcipher;
        }

    }
}
