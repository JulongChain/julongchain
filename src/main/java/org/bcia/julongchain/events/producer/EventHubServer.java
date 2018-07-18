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
package org.bcia.julongchain.events.producer;

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.exception.VerifyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.node.EventsPackage;

/**
 * 事件中心服务
 *
 * @author zhouhui
 * @date 2018/3/20
 * @company Dingxuan
 */
public class EventHubServer implements IEventHubServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EventHubServer.class);
    private IEventProcessor eventProcessor;

    public interface Callback {
        void sendMessage(EventsPackage.Event event);
    }

    public EventHubServer(EventsServerConfig config) {
        eventProcessor = EventProcessor.getInstance(config);
    }

    @Override
    public EventsPackage.Event chat(EventsPackage.SignedEvent signedEvent, StreamObserver<EventsPackage.Event>
            responseObserver) {
        IEventHandler eventHandler = new EventHandler(eventProcessor, new Callback() {
            @Override
            public void sendMessage(EventsPackage.Event event) {
                //往客户端发送一个消息
                if (responseObserver != null) {
                    responseObserver.onNext(event);
                }
            }
        });

        try {
            //处理消息，其实是客户端向服务器注册订阅某一类消息
            return eventHandler.handleMessage(signedEvent);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
        } catch (VerifyException e) {
            log.error(e.getMessage(), e);
        } catch (MspException e) {
            e.printStackTrace();
        }

        return null;
    }
}