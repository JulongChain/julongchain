package org.bcia.javachain.gm;

import org.bcia.javachain.csp.gm.SM2;
import org.bcia.javachain.csp.gm.SM2KeyPair;
import org.bcia.javachain.protos.common.Common;

import org.bouncycastle.math.ec.ECPoint;


public class SM2Impl implements  ISM2Signer{
    SM2 sm2=new SM2();
    @Override
    public Common.SignatureHeader newSignatureHeader() {
        return null;
    }

    @Override
    public byte[] sign(byte[] data, SM2KeyPair sm2KeyPair) {
        String userID=null;
        byte[] signvalue=sm2.sign(data,userID,sm2KeyPair);
        return signvalue;
    }

    @Override
    public boolean verfiy(ECPoint publickey, byte[] signguer) {
        String  msg=null;
        String userID=null;
        boolean verify=sm2.verify(msg.getBytes(),signguer,userID,publickey);
        return verify;
    }
}
