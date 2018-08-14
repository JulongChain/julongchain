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
package org.bcia.julongchain.csp.gm.sdt.sm3;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.gm.sdt.common.Constants;
import org.bcia.julongchain.csp.gm.sdt.jni.SMJniApi;

/**
 * GM SM3算法
 *
 * @author tengxiumin
 * @date 2018/05/14
 * @company SDT
 */
public class SM3 {

    private static JulongChainLog logger = JulongChainLogFactory.getLog( SM3.class );
    private static SMJniApi smJniApi = new SMJniApi();

    /**
     *摘要长度
     */
    private int digestSize;

    public SM3() {
        digestSize = Constants.SM3_DIGEST_LEN;
    }

    /**
     * 计算消息摘要值
     * @param message 消息数据
     * @return 摘要值
     * @throws JulongChainException
     */
    public byte[] hash(byte[] message) throws JulongChainException {
        byte[] result = null;
        try {
            result = smJniApi.sm3Hash(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new JulongChainException(e.getMessage());
        }
        return result;
    }
}


