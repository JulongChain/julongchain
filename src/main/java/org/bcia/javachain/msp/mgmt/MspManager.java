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
package org.bcia.javachain.msp.mgmt;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.factory.CspManager;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.msp.*;
import org.bcia.javachain.msp.mspconfig.MspConfig;
import org.bcia.javachain.msp.util.MspConfigHelper;
import org.bcia.javachain.protos.msp.Identities;
import org.bcia.javachain.protos.msp.MspConfigPackage;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bcia.javachain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/4/12
 * @company Dingxuan
 */
public class MspManager implements IMspManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(MspManager.class);
    /**
     * 直接读取配置文件获取的本地的msp
     */
    public static IMsp localMsp;
    /**
     * 通过node节点传入的参数获取的msp
     */
    public static IMsp loadMsp;
    /**
     * 默认的csp
     */
    public static ICsp defaultCsp;
    /**
     * csp默认配置
     */
    public static String defaultCspValue;

    private  Mspmgr mspmgr;

    private boolean up;

    /**
     * mspMap
     */
    public static HashMap<String,IMspManager> mspManagerHashMap=new HashMap<String, IMspManager>();
    /**
     * 通过类型加载本地msp
     *
     * @param localmspdir
     * @param optsList
     * @param mspId
     * @param mspType
     */
    public static IMsp loadLocalMspWithType(String localmspdir, List<IFactoryOpts> optsList, String mspId, String mspType) throws FileNotFoundException {

        CspManager.initCspFactories(optsList);
        MspConfig mspConfig = loadMspConfig();
        defaultCspValue = mspConfig.node.getCsp().getDefaultValue();
        defaultCsp = CspManager.getCsp(defaultCspValue);
        if (IFactoryOpts.PROVIDER_GM.equalsIgnoreCase(defaultCspValue)) {
            //解析配置文件
            log.info("构建mspconfig");
            MspConfigPackage.MSPConfig buildMspConfig = MspConfigHelper.buildMspConfig(localmspdir,mspId);
            loadMsp = getLocalMsp().setup(buildMspConfig);

        } else if (IFactoryOpts.PROVIDER_GMT0016.equalsIgnoreCase(defaultCspValue)) {

        } else if (IFactoryOpts.PROVIDER_GMT0018.equalsIgnoreCase(defaultCspValue)) {

        }

        return loadMsp;
    }

    /**
     * 加载本地msp
     *
     * @param localmspdir 本地msp目录
     * @param optsList    bccsp配置
     * @param mspID       mspid
     */
    public static IMsp loadlocalMsp(String localmspdir, List<IFactoryOpts> optsList, String mspID) {


        return null;
    }

    /**
     * 获取本地msp
     *
     * @return
     */
    public static IMsp getLocalMsp() {
        if (localMsp == null) {
            //读取配置文件构造选项集合
            List<IFactoryOpts> optsList = MspConfigHelper.buildFactoryOpts();
            CspManager.initCspFactories(optsList);

            MspConfig mspConfig = null;
            try {
                mspConfig = loadMspConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            defaultCspValue = mspConfig.node.getCsp().getDefaultValue();
            defaultCsp = CspManager.getCsp(defaultCspValue);
            if (IFactoryOpts.PROVIDER_GM.equalsIgnoreCase(defaultCspValue)) {
                //构建fabricmspconfig
                // MspConfigPackage.MSPConfig fabricMSPConfig = MspConfigHelper.buildMspConfig( mspConfig.node.getMspConfigPath());
                MspConfigPackage.MSPConfig buildMspConfig = MspConfigHelper.buildMspConfig(mspConfig.node.getMspConfigPath(),mspConfig.node.getLocalMspId());
                localMsp = new Msp().setup(buildMspConfig);
                return localMsp;

            } else if (IFactoryOpts.PROVIDER_GMT0016.equalsIgnoreCase(defaultCspValue)) {

            } else if (IFactoryOpts.PROVIDER_GMT0018.equalsIgnoreCase(defaultCspValue)) {

            }
            return localMsp;
        }
        return localMsp;
    }

    /**
     * 身份序列化
     *
     * @param groupId
     * @return
     */
    public static IIdentityDeserializer getIdentityDeserializer(String groupId) {
        if(groupId==""){
            return  getLocalMsp();
        }
        return getManagerForChain(groupId);
    }

    /**
     * 从链上获取一个管理者,如果没有这样的管理者,则创建一个
     *
     * @param groupId
     * @return
     */
    public static IMspManager  getManagerForChain(String groupId) {
       IMspManager mspManager= mspManagerHashMap.get(groupId);
        if(mspManager==null){
            IMsp[] msps = new IMsp[1];
            for (int i = 0; i < msps.length; i++) {
                msps[i]=getLocalMsp();
            }
            IMspManager mspmgr= new Mspmgr().createMspmgr(msps);
            mspManagerHashMap.put(groupId,mspmgr);
            return mspmgr;
        }

        return   mspManager;
    }

    @Override
    public void setup(IMsp[] msps) {

    }

    @Override
    public Map<String, IMsp> getMSPs() {
        return null;
    }

    @Override
    public IIdentity deserializeIdentity(byte[] serializedIdentity) {


        return null;
    }

    @Override
    public void isWellFormed(Identities.SerializedIdentity identity) {

    }
}
