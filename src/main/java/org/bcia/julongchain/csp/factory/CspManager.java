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

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.GmCspFactory;
import org.bcia.julongchain.csp.gm.sdt.SdtGmCspFactory;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.msp.mspconfig.MspConfig;
import org.bcia.julongchain.msp.mspconfig.MspConfigFactory;

import java.util.HashMap;
import java.util.List;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/29/18
 * @company Dingxuan
 */
public class CspManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CspManager.class);
    /**
     * 默认使用的Csp
     */
    private static ICsp defaultCsp = null;
    /**
     * 全部可用的Csp集合映射
     */
    private static HashMap<String, ICsp> cspMap = new HashMap<String, ICsp>();

    /**
     * 读取配置文件，并对工厂做初始化，创建相应的Csp
     *
     * @param optsList 工厂配置列表
     */
    public static void initCspFactories(List<IFactoryOpts> optsList, String defaultOpts) {
        for (IFactoryOpts opts : optsList) {
            ICspFactory factory = null;

            String providerName = opts.getProviderName();
            if (IFactoryOpts.PROVIDER_GM.equals(providerName)) {
                factory = new GmCspFactory();
            } else if (IFactoryOpts.PROVIDER_GM_SDT.equals(providerName)) {
                factory = new SdtGmCspFactory();
            } else if (IFactoryOpts.PROVIDER_GMT0016.equals(providerName)) {
                //TODO
            } else if (IFactoryOpts.PROVIDER_GMT0018.equals(providerName)) {
                //TODO
            } else if (IFactoryOpts.PROVIDER_GMT0019.equals(providerName)) {
                //TODO
            } else if (IFactoryOpts.PROVIDER_NIST.equals(providerName)) {
                //TODO
            } else if (IFactoryOpts.PROVIDER_PKCS11.equals(providerName)) {
                //TODO
            }

            initCsp(factory, opts, defaultOpts);
        }
    }

    /**
     * 根据工厂创建CSP实例
     *
     * @param factory
     * @param opts
     * @param defaultOpts
     */
    private static void initCsp(ICspFactory factory, IFactoryOpts opts, String defaultOpts) {
        ICsp csp = factory.getCsp(opts);
        log.info("Initialize CSP {}", opts.getProviderName());

        cspMap.put(opts.getProviderName(), csp);

        //指定默认的Csp
        if (opts.getProviderName().equals(defaultOpts)) {
            defaultCsp = csp;
        }
    }

    /**
     * 返回默认的CSP
     *
     * @return
     */
    public static ICsp getDefaultCsp() {
        if (defaultCsp == null) {
            log.warn("Before using CSP, please call initCspFactories(). Falling back to bootCsp.");

            MspConfig mspConfig = null;
            try {
                mspConfig = MspConfigFactory.loadMspConfig();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            String defaultOpts = mspConfig.getNode().getCsp().getDefaultValue();

            CspOptsManager cspOptsManager = CspOptsManager.getInstance();
            cspOptsManager.addAll(defaultOpts, mspConfig.getNode().getCsp().getFactoryOpts());

            List<IFactoryOpts> optsList = cspOptsManager.getFactoryOptsList();
            initCspFactories(optsList, defaultOpts);
        }

        return defaultCsp;
    }

    /**
     * 根据Csp名称返回指定的CSP
     *
     * @param name
     * @return
     */
    public static ICsp getCsp(String name) {
        ICsp csp = cspMap.get(name);
        if (csp == null) {
            log.error("Get Csp {} failed", name);
            return null;
        }
        return csp;
    }
}
