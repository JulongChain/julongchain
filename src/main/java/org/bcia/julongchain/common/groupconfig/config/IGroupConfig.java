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

import org.bcia.julongchain.common.groupconfig.capability.IGroupCapabilities;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.protos.common.Configuration;

/**
 * 群组配置接口
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public interface IGroupConfig {
    Configuration.ConsenterAddresses getConsenterAddresses();

    /**
     * 获取能力集
     *
     * @return
     */
    IGroupCapabilities getCapabilities();

    IMspManager getMspManager();

    /**
     * 获取应用配置对象
     *
     * @return
     */
    IApplicationConfig getApplicationConfig();

    /**
     * 获取共识节点配置对象
     *
     * @return
     */
    IConsenterConfig getConsenterConfig();

    /**
     * 获取联盟配置对象
     *
     * @return
     */
    IConsortiumsConfig getConsortiumsConfig();

    interface HashingAlgorithm {
        byte[] hash(byte[] bytes);
    }

}
