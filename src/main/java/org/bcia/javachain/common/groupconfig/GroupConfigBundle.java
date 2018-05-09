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
package org.bcia.javachain.common.groupconfig;

import org.bcia.javachain.common.configtx.IValidator;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.groupconfig.config.IConsenterConfig;
import org.bcia.javachain.common.groupconfig.config.IGroupConfig;
import org.bcia.javachain.common.policies.IPolicyManager;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.msp.IMspManager;

/**
 * 群组配置集
 *
 * @author zhouhui
 * @date 2018/4/18
 * @company Dingxuan
 */
public class GroupConfigBundle implements IGroupConfigBundle {
    /**
     * 策略管理器
     */
    private IPolicyManager policyManager;
    /**
     * Msp管理器
     */
    private IMspManager mspManager;
    /**
     * 群组配置
     */
    private IGroupConfig groupConfig;
    /**
     * 校验者
     */
    private IValidator validator;

    public void validateNew(IGroupConfigBundle otherBundle) throws ValidateException {
        ValidateUtils.isNotNull(otherBundle, "GroupConfigBundle can not be null");
        ValidateUtils.isNotNull(otherBundle.getGroupConfig(), "GroupConfig can not be null");

        IConsenterConfig consenterConfig = groupConfig.getConsenterConfig();
        if (consenterConfig != null) {
            IConsenterConfig otherConsenterConfig = otherBundle.getGroupConfig().getConsenterConfig();
            ValidateUtils.isNotNull(otherConsenterConfig, "ConsenterConfig can not be null");

//            consenterConfig.getConsensusType()


//            if (bundle.getGroupConfig())

        }


    }

    @Override
    public IPolicyManager getPolicyManager() {
        return policyManager;
    }

    @Override
    public IMspManager getMspManager() {
        return mspManager;
    }

    @Override
    public IGroupConfig getGroupConfig() {
        return groupConfig;
    }

    @Override
    public IValidator getValidator() {
        return validator;
    }
}
