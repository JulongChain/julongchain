package org.bcia.julongchain.csp.factory;

/**
 * Copyright BCIA. All Rights Reserved.
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

import java.util.Map;

/**
 * @author zhanglin, sunianle
 * @purpose Define the interface, IFactoryOpts
 * @date 2018-01-25
 * @company Dingxuan
 */
public interface IFactoryOpts {
    /**
     * 国密默认实现
     */
    String PROVIDER_GM = "gm";
    /**
     * 兴唐通讯国密实现
     */
    String PROVIDER_GM_SDT = "sdtgm";

    String PROVIDER_NIST = "nist";

    String PROVIDER_PKCS11 = "pkcs11";

    String PROVIDER_GMT0016 = "gmt0016";

    String PROVIDER_GMT0018 = "gmt0018";

    String PROVIDER_GMT0019 = "gmt0019";

    /**
     * 返回Csp名称
     *
     * @return
     */
    String getProviderName();

    String getProviderDescription();

    /**
     *返回私钥路经
     * @return
     */
    String getKeyStore();
    /**
     * 从Map中转化数据
     *
     * @param optMap
     */
    void parseFrom(Map<String, String> optMap);
}
