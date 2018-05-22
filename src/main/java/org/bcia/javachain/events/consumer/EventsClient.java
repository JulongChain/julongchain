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
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.csp.factory.CspManager;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.protos.node.EventsGrpc;
import org.bcia.javachain.protos.node.EventsPackage;

import java.io.IOException;
import java.util.List;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/19
 * @company Dingxuan
 */
public class EventsClient {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EventsClient.class);

    private static final long MIN_REGISTER_TIME = 100L;
    private static final long MAX_REGISTER_TIME = 60000L;

    /**
     * IP地址或主机地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;

    private long registerTimeout;
    private IEventAdapter eventAdapter;

    public EventsClient(String host, int port, long registerTimeout, IEventAdapter eventAdapter) {
        //确保事件在100ms和60s之间
        if (registerTimeout < MIN_REGISTER_TIME) {
            registerTimeout = MIN_REGISTER_TIME;
        } else if (registerTimeout > MAX_REGISTER_TIME) {
            registerTimeout = MAX_REGISTER_TIME;
        }

        this.eventAdapter = eventAdapter;
        this.host = host;
        this.port = port;
        this.registerTimeout = registerTimeout;
    }

    private void sendChat(EventsPackage.SignedEvent signedEvent, StreamObserver<EventsPackage.Event> responseObserver) {
        //TODO
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
    private EventsPackage.SignedEvent localSignEvent(EventsPackage.Event event) {
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

    /**
     * 签名事件
     *
     * @param event
     * @param signingIdentity
     * @return
     */
    private EventsPackage.SignedEvent signEvent(EventsPackage.Event event, ISigningIdentity signingIdentity) {
        byte[] eventBytes = event.toByteArray();
        byte[] signature = signingIdentity.sign(eventBytes);

        EventsPackage.SignedEvent.Builder signedEventBuilder = EventsPackage.SignedEvent.newBuilder();
        signedEventBuilder.setEventBytes(ByteString.copyFrom(eventBytes));
        signedEventBuilder.setSignature(ByteString.copyFrom(signature));
        return signedEventBuilder.build();
    }

    public void registerAsync(RegistrationConfig config) {
        IMsp localMsp = GlobalMspManagement.getLocalMsp();
        ISigningIdentity signingIdentity = localMsp.getDefaultSigningIdentity();
        byte[] creator = signingIdentity.serialize();

        EventsPackage.Register.Builder registerBuilder = EventsPackage.Register.newBuilder();
        registerBuilder.addAllEvents(config.getInterestedEvents()).build();

        EventsPackage.Event.Builder eventBuilder = EventsPackage.Event.newBuilder();
        eventBuilder.setRegister(registerBuilder.build());
        eventBuilder.setCreator(ByteString.copyFrom(creator));

        Timestamp timestamp = buildTimestamp(config.getTimestamp().getTime() / 1000, config.getTimestamp().getNanos());
        eventBuilder.setTimestamp(timestamp);

        try {
            byte[] contentBytes = config.getX509Certificate().getEncoded();
            byte[] hashBytes = CspManager.getDefaultCsp().hash(contentBytes, null);
            eventBuilder.setTlsCertHash(ByteString.copyFrom(hashBytes));
        } catch (IOException e) {
            //TODO
            log.error(e.getMessage(), e);
        } catch (JavaChainException e) {
            //TODO
            log.error(e.getMessage(), e);
        }

        EventsPackage.Event event = eventBuilder.build();
        EventsPackage.SignedEvent signedEvent = signEvent(event, signingIdentity);
        sendChat(signedEvent, new StreamObserver<EventsPackage.Event>() {
            @Override
            public void onNext(EventsPackage.Event value) {
                processEvent(value);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    private Timestamp buildTimestamp(long second, int nano) {
        Timestamp.Builder timestampBuilder = Timestamp.newBuilder();
        timestampBuilder.setSeconds(second);
        timestampBuilder.setNanos(nano);
        return timestampBuilder.build();
    }

    public void unRegisterAsync(RegistrationConfig config) {
        IMsp localMsp = GlobalMspManagement.getLocalMsp();
        ISigningIdentity signingIdentity = localMsp.getDefaultSigningIdentity();
        byte[] creator = signingIdentity.serialize();

        EventsPackage.Unregister.Builder unregisterBuilder = EventsPackage.Unregister.newBuilder();
        unregisterBuilder.addAllEvents(config.getInterestedEvents()).build();

        EventsPackage.Event.Builder eventBuilder = EventsPackage.Event.newBuilder();
        eventBuilder.setUnregister(unregisterBuilder.build());
        eventBuilder.setCreator(ByteString.copyFrom(creator));

        EventsPackage.Event event = eventBuilder.build();
        EventsPackage.SignedEvent signedEvent = signEvent(event, signingIdentity);
        sendChat(signedEvent, null);
    }

    private void processEvent(EventsPackage.Event event) {
        if (eventAdapter != null) {
            eventAdapter.receive(event);
        }
    }

    public void start() throws ValidateException {
        ValidateUtils.isNotNull(eventAdapter, "eventAdapter can not be null");

        List<EventsPackage.Interest> interestedEvents = eventAdapter.getInterestedEvents();
        if (interestedEvents == null || interestedEvents.size() <= 0) {
            throw new ValidateException("interestedEvents is empty");
        }

        RegistrationConfig config = new RegistrationConfig();
        config.setInterestedEvents(interestedEvents);
        config.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
        registerAsync(config);
    }
}