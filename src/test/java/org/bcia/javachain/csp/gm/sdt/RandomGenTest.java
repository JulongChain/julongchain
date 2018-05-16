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

import org.bcia.javachain.csp.gm.sdt.random.RandomGen;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Test
 *
 * @author tengxiumin
 * @date 4/23/18
 * @company SDT
 */
public class RandomGenTest {
    @Before
    public void setUp() {
        System.out.println("setup...");
    }

    @After
    public void finalize(){
        System.out.println("finalize...");
    }

    @Test
    public void rngTest()  {
        RandomGen randomGen = new RandomGen();
        /*****************  正常用例集  **************/
        /******   case A-1 : length = 1   ******/
        int randomLength = 1;
        byte[] randomBytesA1 = randomGen.rng(randomLength, null);
        System.out.println("RandomGen test case :");
        System.out.println("   ===== data length : "+randomLength);
        System.out.println("   ===== random data : "+bytesToHexString(randomBytesA1));
        /******   case A-2 : length = 16   ******/
        randomLength = 16;
        byte[] randomBytesA2 = randomGen.rng(randomLength, null);
        System.out.println("RandomGen test case :");
        System.out.println("   ===== data length : "+randomLength);
        System.out.println("   ===== random data : "+bytesToHexString(randomBytesA2));
        /******   case A-3 : length = 32   ******/
        randomLength = 32;
        byte[] randomBytesA3 = randomGen.rng(randomLength, null);
        System.out.println("RandomGen test case :");
        System.out.println("   ===== data length : "+randomLength);
        System.out.println("   ===== random data : "+bytesToHexString(randomBytesA3));
        /******   case A-4 : length = 64   ******/
        randomLength = 64;
        byte[] randomBytesA4 = randomGen.rng(randomLength, null);
        System.out.println("RandomGen test case :");
        System.out.println("   ===== data length : "+randomLength);
        System.out.println("   ===== random data : "+bytesToHexString(randomBytesA4));
        /******   case A-5 : length = 128   ******/
        randomLength = 128;
        byte[] randomBytesA5 = randomGen.rng(randomLength, null);
        System.out.println("RandomGen test case :");
        System.out.println("   ===== data length : "+randomLength);
        System.out.println("   ===== random data : "+bytesToHexString(randomBytesA5));

        /*****************  异常用例集  **************/
        /******   case B-1 : length = 0   ******/
        randomLength = 0;
        byte[] randomBytesB1 = randomGen.rng(randomLength, null);
        System.out.println("RandomGen test case :");
        System.out.println("   ===== data length : "+randomLength);
        System.out.println("   ===== random data : "+bytesToHexString(randomBytesB1));
        /******   case B-2 : length = 16   ******/
        randomLength = -1;
        byte[] randomBytesB2 = randomGen.rng(randomLength, null);
        System.out.println("RandomGen test case :");
        System.out.println("   ===== data length : "+randomLength);
        System.out.println("   ===== random data : "+bytesToHexString(randomBytesB2));
    }


    private  String bytesToHexString(byte[] bArray) {
        if(bArray == null) {
            return "byte array is null";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
}
