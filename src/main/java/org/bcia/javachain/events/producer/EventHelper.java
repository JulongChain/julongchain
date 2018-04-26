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
import org.bcia.javachain.protos.common.Common;

/**
 * 类描述
 *
 * @author sunianle
 * @date 4/25/18
 * @company Dingxuan
 */
public class EventHelper {
    /**
     * createBlockEvents creates block events for a block. It removes the RW set
     * and creates a block event and a filtered block event. Sending the events
     * is the responsibility of the code that calls this function.
     * @param block
     * @return
     * @throws EventException
     */
    public static BlockEvents createBlockEvents(Common.Block block)throws EventException{
        return null;
    }

    /**
     * send sends the event to interested consumers
     * @param events
     * @throws EventException
     */
    public static void send(BlockEvents events) throws EventException{
    }
}
