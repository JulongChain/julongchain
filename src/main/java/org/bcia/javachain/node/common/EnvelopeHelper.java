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
package org.bcia.javachain.node.common;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/6
 * @company Dingxuan
 */
public class EnvelopeHelper {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EnvelopeHelper.class);

    public static void sendCreateGroupTransaction() {

    }

    public static void sendTransaction() {

    }

    public static Common.Envelope sanityCheckAndSignConfigTx(Common.Envelope envelope, String gourpId) throws NodeException {
        Common.Payload payload = null;

        try {
            //从Envelope解析出Payload对象
            payload = Common.Payload.newBuilder().mergeFrom(envelope.getPayload()).build();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong payload");
        }

        if (payload == null) {
            throw new NodeException("Missing payload");
        }

        if (payload.getHeader() == null || payload.getHeader().getGroupHeader() == null) {
            throw new NodeException("Missing header");
        }

        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.newBuilder().mergeFrom(payload.getHeader().getGroupHeader()).build();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong header");
        }

        if (groupHeader.getType() != Common.HeaderType.CONFIG_UPDATE_VALUE) {
            throw new NodeException("Wrong header type");
        }

        if (StringUtils.isBlank(groupHeader.getGroupId())) {
            throw new NodeException("Missing group id");
        }

        if (!groupHeader.getGroupId().equals(gourpId)) {
            throw new NodeException("Wrong group id");
        }

        Configtx.ConfigUpdateEnvelope configUpdateEnvelope = null;

        try {
            //从Envelope解析出Payload对象
            configUpdateEnvelope = Configtx.ConfigUpdateEnvelope.newBuilder().mergeFrom(payload.getData()).build();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong config update envelope");
        }

        return null;


    }

    public static Common.Envelope buildSignedEnvelope(int headerType, String groupId) throws NodeException {
        Common.Payload payload = null;
        return null;
    }

    public static Common.Payload buildPayload(int headerType, String groupId) throws NodeException {
        Common.Payload payload = null;
        return null;
    }

    public static Common.GroupHeader buildGroupHeader(int type, int version) {
        Common.GroupHeader.Builder groupHeaderBuilder = Common.GroupHeader.newBuilder();
        groupHeaderBuilder.setType(type);
        groupHeaderBuilder.setVersion(version);
        groupHeaderBuilder.setTimestamp(nowTimestamp());
        return null;

    }

    public static Timestamp nowTimestamp() {
        long millis = System.currentTimeMillis();
        //完成秒和纳秒（即10亿分之一秒）的设置
        return Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
    }


}
