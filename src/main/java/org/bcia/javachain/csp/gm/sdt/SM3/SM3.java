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
package org.bcia.javachain.csp.gm.sdt.SM3;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.sdt.common.Constants;
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;

/**
 * GM SM3 algorithm
 *
 * @author tengxiumin
 * @date 2018/05/14
 * @company SDT
 */
public class SM3 {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog( SM3.class );
    private static final SMJniApi smJniApi = new SMJniApi();
    /**
     *摘要长度
     */
    private int digestSize;

    public SM3() {
        digestSize = Constants.SM3_DIGEST_LEN;
    }

    public byte[] hash(byte[] message) {
        if(null == message) {
            logger.error("Invalid message. It must not be nil.");
            return null;
        }
        byte[] result = null;
        try {
            result = smJniApi.SM3Hash(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }
}


