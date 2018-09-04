/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.gmt0016.ftsafe.ec;

import org.bcia.julongchain.common.exception.JCSKFException;
import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspLog;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.BlockCipherParam;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.DataUtil;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMT0016CspKey;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.SKFCspKey;
import org.bcia.julongchain.csp.intfs.IKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * SM2 Key Impl Class
 *
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public class ECImpl {

    private static final int KEYLEN = 256;

    GMT0016CspLog csplog = new GMT0016CspLog();

    /**
     * Generate SM2 Keypair
     * @param sContainerName  Container Name
     * @param opts  skf factory
     * @return  IKey's instance
     * @throws CspException
     */
    public IKey generateECKey(String sContainerName, IGMT0016FactoryOpts opts) throws CspException {

        try {
            opts.getSKFFactory().SKF_VerifyPIN(opts.getAppHandle(), USER_TYPE, opts.getUserPin());
            List<String> appNamesList = null;
            try {
                appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            }catch(JCSKFException ex) {
                if (ex.getErrCode() == JCSKFException.JC_SKF_NOCONTAINER)
                {
                    String info = "[JC_SKF]:No container! Need create first!";
                    csplog.setLogMsg(info, csplog.LEVEL_DEBUG, ECImpl.class);
                }
                else {
                    String err = String.format("[JC_SKF]:JCSKFException ErrMessage: %s", ex.getMessage());
                    csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
                    throw new CspException(err, ex.getCause());
                }
            }
            boolean bFind = false;
            long lHandleContainer = 0L;
            if(appNamesList != null && !appNamesList.isEmpty()) {
                for(String name : appNamesList) {
                    if(name.equals(sContainerName)) {
                        bFind = true;
                        //save container handle
                        lHandleContainer = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                        break;
                    }
                }}
            if(!bFind)
            {
                //create container handle
                lHandleContainer = opts.getSKFFactory().SKF_CreateContainer(opts.getAppHandle(), sContainerName);
            }
            SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob = opts.getSKFFactory().SKF_GenECCKeyPair(lHandleContainer);

            opts.getSKFFactory().SKF_CloseContainer(lHandleContainer);
            //der
            /*
             * sm2p256v1 1.2.840.10045.2.1  1.2.156.10197.1.301
             *
             */
            byte[] ecpoint = new byte[32*2+1];
            ecpoint[0] = 0x04;
            System.arraycopy(eccPublicKeyBlob.getxCoordinate(), 0, ecpoint, 1, eccPublicKeyBlob.getxCoordinate().length);
            System.arraycopy(eccPublicKeyBlob.getyCoordinate(), 0, ecpoint,
                    1+eccPublicKeyBlob.getxCoordinate().length, eccPublicKeyBlob.getyCoordinate().length);

            byte[] pubder =  ECCDer.encode(eccPublicKeyBlob.getxCoordinate(), eccPublicKeyBlob.getyCoordinate());
            //public hash (no need, maybe need)
            byte[] bytehash = getPublicHash(ecpoint);
            //ski
            //param1 : RSA 1 ECC 2 AES 3 ....
            //param3 : encrypt 0 sign 1
            byte[] skiData = DataUtil.getKeySki(ALG_ECC, sContainerName.getBytes(), TYPE_SIGN, bytehash);

            GMT0016CspKey.ECCPublicCspKey eccPublicCspKey = new GMT0016CspKey.ECCPublicCspKey(skiData, pubder);
            return eccPublicCspKey;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }

    }


    /**
     * Import encrypt keypair
     * @param algid             Algorithm id
     * @param derPublicKey      Publickey's der
     * @param privateKey        Privatekey
     * @param sContainerName    Container Name
     * @param opts              Skf factory
     * @return IKey's instance
     * @throws CspException
     */
    public IKey importECKey(long algid, byte[] derPublicKey, byte[] privateKey,
                            String sContainerName, IGMT0016FactoryOpts opts) throws CspException {

        try {

            opts.getSKFFactory().SKF_VerifyPIN(opts.getAppHandle(), USER_TYPE, opts.getUserPin());
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;
            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    //save container handle
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != TYPE_SM2)
            {
                String str = String.format("[JC_SKF]:The Container Type is not SM2");
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }

            SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob =
                    (SKFCspKey.ECCPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(lContainerHandle, true, true);

            //gen symmkey
            byte[] random = opts.getSKFFactory().SKF_GenRandom(opts.getDevHandle(), 16);
            long lSessionHandle = opts.getSKFFactory().SKF_SetSymmKey(opts.getDevHandle(), random, algid);
            ECCCipherBlob eccCipherBlob = opts.getSKFFactory().SKF_ExtECCEncrypt(opts.getDevHandle(), eccPublicKeyBlob, random, random.length);
            //use session key encrypt private key
            BlockCipherParam blockCipherParam = new BlockCipherParam();
            opts.getSKFFactory().SKF_EncryptInit(lSessionHandle, blockCipherParam);
            //byte[] data = new byte[64];
            //System.arraycopy(privateKey, 0, data, 0, privateKey.length);
            byte[] encdata = opts.getSKFFactory().SKF_Encrypt(lSessionHandle, privateKey, privateKey.length);
            opts.getSKFFactory().SKF_CloseHandle(lSessionHandle);
            byte[] x = ECCDer.decode(derPublicKey, ECCDer.R_X);
            byte[] y = ECCDer.decode(derPublicKey, ECCDer.S_Y);
            SKFCspKey.ECCPublicKeyBlob pubkeyblob = new SKFCspKey.ECCPublicKeyBlob(x, y, KEYLEN);
            ECCEnvelopedKeyBlob eccEnvelopedKeyBlob = new ECCEnvelopedKeyBlob();
            eccEnvelopedKeyBlob.setEncryptedPriKey(encdata);
            eccEnvelopedKeyBlob.setEccCipherBlob(eccCipherBlob);
            eccEnvelopedKeyBlob.setSymmAlgID(algid);
            eccEnvelopedKeyBlob.setEccPublicKeyBlob(pubkeyblob);
            eccEnvelopedKeyBlob.setBits(pubkeyblob.getBit());

            opts.getSKFFactory().SKF_ImportECCKeyPair(lContainerHandle, eccEnvelopedKeyBlob);

            SKFCspKey.ECCPublicKeyBlob KeyBlob =
                    (SKFCspKey.ECCPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(lContainerHandle, false, true);

            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);

            byte[] ecpoint = new byte[32*2];
            //ecpoint[0] = 0x04;
            System.arraycopy(KeyBlob.getxCoordinate(), 0, ecpoint, /*1*/0, KeyBlob.getxCoordinate().length);
            System.arraycopy(KeyBlob.getyCoordinate(), 0, ecpoint,
                    /*1+*/KeyBlob.getxCoordinate().length, KeyBlob.getyCoordinate().length);
            //der
            byte[] pubder =  ECCDer.encode(KeyBlob.getxCoordinate(), KeyBlob.getyCoordinate());
            //public hash (no need, maybe need)
            byte[] bytehash = getPublicHash(ecpoint);
            //ski
            //param1 : RSA 1 ECC 2 AES 3 ....
            //param3 : encrypt 0 sign 1
            byte[] skiData = DataUtil.getKeySki(ALG_ECC, sContainerName.getBytes(), TYPE_ENCRYPT, bytehash);
            GMT0016CspKey.ECCPublicCspKey eccPublicCspKey = new GMT0016CspKey.ECCPublicCspKey(skiData, pubder);
            return eccPublicCspKey;

        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }


    /**
     * Find and Get the designated sm2 key
     * @param sContainerName    Container Name
     * @param bSignFlag         Signatures or encrypted identities
     * @param opts              Skf factory
     * @return IKey's instance
     * @throws CspException
     */
    public IKey getECKey(String sContainerName, boolean bSignFlag, IGMT0016FactoryOpts opts) throws CspException {

        try {
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;

            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    //save container handle
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_INFO, ECImpl.class);
                return null;
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != TYPE_SM2)
            {
                String str = String.format("[JC_SKF]:The Container Type is not SM2");
                csplog.setLogMsg(str, csplog.LEVEL_INFO, ECImpl.class);
                return null;
            }

            SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob =
                    (SKFCspKey.ECCPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(lContainerHandle, bSignFlag, true);

            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);
            //der
            byte[] ecpoint = new byte[32*2+1];
            ecpoint[0] = 0x04;
            System.arraycopy(eccPublicKeyBlob.getxCoordinate(), 0, ecpoint, 1, eccPublicKeyBlob.getxCoordinate().length);
            System.arraycopy(eccPublicKeyBlob.getyCoordinate(), 0, ecpoint,
                    1+eccPublicKeyBlob.getxCoordinate().length, eccPublicKeyBlob.getyCoordinate().length);


            byte[] pubder =  ECCDer.encode(eccPublicKeyBlob.getxCoordinate(), eccPublicKeyBlob.getyCoordinate());
            //public hash (no need, maybe need)
            byte[] bytehash = getPublicHash(ecpoint);
            //ski
            //param1 : RSA 1 ECC 2 AES 3 ....
            //param3 : encrypt 0 sign 1
            byte[] skiData;
            if(bSignFlag)
                skiData = DataUtil.getKeySki(ALG_ECC, sContainerName.getBytes(), TYPE_SIGN, bytehash);
            else
                skiData = DataUtil.getKeySki(ALG_ECC, sContainerName.getBytes(), TYPE_ENCRYPT, bytehash);

            GMT0016CspKey.ECCPublicCspKey eccPublicCspKey = new GMT0016CspKey.ECCPublicCspKey(skiData, pubder);
            return eccPublicCspKey;

        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }

    /**
     *
     * @param msg               Information/(original) text
     * @param lAlgID            Algorithm id
     * @param sContainerName    Container Name
     * @param bSignFlag         Signatures or encrypted identities
     * @param sPucID            Signer ID
     * @param opts              Skf factory
     * @return Z
     * @throws CspException
     */
    public byte[] getHash(byte[] msg, long lAlgID, String sContainerName, boolean bSignFlag, String sPucID, IGMT0016FactoryOpts opts)
            throws CspException {

        try {
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;
            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    //save container handle
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != TYPE_SM2)
            {
                String str = String.format("[JC_SKF]:The Container %s' Type is not SM2", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }

            SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob =
                    (SKFCspKey.ECCPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(lContainerHandle, bSignFlag, true);
            long lHashHandle = opts.getSKFFactory().SKF_DigestInit(opts.getDevHandle(), lAlgID, eccPublicKeyBlob, sPucID);
            byte[] hashData = opts.getSKFFactory().SKF_Digest(lHashHandle, msg, msg.length);
            opts.getSKFFactory().SKF_CloseHandle(lHashHandle);
            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);
            return hashData;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }


    /**
     * Sign with SM2
     * @param digest            summary info
     * @param sContainerName    Container Name
     * @param opts              Skf factory
     * @return signature value
     * @throws CspException
     */

    public byte[] getECSign(byte[] digest, String sContainerName, IGMT0016FactoryOpts opts) throws CspException {
        try {
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;

            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    //save container handle
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != TYPE_SM2)
            {
                String str = String.format("[JC_SKF]:The Container %s' Type is not SM2", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }
            byte[] hashdata =  opts.getSKFFactory().SKF_ECCSignData(lContainerHandle, digest, digest.length);
            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);
            return  hashdata;

        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }

    /**
     * Verify with SM2
     * @param signature         signature value
     * @param digest            summary info
     * @param sContainerName    Container Name
     * @param opts              Skf factory
     * @return success/error
     * @throws CspException
     */
    public boolean getECVerify(byte[] signature, byte[] digest, String sContainerName, IGMT0016FactoryOpts opts) throws CspException {
        try {
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;

            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    //save container handle
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != TYPE_SM2)
            {
                String str = String.format("[JC_SKF]:The Container %s' Type is not SM2", sContainerName);
                csplog.setLogMsg(str, csplog.LEVEL_ERROR, ECImpl.class);
                throw new CspException(str);
            }
            SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob =
                    (SKFCspKey.ECCPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(lContainerHandle, true, true);
            boolean rv = opts.getSKFFactory().SKF_ECCVerify(opts.getDevHandle(), eccPublicKeyBlob, digest, digest.length, signature);
            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);
            return rv;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }

    public byte[] getECEncrypt(byte[] plaintext, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob, IGMT0016FactoryOpts opts) throws CspException {
        try {
            ECCCipherBlob eccCipherBlob = opts.getSKFFactory().SKF_ExtECCEncrypt(opts.getDevHandle(), eccPublicKeyBlob,
                    plaintext, plaintext.length);
            byte[] cipherdata = objectToBytes(eccCipherBlob);
            return cipherdata;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }

    public byte[] getECDecrypt(SKFCspKey.ECCPrivateKeyBlob eccPrivateKeyBlob, byte[] cipherdata, IGMT0016FactoryOpts opts) throws CspException {

        try {
            ECCCipherBlob eccCipherBlob = (ECCCipherBlob)bytesToObject(cipherdata);
            byte[] plaintext = opts.getSKFFactory().SKF_ExtECCDecrypt(opts.getDevHandle(), eccPrivateKeyBlob, eccCipherBlob);
            return plaintext;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, csplog.LEVEL_ERROR, ECImpl.class);
            throw new CspException(err, ex.getCause());
        }
    }


    public static byte[] objectToBytes(Object obj) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream sOut = new ObjectOutputStream(out);
        sOut.writeObject(obj);
        sOut.flush();
        byte[] bytes = out.toByteArray();
        return bytes;
    }


    public static Object bytesToObject(byte[] bytes) throws Exception {
        //byte array to bject
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream sIn = new ObjectInputStream(in);
        return sIn.readObject();
    }



    /**
     * get public key hash data
     *
     */
    private static byte[] getPublicHash(byte[] attecpoint) throws NoSuchAlgorithmException, Exception {
        byte[] tempecpt = DataUtil.data(attecpoint);
        MessageDigest shahash = MessageDigest.getInstance("SHA-1");
        shahash.update(tempecpt);
        return shahash.digest();
    }




}
