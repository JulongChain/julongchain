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

/**
 * data transfer class
 *
 * @author xuying
 * @date 2018/05/20
 * @company FEITIAN
 */
public class DataUtil {


    /**
     * 进制转换
     *
     * @param b     二行制数据
     * @return  十六进制数据字符串
     */
    public static String MyByteToHex(byte[] b)
    {
        String hs="";
        String stmp="";
        for (int n=0;n<b.length;n++)
        {
            stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1) hs=hs+"0"+stmp;
            else hs=hs+stmp;
            if (n<b.length-1)  hs=hs+":";
        }
        return hs.toUpperCase();
    }
    
    
    public static boolean compereByteArray(byte[] b1, byte[] b2) {
    	
       if(b1.length == 0 || b2.length == 0 ){
           return false;
       }

       if (b1.length != b2.length) {
           return false;
       }

       boolean isEqual = true;
       for (int i = 0; i < b1.length && i < b2.length; i++) {
           if (b1[i] != b2[i]) {
               System.out.println("different");
	               isEqual = false;
	               break;
	           }
	       }
	       return isEqual;
	}
}
