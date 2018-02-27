package org.bcia.javachain.bccsp.gm;

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

import org.bcia.javachain.bccsp.intfs.IBccsp;
import org.bcia.javachain.bccsp.intfs.IHash;
import org.bcia.javachain.bccsp.intfs.IKey;
import org.bcia.javachain.bccsp.intfs.opts.*;
import org.bcia.javachain.common.exception.JavaChainException;

/**
 * @author zhanglin
 * @purpose Define the class, GmBccsp
 * @date 2018-01-25
 * @company Dingxuan
 */

// GmBccsp provides the Guomi's software implements of the IBccsp interface.
public class GmBccsp implements IBccsp {

    // List algorithms to be used.
    private SM2 sm2;
    private SM3 sm3;
    private SM4 sm4;

    //
    private IGmFactoryOpts gmOpts;


    GmBccsp(IGmFactoryOpts gmOpts) {
        this.gmOpts=gmOpts;
        sm3=new SM3();
    }


    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {
        return null;
    }

    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {
        return null;
    }

    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {
        return null;
    }

    public IKey getKey(byte[] ski) throws JavaChainException {
        return null;
    }

    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {
        //System.out.println("To hash:"+msg);
        SM3 mySM3=getSm3();
        byte []results=mySM3.hash(msg,opts);
        return results;
    }

    public IHash getHash(IHashOpts opts) throws JavaChainException {
        return null;
    }

    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {
        return new byte[0];
    }

    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {
        return false;
    }

    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {
        return new byte[0];
    }

    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {
        return new byte[0];
    }

    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        return new byte[0];
    }

    private SM3 getSm3(){
        if(sm3==null)
            sm3=new SM3();
        return sm3;
    }
}
