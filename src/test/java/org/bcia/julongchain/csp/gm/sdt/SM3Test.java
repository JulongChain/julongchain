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
import org.bcia.julongchain.csp.gm.sdt.SM3.SM3;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * SM3 算法单元测试
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class SM3Test {
    private SM3 sm3 = new SM3();
    private SMJniApi jni = new SMJniApi();

    @Before
    public void setUp() {

        System.out.println("setup...");
    }

    @After
    public void finalize(){

        System.out.println("finalize...");
    }

    @Test
    public void testSM3Hash() {
        System.out.println("============ SM3 Hash test ============ ");
        int[] messageLenList = {1, 16, 32, 64, 128, 256, 512, 1024, 4096};
        hashUnitTest(messageLenList);
    }

    private void hashUnitTest(int[] lists) {
        int caseIndex = 1;
        for(int i = 0; i < lists.length; i++) {
            try {
                int msgLen = lists[i];
                System.out.println("\n===== case "+ caseIndex++ +" :  message length is " + msgLen);
                byte[] msg = new byte[msgLen];
                if(msgLen > 1024) {
                    int leftLen = msgLen;
                    while (leftLen > 0) {
                        int len = leftLen;
                        if(len > 1024) {
                            len = 1024;
                        }
                        byte[] randomData = jni.RandomGen(len);
                        System.arraycopy(randomData, 0, msg, msgLen-leftLen, len);
                        leftLen = leftLen - len;
                    }
                } else {
                    msg = jni.RandomGen(msgLen);
                }
                System.out.println("[input data] message data : " + Convert.bytesToHexString(msg));
                byte[] hash = sm3.hash(msg);
                if (null != hash) {
                    System.out.println("[output data] hash data : " + Convert.bytesToHexString(hash));
                } else {
                    System.out.println("[**Error**] compute hash data failed");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    public void testSM3HashInvalidParameters() {

        byte[] msg0 = new byte[0];
        int caseIndex = 1;
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3 hash message is null");
            byte[] hash = sm3.hash(null);
            if (null != hash) {
                System.out.println("[output data] hash data : " + Convert.bytesToHexString(hash));
            } else {
                System.out.println("[**Error**] compute hash data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3 hash message length is 0");
            byte[] hash = sm3.hash(msg0);
            if (null != hash) {
                System.out.println("[output data] hash data : " + Convert.bytesToHexString(hash));
            } else {
                System.out.println("[**Error**] compute hash data failed");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

