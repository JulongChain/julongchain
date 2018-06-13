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

import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.node.EventsPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/18
 * @company Dingxuan
 */
public class GenericHandlerList implements IHandlerList {
    private Map<IEventHandler, Boolean> handlers = new HashMap<>();

    @Override
    public boolean add(EventsPackage.Interest interest, EventHandler eventHandler) throws ValidateException {
        ValidateUtils.isNotNull(interest, "interest can not be null");
        ValidateUtils.isNotNull(eventHandler, "eventHandler can not be null");

        synchronized (this) {
            if (handlers.containsKey(eventHandler)) {
                return false;
            } else {
                handlers.put(eventHandler, true);
                return true;
            }
        }
    }

    @Override
    public boolean delete(EventsPackage.Interest interest, EventHandler eventHandler) throws ValidateException {
        ValidateUtils.isNotNull(interest, "interest can not be null");
        ValidateUtils.isNotNull(eventHandler, "eventHandler can not be null");

        synchronized (this) {
            if (!handlers.containsKey(eventHandler)) {
                return false;
            } else {
                handlers.remove(eventHandler, true);
                return true;
            }
        }
    }

    @Override
    public void foreach(EventsPackage.Event event, IHandlerAction action) {
        if (event != null && action != null) {
            synchronized (this) {
                for (IEventHandler eventHandler : handlers.keySet()) {
                    action.doAction(eventHandler);
                }
            }
        }
    }
}