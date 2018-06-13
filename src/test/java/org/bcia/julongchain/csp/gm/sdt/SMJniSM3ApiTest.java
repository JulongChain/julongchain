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
 * SDTSMJNI SM3接口单元测试
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
        System.out.println("============= SMJniApi SM3Hash test =============");
        int[] msgLenList = {1, 16, 128, 256, 1024, 4096};
        int caseIndex = 0;
        for(int i = 0; i < msgLenList.length; i++) {
            int msgLen = msgLenList[i];
            byte[] msg = new byte[msgLen];
            for(int j = 0; j < msgLen; j++) {
                msg[j] = (byte)((j+1)%255);
            }
            try {
                System.out.println("\n**** case " + caseIndex++ + ": SM3Hash message length = "+ msgLen + " ****");
                System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
                byte[] hash = jni.SM3Hash(msg);
                if (null != hash) {
                    System.out.println("[output data] SM3 hash data : " + Convert.bytesToHexString(hash));
                } else {
                    System.out.println("[**Error**] SM3 hash failed");
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }

    @Test
    public void testSM3HashInvalidParams() {
        System.out.println("============= SMJniApi SM3Hash invalid parameters test =============");
        int caseIndex = 1;
        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM3Hash message is null ****");
            byte[] hash = jni.SM3Hash(null);
            if (null != hash) {
                System.out.println("[output data] SM3 hash data : " + Convert.bytesToHexString(hash));
            } else {
                System.out.println("[**Error**] SM3 hash failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n**** case " + caseIndex++ + ": SM3Hash message length is 0 ****");
            byte[] msg = new byte[0];
            byte[] hash = jni.SM3Hash(msg);
            if (null != hash) {
                System.out.println("[output data] SM3 hash data : " + Convert.bytesToHexString(hash));
            } else {
                System.out.println("[**Error**] SM3 hash failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void SM3HashDataCompareWithStandardData() {
        System.out.println("============= SMJniApi SM3 hash data compare with GM/T 0004-2012 =============");
        System.out.println("**** case 1: ****");
        try {
            byte[] GMHashData = {(byte)0x66, (byte)0xC7, (byte)0xF0, (byte)0xF4, (byte)0x62, (byte)0xEE, (byte)0xED, (byte)0xD9,
                    (byte)0xD1, (byte)0xF2, (byte)0xD4, (byte)0x6B, (byte)0xDC, (byte)0x10, (byte)0xE4, (byte)0xE2,
                    (byte)0x41, (byte)0x67, (byte)0xC4, (byte)0x87, (byte)0x5C, (byte)0xF2, (byte)0xF7, (byte)0xA2,
                    (byte)0x29, (byte)0x7D, (byte)0xA0, (byte)0x2B, (byte)0x8F, (byte)0x4B, (byte)0xA8, (byte)0xE0};
            byte[] msg = {97, 98, 99};
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            byte[] hash = jni.SM3Hash(msg);
            System.out.println("[output data] hash data : " + Convert.bytesToHexString(hash));

            if(Arrays.equals(hash, GMHashData)) {
                System.out.println("[compare result equal] SM3 hash data is equal with GM/T 0004-2012");
            } else {
                System.out.println("[compare result not equal] SM3 hash data is not equal with GM/T 0004-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        System.out.println("**** case 2: ****");
        try {
            byte[] GMHashData = {(byte)0xde, (byte)0xbe, (byte)0x9f, (byte)0xf9, (byte)0x22, (byte)0x75, (byte)0xb8, (byte)0xa1,
                    (byte)0x38, (byte)0x60, (byte)0x48, (byte)0x89, (byte)0xc1, (byte)0x8e, (byte)0x5a, (byte)0x4d,
                    (byte)0x6f, (byte)0xdb, (byte)0x70, (byte)0xe5, (byte)0x38, (byte)0x7e, (byte)0x57, (byte)0x65,
                    (byte)0x29, (byte)0x3d, (byte)0xcb, (byte)0xa3, (byte)0x9c, (byte)0x0c, (byte)0x57, (byte)0x32};
            byte[] msg = { 'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d',
                    'a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d','a','b','c','d'};
            System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
            byte[] hash = jni.SM3Hash(msg);
            System.out.println("[output data] hash data : " + Convert.bytesToHexString(hash));

            if(Arrays.equals(hash, GMHashData)) {
                System.out.println("[compare result equal] SM3 hash data is equal with GM/T 0004-2012");
            } else {
                System.out.println("[compare result not equal] SM3 hash data is not equal with GM/T 0004-2012");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM3HashSpeed() {
        System.out.println("============= SMJniApi SM3 hash speed test =============");
        try {
            byte[] testMsg = new byte[1024];
            for(int i = 0; i < 1024; i++) {
                testMsg[i] = (byte)((i+1)%255);
            }
            int num = 5;
            byte[] hash = null;
            long startTime = System.currentTimeMillis();
            for(int i = 0;i < 1024*100*num; i++) {
                hash = jni.SM3Hash(testMsg);
            }
            long endTime = System.currentTimeMillis();
            float speed = (float) (100*num/ ((endTime - startTime)/1000.00));
            System.out.println("[total time] SM3 hash " + (100*num) + "MB data need : " + (float) (endTime - startTime)/1000.00 + "s");
            System.out.println("[speed] SM3 hash speed : " + speed + "MB/s");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
