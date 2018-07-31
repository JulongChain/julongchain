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

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Class description
 *
 * @author
 * @date 5/25/18
 * @company FEITIAN
 */
public class SignImpl {

    public byte[] signData(IKey key, byte[] degiest, String alg) throws JavaChainException {

        try {
            if(key instanceof RsaKeyOpts.RsaPriKey)
            {
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.toBytes());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPrivateKey rsakey = (RSAPrivateKey)keyFactory.generatePrivate(spec);
                Signature signature = Signature.getInstance(alg);
                signature.initSign(rsakey);
                signature.update(degiest);
                byte[] sigBytes = signature.sign();
                return sigBytes;
            }
            if(key instanceof EcdsaKeyOpts.EcdsaPriKey)
            {
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.toBytes());
                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                ECPrivateKey ecdsakey = (ECPrivateKey)keyFactory.generatePrivate(spec);
                Signature signature = Signature.getInstance(alg);
                signature.initSign(ecdsakey);
                signature.update(degiest);
                byte[] sigBytes = signature.sign();
                return sigBytes;
            }
            return null;
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
