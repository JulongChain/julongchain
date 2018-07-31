/**
 * Copyright BCIA. All Rights Reserved.
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
package org.bcia.julongchain.common.tools.cryptogen;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.CertificateValidity;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.KeyUsageExtension;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * cryptogen 工具类
 *
 * @author chenhao, yegangcheng
 * @date 2018/4/9
 * @company Excelsecu
 */
@SuppressWarnings("WeakerAccess")
public class Util {

    public static final int EXT_KEY_USAGE_ANY = 0;
    public static final int EXT_KEY_USAGE_SERVER_AUTH = 1;
    public static final int EXT_KEY_USAGE_CLIENT_AUTH = 2;
    public static final int EXT_KEY_USAGE_CODE_SIGNING = 3;
    public static final int EXT_KEY_USAGE_EMAIL_PROTECTION = 4;
    public static final int EXT_KEY_USAGE_IPSEC_END_SYSTEM = 5;
    public static final int EXT_KEY_USAGE_IPSEC_TUNNEL = 6;
    public static final int EXT_KEY_USAGE_IPSEC_USER = 7;
    public static final int EXT_KEY_USAGE_TIME_STAMPING = 8;
    public static final int EXT_KEY_USAGE_OCSP_SIGNING = 9;
    public static final int EXT_KEY_USAGE_MICROSOFT_SERVER_GATED_CRYPTO = 10;
    public static final int EXT_KEY_USAGE_NETSCAPE_SERVER_GATED_CRYPTO = 11;

    public static void pemExport(String path, String pemType, byte[] bytes) throws JavaChainException {

        File file = new File(path);
        if (file.exists()) {
            if (!file.delete()) {
                throw new JavaChainException("deleted the file unsuccessfully");
            }
        } else {
            File dir = new File(file.getParent());
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new JavaChainException("made dir failed in method newCA");
                }
            }
        }

        try {
            if (!file.createNewFile()) {
                throw new JavaChainException("created new file unsuccessfully");
            }
            PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(file)));
            PemObject pemObject = new PemObject(pemType, bytes);
            pemWriter.writeObject(pemObject);
            pemWriter.close();
        } catch (Exception e) {
            throw new JavaChainException("An error occurred on Util.pemExport :" + e.getMessage());
        }
    }

    public static KeyUsageExtension parseKeyUsage(int keyUsage) throws JavaChainException {
        KeyUsageExtension keyUsageExtension = new KeyUsageExtension();
        String keyUsageBinary = Integer.toBinaryString(keyUsage);
        int len = keyUsageBinary.length();
        for (int i = 0; i < len; i++) {
            try {
                switch (i) {
                    case 0:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.ENCIPHER_ONLY, true);
                        }
                        break;
                    case 1:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.CRL_SIGN, true);
                        }
                        break;
                    case 2:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.KEY_CERTSIGN, true);
                        }
                        break;
                    case 3:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.KEY_AGREEMENT, true);
                        }
                        break;
                    case 4:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.DATA_ENCIPHERMENT, true);
                        }
                        break;
                    case 5:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.KEY_ENCIPHERMENT, true);
                        }
                        break;
                    case 6:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.NON_REPUDIATION, true);
                        }
                        break;
                    case 7:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.DIGITAL_SIGNATURE, true);
                        }
                        break;
                    case 15:
                        if (keyUsageBinary.charAt(len - 1 - i) == '1') {
                            keyUsageExtension.set(KeyUsageExtension.DECIPHER_ONLY, true);
                        }
                        break;
                    default:
                        break;

                }
            } catch (Exception e) {
                throw new JavaChainException("An error occurred on parseKeyUsage:" + e.getMessage());
            }
        }
        return keyUsageExtension;
    }

    public static ExtendedKeyUsageExtension parseExtendedKeyUsage(int[] eku) throws IOException {
        Vector<ObjectIdentifier> vKeyOid = new Vector<>();
        for (int index : eku) {
            switch (index) {
                case EXT_KEY_USAGE_ANY:
                    vKeyOid.add(new ObjectIdentifier(new int[]{2, 5, 29, 37, 0}));
                    break;
                case EXT_KEY_USAGE_SERVER_AUTH:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 1}));
                    break;
                case EXT_KEY_USAGE_CLIENT_AUTH:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 2}));
                    break;
                case EXT_KEY_USAGE_CODE_SIGNING:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 3}));
                    break;
                case EXT_KEY_USAGE_EMAIL_PROTECTION:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 4}));
                    break;
                case EXT_KEY_USAGE_IPSEC_END_SYSTEM:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 5}));
                    break;
                case EXT_KEY_USAGE_IPSEC_TUNNEL:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 6}));
                    break;
                case EXT_KEY_USAGE_IPSEC_USER:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 7}));
                    break;
                case EXT_KEY_USAGE_TIME_STAMPING:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 8}));
                    break;
                case EXT_KEY_USAGE_OCSP_SIGNING:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 5, 5, 7, 3, 9}));
                    break;
                case EXT_KEY_USAGE_MICROSOFT_SERVER_GATED_CRYPTO:
                    vKeyOid.add(new ObjectIdentifier(new int[]{1, 3, 6, 1, 4, 1, 311, 10, 3, 3}));
                    break;
                case EXT_KEY_USAGE_NETSCAPE_SERVER_GATED_CRYPTO:
                    vKeyOid.add(new ObjectIdentifier(new int[]{2, 16, 840, 1, 113730, 4, 1}));
                    break;
                default:
                    break;
            }
        }
        return new ExtendedKeyUsageExtension(vKeyOid);
    }

    public static ECParameterSpec getECParameterSpec() {
        BigInteger n = new BigInteger(
                "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16);
        BigInteger p = new BigInteger(
                "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16);
        BigInteger a = new BigInteger(
                "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16);
        BigInteger b = new BigInteger(
                "28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16);
        BigInteger gx = new BigInteger(
                "32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16);
        BigInteger gy = new BigInteger(
                "BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16);

        EllipticCurve ellipticCurve = new EllipticCurve(new ECFieldFp(p), a, b);
        return new ECParameterSpec(ellipticCurve, new ECPoint(gx, gy), n, 1);
    }

    public static ECParameterSpec getFakeECParameterSpec() {
        KeyPair keyPair = generateDefaultKeyPair();
        return ((ECPublicKey) keyPair.getPublic()).getParams();
    }

    public static CertificateValidity getCertificateValidity(int years, int months, int days) {
        Date createTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, years);
        calendar.add(Calendar.MONTH, months);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return new CertificateValidity(createTime, calendar.getTime());
    }


    // --------------------test--------------------
    private static KeyPair generateDefaultKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(256);
            keyPair = keyGen.generateKeyPair();

            ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
            ecPrivateKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

}
