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
package org.bcia.julongchain.csp.pkcs11.sw;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class HashImpl {
    // length of the digest in bytes
    private static int digestLength;
    private static byte[] message;
    private static final int BUFFERSIZE = 96;



    public static byte[] getAlgDigest(String alg, byte[] msg)  throws JavaChainException {
        try {
            MessageDigest hashalg = MessageDigest.getInstance(alg);
            hashalg.update(msg);
            byte[] digest = hashalg.digest();
            return digest;
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            throw new JavaChainException(err, ex.getCause());
        }

    }


    public static byte[] getSHA3AlgDigest(int bit, byte[] msg) {
        if(bit == 256)
        {
            SHA3.DigestSHA3 digestSHA3_256 = new SHA3.Digest256();
            digestSHA3_256.update(msg);
            return digestSHA3_256.digest(msg);
        }

        if(bit == 384)
        {
            SHA3.DigestSHA3 digestSHA3_384 = new SHA3.Digest384();
            digestSHA3_384.update(msg);
            return digestSHA3_384.digest(msg);
        }
        return null;
    }

    public static IHash getHash(String alg) throws JavaChainException{

        switch(alg) {
            case PKCS11CSPConstant.SHA256:
                digestLength = 32;
                break;
            case PKCS11CSPConstant.SHA384:
                digestLength = 48;
                break;
            case PKCS11CSPConstant.MD2:
            case PKCS11CSPConstant.MD5:
                digestLength = 16;
                break;
            case PKCS11CSPConstant.SHA1:
                digestLength = 20;
                break;
            case PKCS11CSPConstant.SHA3_256:
                digestLength = 64;
                break;
            case PKCS11CSPConstant.SHA3_384:
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

            public byte[] sum(byte[] b){
                byte[] data = new byte[message.length + b.length];
                System.arraycopy(message, 0, data, 0, message.length);
                System.arraycopy(b, 0, data, message.length, b.length);
                if(alg == PKCS11CSPConstant.SHA3_256)
                {
                    return getSHA3AlgDigest(256, data);
                }
                else if(alg == PKCS11CSPConstant.SHA3_384)
                {
                    return getSHA3AlgDigest(384, data);
                }
                else {

                    try {
                        return getAlgDigest(alg, data);
                    }catch(JavaChainException ex) {
                        ex.printStackTrace();
                    }
                    return null;
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
