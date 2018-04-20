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
package org.bcia.javachain.csp.pkcs11.util;

import org.bcia.javachain.csp.pkcs11.IPKCS11FactoryOpts;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.util.DerValue;
import sun.security.util.ECUtil;
import sun.security.util.Length;
import sun.security.x509.X509Key;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;

import static sun.security.pkcs11.wrapper.PKCS11Constants.*;

/**
 * Class description
 *
 * @author
 * @date 4/19/18
 * @company FEITIAN
 */
public abstract class PKCS11CspKey  implements Key, Length {

    private final static String PUBLIC = "public";
    private final static String PRIVATE = "private";
    // type of key, one of (PUBLIC, PRIVATE, SECRET)
    final String type;
    // effective key length of the key, e.g. 56 for a DES key
    final int keyLength;
    // algorithm name, returned by getAlgorithm(), etc.
    final String algorithm;
    // key id
    final long keyID;

    //final PKCS11 p11;

    final IPKCS11FactoryOpts opts;


    public PKCS11CspKey(String type, IPKCS11FactoryOpts opts,long keyID, String algorithm, int keyLength, CK_ATTRIBUTE[] attributes){
        this.algorithm = algorithm;
        this.type = type;
        this.keyID = keyID;
        this.keyLength = keyLength;
        this.opts = opts;
        int n = (attributes == null) ? 0 : attributes.length;
        for (int i = 0; i < n; i++) {
            CK_ATTRIBUTE attr = attributes[i];
        }
    }

    //Returns the standard algorithm name for this key.
    public final String getAlgorithm(){
        return algorithm;
    }


    //Returns the key in its primary encoding format, or null if this key does not support encoding.
    public final byte[] getEncoded() {
        byte[] b = getEncodedInternal();
        return (b == null) ? null : b.clone();
    }

    abstract byte[] getEncodedInternal();
    /**
     * Return bit length of the key.
     */
    public int length() {
        //return keyLength;
        return 0;
    }

    void fetchAttributes(CK_ATTRIBUTE[] attributes) {
        long tempSessionHandel = 0;
        try {
            tempSessionHandel = this.opts.getSessionhandle();
            opts.getPKCS11().C_GetAttributeValue(tempSessionHandel, keyID, attributes);
        } catch (PKCS11Exception e) {
            throw new ProviderException(e);
        }
    }

    private final static CK_ATTRIBUTE[] A0 = new CK_ATTRIBUTE[0];
    private static CK_ATTRIBUTE[] getAttributes(PKCS11 p11, long sessionhandel, long keyID,
                                                CK_ATTRIBUTE[] knownAttributes, CK_ATTRIBUTE[] desiredAttributes) {
        if (knownAttributes == null) {
            knownAttributes = A0;
        }
        for (int i = 0; i < desiredAttributes.length; i++) {
            // For each desired attribute, check to see if we have the value
            // available already. If everything is here, we save a native call.
            CK_ATTRIBUTE attr = desiredAttributes[i];
            for (CK_ATTRIBUTE known : knownAttributes) {
                if ((attr.type == known.type) && (known.pValue != null)) {
                    attr.pValue = known.pValue;
                    break; // break inner for loop
                }
            }
            if (attr.pValue == null) {
                // nothing found, need to call C_GetAttributeValue()
                for (int j = 0; j < i; j++) {
                    // clear values copied from knownAttributes
                    desiredAttributes[j].pValue = null;
                }
                try {
                    p11.C_GetAttributeValue
                            (sessionhandel, keyID, desiredAttributes);
                } catch (PKCS11Exception e) {
                    throw new ProviderException(e);
                }
                break; // break loop, goto return
            }
        }
        return desiredAttributes;
    }

    public static PublicKey publicKey(IPKCS11FactoryOpts opts, long keyID, String algorithm,
                                      int keyLength, CK_ATTRIBUTE[] attributes) {
        switch (algorithm) {

            case "EC":
                return new P11ECPublicKey
                        (opts, keyID, algorithm, keyLength, attributes);
            default:
                throw new ProviderException
                        ("Unknown public key algorithm " + algorithm);
        }
    }



    public static PrivateKey privateKey(IPKCS11FactoryOpts opts, long keyID, String algorithm,
                                        int keyLength, CK_ATTRIBUTE[] attributes) {
        attributes = getAttributes(opts.getPKCS11(), opts.getSessionhandle(), keyID, attributes, new CK_ATTRIBUTE[] {
                new CK_ATTRIBUTE(CKA_TOKEN),
                new CK_ATTRIBUTE(CKA_SENSITIVE),
                new CK_ATTRIBUTE(CKA_EXTRACTABLE),
        });
        if (attributes[1].getBoolean() || (attributes[2].getBoolean() == false)) {
            return new P11PrivateKey
                    (opts, keyID, algorithm, keyLength, attributes);
        } else {
            switch (algorithm) {
                case "EC":
                    return new P11ECPrivateKey
                            (opts, keyID, algorithm, keyLength, attributes);
                default:
                    throw new ProviderException
                            ("Unknown private key algorithm " + algorithm);
            }
        }

    }


    // class for sensitive and unextractable private keys
    private static final class P11PrivateKey extends PKCS11CspKey
            implements PrivateKey {

        P11PrivateKey(IPKCS11FactoryOpts opts, long keyID, String algorithm,
                      int keyLength, CK_ATTRIBUTE[] attributes) {
            super(PRIVATE, opts, keyID, algorithm, keyLength, attributes);
        }
        // XXX temporary encoding for serialization purposes
        public String getFormat() {
            return null;
        }
        byte[] getEncodedInternal() {
            return null;
        }
    }


    private static final class P11ECPrivateKey extends PKCS11CspKey implements ECPrivateKey {

        private BigInteger s;
        private ECParameterSpec params;
        private byte[] encoded;
        P11ECPrivateKey(IPKCS11FactoryOpts opts, long keyID, String algorithm,
                        int keyLength, CK_ATTRIBUTE[] attributes) {
            super(PRIVATE, opts, keyID, algorithm, keyLength, attributes);
        }
        private synchronized void fetchValues() {
            if (s != null) {
                return;
            }
            CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                    new CK_ATTRIBUTE(CKA_VALUE),
                    new CK_ATTRIBUTE(CKA_EC_PARAMS, params),
            };
            fetchAttributes(attributes);
            s = attributes[0].getBigInteger();
            try {
                params = ECUtil.getECParameterSpec(Security.getProvider("SunEC"),attributes[1].getByteArray());
            } catch (Exception e) {
                throw new RuntimeException("Could not parse key values", e);
            }
        }
        public String getFormat() {

            return "PKCS#8";
        }
        synchronized byte[] getEncodedInternal() {

            if (encoded == null) {
                fetchValues();
                try {
                    KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
                    ECPrivateKeySpec keySpec = new ECPrivateKeySpec(s, params);
                    Key key =  (ECPrivateKey)keyFactory.generatePrivate(keySpec);
                    encoded = key.getEncoded();
                } catch (InvalidKeySpecException e) {
                    throw new ProviderException(e);
                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    throw new RuntimeException(e);
                }
            }
            return encoded;
        }
        public BigInteger getS() {
            fetchValues();
            return s;
        }
        public ECParameterSpec getParams() {
            fetchValues();
            return params;
        }
    }




    private static final class P11ECPublicKey extends PKCS11CspKey  implements ECPublicKey {

        private ECPoint w;
        private ECParameterSpec params;
        private byte[] encoded;
        P11ECPublicKey(IPKCS11FactoryOpts opts, long keyID, String algorithm,
                       int keyLength, CK_ATTRIBUTE[] attributes) {
            super(PUBLIC, opts, keyID, algorithm, keyLength, attributes);
        }

        private synchronized void fetchValues() {
            if (w != null) {
                return;
            }
            CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[] {
                    new CK_ATTRIBUTE(CKA_EC_POINT),
                    new CK_ATTRIBUTE(CKA_EC_PARAMS),
            };
            fetchAttributes(attributes);

            try {
                Provider p = Security.getProvider("SunEC");
                params = ECUtil.getECParameterSpec(p, attributes[1].getByteArray());
                byte[] ecKey = attributes[0].getByteArray();

                // Check whether the X9.63 encoding of an EC point is wrapped
                // in an ASN.1 OCTET STRING
                if (!opts.getuseEcX963Encoding()) {
                    DerValue wECPoint = new DerValue(ecKey);

                    if (wECPoint.getTag() != DerValue.tag_OctetString) {
                        throw new IOException("Could not DER decode EC point." +
                                " Unexpected tag: " + wECPoint.getTag());
                    }
                    w = ECUtil.decodePoint(wECPoint.getDataBytes(), params.getCurve());

                } else {
                    w = ECUtil.decodePoint(ecKey, params.getCurve());
                }

            } catch (Exception e) {
                throw new RuntimeException("Could not parse key values", e);
            }
        }
        public String getFormat() {
            return "X.509";
        }
        synchronized byte[] getEncodedInternal() {
            if (encoded == null) {
                fetchValues();
                try {
                    KeyFactory keyFactory = KeyFactory.getInstance("EC", "SunEC");
                    ECPublicKeySpec keySpec = new ECPublicKeySpec(w, params);
                    X509Key key = (X509Key)keyFactory.generatePublic(keySpec);
                    encoded = key.getEncoded();
                } catch (InvalidKeySpecException e) {
                    throw new ProviderException(e);
                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    throw new RuntimeException(e);
                }
            }
            return encoded;
        }
        public ECPoint getW() {
            fetchValues();
            return w;
        }
        public ECParameterSpec getParams() {
            fetchValues();
            return params;
        }
        public String toString() {
            fetchValues();
            return super.toString()
                    + "\n  public x coord: " + w.getAffineX()
                    + "\n  public y coord: " + w.getAffineY()
                    + "\n  parameters: " + params;
        }

    }

}
