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
package org.bcia.julongchain.csp.gm.sdt;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.sdt.sm2.*;
import org.bcia.julongchain.csp.gm.sdt.sm3.SM3;
import org.bcia.julongchain.csp.gm.sdt.sm4.*;
import org.bcia.julongchain.csp.gm.sdt.common.Constants;
import org.bcia.julongchain.csp.gm.sdt.random.GmRandom;
import org.bcia.julongchain.csp.gm.sdt.util.KeysStore;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.*;


/**
 * SDT 国密算法 密码服务提供者
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class SdtGmCsp implements ICsp {

    private static JavaChainLog logger = JavaChainLogFactory.getLog(SdtGmCsp.class);

    //默认密钥存储路径
    private final static String KEY_STORE_PATH = "msp/keystore/sdt/";

    //算法
    private SM2 sm2;
    private SM3 sm3;
    private SM4 sm4;
    private GmRandom gmRandom;

    public SdtGmCsp() {
        this.sm3 = new SM3();
        this.sm2 = new SM2();
        this.sm4 = new SM4();
        this.gmRandom = new GmRandom();
        this.keysStore = new KeysStore();
    }

    //工厂选项
    private ISdtGmFactoryOpts sdtGmOpts;
    //密钥存储
    private KeysStore keysStore;

    SdtGmCsp(ISdtGmFactoryOpts sdtGmOpts) {
        this.sdtGmOpts = sdtGmOpts;
        this.sm3 = new SM3();
        this.sm2 = new SM2();
        this.sm4 = new SM4();
        this.gmRandom = new GmRandom();
        this.keysStore = new KeysStore();
    }

    /**
     * 生成密钥
     * @param opts 密钥生成选项
     * @return 密钥对象
     * @throws JavaChainException
     */
    @Override
    public IKey keyGen(IKeyGenOpts opts) throws JavaChainException {
        if (null == opts) {
            logger.error("Invalid Opts. It must not be null.");
            throw new JavaChainException("Invalid Opts. It must not be null.");
        }
        //如果opts为SM2KeyGenOpts的实例，则生成SM2公私钥对
        if (opts instanceof SM2KeyGenOpts) {
            IKey sm2Key = new SM2Key(sm2.generateKeyPair());
            //如果是非临时密钥，则存储密钥数据
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                //存储SM2私钥
                if(null != sdtGmOpts.getPrivateKeyPath() && !"".equals(sdtGmOpts.getPrivateKeyPath())) {
                    path = sdtGmOpts.getPrivateKeyPath();
                }
                keysStore.storeKey(path, null, sm2Key, KeysStore.KEY_TYPE_SK);
                //存储SM2公钥
                path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getPublicKeyPath() && !"".equals(sdtGmOpts.getPublicKeyPath())) {
                    path = sdtGmOpts.getPublicKeyPath();
                }
                keysStore.storeKey(path, null, sm2Key.getPublicKey(), KeysStore.KEY_TYPE_PK);
            }
            return sm2Key;
        }
        //如果opts为SM4KeyGenOpts的实例，则生成SM4密钥
        if (opts instanceof SM4KeyGenOpts) {
            byte[] sm4KeyData = gmRandom.rng(Constants.SM4_KEY_LEN);
            IKey sm4Key = new SM4Key(sm4KeyData);
            //如果是非临时密钥，则存储密钥数据
            if (!opts.isEphemeral()) {
                //存储SM4密钥
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getKeyPath() && !"".equals(sdtGmOpts.getKeyPath())) {
                    path = sdtGmOpts.getKeyPath();
                }
                keysStore.storeKey(path, null, sm4Key, KeysStore.KEY_TYPE_KEY);
            }
            return sm4Key;
        }
        logger.error("Unsupported ‘IKeyGenOpts‘.");
        throw new JavaChainException("Unsupported ‘IKeyGenOpts‘.");
    }

    /**
     * 密钥派生
     * @param key 原密钥
     * @param opts 密钥派生选项
     * @return 派生密钥
     * @throws JavaChainException
     */
    @Override
    public IKey keyDeriv(IKey key, IKeyDerivOpts opts) throws JavaChainException {
        if (null == opts) {
            logger.error("Invalid Opts. It must not be null.");
            throw new JavaChainException("Invalid Opts. It must not be null.");
        }
        if (opts instanceof SM2KeyDerivOpts) {
           //TODO: 待实现
            return null;
        }
        if (opts instanceof SM4KeyDerivOpts) {
            //TODO: 待实现
            return null;
        }
        logger.error("Unsupported ‘IKeyDerivOpts‘.");
        throw new JavaChainException("Unsupported ‘IKeyDerivOpts‘.");
    }

    /**
     * 密钥导入
     * @param raw 密钥原始数据
     * @param opts 密钥导入选项
     * @return 密钥对象
     * @throws JavaChainException
     */
    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws JavaChainException {
        if (null == raw) {
            logger.error("Invalid raw. It must not be null.");
            throw new JavaChainException("Invalid raw. It must not be null.");
        }
        if (null == opts) {
            logger.error("Invalid opts. It must not be null.");
            throw new JavaChainException("Invalid opts. It must not be null.");
        }
        //如果opts为SM2PrivateKeyImportOpts的实例，则导入为SM2私钥
        if (opts instanceof SM2PrivateKeyImportOpts) {
            IKey sm2PrivateKey = new SM2PrivateKey((byte[])raw);
            //如果是非临时密钥，则存储密钥数据
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getPrivateKeyPath() && !"".equals(sdtGmOpts.getPrivateKeyPath())) {
                    path = sdtGmOpts.getPrivateKeyPath();
                }
                keysStore.storeKey(path, null, sm2PrivateKey, KeysStore.KEY_TYPE_SK);
            }
            return sm2PrivateKey;
        }
        //如果opts为SM2PublicKeyImportOpts的实例，则导入为SM2公钥
        if (opts instanceof SM2PublicKeyImportOpts) {
            IKey sm2PublicKey = new SM2PublicKey((byte[])raw);
            //如果是非临时密钥，则存储密钥数据
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getPublicKeyPath() && !"".equals(sdtGmOpts.getPublicKeyPath())) {
                    path = sdtGmOpts.getPublicKeyPath();
                }
                keysStore.storeKey(path, null, sm2PublicKey, KeysStore.KEY_TYPE_PK);
            }
            return sm2PublicKey;
        }
        //如果opts为SM4KeyImportOpts的实例，则导入为SM4密钥
        if (opts instanceof SM4KeyImportOpts) {
            IKey sm4Key = new SM4Key((byte[])raw);
            //如果是非临时密钥，则存储密钥数据
            if (!opts.isEphemeral()) {
                String path = KEY_STORE_PATH;
                if(null != sdtGmOpts.getKeyPath() && !"".equals(sdtGmOpts.getKeyPath())) {
                    path = sdtGmOpts.getKeyPath();
                }
                keysStore.storeKey(path, null, sm4Key, KeysStore.KEY_TYPE_KEY);
            }
            return sm4Key;
        }
        logger.error("Unsupported ‘IKeyImportOpts‘.");
        throw new JavaChainException("Unsupported ‘IKeyImportOpts‘.");
    }

    /**
     * 根据密钥标识获取密钥
     * @param ski 密钥标识
     * @return 密钥对象
     * @throws JavaChainException
     */
    @Override
    public IKey getKey(byte[] ski) throws JavaChainException {
        if(null == ski) {
            logger.error("Invalid ski. It must not be null.");
            throw new JavaChainException("Invalid ski. It must not be null.");
        }
        byte[] keyData = null;
        String path = KEY_STORE_PATH;
        //查找SM2私钥
        if(null != sdtGmOpts.getPrivateKeyPath() && !"".equals(sdtGmOpts.getPrivateKeyPath())) {
            path = sdtGmOpts.getPrivateKeyPath();
        }
        keyData = keysStore.loadKey(path, null, ski, KeysStore.KEY_TYPE_SK);
        if(null != keyData) {
            IKey sm2PrivateKey = new SM2PrivateKey(keyData);
            return sm2PrivateKey;
        }
        //查找SM2公钥
        if(null != sdtGmOpts.getPublicKeyPath() && !"".equals(sdtGmOpts.getPublicKeyPath())) {
            path = sdtGmOpts.getPublicKeyPath();
        }
        keyData = keysStore.loadKey(path, null, ski, KeysStore.KEY_TYPE_PK);
        if(null != keyData) {
            IKey sm2PublicKey = new SM2PublicKey(keyData);
            return sm2PublicKey;
        }
        //查找SM4密钥
        if(null != sdtGmOpts.getKeyPath() && !"".equals(sdtGmOpts.getKeyPath())) {
            path = sdtGmOpts.getKeyPath();
        }
        keyData = keysStore.loadKey(path, null, ski, KeysStore.KEY_TYPE_KEY);
        if(null != keyData) {
            IKey sm4Key = new SM4Key(keyData);
            return sm4Key;
        }
        throw new JavaChainException("Cannot find the key for SKI [" + ski + "].");
    }

    /**
     * 计算消息摘要
     * @param msg 消息数据
     * @param opts 哈希选项
     * @return 摘要值
     * @throws JavaChainException
     */
    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws JavaChainException {
        if (null == msg) {
            logger.error("Invalid msg. It must not be null.");
            throw new JavaChainException("Invalid msg. It must not be null.");
        }

        byte[] results = sm3.hash(msg);
        return results;
    }

    /**
     * 获取哈希实例
     * @param opts 哈希选项
     * @return 哈希实例
     * @throws JavaChainException
     */
    @Override
    public IHash getHash(IHashOpts opts) throws JavaChainException {
        return null;
    }

    /**
     * 签名
     * @param key 密钥
     * @param digest 消息摘要
     * @param opts 签名者选项
     * @return 签名值
     * @throws JavaChainException
     */
    @Override
    public byte[] sign(IKey key, byte[] digest, ISignerOpts opts) throws JavaChainException {
        if (null == key) {
            logger.error("Invalid Key. It must not be null.");
            throw new JavaChainException("Invalid Key. It must not be null.");
        }
        if (null == digest) {
            logger.error("Invalid digest. It must not be null.");
            throw new JavaChainException("Invalid digest. It must not be null.");
        }
        if (null == opts) {
            logger.error("Invalid opts. It must not be null.");
            throw new JavaChainException("Invalid opts. It must not be null.");
        }
        //如果opts为SM2SignerOpts的实例，则调用SM2算法进行签名
        if (opts instanceof SM2SignerOpts) {
            return sm2.sign(digest, key.toBytes());
        }
        logger.error("Unsupported ‘ISignerOpts‘.");
        throw new JavaChainException("Unsupported ‘ISignerOpts‘.");
    }

    /**
     * 验签
     * @param key 密钥
     * @param signature 签名值
     * @param digest 消息摘要
     * @param opts 签名者选项
     * @return 验签结果
     * @throws JavaChainException
     */
    @Override
    public boolean verify(IKey key, byte[] signature, byte[] digest, ISignerOpts opts) throws JavaChainException {
        boolean verify = false;
        if (null == key) {
            logger.error("Invalid Key. It must not be null.");
            throw new JavaChainException("Invalid Key. It must not be null.");
        }
        if (null == signature) {
            logger.error("Invalid signature. It must not be null.");
            throw new JavaChainException("Invalid signature. It must not be null.");
        }
        if (null == opts) {
            logger.error("Invalid opts. It must not be null.");
            throw new JavaChainException("Invalid opts. It must not be null.");
        }
        //如果opts为SM2SignerOpts的实例，则调用SM2算法进行验签
        if (opts instanceof SM2SignerOpts) {
            if(0 == sm2.verify(digest, key.getPublicKey().toBytes(), signature)) {
                verify = true;
            }
            return verify;
        }
        logger.error("Unsupported ‘ISignerOpts‘.");
        throw new JavaChainException("Unsupported ‘ISignerOpts‘.");
    }

    /**
     * 对称加密
     * @param key 密钥
     * @param plaintext 明文数据
     * @param opts 加密选项
     * @return 密文数据
     * @throws JavaChainException
     */
    @Override
    public byte[] encrypt(IKey key, byte[] plaintext, IEncrypterOpts opts) throws JavaChainException {
        if (null == key) {
            logger.error("Invalid Key. It must not be null.");
            throw new JavaChainException("Invalid Key. It must not be null.");
        }
        if (null == plaintext) {
            logger.error("Invalid plaintext. It must not be null.");
            throw new JavaChainException("Invalid plaintext. It must not be null.");
        }
        if (null == opts) {
            logger.error("Invalid opts. It must not be null.");
            throw new JavaChainException("Invalid opts. It must not be null.");
        }
        //如果opts为SM4EncrypterOpts的实例，则调用SM4算法ECB模式进行加密
        if (opts instanceof SM4EncrypterOpts) {
            return sm4.encryptECB(plaintext, key.toBytes());
        }
        logger.error("Unsupported ‘IEncrypterOpts’.");
        throw new JavaChainException("Unsupported ‘IEncrypterOpts’.");
    }

    /**
     * 对称解密
     * @param key 密钥
     * @param ciphertext 密文数据
     * @param opts 解密选项
     * @return
     * @throws JavaChainException
     */
    @Override
    public byte[] decrypt(IKey key, byte[] ciphertext, IDecrypterOpts opts) throws JavaChainException {
        if (null == key) {
            logger.error("Invalid Key. It must not be null.");
            throw new JavaChainException("Invalid Key. It must not be null.");
        }
        if (null == ciphertext) {
            logger.error("Invalid ciphertext. It must not be null.");
            throw new JavaChainException("Invalid ciphertext. It must not be null.");
        }
        if (null == opts) {
            logger.error("Invalid opts. It must not be null.");
            throw new JavaChainException("Invalid opts. It must not be null.");
        }
        //如果opts为SM4EncrypterOpts的实例，则调用SM4算法ECB模式进行解密
        if (opts instanceof SM4DecrypterOpts) {
            return sm4.decryptECB(ciphertext, key.toBytes());
        }
        logger.error("Unsupported ‘IDecrypterOpts’.");
        throw new JavaChainException("Unsupported ‘IDecrypterOpts’.");
    }

    /**
     * 生成随机数
     * @param len 随机数长度
     * @param opts 随机数生成选项
     * @return
     * @throws JavaChainException
     */
    @Override
    public byte[] rng(int len, IRngOpts opts) throws JavaChainException {
        return gmRandom.rng(len);
    }
}
