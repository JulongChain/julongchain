package org.bcia.javachain.csp.gm;

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

import org.bouncycastle.asn1.*;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author zhanglin
 * @purpose Define the class, SM2
 * @date 2018-01-25
 * @company Dingxuan
 */

public class SM2 {
    private static BigInteger n = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16);
    private static BigInteger p = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16);
    private static BigInteger a = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16);
    private static BigInteger b = new BigInteger(
            "28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16);
    private static BigInteger gx = new BigInteger(
            "32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16);
    private static BigInteger gy = new BigInteger(
            "BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16);
    private static ECDomainParameters ecc_bc_spec;
    private static int w = (int) Math.ceil(n.bitLength() * 1.0 / 2) - 1;
    private static BigInteger _2w = new BigInteger("2").pow(w);
    private static final int DIGEST_LENGTH = 32;
    private final SM3Digest digest = new SM3Digest();
    private static SecureRandom random = new SecureRandom();
    private static ECCurve.Fp curve;
    private static ECPoint G;
    private boolean debug = false;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 以16进制打印字节数组
     *
     * @param b
     */
    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());
        }
        System.out.println();
    }

    /**
     * 随机数生成器
     *
     * @param max
     * @return
     */
    private static BigInteger random(BigInteger max) {

        BigInteger r = new BigInteger(256, random);
        // int count = 1;

        while (r.compareTo(max) >= 0) {
            r = new BigInteger(128, random);
            // count++;
        }

        // System.out.println("count: " + count);
        return r;
    }

    /**
     * 判断字节数组是否全0
     *
     * @param buffer
     * @return
     */
    private boolean allZero(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0)
                return false;
        }
        return true;
    }

    /**
     * 公钥加密
     *
     * @param input
     *            加密原文
     * @param publicKey
     *            公钥
     * @return
     */
    public byte[] encrypt(String input, ECPoint publicKey) {

        byte[] inputBuffer = input.getBytes();
        if (debug) {
            printHexString(inputBuffer);
        }

        byte[] C1Buffer;
        ECPoint kpb;
        byte[] t;
        do {
            /* 1 产生随机数k，k属于[1, n-1] */
            BigInteger k = random(n);
            if (debug) {
                System.out.print("k: ");
                printHexString(k.toByteArray());
            }

            /* 2 计算椭圆曲线点C1 = [k]G = (x1, y1) */
            ECPoint C1 = G.multiply(k);
            C1Buffer = C1.getEncoded(false);
            if (debug) {
                System.out.print("C1: ");
                printHexString(C1Buffer);
            }

            /*
             * 3 计算椭圆曲线点 S = [h]Pb
             */
            BigInteger h = ecc_bc_spec.getH();
            if (h != null) {
                ECPoint S = publicKey.multiply(h);
                if (S.isInfinity()) {
                    throw new IllegalStateException();
                }
            }

            /* 4 计算 [k]PB = (x2, y2) */
            kpb = publicKey.multiply(k).normalize();

            /* 5 计算 t = KDF(x2||y2, klen) */
            byte[] kpbBytes = kpb.getEncoded(false);
            t = KDF(kpbBytes, inputBuffer.length);
            // DerivationFunction kdf = new KDF1BytesGenerator(new
            // ShortenedDigest(new SHA256Digest(), DIGEST_LENGTH));
            //
            // t = new byte[inputBuffer.length];
            // kdf.init(new ISO18033KDFParameters(kpbBytes));
            // kdf.generateBytes(t, 0, t.length);
        } while (allZero(t));

        /* 6 计算C2=M^t */
        byte[] C2 = new byte[inputBuffer.length];
        for (int i = 0; i < inputBuffer.length; i++) {
            C2[i] = (byte) (inputBuffer[i] ^ t[i]);
        }

        /* 7 计算C3 = Hash(x2 || M || y2) */
        byte[] C3 = sm3hash(kpb.getXCoord().toBigInteger().toByteArray(), inputBuffer,
                kpb.getYCoord().toBigInteger().toByteArray());

        /* 8 输出密文 C=C1 || C2 || C3 */

        byte[] encryptResult = new byte[C1Buffer.length + C2.length + C3.length];

        System.arraycopy(C1Buffer, 0, encryptResult, 0, C1Buffer.length);
        System.arraycopy(C2, 0, encryptResult, C1Buffer.length, C2.length);
        System.arraycopy(C3, 0, encryptResult, C1Buffer.length + C2.length, C3.length);

        if (debug) {
            System.out.print("密文: ");
            printHexString(encryptResult);
        }

        return encryptResult;
    }

    /**
     * 私钥解密
     *
     * @param encryptData
     *            密文数据字节数组
     * @param privateKey
     *            解密私钥
     * @return
     */
    public String decrypt(byte[] encryptData, BigInteger privateKey) {

        if (debug) {
            System.out.println("encryptData length: " + encryptData.length);
        }

        byte[] C1Byte = new byte[65];
        System.arraycopy(encryptData, 0, C1Byte, 0, C1Byte.length);

        ECPoint C1 = curve.decodePoint(C1Byte).normalize();

        /*
         * 计算椭圆曲线点 S = [h]C1 是否为无穷点
         */
        BigInteger h = ecc_bc_spec.getH();
        if (h != null) {
            ECPoint S = C1.multiply(h);
            if (S.isInfinity()) {
                throw new IllegalStateException();
            }
        }
        /* 计算[dB]C1 = (x2, y2) */
        ECPoint dBC1 = C1.multiply(privateKey).normalize();

        /* 计算t = KDF(x2 || y2, klen) */
        byte[] dBC1Bytes = dBC1.getEncoded(false);
        int klen = encryptData.length - 65 - DIGEST_LENGTH;
        byte[] t = KDF(dBC1Bytes, klen);
        // DerivationFunction kdf = new KDF1BytesGenerator(new
        // ShortenedDigest(new SHA256Digest(), DIGEST_LENGTH));
        // if (debug)
        // System.out.println("klen = " + klen);
        // kdf.init(new ISO18033KDFParameters(dBC1Bytes));
        // kdf.generateBytes(t, 0, t.length);

        if (allZero(t)) {
            System.err.println("all zero");
            throw new IllegalStateException();
        }

        /* 5 计算M'=C2^t */
        byte[] M = new byte[klen];
        for (int i = 0; i < M.length; i++) {
            M[i] = (byte) (encryptData[C1Byte.length + i] ^ t[i]);
        }
        if (debug) {
            printHexString(M);
        }

        /* 6 计算 u = Hash(x2 || M' || y2) 判断 u == C3是否成立 */
        byte[] C3 = new byte[DIGEST_LENGTH];

        if (debug) {
            try {
                System.out.println("M = " + new String(M, "UTF8"));
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        System.arraycopy(encryptData, encryptData.length - DIGEST_LENGTH, C3, 0, DIGEST_LENGTH);
        byte[] u = sm3hash(dBC1.getXCoord().toBigInteger().toByteArray(), M,
                dBC1.getYCoord().toBigInteger().toByteArray());
        if (Arrays.equals(u, C3)) {
            if (debug) {
                System.out.println("解密成功");
            }
            try {
                return new String(M, "UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            if (debug) {
                System.out.print("u = ");
                printHexString(u);
                System.out.print("C3 = ");
                printHexString(C3);
                System.err.println("解密验证失败");
            }
            return null;
        }

    }

    // /**
    // * SHA摘要
    // * @param x2
    // * @param M
    // * @param y2
    // * @return
    // */
    // private byte[] calculateHash(BigInteger x2, byte[] M, BigInteger y2) {
    // ShortenedDigest digest = new ShortenedDigest(new SHA256Digest(),
    // DIGEST_LENGTH);
    // byte[] buf = x2.toByteArray();
    // digest.update(buf, 0, buf.length);
    // digest.update(M, 0, M.length);
    // buf = y2.toByteArray();
    // digest.update(buf, 0, buf.length);
    //
    // buf = new byte[DIGEST_LENGTH];
    // digest.doFinal(buf, 0);
    //
    // return buf;
    // }

    /**
     * 判断是否在范围内
     *
     * @param param
     * @param min
     * @param max
     * @return
     */
    private boolean between(BigInteger param, BigInteger min, BigInteger max) {
        if (param.compareTo(min) >= 0 && param.compareTo(max) < 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断生成的公钥是否合法
     *
     * @param publicKey
     * @return
     */
    private boolean checkPublicKey(ECPoint publicKey) {

        if (!publicKey.isInfinity()) {

            BigInteger x = publicKey.getXCoord().toBigInteger();
            BigInteger y = publicKey.getYCoord().toBigInteger();

            if (between(x, new BigInteger("0"), p) && between(y, new BigInteger("0"), p)) {

                BigInteger xResult = x.pow(3).add(a.multiply(x)).add(b).mod(p);

                if (debug) {
                    System.out.println("xResult: " + xResult.toString());
                }

                BigInteger yResult = y.pow(2).mod(p);

                if (debug) {
                    System.out.println("yResult: " + yResult.toString());
                }

                if (yResult.equals(xResult) && publicKey.multiply(n).isInfinity()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 生成密钥对
     *
     * @return
     */
    public SM2KeyPair generateKeyPair() {

        BigInteger d = random(n.subtract(new BigInteger("1")));

        SM2KeyPair keyPair = new SM2KeyPair(G.multiply(d).normalize(), d);

        if (checkPublicKey(keyPair.getPublicKey())) {
            if (debug) {
                System.out.println("generate key successfully");
            }
            return keyPair;
        } else {
            if (debug) {
                System.err.println("generate key failed");
            }
            return null;
        }
    }

    public SM2() {
        curve = new ECCurve.Fp(p, // q
                a, // a
                b); // b
        G = curve.createPoint(gx, gy);
        ecc_bc_spec = new ECDomainParameters(curve, G, n);
    }

    public SM2(boolean debug) {
        this();
        this.debug = debug;
    }

    /**
     * 导出公钥到本地
     *
     * @param publicKey
     * @param path
     */
    public void exportPublicKey(ECPoint publicKey, String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            byte buffer[] = publicKey.getEncoded(false);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地导入公钥
     *
     * @param path
     * @return
     */
    public ECPoint importPublicKey(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte buffer[] = new byte[16];
            int size;
            while ((size = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, size);
            }
            fis.close();
            return curve.decodePoint(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导出私钥到本地
     *
     * @param privateKey
     * @param path
     */
    public void exportPrivateKey(BigInteger privateKey, String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(privateKey);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地导入私钥
     *
     * @param path
     * @return
     */
    public BigInteger importPrivateKey(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            BigInteger res = (BigInteger) (ois.readObject());
            ois.close();
            fis.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节数组拼接
     *
     * @param params
     * @return
     */
    private static byte[] join(byte[]... params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] res = null;
        try {
            for (int i = 0; i < params.length; i++) {
                baos.write(params[i]);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * sm3摘要
     *
     * @param params
     * @return
     */
    private static byte[] sm3hash(byte[]... params) {
        byte[] res = null;
        try {
            res = SM3.hash(join(params));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 取得用户标识字节数组
     *
     * @param IDA
     * @param aPublicKey
     * @return
     */
    private static byte[] ZA(String IDA, ECPoint aPublicKey) {
        byte[] idaBytes = IDA.getBytes();
        int entlenA = idaBytes.length * 8;
        byte[] ENTLA = new byte[] { (byte) (entlenA & 0xFF00), (byte) (entlenA & 0x00FF) };
        byte[] ZA = sm3hash(ENTLA, idaBytes, a.toByteArray(), b.toByteArray(), gx.toByteArray(), gy.toByteArray(),
                aPublicKey.getXCoord().toBigInteger().toByteArray(),
                aPublicKey.getYCoord().toBigInteger().toByteArray());
        return ZA;
    }

    /**
     * 签名
     *
     * @param M
     *            签名信息
     * @param IDA
     *            签名方唯一标识
     * @param keyPair
     *            签名方密钥对
     * @return 签名
     */
    public byte[] sign(byte[] M, String IDA, SM2KeyPair keyPair) {
        byte[] ZA = ZA(IDA, keyPair.getPublicKey());
        byte[] M_ = join(ZA, M);
        BigInteger e = new BigInteger(1, sm3hash(M_));
        // BigInteger k = new BigInteger(
        // "6CB28D99 385C175C 94F94E93 4817663F C176D925 DD72B727 260DBAAE
        // 1FB2F96F".replace(" ", ""), 16);
        BigInteger k;
        BigInteger r;
        do {
            k = random(n);
            ECPoint p1 = G.multiply(k).normalize();
            BigInteger x1 = p1.getXCoord().toBigInteger();
            r = e.add(x1);
            r = r.mod(n);
        } while (r.equals(BigInteger.ZERO) || r.add(k).equals(n));

        BigInteger s = ((keyPair.getPrivateKey().add(BigInteger.ONE).modInverse(n))
                .multiply((k.subtract(r.multiply(keyPair.getPrivateKey()))).mod(n))).mod(n);
        try {
          byte[] signvalue=  derEncode(r, s);
            return signvalue;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * 验签
     *
     * @param M
     *            签名信息
     * @param
     *
     * @param IDA
     *            签名方唯一标识
     * @param aPublicKey
     *            签名方公钥
     * @return true or false
     */
    public boolean verify(byte[] M, byte[] signValue, String IDA, ECPoint aPublicKey) {
        Signature signature=new Signature();
        BigInteger[] r_s=decode(signValue);
        signature.setR(r_s[0]);
        signature.setS(r_s[1]);
        if (!between(signature.r, BigInteger.ONE, n)) {
            return false;
        }
        if (!between(signature.s, BigInteger.ONE, n)) {
            return false;
        }

        byte[] M_ = join(ZA(IDA, aPublicKey), M);
        BigInteger e = new BigInteger(1, sm3hash(M_));
        BigInteger t = signature.r.add(signature.s).mod(n);

        if (t.equals(BigInteger.ZERO)) {
            return false;
        }

        ECPoint p1 = G.multiply(signature.s).normalize();
        ECPoint p2 = aPublicKey.multiply(t).normalize();
        BigInteger x1 = p1.add(p2).normalize().getXCoord().toBigInteger();
        BigInteger R = e.add(x1).mod(n);
        if (R.equals(signature.r)) {
            return true;
        }
        return false;
    }

    /**
     * 密钥派生函数
     *
     * @param Z
     * @param klen
     *            生成klen字节数长度的密钥
     * @return
     */
    private static byte[] KDF(byte[] Z, int klen) {
        int ct = 1;
        int end = (int) Math.ceil(klen * 1.0 / 32);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (int i = 1; i < end; i++) {
                baos.write(sm3hash(Z, SM3.toByteArray(ct)));
                ct++;
            }
            byte[] last = sm3hash(Z, SM3.toByteArray(ct));
            if (klen % 32 == 0) {
                baos.write(last);
            } else {
                baos.write(last, 0, klen % 32);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥转换为ECPoint类型
     * @param publicKey
     * @return
     */
    public static ECPoint byte2ECpoint(byte[] publicKey){
        byte[] formatedPubKey;
        if (publicKey.length == 64){
            //添加一字节标识，用于ECPoint解析
            formatedPubKey = new byte[65];
            formatedPubKey[0] = 0x04;
            System.arraycopy(publicKey,0,formatedPubKey,1,publicKey.length);
        }
        else {
            formatedPubKey = publicKey;
        }

        ECPoint userKey = curve.decodePoint(formatedPubKey);
        return userKey;
    }
    /**
     * 传输实体类
     *
     * @author Potato
     *
     */
    private static class TransportEntity implements Serializable {
        final byte[] R; //R点
        final byte[] S; //验证S
        final byte[] Z; //用户标识
        final byte[] K; //公钥

        public TransportEntity(byte[] r, byte[] s,byte[] z,ECPoint pKey) {
            R = r;
            S = s;
            Z=z;
            K=pKey.getEncoded(false);
        }
    }

    /**
     * 密钥协商辅助类
     *
     * @author Potato
     *
     */
    public static class KeyExchange {
        BigInteger rA;
        ECPoint RA;
        ECPoint V;
        byte[] Z;
        byte[] key;

        String ID;
        SM2KeyPair keyPair;

        public KeyExchange(String ID,SM2KeyPair keyPair) {
            this.ID=ID;
            this.keyPair = keyPair;
            this.Z=ZA(ID, keyPair.getPublicKey());
        }

        /**
         * 密钥协商发起第一步
         *
         * @return
         */
        public TransportEntity keyExchange_1() {
            rA = random(n);
            // rA=new BigInteger("83A2C9C8 B96E5AF7 0BD480B4 72409A9A 327257F1
            // EBB73F5B 073354B2 48668563".replace(" ", ""),16);
            RA = G.multiply(rA).normalize();
            return new TransportEntity(RA.getEncoded(false), null,Z,keyPair.getPublicKey());
        }

        /**
         * 密钥协商响应方
         *
         * @param entity 传输实体
         * @return
         */
        public TransportEntity keyExchange_2(TransportEntity entity) {
            BigInteger rB = random(n);
            // BigInteger rB=new BigInteger("33FE2194 0342161C 55619C4A 0C060293
            // D543C80A F19748CE 176D8347 7DE71C80".replace(" ", ""),16);
            ECPoint RB = G.multiply(rB).normalize();

            this.rA=rB;
            this.RA=RB;

            BigInteger x2 = RB.getXCoord().toBigInteger();
            x2 = _2w.add(x2.and(_2w.subtract(BigInteger.ONE)));

            BigInteger tB = keyPair.getPrivateKey().add(x2.multiply(rB)).mod(n);
            ECPoint RA = curve.decodePoint(entity.R).normalize();

            BigInteger x1 = RA.getXCoord().toBigInteger();
            x1 = _2w.add(x1.and(_2w.subtract(BigInteger.ONE)));

            ECPoint aPublicKey=curve.decodePoint(entity.K).normalize();
            ECPoint temp = aPublicKey.add(RA.multiply(x1).normalize()).normalize();
            ECPoint V = temp.multiply(ecc_bc_spec.getH().multiply(tB)).normalize();
            if (V.isInfinity()) {
                throw new IllegalStateException();
            }
            this.V=V;

            byte[] xV = V.getXCoord().toBigInteger().toByteArray();
            byte[] yV = V.getYCoord().toBigInteger().toByteArray();
            byte[] KB = KDF(join(xV, yV, entity.Z, this.Z), 16);
            key = KB;
            System.out.print("协商得B密钥:");
            printHexString(KB);
            byte[] sB = sm3hash(new byte[] { 0x02 }, yV,
                    sm3hash(xV, entity.Z, this.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), RB.getXCoord().toBigInteger().toByteArray(),
                            RB.getYCoord().toBigInteger().toByteArray()));
            return new TransportEntity(RB.getEncoded(false), sB,this.Z,keyPair.getPublicKey());
        }

        /**
         * 密钥协商发起方第二步
         *
         * @param entity 传输实体
         */
        public TransportEntity keyExchange_3(TransportEntity entity) {
            BigInteger x1 = RA.getXCoord().toBigInteger();
            x1 = _2w.add(x1.and(_2w.subtract(BigInteger.ONE)));

            BigInteger tA = keyPair.getPrivateKey().add(x1.multiply(rA)).mod(n);
            ECPoint RB = curve.decodePoint(entity.R).normalize();

            BigInteger x2 = RB.getXCoord().toBigInteger();
            x2 = _2w.add(x2.and(_2w.subtract(BigInteger.ONE)));

            ECPoint bPublicKey=curve.decodePoint(entity.K).normalize();
            ECPoint temp = bPublicKey.add(RB.multiply(x2).normalize()).normalize();
            ECPoint U = temp.multiply(ecc_bc_spec.getH().multiply(tA)).normalize();
            if (U.isInfinity()) {
                throw new IllegalStateException();
            }
            this.V=U;

            byte[] xU = U.getXCoord().toBigInteger().toByteArray();
            byte[] yU = U.getYCoord().toBigInteger().toByteArray();
            byte[] KA = KDF(join(xU, yU,
                    this.Z, entity.Z), 16);
            key = KA;
            System.out.print("协商得A密钥:");
            printHexString(KA);
            byte[] s1= sm3hash(new byte[] { 0x02 }, yU,
                    sm3hash(xU, this.Z, entity.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), RB.getXCoord().toBigInteger().toByteArray(),
                            RB.getYCoord().toBigInteger().toByteArray()));
            if(Arrays.equals(entity.S, s1)) {
                System.out.println("B->A 密钥确认成功");
            } else {
                System.out.println("B->A 密钥确认失败");
            }
            byte[] sA= sm3hash(new byte[] { 0x03 }, yU,
                    sm3hash(xU, this.Z, entity.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), RB.getXCoord().toBigInteger().toByteArray(),
                            RB.getYCoord().toBigInteger().toByteArray()));

            return new TransportEntity(RA.getEncoded(false), sA,this.Z,keyPair.getPublicKey());
        }

        /**
         * 密钥确认最后一步
         *
         * @param entity 传输实体
         */
        public void keyExchange_4(TransportEntity entity) {
            byte[] xV = V.getXCoord().toBigInteger().toByteArray();
            byte[] yV = V.getYCoord().toBigInteger().toByteArray();
            ECPoint RA = curve.decodePoint(entity.R).normalize();
            byte[] s2= sm3hash(new byte[] { 0x03 }, yV,
                    sm3hash(xV, entity.Z, this.Z, RA.getXCoord().toBigInteger().toByteArray(),
                            RA.getYCoord().toBigInteger().toByteArray(), this.RA.getXCoord().toBigInteger().toByteArray(),
                            this.RA.getYCoord().toBigInteger().toByteArray()));
            if(Arrays.equals(entity.S, s2)) {
                System.out.println("A->B 密钥确认成功");
            } else {
                System.out.println("A->B 密钥确认失败");
            }
        }
    }




    public static void main(String[] args) throws IOException {

        SM2 sm02 = new SM2();
//		 BigInteger px = new BigInteger(
//		 "0AE4C779 8AA0F119 471BEE11 825BE462 02BB79E2 A5844495 E97C04FF 4DF2548A".replace(" ", ""), 16);
//		 BigInteger py = new BigInteger("7C0240F8 8F1CD4E1 6352A73C 17B7F16F 07353E53 A176D684 A9FE0C6B B798E857".replace(" ", ""), 16);
//		 ECPoint publicKey = curve.createPoint(px, py);
//		 BigInteger privateKey = new BigInteger("128B2FA8 BD433C6C 068C8D80 3DFF7979 2A519A55 171B1B65 0C23661D 15897263".replace(" ", ""), 16);

		 SM2KeyPair keyPair = sm02.generateKeyPair();
		 ECPoint publicKey=keyPair.getPublicKey();
		 BigInteger privateKey=keyPair.getPrivateKey();
		 sm02.exportPublicKey(publicKey, "E:/publickey.pem");
		 sm02.exportPrivateKey(privateKey, "E:/privatekey.pem");

        System.out.println("-----------------公钥加密与解密-----------------");
//        ECPoint publicKey = sm02.importPublicKey("E:/publickey.pem");
//        BigInteger privateKey = sm02.importPrivateKey("E:/privatekey.pem");
//        SM2KeyPair keyPair = new SM2KeyPair();
//        keyPair.setPublicKey(publicKey);
//        keyPair.setPrivateKey(privateKey);
     //   ECPoint publicKey = sm02.importPublicKey("E:/publickey.pem");
       // BigInteger privateKey = sm02.importPrivateKey("E:/privatekey.pem");
        //sm02.generateKeyPair()
//
//
//        SM2KeyPair keys= sm02.generateKeyPair();
//        ECPoint publicKey =keys.getPublicKey();
//        BigInteger privateKey=keys.getPrivateKey();


//        //SM2KeyPair keys;
//        //给定密钥
//        String prik = "444E6EA3EE0C7E0AAA5EE5C6BBC7A2D8DE3FB3FA990AD470232D07FB445F92D7";
//        String prikS = new String(Base64.encode(Util.hexToByte(prik)));
//        BigInteger publicKey=(BigInteger)Base64.decode(prikS.getBytes());
//        //keys.getPrivateKey();
        printHexString(publicKey.getEncoded());
        System.out.println("公钥："+publicKey.getEncoded());
      //  System.out.println("私钥："+Util.byteToHex(privateKey.toByteArray()));
        byte[] data = sm02.encrypt("测试加密aaaaaaaaaaa123aabb", publicKey);
        System.out.print("密文:");
        SM2.printHexString(data);
        System.out.println("解密后明文:" + sm02.decrypt(data, privateKey));

        System.out.println("-----------------签名与验签-----------------");
        String IDA = "Heartbeats";
        String msg = "要签名的信息";
        byte[] M=msg.getBytes();
        //Signature signature = sm02.sign(M, IDA, keyPair);
        byte[] signvalue=sm02.sign(M, IDA, keyPair);
        System.out.println("用户标识:" + IDA);
        System.out.println("签名信息:" + M);
      //  System.out.println("数字签名:" + signature);

        //byte[] signvalue=derEncode(signature.r,signature.s);
        BigInteger[] rs = decode(signvalue);
       // System.out.println(Util.getHexString(signvalue));
        System.out.println("签名值1："+rs[0]);
        System.out.println("签名值2："+rs[1]);
        System.out.println("验证签名:" + sm02.verify(M, signvalue, IDA, publicKey));

//		System.out.println("-----------------密钥协商-----------------");
//		String aID = "AAAAAAAAAAAAA";
//		SM2KeyPair aKeyPair = sm02.generateKeyPair();
//		KeyExchange aKeyExchange = new KeyExchange(aID,aKeyPair);
//
//		String bID = "BBBBBBBBBBBBB";
//		SM2KeyPair bKeyPair = sm02.generateKeyPair();
//		KeyExchange bKeyExchange = new KeyExchange(bID,bKeyPair);
//		TransportEntity entity1 = aKeyExchange.keyExchange_1();
//		TransportEntity entity2 = bKeyExchange.keyExchange_2(entity1);
//		TransportEntity entity3 = aKeyExchange.keyExchange_3(entity2);
//		bKeyExchange.keyExchange_4(entity3);
    }
    public static BigInteger[] decode(byte[] sig)
    {
        ASN1Sequence s = ASN1Sequence.getInstance(sig);

        return new BigInteger[] { ASN1Integer.getInstance(s.getObjectAt(0)).getValue(),
                ASN1Integer.getInstance(s.getObjectAt(1)).getValue() };
    }
    private static byte[] derEncode(BigInteger r, BigInteger s)
            throws IOException
    {

        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        return new DERSequence(v).getEncoded(ASN1Encoding.DER);
    }

    public static class Signature {
        BigInteger r;
        BigInteger s;

        public Signature() {
        }

        public void setR(BigInteger r) {
            this.r = r;
        }

        public void setS(BigInteger s) {
            this.s = s;
        }

        public Signature(BigInteger r, BigInteger s) {
            this.r = r;
            this.s = s;
        }

        @Override
        public String toString() {
            return r.toString(16) + "," + s.toString(16);
        }
    }
}
