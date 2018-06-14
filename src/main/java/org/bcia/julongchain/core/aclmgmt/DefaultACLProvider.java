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

import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.policies.PolicyConstant;
import org.bcia.julongchain.common.policycheck.IPolicyChecker;
import org.bcia.julongchain.common.policycheck.PolicyChecker;
import org.bcia.julongchain.common.policycheck.policies.GroupPolicyManagerGetter;
import org.bcia.julongchain.common.policycheck.policies.IGroupPolicyManagerGetter;
import org.bcia.julongchain.core.aclmgmt.resources.Resources;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.msp.mgmt.MSPPrincipalGetter;
import org.bcia.julongchain.protos.node.ProposalPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/13
 * @company Dingxuan
 */
public class DefaultACLProvider implements IAclProvider {
    private IPolicyChecker policyChecker;

    private Map<String, String> resourcePolicyMap = new HashMap<>();

    private Map<String, String> groupPolicyMap = new HashMap<>();

    public DefaultACLProvider() {
        init();
    }

    private void init() {
        policyChecker = new PolicyChecker(new GroupPolicyManagerGetter(), GlobalMspManagement.getLocalMsp(),
                new MSPPrincipalGetter());

        //LSSC
        resourcePolicyMap.put(Resources.LSSC_INSTALL, "");
        resourcePolicyMap.put(Resources.LSSC_GETCHAINCODES, "");
        resourcePolicyMap.put(Resources.LSSC_GETINSTALLEDCHAINCODES, "");

        groupPolicyMap.put(Resources.LSSC_DEPLOY, "");
        groupPolicyMap.put(Resources.LSSC_UPGRADE, "");
        groupPolicyMap.put(Resources.LSSC_GETSCINFO, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.LSSC_GETDEPSPEC, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.LSSC_GETSCDATA, PolicyConstant.GROUP_APP_READERS);

        //QSSC
        groupPolicyMap.put(Resources.QSSC_GetChainInfo, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.QSSC_GetBlockByNumber, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.QSSC_GetBlockByHash, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.QSSC_GetTransactionByID, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.QSSC_GetBlockByTxID, PolicyConstant.GROUP_APP_READERS);

        //CSSC
        resourcePolicyMap.put(Resources.CSSC_JoinChain, "");
        resourcePolicyMap.put(Resources.CSSC_GetChannels, "");

        groupPolicyMap.put(Resources.CSSC_GetConfigBlock, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.CSSC_GetConfigTree, PolicyConstant.GROUP_APP_READERS);
        groupPolicyMap.put(Resources.CSSC_SimulateConfigTreeUpdate, PolicyConstant.GROUP_APP_WRITERS);



    }

    @Override
    public void checkACL(String resName, String groupID, ProposalPackage.SignedProposal idinfo) throws JavaChainException {

    }
}
