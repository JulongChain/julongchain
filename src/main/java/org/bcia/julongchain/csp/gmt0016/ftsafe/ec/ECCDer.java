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
package org.bcia.julongchain.csp.gmt0016.ftsafe.ec;

import java.util.Arrays;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class ECCDer {

    public static final int R_X = 0;
    public static final int S_Y = 1;

    public static byte[] encode(byte[] r_x, byte[] s_y) {

        byte rxBit = r_x[0];
        byte syBit = s_y[0];
        String binRStr = Integer.toBinaryString((rxBit & 0xFF) + 0x100).substring(1);
        String binSStr = Integer.toBinaryString((syBit & 0xFF) + 0x100).substring(1);
        byte[] rbigByte = binRStr.getBytes();
        byte[] sbigByte = binSStr.getBytes();
        byte[] paddingByte = {0};
        if (rbigByte[0] == 0x31) {
            r_x = concatArrays(paddingByte, r_x);
        }
        if (sbigByte[0] == 0x31) {
            s_y = concatArrays(paddingByte, s_y);
        }
        byte rxLength= intToByte(r_x.length);
        byte syLength= intToByte(s_y.length);

        byte totalLength=intToByte(s_y.length+r_x.length+4);
        // Der 30 length 02 rLength r 02 sLength s
        byte[] rByteArrayBefore = {0x30, totalLength, 0x02, rxLength};
        byte[] betweenRAndS = {0x02, syLength};

        return concatArrays(rByteArrayBefore, r_x, betweenRAndS, s_y);
    }

    public static byte[] decode(byte[] data, int type) {

        int rLength = data[3];
        int sLength = data[5 + rLength];
        byte[] r_x = new byte[rLength];
        byte[] s_y = new byte[sLength];

        if (type == R_X) {
            if(rLength == 0x21)
            {
                System.arraycopy(data, 5, r_x, 0, r_x.length-1);
            }else {
                System.arraycopy(data, 4, r_x, 0, r_x.length);
            }
            return r_x;
        } else if (type == S_Y) {
            if(sLength == 0x21)
            {
                System.arraycopy(data, 7 + rLength, s_y, 0, s_y.length-1);
            }else {
                System.arraycopy(data, 6 + rLength, s_y, 0, s_y.length);
            }
            return s_y;
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
