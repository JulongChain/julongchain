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
import org.bcia.julongchain.protos.node.EventsPackage;

import java.util.Date;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/17
 * @company Dingxuan
 */
public class EventsUtils {
    public static void registerHandler(IEventProcessor eventProcessor, EventsPackage.Interest interest, EventHandler
            eventHandler) throws ValidateException {
        synchronized (eventProcessor) {
            IHandlerList handlerList = eventProcessor.getEventConsumers().get(interest.getEventTypeValue());
            if (handlerList != null) {
                handlerList.add(interest, eventHandler);
            }
        }
    }

    public static void deRegisterHandler(IEventProcessor eventProcessor, EventsPackage.Interest interest,
                                         EventHandler eventHandler) throws ValidateException {
        synchronized (eventProcessor) {
            IHandlerList handlerList = eventProcessor.getEventConsumers().get(interest.getEventTypeValue());
            if (handlerList != null) {
                handlerList.delete(interest, eventHandler);
            }
        }
    }

    /**
     * 是否过期
     *
     * @param sessionEndDate
     * @return
     */
    public static boolean hasSessionExpired(Date sessionEndDate) {
        return sessionEndDate != null && new Date().after(sessionEndDate);
    }
}
