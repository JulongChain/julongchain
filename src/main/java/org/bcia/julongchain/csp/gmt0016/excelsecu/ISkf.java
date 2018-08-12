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
import org.bcia.julongchain.csp.gmt0016.excelsecu.security.ECCPublicKeyBlob;
import org.bcia.julongchain.csp.gmt0016.excelsecu.security.RSAPublicKeyBlob;

import java.util.List;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public interface ISkf {

    long SKF_ConnectDev(String name) throws SarException;

    byte[] SKF_GenRandom(long hDev, int length) throws SarException;

    long SKF_GetDevState(String name) throws SarException;

    long SKF_VerifyPIN(long hApplication, long pinType, String pin) throws SarException;

    long SKF_CreateContainer(long hApplication, String containerName) throws SarException;

    RSAPublicKeyBlob SKF_GenRSAKeyPair(long hContainer, long bitsLen) throws SarException;

    ECCPublicKeyBlob SKF_GenECCKeyPair(long hContainer, long algId) throws SarException;

    long SKF_GetContainerType(long hContainer) throws SarException;

    List<String> SKF_EnumContainer(long hApplication, long size) throws SarException;

    void SKF_DeleteContainer(long hApplication, String containerName) throws SarException;

    SessionKey SKF_ECCExportSessionKey(long hContainer, long algId, ECCPublicKeyBlob eccPublicKeyBlob) throws SarException;

    void SKF_ImportECCKeyPair(long hContainer, EnvelopedKeyBlob envelopedKeyBlob) throws SarException;

    void SKF_ImportRSAKeyPair(long hContainer, long symAlgId, byte[] wrappedKey, long wrappedKeyLen, byte[] encryptedData, long encryptedDataLen) throws SarException;

    long SKE_CreateApplication(long hDev, String appName, String adminPin, int adminPinRetryCount, String userPin, int userPinRetryCount, int createFileRights) throws SarException;

    PublicKeyBlob SKF_ExportPublicKey(long hContainer, boolean signFlag, long blobLen) throws SarException;

    void SKF_EncryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException;

    byte[] SKF_Encrypt(long hKey, byte[] data, long dataLen, long encryptedBufferLen) throws SarException;

    void SKF_CloseHandle(long hHandle) throws SarException;

    long SKF_ImportSessionKey(long hContainer, long algId, byte[] wrappedData, long wrappedLen) throws SarException;

    long SKF_DigestInit(long hDev, long algId, ECCPublicKeyBlob eccPublicKeyBlob, char[] pucID, long idLen) throws SarException;

    byte[] SKF_Digest(long hHash, byte[] data, long dataLen, long hashDataBufferLen) throws SarException;

    byte[] SKF_RSASignData(long hContainer, byte[] data, long signBufferLen) throws SarException;

    byte[] SKF_ECCSignData(long hContainer, byte[] data, long dataLen) throws SarException;

    boolean SKF_RSAVerify(long hDev, RSAPublicKeyBlob rsaPublicKey, byte[] data, long dataLen, byte[] signature, long signLen) throws SarException;

    boolean SKF_ECCVerify(long hDev, ECCPublicKeyBlob eccPublickKey, byte[] data, long dataLen, byte[] signature) throws SarException;

    void SKF_DecryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException;

    byte[] SKF_Decrypt(long hKey, byte[] encryptedData, long encryptedLen, long dataBufferLen) throws SarException;

    List<String> SKF_EnumDev(boolean present, long size) throws SarException;

    void SKF_LockDev(long hDev, long timeOut) throws SarException;

    long SKF_OpenApplication(long hDev, String name) throws SarException;

    void SKF_UnlockDev(long hDev) throws SarException;

    void SKF_DisconnectDev(long hDev) throws SarException;

    void SKF_CloseContainer(long hContainer) throws SarException;

    ECCCipherBlob SKF_ExtECCEncrypt(long hDev, ECCPublicKeyBlob eccPublicKeyBlob, byte[] plainText, long plainTextLen) throws SarException;
}
