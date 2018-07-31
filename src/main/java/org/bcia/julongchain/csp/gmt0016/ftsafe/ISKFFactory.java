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
package org.bcia.julongchain.csp.gmt0016.ftsafe;

import org.bcia.julongchain.common.exception.JCSKFException;
import org.bcia.julongchain.common.exception.SarException;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCCipherBlob;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCEnvelopedKeyBlob;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.BlockCipherParam;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.SKFCspKey;

import java.util.List;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public interface ISKFFactory{

    void InitSKF(String lib);

    List<String> SKF_EnumDevs(boolean bpresent) throws SarException, JCSKFException;

    long SKF_ConnectDev(String name) throws SarException;

    SKFDeviceInfo SKF_GetDevInfo(long lDevHandle) throws SarException;

    List<String> SKF_EnumApplication(long lDevHandle) throws SarException, JCSKFException;

    long SKF_OpenApplication(long hDev, String name) throws SarException;

    //long SKF_CreateApplication(long lDevHandle, byte[] appNameByte, byte[] adminPinByte, long adminPinRetryCount,
    //		byte[] userPinByte, long userPinRetryCount, long createFileRights) throws SarException;

    void SKF_DisconnectDev(long lDevHandle) throws SarException;


    long SKF_VerifyPIN(long hApplication, long pinType, String pin) throws SarException;

    List<String> SKF_EnumContainer(long lAppHandle) throws SarException, JCSKFException;

    long SKF_CreateContainer(long lAppHandle, String sContainerName) throws SarException;

    long SKF_OpenContainer(long lAppHandle, String sContainerName) throws SarException;

    SKFCspKey.ECCPublicKeyBlob  SKF_GenECCKeyPair(long lContainer) throws SarException;

    long SKF_GetContainerType(long lContainerHandle) throws SarException;

    Object SKF_ExportPublicKey(long lContainerHandle, boolean bSignFlag, boolean bECC) throws SarException;

    byte[] SKF_GenRandom(long lDevHandle, int length) throws SarException;

    long SKF_SetSymmKey(long lDevHandle, byte[] byteKey, long lAlgID) throws SarException;

    void SKF_EncryptInit(long lKeyHandle, BlockCipherParam blockCipherParam) throws SarException;

    byte[] SKF_Encrypt(long lKeyHandle, byte[] data, long dataLen) throws SarException;

    ECCCipherBlob SKF_ExtECCEncrypt(long hDev, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob, byte[] plainText, long plainTextLen) throws SarException;

    void SKF_ImportECCKeyPair(long hContainer, ECCEnvelopedKeyBlob eccEnvelopedKeyBlob) throws SarException;

    long SKF_DigestInit(long lDevHandle, long algId, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob, String sPucID) throws SarException;

    byte[] SKF_Digest(long hHash, byte[] msg, long dataLen) throws SarException;

    byte[] SKF_ECCSignData(long hContainer, byte[] digest, long dataLen) throws SarException;

    boolean SKF_ECCVerify(long lDevHandle, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob,byte[] digest,
                          long digestLen, byte[] signature) throws SarException;

    byte[] SKF_ExtECCDecrypt(long lDevHandle, SKFCspKey.ECCPrivateKeyBlob eccPrivateKeyBlob, ECCCipherBlob eccCipherBlob) throws SarException;

    SKFCspKey.RSAPublicKeyBlob SKF_GenRSAKeyPair(long lContainerHandle, long lBits) throws SarException;

    long SKF_RSAExportSessionKey(long lContainerHandle, long lAlgID, SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob,
                                 byte[] data, long[] lDataLen) throws SarException;

    void SKF_ImportRSAKeyPair(long lContainerHandle, long symAlgId, byte[] wrappedKey, long wrappedKeyLen,
                              byte[] encryptedData, long encryptedDataLen) throws SarException;

    byte[] SKF_RSASignData(long lContainerHandle, byte[] data, long signatureLen) throws SarException;

    boolean SKF_RSAVerify(long lDevHandle, SKFCspKey.RSAPublicKeyBlob rsaPublicKey, byte[] data,
                          byte[] signature) throws SarException;

    byte[] SKF_ExtRSAPubKeyOperation(long lDevHandle, SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob, byte[] pbInput,
                                     long ulInputLen) throws SarException;

    void SKF_DecryptInit(long lKeyHandle, BlockCipherParam blockCipherParam) throws SarException;

    byte[] SKF_Decrypt(long lKeyHandle, byte[] cipherData, long cipherDataLen) throws SarException;

    void SKF_CloseContainer(long hContainer) throws SarException;

    void SKF_CloseHandle(long hHandle) throws SarException;
/*
    long SKF_GetDevState(String name) throws SarException;

    void SKF_DeleteContainer(long hApplication, String containerName) throws SarException;

    SessionKey SKF_ECCExportSessionKey(long hContainer, long algId, ECCPublicKeyBlob eccPublicKeyBlob) throws SarException;

    long SKF_ImportSessionKey(long hContainer, long algId, byte[] wrappedData, long wrappedLen) throws SarException;

*/

/*
    void SKF_LockDev(long hDev, long timeOut) throws SarException;


    void SKF_UnlockDev(long hDev) throws SarException;

    void SKF_CloseContainer(long hContainer) throws SarException;


*/
}

