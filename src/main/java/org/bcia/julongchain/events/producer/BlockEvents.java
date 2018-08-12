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

import org.bcia.julongchain.protos.node.EventsPackage;

/**
 * 类描述
 *
 * @author sunianle
 * @date 2018/04/25
 * @company Dingxuan
 */
public class BlockEvents {
    /**
     *
     */
    private EventsPackage.Event blockEvent;
    /**
     *
     */
    private EventsPackage.Event filteredBlockEvent;

    private String groupId;

    public BlockEvents(EventsPackage.Event blockEvent, EventsPackage.Event filteredBlockEvent, String groupId) {
        this.blockEvent = blockEvent;
        this.filteredBlockEvent = filteredBlockEvent;
        this.groupId = groupId;
    }

    public EventsPackage.Event getBlockEvent() {
        return blockEvent;
    }

    public EventsPackage.Event getFilteredBlockEvent() {
        return filteredBlockEvent;
    }

    public String getGroupId() {
        return groupId;
    }
}
