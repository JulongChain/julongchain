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
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCDer;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCEnvelopedKeyBlob;
import org.bcia.julongchain.csp.gmt0016.ftsafe.ec.ECCSignatureBlob;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.BlockCipherParam;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.DataUtil;
import org.bcia.julongchain.csp.gmt0016.ftsafe.util.SKFCspKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * SKFFactory接口实现
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public class SKFFactoryOpts implements ISKFFactory {

    private GMT0016Dll gmtDll;
    private static final int DEV_MININUM = 2;
    private static final long INVALID_HANDLE = 0;

    public void InitSKF(String lib) {
        gmtDll = GMT0016Dll.getInstance(lib);
    }


    /**
     * 枚举设备
     * @param bPresent          设备状态
     * @return  设备名称列表
     * @throws SarException     错误码
     * @throws JCSKFException   错误码
     */
    public List<String> SKF_EnumDevs(boolean bPresent) throws SarException, JCSKFException {

        byte[] devNamesByte = null;
        long[] lSize = new long[1];

        SKF_CatchExcetpt(gmtDll.SKF_EnumDev_N(bPresent, null, lSize));
        if(lSize[0] < DEV_MININUM) {//default exist 0x00
            SKF_CatchJCExcetpt(JCSKFException.JC_SKF_NODEV);
        }

        devNamesByte = new byte[(int)lSize[0]];
        SKF_CatchExcetpt(gmtDll.SKF_EnumDev_N(bPresent, devNamesByte, lSize));

        String[] devNames=new String(devNamesByte,0,devNamesByte.length).split("\0");
        return new ArrayList<String>(Arrays.asList(devNames));
    }


    /**
     * 连接设备
     * @param devName   设备名称
     * @return  设备句柄
     * @throws SarException 错误码
     */
    public long SKF_ConnectDev(String devName) throws SarException{
        byte[] devNameBytes = devName.getBytes();
        byte[] tempdevNameBytes = new byte[devNameBytes.length + 1];
        System.arraycopy(devNameBytes, 0, tempdevNameBytes, 0, devNameBytes.length);

        long[] lDevHandle = {0};
        SKF_CatchExcetpt(gmtDll.SKF_ConnectDev_N(tempdevNameBytes, lDevHandle));
        return lDevHandle[0];
    }


    /**
     * 获取设备信息
     * @param lDevHandle    设备句柄
     * @return  设备信息
     * @throws SarException 错误码
     */
    public SKFDeviceInfo SKF_GetDevInfo(long lDevHandle) throws SarException{
        SKF_CheckHandler(lDevHandle);
        byte[] skf_ver = new byte[2];
        byte[] skf_hwver = new byte[2];
        byte[] skf_fwver = new byte[2];
        byte[] manufacturer = new byte[64];
        byte[] issuer = new byte[64];
        byte[] label = new byte[32];
        byte[] serialnumber = new byte[32];
        byte[] reserved = new byte[64];
        long[] algsyscap = new long[1];
        long[] algasymcap = new long[1];
        long[] alghashcap = new long[1];
        long[] devauthalgid = new long[1];;
        long[] totalspace = new long[1];
        long[] freespace = new long[1];
        long[] maxeccbuffersize = new long[1];
        long[] maxbuffersize = new long[1];

        SKF_CatchExcetpt(gmtDll.SKF_GetDevInfo_N(lDevHandle, skf_ver,
                manufacturer, issuer, label, serialnumber,skf_hwver, skf_fwver, algsyscap,
                algasymcap, alghashcap, devauthalgid, totalspace, freespace,
                maxeccbuffersize, maxbuffersize, reserved));

        SKFDeviceInfo devinfo = new SKFDeviceInfo(skf_ver, manufacturer, issuer,
                label, serialnumber, skf_hwver, skf_fwver, algsyscap[0], algasymcap[0], alghashcap[0],
                devauthalgid[0], totalspace[0], freespace[0], maxeccbuffersize[0], maxbuffersize[0], reserved);

        return devinfo;
    }


    /**
     * 枚举应用
     * @param lDevHandle        设备句柄
     * @return 应用名称列表
     * @throws SarException     错误码
     * @throws JCSKFException   错误码
     */
    public List<String> SKF_EnumApplication(long lDevHandle) throws SarException, JCSKFException{
        SKF_CheckHandler(lDevHandle);


        long[] lSize = new long[1];

        SKF_CatchExcetpt(gmtDll.SKF_EnumApplication_N(lDevHandle, null, lSize));
        if(lSize[0] < DEV_MININUM) {//default exist 0x00
            SKF_CatchJCExcetpt(JCSKFException.JC_SKF_NOAPP);
        }
        byte[] appNameByte = new byte[(int)lSize[0]];
        SKF_CatchExcetpt(gmtDll.SKF_EnumApplication_N(lDevHandle, appNameByte, lSize));
        String[] appNames=new String(appNameByte,0,appNameByte.length).split("\0");
        return new ArrayList<String>(Arrays.asList(appNames));
    }


    /**
     * 打开应用
     * @param lDevHandle    设备句柄
     * @param appName       应用名称
     * @return  应用句柄
     * @throws SarException 错误码
     */
    public long SKF_OpenApplication(long lDevHandle, String appName) throws SarException {
        SKF_CheckHandler(lDevHandle);
        byte[] appNameBytes = appName.getBytes();
        byte[] tempappNameBytes = new byte[appNameBytes.length + 1];
        System.arraycopy(appNameBytes, 0, tempappNameBytes, 0, appNameBytes.length);

        long[] lAppHandle = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_OpenApplication_N(lDevHandle, tempappNameBytes, lAppHandle));
        return lAppHandle[0];
    }


    /**
     * 断开设备
     * @param lDevHandle    设备句柄
     * @throws SarException 错误码
     */
    public void SKF_DisconnectDev(long lDevHandle) throws SarException{
        SKF_CheckHandler(lDevHandle);
        SKF_CatchExcetpt(gmtDll.SKF_DisConnectDev_N(lDevHandle));
    }


    /**
     * 校验PIN码
     * @param lAppHandle        应用句柄
     * @param lUserType         用户类型
     * @param sPin              用户PIN码
     * @return Success          成功
     * @throws SarException     错误码
     */
    public long SKF_VerifyPIN(long lAppHandle, long lUserType, String sPin) throws SarException{
        SKF_CheckHandler(lAppHandle);
        byte[] pinBytes = sPin.getBytes();
        byte[] temppinBytes = new byte[pinBytes.length];
        System.arraycopy(pinBytes, 0, temppinBytes, 0, pinBytes.length);

        long[] lPinRetryCount = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_VerifyPIN_N(lAppHandle, lUserType, temppinBytes, lPinRetryCount));
        //return lPinRetryCount[0]; //if need the retrycount, throw exception need add judge
        return 0;
    }


    /**
     * 枚举容器
     * @param lAppHandle        应用句柄
     * @return  容器名称列表
     * @throws SarException     错误码
     * @throws JCSKFException   错误码
     */
    public List<String> SKF_EnumContainer(long lAppHandle) throws SarException, JCSKFException{
        SKF_CheckHandler(lAppHandle);

        byte[] ContainerNameByte = {0};
        long[] lSize = new long[1];

        SKF_CatchExcetpt(gmtDll.SKF_EnumContainer_N(lAppHandle, null, lSize));
        if(lSize[0] < DEV_MININUM) {//exist default 0x00
            //SKF_CatchJCExcetpt(JCSKFException.JC_SKF_NOCONTAINER);
            return null;
        }

        ContainerNameByte = new byte[(int)lSize[0]];
        SKF_CatchExcetpt(gmtDll.SKF_EnumContainer_N(lAppHandle, ContainerNameByte, lSize));
        String[] appNames=new String(ContainerNameByte,0,(int)lSize[0]).split("\0");
        return new ArrayList<String>(Arrays.asList(appNames));
    }


    /**
     * 创建容器
     * @param lAppHandle        应用句柄
     * @param sContainerName    容器名称
     * @return  容器句柄
     * @throws SarException     错误码
     */
    public long SKF_CreateContainer(long lAppHandle, String sContainerName) throws SarException{
        SKF_CheckHandler(lAppHandle);

        byte[] ContainerNameBytes = sContainerName.getBytes();
        byte[] tempContainerNameBytes = new byte[ContainerNameBytes.length + 1];
        System.arraycopy(ContainerNameBytes, 0, tempContainerNameBytes, 0, ContainerNameBytes.length);

        long[] lContainerHandle = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_CreateContainer_N(lAppHandle, /*ContainerNameBytes*/tempContainerNameBytes, lContainerHandle));
        return lContainerHandle[0];
    }


    /**
     * 打开容器
     * @param lAppHandle        应用句柄
     * @param sContainerName    容器名称
     * @return  Container Handle	容器句柄
     * @throws SarException     错误码
     */
    public long SKF_OpenContainer(long lAppHandle, String sContainerName) throws SarException {
        SKF_CheckHandler(lAppHandle);

        byte[] ContainerNameBytes = sContainerName.getBytes();
        byte[] tempContainerNameBytes = new byte[ContainerNameBytes.length + 1];
        System.arraycopy(ContainerNameBytes, 0, tempContainerNameBytes, 0, ContainerNameBytes.length);

        long[] lContainerHandle = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_OpenContainer_N(lAppHandle, tempContainerNameBytes, lContainerHandle));
        return lContainerHandle[0];
    }


    /**
     * 关闭容器句柄
     * @param lContainerHandle  容器句柄
     * @throws SarException     错误码
     */
    public void SKF_CloseContainer(long lContainerHandle) throws SarException {
        SKF_CatchExcetpt(gmtDll.SKF_CloseContainer_N(lContainerHandle));
    }


    /**
     * 关闭句柄
     * @param lHandle       句柄
     * @throws SarException 错误码
     */
    public void SKF_CloseHandle(long lHandle) throws SarException {
        SKF_CatchExcetpt(gmtDll.SKF_CloseHandle_N(lHandle));
    }


    /**
     * 生成SM2密钥对
     * @param lContainerHandle      容器句柄
     * @return  SM2的密钥公钥
     * @throws SarException         错误码
     */
    public SKFCspKey.ECCPublicKeyBlob SKF_GenECCKeyPair(long lContainerHandle) throws SarException {
        SKF_CheckHandler(lContainerHandle);
        byte[] xCoordinate = new byte[(int)(ECC_MAX_XCOORDINATE_BITS_LEN)/8];
        byte[] yCoordinate = new byte[(int)(ECC_MAX_YCOORDINATE_BITS_LEN)/8];
        SKF_CatchExcetpt(gmtDll.SKF_GenECCKeyPair_N(lContainerHandle, SGD_SM2_1, xCoordinate, yCoordinate));
        int xlen = DataUtil.getVirtualValueLength(xCoordinate);
        int ylen = DataUtil.getVirtualValueLength(yCoordinate);
        byte[] x = new byte[xlen];
        byte[] y = new byte[ylen];
        System.arraycopy(xCoordinate, 0, x, 0, xlen);
        System.arraycopy(yCoordinate, 0, y, 0, ylen);
        SKFCspKey.ECCPublicKeyBlob eccPubBlob = new SKFCspKey.ECCPublicKeyBlob(x, y, 256);
        return eccPubBlob;
    }


    /**
     * 获取容器类型
     * @param lContainerHandle      容器句柄
     * @return 类型(RSA or SM2)
     * @throws SarException         错误码
     */
    public long SKF_GetContainerType(long lContainerHandle) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lType = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_GetContainerType_N(lContainerHandle, lType));
        return lType[0];
    }


    /**
     * 导出公钥
     * @param lContainerHandle      容器句柄
     * @param bSignFlag             密钥类型 (True: Sign  False: Encrypt)
     * @param bECC                  是否为SM2密钥 (True: SM2 False: RSA)
     * @return SM2 或 RSA 公钥
     * @throws SarException         错误码
     */
    public Object SKF_ExportPublicKey(long lContainerHandle, boolean bSignFlag, boolean bECC) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lBlobLen = new long[1];
        long[] lAlgID = new long[1];
        long[] lBitLen = new long[1];
        int[] iType = new int[1];
        byte[] modulus = new byte[(int)MAX_RSA_MODULUS_LEN];
        byte[] publicExponent = new byte[(int)MAX_RSA_EXPONENT_LEN];
        byte[] xCoordinate = new byte[(int)(ECC_MAX_XCOORDINATE_BITS_LEN)/8];
        byte[] yCoordinate = new byte[(int)(ECC_MAX_YCOORDINATE_BITS_LEN)/8];
        if(bECC) {
            SKF_CatchExcetpt(gmtDll.SKF_ExportPublicKey_N(lContainerHandle, bSignFlag, iType, lAlgID, lBitLen,
                    modulus, publicExponent, xCoordinate, yCoordinate, lBlobLen));
            int xlen = DataUtil.getVirtualValueLength(xCoordinate);
            int ylen = DataUtil.getVirtualValueLength(yCoordinate);
            byte x[] = new byte[xlen];
            byte y[] = new byte[ylen];
            System.arraycopy(xCoordinate, 0, x, 0, xlen);
            System.arraycopy(yCoordinate, 0, y, 0, ylen);
            SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob = new SKFCspKey.ECCPublicKeyBlob(x, y, 256);
            return eccPublicKeyBlob;
        }else {
            SKF_CatchExcetpt(gmtDll.SKF_ExportPublicKey_N(lContainerHandle, bSignFlag, iType, lAlgID, lBitLen,
                    modulus, publicExponent, xCoordinate, yCoordinate, lBlobLen));
            if(iType[0] == 1)
            {
                byte[] pubmodulus = new byte[(int)lBitLen[0]/8];
                System.arraycopy(modulus, 0, pubmodulus, 0, (int)lBitLen[0]/8);
                SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob =
                        new  SKFCspKey.RSAPublicKeyBlob(lAlgID[0], lBitLen[0], pubmodulus, publicExponent);
                return rsaPublicKeyBlob;
            }else{
                return null;
            }
        }
    }

    /**
     * 生成随机数
     * @param lDevHandle        设备句柄
     * @param length            随机数长度
     * @return  随机数数组
     * @throws SarException     错误码
     */
    public byte[] SKF_GenRandom(long lDevHandle, int length) throws SarException {
        SKF_CheckHandler(lDevHandle);
        byte[] randomData = new byte[length];
        SKF_CatchExcetpt(gmtDll.SKF_GenRandom_N(lDevHandle, randomData, length));
        return randomData;
    }

    /**
     * 生成对称密钥
     * @param lDevHandle        设备句柄
     * @param byteKey           对称密钥 (Random numbers)
     * @param lAlgID            算法ID
     * @return Key Handle   密钥句柄
     * @throws SarException     错误码
     */
    public long SKF_SetSymmKey(long lDevHandle, byte[] byteKey, long lAlgID) throws SarException{
        SKF_CheckHandler(lDevHandle);
        long[] lSessionHandle = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_SetSymmKey_N(lDevHandle, byteKey, lAlgID, lSessionHandle));
        return lSessionHandle[0];
    }

    /**
     * 初始化加密操作
     * @param lKeyHandle            用于加密的密钥句柄
     * @param blockCipherParam      分组密码算法相关参数
     * @throws SarException         错误码
     */
    public void SKF_EncryptInit(long lKeyHandle, BlockCipherParam blockCipherParam) throws SarException {
        SKF_CheckHandler(lKeyHandle);
        SKF_CatchExcetpt(gmtDll.SKF_EncryptInit_N(lKeyHandle, blockCipherParam.getIV(),
                blockCipherParam.getIVLen(), blockCipherParam.getPaddingType(), blockCipherParam.getFeedBitLen()));
    }


    /**
     * 加密操作
     * @param lKeyHandle        用于加密的密钥句柄
     * @param data              原文数据
     * @param dataLen           原文数据长度
     * @return  Ciphertext data 密文数据
     * @throws SarException     错误码
     */
    public byte[] SKF_Encrypt(long lKeyHandle, byte[] data, long dataLen) throws SarException {
        SKF_CheckHandler(lKeyHandle);
        long[] lEncrytedDataLen = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_Encrypt_N(lKeyHandle, data, dataLen, null, lEncrytedDataLen));
        byte[] encrytedData = new byte[(int)lEncrytedDataLen[0]];
        SKF_CatchExcetpt(gmtDll.SKF_Encrypt_N(lKeyHandle, data, dataLen, encrytedData, lEncrytedDataLen));
        return encrytedData;
    }

    /**
     * 初始化解密操作
     * @param lKeyHandle        用于解密的密钥句柄
     * @param blockCipherParam  分组密码算法相关参数
     * @throws SarException     错误码
     */
    public void SKF_DecryptInit(long lKeyHandle, BlockCipherParam blockCipherParam) throws SarException {
        SKF_CheckHandler(lKeyHandle);
        SKF_CatchExcetpt(gmtDll.SKF_DecryptInit_N(lKeyHandle, blockCipherParam.getIV(),
                blockCipherParam.getIVLen(), blockCipherParam.getPaddingType(), blockCipherParam.getFeedBitLen()));
    }

    /**
     * 解密操作
     * @param lKeyHandle    用于解密的密钥句柄
     * @param cipherData    密文数据
     * @param cipherDataLen 密文数据长度
     * @return  原文数据
     * @throws SarException 错误码
     */
    public byte[] SKF_Decrypt(long lKeyHandle, byte[] cipherData, long cipherDataLen) throws SarException {
        SKF_CheckHandler(lKeyHandle);
        long[] lDecrytedDataLen = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_Decrypt_N(lKeyHandle, cipherData, cipherDataLen, null, lDecrytedDataLen));
        byte[] tempData = new byte[(int)lDecrytedDataLen[0]];
        SKF_CatchExcetpt(gmtDll.SKF_Decrypt_N(lKeyHandle, cipherData, cipherDataLen, tempData, lDecrytedDataLen));
        byte[] decrytedData = new byte[(int)lDecrytedDataLen[0]];
        System.arraycopy(tempData, 0, decrytedData, 0, (int)lDecrytedDataLen[0]);
        return decrytedData;
    }


    /**
     * ECC外来公钥加密
     * @param lDevHandle        设备句柄
     * @param eccPublicKeyBlob  ECC公钥数据结构
     * @param plainText         原文数据
     * @param plainTextLen      原文数据长度
     * @return 密文数据
     * @throws SarException     错误码
     */
    public ECCCipherBlob SKF_ExtECCEncrypt(long lDevHandle, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob,
                                           byte[] plainText, long plainTextLen) throws SarException {

        byte[] hash = new byte[32];
        long[] cipherLen = new long[1];
        byte[] cipher = new byte[(int)plainTextLen];
        byte[] cipherXCoordinate = new byte[32];
        byte[] cipherYCoordinate = new byte[32];
        SKF_CatchExcetpt(gmtDll.SKF_ExtECCEncrypt_N(lDevHandle, eccPublicKeyBlob.getBit(), eccPublicKeyBlob.getxCoordinate(),
                eccPublicKeyBlob.getyCoordinate(),plainText, plainTextLen, cipherXCoordinate, cipherYCoordinate,
                hash, cipherLen, cipher));
        ECCCipherBlob eccCipherBlob = new ECCCipherBlob(cipherXCoordinate, cipherYCoordinate, hash, cipherLen[0], cipher);
        return eccCipherBlob;
    }


    /**
     * 导入ECC加密密钥对
     * @param lContainer                容器句柄
     * @param eccEnvelopedKeyBlob       受保护的加密密钥对
     * @throws SarException             错误码
     */
    public void SKF_ImportECCKeyPair(long lContainer, ECCEnvelopedKeyBlob eccEnvelopedKeyBlob) throws SarException{
        SKF_CheckHandler(lContainer);
        SKF_CatchExcetpt(gmtDll.SKF_ImportECCKeyPair_N(lContainer, eccEnvelopedKeyBlob.getVersion(), eccEnvelopedKeyBlob.getSymmAlgID(),
                eccEnvelopedKeyBlob.getBits(), eccEnvelopedKeyBlob.getEncryptedPriKey(), eccEnvelopedKeyBlob.getEccPublicKeyBlob().getBit(),
                eccEnvelopedKeyBlob.getEccPublicKeyBlob().getxCoordinate(), eccEnvelopedKeyBlob.getEccPublicKeyBlob().getyCoordinate(),
                eccEnvelopedKeyBlob.getEccCipherBlob().getXCoordinate(), eccEnvelopedKeyBlob.getEccCipherBlob().getYCoordinate(),
                eccEnvelopedKeyBlob.getEccCipherBlob().getHash(), eccEnvelopedKeyBlob.getEccCipherBlob().getCipherLen(),
                eccEnvelopedKeyBlob.getEccCipherBlob().getCipher()));
        return;
    }


    /**
     * 密码杂凑初始化
     * @param lDevHandle            设备句柄
     * @param algId                 密码杂凑算法标识
     * @param eccPublicKeyBlob      签名者公钥。当算法标识为SGD_SM3时有效。
     * @param sPucID                签名者ID
     * @return  密码杂凑对象句柄
     * @throws SarException         错误码
     */
    public long SKF_DigestInit(long lDevHandle, long algId, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob, String sPucID) throws SarException{
        SKF_CheckHandler(lDevHandle);

        long[] lHashHandle = new long[1];
        if(eccPublicKeyBlob == null)
        {
            SKF_CatchExcetpt(gmtDll.SKF_DigestInit_N(lDevHandle, algId, 0L, null,
                    null, null, 0L, lHashHandle));
        }
        else {
            char[] charArray = sPucID.toCharArray();
            SKF_CatchExcetpt(gmtDll.SKF_DigestInit_N(lDevHandle, algId, eccPublicKeyBlob.getBit(), eccPublicKeyBlob.getxCoordinate(),
                    eccPublicKeyBlob.getyCoordinate(), charArray, charArray.length, lHashHandle));
        }
        return lHashHandle[0];
    }

    /**
     * 密码杂凑
     * @param lHashHandle       密码杂凑对象句柄
     * @param msg               消息数据
     * @param dataLen           消息数据长度
     * @return Hash Data    密码杂凑数据
     * @throws SarException 错误码
     */
    public byte[] SKF_Digest(long lHashHandle, byte[] msg, long dataLen) throws SarException {
        SKF_CheckHandler(lHashHandle);
        long[] lHashLen = new long[1];
        lHashLen[0] = 0L;
        SKF_CatchExcetpt(gmtDll.SKF_Digest_N(lHashHandle, msg, dataLen, null, lHashLen));
        byte[] byteHash = new byte[(int)lHashLen[0]];
        SKF_CatchExcetpt(gmtDll.SKF_Digest_N(lHashHandle, msg, dataLen, byteHash, lHashLen));
        return byteHash;

    }

    /**
     * ECC 签名
     * @param lContainer    容器句柄
     * @param digest        待签数据
     * @param digestLen     待签数据长度
     * @return  Signature   签名值
     * @throws SarException 错误码
     */
    public byte[] SKF_ECCSignData(long lContainer, byte[] digest, long digestLen) throws SarException {
        byte[] r = new byte[(int)ECC_MAX_XCOORDINATE_BITS_LEN/8];
        byte[] s = new byte[(int)ECC_MAX_YCOORDINATE_BITS_LEN/8];
        SKF_CatchExcetpt(gmtDll.SKF_ECCSignData_N(lContainer, digest, digestLen, r, s));
        int rlen = DataUtil.getVirtualValueLength(r);
        int slen = DataUtil.getVirtualValueLength(s);
        byte[] br = new byte[rlen];
        byte[] bs = new byte[slen];
        System.arraycopy(r, 0, br, 0, rlen);
        System.arraycopy(s, 0, bs, 0, slen);

        ECCSignatureBlob signBlob = new ECCSignatureBlob();
        signBlob.setR(br);
        signBlob.setS(bs);
        return ECCDer.encode(signBlob.getR(), signBlob.getS());
    }

    /**
     * ECC 验签
     * @param lDevHandle            设备句柄
     * @param eccPublicKeyBlob      ECC公钥数据结构
     * @param digest                待验证签名数据
     * @param digestLen             数据长度
     * @param signature             待验证签名值
     * @return  Success
     * @throws SarException         错误码
     */
    public boolean SKF_ECCVerify(long lDevHandle, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob,byte[] digest,
                                 long digestLen, byte[] signature) throws SarException{
        SKF_CheckHandler(lDevHandle);
        byte[] r = ECCDer.decode(signature, ECCDer.R_X);
        byte[] s = ECCDer.decode(signature, ECCDer.S_Y);
        ECCSignatureBlob signBlob = new ECCSignatureBlob(r, s);
        SKF_CatchExcetpt(gmtDll.SKF_ECCVerify_N(lDevHandle, eccPublicKeyBlob.getBit(),eccPublicKeyBlob.getxCoordinate(),
                eccPublicKeyBlob.getyCoordinate(),digest, digestLen, r, s));
        return true;
    }


    public byte[] SKF_ExtECCDecrypt(long lDevHandle, SKFCspKey.ECCPrivateKeyBlob eccPrivateKeyBlob,
                                    ECCCipherBlob eccCipherBlob) throws SarException{
/*        SKF_CheckHandler(lDevHandle);
        ECCCipherBlob[] cipherBlob = new ECCCipherBlob[1];
        cipherBlob[0] = new ECCCipherBlob(eccCipherBlob.getXCoordinate(),eccCipherBlob.getYCoordinate(),
                eccCipherBlob.getHash(), eccCipherBlob.getCipherLen(), eccCipherBlob.getCipher());

        SKFCspKey.ECCPrivateKeyBlob[] privateKeyBlob = new SKFCspKey.ECCPrivateKeyBlob[1];
        privateKeyBlob[0] = new SKFCspKey.ECCPrivateKeyBlob(eccPrivateKeyBlob.getBit(), eccPrivateKeyBlob.getPrivateKey());
        long[] lPlaintextLen = new long[1];
        SKF_CatchExcetpt(SKF_ExtECCDecrypt_N(lDevHandle, privateKeyBlob, cipherBlob, null, lPlaintextLen));

        byte[] plainText = new byte[(int)lPlaintextLen[0]];
        SKF_CatchExcetpt(SKF_ExtECCDecrypt_N(lDevHandle, privateKeyBlob, cipherBlob, plainText, lPlaintextLen));
        return plainText;
*/
        return  null;
    }


    /**
     * 生成RSA密钥对
     * @param lContainerHandle      容器句柄
     * @param lBits                 密钥模长
     * @return  Rsa公钥数据结构
     * @throws SarException     错误码
     */
    public SKFCspKey.RSAPublicKeyBlob SKF_GenRSAKeyPair(long lContainerHandle, long lBits) throws SarException{
        SKF_CheckHandler(lContainerHandle);

        long[] lAlgID = new long[1];
        long[] lBitLen = new long[1];
        byte[] modulus = new byte[(int)lBits/8];
        byte[] publicExponent = new byte[(int)MAX_RSA_EXPONENT_LEN];

        SKF_CatchExcetpt(gmtDll.SKF_GenRSAKeyPair_N(lContainerHandle, lBits, lAlgID, lBitLen, modulus, publicExponent));

        SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob =
                new  SKFCspKey.RSAPublicKeyBlob(lAlgID[0], lBitLen[0], modulus, publicExponent);
        return rsaPublicKeyBlob;

    }

    public long SKF_RSAExportSessionKey(long lContainerHandle, long lAlgID, SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob,
                                        byte[] data, long[] lDataLen) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lSessionHandle = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_RSAExportSessionKey_N(lContainerHandle, lAlgID, rsaPublicKeyBlob.getAlgID(),
                rsaPublicKeyBlob.getBitLen(), rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent(),
                data, lDataLen, lSessionHandle));
        return lSessionHandle[0];
    }

    public byte[] SKF_ExtRSAPubKeyOperation(long lDevHandle, SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob, byte[] input,
                                            long lInputLen) throws SarException{
        SKF_CheckHandler(lDevHandle);
        long[] datalen = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_ExtRSAPubKeyOperation_N(lDevHandle, rsaPublicKeyBlob.getAlgID(),
                rsaPublicKeyBlob.getBitLen(), rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent(),
                input, lInputLen, null, datalen));
        byte[] dataout = new byte[(int)datalen[0]];
        SKF_CatchExcetpt(gmtDll.SKF_ExtRSAPubKeyOperation_N(lDevHandle, rsaPublicKeyBlob.getAlgID(),
                rsaPublicKeyBlob.getBitLen(), rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent(),
                input, lInputLen, dataout, datalen));
        return dataout;
    }

    /**
     * 导入RSA加密密钥
     * @param lContainerHandle      容器句柄
     * @param lAlgID                对称算法密钥标识
     * @param wrappedKey            使用该容器内签名公钥保护的对称算法密钥
     * @param wrappedKeyLen         保护的对称算法密钥长度
     * @param encryptedData         对称算法密钥保护的RSA加密私钥
     * @param encryptedDataLen      对称算法密钥保护的RSA加密公私钥对长度
     * @throws SarException     错误码
     */
    public void SKF_ImportRSAKeyPair(long lContainerHandle, long lAlgID, byte[] wrappedKey, long wrappedKeyLen,
                                     byte[] encryptedData, long encryptedDataLen) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        SKF_CatchExcetpt(gmtDll.SKF_ImportRSAKeyPair_N(lContainerHandle, lAlgID,wrappedKey, wrappedKeyLen,
                encryptedData, encryptedDataLen));
    }

    /**
     * RSA签名
     * @param lContainerHandle      容器句柄
     * @param data                  被签名的数据
     * @param dataLen               数据长度
     * @return 签名值
     * @throws SarException     错误码
     */
    public byte[] SKF_RSASignData(long lContainerHandle, byte[] data, long dataLen) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lSignatureLen = new long[1];
        SKF_CatchExcetpt(gmtDll.SKF_RSASignData_N(lContainerHandle, data, dataLen,null,lSignatureLen));
        byte[] signature = new byte[(int)lSignatureLen[0]];
        SKF_CatchExcetpt(gmtDll.SKF_RSASignData_N(lContainerHandle, data, dataLen,signature,lSignatureLen));
        return signature;
    }

    /**
     * RSA验签
     * @param lDevHandle            设备句柄
     * @param rsaPublicKey          RSA公钥数据结构
     * @param data                  待验证签名的数据
     * @param signature             待验证签名值
     * @return 成功
     * @throws SarException     错误码
     */
    public boolean SKF_RSAVerify(long lDevHandle, SKFCspKey.RSAPublicKeyBlob rsaPublicKey, byte[] data,
                                 byte[] signature) throws SarException{
        SKF_CheckHandler(lDevHandle);
        SKF_CatchExcetpt(gmtDll.SKF_RSAVerify_N(lDevHandle, rsaPublicKey.getAlgID(), rsaPublicKey.getBitLen(),
                rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent(), data, data.length, signature, signature.length));
        return true;
    }

    /**
     * 校验句柄是否有效
     * @param lHandle       句柄
     * @throws SarException     错误码
     */
    private void SKF_CheckHandler(long lHandle) throws SarException {
        if (lHandle <= INVALID_HANDLE) {
            throw new SarException(SarException.SAR_INVALIDHANDLEERR);
        }
    }

    /**
     * 捕获SarException
     * @param resultValue   返回值
     * @throws SarException     错误码
     */
    private void SKF_CatchExcetpt(long resultValue) throws SarException{
        if(resultValue != SarException.SAR_OK) {
            throw new SarException((int)resultValue);
        }
    }

    /**
     * 捕获JCSKFException
     * @param resultValue   返回值
     * @throws JCSKFException   错误码
     */
    private void SKF_CatchJCExcetpt(long resultValue) throws JCSKFException{
        if(resultValue != JCSKFException.JC_SKF_OK) {
            throw new JCSKFException((int)resultValue);
        }
    }
}
