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

    @Override
    public long skfConnectDev(String name) throws SarException {
        byte[] nameBytes = name.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] buffer = new long[]{-1};
        checkResult(SkfConnectDev(nameBytesWithZeroPadding, buffer));
        return buffer[0];
    }

    @Override
    public byte[] skfGenRandom(long hDev, int length) throws SarException {
        checkHandler(hDev);
        byte[] randomData = new byte[length];
        checkResult(SkfGenRandom(hDev, randomData, length));
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


    @Override
    public long skfGetDevState(String name) throws SarException {
        byte[] nameBytes = name.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] state = new long[1];
        checkResult(SkfGetDevState(nameBytesWithZeroPadding, state));
        return state[0];
    }

    @Override
    public long skfVerifyPIN(long hApplication, long pinType, String pin) throws SarException {
        byte[] pinBytes = pin.getBytes();
        byte[] pinBytesWithZeroPadding = new byte[pinBytes.length + 1];
        System.arraycopy(pinBytes, 0, pinBytesWithZeroPadding, 0, pinBytes.length);

        long[] retryCount = new long[1];
        checkResult(SkfVerifyPIN(hApplication, pinType, pinBytesWithZeroPadding, retryCount));
        return retryCount[0];
    }

    @Override
    public long skfCreateContainer(long hApplication, String containerName) throws SarException {
        byte[] nameBytes = containerName.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] hContainer = new long[1];
        checkResult(SkfCreateContainer(hApplication, nameBytesWithZeroPadding, hContainer));
        return hContainer[0];
    }

    @Override
    public RSAPublicKeyBlob skfGenRSAKeyPair(long hContainer, long bitsLen) throws SarException {
        long[] algID = new long[1];
        long[] bitLen = new long[1];
        byte[] modulus = new byte[Constants.MAX_RSA_MODULUS_LEN];
        byte[] publicExponent = new byte[Constants.MAX_RSA_EXPONENT_LEN];
        checkResult(SkfGenRSAKeyPair(hContainer, bitsLen, algID, bitLen, modulus, publicExponent));
        return new RSAPublicKeyBlob(algID[0], bitLen[0], modulus, publicExponent);
    }

    @Override
    public ECCPublicKeyBlob skfGenECCKeyPair(long hContainer, long algId) throws SarException {
        byte[] xCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        byte[] yCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN];
        checkResult(SkfGenECCKeyPair(hContainer, algId, xCoordinate, yCoordinate));
        return new ECCPublicKeyBlob(xCoordinate, yCoordinate);
    }

    @Override
    public long skfGetContainerType(long hContainer) throws SarException {
        long[] containerType = new long[1];
        checkResult(SkfGetContainerType(hContainer, containerType));
        return containerType[0];
    }

    @Override
    public List<String> skfEnumContainer(long hApplication, long size) throws SarException {

        byte[] containerNameBytes = new byte[(int) size];
        checkResult(SkfEnumContainer(hApplication,containerNameBytes,size));
        return parseContainerNameBytes(containerNameBytes);


    }

    private List<String> parseContainerNameBytes(byte[] containerNameBytes) {
        String[] names=new String(containerNameBytes,0,containerNameBytes.length).split("\0");
        return new ArrayList<>(Arrays.asList(names));

    }

    @Override
    public void skfDeleteContainer(long hApplication, String containerName) throws SarException {

        checkResult(SkfDELETECONTAINER(hApplication, containerName.getBytes()));
    }

    @Override
    public SessionKey skfECCExportSessionKey(long hContainer, long algId, ECCPublicKeyBlob eccPublicKeyBlob) throws SarException {
        byte[] pubXCoordinate = eccPublicKeyBlob.getxCoordinate();
        byte[] pubYCoordinate = eccPublicKeyBlob.getyCoordinate();
        byte[] cipherXCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN / 8];
        byte[] cipherYCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN / 8];
        byte[] hash = new byte[32];
        long[] cipherLen = new long[1];
        byte[] cipher = new byte[1024];
        long[] hSessionKey = new long[1];

        checkResult(SkfECCExportSessionKey(hContainer,
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

    @Override
    public void skfImportECCKeyPair(long hContainer, EnvelopedKeyBlob envelopedKeyBlob) throws SarException {
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

        checkResult(SkfImportECCKeyPair(hContainer,
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

    @Override
    public void skfImportRSAKeyPair(long hContainer,
                                     long symAlgId,
                                     byte[] wrappedKey,
                                     long wrappedKeyLen,
                                     byte[] encryptedData,
                                     long encryptedDataLen) throws SarException {
        checkResult(SkfImportRSAKeyPair(hContainer, symAlgId, wrappedKey, wrappedKeyLen, encryptedData, encryptedDataLen));
    }

    @Override
    public long skfCreateApplication(long hDev,
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
        checkResult(SKE_CreateApplication(hDev,
                appNameBytesWithZeroPadding,
                adminPinBytesWithZeroPadding,
                adminPinRetryCount,
                userPinBytesWithZeroPadding,
                userPinRetryCount,
                createFileRights,
                hApplication));
        return hApplication[0];
    }


    @Override
    public PublicKeyBlob skfExportPublicKey(long hContainer, boolean signFlag, long blobLen) throws SarException {
        int[] blobType = new int[1];
        long[] algID = new long[1];
        long[] bitLen = new long[1];
        byte[] modulus = new byte[Constants.MAX_RSA_MODULUS_LEN];
        byte[] publicExponent = new byte[Constants.MAX_RSA_EXPONENT_LEN];

        byte[] xCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        byte[] yCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN];

        long[] blobLength = new long[1];
        blobLength[0] = blobLen;

        checkResult(SkfExportPublicKey(hContainer,
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

    @Override
    public void skfEncryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException {
        byte[] IV = blockCipherParam.getIV();
        long paddingType = blockCipherParam.getPaddingType();
        long feedBitLen = blockCipherParam.getFeedBitLen();
        checkResult(SkfEncryptInit(hKey, IV, IV.length, paddingType, feedBitLen));
    }

    @Override
    public byte[] skfEncrypt(long hKey, byte[] data, long dataLen, long encryptedBufferLen) throws SarException {
        byte[] encryptedDataTmp = new byte[(int)dataLen];
        long[] encryptedLen = new long[1];
        encryptedLen[0] = encryptedBufferLen;
        checkResult(SkfEncrypt(hKey, data, dataLen, encryptedDataTmp, encryptedLen));
        byte[] encryptedData = new byte[(int)encryptedLen[0]];
        System.arraycopy(encryptedDataTmp, 0, encryptedData, 0, (int)encryptedLen[0]);
        return encryptedData;
    }

    @Override
    public void skfCloseHandle(long hHandle) throws SarException {
        checkResult(SkfCloseHandle(hHandle));
    }

    @Override
    public long skfImportSessionKey(long hContainer, long algId, byte[] wrappedData, long wrappedLen) throws SarException {

        long[] key = new long[1];
        checkResult(SkfImportSessionKey(hContainer, algId, wrappedData, wrappedLen, key));
        return key[0];
    }

    @Override
    public long skfDigestInit(long hDev, long algId, ECCPublicKeyBlob eccPublicKeyBlob, char[] pucID, long idLen) throws SarException {
        long[] hHash = new long[1];
        checkResult(SkfDigestInit(hDev, algId, eccPublicKeyBlob.getxCoordinate(), eccPublicKeyBlob.getyCoordinate(), pucID, idLen, hHash));
        return hHash[0];
    }

    @Override
    public byte[] skfDigest(long hHash, byte[] data, long dataLen, long hashDataBufferLen) throws SarException {
        byte[] hash = new byte[(int) hashDataBufferLen];
        long[] hashLen = new long[1];
        hashLen[0] = hashDataBufferLen;
        checkResult(SkfDigest(hHash, data, dataLen, hash, hashLen));
        byte[] hashData = new byte[(int)hashLen[0]];
        System.arraycopy(hash, 0, hashData, 0, (int)hashLen[0]);
        return hashData;
    }

    @Override
    public byte[] skfRSASignData(long hContainer, byte[] data, long signBufferLen) throws SarException {
        byte[] sign = new byte[(int) signBufferLen];
        long[] signLen = new long[1];
        signLen[0] = signBufferLen;
        checkResult(SkfRSASignData(hContainer, data, data.length, sign, signLen));
        byte[] signData = new byte[(int) signLen[0]];
        System.arraycopy(sign, 0, signData, 0, (int)signLen[0]);
        return signData;
    }

    @Override
    public byte[] skfECCSignData(long hContainer, byte[] data, long dataLen) throws SarException {
        byte[] r = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        byte[] s = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN];
        checkResult(SkfECCSignData(hContainer,
                data,
                dataLen,
                r,
                s));
        return Der.encode(r, s);
    }

    @Override
    public boolean skfRSAVerify(long hDev, RSAPublicKeyBlob rsaPublicKeyBlob, byte[] data, long dataLen,
                                 byte[] signature, long signLen) throws SarException {

        return checkVerify(SkfRSAVerify(hDev,
                rsaPublicKeyBlob.getAlgID(),
                rsaPublicKeyBlob.getBitLen(),
                rsaPublicKeyBlob.getModulus(),
                rsaPublicKeyBlob.getPublicExponent(),
                data,
                dataLen,
                signature,
                signLen));
    }

    @Override
    public boolean skfECCVerify(long hDev, ECCPublicKeyBlob eccPublicKeyBlob, byte[] data,
                                 long dataLen, byte[] signature) throws SarException {
        return checkVerify(SkfECCVerify(hDev,
                eccPublicKeyBlob.getxCoordinate(),
                eccPublicKeyBlob.getyCoordinate(),
                data,
                dataLen,
                Der.decode(signature, Der.R),
                Der.decode(signature, Der.S)));
    }

    @Override
    public void skfDecryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException {
        checkResult(SkfDecryptInit(hKey,
                blockCipherParam.getIV(),
                blockCipherParam.getIV().length,
                blockCipherParam.getPaddingType(),
                blockCipherParam.getFeedBitLen()));
    }

    @Override
    public byte[] skfDecrypt(long hKey, byte[] encryptedData, long encryptedLen, long dataBufferLen) throws
            SarException {
        byte[] decryptDataTmp = new byte[(int) dataBufferLen];
        long[] decryptLen = new long[1];
        decryptLen[0] = dataBufferLen;
        checkResult(SkfDecrypt(hKey, encryptedData, encryptedLen, decryptDataTmp, decryptLen));
        byte[] decryptData = new byte[(int)decryptLen[0]];
        System.arraycopy(decryptDataTmp, 0, decryptData, 0, (int)decryptLen[0]);
        return decryptData;
    }

    @Override
    public List<String> skfEnumDev(boolean present, long size) throws SarException {
        byte[] devNamesBytes = new byte[(int) size];
        checkResult(SkfEnumDev(present, devNamesBytes, size));
        return parseDevNameBytes(devNamesBytes);
    }

    private List<String> parseDevNameBytes(byte[] devNamesBytes) {
        String[] names=new String(devNamesBytes,0,devNamesBytes.length).split("\0");
        return new ArrayList<>(Arrays.asList(names));
    }

    @Override
    public void skfLockDev(long hDev, long timeOut) throws SarException {
        checkResult(SkfLockDev(hDev, timeOut));
    }

    @Override
    public long skfOpenApplication(long hDev, String name) throws SarException {
        byte[] nameBytes = name.getBytes();
        byte[] nameBytesWithZeroPadding = new byte[nameBytes.length + 1];
        System.arraycopy(nameBytes, 0, nameBytesWithZeroPadding, 0, nameBytes.length);

        long[] phApplication = new long[1];
        checkResult(SkfOpenApplication(hDev, nameBytesWithZeroPadding, phApplication));
        return phApplication[0];
    }

    @Override
    public void skfUnlockDev(long hDev) throws SarException {
        checkResult(SkfUnlockDev(hDev));
    }

    @Override
    public void skfDisconnectDev(long hDev) throws SarException {
        checkResult(SkfDisconnectDev(hDev));
    }

    @Override
    public void skfCloseContainer(long hContainer) throws SarException {
        checkResult(SkfCloseContainer(hContainer));
    }

    @Override
    public ECCCipherBlob skfExtECCEncrypt(long hDev, ECCPublicKeyBlob eccPublicKeyBlob, byte[] plainText, long plainTextLen) throws SarException {
        byte[] pubXCoordinate = eccPublicKeyBlob.getxCoordinate();
        byte[] pubYCoordinate = eccPublicKeyBlob.getyCoordinate();
        byte[] cipherXCoordinate = new byte[Constants.ECC_MAX_XCOORDINATE_BITS_LEN / 8];
        byte[] cipherYCoordinate = new byte[Constants.ECC_MAX_YCOORDINATE_BITS_LEN / 8];
        byte[] hash = new byte[32];
        long[] cipherLen = new long[1];
        byte[] cipher = new byte[1024];

        checkResult(SkfExtECCEncrypt(hDev,
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

    private static native int SkfConnectDev(byte[] name, long[] hDev);

    private static native int SkfGenRandom(long hDev, byte[] random, int randomLength);

    private static native int SkfGetDevState(byte[] name, long[] state);

    private static native int SkfVerifyPIN(long hApplication, long pinType, byte[] pin, long[] retryCount);

    private static native int SkfCreateContainer(long hApplication, byte[] containerName, long[] hContainer);

    private static native int SkfGenRSAKeyPair(long hContainer,
                                                  long bitsLen,
                                                  long[] algID,
                                                  long[] bitLen,
                                                  byte[] modulus,
                                                  byte[] publicExponent);

    private static native int SkfGenECCKeyPair(long hContainer, long algId, byte[] xCoordinate,
                                                  byte[] yCoordinate);

    private static native int SkfGetContainerType(long hContainer, long[] containerType);

    private static native int SkfECCExportSessionKey(long hContainer,
                                                        long algId,
                                                        byte[] pubXCoordinate,
                                                        byte[] pubYCoordinate,
                                                        byte[] cipherXCoordinate,
                                                        byte[] cipherYCoordinate,
                                                        byte[] Hash,
                                                        long[] cipherLen,
                                                        byte[] cipher,
                                                        long[] sessionKey);

    private static native int SkfImportECCKeyPair(long hContainer,
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

    private static native int SkfImportRSAKeyPair(long hContainer,
                                                     long symAlgId,
                                                     byte[] wrappedKey,
                                                     long wrappedKeyLen,
                                                     byte[] encryptedData,
                                                     long encryptedDataLen);

    private static native int SKE_CreateApplication(long hDev,
                                                      byte[] appName,
                                                      byte[] adminPin,
                                                      int adminPinRetryCount,
                                                      byte[] userPin,
                                                      int userPinRetryCount,
                                                      int createFileRights,
                                                      long[] hApplication);

    private static native int SkfExportPublicKey(long hContainer,
                                                    boolean signFlag,
                                                    int[] blobType,
                                                    long[] algID,
                                                    long[] bitLen,
                                                    byte[] modulus,
                                                    byte[] publicExponent,
                                                    byte[] xCoordinate,
                                                    byte[] yCoordinate,
                                                    long[] blobLen);

    private static native int SkfEncryptInit(long hKey,
                                                byte[] IV,
                                                long IVLen,
                                                long paddingType,
                                                long feedBitLen);

    private static native int SkfEncrypt(long hKey, byte[] data, long dataLen, byte[] encryptedData,
                                            long[] encryptedLen);

    private static native int SkfCloseHandle(long hHandle);

    private static native int SkfImportSessionKey(long hContainer,
                                                     long algId,
                                                     byte[] wrappedData,
                                                     long wrappedLen,
                                                     long[] hKey);

    private static native int SkfDigestInit(long hDev,
                                               long algId,
                                               byte[] xCoordinate,
                                               byte[] yCoordinate,
                                               char[] Id,
                                               long idLen,
                                               long[] hHash);

    private static native int SkfDigest(long hHash, byte[] data, long dataLen, byte[] hashData,
                                           long[] hashLen);

    private static native int SkfRSASignData(long hContainer, byte[] data, long dataLen, byte[] signature,
                                                long[] signLen);

    private static native int SkfECCSignData(long hContainer, byte[] data, long dataLen, byte[] r, byte[] s)
            ;

    private static native int SkfRSAVerify(long hDev,
                                              long algID,
                                              long bitLen,
                                              byte[] modulus,
                                              byte[] publicExponent,
                                              byte[] data,
                                              long dataLen,
                                              byte[] signature,
                                              long signLen);

    private static native int SkfECCVerify(long hDev,
                                              byte[] xCoordinate,
                                              byte[] yCoordinate,
                                              byte[] data,
                                              long dataLen,
                                              byte[] r,
                                              byte[] s);

    private static native int SkfDecryptInit(long hKey,
                                                byte[] IV,
                                                long IVLen,
                                                long paddingType,
                                                long feedBitLen);

    private static native int SkfDecrypt(long hKey, byte[] encryptedData, long encryptedDataLen, byte[] data,
                                            long[] dataLen);

    private static native int SkfEnumDev(boolean present, byte[] nameList, long size);

    private static native int SkfLockDev(long hDev, long timeOut);

    private static native int SkfOpenApplication(long hDev, byte[] appName, long[] hApplication);

    private static native int SkfUnlockDev(long hDev);

    private static native int SkfDisconnectDev(long hDev);

    private static native int SkfCloseContainer(long hContainer);

    private static native int SkfExtECCEncrypt(long hDev,
                                                  byte[] pubXCoordinate,
                                                  byte[] pubYCoordinate,
                                                  byte[] plainText,
                                                  long plainTextLen,
                                                  byte[] cipherXCoordinate,
                                                  byte[] cipherYCoordinate,
                                                  byte[] hash,
                                                  long[] cipherLen,
                                                  byte[] cipher);

    private static native int SkfEnumContainer(long hApplication, byte[] nameList, long size);

    private static native int SkfDELETECONTAINER(long hApplication, byte[] containerName);
}
