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
package org.bcia.julongchain.csp.gm.sdt.random;

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;

/**
 * 产生随机数
 *
 * @author tengxiumin
 * @date 2018/05/16
 * @company SDT
 */
public class GmRandom {

    private static JavaChainLog logger = JavaChainLogFactory.getLog(GmRandom.class);
    private static SMJniApi smJniApi = new SMJniApi();

    /**
     * 生成随机数
     * @param length 随机数长度
     * @return 随机数
     * @throws JavaChainException
     */
    public byte[] rng(int length) throws JavaChainException{
        byte[] result = null;
        try {
            result = smJniApi.randomGen(length);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new JavaChainException(e.getMessage());
        }
        return result;
    }

}
