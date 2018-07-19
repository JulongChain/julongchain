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
package org.bcia.julongchain.csp.gmt0016.ftsafe.rsa;

import org.bcia.julongchain.common.exception.JCSKFException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspLog;
import org.bcia.julongchain.csp.gmt0016.ftsafe.IGMT0016FactoryOpts;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.BlockCipherParam;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.DataUtil;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.GMT0016CspKey;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.SKFCspKey;
import org.bcia.julongchain.csp.intfs.IKey;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class RSAImpl {

    GMT0016CspLog csplog = new GMT0016CspLog();

    public IKey generateRSAKey(String sContainerName, long lBits, IGMT0016FactoryOpts opts) throws JavaChainException {

        try {
            opts.getSKFFactory().SKF_VerifyPIN(opts.getAppHandle(), USER_TYPE, opts.getUserPin());
            List<String> appNamesList = null;
            try {
                appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            }catch(JCSKFException ex) {
                if (ex.getErrCode() == JCSKFException.JC_SKF_NOCONTAINER)
                {
                    String info = "No container! Need create first!";
                    csplog.setLogMsg(info, 0, RSAImpl.class);
                }
                else {
                    String err = String.format("[JC_SKF]:JCSKFException ErrMessage: %s", ex.getMessage());
                    csplog.setLogMsg(err, 2, RSAImpl.class);
                    throw new JavaChainException(err, ex.getCause());
                }
            }

            boolean bFind = false;
            long lHandleContainer = 0L;
            if (appNamesList != null && !appNamesList.isEmpty()) {
                for(String name : appNamesList) {
                    if(name.equals(sContainerName)) {
                        bFind = true;
                        //save container handle
                        lHandleContainer = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                        break;
                    }
                }
            }
            if(!bFind)
            {
                //create container and save handle
                lHandleContainer = opts.getSKFFactory().SKF_CreateContainer(opts.getAppHandle(), sContainerName);
            }
            SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob = opts.getSKFFactory().SKF_GenRSAKeyPair(lHandleContainer, lBits);

            //public key der
            byte[] pubder =  getPublicDer(rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent());
            //public hash (no need, maybe need)
            byte[] PublicHash = getPublicHash(rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent());
            //ski
            //param1 : RSA 1 ECC 2 AES 3 ....
            //param3 : encrypt 0 sign 1
            byte[] skiData = getKeySki(1, sContainerName.getBytes(), 1, PublicHash);

            opts.getSKFFactory().SKF_CloseContainer(lHandleContainer);

            GMT0016CspKey.RSAPublicCspKey rsaPublicCspKey = new GMT0016CspKey.RSAPublicCspKey(skiData,  pubder);
            return rsaPublicCspKey;

        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeyException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:InvalidKeyException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(IOException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:IOException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }
    }


    public IKey importRSAKey(long algid, byte[] derPublicKey, byte[] derPrivateKey,
                             String sContainerName, IGMT0016FactoryOpts opts) throws JavaChainException {
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
                csplog.setLogMsg(str, 2, RSAImpl.class);
                throw new JavaChainException(str);
            }

            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != 1)
            {
                String str = String.format("[JC_SKF]:The Container is not RSA!");
                csplog.setLogMsg(str, 2, RSAImpl.class);
                throw new JavaChainException(str);
            }

            SKFCspKey.RSAPublicKeyBlob publicKeyBlob =
                    (SKFCspKey.RSAPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(lContainerHandle, true, false);
            //gen session key
/*
			long[] lDataLen = new long[1];
			long lSessionHandle = opts.getSKFFactory().SKF_RSAExportSessionKey(
					lContainerHandle, algid, publicKeyBlob,null, lDataLen);

			byte[] data = new byte[(int)lDataLen[0]];
			lSessionHandle = opts.getSKFFactory().SKF_RSAExportSessionKey(lContainerHandle, algid, publicKeyBlob,data, lDataLen);
*/
            byte[] random = /*opts.getSKFFactory().SKF_GenRandom(opts.getDevHandle(), 16)*/"1234567812345678".getBytes();
            long lSessionHandle = opts.getSKFFactory().SKF_SetSymmKey(opts.getDevHandle(), random, algid);
            byte[] data = opts.getSKFFactory().SKF_ExtRSAPubKeyOperation(opts.getDevHandle(), publicKeyBlob, random, 16L);
            //use session key encrypt private key
            BlockCipherParam blockCipherParam = new BlockCipherParam();
            blockCipherParam.setPaddingType(1);
            blockCipherParam.setIVLen(16);
            opts.getSKFFactory().SKF_EncryptInit(lSessionHandle, blockCipherParam);
            byte[] encdata = opts.getSKFFactory().SKF_Encrypt(lSessionHandle,derPrivateKey, derPrivateKey.length);
            opts.getSKFFactory().SKF_CloseHandle(lSessionHandle);
            //import encrypt key
            opts.getSKFFactory().SKF_ImportRSAKeyPair(lContainerHandle, algid, data, data.length, encdata, encdata.length);
            //export encrypt public key
            SKFCspKey.RSAPublicKeyBlob KeyBlob =
                    (SKFCspKey.RSAPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(lContainerHandle, false, false);

            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);
            //public key der
            byte[] pubder =  getPublicDer(KeyBlob.getModulus(), KeyBlob.getPublicExponent());
            if(!compereByteArray(pubder, derPublicKey))
            {
                csplog.setLogMsg("[JC_SKF]: Import Encrypt Key Error!", 2, RSAImpl.class);
                throw new JavaChainException("[JC_SKF]: Import Encrypt Key Error!");
            }
            //public hash (no need, maybe need)
            byte[] PublicHash = getPublicHash(KeyBlob.getModulus(), KeyBlob.getPublicExponent());
            //ski
            //param1 : RSA 1 ECC 2 AES 3 ....
            //param3 : encrypt 0 sign 1
            byte[] skiData = getKeySki(1, sContainerName.getBytes(), 1, PublicHash);
            GMT0016CspKey.RSAPublicCspKey rsaPublicCspKey = new GMT0016CspKey.RSAPublicCspKey(skiData,  pubder);

            return rsaPublicCspKey;

        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeyException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:InvalidKeyException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(IOException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:IOException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }
    }

    public IKey getRSAKey(String sContainerName, boolean bSignFlag, IGMT0016FactoryOpts opts) throws JavaChainException {
        try {
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;

            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    //open container
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, 1, RSAImpl.class);
                return null;
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != 1)
            {
                String str = String.format("[JC_SKF]:The Container %s' Type is not RSA", sContainerName);
                csplog.setLogMsg(str, 1, RSAImpl.class);
                return null;
            }
            SKFCspKey.RSAPublicKeyBlob publicKeyBlob = (SKFCspKey.RSAPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(
                    lContainerHandle, bSignFlag, false);

            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);

            //public key der
            byte[] pubder =  getPublicDer(publicKeyBlob.getModulus(), publicKeyBlob.getPublicExponent());
            //public hash (no need, maybe need)
            byte[] PublicHash = getPublicHash(publicKeyBlob.getModulus(), publicKeyBlob.getPublicExponent());
            //ski
            //param1 : RSA 1 ECC 2 AES 3 ....
            //param3 : encrypt 0 sign 1
            byte[] skiData = getKeySki(1, sContainerName.getBytes(), 1, PublicHash);
            GMT0016CspKey.RSAPublicCspKey rsaPublicCspKey = new GMT0016CspKey.RSAPublicCspKey(skiData,  pubder);
            return rsaPublicCspKey;

        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(InvalidKeyException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:InvalidKeyException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:NoSuchAlgorithmException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(IOException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:IOException ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(Exception ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:Exception ErrMessage: %s", ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }

    }

    public byte[] getRSASign(byte[] digest, String sContainerName, IGMT0016FactoryOpts opts) throws JavaChainException {
        try {
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;
            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, 2, RSAImpl.class);
                throw new JavaChainException(str);
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != 1)
            {
                String str = String.format("[JC_SKF]:The Container %s' Type is not RSA", sContainerName);
                csplog.setLogMsg(str, 2, RSAImpl.class);
                throw new JavaChainException(str);
            }

            byte[] signature = opts.getSKFFactory().SKF_RSASignData(lContainerHandle, digest, digest.length);
            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);
            return signature;
        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }
    }

    public boolean getRSAVerify(byte[] signature, byte[] digest, String sContainerName, IGMT0016FactoryOpts opts)
            throws JavaChainException{

        try {
            List<String> appNamesList = opts.getSKFFactory().SKF_EnumContainer(opts.getAppHandle());
            boolean bFind = false;
            long lContainerHandle = 0L;
            for(String name : appNamesList) {
                if(name.equals(sContainerName)) {
                    bFind = true;
                    lContainerHandle = opts.getSKFFactory().SKF_OpenContainer(opts.getAppHandle(), sContainerName);
                    break;
                }
            }
            if(!bFind)
            {
                String str = String.format("[JC_SKF]:No Find The Container %s!", sContainerName);
                csplog.setLogMsg(str, 2, RSAImpl.class);
                throw new JavaChainException(str);
            }
            long type = opts.getSKFFactory().SKF_GetContainerType(lContainerHandle);
            if(type != 1)
            {
                String str = String.format("[JC_SKF]:The Container %s' Type is not RSA", sContainerName);
                csplog.setLogMsg(str, 2, RSAImpl.class);
                throw new JavaChainException(str);
            }

            SKFCspKey.RSAPublicKeyBlob publicKeyBlob = (SKFCspKey.RSAPublicKeyBlob)opts.getSKFFactory().SKF_ExportPublicKey(
                    lContainerHandle, true, false);

            opts.getSKFFactory().SKF_CloseContainer(lContainerHandle);

            boolean rv = opts.getSKFFactory().SKF_RSAVerify(opts.getDevHandle(), publicKeyBlob, digest, signature);
            return rv;

        }catch(SarException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:SarException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrorCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }catch(JCSKFException ex) {
            ex.printStackTrace();
            String err = String.format("[JC_SKF]:JCSKFException ErrCode: 0x%08x, ErrMessage: %s", ex.getErrCode(), ex.getMessage());
            csplog.setLogMsg(err, 2, RSAImpl.class);
            throw new JavaChainException(err, ex.getCause());
        }

    }



    public byte[] getPublicDer(byte[] modulus, byte[] publicExponent) throws InvalidKeyException {
        BigInteger b_n = new BigInteger(1, modulus);
        byte[] temp = b_n.toByteArray();
        BigInteger b_e = new BigInteger(publicExponent);
        RSAPublicKeyImpl rsapublickeyimpl = new RSAPublicKeyImpl(b_n, b_e);
        return rsapublickeyimpl.getEncoded();
    }

    public byte[] getPublicHash(byte[] modulus, byte[] publicExponent)
            throws NoSuchAlgorithmException,IOException, InvalidKeyException {
        MessageDigest shahash = MessageDigest.getInstance("SHA-1");
        byte[] out = getPublicDer(modulus, publicExponent);
        shahash.update(out);
        return shahash.digest();
    }

    public byte[] getKeySki(int keytype, byte[] container, int signflag, byte[] pubhash) throws Exception {

        byte flag[] = new byte[1];
        flag[0] = (byte)signflag; //for sign
        byte type[] = new byte[1];
        type[0] = (byte)keytype; //RSA
        byte[] tlv_Container = DataUtil.getTLV(TAG_CONTAINER, container, container.length);
        byte[] tlv_SignFlag = DataUtil.getTLV(TAG_PUBLICK_KEY_SIGN_FLAG, flag, 1);
        byte[] tlv_Type = DataUtil.getTLV(TAG_KEY_TYPE, type, 1);
        byte[] tlv_PublicHash = DataUtil.getTLV(TAG_PUBLICK_KEY_HASH, pubhash, pubhash.length);
        byte[] skiData = new byte[tlv_Type.length + tlv_Container.length + tlv_SignFlag.length + tlv_PublicHash.length];
        int pos = 0;
        System.arraycopy(tlv_Type, 0, skiData, 0, tlv_Type.length);
        pos += tlv_Type.length;
        System.arraycopy(tlv_Container, 0, skiData, pos, tlv_Container.length);
        pos += tlv_Container.length;
        System.arraycopy(tlv_SignFlag, 0, skiData, pos, tlv_SignFlag.length);
        pos += tlv_SignFlag.length;
        System.arraycopy(tlv_PublicHash, 0, skiData, pos, tlv_PublicHash.length);
        return skiData;

    }

    public static boolean compereByteArray(byte[] b1, byte[] b2) {

        if(b1.length == 0 || b2.length == 0 ){
            return false;
        }

        if (b1.length != b2.length) {
            return false;
        }

        boolean isEqual = true;
        for (int i = 0; i < b1.length && i < b2.length; i++) {
            if (b1[i] != b2[i]) {
                System.out.println("different");
                isEqual = false;
                break;
            }
        }
        return isEqual;
    }
}
