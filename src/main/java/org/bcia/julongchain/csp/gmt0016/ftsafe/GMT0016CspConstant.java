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

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class GMT0016CspConstant {

    public static final long ADMIN_TYPE=0;
    public static final long USER_TYPE=1;


    //asymmetric algorithm
    public static final long SGD_RSA = 0x00010000;
    public static final long SGD_SM2_1 = 0x00020100;
    public static final long SGD_SM2_2 = 0x00020200;
    public static final long SGD_SM2_3 = 0x00020400;

    public static final long SM_BITS = 256;
    public static final long MAX_RSA_MODULUS_LEN = 256;
    public static final long MAX_RSA_EXPONENT_LEN = 4;

    public static final long ECC_MAX_XCOORDINATE_BITS_LEN = 512;
    public static final long ECC_MAX_YCOORDINATE_BITS_LEN = 512;
    public static final long ECC_MAX_MODULUS_BITS_LEN = 512;


    public static final long SGD_SM1_ECB = 0x00000101;    //SM1算法ECB加密模式
    public static final long SGD_SM1_CBC = 0x00000102;    //SM1算法CBC加密模式
    public static final long SGD_SM1_CFB = 0x00000104;    //SM1算法CFB加密模式
    public static final long SGD_SM1_OFB = 0x00000108;    //SM1算法OFB加密模式
    public static final long SGD_SM1_MAC = 0x00000110;    //SM1算法MAC运算

    public static final long SGD_SSF33_ECB = 0x00000201;    //SSF33算法ECB加密模式
    public static final long SGD_SSF33_CBC = 0x00000202;    //SSF33算法CBC加密模式
    public static final long SGD_SSF33_CFB = 0x00000204;    //SSF33算法CFB加密模式
    public static final long SGD_SSF33_OFB = 0x00000208;    //SSF33算法OFB加密模式
    public static final long SGD_SSF33_MAC = 0x00000210;    //SSF33算法MAC运算

    public static final long SGD_SMS4_ECB = 0x00000401;    //SMS4算法ECB加密模式
    public static final long SGD_SMS4_CBC = 0x00000402;    //SMS4算法CBC加密模式
    public static final long SGD_SMS4_CFB = 0x00000404;    //SMS4算法CFB加密模式
    public static final long SGD_SMS4_OFB = 0x00000408;    //SMS4算法OFB加密模式
    public static final long SGD_SMS4_MAC = 0x00000410;    //SMS4算法MAC运算

    public static final long SGD_SM3 = 0x00000001;	//SM3运算算法
    public static final long SGD_SHA1 = 0x00000002;	//SHA1运算算法
    public static final long SGD_SHA256 = 0x00000004;	//SHA256运算算法



    public static final int TAG_CONTAINER = 0x01; //container
    public static final int TAG_KEY_CIPHER_DATA = 0x02;
    public static final int TAG_PUBLICK_KEY_SIGN_FLAG = 0x03;
    public static final int TAG_PUBLICK_KEY_HASH = 0x04; //hash
    public static final int TAG_KEY_TYPE = 0x05;

    public static final int BUFFERSIZE = 96;



    // an error will be returned.
    public static final String RSA = "RSA";
    // RSA at 1024 bit security level.
    public static final String RSA1024 = "RSA1024";
    // RSA at 2048 bit security level.
    public static final String RSA2048 = "RSA2048";
    // RSA at 3072 bit security level.
    public static final String RSA3072 = "RSA3072";
    // RSA at 4096 bit security level.
    public static final String RSA4096 = "RSA4096";


    public static final String SM1 = "SM1";
    public static final String SM2 = "SM2";
    public static final String SM3 = "SM3";
    public static final String SM4 = "SM4";
    public static final String SHA1 = "SHA1";
    public static final String SHA256 = "SHA256";
    public static final String SSF33 = "SSF33";

    public static final String SM1_ECB = "SM1_ECB";
    public static final String SM1_CBC = "SM1_CBC";
    public static final String SM1_CFB = "SM1_CFB";
    public static final String SM1_OFB = "SM1_OFB";
    public static final String SM1_MAC = "SM1_MAC";

    public static final String SM4_ECB = "SM4_ECB";
    public static final String SM4_CBC = "SM4_CBC";
    public static final String SM4_CFB = "SM4_CFB";
    public static final String SM4_OFB = "SM4_OFB";
    public static final String SM4_MAC = "SM4_MAC";

    public static final String SSF33_ECB = "SSF33_ECB";
    public static final String SSF33_CBC = "SSF33_CBC";
    public static final String SSF33_CFB = "SSF33_CFB";
    public static final String SSF33_OFB = "SSF33_OFB";
    public static final String SSF33_MAC = "SSF33_MAC";
}
