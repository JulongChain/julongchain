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

/**
 * Class description
 *
 * @author
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
}
