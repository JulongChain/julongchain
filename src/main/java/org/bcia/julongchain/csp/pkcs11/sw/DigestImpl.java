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

package org.bcia.julongchain.csp.pkcs11.sw;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.pkcs11.PKCS11CspLog;

import static org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant.*;
import org.bouncycastle.jcajce.provider.digest.SHA3;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 软实现计算摘要信息
 *
 * @author Ying Xu
 * @date 2018/05/21
 * @company FEITIAN
 */
public class DigestImpl {
	
    // length of the digest in bytes
    private static int digestLength;
    private static byte[] message;
    private static final int BUFFERSIZE = 96;

    PKCS11CspLog csplog = new PKCS11CspLog();

    public byte[] getAlgDigest(String alg, byte[] msg) {
        try {
            MessageDigest hashalg = MessageDigest.getInstance(alg);
            hashalg.update(msg);
            byte[] digest = hashalg.digest();
            return digest;
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            csplog.setLogMsg("[JC_PKCS_SOFT]:No Support Hash Alg!", csplog.LEVEL_ERROR, DigestImpl.class);   
        }
        return null;
    }

    public byte[] getSHA3AlgDigest(String alg, byte[] msg){
        if(alg == SHA3_256)
        {
            SHA3.DigestSHA3 digestSHA3_256 = new SHA3.Digest256();
            digestSHA3_256.update(msg);
            return digestSHA3_256.digest(msg);
        }

        if(alg == SHA3_384)
        {
            SHA3.DigestSHA3 digestSHA3_384 = new SHA3.Digest384();
            digestSHA3_384.update(msg);
            return digestSHA3_384.digest(msg);
        }
       
        csplog.setLogMsg("[JC_PKCS_SOFT]:No Support Hash Alg!", csplog.LEVEL_INFO, DigestImpl.class);       
        return null;
    }

    public IHash getHash(String alg) throws JulongChainException{

        switch(alg) {
            case SHA256:
                digestLength = 32;
                break;
            case SHA384:
                digestLength = 48;
                break;
            case MD2:
            case MD5:
                digestLength = 16;
                break;
            case SHA1:
                digestLength = 20;
                break;
            case SHA3_256:
                digestLength = 64;
                break;
            case SHA3_384:
                digestLength = 96;
                break;
            default:
                return null;
        }

        IHash hash = new IHash() {

            public int write(byte[] p) {
                message = new byte[p.length];
                System.arraycopy(p, 0, message, 0, p.length);
                return p.length;
            }

            public byte[] sum(byte[] b) {
            	
                byte[] data = new byte[message.length + b.length];
                System.arraycopy(message, 0, data, 0, message.length);
                System.arraycopy(b, 0, data, message.length, b.length);
                
                if(alg == SHA3_256 || alg == SHA3_384)
                {                	
                	byte[] hashdata = getSHA3AlgDigest(alg, data);
                	return hashdata;                	
                }
                else {
                    return getAlgDigest(alg, data);
                }
            	
            }

            public void reset() {

            }

            public int size() {
                return digestLength;
            }

            public int blockSize() {
                return BUFFERSIZE;
            }
        };

        return hash;
    }
}
