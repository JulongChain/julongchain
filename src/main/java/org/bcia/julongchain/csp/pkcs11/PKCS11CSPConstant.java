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
package org.bcia.julongchain.csp.pkcs11;

/**
 * Class description
 *
 * @author
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11CSPConstant {

    // ECDSA Elliptic Curve Digital Signature Algorithm (key gen, import, sign, verify),
    // at default security level.
    // Each CSP may or may not support default security level. If not supported than
    // an error will be returned.
    public static final String ECDSA = "ECDSA";
    public static final String ECDSA192 = "ECDSA192";
    // ECDSA Elliptic Curve Digital Signature Algorithm over P-256 curve
    public static final String ECDSA256 = "ECDSA256";
    // ECDSA Elliptic Curve Digital Signature Algorithm over P-384 curve
    public static final String ECDSA384 = "ECDSA384";
    // ECDSAReRand ECDSA key re-randomization
    public static final String ECDSAReRand = "ECDSAReRand";
    // RSA at the default security level.
    // Each CSP may or may not support default security level. If not supported than
    // an error will be returned.
    public static final String RSA = "RSA";
    // RSA at 1024 bit security level.
    public static final String RSA1024 = "RSA1024";
    // RSA at 2048 bit security level.
    public static final String RSA2048 = "RSA2048";
    // RSA at 3072 bit security level.
    public static final String RSA3072 = "RSA3072";
    // RSA at 4096 bit security level.
    public static final String RSA4096 = "RSA4096";
    // AES Advanced Encryption Standard at the default security level.
    // Each CSP may or may not support default security level. If not supported than
    // an error will be returned.
    public static final String AES = "AES";
    // AES Advanced Encryption Standard at 128 bit security level
    public static final String AES128 = "AES128";
    // AES Advanced Encryption Standard at 192 bit security level
    public static final String AES192 = "AES192";
    // AES Advanced Encryption Standard at 256 bit security level
    public static final String AES256 = "AES256";

    // HMAC keyed-hash message authentication code
    public static final String HMAC = "HMAC";
    // HMACTruncated256 HMAC truncated at 256 bits.
    public static final String HMACTruncated256 = "HMAC_TRUNCATED_256";

    // SHA Secure Hash Algorithm using default family.
    // Each CSP may or may not support default security level. If not supported than
    // an error will be returned.
    public static final String SHA = "SHA";

    public static final String SHA1 = "SHA1";

    // SHA2 is an identifier for SHA2 hash family
    public static final String SHA2 = "SHA2";
    // SHA3 is an identifier for SHA3 hash family
    public static final String SHA3 = "SHA3";

    // SHA256
    public static final String SHA256 = "SHA256";
    // SHA384
    public static final String SHA384 = "SHA384";
    // SHA3_256
    public static final String SHA3_256 = "SHA3_256";
    // SHA3_384
    public static final String SHA3_384 = "SHA3_384";

    public static final String MD5 = "MD5";
    public static final String MD2 = "MD2";

    // X509Certificate Label for X509 certificate related operation
    public static final String X509Certificate = "X509Certificate";


    public static final long CKM_SHA3_256_RSA_PKCS = 0x00000060L;
    public static final long CKM_SHA3_384_RSA_PKCS = 0x00000061L;
    public static final long CKM_DERIVEECCKEY = 0x80000002L;
}
