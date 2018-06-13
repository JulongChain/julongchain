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

import org.bcia.julongchain.common.groupconfig.capability.IConsenterCapabilities;
import org.bcia.julongchain.protos.consenter.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 共识节点配置接口
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public interface IConsenterConfig {
    String getConsensusType();

    Configuration.BatchSize getBatchSize();

    long getBatchTimeout();

    List<String> getKafkaBrokers();

    long getMaxChannelsCount();

    IConsenterCapabilities getCapabilities();

    Map<String, IOrganizationConfig> getOrganizationConfigMap();
}
