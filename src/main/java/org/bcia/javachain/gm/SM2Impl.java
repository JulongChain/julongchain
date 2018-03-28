package org.bcia.javachain.gm;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.csp.gm.sm2.SM2;
import org.bcia.javachain.csp.gm.sm2.SM2KeyPair;
import org.bcia.javachain.protos.common.Common;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.net.URL;


public class SM2Impl implements  ISM2Signer{
    SM2 sm2=new SM2();
    @Override
    public Common.SignatureHeader newSignatureHeader() {
       //  Common.H
        String nodeid="123";
        URL url = EnvelopeHelper.class.getClassLoader().getResource("publickey.pem");
        ECPoint pbkey= loadPublicKey(url.getFile());
        byte[] publickey=pbkey.getEncoded();
        Common.SignatureHeader.Builder signatureHeader= Common.SignatureHeader.newBuilder();
        signatureHeader.setCreator(ByteString.copyFrom(publickey));
        signatureHeader.setNodeid(nodeid);
        Common.SignatureHeader.newBuilder().setCreator(ByteString.copyFrom(publickey)).build();
        return  signatureHeader.build();
    }

    @Override
    public byte[] sign(String nodeId,byte[] data, SM2KeyPair sm2KeyPair) {
        byte[] signvalue=sm2.sign(data,nodeId,sm2KeyPair);
        return signvalue;
    }

    @Override
    public boolean verfiy(String nodeId,byte[] data,ECPoint publickey, byte[] signguer) {

        boolean verify=sm2.verify(data,signguer,nodeId,publickey);
        return verify;
    }

    @Override
    public SM2KeyPair generateKeyPair() {
        return  sm2.generateKeyPair();
    }

    @Override
    public void exportPublicKey(ECPoint publicKey, String path) {
        sm2.exportPublicKey(publicKey,path);
    }

    @Override
    public void exportPrivateKey(BigInteger privateKey, String path) {
        sm2.exportPrivateKey(privateKey,path);
    }

    @Override
    public ECPoint loadPublicKey(String path) {
        return sm2.importPublicKey(path);
    }

    @Override
    public BigInteger loadPrivateKey(String path) {
        return sm2.importPrivateKey(path);
    }
}
