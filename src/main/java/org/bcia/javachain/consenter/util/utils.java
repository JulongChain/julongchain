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
package org.bcia.javachain.consenter.util;

import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Kafka;

/**
 * 类描述
 *
 * @author
 * @date 2018/5/2
 * @company Shudun
 */

public  class utils {

    public void ChannelHeader(Common.Envelope env){
        Common.Payload envPayload=UnmarshalPayload(env.toByteArray());
        if(envPayload.getHeader()==null){
        }
        //TODO 检查envPayload.Header.ChannelHeader
        //TODO 执行UnmarshalChannelHeader()
        //TODO 返回ChannelHeader对象
    }

    public Common.Payload  UnmarshalPayload(byte[] encoded){
       Common.Payload payload=Common.Payload.newBuilder().build();
        //TODO payload进行赋值
        return payload;

    }
    public static byte[] MarshalOrPanic(Kafka.KafkaMetadata kafkaMetadata){
        byte[] data=kafkaMetadata.toByteArray();
        return data;
    }
}
