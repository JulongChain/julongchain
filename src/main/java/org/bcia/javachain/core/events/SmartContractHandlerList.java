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

import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.protos.node.EventsPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/18
 * @company Dingxuan
 */
public class SmartContractHandlerList implements IHandlerList {
    private Map<String, Map<String, IEventHandler>> handlers = new HashMap<String, Map<String, IEventHandler>>();

    @Override
    public boolean add(EventsPackage.Interest interest, EventHandler eventHandler) throws ValidateException {
        ValidateUtils.isNotNull(interest, "interest can not be null");
        ValidateUtils.isNotNull(eventHandler, "eventHandler can not be null");
        ValidateUtils.isNotNull(interest.getSmartcontractRegInfo(), "smartcontractRegInfo can not be null");

        String scId = interest.getSmartcontractRegInfo().getSmartcontractId();
        ValidateUtils.isNotBlank(scId, "smartcontractId can not be empty");

        String eventName = interest.getSmartcontractRegInfo().getEventName();
        ValidateUtils.isNotBlank(eventName, "eventName can not be empty");

        synchronized (this) {
            Map<String, IEventHandler> eventHandlerMap = null;
            if (!handlers.containsKey(scId)) {
                eventHandlerMap = new HashMap<String, IEventHandler>();
                handlers.put(scId, eventHandlerMap);
            } else {
                eventHandlerMap = handlers.get(scId);
            }

            if (!eventHandlerMap.containsKey(eventName)) {
                eventHandlerMap.put(eventName, eventHandler);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean delete(EventsPackage.Interest interest, EventHandler eventHandler) {
        return false;
    }

    @Override
    public void foreach(EventsPackage.Event event, IHandlerAction action) {

    }
}
