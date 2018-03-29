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

import org.bcia.javachain.common.util.MspType;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.IMspFactory;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.msp.IMspOpts;
import org.bcia.javachain.msp.mgmt.MspmgrImpl;
import org.bcia.javachain.protos.msp.MspConfigPackage;

import java.util.Map;

/**
 * Msg配置处理器
 *
 * @author zhouhui
 * @date 2018/3/27
 * @company Dingxuan
 */
public class MSPConfigHandler {
    private static class PendingMSPConfig {
        private IMsp msp;
        private MspConfigPackage.MSPConfig mspConfig;

        public PendingMSPConfig(IMsp msp, MspConfigPackage.MSPConfig mspConfig) {
            this.msp = msp;
            this.mspConfig = mspConfig;
        }

        public IMsp getMsp() {
            return msp;
        }

        public void setMsp(IMsp msp) {
            this.msp = msp;
        }

        public MspConfigPackage.MSPConfig getMspConfig() {
            return mspConfig;
        }

        public void setMspConfig(MspConfigPackage.MSPConfig mspConfig) {
            this.mspConfig = mspConfig;
        }
    }

    private int mspVersion;

    private Map<String, PendingMSPConfig> idMap;

    public MSPConfigHandler(int mspVersion) {
        this.mspVersion = mspVersion;
    }

    public IMsp proposeMSP(MspConfigPackage.MSPConfig mspConfig) {
//        if (mspConfig.getType() == MspType.CSP.ordinal()) {
        IMspFactory factory = new MockMspFactory();
        IMspOpts opts = new MockMspOpts();

        IMsp msp = factory.getMsp(opts);

        msp.setup(mspConfig);
        String mspId = msp.getIdentifier();

        //TODO:判断是否已经存在
        //PendingMSPConfig existingPendingMSPConfig = idMap.get(mspId);

        idMap.put(mspId, new PendingMSPConfig(msp, mspConfig));


        return msp;
//        }
    }

    public IMspManager CreateMSPManager() {
        IMsp[] mspArray = new IMsp[idMap.size()];
        int i = 0;
        for (PendingMSPConfig pendingMSPConfig : idMap.values()) {
            mspArray[i++] = pendingMSPConfig.getMsp();
        }

        IMspManager mspManager = new MspmgrImpl();
        mspManager.setup(mspArray);
        return mspManager;
    }


}
