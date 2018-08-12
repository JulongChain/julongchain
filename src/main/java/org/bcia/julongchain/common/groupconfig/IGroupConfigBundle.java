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

import org.bcia.julongchain.common.config.IConfig;
import org.bcia.julongchain.common.configtx.IConfigtxValidator;
import org.bcia.julongchain.common.groupconfig.config.IGroupConfig;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.msp.IMspManager;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public interface IGroupConfigBundle extends IConfig {

    IPolicyManager getPolicyManager();

    IMspManager getMspManager();

    IGroupConfig getGroupConfig();

    IConfigtxValidator getConfigtxValidator();
}
