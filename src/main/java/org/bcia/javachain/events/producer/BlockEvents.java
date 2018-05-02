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

import org.bcia.javachain.protos.node.EventsPackage;

/**
 * 类描述
 *
 * @author sunianle
 * @date 4/25/18
 * @company Dingxuan
 */
public class BlockEvents {
    //block event
    EventsPackage.Event bevent;
    //filtered block event
    EventsPackage.Event fbevent;
    String groupID;


    public BlockEvents(EventsPackage.Event bevent, EventsPackage.Event fbevent, String groupID) {
        this.bevent = bevent;
        this.fbevent = fbevent;
        this.groupID = groupID;
    }


}
