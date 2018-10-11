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
 * SM1 算法相关选项
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public class SM1Opts {
	
	private static final int SM1_KEY_LEN = 16;
	private static final long SM1_ENC_PADDING = 1;
	private static final long SM1_ENC_NOPADDING = 0;

    public static class SM1ECBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;        

        public SM1ECBKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;            
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1_ECB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        @Override
        public long getAlgID() {
            return GMT0016CspConstant.SGD_SM1_ECB;
        }

        @Override
        public int getBitLen() {
            return SM1_KEY_LEN;
        }
    }

    public static class SM1CBCKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;        

        public SM1CBCKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;            
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1_CBC;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        @Override
        public long getAlgID() {
            return GMT0016CspConstant.SGD_SM1_CBC;
        }

        @Override
        public int getBitLen() {
            return SM1_KEY_LEN;
        }
    }

    public static class SM1CFBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;      

        public SM1CFBKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;            
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1_CFB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        @Override
        public long getAlgID() {
            return GMT0016CspConstant.SGD_SM1_CFB;
        }

        @Override
        public int getBitLen() {
            return SM1_KEY_LEN;
        }
    }

    public static class SM1OFBKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;        

        public SM1OFBKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;            
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1_OFB;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        @Override
        public long getAlgID() {
            return GMT0016CspConstant.SGD_SM1_OFB;
        }

        @Override
        public int getBitLen() {
            return SM1_KEY_LEN;
        }
    }

    public static class SM1MACKeyGenOpts implements ISymmKeyGenOpts {

        private boolean bTemporary;
       
        public SM1MACKeyGenOpts(boolean bTemporary) {
            this.bTemporary = bTemporary;            
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1_MAC;
        }

        @Override
        public boolean isEphemeral() {
            return bTemporary;
        }

        @Override
        public long getAlgID() {
            return GMT0016CspConstant.SGD_SM1_MAC;
        }

        @Override
        public int getBitLen() {
            return SM1_KEY_LEN;
        }
    }

    public static class SM1EncrypterNoPadOpts implements IEncrypterOpts {

        private byte[] byteIV;        
        private long lFeedBitLen;

        public SM1EncrypterNoPadOpts(byte[] byteIV, long lfeedbit) {
            this.byteIV = byteIV;           
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, SM1_ENC_NOPADDING, lFeedBitLen);
            return blockcipher;
        }

    }
    
    public static class SM1EncrypterOpts implements IEncrypterOpts {

        private byte[] byteIV;        
        private long lFeedBitLen;

        public SM1EncrypterOpts(byte[] byteIV, long lfeedbit) {
            this.byteIV = byteIV;            
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, SM1_ENC_PADDING, lFeedBitLen);
            return blockcipher;
        }

    }
    
    public static class SM1DecrypterNoPadOpts implements IDecrypterOpts {

        private byte[] byteIV;        
        private long lFeedBitLen;

        public SM1DecrypterNoPadOpts(byte[] byteIV, long lfeedbit) {
            this.byteIV = byteIV;            
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, SM1_ENC_NOPADDING, lFeedBitLen);
            return blockcipher;
        }

    }

    public static class SM1DecrypterOpts implements IDecrypterOpts {

        private byte[] byteIV;        
        private long lFeedBitLen;

        public SM1DecrypterOpts(byte[] byteIV, long lfeedbit) {
            this.byteIV = byteIV;            
            this.lFeedBitLen = lfeedbit;
        }

        @Override
        public String getAlgorithm() {
            return GMT0016CspConstant.SM1;
        }

        public BlockCipherParam getBlockChipher() {
            BlockCipherParam blockcipher = new BlockCipherParam(byteIV, byteIV.length, SM1_ENC_PADDING, lFeedBitLen);
            return blockcipher;
        }

    }
}
