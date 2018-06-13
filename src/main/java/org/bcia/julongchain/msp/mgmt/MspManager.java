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

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.protos.msp.Identities;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/3/13
 * @company Dingxuan
 */
public class MspManager implements IMspManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(MspManager.class);
    public  HashMap<String, IMsp> mspsMap = new HashMap<String, IMsp>();
    private IMspManager mspManager;
    private boolean up;
    private static HashMap<String, IMsp> mspsByProviders = new HashMap<String, IMsp>();

    public MspManager() {
    }

    public MspManager(IMspManager mspManager, boolean up) {
        this.mspManager = mspManager;
        this.up = up;
    }

    public IMspManager createMspmgr(IMsp[] msps){

        for (IMsp msp: msps) {
            String mspId= msp.getIdentifier();
            mspsMap.put(mspId,msp);
        }
        return this;
    }


    @Override
    public void setup(IMsp[] msps) {
        if(up==true){
            log.info("MSP getPolicyManager already up");
        }

        for (IMsp msp: msps) {
            String mspId= msp.getIdentifier();
            mspsMap.put(mspId,msp);
        }

    }

    @Override
    public Map<String, IMsp> getMSPs() {
        return mspsMap;
    }

    @Override
    public IIdentity deserializeIdentity(byte[] serializedID) {
        try {
            Identities.SerializedIdentity sId = Identities.SerializedIdentity.parseFrom(serializedID);
            // TODO 暂时先用getlocalmsp获取，之后需要通过id获取
            IMsp msp = getMSPs().get(sId.getMspid());
            //IMsp msp=GlobalMspManagement.getLocalMsp();
            // return msp.deserializeIdentity(sId.getIdBytes().toByteArray());
            return msp.deserializeIdentity(serializedID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void isWellFormed(Identities.SerializedIdentity identity) {

    }

    public boolean isUp() {
        return up;
    }

    public IMspManager getMspManager() {
        return mspManager;
    }
}
