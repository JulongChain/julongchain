/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.javachain.csp.gm.sdt;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.sdt.SM2.*;
import org.bcia.javachain.csp.gm.sdt.SM3.SM3;
import org.bcia.javachain.csp.gm.sdt.SM4.*;
import org.bcia.javachain.csp.gm.sdt.config.Config;
import org.bcia.javachain.csp.gm.sdt.random.GmRandom;
import org.bcia.javachain.csp.gm.sdt.util.KeysStore;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.csp.intfs.IHash;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * SDT GM algorithm CSP
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class SdtGmCsp implements ICsp {

    private static JavaChainLog logger = JavaChainLogFactory.getLog(SdtGmCsp.class);

    private final static String KEY_STORE_PATH = "/opt/msp/keystore/sdt/";

    //config
    private Config config;
    // List algorithms to be used.
    private SM2 sm2;
    private SM3 sm3;
    private SM4 sm4;
    private GmRandom gmRandom;

    public SdtGmCsp() {
        this.config = new Config();
        this.sm3 = new SM3();
        this.sm2 = new SM2();
        this.sm4 = new SM4();
        this.gmRandom = new GmRandom();
        loadConfig();
    }

    //GmFactoryOpts
    private ISdtGmFactoryOpts sdtGmOpts;

    SdtGmCsp(ISdtGmFactoryOpts sdtGmOpts) {
        this.sdtGmOpts = sdtGmOpts;
        this.sm3 = new SM3();
        this.sm2 = new SM2();
        this.sm4 = new SM4();
        this.gmRandom = new GmRandom();
        this.config = new Config();
    }

    private void loadConfig() {
        try {
            Yaml yaml = new Yaml();
            FileInputStream inputStream = new FileInputStream(new File("src/main/resources/sdtgmcsp.yaml"));
            config = yaml.loadAs(inputStream, Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {
        if (null == opts) {
            logger.error("Invalid Opts parameter. It must not be null.");
            throw new JavaChainException("Invalid Opts parameter. It must not be null.");
        }
        if (opts instanceof SM2KeyGenOpts) {

            IKey sm2Key = new SM2Key(sm2.generateKeyPair());

            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getPrivateKeyPath() && !"".equals(sdtGmOpts.getPrivateKeyPath())) {
                    path = sdtGmOpts.getPrivateKeyPath();
                }
                KeysStore.storeKey(path, null, sm2Key, KeysStore.KEY_TYPE_SK);
            }
            return sm2Key;
        }
        if (opts instanceof SM4KeyGenOpts) {
            IKey sm4Key = new SM4Key();
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getKeyPath() && !"".equals(sdtGmOpts.getKeyPath())) {
                    path = sdtGmOpts.getKeyPath();
                }
                KeysStore.storeKey(path, null, sm4Key, KeysStore.KEY_TYPE_KEY);
            }
            return sm4Key;
        }
        return null;
    }

    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {
        if (null == opts) {
            logger.error("Invalid Opts parameter. It must not be null.");
            throw new JavaChainException("Invalid Opts parameter. It must not be null.");
        }
        if (opts instanceof SM2KeyDerivOpts) {
           //TODO:
        }
        if (opts instanceof SM4KeyDerivOpts) {
            //TODO:
        }
        return null;
    }

    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {
        if (null == raw) {
            logger.error("Invalid raw. It must not be nil.");
            throw new JavaChainException("Invalid raw. It must not be nil.");
        }
        if (null == opts) {
            logger.error("Invalid opts. It must not be nil.");
            throw new JavaChainException("Invalid opts. It must not be nil.");
        }
        if (opts instanceof SM2PrivateKeyImportOpts) {
            IKey sm2PrivateKey = new SM2PrivateKey((byte[])raw);
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getPrivateKeyPath() && !"".equals(sdtGmOpts.getPrivateKeyPath())) {
                    path = sdtGmOpts.getPrivateKeyPath();
                }
                KeysStore.storeKey(path, null, sm2PrivateKey, KeysStore.KEY_TYPE_SK);
            }
            return sm2PrivateKey;
        }

        if (opts instanceof SM2PublicKeyImportOpts) {
            IKey sm2PublicKey = new SM2PublicKey((byte[])raw);
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getPublicKeyPath() && !"".equals(sdtGmOpts.getPublicKeyPath())) {
                    path = sdtGmOpts.getPublicKeyPath();
                }
                KeysStore.storeKey(path, null, sm2PublicKey, KeysStore.KEY_TYPE_PK);
            }
            return sm2PublicKey;
        }

        if (opts instanceof SM4KeyImportOpts) {
            IKey sm4Key = new SM4Key((byte[])raw);
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getKeyPath() && !"".equals(sdtGmOpts.getKeyPath())) {
                    path = sdtGmOpts.getKeyPath();
                }
                KeysStore.storeKey(path, null, sm4Key, KeysStore.KEY_TYPE_KEY);
            }
            return sm4Key;
        }
        return null;
    }

    @Override
    public IKey getKey(byte[] ski) throws JavaChainException {
        if(null == ski || 0 == ski.length) {
            logger.error("Invalid ski. It must not be nil.");
            throw new JavaChainException("Invalid ski. It must not be nil.");
        }
        byte[] keyData = null;
        String path = KEY_STORE_PATH;
        //find sm2 sk
        if(null != sdtGmOpts.getPrivateKeyPath() && !"".equals(sdtGmOpts.getPrivateKeyPath())) {
            path = sdtGmOpts.getPrivateKeyPath();
        }
        keyData = KeysStore.loadKey(path, null, ski, KeysStore.KEY_TYPE_SK);
        if(null != keyData) {
            IKey sm2PrivateKey = new SM2PrivateKey(keyData);
            return sm2PrivateKey;
        }
        //find sm2 pk
        if(null != sdtGmOpts.getPublicKeyPath() && !"".equals(sdtGmOpts.getPublicKeyPath())) {
            path = sdtGmOpts.getPublicKeyPath();
        }
        keyData = KeysStore.loadKey(path, null, ski, KeysStore.KEY_TYPE_PK);
        if(null != keyData) {
            IKey sm2PublicKey = new SM2PublicKey(keyData);
            return sm2PublicKey;
        }
        //find sm4 key
        if(null != sdtGmOpts.getKeyPath() && !"".equals(sdtGmOpts.getKeyPath())) {
            path = sdtGmOpts.getKeyPath();
        }
        keyData = KeysStore.loadKey(path, null, ski, KeysStore.KEY_TYPE_KEY);
        if(null != keyData) {
            IKey sm4Key = new SM4Key(keyData);
            return sm4Key;
        }
        return null;
    }

    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {
        if (msg.length == 0) {
            logger.error("Invalid msg. Cannot be empty.");
            throw new JavaChainException("Invalid msg. Cannot be empty.");
        }

        byte[] results = sm3.hash(msg);
        return results;
    }

    @Override
    public IHash getHash(IHashOpts opts) throws JavaChainException {
        return null;
    }

    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {
        if (key == null) {
            logger.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (digest.length == 0) {
            logger.error("Invalid digest. Cannot be empty.");
            throw new JavaChainException("Invalid digest. Cannot be empty.");
        }
        if (opts instanceof SM2SignerOpts) {
            return sm2.sign(digest, key.toBytes());
        }
        return null;
    }

    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {
        boolean verify = false;
        if (key == null) {
            logger.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (signature.length == 0) {
            logger.error("Invalid signature. It must not be nil.");
            throw new JavaChainException("Invalid signature. It must not be nil.");
        }
        if (opts instanceof SM2SignerOpts) {
            if(0 == sm2.verify(digest, key.getPublicKey().toBytes(), signature)) {
                verify = true;
            }
        }
        return verify;
    }

    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {
        if (key == null) {
            logger.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (opts instanceof SM4EncrypterOpts) {
            return sm4.encryptECB(plaintext, key.toBytes());
        }
        return null;
    }

    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {
        if (key == null) {
            logger.error("Invalid Key. It must not be nil.");
            throw new JavaChainException("Invalid Key. It must not be nil.");
        }
        if (ciphertext.length == 0) {
            logger.error("Invalid ciphertext. Cannot be empty.");
            throw new JavaChainException("Invalid ciphertext. Cannot be empty.");
        }
        if (opts instanceof SM4DecrypterOpts) {
            return sm4.decryptECB(ciphertext, key.toBytes());
        }
        return null;
    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        if (len <= 0) {
            logger.error("The random length is less than Zero! ");
            throw new JavaChainException("The random length is less than Zero! ");
        }
        byte[] random = gmRandom.rng(len);
        return random;
    }
}