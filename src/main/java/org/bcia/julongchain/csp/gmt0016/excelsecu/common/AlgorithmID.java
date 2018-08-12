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
package org.bcia.julongchain.csp.gmt0016.excelsecu.common;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class AlgorithmID {


    //cryptographic hash algorithm
    public static final int SGD_SM3 = 0x00000001;
    public static final int SGD_SHA1 = 0x00000002;
    public static final int SGD_SHA256 = 0x00000004;


    //asymmetric algorithm
    public static final int SGD_RSA = 0x00010000;
    public static final int SGD_SM2 = 0x00020100;
    public static final int SGD_SM2_1 = 0x00020200;
    public static final int SGD_SM2_2 = 0x00020400;
    public static final int SGD_SM2_3 = 0x00020800;


    //symmetrical algorithm

    // block cipher
    public static final int SGD_DES_ECB = 0xF1000001;  //DES算法ECB加密模式
    public static final int SGD_DES_CBC = 0xF1000002;  //DES算法CBC加密模式
    public static final int SGD_DES_CFB = 0xF1000004;  //DES算法CFB加密模式
    public static final int SGD_DES_OFB = 0xF1000008;  //DES算法OFB加密模式
    public static final int SGD_DES_MAC = 0xF1000010;  //DES算法MAC加密模式

    public static final int SGD_TDES_ECB = 0xF2000001;  //TDES算法ECB加密模式
    public static final int SGD_TDES_CBC = 0xF2000002;  //TDES算法CBC加密模式
    public static final int SGD_TDES_CFB = 0xF2000004;  //TDES算法CFB加密模式
    public static final int SGD_TDES_OFB = 0xF2000008;  //TDES算法OFB加密模式
    public static final int SGD_TDES_MAC = 0xF2000010;  //TDES算法MAC加密模式
    public static final int SGD_3DES_ECB = 0x00000001;  //3DES算法ECB加密模式
    public static final int SGD_3DES_CBC = 0x00000002;  //3DES算法CBC加密模式
    public static final int SGD_3DES_CFB = 0x00000004;  //3DES算法CFB加密模式
    public static final int SGD_3DES_OFB = 0x00000008;  //3DES算法OFB加密模式
    public static final int SGD_3DES_MAC = 0x00000010;  //3DES算法MAC加密模式

    public static final int SGD_SM1_ECB = 0x00000101;    //SM1算法ECB加密模式
    public static final int SGD_SM1_CBC = 0x00000102;    //SM1算法CBC加密模式
    public static final int SGD_SM1_CFB = 0x00000104;    //SM1算法CFB加密模式
    public static final int SGD_SM1_OFB = 0x00000108;    //SM1算法OFB加密模式
    public static final int SGD_SM1_MAC = 0x00000110;    //SM1算法MAC运算

    public static final int SGD_SSF33_ECB = 0x00000201;    //SSF33算法ECB加密模式
    public static final int SGD_SSF33_CBC = 0x00000202;    //SSF33算法CBC加密模式
    public static final int SGD_SSF33_CFB = 0x00000204;    //SSF33算法CFB加密模式
    public static final int SGD_SSF33_OFB = 0x00000208;    //SSF33算法OFB加密模式
    public static final int SGD_SSF33_MAC = 0x00000210;    //SSF33算法MAC运算

    public static final int SGD_SMS4_ECB = 0x00000401;    //SMS4算法ECB加密模式
    public static final int SGD_SMS4_CBC = 0x00000402;    //SMS4算法CBC加密模式
    public static final int SGD_SMS4_CFB = 0x00000404;    //SMS4算法CFB加密模式
    public static final int SGD_SMS4_OFB = 0x00000408;    //SMS4算法OFB加密模式
    public static final int SGD_SMS4_MAC = 0x00000410;    //SMS4算法MAC运算


}
