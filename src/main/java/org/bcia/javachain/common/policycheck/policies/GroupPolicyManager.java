/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.javachain.common.policycheck.policies;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.policies.IPolicyProvider;
import org.bcia.javachain.common.policies.PolicyConstant;
import org.bcia.javachain.common.policies.PolicyManager;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 * 提供通道策略管理
 * @author yuanjun
 * @date 21/05/18
 * @company Aisino
 */
public class GroupPolicyManager implements IGroupPolicyManagerGetter {
    @Override
    public PolicyManager getPolicyManager(String channelID) throws InvalidProtocolBufferException, PolicyException {
        Map<Integer, IPolicyProvider> providers = new HashMap<Integer, IPolicyProvider>();
        Configtx.ConfigTree rootTree = Configtx.ConfigTree.getDefaultInstance();
        return new PolicyManager(PolicyConstant.GROUP_APP_ADMINS,providers,rootTree);
    }


}
