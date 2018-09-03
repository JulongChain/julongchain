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
package org.bcia.julongchain.csp.gmt0016.excelsecu;

import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.csp.gmt0016.excelsecu.algorithm.SHA1;
import org.bcia.julongchain.csp.gmt0016.excelsecu.algorithm.SHA256;
import org.bcia.julongchain.csp.gmt0016.excelsecu.algorithm.SM3;
import org.bcia.julongchain.csp.gmt0016.excelsecu.bean.Properties;
import org.bcia.julongchain.csp.gmt0016.excelsecu.bean.*;
import org.bcia.julongchain.csp.gmt0016.excelsecu.common.*;
import org.bcia.julongchain.csp.gmt0016.excelsecu.security.ECCPublicKeyBlob;
import org.bcia.julongchain.csp.gmt0016.excelsecu.security.RSAPublicKeyBlob;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IHash;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.opts.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class CspGm0016 implements ICsp {
    // TODO temp use, remove later
    private static final String RSA = "RSA";
    private static final String ECC = "ECC";
    private static final String SM2 = "SM2";

    private static final String SM3 = "SM3";
    private static final String SHA1 = "SHA1";
    private static final String SHA256 = "SHA256";

    private static final String SM1 = "SM1";
    private static final String DES = "DES";
    private static final String TDES = "TDES";
    private static final String TRI_DES = "3DES";
    private static final String SSF33 = "SSF33";
    private static final String SM4 = "SM4";

    private static final String TYPE_1 = "@1";
    private static final String TYPE_2 = "@2";
    private static final String TYPE_3 = "@3";
    @SuppressWarnings("unused")
    private static final String TYPE_1_ID = "1";
    private static final String TYPE_2_ID = "2";
    private static final String TYPE_3_ID = "3";

    private ISkf mSkf;

    private Yaml mYaml;

    private org.bcia.julongchain.csp.gmt0016.excelsecu.bean.Properties mProperties;

    //application handle
    private long mHApplication = -1;

    //dev handle
    private long mHDev = -1;

    // type 2 container handle
    private long mContainer2Handle;

    private Map<String, Container> mContainers;

    private String mDevName;


    public CspGm0016() {
        this.mYaml = new Yaml();
        mSkf = new Skf();
        mContainers = new HashMap<>();
        init();
    }


    private void init() {

        parsePropertiesFile();
        startService();
    }

    private void startService() {
        findDevice();
    }

    private void findDevice() {
        System.load(mProperties.getDriverPath());
        try {
            //size is the specified length of device list returned
            List<String> deviceList = mSkf.skfEnumDev(true, ServiceConfig.DEVICE_LIST_LENGTH);
            long devHandle = 0L;
            long applicationHandle = 0L;
            String name;

            for (String devName : deviceList) {
                name = devName;
                devHandle = mSkf.skfConnectDev(name);
                mSkf.skfLockDev(devHandle, ServiceConfig.LOCK_DEV_TIME_OUT);
                try {
                    applicationHandle = mSkf.skfOpenApplication(devHandle, name);
                    mDevName = name;
                    mHDev = devHandle;
                    mHApplication = applicationHandle;
                    createContainer();
                    break;
                } catch (SarException e) {
                    //if not exist the specified application ,release the current dev
                    if (e.getErrorCode() == SarException.SAR_APPLICATION_NOT_EXIST) {
                        mSkf.skfUnlockDev(devHandle);
                        mSkf.skfDisconnectDev(devHandle);
                    }
                }
            }
        } catch (SarException e) {
            e.printStackTrace();
        }
    }

    private void createContainer() {

        try {
            String container2Name = UUID.randomUUID().toString() + TYPE_2;
            mContainer2Handle = mSkf.skfCreateContainer(mHApplication, container2Name);
            mContainers.put(container2Name, new Container(container2Name, mContainer2Handle));
            //TODO: 2018/4/2 生成密钥对，导入一对加密密钥对
        } catch (SarException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkDevAvailable() {
        if (mHDev != -1 && mHApplication != -1 && mDevName != null) {
            try {
                switch ((int) mSkf.skfGetDevState(mDevName)) {
                    case DeviceState.DEV_ABSENT_STATE:
                        break;
                    case DeviceState.DEV_PRESENT_STATE:
                        return true;
                    case DeviceState.DEV_UNKNOW_STATE:
                        break;
                    default:
                        break;
                }
            } catch (SarException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private void parsePropertiesFile() {

        try {
            FileInputStream fis = new FileInputStream(new File("src/main/properties.yml"));
            mProperties = mYaml.loadAs(fis, Properties.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IKey keyGen(IKeyGenOpts opts) throws CspException {

        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }
        try {
            if (mSkf.skfVerifyPIN(mHApplication, Constants.USER_TYPE, mProperties.getPinCode()) == 0) {
                // TODO: 2018/4/2 LOCK the pin code
                return null;
            }

            String container1Name = UUID.randomUUID().toString() + TYPE_1;
            long containerHandler = mSkf.skfCreateContainer(mHApplication, container1Name);
            mContainers.put(container1Name, new Container(container1Name, containerHandler));

            //according to the algorithm, generates difference types of keypair with type 1 container
            if (RSA.equals(opts.getAlgorithm())) {
                RSAPublicKeyBlob rsaKeyPair = mSkf.skfGenRSAKeyPair(containerHandler, 1024);
            } else if (SM2.equals(opts.getAlgorithm())) {
                ECCPublicKeyBlob eccKeyPair = mSkf.skfGenECCKeyPair(containerHandler, AlgorithmID.SGD_SM2_1);
            }

            long containerTYpe = mSkf.skfGetContainerType(mContainer2Handle);

            // empty container
            if (containerTYpe == 0) {

                ECCPublicKeyBlob eccKeyPair = mSkf.skfGenECCKeyPair(mContainer2Handle, AlgorithmID.SGD_SM2_1);
                mSkf.skfECCExportSessionKey(mContainer2Handle, KeyID.SGD_SESSION_KEY, eccKeyPair);

            }

            //generates temp asymmetric keypair with type 3 container
            String container3Name = UUID.randomUUID().toString() + TYPE_3;
            long container3Handle = mSkf.skfCreateContainer(mHApplication, container3Name);
            mContainers.put(container3Name, new Container(container3Name, container3Handle));
            mSkf.skfGenRSAKeyPair(container3Handle, Constants.MAX_RSA_MODULUS_LEN);

            //generates temp symmetric key  with type 2 container
            mSkf.skfGenECCKeyPair(mContainer2Handle, AlgorithmID.SGD_SM2_1);

            // TODO: 2018/3/29 优化

        } catch (SarException e) {

            //if no enough room ,delete type 3 container
            if (SarException.SAR_NO_ROOM == e.getErrorCode()) {
                for (String containerName : mContainers.keySet()) {
                    if (containerName.contains(TYPE_3)) {
                        try {
                            mSkf.skfDeleteContainer(mHApplication, containerName);
                        } catch (SarException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
//            if (SarException.SAR_PIN_INCORRECT == e.getErrorCode()) {
//
//            }

        }
        return null;

    }

    @Override
    public IKey keyDeriv(IKey k, IKeyDerivOpts opts) throws CspException {

        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }

        throw new CspException("not allow to import key");
    }

    @Override
    public IKey keyImport(Object raw, IKeyImportOpts opts) throws CspException{

        String containerName = UUID.randomUUID().toString() + TYPE_1;
        long containerHandle = mSkf.skfCreateContainer(mHApplication, containerName);
        Container container = new Container(containerName, containerHandle);
        mContainers.put(containerName, container);

        // TODO: 2018/3/30

//        if (ECC.equals(opts.getAlgorithm())) {
//            ECCPublicKeyBlob eccPublicKeyBlob = mSkf.skfGenECCKeyPair(container.getContainerHandle(), AlgorithmID.SGD_SM2_1);
//            mSkf.skfImportECCKeyPair(containerHandle,);
//
//        } else if (RSA.equals(opts.getAlgorithm())) {
//            RSAPublicKeyBlob rsaPublicKeyBlob=mSkf.skfGenRSAKeyPair(containerHandle,Constants.MAX_RSA_MODULUS_LEN);
//            mSkf.skfImportRSAKeyPair(container.getContainerHandle(),);
//        }

        return null;
    }

    @Override
    public IKey getKey(byte[] ski) throws CspException {

        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }
        boolean isGetKeyPair;
        String containerName = "";
        boolean isTempKeyPair;
        boolean signFlag = false; //true for signPubKey , false for encryptPubKey
        long blobBufferLen = 300;
        byte[] cipher = new byte[0];

        //遍历ski的TLV结构
        int skiIndex = 0; //标记ski数组的当前未读取的下标
        while (skiIndex < ski.length) {
            int tag = ski[skiIndex];
            skiIndex++;
            switch (tag) {
                case GmKey.TAG_CONTAINER:
                    int nameLen = ski[skiIndex];
                    skiIndex++;
                    byte[] name = new byte[nameLen];
                    System.arraycopy(ski, skiIndex, name, 0, nameLen);
                    skiIndex += nameLen;
                    containerName = new String(name);
                    break;
                case GmKey.TAG_PUBLICK_KEY_SIGN_FLAG:
                    int flagLen = ski[skiIndex];
                    skiIndex += flagLen;
                    signFlag = (ski[skiIndex] == 1); //Flag的值只有一个byte
                    skiIndex++;
                    break;
                case GmKey.TAG_KEY_CIPHER_DATA:
                    int dataLen = ski[skiIndex];
                    skiIndex++;
                    cipher = new byte[dataLen];
                    System.arraycopy(ski, skiIndex, cipher, 0, dataLen);
                    skiIndex += dataLen;
                    break;
                default:
                    break;
            }
        }

        String[] str = containerName.split("@");
        isGetKeyPair = !TYPE_2_ID.equals(str[1]);

        //获取密钥对：
        if (isGetKeyPair) {
            isTempKeyPair = TYPE_3_ID.equals(str[1]);

            if (isTempKeyPair && mContainers.get(containerName) == null) {
                //临时密钥对的在内存中的容器名称不存在
                throw new CspException("tempKeyPair's container doesn't exist in Ram");
            }

            long hContainer = mSkf.skfOpenApplication(mHApplication, containerName);
            PublicKeyBlob publicKeyBlob = mSkf.skfExportPublicKey(hContainer, signFlag, blobBufferLen);

            if (publicKeyBlob.getType() == PublicKeyBlob.ECC_PUBLIC_KEY_BLOB_TYPE) {
                ECCPublicKeyBlob eccPubKeyBlob = publicKeyBlob.getECCPublicKeyBlob();
                return new GmECCKey(eccPubKeyBlob.getxCoordinate(),
                        eccPubKeyBlob.getyCoordinate());
            } else if (publicKeyBlob.getType() == PublicKeyBlob.RSA_PUBLIC_KEY_BLOB_TYPE) {
                RSAPublicKeyBlob rsaPubKeyBlob = publicKeyBlob.getRSAPublicKeyBlob();
                return new GmRSAKey(rsaPubKeyBlob.getModulus(), rsaPubKeyBlob.getPublicExponent());
            }
        }

        //获取对称密钥：
        long hContainer = mSkf.skfOpenApplication(mHApplication, containerName);
        long hKey = mSkf.skfImportSessionKey(hContainer, AlgorithmID.SGD_SM2_1, cipher, cipher.length);
        return new GmSymmKey(hKey, cipher);
    }

    @Override
    public byte[] hash(byte[] msg, IHashOpts opts) throws CspException {

        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }

        if (SM3.equals(opts.getAlgorithm())) {
            // TODO: 2018/3/30 公钥待确定
            long hashHandle = mSkf.skfDigestInit(mHDev, AlgorithmID.SGD_SM3, null, null, 0);
            return mSkf.skfDigest(hashHandle, msg, msg.length, 256);
        } else if (SHA256.equals(opts.getAlgorithm())) {
            long hashHandle = mSkf.skfDigestInit(mHDev, AlgorithmID.SGD_SHA256, null, null, 0);
            return mSkf.skfDigest(hashHandle, msg, msg.length, 256);
        }

        //SHA1
        long hashHandle = mSkf.skfDigestInit(mHDev, AlgorithmID.SGD_SHA1, null, null, 0);
        return mSkf.skfDigest(hashHandle, msg, msg.length, 160);
    }

    @Override
    public IHash getHash(IHashOpts opts) throws CspException {

        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }

        if (SM3.equals(opts.getAlgorithm())) {
            return new SM3();
        } else if (SHA1.equals(opts.getAlgorithm())) {
            return new SHA1();
        }
        return new SHA256();
    }

    @Override
    public byte[] sign(IKey k, byte[] data, ISignerOpts opts) throws CspException {

        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }

        byte[] digest = new byte[0];


        if (opts.hashFunc().equalsIgnoreCase(SM3) ) {
            SM3 sm3 = new SM3();
            digest = sm3.sum(data);

        } else if (opts.hashFunc().equalsIgnoreCase(SHA1)) {
            SHA1 sha1 = new SHA1();
            digest = sha1.sum(data);

        } else if (opts.hashFunc().equalsIgnoreCase(SHA256)) {
            SHA256 sha256 = new SHA256();
            digest = sha256.sum(data);
        }

        if (ECC.equals(opts.getAlgorithm())) {

            return mSkf.skfECCSignData(mHApplication, digest, digest.length);

        } else if (RSA.equals(opts.getAlgorithm())) {

            return mSkf.skfRSASignData(mHApplication, digest, digest.length);
        }


        return new byte[1];
    }

    @Override
    public boolean verify(IKey k, byte[] signature, byte[] data, ISignerOpts opts) throws CspException {

        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }

        if (k instanceof RSAPublicKeyBlob) {
            RSAPublicKeyBlob rsaPublicKeyBlob = (RSAPublicKeyBlob) k;
            return mSkf.skfRSAVerify(mHDev, rsaPublicKeyBlob, data, data.length, signature, signature.length);

        } else if (k instanceof ECCPublicKeyBlob) {
            ECCPublicKeyBlob eccPublicKeyBlob = (ECCPublicKeyBlob) k;
            return mSkf.skfECCVerify(mHDev, eccPublicKeyBlob, data, data.length, signature);
        }
        return false;
    }

    @Override
    public byte[] encrypt(IKey k, byte[] plaintext, IEncrypterOpts opts) throws CspException {
        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }

        String algorithm = opts.getAlgorithm();
        if (algorithm.equals(SM1) || algorithm.equals(DES) || algorithm.equals(TDES)
                || algorithm.equals(TRI_DES) || algorithm.equals(SSF33) || algorithm.equals(SM4)) {

            if (!(k instanceof GmSymmKey)) {
                throw new CspException("IKey is not the instance of GmSymmKey");
            }
            GmSymmKey sKey = (GmSymmKey) k;
            try {
                long hSessionKey = sKey.getKeyHandle();
                mSkf.skfEncryptInit(hSessionKey, getDefaultBlockCipherParam(algorithm));
                return mSkf.skfEncrypt(hSessionKey,
                        plaintext,
                        plaintext.length,
                        plaintext.length + 16);
            } catch (SarException e) {
                e.printStackTrace();
            }
            return new byte[0];
        }

        if (algorithm.equals(ECC)) {
            if (!(k instanceof GmECCKey)) {
                throw new CspException("IKey is not the instance of GmECCKey");
            }
            GmECCKey eccKey = (GmECCKey) k;
            try {
                ECCCipherBlob eccCipherBlob = mSkf.skfExtECCEncrypt(mHDev,
                        new ECCPublicKeyBlob(eccKey.getxCoordinate(), eccKey.getyCoordinate()),
                        plaintext,
                        plaintext.length);
                return eccCipherBlob.getCipher();
            } catch (SarException e) {
                e.printStackTrace();
            }
        }

        if (algorithm.equals(RSA)) {
            throw new CspException("encryption of RSA is not available");
        }

        return new byte[0];

    }

    @Override
    public byte[] decrypt(IKey k, byte[] ciphertext, IDecrypterOpts opts) throws CspException {
        if (!checkDevAvailable()) {
            throw new CspException("device is not available");
        }

        String algorithm = opts.getAlgorithm();
        if (algorithm.equals(SM1) || algorithm.equals(DES) || algorithm.equals(TDES)
                || algorithm.equals(TRI_DES) || algorithm.equals(SSF33) || algorithm.equals(SM4)) {

            if (!(k instanceof GmSymmKey)) {
                throw new CspException("Ikey is not the instance of GmSymmKey");
            }
            GmSymmKey sKey = (GmSymmKey) k;
            try {
                long hSessionKey = sKey.getKeyHandle();
                mSkf.skfDecryptInit(hSessionKey, getDefaultBlockCipherParam(algorithm));
                return mSkf.skfDecrypt(hSessionKey,
                        ciphertext,
                        ciphertext.length,
                        ciphertext.length + 16);
            } catch (SarException e) {
                e.printStackTrace();
            }
        }
        if (algorithm.equals(ECC)) {
            throw new CspException("decryption of ECC is not available");
        }
        if (algorithm.equals(RSA)) {
            throw new CspException("decryption of RSA is not available");
        }
        return new byte[0];
    }

    private BlockCipherParam getDefaultBlockCipherParam(String algorithm) {
        if (SM1.equals(algorithm)) {
            return BlockCipherParam.getDefault(AlgorithmID.SGD_SM1_ECB);
        } else if (DES.equals(algorithm)) {
            return BlockCipherParam.getDefault(AlgorithmID.SGD_DES_ECB);
        } else if (TDES.equals(algorithm)) {
            return BlockCipherParam.getDefault(AlgorithmID.SGD_TDES_ECB);
        } else if (TRI_DES.equals(algorithm)) {
            return BlockCipherParam.getDefault(AlgorithmID.SGD_3DES_ECB);
        } else if (SSF33.equals(algorithm)) {
            return BlockCipherParam.getDefault(AlgorithmID.SGD_SSF33_ECB);
        } else if (SM4.equals(algorithm)) {
            return BlockCipherParam.getDefault(AlgorithmID.SGD_SMS4_ECB);
        }
        return null;
    }

    @Override
    public byte[] rng(int len, IRngOpts opts) throws CspException {
        throw new CspException();
    }

}
