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
import org.bcia.julongchain.csp.gm.sdt.random.GmRandom;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 产生随机数 单元测试
 *
 * @author tengxiumin
 * @date 2018/05/18
 * @company SDT
 */
public class GmRandomTest {
    @Before
    public void setUp() {
        System.out.println("setup...");
    }

    @After
    public void finalize(){
        System.out.println("finalize...");
    }

    @Test
    public void testRng() {
        System.out.println("============= GmRandom rng test =============");
        int[] randomLen = {1, 16, 32, 128, 240, 1024};
        unitTest(randomLen);
    }

    @Test
    public void testRngInvalidParams() {
        System.out.println("============= GmRandom rng invalid parameters test =============");
        int[] invalidRandomLens = {0, -1, 1025};
        unitTest(invalidRandomLens);

    }

    private void unitTest(int[] lists) {
        GmRandom randomGen = new GmRandom();
        int caseIndex = 1;
        for (int index = 0; index < lists.length; index++) {
            try {
                int len = lists[index];
                System.out.println("\n**** case " + caseIndex++ + ": generate random length = " + len + "  ****");
                byte[] random = randomGen.rng(len);
                if (null != random) {
                    System.out.println("[ output ] random data : " + Convert.bytesToHexString(random));
                } else {
                    System.out.println("[** error **] failed generating random data");
                }
            } catch (Exception e) {
                System.out.println("[## exception ##] " + e.getMessage());
            }
        }
    }
}
