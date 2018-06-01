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
package org.bcia.javachain.common.resourceconfig;

import org.bcia.javachain.common.configtx.IValidator;
import org.bcia.javachain.common.policies.IManager;

/**
 * Resources defines a way to query peer resources associated with a channel.
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public interface IResources {

    /** ConfigtxValidator returns a reference to a configtx.Validator which can process updates to this config.
     *
     * @return
     */
    IValidator configtxValidator();

    /** PolicyManager returns a policy getPolicyManager which can resolve names both in the /Channel and /Resources namespaces.
     * Note, the result of this method is almost definitely the one you want.  Calling ChannelConfig().PolicyManager()
     * will return a policy getPolicyManager which can only resolve policies in the /Channel namespace.
     * @return
     */
    IManager managerPolicyManager();

    /** APIPolicyMapper returns a way to map API names to policies governing their invocation.
     *
     * @return
     */
    IPolicyMapper APIPolicyMapper();

    /** ChaincodeRegistery returns a way to query for chaincodes defined in this channel.
     *
     * @return
     */
    ISmartContractRegistry chaincodeRegistry();

    /** ChannelConfig returns the channelconfig.Resources which this config depends on.
     *
     * @return
     */
    org.bcia.javachain.common.channelconfig.IResources resourcesChannelConfig();

}
