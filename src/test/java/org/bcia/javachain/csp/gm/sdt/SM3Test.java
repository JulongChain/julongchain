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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 类描述
 *
 * @author tengxiumin
 * @date 18-5-9
 * @company SDT
 */
public class SM3Test {
    private SM3 sm3 = new SM3();
    @Before
    public void setUp() {

        System.out.println("setup...");
    }

    @After
    public void finalize(){

        System.out.println("finalize...");
    }

    @Test
    public void SM3HashTest()
    {
        System.out.println("SM3Hash test case :");
        /*****************  正常用例集  **************/
        /******   case A-1 : msg 16 bytes******/
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1};
            byte[] buff = sm3.hash(msg);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        /*****************  正常用例集  **************/
        /******   case A-2 : msg 32 bytes ******/
        try {
            byte[] msg = {(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1,(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E, (byte)0x3C, (byte)0xA1,(byte)0xF1, (byte)0x5D, (byte)0x12, (byte)0x7A, (byte)0x02, (byte)0xBC, (byte)0x65, (byte)0x89,
                    (byte)0x60, (byte)0xA0, (byte)0x71, (byte)0x6B, (byte)0x3F, (byte)0x8E};
            byte[] buff = sm3.hash(msg);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
            System.out.println("   ===== msg 32 bytes " );
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        /*****************  正常用例集  **************/
        /******   case A-3 : msg0 byte***/
        try {
            byte[] msg = null;
            System.out.println("   ===== msg is null " );
            byte[] buff = sm3.hash(msg);
            System.out.println("   ===== hash data : " + Convert.bytesToHexString(buff));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }



    }
}
