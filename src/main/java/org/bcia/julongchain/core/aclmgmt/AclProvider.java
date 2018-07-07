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

import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.resourceconfig.IResourcesConfigBundle;
import org.bcia.julongchain.common.resourceconfig.config.IApisConfig;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.core.node.util.NodeUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;

import java.util.List;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/14/18
 * @company Dingxuan
 */
public class AclProvider implements IAclProvider {
    private static JavaChainLog log = JavaChainLogFactory.getLog(AclProvider.class);

    private IAclProvider defaultACLProvider;

    public AclProvider() {
        this.defaultACLProvider = new DefaultACLProvider();
    }

    @Override
    public void checkACL(String resName, String groupId, ProposalPackage.SignedProposal signedProposal)
            throws PolicyException {
        IPolicy policy = getPolicy(groupId, resName);
        if (policy != null) {
            try {
                List<SignedData> signedDataList = SignedData.asSignedData(signedProposal);
                policy.evaluate(signedDataList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new PolicyException(e);
            }
        } else {
            log.warn("Can not find policy: " + groupId + "," + resName);
            defaultACLProvider.checkACL(resName, groupId, signedProposal);
        }
    }

    @Override
    public void checkACL(String resName, String groupId, Common.Envelope envelope) throws PolicyException {
        IPolicy policy = getPolicy(groupId, resName);
        if (policy != null) {
            try {
                List<SignedData> signedDataList = SignedData.asSignedData(envelope);
                policy.evaluate(signedDataList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new PolicyException(e);
            }
        } else {
            log.warn("Can not find policy: " + groupId + "," + resName);
            defaultACLProvider.checkACL(resName, groupId, envelope);
        }
    }

    private IPolicy getPolicy(String groupId, String resName) {
        IResourcesConfigBundle resourcesConfigBundle = NodeUtils.getResourcesConfigBundle(groupId);
        if (resourcesConfigBundle != null) {
            IApisConfig apisConfig = resourcesConfigBundle.getResourcesConfig().getApiConfig();
            if (apisConfig != null) {
                String policyName = apisConfig.getPolicy(resName);

                if (StringUtils.isNotBlank(policyName)) {
                    return resourcesConfigBundle.getPolicyManager().getPolicy(policyName);
                }
            }
        }

        return null;
    }
}
