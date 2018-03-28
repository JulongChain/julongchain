package org.bcia.javachain.gm;

import org.bcia.javachain.csp.gm.sm2.SM2KeyPair;
import org.bcia.javachain.protos.common.Common;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public interface ISM2Signer {
    /**
     * 对消息头进行签名
     * @return
     */
    Common.SignatureHeader newSignatureHeader();

    /**
     *对数据进行签名
     * @param data
     * @param sm2KeyPair 密钥对
     * @return
     */
    byte[] sign(String nodeId, byte[] data, SM2KeyPair sm2KeyPair);

    /**
     * 验证签名
     * @param nodeId
     * @param data
     * @param publickey
     * @param signguer
     * @return
     */
    boolean verfiy(String nodeId,byte[] data,ECPoint publickey, byte[] signguer);

    /**
     * 密钥生成
     * @return
     */
    SM2KeyPair generateKeyPair();

    /**
     * 导出公钥到本地路径
     * @param publicKey
     * @param path
     */
    void exportPublicKey(ECPoint publicKey, String path);

    /**
     * 导出私钥到本地
     * @param privateKey
     * @param path
     */
    void exportPrivateKey(BigInteger privateKey, String path);

    /**
     * 从本地加载公钥
     * @param path
     * @return
     */
    ECPoint loadPublicKey(String path);

    /**
     * 从本地加载私钥
     * @param path
     * @return
     */
    BigInteger loadPrivateKey(String path);

}
