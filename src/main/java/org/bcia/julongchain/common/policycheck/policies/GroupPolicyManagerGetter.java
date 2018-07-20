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

package org.bcia.julongchain.common.policycheck.policies;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigBundle;
import org.bcia.julongchain.common.policies.*;
import org.bcia.julongchain.consenter.common.multigroup.ChainSupport;
import org.bcia.julongchain.core.RWMutex;
import org.bcia.julongchain.core.node.GroupSupport;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.entity.Group;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * 类描述
 * 提供通道策略管理
 * @author yuanjun
 * @date 21/05/18
 * @company Aisino
 */
public class GroupPolicyManagerGetter implements IGroupPolicyManagerGetter {
    /**
     * 通过groupid获取策略管理的对象
     *
     * @param groupId
     * @return
     * @throws InvalidProtocolBufferException
     * @throws PolicyException
     */
    @Override
    public IPolicyManager getPolicyManager(String groupId) throws InvalidProtocolBufferException, PolicyException {
        GroupConfigBundle groupConfigBundle = null;
        ChainSupport chainSupport = new ChainSupport();
        try {
            groupConfigBundle = chainSupport.createBundle(groupId, Configtx.Config.getDefaultInstance());
        } catch (ValidateException e) {
            e.printStackTrace();
        }
        return groupConfigBundle.getPolicyManager();

    }

}
