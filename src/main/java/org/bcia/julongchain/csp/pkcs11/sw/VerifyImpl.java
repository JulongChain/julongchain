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
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.pkcs11.ecdsa.EcdsaKeyOpts;
import org.bcia.julongchain.csp.pkcs11.rsa.RsaKeyOpts;
import sun.security.ec.ECPublicKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class VerifyImpl {
    public boolean verifyData(IKey key, byte[] degiest, byte[] signature, String alg) throws JavaChainException {
        try {
            if(key instanceof RsaKeyOpts.RsaPriKey)
            {
                X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getPublicKey().toBytes());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKeyImpl rsakey = (RSAPublicKeyImpl)keyFactory.generatePublic(spec);
                Signature signature1 = Signature.getInstance(alg);
                signature1.initVerify(rsakey);
                signature1.update(degiest);
                boolean result = signature1.verify(signature);
            }
            if(key instanceof RsaKeyOpts.RsaPubKey)
            {
                X509EncodedKeySpec spec = new X509EncodedKeySpec(key.toBytes());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKeyImpl rsakey = (RSAPublicKeyImpl)keyFactory.generatePublic(spec);
                Signature signature1 = Signature.getInstance(alg);
                signature1.initVerify(rsakey);
                signature1.update(degiest);
                boolean result = signature1.verify(signature);
            }
            if(key instanceof EcdsaKeyOpts.EcdsaPriKey) {
                X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getPublicKey().toBytes());
                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                ECPublicKeyImpl ecdsakey = (ECPublicKeyImpl)keyFactory.generatePublic(spec);
                Signature signature1 = Signature.getInstance(alg);
                signature1.initVerify(ecdsakey);
                signature1.update(degiest);
                boolean result = signature1.verify(signature);
            }
            if(key instanceof EcdsaKeyOpts.EcdsaPubKey) {
                X509EncodedKeySpec spec = new X509EncodedKeySpec(key.toBytes());
                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                ECPublicKeyImpl ecdsakey = (ECPublicKeyImpl)keyFactory.generatePublic(spec);
                Signature signature1 = Signature.getInstance(alg);
                signature1.initVerify(ecdsakey);
                signature1.update(degiest);
                boolean result = signature1.verify(signature);
            }
            return false;
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:NoSuchAlgorithmException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(InvalidKeyException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeyException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(InvalidKeySpecException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:InvalidKeySpecException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }catch(SignatureException e) {
            e.printStackTrace();
            String err = String.format("[JC_PKCS_SOFT]:SignatureException ErrMessage: %s", e.getMessage());
            throw new JavaChainException(err, e.getCause());
        }
    }
}
