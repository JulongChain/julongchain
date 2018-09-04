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
        int count = 0;
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
     * Compare byte array value
     * @param b1    byte array source
     * @param b2    byte array data
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
     * Make SKI
     * @param keytype       key type(RSA or SM2)
     * @param container     container name
     * @param signflag      Signatures or encrypted identities
     * @param pubhash       publickey hash  (maybe useful)
     * @return ski byte array
     * @throws Exception
     */
    public static byte[] getKeySki(int keytype, byte[] container, int signflag, byte[] pubhash) throws Exception {
        byte flag[] = new byte[1];
        flag[0] = (byte)signflag; //for sign
        byte type[] = new byte[1];
        type[0] = (byte)keytype; //RSA
        byte[] tlv_Container = DataUtil.getTLV(TAG_CONTAINER, container, container.length);
        byte[] tlv_SignFlag = DataUtil.getTLV(TAG_PUBLICK_KEY_SIGN_FLAG, flag, 1);
        byte[] tlv_Type = DataUtil.getTLV(TAG_KEY_TYPE, type, 1);
        byte[] tlv_PublicHash = DataUtil.getTLV(TAG_PUBLICK_KEY_HASH, pubhash, pubhash.length);
        byte[] skiData = new byte[tlv_Type.length + tlv_Container.length + tlv_SignFlag.length + tlv_PublicHash.length];
        int pos = 0;
        System.arraycopy(tlv_Type, 0, skiData, 0, tlv_Type.length);
        pos += tlv_Type.length;
        System.arraycopy(tlv_Container, 0, skiData, pos, tlv_Container.length);
        pos += tlv_Container.length;
        System.arraycopy(tlv_SignFlag, 0, skiData, pos, tlv_SignFlag.length);
        pos += tlv_SignFlag.length;
        System.arraycopy(tlv_PublicHash, 0, skiData, pos, tlv_PublicHash.length);
        return skiData;

    }


    public static byte[] data(byte[] tempecpt) throws Exception {

        int len = tempecpt.length;
        byte[] tempdata = new byte[len];
        if(0 == (len % 2) &&
                (tempecpt[0] == DER_TAG)&&
                (tempecpt[len-1] == DER_TAG))
        {
            // Trim trailing 0x04
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
