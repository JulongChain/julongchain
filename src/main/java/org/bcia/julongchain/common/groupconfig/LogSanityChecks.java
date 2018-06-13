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
package org.bcia.julongchain.common.groupconfig;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.policies.PolicyConstant;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public class LogSanityChecks {
    private static JavaChainLog log = JavaChainLogFactory.getLog(LogSanityChecks.class);

    public static void logPolicy(IGroupConfigBundle bundle) {
        IPolicyManager policyManager = bundle.getPolicyManager();

        IPolicy groupReaderPolicy = policyManager.getPolicy(PolicyConstant.GROUP_READERS);
        log.info("groupReaderPolicy-----$" + groupReaderPolicy);
        IPolicy groupWriterPolicy = policyManager.getPolicy(PolicyConstant.GROUP_WRITERS);
        log.info("groupWriterPolicy-----$" + groupWriterPolicy);

        IPolicyManager appPolicyManager = policyManager.getSubPolicyManager(new String[]{PolicyConstant
                .APPLICATION_PREFIX});
        if (appPolicyManager != null) {
            IPolicy groupAppReaderPolicy = policyManager.getPolicy(PolicyConstant.GROUP_APP_READERS);
            log.info("groupAppReaderPolicy-----$" + groupAppReaderPolicy);
            IPolicy groupAppWriterPolicy = policyManager.getPolicy(PolicyConstant.GROUP_APP_WRITERS);
            log.info("groupAppWriterPolicy-----$" + groupAppWriterPolicy);
            IPolicy groupAppAdminPolicy = policyManager.getPolicy(PolicyConstant.GROUP_APP_ADMINS);
            log.info("groupAppAdminPolicy-----$" + groupAppAdminPolicy);
        }

        IPolicyManager consenterPolicyManager = policyManager.getSubPolicyManager(new String[]{PolicyConstant
                .CONSENTER_PREFIX});
        if (consenterPolicyManager != null) {
            IPolicy blockValidationPolicy = policyManager.getPolicy(PolicyConstant.BLOCK_VALIDATION);
            log.info("blockValidationPolicy-----$" + blockValidationPolicy);
        }
    }

}
