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

import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.msp.entity.CspConfig;
import org.bcia.javachain.msp.entity.GmSoftConf;
import org.bcia.javachain.msp.gmsoft.GmSoftMsp;
import org.bcia.javachain.msp.gmsoft.GmSoftSigningIdentity;


/**
 * msp管理者
 *
 * @author zhangmingyang
 * @Date: 2018/3/14
 * @company Dingxuan
 */
public class Mgmt {

    /**
     *
     * @param localmspdir
     * @param bccspconfig
     * @param mspID
     * @param mspType
     */
    public void loadLocalMspWithType(String localmspdir, CspConfig bccspconfig, String mspID, String mspType) {
        //构建msp配置


        getLocalMsp().load(new GmSoftConf(localmspdir,mspID,mspType,bccspconfig));
    }

    /**
     * 加载本地msp
     *
     * @param localmspdir 本地msp目录
     * @param bccspconfig bccsp配置
     * @param mspID       mspid
     */
    public void loadlocalMsp(String localmspdir, Object bccspconfig, String mspID) {

    }

    /**
     * 获取本地msp
     *
     * @return
     */
    public IMsp getLocalMsp() {
        //根据配置文件获取不通的msp类型,返回不同的msp实现
        String localmspdir="local";
        CspConfig bccspconfig=new CspConfig("SM3","publickey.pem","privateke.pem");
        String mspID="bciamsp";
        String mspType = "GmSoftMsp";
        if (mspType.equals("GmSoftMsp")) {
            return new GmSoftMsp(new GmSoftConf(localmspdir,mspID,mspType,bccspconfig));
        }
        if (mspType.equals("Cspmsp")) {
            return null;
        }

//        MspConfigPackage.MSPConfig mspConfig = MspConfigPackage.MSPConfig.newBuilder().build();
//        MspConfigPackage.FabricMSPConfig fabricMSPConfig = MspConfigPackage.FabricMSPConfig.parseFrom(mspConfig.getConfig());
//
//        fabricMSPConfig.getName()


        return null;
    }

    /**
     * 身份序列化
     *
     * @param chainID
     * @return
     */
    public IIdentityDeserializer getIdentityDeserializer(String chainID) {
        return getManagerForChain(chainID);
    }

    /**
     * 从链上获取一个管理者,如果没有这样的管理者,则创建一个
     *
     * @param chainID
     * @return
     */
    public IMspManager getManagerForChain(String chainID) {
        return null;
    }

    public static void main(String[] args) {
        Mgmt mgmt=new Mgmt();
        String localmspdir="local";
        CspConfig bccspconfig=new CspConfig("SM3","publickey.pem","privateke.pem");
        String mspID="bciamsp";
        String mspType="GmSoftMsp";
        mgmt.loadLocalMspWithType(localmspdir,bccspconfig,mspID,mspType);
        GmSoftSigningIdentity gmSoftSigningIdentity= (GmSoftSigningIdentity) mgmt.getLocalMsp().getDefaultSigningIdentity();
        byte[] signvalue=gmSoftSigningIdentity.sign("123".getBytes());
        gmSoftSigningIdentity.verify("123".getBytes(),signvalue);
    }
}
