/**
 * Copyright SDT. All Rights Reserved.
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
package org.bcia.julongchain.csp.gm.sdt;

import org.bcia.julongchain.common.util.Convert;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;
import org.junit.Test;

import java.util.Arrays;

/**
 * SMJniApi SM3算法接口单元测试
 *
 * @author tengxiumin
 * @date 2018/05/29
 * @company SDT
 */
public class SMJniSM3ApiTest {

    private SMJniApi jni = new SMJniApi();

    @Test
    public void testSM3Hash()
    {
        System.out.println("============= SMJniApi sm3Hash test =============");
        int[] messageLenList = {1, 16, 128, 256, 1024, 4096};
        int caseIndex = 0;
        for(int i = 0; i < messageLenList.length; i++) {
            int messageLen = messageLenList[i];
            byte[] message = new byte[messageLen];
            for(int j = 0; j < messageLen; j++) {
                message[j] = (byte)((j+1)%255);
            }
            try {
                System.out.println("\n**** case " + caseIndex++ + ": message length = "+ messageLen + " ****");
                System.out.println("[ input ] message : " + Convert.bytesToHexString(message));
                byte[] digest = jni.sm3Hash(message);
                if (null != digest) {
                    System.out.println("[ output ] digest: " + Convert.bytesToHexString(digest));
                } else {
                    System.out.println("[** error** ] failed computing message digest by SM3 algorithm");
                }
            } catch (Exception e) {
                System.out.println("[## exception ##] " + e.getMessage());
            }
        }
    }

    @Test
    public void testSM3HashInvalidParams() {
        System.out.println("============= SMJniApi sm3Hash invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": message is null ****");
            byte[] digest = jni.sm3Hash(null);
            if (null != digest) {
                System.out.println("[ output ] digest : " + Convert.bytesToHexString(digest));
            } else {
                System.out.println("[** error** ] failed computing message digest by SM3 algorithm");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": message length is 0 ****");
            byte[] message = new byte[0];
            byte[] digest = jni.sm3Hash(message);
            if (null != digest) {
                System.out.println("[ output ] digest : " + Convert.bytesToHexString(digest));
            } else {
                System.out.println("[** error **] failed computing message digest by SM3 algorithm");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void SM3HashResultCompareWithStandardData() {
        System.out.println("============= SMJniApi sm3Hash result compare with GM/T 0004-2012 standard data =============");
        System.out.println("****  case 1  ****");
        try {
            byte[] gmtDigest = {(byte)0x66, (byte)0xC7, (byte)0xF0, (byte)0xF4, (byte)0x62, (byte)0xEE, (byte)0xED, (byte)0xD9,
                    (byte)0xD1, (byte)0xF2, (byte)0xD4, (byte)0x6B, (byte)0xDC, (byte)0x10, (byte)0xE4, (byte)0xE2,
                    (byte)0x41, (byte)0x67, (byte)0xC4, (byte)0x87, (byte)0x5C, (byte)0xF2, (byte)0xF7, (byte)0xA2,
                    (byte)0x29, (byte)0x7D, (byte)0xA0, (byte)0x2B, (byte)0x8F, (byte)0x4B, (byte)0xA8, (byte)0xE0};
            byte[] message = {97, 98, 99};
            System.out.println("[ input ] message : " + Convert.bytesToHexString(message));
            byte[] digest = jni.sm3Hash(message);
            System.out.println("[ output ] digest : " + Convert.bytesToHexString(digest));

            if(Arrays.equals(digest, gmtDigest)) {
                System.out.println("[ compare result | equal ] sm3Hash result is equal with GM/T 0004-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm3Hash result is not equal with GM/T 0004-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }

        System.out.println("****  case 2  ****");
        try {
            byte[] gmtDigest = {(byte)0xDE, (byte)0xBE, (byte)0x9F, (byte)0xF9, (byte)0x22, (byte)0x75, (byte)0xB8, (byte)0xA1,
                    (byte)0x38, (byte)0x60, (byte)0x48, (byte)0x89, (byte)0xC1, (byte)0x8E, (byte)0x5A, (byte)0x4D,
                    (byte)0x6F, (byte)0xDB, (byte)0x70, (byte)0xE5, (byte)0x38, (byte)0x7E, (byte)0x57, (byte)0x65,
                    (byte)0x29, (byte)0x3D, (byte)0xCB, (byte)0xA3, (byte)0x9C, (byte)0x0C, (byte)0x57, (byte)0x32};
            byte[] message = new byte[64];
            String srcString = "abcd";
            for (int i = 0; i < 16; i++) {
                System.arraycopy(srcString.getBytes(), 0, message, i*4, 4);
            }
            System.out.println("[ input ] message : " + Convert.bytesToHexString(message));
            byte[] digest = jni.sm3Hash(message);
            System.out.println("[ output ] digest : " + Convert.bytesToHexString(digest));

            if(Arrays.equals(digest, gmtDigest)) {
                System.out.println("[ compare result | equal ] sm3Hash result is equal with GM/T 0004-2012 standard data");
            } else {
                System.out.println("[ compare result | unequal ] sm3Hash result is not equal with GM/T 0004-2012 standard data");
            }
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }

    @Test
    public void testSM3HashSpeed() {
        System.out.println("============= SMJniApi sm3Hash speed test =============");
        try {
            byte[] testMessage = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                testMessage[i] = (byte)((i+1)%255);
            }
            int num = 5;
            byte[] digest = null;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                digest = jni.sm3Hash(testMessage);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[ total time ] sm3Hash " + (100*num) + "MB data need : " +
                                (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[ speed ] sm3Hash speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("[## exception ##] " + e.getMessage());
        }
    }
}
