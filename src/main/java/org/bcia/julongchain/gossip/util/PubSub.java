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
package org.bcia.julongchain.gossip.util;

import org.omg.CORBA.OBJECT_NOT_EXIST;

import java.util.Map;
import java.util.Set;

/**
 * PubSub 定义结构:
 * - 向多个订阅用户发表主题
 * - 订阅主题
 * 订阅拥有TTL，通过后会被清除
 * @author wanliangbing
 * @date 18-7-24
 * @company Dingxuan
 */
public class PubSub {

    /**
     * 从主题到订阅集合的表
     */
    private Map<String, Set<Object>> subscriptions;

    public Map<String, Set<Object>> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Map<String, Set<Object>> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
