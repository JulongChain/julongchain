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
package org.bcia.julongchain.core.events;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.EventsPackage;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/21
 * @company Dingxuan
 */
public class DeliverHandler implements IDeliverHandler {
    private JavaChainLog log = JavaChainLogFactory.getLog(DeliverHandler.class);

    @Override
    public EventsPackage.DeliverResponse handle(Common.Envelope envelope) {


        return null;
    }

    public EventsPackage.DeliverResponse validateEnvelope(Common.Envelope envelope) {
        if (envelope == null) {
            log.warn("Missing envelope");
            return buildStatusResponse(Common.Status.BAD_REQUEST);
        }

        if (envelope.getPayload() == null) {
            log.warn("Missing payload");
            return buildStatusResponse(Common.Status.BAD_REQUEST);
        }

        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(envelope.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.warn("Wrong payload" + e.getMessage(), e);
            return buildStatusResponse(Common.Status.BAD_REQUEST);
        }

        if (payload.getHeader() == null || payload.getHeader().getGroupHeader() == null) {
            log.warn("Missing payload.header");
            return buildStatusResponse(Common.Status.BAD_REQUEST);
        }

        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            log.warn("Wrong groupHeader" + e.getMessage(), e);
            return buildStatusResponse(Common.Status.BAD_REQUEST);
        }

        if (groupHeader.hasTimestamp()) {
            log.warn("Missing timestamp");
            return buildStatusResponse(Common.Status.BAD_REQUEST);
        }

        //TODO：判断时间间隔
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime();
//        if(System.currentTimeMillis() - )
        return null;


    }

    /**
     * 构造错误状态响应
     *
     * @param status
     * @return
     */
    public EventsPackage.DeliverResponse buildStatusResponse(Common.Status status) {
        EventsPackage.DeliverResponse.Builder responseBuilder = EventsPackage.DeliverResponse.newBuilder();
        responseBuilder.setStatus(status);
        return responseBuilder.build();
    }
}
