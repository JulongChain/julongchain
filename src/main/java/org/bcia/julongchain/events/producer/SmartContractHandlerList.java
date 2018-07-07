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

import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.node.EventsPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/18
 * @company Dingxuan
 */
public class SmartContractHandlerList implements IHandlerList {
    private Map<String, Map<String, List<IEventHandler>>> handlers = new HashMap<>();

    @Override
    public boolean add(EventsPackage.Interest interest, EventHandler eventHandler) throws ValidateException {
        ValidateUtils.isNotNull(interest, "interest can not be null");
        ValidateUtils.isNotNull(eventHandler, "eventHandler can not be null");
        ValidateUtils.isNotNull(interest.getSmartContractRegInfo(), "smartContractRegInfo can not be null");

        String scId = interest.getSmartContractRegInfo().getSmartContractId();
        ValidateUtils.isNotBlank(scId, "smartContractId can not be empty");

        String eventName = interest.getSmartContractRegInfo().getEventName();
        ValidateUtils.isNotBlank(eventName, "eventName can not be empty");

        synchronized (this) {
            Map<String, List<IEventHandler>> eventHandlerMap = null;
            if (!handlers.containsKey(scId)) {
                eventHandlerMap = new HashMap<String, List<IEventHandler>>();
                handlers.put(scId, eventHandlerMap);
            } else {
                eventHandlerMap = handlers.get(scId);
            }

            List<IEventHandler> eventHandlerList = null;
            if (!eventHandlerMap.containsKey(eventName)) {
                eventHandlerList = new ArrayList<>();
                eventHandlerMap.put(eventName, eventHandlerList);
            } else {
                eventHandlerList = eventHandlerMap.get(eventName);
            }

            if (!eventHandlerList.contains(eventHandler)) {
                eventHandlerList.add(eventHandler);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean delete(EventsPackage.Interest interest, EventHandler eventHandler) throws ValidateException {
        ValidateUtils.isNotNull(interest, "interest can not be null");
        ValidateUtils.isNotNull(eventHandler, "eventHandler can not be null");
        ValidateUtils.isNotNull(interest.getSmartContractRegInfo(), "smartContractRegInfo can not be null");

        String scId = interest.getSmartContractRegInfo().getSmartContractId();
        ValidateUtils.isNotBlank(scId, "smartContractId can not be empty");

        String eventName = interest.getSmartContractRegInfo().getEventName();
        ValidateUtils.isNotBlank(eventName, "eventName can not be empty");

        synchronized (this) {
            Map<String, List<IEventHandler>> eventHandlerMap = null;
            if (!handlers.containsKey(scId)) {
                return false;
            } else {
                eventHandlerMap = handlers.get(scId);
            }

            List<IEventHandler> eventHandlerList = null;
            if (!eventHandlerMap.containsKey(eventName)) {
                return false;
            } else {
                eventHandlerList = eventHandlerMap.get(eventName);
            }

            if (!eventHandlerList.contains(eventHandler)) {
                return false;
            } else {
                eventHandlerList.remove(eventHandler);

                if (eventHandlerList.size() <= 0) {
                    eventHandlerMap.remove(eventName);
                }

                if (eventHandlerMap.size() <= 0) {
                    handlers.remove(scId);
                }

                return true;
            }
        }
    }

    @Override
    public void foreach(EventsPackage.Event event, IHandlerAction action) {
        if (event.getSmartContractEvent() != null && action != null) {
            String scId = event.getSmartContractEvent().getSmartContractId();
            String eventName = event.getSmartContractEvent().getEventName();
            synchronized (this) {
                if (StringUtils.isNotBlank(scId) && StringUtils.isNotBlank(eventName) && handlers.containsKey(scId)) {
                    Map<String, List<IEventHandler>> eventHandlerMap = handlers.get(scId);

                    if (eventHandlerMap.containsKey(eventName)) {
                        List<IEventHandler> eventHandlerList = eventHandlerMap.get(eventName);
                        for (IEventHandler eventHandler : eventHandlerList) {
                            action.doAction(eventHandler);
                        }
                    }
                }
            }
        }
    }
}
