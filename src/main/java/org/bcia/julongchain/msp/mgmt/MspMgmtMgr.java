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
import org.bcia.julongchain.msp.*;
import org.bcia.julongchain.protos.msp.Identities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.bcia.julongchain.msp.mgmt.GlobalMspManagement.getLocalMsp;

/**
 * msp集合管理类
 *
 * @author zhangmingyang
 * @date 2018/07/24
 * @company Dingxuan
 */
public class MspMgmtMgr implements IMspManager {
    private static JulongChainLog log = JulongChainLogFactory.getLog(MspMgmtMgr.class);
    private IMspManager mspManager;
    private boolean up;
    private static Map<String, IMspManager> mspManagerMap = new ConcurrentHashMap<>();

    public MspMgmtMgr(IMspManager mspManager, boolean up) {
        this.mspManager = mspManager;
        this.up = up;
    }

    @Override
    public IIdentity deserializeIdentity(byte[] serializedID) throws MspException {
//        if (!up){
//            throw new MspException("Group doesn't exist");
//        }
        return mspManager.deserializeIdentity(serializedID);
    }

    @Override
    public void isWellFormed(Identities.SerializedIdentity identity) {

    }

    @Override
    public void setup(IMsp[] msps) throws MspException {
        try {
            mspManager.setup(msps);
            up = true;
        } catch (MspException e) {
            throw new MspException(e.getMessage());
        }

    }

    /**
     * 返回提供的msp管理链
     *
     * @param groupId
     * @return
     */
    public static IMspManager getManagerForChain(String groupId) {
        IMspManager mspManager = mspManagerMap.get(groupId);
        if (mspManager == null) {
            log.debug(String.format("Created new msp manager for group %s", groupId));
            MspMgmtMgr mspMgmtMgr = new MspMgmtMgr(new MspManager(), false);
            mspManagerMap.put(groupId, mspMgmtMgr);
            mspManager = mspManagerMap.get(groupId);
        } else {
            //TODO 通过反射判断是否为mspmanager type
            log.debug(String.format("Returning existing manager for group '%s'", groupId));
        }
        return mspManager;
    }

    /**
     * IdentityDeserializer
     *
     * @param groupId
     * @return
     */
    public static IIdentityDeserializer getIdentityDeserializer(String groupId) {
        if (groupId == "" || groupId == null) {
            return getLocalMsp();
        }
        return getManagerForChain(groupId);
    }


    /**
     * 返回所有已注册的manager
     *
     * @return
     */
    public static Map<String, IMspManager> getDeserializers() {
        return mspManagerMap;
    }


    /**
     * 将mspManager通过key装载到map中
     *
     * @param groupId
     * @param manager
     */
    public void setMspManager(String groupId, IMspManager manager) {
        MspMgmtMgr mspManager = new MspMgmtMgr(manager, true);
        mspManagerMap.put(groupId, mspManager);
    }


    /**
     * 返回本地签名身份，或者错误
     *
     * @return
     */
    public static ISigningIdentity getLocalSigningIdentityOrPanic() {
        ISigningIdentity id = getLocalMsp().getDefaultSigningIdentity();
        return id;
    }

}
