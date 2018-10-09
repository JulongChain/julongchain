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

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * 调用接口定义
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public interface GMT0016Dll extends Library{

    public static GMT0016Dll getInstance(String library) {
        GMT0016Dll instanceDll = (GMT0016Dll)Native.loadLibrary(library, GMT0016Dll.class);
        return instanceDll;
    }

    //public GMT0016Dll instanceDll = (GMT0016Dll)Native.loadLibrary("/root/Desktop/libes_3000gm.so", GMT0016Dll.class);

    public long SKF_EnumDev_N(boolean bPresent, byte[] devNameList, long[] lSize);

    public long SKF_ConnectDev_N(byte[] devName, long[] hdevHandle);

    public long SKF_GetDevInfo_N(long devHandle, SKFDeviceInfo[] devinfo);

    public long SKF_GetDevInfo_N(long devHandle, byte[] Version, byte[] Manufacturer, byte[] Issuer, byte[] Label, byte[] SerialNumber,
                                 byte[] HWVersion, byte[] FirmwareVersion, long[] AlgSymCap, long[] AlgAsymCap,
                                 long[] AlgHashCap, long[] DevAuthAlgId, long[] TotalSpace, long[] FreeSpace,
                                 long[] MaxECCBufferSize, long[] MaxBufferSize, byte[] Reserved);

    public long SKF_EnumApplication_N(long devHandle, byte[] appNameList, long[] lSize);

    public long SKF_OpenApplication_N(long lDevHandle, byte[] appNameByte, long[] hAppHandle);

    public long SKF_VerifyPIN_N(long lAppHandle, long lUserType, byte[] pinBytes, long[] lPinRetryCount);

    public long SKF_EnumContainer_N(long lAppHandle, byte[] containerNameList, long[] lSize);

    public long SKF_OpenContainer_N(long lAppHandle, byte[] containerNameBytes, long[] lContainerHandle);

    public long SKF_CreateContainer_N(long lAppHandle, byte[] containerNameBytes, long[] lContainerHandle);

    public long SKF_GetContainerType_N(long lContainerHandle, long[] lType);

    public long SKF_GenRSAKeyPair_N(long lContainerHandle, long lBits, long[] algID, long[] bitlen, byte[] modulus, byte[] publicExponent);

    public long SKF_ExportPublicKey_N(long lContainerHandle, boolean bSignFlag, int[] blobType, long[] algID, long[] bitLen,
                                      byte[] modulus, byte[] publicExponent, byte[] xCoordinate, byte[] yCoordinate, long[] blobLen);

    public long SKF_RSAExportSessionKey_N(long lContainerHandle, long lAlgID, long algID, long bitLen,
                                          byte[] modulus, byte[] publicExponent, byte[] data, long[] lDataLen, long[] lSessionHandle);

    public long SKF_EncryptInit_N(long lKeyHandle, byte[] IV, long IVLen, long paddingType, long feedBitLen);

    public long SKF_Encrypt_N(long lKeyHandle, byte[] data, long lDataLen, byte[] encryptedData, long[] lEncryptedLen);

    public long SKF_DecryptInit_N(long lKeyHandle, byte[] IV, long IVLen, long paddingType, long feedBitLen);

    public long SKF_Decrypt_N(long lKeyHandle, byte[] cipherdata, long lCipherLen, byte[] data, long[] lDataLen);

    public long SKF_ImportRSAKeyPair_N(long lContainerHandle, long lAlgId, byte[] wrappedKey, long wrappedKeyLen,
                                       byte[] encryptedData, long encryptedDataLen);

    public long SKF_ExtRSAPubKeyOperation_N(long devHandle, long algID, long bitlen, byte[] modulus, byte[] publicExponent,
                                            byte[] input, long linputLen, byte[] output, long[] loutputLen);

    public long SKF_GenRandom_N(long lDevHandle, byte[] randomBytes, long lRandomLen);

    public long SKF_SetSymmKey_N(long lDevHandle, byte[] byteKey, long lAlgID, long[] lSessionHandle);

    public long SKF_GenECCKeyPair_N(long lContainerHandle, long lAlgID, byte[] xCoordinate, byte[] yCoordinate);

    public long SKF_DigestInit_N(long lDevHandle, long algId, long bitLen, byte[] xCoordinate, byte[] yCoordinate, char[] Id,
                                 long idLen, long[] lHashHandle);

    public long SKF_Digest_N(long hHashHandle, byte[] data, long dataLen, byte[] hashData, long[] hashLen);

    public long SKF_RSASignData_N(long lContainerHandle, byte[] data, long dataLen, byte[] signature, long[] signLen);

    public long SKF_RSAVerify_N(long lDevHandle, long algID, long bitLen,byte[] modulus, byte[] publicExponent, byte[] data,
                                long dataLen, byte[] signerature, long signeratureLen);

    public long SKF_ECCSignData_N(long lContainerHandle, byte[] data, long dataLen, byte[] r, byte[] s);

    public long SKF_ECCVerify_N(long lDevHandle, long bitLen, byte[] xCoordinate, byte[] yCoordinate, byte[] data,
                                long dataLen, byte[] r, byte[] s);

    public long SKF_ExtECCEncrypt_N (long hDev, long bitLen, byte[] pubXCoordinate, byte[] pubYCoordinate,
                                     byte[] pbPlainText, long ulPlainTextLen, byte[] cipherXCoordinate, byte[] cipherYCoordinate,
                                     byte[] hash, long[] cipherLen, byte[] cipher);

    public long SKF_ImportECCKeyPair_N(long hContainer, long version, long symmAlgID, long bits,
                                       byte[] encryptedPriKey, long pubbitLen, byte[] pubXCoordinate, byte[] pubYCoordinate,
                                       byte[] cipherXCoordinate, byte[] cipherYCoordinate, byte[] Hash,
                                       long cipherLen, byte[] cipher);

    public long SKF_DisConnectDev_N(long lDevHandle);

    public long SKF_CloseContainer_N(long hContainer);

    public long SKF_CloseHandle_N(long lHandle);
}