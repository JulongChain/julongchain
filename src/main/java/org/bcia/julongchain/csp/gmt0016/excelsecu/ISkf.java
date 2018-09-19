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
 * GM0016 SKF 接口类
 *
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public interface ISkf {

    long skfConnectDev(String name) throws SarException;

    byte[] skfGenRandom(long hDev, int length) throws SarException;

    long skfGetDevState(String name) throws SarException;

    long skfVerifyPIN(long hApplication, long pinType, String pin) throws SarException;

    long skfCreateContainer(long hApplication, String containerName) throws SarException;

    RSAPublicKeyBlob skfGenRSAKeyPair(long hContainer, long bitsLen) throws SarException;

    ECCPublicKeyBlob skfGenECCKeyPair(long hContainer, long algId) throws SarException;

    long skfGetContainerType(long hContainer) throws SarException;

    List<String> skfEnumContainer(long hApplication, long size) throws SarException;

    void skfDeleteContainer(long hApplication, String containerName) throws SarException;

    SessionKey skfECCExportSessionKey(long hContainer, long algId, ECCPublicKeyBlob eccPublicKeyBlob) throws SarException;

    void skfImportECCKeyPair(long hContainer, EnvelopedKeyBlob envelopedKeyBlob) throws SarException;

    void skfImportRSAKeyPair(long hContainer, long symAlgId, byte[] wrappedKey, long wrappedKeyLen, byte[] encryptedData, long encryptedDataLen) throws SarException;

    long skfCreateApplication(long hDev, String appName, String adminPin, int adminPinRetryCount, String userPin, int userPinRetryCount, int createFileRights) throws SarException;

    PublicKeyBlob skfExportPublicKey(long hContainer, boolean signFlag, long blobLen) throws SarException;

    void skfEncryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException;

    byte[] skfEncrypt(long hKey, byte[] data, long dataLen, long encryptedBufferLen) throws SarException;

    void skfCloseHandle(long hHandle) throws SarException;

    long skfImportSessionKey(long hContainer, long algId, byte[] wrappedData, long wrappedLen) throws SarException;

    long skfDigestInit(long hDev, long algId, ECCPublicKeyBlob eccPublicKeyBlob, char[] pucID, long idLen) throws SarException;

    byte[] skfDigest(long hHash, byte[] data, long dataLen, long hashDataBufferLen) throws SarException;

    byte[] skfRSASignData(long hContainer, byte[] data, long signBufferLen) throws SarException;

    byte[] skfECCSignData(long hContainer, byte[] data, long dataLen) throws SarException;

    boolean skfRSAVerify(long hDev, RSAPublicKeyBlob rsaPublicKey, byte[] data, long dataLen, byte[] signature, long signLen) throws SarException;

    boolean skfECCVerify(long hDev, ECCPublicKeyBlob eccPublickKey, byte[] data, long dataLen, byte[] signature) throws SarException;

    void skfDecryptInit(long hKey, BlockCipherParam blockCipherParam) throws SarException;

    byte[] skfDecrypt(long hKey, byte[] encryptedData, long encryptedLen, long dataBufferLen) throws SarException;

    List<String> skfEnumDev(boolean present, long size) throws SarException;

    void skfLockDev(long hDev, long timeOut) throws SarException;

    long skfOpenApplication(long hDev, String name) throws SarException;

    void skfUnlockDev(long hDev) throws SarException;

    void skfDisconnectDev(long hDev) throws SarException;

    void skfCloseContainer(long hContainer) throws SarException;

    ECCCipherBlob skfExtECCEncrypt(long hDev, ECCPublicKeyBlob eccPublicKeyBlob, byte[] plainText, long plainTextLen) throws SarException;
}
