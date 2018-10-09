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
package org.bcia.julongchain.common.policies.config;

import org.bcia.julongchain.protos.common.Policies;

/**
 * 策略：大多数子策略需要满足
 *
 * @author zhouhui
 * @date 2018/3/8
 * @company Dingxuan
 */
public class ImplicitMetaMajorityPolicy extends StandardConfigPolicy {
    public ImplicitMetaMajorityPolicy(String key) {
        super(key);

        this.value = buildImplicitMetaPolicy(key, Policies.ImplicitMetaPolicy.Rule.MAJORITY);
    }
}
