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
package org.bcia.javachain.events.consumer;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.protos.node.EventsGrpc;
import org.bcia.javachain.protos.node.EventsPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/19
 * @company Dingxuan
 */
public class EventsClient {
    /**
     * IP地址或主机地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;

    private long regTimeout;
    private IEventAdapter eventAdapter;

    public EventsClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public EventsClient(String host, int port, long regTimeout, IEventAdapter eventAdapter) {
        //确保事件在100ms和60s之间
        if (regTimeout < 100) {
            regTimeout = 100;
        } else if (regTimeout > 60000) {
            regTimeout = 60000;
        }

        this.eventAdapter = eventAdapter;
        this.host = host;
        this.port = port;
        this.regTimeout = regTimeout;
    }

    public void sendChat(EventsPackage.SignedEvent signedEvent, StreamObserver<EventsPackage.Event> responseObserver) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
        EventsGrpc.EventsStub stub = EventsGrpc.newStub(managedChannel);
        StreamObserver<EventsPackage.SignedEvent> streamObserver = stub.chat(responseObserver);
        streamObserver.onNext(signedEvent);
    }

    /**
     * 使用本地MSP来签名事件
     *
     * @param event
     * @return
     */
    public EventsPackage.SignedEvent localSignEvent(EventsPackage.Event event) {
        IMsp localMsp = GlobalMspManagement.getLocalMsp();

        ISigningIdentity signingIdentity = localMsp.getDefaultSigningIdentity();

        byte[] creator = signingIdentity.serialize();

        EventsPackage.Event.Builder eventBuilder = EventsPackage.Event.newBuilder(event);
        eventBuilder.setCreator(ByteString.copyFrom(creator));
        EventsPackage.Event newEvent = eventBuilder.build();

        byte[] eventBytes = newEvent.toByteArray();
        byte[] signature = signingIdentity.sign(eventBytes);

        EventsPackage.SignedEvent.Builder signedEventBuilder = EventsPackage.SignedEvent.newBuilder();
        signedEventBuilder.setEventBytes(ByteString.copyFrom(eventBytes));
        signedEventBuilder.setSignature(ByteString.copyFrom(signature));
        return signedEventBuilder.build();
    }
}
