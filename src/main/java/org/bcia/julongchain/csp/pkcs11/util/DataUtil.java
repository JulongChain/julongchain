/**
 * Copyright Dingxuan. All Rights Reserved.
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

package org.bcia.julongchain.csp.pkcs11.util;

import static org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant.TAG;

import org.bcia.julongchain.csp.pkcs11.PKCS11CSPConstant;

/**
 * 数据转换类
 *
 * @author Ying Xu
 * @date 5/20/18
 * @company FEITIAN
 */
public class DataUtil {


    /**
     * Byte Array to Hex String
     *
     * @param src   Byte Array
     * @return  Hex String
     */
    /*
    public static String MyByteToHex(byte[] src)
    {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();

    }*/


    /**
     * 比较Byte数组是否一致
     * @param  b1   数组
     * @param  b2   Contrast byte array
     * @return true/false
     */
    public static boolean compareByteArray(byte[] b1, byte[] b2) {
    	
       if(b1.length == 0 || b2.length == 0 ){
           return false;
       }

       if (b1.length != b2.length) {
           return false;
       }

       boolean isEqual = true;
       for (int i = 0; i < b1.length && i < b2.length; i++) {
           if (b1[i] != b2[i]) {
               //System.out.println("different");
	           isEqual = false;
	           break;
	       }
	   }
	   return isEqual;
	}
    
    
    /**
     * 数据处理
     *
     * @param tempecpt     ECPoint数据
     * @return 去tag后数据
     */
    public static byte[] data(byte[] tempecpt){

        int len = tempecpt.length;
        byte[] tempdata = new byte[len];
        // Determine whether the data contains tag value
        if(0 == (len % PKCS11CSPConstant.CARDINAL_NUM) &&
                (tempecpt[0] == TAG)&&
                (tempecpt[len-1] == TAG))
        {
            // Trim trailing 0x04
            System.arraycopy(tempecpt, 0, tempdata, 0, len-1);
        }
        else if((tempecpt[0] == TAG) &&
                (tempecpt[2] == TAG))
        {
            System.arraycopy(tempecpt, 2, tempdata, 0, len-2);
        }
        else{
            tempdata = tempecpt;
        }

        return tempdata;
    }
}
