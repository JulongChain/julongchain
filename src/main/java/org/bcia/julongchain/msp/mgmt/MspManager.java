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
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.protos.msp.Identities;

import java.util.HashMap;
import java.util.Map;

/**
 * IMspManager的实现
 *
 * @author zhangmingyang
 * @Date: 2018/3/13
 * @company Dingxuan
 */
public class MspManager implements IMspManager {
    private static JulongChainLog log = JulongChainLogFactory.getLog(MspManager.class);
    public static Map<String, IMsp> mspsMap = new HashMap<String, IMsp>();
    private boolean up;
    private static Map<Integer, IMsp> mspsByProviders = new HashMap<>();

    public MspManager() {
    }

    @Override
    public void setup(IMsp[] msps) throws MspException {
        if (up == true) {
            log.info("MSP manager already up");
        }

        log.debug(String.format("Setting up the MSP manager (%d msps)", msps.length));

        for (IMsp msp : msps) {
            String mspId = msp.getIdentifier();
            int providerType = msp.getType();
            mspsMap.put(mspId, msp);
            mspsByProviders.put(providerType, msp);
        }
        up = true;
        log.debug(String.format("MSP manager setup complete, setup %d msps", msps.length));

    }

    /**
     * 获取msp集合
     *
     * @return
     * @throws MspException
     */
    public Map<String, IMsp> getMSPs() throws MspException {
        return mspsMap;
    }

    @Override
    public IIdentity deserializeIdentity(byte[] serializedID) throws MspException {
        IMsp msp = null;
        try {
            Identities.SerializedIdentity sId = Identities.SerializedIdentity.parseFrom(serializedID);
            msp = mspsMap.get(sId.getMspid());
        } catch (Exception e) {
            throw new MspException(e.getMessage());
        }
        return msp.deserializeIdentity(serializedID);
    }

    @Override
    public void isWellFormed(Identities.SerializedIdentity identity) {
        Msp msp = (Msp) mspsByProviders.get(0);
        msp.isWellFormed(identity);
    }

    public boolean isUp() {
        return up;
    }
}
