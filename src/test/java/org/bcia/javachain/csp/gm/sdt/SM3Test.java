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
package org.bcia.javachain.csp.gm.sdt;

import org.bcia.javachain.common.util.Convert;
import org.bcia.javachain.csp.gm.sdt.SM3.SM3;
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * SM3 算法单元测试
 *
 * @author tengxiumin
 * @date 5/16/18
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
    public void testSM3Hash() throws Exception {
        System.out.println("============ SM3 Hash test ============ ");
        /*****************  正常用例集  **************/
        byte[] msg1 = jni.RandomGen(1);
        byte[] msg16 = jni.RandomGen(16);
        byte[] msg32 = jni.RandomGen(32);
        byte[] msg64 = jni.RandomGen(64);
        byte[] msg128 = jni.RandomGen(128);
        byte[] msg256 = jni.RandomGen(256);
        byte[] msg512 = jni.RandomGen(512);
        byte[] msg1024 = jni.RandomGen(1024);
        byte[] msg2048 = new byte[2048];
        System.arraycopy(msg1024, 0, msg2048, 0, 1024);
        System.arraycopy(msg1024, 0, msg2048, 1024, 1024);


        int caseIndex = 1;
        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 1");
            byte[] buff = sm3.hash(msg1);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 16");
            byte[] buff = sm3.hash(msg16);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 32");
            byte[] buff = sm3.hash(msg32);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 64");
            byte[] buff = sm3.hash(msg64);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 128");
            byte[] buff = sm3.hash(msg128);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 256");
            byte[] buff = sm3.hash(msg256);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 512");
            byte[] buff = sm3.hash(msg512);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 1024");
            byte[] buff = sm3.hash(msg1024);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case A-"+ caseIndex++ +" :  SM3 msg is 2048");
            byte[] buff = sm3.hash(msg2048);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Test
    public void testSM3HashInvalidParameters() throws Exception {

        byte[] msg0 = new byte[0];
        byte[] msg1024 = jni.RandomGen(1024);
        byte[] msg4096 = new byte[4096];
        System.arraycopy(msg1024, 0, msg4096, 0, 1024);
        System.arraycopy(msg1024, 0, msg4096, 1024, 1024);
        System.arraycopy(msg1024, 0, msg4096, 2048, 1024);
        System.arraycopy(msg1024, 0, msg4096, 3072, 1024);
        byte[] msg8192 = new byte[8192];
        System.arraycopy(msg4096, 0, msg8192, 0, 4096);
        System.arraycopy(msg4096, 0, msg8192, 4096, 4096);
        int caseIndex = 1;
        /*****************  异常用例集  **************/
        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3 hash msg is null");
            byte[] buff = sm3.hash(null);
            System.out.println("    signature data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3 hash msg is 0");
            byte[] buff = sm3.hash(msg0);
            System.out.println("    signature data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3 hash msg is 4096");
            byte[] buff = sm3.hash(msg4096);
            System.out.println("    signature data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        try {
            System.out.println("\n===== case B-"+ caseIndex++ +" :  SM3 hash msg is 8192");
            byte[] buff = sm3.hash(msg8192);
            System.out.println("    signature data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("    Exception: " + e.getMessage());
        }
    }
}

