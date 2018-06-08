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
package org.bcia.julongchain.consenter.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.consenter.Kafka;

/**
 * 类描述
 *
 * @author
 * @date 2018/5/2
 * @company Shudun
 */

public class Utils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Utils.class);

    public void ChannelHeader(Common.Envelope env) {
        Common.Payload envPayload = UnmarshalPayload(env.toByteArray());
        if (envPayload.getHeader() == null) {
        }
        //TODO 检查envPayload.Header.ChannelHeader
        //TODO 执行UnmarshalChannelHeader()
        //TODO 返回ChannelHeader对象
    }

    public Common.Payload UnmarshalPayload(byte[] encoded) {
        Common.Payload payload = Common.Payload.newBuilder().build();
        //TODO payload进行赋值
        return payload;

    }

    public static byte[] MarshalOrPanic(Kafka.KafkaMetadata kafkaMetadata) {
        byte[] data = kafkaMetadata.toByteArray();
        return data;
    }


    public static Common.Payload unmarshalPayload(byte[] encoded) {
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(encoded);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return payload;
    }


    public static Common.GroupHeader unmarshalGroupHeader(byte[] bytes) {
        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return groupHeader;
    }


    public static Common.Envelope unmarshalEnvelope(byte[] encoded) {
        Common.Envelope envelope = null;
        try {
            envelope = Common.Envelope.parseFrom(encoded);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return envelope;
    }

    public static byte[] marshalOrPanic(Message pb) {
        byte[] data = pb.toByteArray();
        return data;
    }

    public static Configtx.ConfigEnvelope unmarshalConfigEnvelope(byte[] data) {
        Configtx.ConfigEnvelope configEnvelope = null;
        try {
            configEnvelope = Configtx.ConfigEnvelope.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return configEnvelope;

    }

    public static byte [] concatenateBytes(byte[]... data) {
        int length_byte = 0;
        for (int i = 0; i < data.length; i++) {
            length_byte += data[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < data.length; i++) {
            byte[] b = data[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }




}
