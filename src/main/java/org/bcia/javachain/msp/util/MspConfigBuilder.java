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
package org.bcia.javachain.msp.util;

import com.google.protobuf.ByteString;
import org.bcia.javachain.msp.mspconfig.MspConfig;
import org.bcia.javachain.protos.msp.MspConfigPackage;

import java.io.FileNotFoundException;
import java.util.List;

import static org.bcia.javachain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * 构造mspconfig
 *
 * @author zhangmingyang
 * @Date: 2018/4/8
 * @company Dingxuan
 */


public class MspConfigBuilder {



    public static MspConfigPackage.MSPConfig buildMspConfig(String mspId,List<String> cacert, List<String> keystore, List<String> signcert,
                                                            List<String> admincert, List<String> intermediatecerts, List<String> crls,
                                                            List<String> configFileContent, List<String> tlscacert, List<String> tlsintermediatecerts) {
        MspConfigPackage.MSPConfig.Builder mspConfig = MspConfigPackage.MSPConfig.newBuilder();
        MspConfigPackage.FabricMSPConfig fabricMSPConfig = buildFabricMspConfig(mspId,cacert, keystore, signcert,
                admincert, intermediatecerts, crls,
                configFileContent, tlscacert, tlsintermediatecerts);
        mspConfig.setType(1);
        mspConfig.setConfig(ByteString.copyFrom(fabricMSPConfig.toByteArray()));
        return mspConfig.build();
    }

    /**
     * 构造fabricMspConfig
     *
     * @param cacert
     * @param keystore
     * @param signcert
     * @param admincert
     * @param intermediatecerts
     * @param crls
     * @param configFileContent
     * @param tlscacert
     * @param tlsintermediatecerts
     * @return
     */
    public static MspConfigPackage.FabricMSPConfig buildFabricMspConfig(String mspId,List<String> cacert, List<String> keystore, List<String> signcert,
                                                                        List<String> admincert, List<String> intermediatecerts, List<String> crls,
                                                                        List<String> configFileContent, List<String> tlscacert, List<String> tlsintermediatecerts) {
        MspConfigPackage.FabricMSPConfig.Builder mspConfig = MspConfigPackage.FabricMSPConfig.newBuilder();


        mspConfig.setName(mspId);

        mspConfig.addRootCerts(ByteString.copyFrom(cacert.get(0).getBytes()));
        // mspConfig.addIntermediateCerts(ByteString.copyFrom(intermediatecerts.get(0).getBytes()));
        mspConfig.addAdmins(ByteString.copyFrom(admincert.get(0).getBytes()));
//        mspConfig.addRevocationList(ByteString.copyFrom(crls.get(0).getBytes()));
        MspConfigPackage.SigningIdentityInfo signingIdentityInfo=buildSigningIdentityInfo(signcert);
        mspConfig.setSigningIdentity(signingIdentityInfo);
        if(tlsintermediatecerts==null||tlsintermediatecerts.size()==0){
        }else {
            mspConfig.addTlsIntermediateCerts(ByteString.copyFrom(tlsintermediatecerts.get(0).getBytes()));
        }
        mspConfig.addTlsRootCerts(ByteString.copyFrom(tlscacert.get(0).getBytes()));

        mspConfig.setFabricNodeOUs(buildFabricNodeOUs());

        mspConfig.setCryptoConfig(buildCryptoConfig(keystore));
        return mspConfig.build();
    }

    /**
     * 构建fabricNodeOUs
     * @return
     */

    public static MspConfigPackage.FabricNodeOUs buildFabricNodeOUs(){
        String testMessage="testMessage";
        MspConfigPackage.FabricNodeOUs.Builder fabricNodeOUs=MspConfigPackage.FabricNodeOUs.newBuilder();
        fabricNodeOUs.setEnable(true);

        MspConfigPackage.FabricOUIdentifier.Builder clientOUIdentifier=   MspConfigPackage.FabricOUIdentifier.newBuilder();
        clientOUIdentifier.setCertificate(ByteString.copyFrom(testMessage.getBytes()));
        clientOUIdentifier.setOrganizationalUnitIdentifier(testMessage);

        MspConfigPackage.FabricOUIdentifier.Builder peerOUIdentifier=   MspConfigPackage.FabricOUIdentifier.newBuilder();
        peerOUIdentifier.setCertificate(ByteString.copyFrom(testMessage.getBytes()));
        peerOUIdentifier.setOrganizationalUnitIdentifier(testMessage);

        MspConfigPackage.FabricOUIdentifier.Builder ordererOUIdentifier=   MspConfigPackage.FabricOUIdentifier.newBuilder();
        ordererOUIdentifier.setCertificate(ByteString.copyFrom(testMessage.getBytes()));
        ordererOUIdentifier.setOrganizationalUnitIdentifier(testMessage);

        fabricNodeOUs.setClientOUIdentifier(clientOUIdentifier.build());
        fabricNodeOUs.setPeerOUIdentifier(peerOUIdentifier.build());
        fabricNodeOUs.setOrdererOUIdentifier(ordererOUIdentifier.build());

        return fabricNodeOUs.build();
    }

    /**
     * 构建cryptoConfig
     */
    public static MspConfigPackage.FabricCryptoConfig buildCryptoConfig(List<String> keystore) {
        MspConfigPackage.FabricCryptoConfig.Builder cryptoConfig = MspConfigPackage.FabricCryptoConfig.newBuilder();

        MspConfig mspConfig = null;
        try {
            mspConfig = loadMspConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String symmetrickey = mspConfig.node.getCsp().getGm().getAsymmetric();
        String sign = mspConfig.node.getCsp().getGm().getSign();
        String hash = mspConfig.node.getCsp().getGm().getHash();
        String asymmetric = mspConfig.node.getCsp().getGm().getAsymmetric();
        String privateKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPrivateKeyStore();
        String publicKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPublicKeyStore();

        cryptoConfig.setSymmetrickey(symmetrickey);
        cryptoConfig.setSign(sign);
        cryptoConfig.setHash(hash);
        cryptoConfig.setAsymmetric(asymmetric);
        cryptoConfig.setFilekeystore(buildFileKeyStore(privateKeyPath, publicKeyPath, keystore));
        return cryptoConfig.build();
    }

    /**
     * 构建签名实体
     *
     * @return
     */
    public static MspConfigPackage.SigningIdentityInfo buildSigningIdentityInfo(List<String> signcert) {
        MspConfigPackage.SigningIdentityInfo.Builder signingIdentityInfo = MspConfigPackage.SigningIdentityInfo.newBuilder();
        signingIdentityInfo.setPublicSigner(ByteString.copyFrom(signcert.get(0).getBytes()));
        //源码中privateSigner为null
        signingIdentityInfo.setPrivateSigner(buildKeyInfo());
        return signingIdentityInfo.build();
    }

    /**
     * 构建KeyInfo
     *
     * @return
     */

    public static MspConfigPackage.KeyInfo buildKeyInfo() {
        String keyMaterial="keyMaterial";
        MspConfigPackage.KeyInfo.Builder keyInfo = MspConfigPackage.KeyInfo.newBuilder();
        keyInfo.setKeyIdentifier("keyIdentifier");
        keyInfo.setKeyMaterial(ByteString.copyFrom(keyMaterial.getBytes()));
        return keyInfo.build();
    }


    /**
     * 构建fileKeyStore
     *
     * @param privateKeyPath
     * @param publicKeyPath
     * @return
     */
    public static MspConfigPackage.FileKeyStore buildFileKeyStore(String privateKeyPath, String publicKeyPath, List<String> keystore) {
        MspConfigPackage.FileKeyStore.Builder fileKeyStore = MspConfigPackage.FileKeyStore.newBuilder();
        fileKeyStore.setPrivateKeyPath(privateKeyPath);
        fileKeyStore.setPublicKeyPath(publicKeyPath);
        fileKeyStore.setPrivateKeyValue(keystore.get(0));
        fileKeyStore.setPublicKeyValue(keystore.get(1));
        return fileKeyStore.build();
    }



}
