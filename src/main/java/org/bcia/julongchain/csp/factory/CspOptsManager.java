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
package org.bcia.julongchain.csp.factory;

import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.julongchain.csp.gm.sdt.SdtGmFactoryOpts;

import java.util.*;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/13
 * @company Dingxuan
 */
public class CspOptsManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CspOptsManager.class);

    private Map<String, IFactoryOpts> factoryOptsMap = new HashMap<>();
    private List<IFactoryOpts> factoryOptsList = new ArrayList<>();

    public void addFactoryOpts(String name, Map<String, String> optionMap) throws ValidateException {
        IFactoryOpts factoryOpts = null;

        if (IFactoryOpts.PROVIDER_GM.equalsIgnoreCase(name)) {
            factoryOpts = new GmFactoryOpts();
        } else if (IFactoryOpts.PROVIDER_GM_SDT.equalsIgnoreCase(name)) {
            factoryOpts = new SdtGmFactoryOpts();
        } else if (IFactoryOpts.PROVIDER_GMT0016.equals(name)) {
            factoryOpts = new GmFactoryOpts();
        } else if (IFactoryOpts.PROVIDER_PKCS11.equals(name)) {
            factoryOpts = new GmFactoryOpts();
        }

        ValidateUtils.isNotNull(factoryOpts, "can not support this csp yet: " + name);

        factoryOpts.parseFrom(optionMap);

        factoryOptsMap.put(name, factoryOpts);
        factoryOptsList.add(factoryOpts);
    }

    public void addAll(Map<String, Map<String, String>> optionsMap) {
        if (optionsMap != null && optionsMap.size() > 0) {
            Iterator<Map.Entry<String, Map<String, String>>> iterator = optionsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map<String, String>> entry = iterator.next();
                try {
                    addFactoryOpts(entry.getKey(), entry.getValue());
                } catch (ValidateException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public IFactoryOpts getFactoryOpts(String providerName) {
        return factoryOptsMap.get(providerName);
    }

    public List<IFactoryOpts> getFactoryOptsList() {
        return factoryOptsList;
    }
}
