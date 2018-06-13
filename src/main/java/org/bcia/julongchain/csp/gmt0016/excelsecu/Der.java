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

import java.util.Arrays;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class Der {
    // TODO: 2018/3/30 Der编码

    public static final int R = 0;
    public static final int S = 1;


    public static byte[] encode(byte[] r, byte[] s) {

        byte rBit = r[r.length - 1]; //r highest position bit
        byte sBit = s[s.length - 1]; //s highest position bit

        byte[] paddingByte = {0};

        if (rBit == 0x01) {
            r = concatArrays(paddingByte, r);
        }
        if (sBit == 0x01) {
            s = concatArrays(paddingByte, s);
        }
        byte rLength= intToByte(r.length);
        byte sLength= intToByte(s.length);

        byte totalLength=intToByte(s.length+r.length+4);
        // Der编码的签名值 30 length 02 rLength r 02 sLength s
        byte[] rByteArrayBefore = {0x30, totalLength, 0x02, rLength};
        byte[] betweenRAndS = {0x02, sLength};

        return concatArrays(rByteArrayBefore, r, betweenRAndS, s);
    }

    public static byte[] decode(byte[] data, int type) {

        int rLength = data[3];
        int sLength = data[5 + rLength];
        byte[] r = new byte[rLength];
        byte[] s = new byte[sLength];

        if (type == R) {
            System.arraycopy(data, 4, r, 0, r.length);
            return r;
        } else if (type == S) {
            System.arraycopy(data, 6 + rLength, s, 0, s.length);
            return s;
        }

        return new byte[1];
    }


    private static byte[] concatArrays(byte[] first, byte[]... rest) {

        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private static byte intToByte(int i) {

       return Byte.valueOf(String.valueOf(i),10);
    }
}
