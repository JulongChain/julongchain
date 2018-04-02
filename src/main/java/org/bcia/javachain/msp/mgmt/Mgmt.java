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

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.util.LoadYaml;
import org.bcia.javachain.csp.gm.sm2.SM2Key;
import org.bcia.javachain.csp.gm.sm2.SM2KeyGenOpts;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.msp.entity.CspConfig;
import org.bcia.javachain.msp.entity.GmSoftConf;
import org.bcia.javachain.msp.gmsoft.GmSoftMsp;

import java.io.File;
import java.util.HashMap;

import static org.bcia.javachain.csp.factory.CspManager.getDefaultCsp;
import static org.bcia.javachain.csp.gm.sm2.util.SM2KeyUtil.keyFileGen;


/**
 * msp管理者
 *
 * @author zhangmingyang
 * @Date: 2018/3/14
 * @company Dingxuan
 */
public class Mgmt {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Mgmt.class);
    //本地的msp
private static IMsp  localMsp;
    //通过具体配置加载得到的msp
private static IMsp loadMsp;
    /**
     * 通过类型加载本地msp
     * @param localmspdir
     * @param bccspconfig
     * @param mspID
     * @param mspType
     */
    public static IMsp loadLocalMspWithType(String localmspdir, CspConfig bccspconfig, String mspID, String mspType) {
        //构建msp配置
        //根据配置项加载公私钥文件,如果密钥文件存在,则将密钥导入到配置项中
        HashMap map= (HashMap) LoadYaml.readYamlFile("gmcsp.yaml");
        String  publickey= (String) ((HashMap) ((HashMap)((HashMap)((HashMap) map.get("node")).get("CSP")).get("GM")).get("FileKeyStore")).get("PublicKeyStore");
        String  privatekey= (String) ((HashMap) ((HashMap)((HashMap)((HashMap) map.get("node")).get("CSP")).get("GM")).get("FileKeyStore")).get("PrivateKeyStore");
        File publicKeyfile=new File(publickey);
        File privateKeyfile=new File(privatekey);
        if(publicKeyfile.exists()&&privateKeyfile.exists()){
            log.info("密钥文件已存在");

        }else {
            try {
                SM2Key keys= (SM2Key) getDefaultCsp().keyGen(new SM2KeyGenOpts());
                keyFileGen(keys,privatekey,publickey,new SM2KeyGenOpts());
            } catch (JavaChainException e) {
                e.printStackTrace();
            }

            //将生成的密钥重新组装到配置中
            log.info("the file is not exists!");
        }
       // File file=new File(url.getFile());
        //if(!file.exists()){
            //根据配置文件中的配置,获取对应的工厂,然后生成对应的密钥

       // }
        loadMsp=getLocalMsp().load(new GmSoftConf(localmspdir,mspID,mspType,bccspconfig));
        return   loadMsp;
    }

    /**
     * 加载本地msp
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
    public static IMsp getLocalMsp() {
        if(localMsp==null){
            //根据配置文件获取不通的msp类型,返回不同的msp实现
            HashMap map= (HashMap) LoadYaml.readYamlFile("gmcsp.yaml");
            String mspType= (String) ((HashMap)map.get("node")).get("localMspType");
            String localmspdir=(String) ((HashMap)map.get("node")).get("mspConfigPath");
            String mspID=(String) ((HashMap)map.get("node")).get("localMspId");
            if (mspType.equalsIgnoreCase("GMMSP")) {
                localMsp=new GmSoftMsp(new GmSoftConf(localmspdir,mspID,mspType,new CspConfig("SM3","publickey.pem","privateke.pem")));
                return localMsp;
            }
            if (mspType.equals("Cspmsp")) {
                return null;
            }
        }
        return localMsp;
    }

    /**
     * 身份序列化
     *
     * @param chainID
     * @return
     */
    public  IIdentityDeserializer getIdentityDeserializer(String chainID) {
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
        String localmspdir="local";
        CspConfig bccspconfig=new CspConfig("SM3","publickey.pem","privateke.pem");
        String mspID="bciamsp";
        String mspType="GmSoftMsp";
        loadLocalMspWithType(localmspdir,bccspconfig,mspID,mspType);
        Mgmt.getLocalMsp().getDefaultSigningIdentity().sign("123".getBytes());

    }
}
