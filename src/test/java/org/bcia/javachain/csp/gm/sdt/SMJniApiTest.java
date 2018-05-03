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
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述
 *
 * @author tengxiumin
 * @date 4/24/18
 * @company SDT
 */
public class SMJniApiTest {

    private SMJniApi jni = new SMJniApi();

    @Test
    public void randomGenJniTest() {
        /*****************  正常用例集  **************/
        /******   case A-1 : length = 1   ******/
        int length = 1;
        unitTest(length);
        /******   case A-2 : length = 16   ******/
        length = 16;
        unitTest(length);
        /******   case A-3 : length = 128   ******/
        length = 128;
        unitTest(length);
        /******   case A-3 : length = 2048   ******/
        length = 2048;
        unitTest(length);

        /*****************  异常用例集  **************/
        /******   case B-1 : length = 0   ******/
        length = 0;
        unitTest(length);
        /******   case B-2 : length = 2049   ******/
        length = 2049;
        unitTest(length);

    }

    private void unitTest(int length) {
        try {
            System.out.println("RandomGen test case :");
            System.out.println("   ===== data length : " + length);
            byte[] random = jni.RandomGen(length);

            System.out.println("   ===== random data : " + Convert.bytesToHexString(random));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
