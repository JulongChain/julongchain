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

import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.csp.gmt0016.excelsecu.bean.*;
import org.bcia.julongchain.csp.gmt0016.excelsecu.common.Constants;
import org.bcia.julongchain.csp.gmt0016.excelsecu.security.ECCPublicKeyBlob;
import org.bcia.julongchain.csp.gmt0016.excelsecu.security.RSAPublicKeyBlob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class Skf implements ISkf {

    public long SKF_ConnectDev(String name) throws SarException {
        byte[] nameBytes = name.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] buffer = new long[]{-1};
        checkResult(SKF_ConnectDev_N(nameBytesWithZeroPadding, buffer));
        return buffer[0];
    }

    public byte[] SKF_GenRandom(long hDev, int length) throws SarException {
        checkHandler(hDev);
        byte[] randomData = new byte[length];
        checkResult(SKF_GenRandom_N(hDev, randomData, length));
        return randomData;
    }


    private void checkHandler(long hDev) throws SarException {
        if (hDev <= 0) {
            throw new SarException(SarException.SAR_INVALIDHANDLEERR);
        }

    }

    private void checkResult(int result) throws SarException {
        if (result != SarException.SAR_OK) {
            throw new SarException(result);
        }
    }

    private boolean checkVerify(int result) throws SarException {
        if (result != SarException.SAR_OK) {
            throw new SarException(result);
        }

        return true;

    }


    public long SKF_GetDevState(String name) throws SarException {
        byte[] nameBytes = name.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] state = new long[1];
        checkResult(SKF_GetDevState_N(nameBytesWithZeroPadding, state));
        return state[0];
    }

    public long SKF_VerifyPIN(long hApplication, long pinType, String pin) throws SarException {
        byte[] pinBytes = pin.getBytes();
        byte[] pinBytesWithZeroPadding = new byte[pinBytes.length + 1];
        System.arraycopy(pinBytes, 0, pinBytesWithZeroPadding, 0, pinBytes.length);

        long[] retryCount = new long[1];
        checkResult(SKF_VerifyPIN_N(hApplication, pinType, pinBytesWithZeroPadding, retryCount));
        return retryCount[0];
    }

    public long SKF_CreateContainer(long hApplication, String containerName) throws SarException {
        byte[] nameBytes = containerName.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] hContainer = new long[1];
        checkResult(SKF_CreateContainer_N(hApplication, nameBytesWithZeroPadding, hContainer));
        return hContainer[0];
    }

    public RSAPublicKeyBlob SKF_GenRSAKeyPair(long hContainer, long bitsLen) throws SarException {
        long[] algID = new long[1];
        long[] bitLen = new long[1];
        byte[] modulus = new byte[Constants.MAX_RSA_MODULUS_LEN];
        byte[] publicExponent = new byte[Constants.MAX_RSA_EXPONENT_LEN];
        checkResult(SKF_GenRSAKeyPair_N(hContainer, bitsLen, algID, bitLen, modulus, publicExponent));
        return new RSAPublicKeyBlob(algID[0], bitLen[0], modulus, publicExponent);
    }

    public ECCPublicKeyBlob SKF_GenECCKeyPair(long hContainer, long algId) throws SarException {
        byte[] xCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        byte[] yCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN];
        checkResult(SKF_GenECCKeyPair_N(hContainer, algId, xCoordinate, yCoordinate));
        return new ECCPublicKeyBlob(xCoordinate, yCoordinate);
    }

    public long SKF_GetContainerType(long hContainer) throws SarException {
        long[] containerType = new long[1];
        checkResult(SKF_GetContainerType_N(hContainer, containerType));
        return containerType[0];
    }

    public List<String> SKF_EnumContainer(long hApplication, long size) throws SarException {

        byte[] containerNameBytes = new byte[(int) size];
        checkResult(SKF_EnumContainer_N(hApplication,containerNameBytes,size));
        return parseContainerNameBytes(containerNameBytes);


    }

    private List<String> parseContainerNameBytes(byte[] containerNameBytes) {
        String[] names=new String(containerNameBytes,0,containerNameBytes.length).split("\0");
        return new ArrayList<String>(Arrays.asList(names));

    }

    public void SKF_DeleteContainer(long hApplication, String containerName) throws SarException {

        checkResult(SKF_DELETECONTAINER_N(hApplication, containerName.getBytes()));
    }

    public SessionKey SKF_ECCExportSessionKey(long hContainer, long algId, ECCPublicKeyBlob eccPublicKeyBlob) throws SarException {
        byte[] pubXCoordinate = eccPublicKeyBlob.getxCoordinate();
        byte[] pubYCoordinate = eccPublicKeyBlob.getyCoordinate();
        byte[] cipherXCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN / 8];
        byte[] cipherYCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN / 8];
        byte[] hash = new byte[32];
        long[] cipherLen = new long[1];
        byte[] cipher = new byte[1024];
        long[] hSessionKey = new long[1];

        checkResult(SKF_ECCExportSessionKey_N(hContainer,
                algId,
                pubXCoordinate,
                pubYCoordinate,
                cipherXCoordinate,
                cipherYCoordinate,
                hash,
                cipherLen,
                cipher,
                hSessionKey));
        byte[] cipherData = new byte[(int)cipherLen[0]];
        System.arraycopy(cipher, 0, cipherData, 0, (int)cipherLen[0]);
        ECCCipherBlob eccCipherBlob = new ECCCipherBlob(cipherXCoordinate,
                cipherYCoordinate,
                hash,
                cipherData);
        return new SessionKey(eccCipherBlob, hSessionKey[0]);
    }

    public void SKF_ImportECCKeyPair(long hContainer, EnvelopedKeyBlob envelopedKeyBlob) throws SarException {
        long version = envelopedKeyBlob.getVersion();
        long symmAlgID = envelopedKeyBlob.getSymmAlgID();
        long bits = envelopedKeyBlob.getBits();
        byte[] encryptedPriKey = envelopedKeyBlob.getEncryptedPriKey();

        ECCPublicKeyBlob eccPublicKeyBlob = envelopedKeyBlob.getPubKey();
        byte[] pubXCoordinate = eccPublicKeyBlob.getxCoordinate();
        byte[] pubYCoordinate = eccPublicKeyBlob.getyCoordinate();

        ECCCipherBlob eccCipherBlob = envelopedKeyBlob.getEccCipherBlob();
        byte[] cipherXCoordinate = eccCipherBlob.getxCoordinate();
        byte[] cipherYCoordinate = eccCipherBlob.getyCoordinate();
        byte[] hash = eccCipherBlob.getHash();
        long[] cipherLen = new long[1];
        byte[] cipher = eccCipherBlob.getCipher();
        cipherLen[0] = cipher.length;

        checkResult(SKF_ImportECCKeyPair_N(hContainer,
                version,
                symmAlgID,
                bits,
                encryptedPriKey,
                pubXCoordinate,
                pubYCoordinate,
                cipherXCoordinate,
                cipherYCoordinate,
                hash,
                cipherLen,
                cipher));
    }

    public void SKF_ImportRSAKeyPair(long hContainer,
                                     long symAlgId,
                                     byte[] wrappedKey,
                                     long wrappedKeyLen,
                                     byte[] encryptedData,
                                     long encryptedDataLen) throws SarException {
        checkResult(SKF_ImportRSAKeyPair_N(hContainer, symAlgId, wrappedKey, wrappedKeyLen, encryptedData, encryptedDataLen));
    }

    public long SKE_CreateApplication(long hDev,
                                      String appName,
                                      String adminPin,
                                      int adminPinRetryCount,
                                      String userPin,
                                      int userPinRetryCount,
                                      int createFileRights) throws SarException {
        byte[] appNameBytes = appName.getBytes();
        byte[] appNameBytesWithZeroPadding = new byte[appNameBytes.length + 1];
        System.arraycopy(appNameBytes, 0, appNameBytesWithZeroPadding, 0, appNameBytes.length);

        byte[] adminPinBytes = adminPin.getBytes();
        byte[] adminPinBytesWithZeroPadding = new byte[adminPinBytes.length + 1];
        System.arraycopy(adminPinBytes, 0, adminPinBytesWithZeroPadding, 0, adminPinBytes.length);

        byte[] userPinBytes = userPin.getBytes();
        byte[] userPinBytesWithZeroPadding = new byte[userPinBytes.length + 1];
        System.arraycopy(userPinBytes, 0, userPinBytesWithZeroPadding, 0, userPinBytes.length);

        long[] hApplication = new long[1];
        checkResult(SKE_CreateApplication_N(hDev,
                appNameBytesWithZeroPadding,
                adminPinBytesWithZeroPadding,
                adminPinRetryCount,
                userPinBytesWithZeroPadding,
                userPinRetryCount,
                createFileRights,
                hApplication));
        return hApplication[0];
    }


    public PublicKeyBlob SKF_ExportPublicKey(long hContainer, boolean signFlag, long blobLen) throws SarException {
        int[] blobType = new int[1];
        long[] algID = new long[1];
        long[] bitLen = new long[1];
        byte[] modulus = new byte[Constants.MAX_RSA_MODULUS_LEN];
        byte[] publicExponent = new byte[Constants.MAX_RSA_EXPONENT_LEN];

        byte[] xCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        byte[] yCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN];

        long[] blobLength = new long[1];
        blobLength[0] = blobLen;

        checkResult(SKF_ExportPublicKey_N(hContainer,
                signFlag,
                blobType,
                algID,
                bitLen,
                modulus,
                publicExponent,
                xCoordinate,
                yCoordinate,
                blobLength
        ));
        PublicKeyBlob publicKeyBlob;
        if (blobType[0] == PublicKeyBlob.RSA_PUBLIC_KEY_BLOB_TYPE) {
            RSAPublicKeyBlob rsaPublicKeyBlob = new RSAPublicKeyBlob(algID[0], bitLen[0], modulus, publicExponent);
            publicKeyBlob = new PublicKeyBlob();
            publicKeyBlob.setType(PublicKeyBlob.RSA_PUBLIC_KEY_BLOB_TYPE);
            publicKeyBlob.setBlobLen(blobLength[0]);
            publicKeyBlob.setRSAPublicKeyBlob(rsaPublicKeyBlob);
            return publicKeyBlob;
        }
        ECCPublicKeyBlob eccPublicKeyBlob = new ECCPublicKeyBlob(xCoordinate, yCoordinate);
        publicKeyBlob = new PublicKeyBlob();
        publicKeyBlob.setType(PublicKeyBlob.ECC_PUBLIC_KEY_BLOB_TYPE);
        publicKeyBlob.setBlobLen(blobLength[0]);
        publicKeyBlob.setECCPublicKeyBlob(eccPublicKeyBlob);
        return publicKeyBlob;
    }

    public void SKF_EncryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException {
        byte[] IV = blockCipherParam.getIV();
        long paddingType = blockCipherParam.getPaddingType();
        long feedBitLen = blockCipherParam.getFeedBitLen();
        checkResult(SKF_EncryptInit_N(hKey, IV, IV.length, paddingType, feedBitLen));
    }

    public byte[] SKF_Encrypt(long hKey, byte[] data, long dataLen, long encryptedBufferLen) throws SarException {
        byte[] encryptedDataTmp = new byte[(int)dataLen];
        long[] encryptedLen = new long[1];
        encryptedLen[0] = encryptedBufferLen;
        checkResult(SKF_Encrypt_N(hKey, data, dataLen, encryptedDataTmp, encryptedLen));
        byte[] encryptedData = new byte[(int)encryptedLen[0]];
        System.arraycopy(encryptedDataTmp, 0, encryptedData, 0, (int)encryptedLen[0]);
        return encryptedData;
    }

    public void SKF_CloseHandle(long hHandle) throws SarException {
        checkResult(SKF_CloseHandle_N(hHandle));
    }

    public long SKF_ImportSessionKey(long hContainer, long algId, byte[] wrappedData, long wrappedLen) throws SarException {

        long[] key = new long[1];
        checkResult(SKF_ImportSessionKey_N(hContainer, algId, wrappedData, wrappedLen, key));
        return key[0];
    }


    public long SKF_DigestInit(long hDev, long algId, ECCPublicKeyBlob eccPublicKeyBlob, char[] pucID, long idLen) throws SarException {
        long[] hHash = new long[1];
        checkResult(SKF_DigestInit_N(hDev, algId, eccPublicKeyBlob.getxCoordinate(), eccPublicKeyBlob.getyCoordinate(), pucID, idLen, hHash));
        return hHash[0];
    }

    public byte[] SKF_Digest(long hHash, byte[] data, long dataLen, long hashDataBufferLen) throws SarException {
        byte[] hash = new byte[(int) hashDataBufferLen];
        long[] hashLen = new long[1];
        hashLen[0] = hashDataBufferLen;
        checkResult(SKF_Digest_N(hHash, data, dataLen, hash, hashLen));
        byte[] hashData = new byte[(int)hashLen[0]];
        System.arraycopy(hash, 0, hashData, 0, (int)hashLen[0]);
        return hashData;
    }

    public byte[] SKF_RSASignData(long hContainer, byte[] data, long signBufferLen) throws SarException {
        byte[] sign = new byte[(int) signBufferLen];
        long[] signLen = new long[1];
        signLen[0] = signBufferLen;
        checkResult(SKF_RSASignData_N(hContainer, data, data.length, sign, signLen));
        byte[] signData = new byte[(int) signLen[0]];
        System.arraycopy(sign, 0, signData, 0, (int)signLen[0]);
        return signData;
    }


    public byte[] SKF_ECCSignData(long hContainer, byte[] data, long dataLen) throws SarException {
        byte[] r = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        byte[] s = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        checkResult(SKF_ECCSignData_N(hContainer,
                data,
                dataLen,
                r,
                s));
        return Der.encode(r, s);
    }


    public boolean SKF_RSAVerify(long hDev, RSAPublicKeyBlob rsaPublicKeyBlob, byte[] data, long dataLen,
                                 byte[] signature, long signLen) throws SarException {

        return checkVerify(SKF_RSAVerify_N(hDev,
                rsaPublicKeyBlob.getAlgID(),
                rsaPublicKeyBlob.getBitLen(),
                rsaPublicKeyBlob.getModulus(),
                rsaPublicKeyBlob.getPublicExponent(),
                data,
                dataLen,
                signature,
                signLen));
    }

    public boolean SKF_ECCVerify(long hDev, ECCPublicKeyBlob eccPublicKeyBlob, byte[] data,
                                 long dataLen, byte[] signature) throws SarException {
        return checkVerify(SKF_ECCVerify_N(hDev,
                eccPublicKeyBlob.getxCoordinate(),
                eccPublicKeyBlob.getyCoordinate(),
                data,
                dataLen,
                Der.decode(signature, Der.R),
                Der.decode(signature, Der.S)));
    }


    public void SKF_DecryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException {
        checkResult(SKF_DecryptInit_N(hKey,
                blockCipherParam.getIV(),
                blockCipherParam.getIV().length,
                blockCipherParam.getPaddingType(),
                blockCipherParam.getFeedBitLen()));
    }

    public byte[] SKF_Decrypt(long hKey, byte[] encryptedData, long encryptedLen, long dataBufferLen) throws
            SarException {
        byte[] decryptDataTmp = new byte[(int) dataBufferLen];
        long[] decryptLen = new long[1];
        decryptLen[0] = dataBufferLen;
        checkResult(SKF_Decrypt_N(hKey, encryptedData, encryptedLen, decryptDataTmp, decryptLen));
        byte[] decryptData = new byte[(int)decryptLen[0]];
        System.arraycopy(decryptDataTmp, 0, decryptData, 0, (int)decryptLen[0]);
        return decryptData;
    }


    public List<String> SKF_EnumDev(boolean present, long size) throws SarException {
        byte[] devNamesBytes = new byte[(int) size];
        checkResult(SKF_EnumDev_N(present, devNamesBytes, size));
        return parseDevNameBytes(devNamesBytes);
    }

    private List<String> parseDevNameBytes(byte[] devNamesBytes) {
        String[] names=new String(devNamesBytes,0,devNamesBytes.length).split("\0");
        return new ArrayList<String>(Arrays.asList(names));
    }

    public void SKF_LockDev(long hDev, long timeOut) throws SarException {
        checkResult(SKF_LockDev_N(hDev, timeOut));
    }

    public long SKF_OpenApplication(long hDev, String name) throws SarException {
        byte[] nameBytes = name.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] phApplication = new long[1];
        checkResult(SKF_OpenApplication_N(hDev, nameBytesWithZeroPadding, phApplication));
        return phApplication[0];
    }

    public void SKF_UnlockDev(long hDev) throws SarException {
        checkResult(SKF_UnlockDev_N(hDev));
    }

    public void SKF_DisconnectDev(long hDev) throws SarException {
        checkResult(SKF_DisconnectDev_N(hDev));
    }

    public void SKF_CloseContainer(long hContainer) throws SarException {
        checkResult(SKF_CloseContainer_N(hContainer));
    }

    public ECCCipherBlob SKF_ExtECCEncrypt(long hDev, ECCPublicKeyBlob eccPublicKeyBlob, byte[] plainText, long plainTextLen) throws SarException {
        byte[] pubXCoordinate = eccPublicKeyBlob.getxCoordinate();
        byte[] pubYCoordinate = eccPublicKeyBlob.getyCoordinate();
        byte[] cipherXCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN / 8];
        byte[] cipherYCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN / 8];
        byte[] hash = new byte[32];
        long[] cipherLen = new long[1];
        byte[] cipher = new byte[1024];

        checkResult(SKF_ExtECCEncrypt_N(hDev,
                pubXCoordinate,
                pubYCoordinate,
                plainText,
                plainTextLen,
                cipherXCoordinate,
                cipherYCoordinate,
                hash,
                cipherLen,
                cipher));
        byte[] cipherData = new byte[(int)cipherLen[0]];
        System.arraycopy(cipher, 0, cipherData, 0, (int)cipherLen[0]);
        return new ECCCipherBlob(cipherXCoordinate, cipherYCoordinate, hash, cipherData);
    }

    private static native int SKF_ConnectDev_N(byte[] name, long[] hDev);

    private static native int SKF_GenRandom_N(long hDev, byte[] random, int randomLength);

    private static native int SKF_GetDevState_N(byte[] name, long[] state);

    private static native int SKF_VerifyPIN_N(long hApplication, long pinType, byte[] pin, long[] retryCount);

    private static native int SKF_CreateContainer_N(long hApplication, byte[] containerName, long[] hContainer);

    private static native int SKF_GenRSAKeyPair_N(long hContainer,
                                                  long bitsLen,
                                                  long[] algID,
                                                  long[] bitLen,
                                                  byte[] modulus,
                                                  byte[] publicExponent);

    private static native int SKF_GenECCKeyPair_N(long hContainer, long algId, byte[] xCoordinate,
                                                  byte[] yCoordinate);

    private static native int SKF_GetContainerType_N(long hContainer, long[] containerType);

    private static native int SKF_ECCExportSessionKey_N(long hContainer,
                                                        long algId,
                                                        byte[] pubXCoordinate,
                                                        byte[] pubYCoordinate,
                                                        byte[] cipherXCoordinate,
                                                        byte[] cipherYCoordinate,
                                                        byte[] Hash,
                                                        long[] cipherLen,
                                                        byte[] cipher,
                                                        long[] sessionKey);

    private static native int SKF_ImportECCKeyPair_N(long hContainer,
                                                     long version,
                                                     long symmAlgID,
                                                     long bits,
                                                     byte[] encryptedPriKey,
                                                     byte[] pubXCoordinate,
                                                     byte[] pubYCoordinate,
                                                     byte[] cipherXCoordinate,
                                                     byte[] cipherYCoordinate,
                                                     byte[] Hash,
                                                     long[] cipherLen,
                                                     byte[] cipher);

    private static native int SKF_ImportRSAKeyPair_N(long hContainer,
                                                     long symAlgId,
                                                     byte[] wrappedKey,
                                                     long wrappedKeyLen,
                                                     byte[] encryptedData,
                                                     long encryptedDataLen);

    private static native int SKE_CreateApplication_N(long hDev,
                                                      byte[] appName,
                                                      byte[] adminPin,
                                                      int adminPinRetryCount,
                                                      byte[] userPin,
                                                      int userPinRetryCount,
                                                      int createFileRights,
                                                      long[] hApplication);

    private static native int SKF_ExportPublicKey_N(long hContainer,
                                                    boolean signFlag,
                                                    int[] blobType,
                                                    long[] algID,
                                                    long[] bitLen,
                                                    byte[] modulus,
                                                    byte[] publicExponent,
                                                    byte[] xCoordinate,
                                                    byte[] yCoordinate,
                                                    long[] blobLen);

    private static native int SKF_EncryptInit_N(long hKey,
                                                byte[] IV,
                                                long IVLen,
                                                long paddingType,
                                                long feedBitLen);

    private static native int SKF_Encrypt_N(long hKey, byte[] data, long dataLen, byte[] encryptedData,
                                            long[] encryptedLen);

    private static native int SKF_CloseHandle_N(long hHandle);

    private static native int SKF_ImportSessionKey_N(long hContainer,
                                                     long algId,
                                                     byte[] wrappedData,
                                                     long wrappedLen,
                                                     long[] hKey);

    private static native int SKF_DigestInit_N(long hDev,
                                               long algId,
                                               byte[] xCoordinate,
                                               byte[] yCoordinate,
                                               char[] Id,
                                               long idLen,
                                               long[] hHash);

    private static native int SKF_Digest_N(long hHash, byte[] data, long dataLen, byte[] hashData,
                                           long hashLen[]);

    private static native int SKF_RSASignData_N(long hContainer, byte[] data, long dataLen, byte[] signature,
                                                long[] signLen);

    private static native int SKF_ECCSignData_N(long hContainer, byte[] data, long dataLen, byte[] r, byte[] s)
            ;

    private static native int SKF_RSAVerify_N(long hDev,
                                              long algID,
                                              long bitLen,
                                              byte[] modulus,
                                              byte[] publicExponent,
                                              byte[] data,
                                              long dataLen,
                                              byte[] signature,
                                              long signLen);

    private static native int SKF_ECCVerify_N(long hDev,
                                              byte[] xCoordinate,
                                              byte[] yCoordinate,
                                              byte[] data,
                                              long dataLen,
                                              byte[] r,
                                              byte[] s);

    private static native int SKF_DecryptInit_N(long hKey,
                                                byte[] IV,
                                                long IVLen,
                                                long paddingType,
                                                long feedBitLen);

    private static native int SKF_Decrypt_N(long hKey, byte[] encryptedData, long encryptedDataLen, byte[] data,
                                            long[] dataLen);

    private static native int SKF_EnumDev_N(boolean present, byte[] nameList, long size);

    private static native int SKF_LockDev_N(long hDev, long timeOut);

    private static native int SKF_OpenApplication_N(long hDev, byte[] appName, long[] hApplication);

    private static native int SKF_UnlockDev_N(long hDev);

    private static native int SKF_DisconnectDev_N(long hDev);

    private static native int SKF_CloseContainer_N(long hContainer);

    private static native int SKF_ExtECCEncrypt_N(long hDev,
                                                  byte[] pubXCoordinate,
                                                  byte[] pubYCoordinate,
                                                  byte[] plainText,
                                                  long plainTextLen,
                                                  byte[] cipherXCoordinate,
                                                  byte[] cipherYCoordinate,
                                                  byte[] hash,
                                                  long[] cipherLen,
                                                  byte[] cipher);

    private static native int SKF_EnumContainer_N(long hApplication, byte[] nameList, long size);

    private static native int SKF_DELETECONTAINER_N(long hApplication, byte[] containerName);
}
