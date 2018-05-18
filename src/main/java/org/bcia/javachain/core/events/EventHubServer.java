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
package org.bcia.javachain.core.events;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.EventsPackage;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/20
 * @company Dingxuan
 */
public class EventHubServer implements IEventHubServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EventGrpcServer.class);

    @Override
    public EventsPackage.Event chat(EventsPackage.SignedEvent signedEvent) {
        IEventHandler eventHandler = new EventHandler();
        try {
            return eventHandler.handleMessage(signedEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
