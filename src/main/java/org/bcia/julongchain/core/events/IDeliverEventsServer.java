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
package org.bcia.julongchain.core.events;

import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.EventsPackage;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/21
 * @company Dingxuan
 */
public interface IDeliverEventsServer {
    EventsPackage.DeliverResponse deliverFiltered(Common.Envelope envelope);

    EventsPackage.DeliverResponse deliver(Common.Envelope envelope);
}
