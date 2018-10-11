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
package org.bcia.julongchain.csp.gmt0016.ftsafe.util;

import static org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * Class description
 *
 * @author Ying Xu
 * @date 7/4/18
 * @company FEITIAN
 */
public class DataUtil {

    private static final int LEN_SURPLUS = 2;

    public static byte[] getTLV(int tag, byte[] value, int length) {
        byte[] tlv = new byte[value.length + 2];
        tlv[0] = (byte) tag;
        tlv[1] = (byte) length;
        System.arraycopy(value, 0, tlv, 2, value.length);
        return tlv;
    }

    /**
     *
     * 获取字符串字节流中有效字节个数
     *
     * @param buf
     *
     * @return
     */

    public static int getVirtualValueLength(byte[] buf)
    {
        int len = buf.length;
        //int count = 0;
        for (; len > 0; len--)
        {
            if (buf[len-1] != (byte) 0)
            {
                break;
            }
        }
        return len;
    }

    /**
     * 比较byte数组是否一致
     * @param b1    源数据
     * @param b2    对比数据
     * @return  ture/false
     */
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

    /**
     * 生成SKI
     * @param keytype       密钥类型(RSA or SM2)
     * @param container     密钥容器名称
     * @param signflag      签名/加密类型标识
     * @param pubhash       公钥hash  (maybe useful)
     * @return ski
     * @throws Exception    错误码
     */
    public static byte[] getKeySki(int keytype, byte[] container, int signflag, byte[] pubhash) throws Exception {
        byte[] flag = new byte[1];
        //签名标识
        flag[0] = (byte)signflag;
        byte[] type = new byte[1];
        //密钥算法类型标识
        type[0] = (byte)keytype;
        byte[] tlvContainer = DataUtil.getTLV(TAG_CONTAINER, container, container.length);
        byte[] tlvSignFlag = DataUtil.getTLV(TAG_PUBLICK_KEY_SIGN_FLAG, flag, 1);
        byte[] tlvType = DataUtil.getTLV(TAG_KEY_TYPE, type, 1);
        byte[] tlvPublicHash = DataUtil.getTLV(TAG_PUBLICK_KEY_HASH, pubhash, pubhash.length);
        byte[] skiData = new byte[tlvType.length + tlvContainer.length + tlvSignFlag.length + tlvPublicHash.length];
        int pos = 0;
        System.arraycopy(tlvType, 0, skiData, 0, tlvType.length);
        pos += tlvType.length;
        System.arraycopy(tlvContainer, 0, skiData, pos, tlvContainer.length);
        pos += tlvContainer.length;
        System.arraycopy(tlvSignFlag, 0, skiData, pos, tlvSignFlag.length);
        pos += tlvSignFlag.length;
        System.arraycopy(tlvPublicHash, 0, skiData, pos, tlvPublicHash.length);
        return skiData;

    }


    public static byte[] data(byte[] tempecpt) throws Exception {

        int len = tempecpt.length;
        byte[] tempdata = new byte[len];
        if(0 == (len % LEN_SURPLUS) &&
                (tempecpt[0] == DER_TAG)&&
                (tempecpt[len-1] == DER_TAG))
        {
            // 去掉 0x04
            System.arraycopy(tempecpt, 0, tempdata, 0, len-1);
        }
        else if((tempecpt[0] == DER_TAG) &&
                (tempecpt[2] == DER_TAG))
        {
            System.arraycopy(tempecpt, 2, tempdata, 0, len-2);
        }
        else
            tempdata = tempecpt;

        return tempdata;
    }

}
