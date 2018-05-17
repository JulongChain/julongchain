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
import org.bcia.javachain.protos.node.EventsPackage;

import java.util.Map;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/17
 * @company Dingxuan
 */
public class EventHandler {
    private Map<String, EventsPackage.Interest> interestedEvents;

    public void handleMessage(EventsPackage.SignedEvent signedEvent){
//        validateEventMessage(signedEvent);



    }

    private void validateEventMessage(EventsPackage.SignedEvent signedEvent) throws InvalidProtocolBufferException {
        EventsPackage.Event event = EventsPackage.Event.parseFrom(signedEvent.getEventBytes());

//        event.getCreator()

    }


}
