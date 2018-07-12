package org.bcia.julongchain.csp.gm.dxct.sm2; /**
 * Copyright DingXuan. All Rights Reserved.
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

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author zhangmingyang
 * @Date: 2018/4/24
 * @company Dingxuan
 */
public class SM2 {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SM2.class);
    /**
     * 第一组参数
     */
//    public static BigInteger SM2_ECC_P = new BigInteger("8542D69E4C044F18E8B92435BF6FF7DE457283915C45517D722EDB8B08F1DFC3", 16);
//    public static BigInteger SM2_ECC_A = new BigInteger("787968B4FA32C3FD2417842E73BBFEFF2F3C848B6831D7E0EC65228B3937E498", 16);
//    public static BigInteger SM2_ECC_B = new BigInteger("63E4C6D3B23B0C849CF84241484BFE48F61D59A5B16BA06E6E12D1DA27C5249A", 16);
//    public static BigInteger SM2_ECC_N = new BigInteger("8542D69E4C044F18E8B92435BF6FF7DD297720630485628D5AE74EE7C32E79B7", 16);
//    public static BigInteger SM2_ECC_GX = new BigInteger("421DEBD61B62EAB6746434EBC3CC315E32220B3BADD50BDC4C4E6C147FEDD43D", 16);
//    public static BigInteger SM2_ECC_GY = new BigInteger("0680512BCBB42C07D47349D2153B70C4E5D7FDFCBFA36EA1A85841B9E46E09A2", 16);
    /**
     * 第二组参数
     */
    private static BigInteger SM2_ECC_N = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);
    private static BigInteger SM2_ECC_P = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16);
    private static BigInteger SM2_ECC_A = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16);
    private static BigInteger SM2_ECC_B = new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16);
    private static BigInteger SM2_ECC_GX = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    private static BigInteger SM2_ECC_GY = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);


    private static ECDomainParameters ecc_bc_spec;
    private static ECCurve.Fp curve;

    public SM2() {
        curve = new ECCurve.Fp(SM2_ECC_P, SM2_ECC_A, SM2_ECC_B);
        ECPoint G = curve.createPoint(SM2_ECC_GX, SM2_ECC_GY);
        ecc_bc_spec = new ECDomainParameters(curve, G, SM2_ECC_N);
    }

    /**
     * sm2密钥对生成
     *
     * @return
     */
    public SM2KeyPair generateKeyPair() {
        ECKeyGenerationParameters ecKeyGenerationParameters = new ECKeyGenerationParameters(ecc_bc_spec, new SecureRandom());
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();

        keyPairGenerator.init(ecKeyGenerationParameters);

        AsymmetricCipherKeyPair kp = keyPairGenerator.generateKeyPair();
        ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) kp.getPrivate();
        ECPublicKeyParameters ecpub = (ECPublicKeyParameters) kp.getPublic();
        BigInteger privateKey = ecpriv.getD();
        ECPoint publicKey = ecpub.getQ();
        return new SM2KeyPair(publicKey.getEncoded(false), privateKey.toByteArray());
    }


    /**
     * 字节数组转换为大整型
     *
     * @param b
     * @return
     */
    public static BigInteger byte2BigInteger(byte[] b) {
        if (b[0] < 0) {
            byte[] temp = new byte[b.length + 1];
            temp[0] = 0;
            System.arraycopy(b, 0, temp, 1, b.length);
            return new BigInteger(temp);
        }
        return new BigInteger(b);
    }

    /**
     * 对数据进行签名
     *
     * @param privateKey
     * @param msg
     * @return
     * @throws CryptoException
     */
    public byte[] sign(byte[] privateKey, byte[] msg) {
        SM2Signer signer = new SM2Signer();
        BigInteger d = byte2BigInteger(privateKey);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(d, ecc_bc_spec);
        signer.init(true, privateKeyParameters);
        signer.update(msg, 0, msg.length);
        byte[] sig = new byte[0];
        try {
            sig = signer.generateSignature();
        } catch (CryptoException e) {
            e.printStackTrace();
        }
        return sig;
    }

    /**
     * 验证签名值
     *
     * @param publicKey
     * @param signValue
     * @param msg
     * @return
     */
    public boolean verify(byte[] publicKey, byte[] signValue, byte[] msg) {
        SM2Signer signer = new SM2Signer();
        BigInteger[] rs = decode(signValue);
        ECPublicKeyParameters ecPub = new ECPublicKeyParameters(byte2ECpoint(publicKey), ecc_bc_spec);
        signer.init(false, ecPub);
        signer.update(msg, 0, msg.length);
        return signer.verifySignature(signValue);
    }

    /**
     * 公钥加密消息
     *
     * @param input
     * @param publicLKey
     * @return
     */
    public byte[] encrypt(byte[] input, byte[] publicLKey) {
        SM2Engine sm2Engine = new SM2Engine();
        ECPublicKeyParameters ecPub = new ECPublicKeyParameters(byte2ECpoint(publicLKey), ecc_bc_spec);
        ParametersWithRandom parametersWithRandom = new ParametersWithRandom(ecPub);
        sm2Engine.init(true, parametersWithRandom);
        try {
            byte[] enc = sm2Engine.processBlock(input, 0, input.length);
            return enc;
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥解密
     *
     * @param encryptData
     * @param privateKey
     * @return
     */
    public byte[] decrypt(byte[] encryptData, byte[] privateKey) {
        SM2Engine sm2Engine = new SM2Engine();
        BigInteger d = byte2BigInteger(privateKey);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(d, ecc_bc_spec);
        sm2Engine.init(false, privateKeyParameters);
        try {
            byte[] dec = sm2Engine.processBlock(encryptData, 0, encryptData.length);
            return dec;
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将摘要值r,s 转换为大整型数组
     *
     * @param sig
     * @return
     */
    private static BigInteger[] decode(byte[] sig) {
        ASN1Sequence s = ASN1Sequence.getInstance(sig);
        return new BigInteger[]{ASN1Integer.getInstance(s.getObjectAt(0)).getValue(),
                ASN1Integer.getInstance(s.getObjectAt(1)).getValue()};
    }

    /**
     * 字节转换为ECpoint
     *
     * @param publicKey
     * @return
     */
    public static ECPoint byte2ECpoint(byte[] publicKey) {
        byte[] formatedPubKey;
        if (publicKey.length == 64) {
            //添加一字节标识，用于ECPoint解析
            formatedPubKey = new byte[65];
            formatedPubKey[0] = 0x04;
            System.arraycopy(publicKey, 0, formatedPubKey, 1, publicKey.length);
        } else {
            formatedPubKey = publicKey;
        }
        ECPoint userKey = curve.decodePoint(formatedPubKey);
        return userKey;
    }
}
