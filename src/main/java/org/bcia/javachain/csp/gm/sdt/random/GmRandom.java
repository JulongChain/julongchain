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
package org.bcia.javachain.csp.gm.sdt.random;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.sdt.jni.SMJniApi;

/**
 * Generate Random Data
 *
 * @author tengxiumin
 * @date 18/5/16
 * @company SDT
 */
public class GmRandom {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(GmRandom.class);
    private static final SMJniApi smJniApi = new SMJniApi();

    public byte[] rng(int len) {

        //判断随机数长度是否为非负整数
        if(len <= 0) {
            return null;
        }
        byte[] result = null;
        try {
            result = smJniApi.RandomGen(len);
        } catch (Exception e) {
            logger.error("SM RandomGen error: generate random failed");
        }
        return result;
    }

}
