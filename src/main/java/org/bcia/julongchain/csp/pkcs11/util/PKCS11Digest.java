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

package org.bcia.julongchain.csp.pkcs11.util;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.pkcs11.IPKCS11FactoryOpts;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * hard hash impl
 *
 * @author xuying
 * @date 2018/05/21
 * @company FEITIAN
 */
public class PKCS11Digest {
    // length of the digest in bytes
    private static int digestLength;
    private static long mechanism;
    private static byte[] message;

    private static final int BUFFERSIZE = 96;
    
    private static JavaChainLog logger;

    public PKCS11Digest(long mechanism) throws JavaChainException{
        this.mechanism = mechanism;
        switch((int)mechanism) {
            case (int)CKM_MD2:
            case (int)CKM_MD5:
                digestLength = 16;
                break;
            case (int)CKM_SHA_1:
                digestLength = 20;
                break;
            case (int)CKM_SHA256:
                digestLength = 32;
                break;
            case (int)CKM_SHA384:
                digestLength = 48;
                break;
            default:
                throw new JavaChainException("[JC_PKCS]: no support the alg!");
        }
    }

    public byte[] getDigest(byte[] msg, IPKCS11FactoryOpts opts) throws JavaChainException{

        try {
            CK_MECHANISM ckm = new CK_MECHANISM();
            ckm.mechanism = mechanism;
            //opts.getPKCS11().C_DigestInit(opts.getSessionhandle(), ckm);
            byte[] digest = new byte[digestLength];
            int rv = opts.getPKCS11().C_DigestSingle(opts.getSessionhandle(), ckm,
                    msg, 0, msg.length, digest, 0, digestLength);
            if (rv == digestLength)
            {
                return digest;
            }
            else{
            	logger.error("[JC_PKCS]: digest methor1 data length error!");
                throw new JavaChainException("[JC_PKCS]: digest methor1 data length error!");
            }

        }catch(PKCS11Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
            logger.error(err);
            throw new JavaChainException(err, ex.getCause());
        }
    }

    public static byte[] getDigestwithUpdate(byte[] msg, IPKCS11FactoryOpts opts) throws JavaChainException{

        try {

            CK_MECHANISM ckm = new CK_MECHANISM();
            ckm.mechanism = mechanism;
            opts.getPKCS11().C_DigestInit(opts.getSessionhandle(), ckm);
            byte[] digest = new byte[digestLength];
            opts.getPKCS11().C_DigestUpdate(opts.getSessionhandle(), 0, msg, 0, msg.length);
            int rv = opts.getPKCS11().C_DigestFinal(opts.getSessionhandle(), digest, 0, digestLength);
            if (rv == digestLength)
            {
                return digest;
            }
            else{
            	logger.error("[JC_PKCS]: digest methor2 data length error!");
                throw new JavaChainException("[JC_PKCS]: digest methor2 data length error!");
            }

        }catch(PKCS11Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_PKCS]:PKCS11Exception ErrCode: 0x%08x", ex.getErrorCode());
            throw new JavaChainException(err, ex.getCause());
        }

    }


    public static IHash getHash(IPKCS11FactoryOpts opts){

        IHash hash = new IHash() {

            public synchronized int write(byte[] p) {
            	if(message != null)
            	{
            		byte[] temp = new byte[p.length + message.length];
            		System.arraycopy(message, 0, temp, 0, message.length);
            		System.arraycopy(p, 0, temp, message.length, p.length);
            		message = temp;
            	}
            	else
            		message = p;            	
                
                return message.length;
            }

            public byte[] sum(byte[] b){
                try {
                    byte[] data = new byte[message.length + b.length];
                    System.arraycopy(message, 0, data, 0, message.length);
                    System.arraycopy(b, 0, data, message.length, b.length);
                    byte[] digest = getDigestwithUpdate(data, opts);
                    return digest;
                } catch (JavaChainException ex) {
                	logger.error("[JC_PKCS]: sum error!");
                    return null;
                }
            }

            public void reset() {
                try {
                    byte[] buffer = new byte[BUFFERSIZE];
                    getDigestwithUpdate(buffer, opts);
                } catch (JavaChainException ex) {
                	logger.error("[JC_PKCS]: reset error!");
                	return;
                }
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
