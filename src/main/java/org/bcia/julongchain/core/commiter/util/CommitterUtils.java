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
package org.bcia.julongchain.core.commiter.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.protos.common.Common;

/**
 * 提交节点工具类
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class CommitterUtils {
    /**
     * 是否为配置区块
     *
     * @param block
     * @return
     * @throws InvalidProtocolBufferException
     * @throws ValidateException
     */
    public static boolean isConfigBlock(Common.Block block) throws InvalidProtocolBufferException, ValidateException {
        //获取第一个信封对象(配置交易单独成块，所以获取第一个信封就行)
        Common.Envelope envelope = BlockUtils.extractEnvelope(block, 0);
        if (envelope == null) {
            return false;
        }

        //获取负载对象
        Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
        if (payload == null) {
            return false;
        }

        if (payload.getHeader() == null || payload.getHeader().getGroupHeader() == null) {
            return false;
        }

        //获取群组头部
        Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        if (groupHeader == null) {
            return false;
        }

        //当且仅当头部类型为CONFIG时为配置区块
        return groupHeader.getType() == Common.HeaderType.CONFIG_VALUE;
    }
}
