/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.msp.mgmt;

import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.csp.factory.CspOptsManager;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.msp.*;
import org.bcia.julongchain.msp.mspconfig.MspConfig;
import org.bcia.julongchain.msp.util.MspConfigBuilder;
import org.bcia.julongchain.protos.msp.MspConfigPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.bcia.julongchain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/4/12
 * @company Dingxuan
 */
public class GlobalMspManagement {
    private static JulongChainLog log = JulongChainLogFactory.getLog(GlobalMspManagement.class);
    /**
     * 本地msp
     */
    public static IMsp localMsp;
    /**
     * csp默认配置值
     */
    public static String defaultValue;

    /**
     * 通过类型加载本地msp
     * @param localmspdir
     * @param optsList
     * @param mspId
     */
    public static IMsp loadLocalMspWithType(String localmspdir, List<IFactoryOpts> optsList, String defaultOpts,
                                            String mspId) throws MspException {

        CspManager.initCspFactories(optsList, defaultOpts);
        defaultValue = defaultOpts;

        log.info("Build the mspconfig");
        if (!new File(localmspdir).exists()) {
            throw new MspException(String.format("The %s dir is not find", localmspdir));
        }
        MspConfigPackage.MSPConfig buildMspConfig = MspConfigBuilder.buildMspConfig(localmspdir, mspId);
        localMsp=new Msp().setup(buildMspConfig);
        return localMsp;
    }

    /**
     * 加载本地msp
     * @return
     * @throws MspException
     */
    public static IMsp loadlocalMsp() throws MspException {
        return getLocalMsp();
    }

    /**
     * 获取本地msp
     * @return
     */
    public static IMsp getLocalMsp(){
        if (localMsp == null) {
            //读取配置文件构造选项集合
            MspConfig mspConfig = null;
            try {
                mspConfig = loadMspConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            MspConfigPackage.MSPConfig buildMspConfig = null;
            buildMspConfig = MspConfigBuilder.buildMspConfig(mspConfig.getNode().getMspConfigPath(), mspConfig.getNode().getLocalMspId());
            localMsp = new Msp().setup(buildMspConfig);
            return localMsp;
        }
        return localMsp;
    }

    /**
     * 初始化本地msp
     * @throws MspException
     */
    public static void initLocalMsp() throws MspException {
        log.info("Init LocalMsp");
        try {
            MspConfig mspConfig = loadMspConfig();
            String mspConfigDir = mspConfig.getNode().getMspConfigPath();
            String mspId = mspConfig.getNode().getLocalMspId();
            String defaultOpts = mspConfig.getNode().getCsp().getDefaultValue();

            CspOptsManager cspOptsManager = CspOptsManager.getInstance();
            cspOptsManager.addAll(defaultOpts,mspConfig.getNode().getCsp().getFactoryOpts());
            List<IFactoryOpts> optsList = cspOptsManager.getFactoryOptsList();

            GlobalMspManagement.loadLocalMspWithType(mspConfigDir, optsList, defaultOpts, mspId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MspException(e);
        }
    }
}
