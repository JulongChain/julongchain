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
package org.bcia.julongchain.msp.util;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.msp.mspconfig.MspConfig;
import org.bcia.julongchain.protos.msp.MspConfigPackage;

import java.io.FileNotFoundException;
import java.util.List;

import static org.bcia.julongchain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * 构造mspconfig
 *
 * @author zhangmingyang
 * @Date: 2018/4/8
 * @company Dingxuan
 */


public class MspConfigBuilder {

    /**
     *  构建配置,添加国密证书
     * @param mspId
     * @param cacert
     * @param signcert
     * @param admincert
     * @param intermediatecerts
     * @param crls
     * @param configFileContent
     * @param tlscacert
     * @param tlsintermediatecerts
     * @return
     */
    public static MspConfigPackage.MSPConfig.Builder mspConfigBuilder(String mspId,List<byte[]> cacert, List<byte[]> signcert,
                                                                      List<byte[]> admincert, List<byte[]> intermediatecerts, List<byte[]> crls,
                                                                      List<byte[]> configFileContent, List<byte[]> tlscacert, List<byte[]> tlsintermediatecerts){

        //
        MspConfigPackage.MSPConfig.Builder mspConfigBuilder=MspConfigPackage.MSPConfig.newBuilder();

        MspConfigPackage.FabricMSPConfig.Builder fabricMspConfigBuilder=MspConfigPackage.FabricMSPConfig.newBuilder();
        fabricMspConfigBuilder.setName(mspId);
        //设置管理员证书
        fabricMspConfigBuilder.addAdmins(ByteString.copyFrom(admincert.get(0)));
        //设置根CA证书
        fabricMspConfigBuilder.addRootCerts(ByteString.copyFrom(cacert.get(0)));

        //构建中间CA证书,中间CA证书在密码材料生成工具中没有生成
        if(intermediatecerts.equals(null)||intermediatecerts.size()==0){

        }else {
            fabricMspConfigBuilder.addIntermediateCerts(ByteString.copyFrom(intermediatecerts.get(0)));
        }if(crls.equals(null)||crls.size()==0){

        }
        if(configFileContent.equals(null)||configFileContent.size()==0){

        }if(tlsintermediatecerts.equals(null)||tlsintermediatecerts.size()==0){

        }

        //设置TLS 根证书
        fabricMspConfigBuilder.addTlsRootCerts(ByteString.copyFrom(tlscacert.get(0)));

        //将签名证书设置到签名身份信息中
        MspConfigPackage.SigningIdentityInfo.Builder  signingIdentityInfoBuilder= MspConfigPackage.SigningIdentityInfo.newBuilder();
        signingIdentityInfoBuilder.setPublicSigner(ByteString.copyFrom(signcert.get(0)));
        fabricMspConfigBuilder.setSigningIdentity(signingIdentityInfoBuilder);

        mspConfigBuilder.setConfig(ByteString.copyFrom(fabricMspConfigBuilder.build().toByteArray()));

        return mspConfigBuilder;
    }

}
