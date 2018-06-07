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
import io.netty.channel.ChannelConfig;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.GroupConfigBundle;
import org.bcia.javachain.common.policies.*;
import org.bcia.javachain.consenter.common.multigroup.ChainSupport;
import org.bcia.javachain.consenter.consensus.IChain;
import org.bcia.javachain.consenter.entity.ChainEntity;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.protos.common.Configtx;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 类描述
 * 提供通道策略管理
 * @author yuanjun
 * @date 21/05/18
 * @company Aisino
 */
public class GroupPolicyManager implements IGroupPolicyManagerGetter {
    @Override
    public IPolicyManager getPolicyManager(String groupId) throws InvalidProtocolBufferException, PolicyException {
        Configtx.Config config =  Configtx.Config.newBuilder().build();
        GroupConfigBundle groupConfigBundle = null;
        try {
            groupConfigBundle = new GroupConfigBundle(groupId,config);
        } catch (ValidateException e) {
            e.printStackTrace();
        }
        return groupConfigBundle.getPolicyManager();
    }


}
