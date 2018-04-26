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
package org.bcia.javachain.common.util.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.protos.common.Common;

/**
 * 类描述
 *
 * @author sunianle
 * @date 4/3/18
 * @company Dingxuan
 */
public class BlockUtils {
    public static Common.Block getBlockFromBlockBytes(byte[] blockBytes) throws InvalidProtocolBufferException {
        return Common.Block.parseFrom(blockBytes);
    }

    public static String getGroupIDFromBlock(Common.Block block) throws JavaChainException{
        Common.Envelope envelope = null;
        Common.Payload payload = null;
        Common.GroupHeader gh = null;
        try {
            if(block == null || block.getData() == null || block.getData().getDataCount() == 0){
                return null;
            }
            envelope = Common.Envelope.parseFrom(block.getData().getData(0));
            if(envelope == null || envelope.getPayload() == null){
                return null;
            }
            payload = Common.Payload.parseFrom(envelope.getPayload());
            if(payload.getHeader() == null || payload.getHeader().getGroupHeader() == null){
                return null;
            }
            gh = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
            if(gh == null || gh.getGroupId() == null){
                return null;
            }
        } catch (InvalidProtocolBufferException e) {
            throw new JavaChainException(e);
        }
        return gh.getGroupId();
    }

    public static Common.Envelope extractEnvelope(Common.Block block,int index)throws  JavaChainException{
        return null;
    }
}
