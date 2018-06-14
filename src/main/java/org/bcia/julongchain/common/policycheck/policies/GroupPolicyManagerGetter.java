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
import org.bcia.julongchain.common.policies.*;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.entity.Group;

/**
 * 类描述
 * 提供通道策略管理
 * @author yuanjun
 * @date 21/05/18
 * @company Aisino
 */
public class GroupPolicyManagerGetter implements IGroupPolicyManagerGetter {
    @Override
    public IPolicyManager getPolicyManager(String groupId) throws InvalidProtocolBufferException, PolicyException {
        Group group = null;
        try {
            group = Node.getInstance().getGroupMap().get(groupId);
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return group.getGroupSupport().getGroupConfigBundle().getPolicyManager();

    }

}
