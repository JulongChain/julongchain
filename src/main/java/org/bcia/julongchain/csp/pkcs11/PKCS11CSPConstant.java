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
 * @author Ying Xu
 * @date 4/19/18
 * @company FEITIAN
 */
public class PKCS11CSPConstant {

    // ECDSA
    public static final String ECDSA = "ECDSA";
    public static final String ECDSA192 = "ECDSA192";
    // ECDSA P-256 curve
    public static final String ECDSA256 = "ECDSA256";
    // ECDSA P-384 curve
    public static final String ECDSA384 = "ECDSA384";
    // ECDSAReRand ECDSA key re-randomization
    public static final String ECDSAReRand = "ECDSAReRand";
    // RSA
    public static final String RSA = "RSA";
    // RSA 1024 bit
    public static final String RSA1024 = "RSA1024";
    // RSA 2048 bit
    public static final String RSA2048 = "RSA2048";
    // RSA 3072 bit
    public static final String RSA3072 = "RSA3072";
    // RSA 4096 bit
    public static final String RSA4096 = "RSA4096";
    // AES
    public static final String AES = "AES";
    // AES 128 bit
    public static final String AES128 = "AES128";
    // AES 192 bit
    public static final String AES192 = "AES192";
    // AES 256 bit
    public static final String AES256 = "AES256";


    public static final String HMAC = "HMAC";

    public static final String HMACTruncated256 = "HMAC_TRUNCATED_256";


    public static final String SHA = "SHA";

    public static final String SHA1 = "SHA1";

    // SHA2
    public static final String SHA2 = "SHA2";
    // SHA3
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
    
    
    public static final byte TAG = 0x04;
    public static final int  CARDINAL_NUM = 2;
    
    public static final int CLS_SELF = 0;
    public static final int CLS_GENKEY = 1;
    public static final int CLS_IMPORTKEY = 2;
    public static final int CLS_GETKEY = 3;
    public static final int CLS_SIGN = 4;
    public static final int CLS_VERIFY = 5;
    public static final int CLS_ENCRYPT = 6;
    public static final int CLS_DECRYPT = 7;
    public static final int CLS_DERIV = 8;
    
}
