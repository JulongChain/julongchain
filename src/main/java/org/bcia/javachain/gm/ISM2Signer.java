package org.bcia.javachain.gm;

import org.bcia.javachain.csp.gm.SM2KeyPair;
import org.bcia.javachain.protos.common.Common;

import org.bouncycastle.math.ec.ECPoint;

public interface ISM2Signer {
    /**
     * 对消息头进行签名
     * @return
     */
    Common.SignatureHeader newSignatureHeader();

    /**
     *对数据进行签名
     * @param data
     * @param sm2KeyPair
     * @return
     */
    byte[] sign(byte[] data, SM2KeyPair sm2KeyPair);

    /**
     *验证签名
     * @param publickey
     * @param signguer
     * @return
     */
    boolean verfiy(ECPoint publickey, byte[] signguer);

}
