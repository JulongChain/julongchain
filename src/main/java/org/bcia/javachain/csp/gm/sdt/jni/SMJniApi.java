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
package org.bcia.javachain.csp.gm.sdt.jni;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;

/**
 * JNI interface definition
 *
 * @author tengxiumin
 * @date 4/24/18
 * @company SDT
 */
public class SMJniApi {

    private JavaChainLog logger = new JavaChainLog();

    private static final int SMJNIAPI_ERR_PARAM = 0x1001;

    static {
        try {
            System.loadLibrary("sdtsmjni");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public byte[] RandomGen(int length) throws Exception {
        byte[] outData = null;
        try {
            outData = nRandomGen(length);
        } catch (Exception e) {
            throw new JavaChainException(getErrorMsg(Integer.parseInt(e.getMessage())));
        }
        return outData;
    }

    private String getErrorMsg(int errorCode) {
        String description;
        switch(errorCode) {
            case SMJNIAPI_ERR_PARAM:
            {
                description = "input parameter error";
                break;
            }
            default:
            {
                description = "unknown error code";
                break;
            }
        }
        String message = String.format("Error code: %d, Error description: %s",
                                        errorCode, description);
        return message;
    }

    private native byte[] nRandomGen(int length);

}
