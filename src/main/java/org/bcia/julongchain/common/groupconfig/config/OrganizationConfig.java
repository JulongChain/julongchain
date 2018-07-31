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
package org.bcia.julongchain.common.groupconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.common.groupconfig.MSPConfigHandler;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.msp.MspConfigPackage;

/**
 * 组织配置对象
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class OrganizationConfig implements IOrganizationConfig {
    private MspConfigPackage.MSPConfig mspConfig;
    private MSPConfigHandler mspConfigHandler;
    private IMsp msp;
    private String mspId;
    private String name;

    public OrganizationConfig(String name, MSPConfigHandler mspConfigHandler, Configtx.ConfigTree orgTree) throws
            ValidateException, InvalidProtocolBufferException {
        this.name = name;

        this.mspConfigHandler = mspConfigHandler;

        if (orgTree != null && orgTree.getChildsMap() != null && orgTree.getChildsMap().size() > 0) {
            throw new ValidateException("Not supported sub organization");
        }

        if (orgTree != null && orgTree.getValuesMap() != null) {
            Configtx.ConfigValue configValue = orgTree.getValuesMap().get(GroupConfigConstant.MSP);
            if (configValue != null) {
                mspConfig = MspConfigPackage.MSPConfig.parseFrom(configValue.getValue());
            }
        }

        msp = mspConfigHandler.proposeMSP(mspConfig);
        mspId = msp.getIdentifier();
    }

    public boolean validate() {
//        msp = mspConfigHandler.proposeMSP(mspConfig);
        mspId = msp.getIdentifier();

        return StringUtils.isNotBlank(mspId);
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    @Override
    public String getName() {
        return name;
    }
}
