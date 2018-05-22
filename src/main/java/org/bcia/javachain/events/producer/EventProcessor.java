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
package org.bcia.javachain.events.producer;

import org.bcia.javachain.common.exception.EventException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.protos.node.EventsPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/17
 * @company Dingxuan
 */
public class EventProcessor implements IEventProcessor {
    private Map<Integer, IHandlerList> eventConsumers;
    private EventsServerConfig eventsServerConfig;

    public EventProcessor(EventsServerConfig eventsServerConfig) {
        this.eventsServerConfig = eventsServerConfig;

        this.eventConsumers = new HashMap<>();
        //TODO:注册类消息不需要？
        eventConsumers.put(EventsPackage.EventType.REGISTER_VALUE, new GenericHandlerList());
        eventConsumers.put(EventsPackage.EventType.BLOCK_VALUE, new GenericHandlerList());
        eventConsumers.put(EventsPackage.EventType.SMART_CONTRACT_VALUE, new SmartContractHandlerList());
        eventConsumers.put(EventsPackage.EventType.REJECTION_VALUE, new GenericHandlerList());
        eventConsumers.put(EventsPackage.EventType.FILTEREDBLOCK_VALUE, new GenericHandlerList());
    }

    @Override
    public Map<Integer, IHandlerList> getEventConsumers() {
        return eventConsumers;
    }

    public void doProcess(EventsPackage.Event event) throws EventException {
        int eventType = getMessageType(event);

        if (!eventConsumers.containsKey(eventType)) {
            throw new EventException("Event type has not consumer");
        }

        IHandlerList handlerList = eventConsumers.get(eventType);
        handlerList.foreach(event, new IHandlerList.IHandlerAction() {
            @Override
            public void doAction(IEventHandler handler) {
                if (!EventsUtils.hasSessionExpired(handler.getSessionEndDate())) {
                    handler.sendMessage(event);
                }
            }
        });

    }

    /**
     * 判断事件类型
     *
     * @param event
     * @return
     */
    private int getMessageType(EventsPackage.Event event) {
        int eventType = -1;
        if (event.hasRegister()) {
            eventType = EventsPackage.EventType.REGISTER_VALUE;
        } else if (event.hasBlock()) {
            eventType = EventsPackage.EventType.BLOCK_VALUE;
        } else if (event.hasSmartcontractEvent()) {
            eventType = EventsPackage.EventType.SMART_CONTRACT_VALUE;
        } else if (event.hasRejection()) {
            eventType = EventsPackage.EventType.REJECTION_VALUE;
        } else if (event.hasFilteredBlock()) {
            eventType = EventsPackage.EventType.FILTEREDBLOCK_VALUE;
        }

        return eventType;
    }

    public void send(EventsPackage.Event event) throws ValidateException {
        ValidateUtils.isNotNull(event, "event can not be null");


    }
}
