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
package org.bcia.julongchain.core.aclmgmt;

import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;

/**
 * AclProvider的模拟测试类,用于部分单元用例
 *
 * @author sunianle
 * @date 4/16/18
 * @company Dingxuan
 */
public class MockAclProvider implements IAclProvider {
    @Override
    public void checkACL(String resName, String groupID, ProposalPackage.SignedProposal idinfo) {

    }

    @Override
    public void checkACL(String resName, String groupId, Common.Envelope envelope) throws PolicyException {

    }

    public void reset(){

    }
}
