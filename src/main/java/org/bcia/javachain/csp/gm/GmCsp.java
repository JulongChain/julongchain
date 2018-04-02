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

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.sm2.*;
import org.bcia.javachain.csp.gm.sm3.SM3;
import org.bcia.javachain.csp.gm.sm4.SM4;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.csp.intfs.IHash;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.*;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

import static org.bcia.javachain.csp.gm.sm2.SM2.byte2ECpoint;

/**
 * @author zhanglin
 * @purpose Define the class, GmCsp
 * @date 2018-01-25
 * @company Dingxuan
 */

// GmCsp provides the Guomi's software implements of the ICsp interface.
public class GmCsp implements ICsp {
    // List algorithms to be used.
    //private SM2 sm2;
     SM2 sm2=new SM2();
    private SM3 sm3;
    private SM4 sm4;

    public GmCsp() {
    }

    //
    private IGmFactoryOpts gmOpts;


    GmCsp(IGmFactoryOpts gmOpts) {
        this.gmOpts=gmOpts;
        this.sm3=new SM3();
    }


    @Override
    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {
        if(opts instanceof Sm2KeyGenOpts){
            return new SM2Key();
        }
        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public IKey getKey(byte[] ski) throws JavaChainException {
        return null;
    }

    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {
        //System.out.println("To hash:"+msg);
        SM3 mySM3=getSm3();
        byte []results=mySM3.hash(msg,opts);
        return results;
    }

    @Override
    public IHash getHash(IHashOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {
        SM2KeyExport sm2KeyExport=(SM2KeyExport) key;
        return sm2.sign(digest,"123",new SM2KeyPair(byte2ECpoint(sm2KeyExport.getPublicKey().toBytes()),new BigInteger(sm2KeyExport.toBytes())));
    }

    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {
        SM2KeyExport sm2KeyExport=(SM2KeyExport) key;

       ECPoint ecPoint= byte2ECpoint(sm2KeyExport.getPublicKey().toBytes());
        return sm2.verify(digest,signature,"123",ecPoint);
    }

    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {
        return new byte[0];
    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        return new byte[0];
    }

//    @Override
//    public IKey getKey(String nodeId, IKeyGenOpts opts) throws JavaChainException {
//        if(opts instanceof Sm2KeyGenOpts){
//            return new SM2KeyExport(nodeId);
//        }
//        return null;
//    }
//
//    @Override
//    public void keyFileGen(IKey k, String privatePath,String publicKey,IKeyGenOpts opts) {
//        if(opts instanceof Sm2KeyGenOpts){
//            SM2Key sm2Key= (SM2Key) k;
//            SM2KeyFileGen sm2KeyFileGen=new SM2KeyFileGen(sm2Key);
//            sm2KeyFileGen.privateKeyFileGen(privatePath);
//            sm2KeyFileGen.publicKeyFileGen(publicKey);
//        }
//    }


    private SM3 getSm3(){
        if(sm3==null) {
            sm3=new SM3();
        }
        return sm3;
    }

public static  void main(String[] args) throws JavaChainException {
    GmCsp gmCsp=new GmCsp();
    Sm2KeyGenOpts sm2KeyGenOpts=new Sm2KeyGenOpts();
    IKey sm2Key= gmCsp.keyGen(sm2KeyGenOpts);
    SM2Key sm2Key1= (SM2Key) sm2Key;
    sm2Key.toBytes();
    sm2Key1.getPublicKey().toBytes();

//    gmCsp.keyFileGen(sm2Key1,"E:",sm2KeyGenOpts);
//    Sm2KeyGenOpts sm2KeyGenOpts=new Sm2KeyGenOpts();
//    IKey sm2Key= gmCsp.getKey("",sm2KeyGenOpts);
//    SM2KeyExport sm2Key1= (SM2KeyExport) sm2Key;
//    sm2Key1.getPublicKey().toBytes();
//    String abc="123";
//
//   byte[] signValue= gmCsp.sign(sm2Key1,abc.getBytes(),new SM2SignerOpts());
//   boolean verify=gmCsp.verify(sm2Key1,signValue,abc.getBytes(),new SM2SignerOpts());
//   System.out.println("验签结果："+verify);
}
}
