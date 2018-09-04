/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.gmt0016.ftsafe.util;

import org.bcia.julongchain.csp.gmt0016.ftsafe.GMT0016CspConstant;

/**
 * Class description
 *
 * @author
 * @date 9/2/18
 * @company FEITIAN
 */
public class AnalyzeSKI {

    private String sContainerName = "";
    private boolean bSignFlag;
    private byte[] cipher;
    private byte[] hash;
    private int type;

    public void analyzeSKI(byte[] ski){

        //analyze ski
        int skiIndex = 0;
        while (skiIndex < ski.length) {
            int tag = ski[skiIndex];
            skiIndex++;
            switch (tag) {
                case GMT0016CspConstant.TAG_CONTAINER:
                    int nameLen = ski[skiIndex];
                    skiIndex++;
                    byte[] name = new byte[nameLen];
                    System.arraycopy(ski, skiIndex, name, 0, nameLen);
                    skiIndex += nameLen;
                    sContainerName = new String(name);
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_SIGN_FLAG:
                    int flagLen = ski[skiIndex];
                    skiIndex++;
                    bSignFlag = (ski[skiIndex] == (byte)1); //Sign Flag
                    skiIndex += flagLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_CIPHER_DATA:
                    int dataLen = ski[skiIndex];
                    skiIndex++;
                    cipher = new byte[dataLen];
                    System.arraycopy(ski, skiIndex, cipher, 0, dataLen);
                    skiIndex += dataLen;
                    break;
                case GMT0016CspConstant.TAG_PUBLICK_KEY_HASH:
                    int hashLen = ski[skiIndex];
                    skiIndex++;
                    hash = new byte[hashLen];
                    System.arraycopy(ski, skiIndex, hash, 0, hashLen);
                    skiIndex += hashLen;
                    break;
                case GMT0016CspConstant.TAG_KEY_TYPE:
                    int typelen = ski[skiIndex];
                    type = ski[++skiIndex];
                    skiIndex += typelen;
                    break;
                default:
                    break;
            }
        }
    }


    public String getContainerName(){ return sContainerName;}

    public boolean getSignFlag(){ return bSignFlag;}

    public int getType() { return type;}

    public byte[] getCipher() { return cipher;}

    public byte[] getHash() { return hash;}
}
