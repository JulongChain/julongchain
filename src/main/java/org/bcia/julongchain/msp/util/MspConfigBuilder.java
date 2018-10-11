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
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bcia.julongchain.msp.config.Config;
import org.bcia.julongchain.msp.config.ConfigFactory;
import org.bcia.julongchain.msp.mgmt.Msp;
import org.bcia.julongchain.msp.mgmt.MspManager;
import org.bcia.julongchain.msp.mspconfig.MspConfig;
import org.bcia.julongchain.protos.msp.MspConfigPackage;
import org.bouncycastle.asn1.x509.Certificate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bcia.julongchain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * 构造mspconfig
 * @author zhangmingyang
 * @Date: 2018/4/8
 * @company Dingxuan
 */


public class MspConfigBuilder {
    private static JulongChainLog log = JulongChainLogFactory.getLog(MspConfigBuilder.class);
    private static final String WINDOWS = "win";
    private static MspConfigPackage.JuLongNodeOUs.Builder defaultJuLongNodeOUsBuilder;
    private static MspConfigPackage.JuLongOUIdentifier defaultJuLongOUIdentifier;

    public static MspConfigPackage.MSPConfig buildMspConfig(String mspPath, String mspId) {

        //拼接文件路径
        String cacertDir = filePath(mspPath, LoadLocalMspFiles.CACERTS);
        // 密钥文件未装载
        String signcertDir = filePath(mspPath, LoadLocalMspFiles.SIGNCERTS);
        String admincertDir = filePath(mspPath, LoadLocalMspFiles.ADMINCERTS);
        String intermediatecertsDir = filePath(mspPath, LoadLocalMspFiles.INTERMEDIATECERTS);
        String crlsDir = filePath(mspPath, LoadLocalMspFiles.CRLSFOLDER);
        String configFile = filePath(mspPath, LoadLocalMspFiles.CONFIGFILENAME);
        String tlscacertDir = filePath(mspPath, LoadLocalMspFiles.TLSCACERTS);
        String tlsintermediatecertsDir = filePath(mspPath, LoadLocalMspFiles.TLSINTERMEDIATECERTS);

        LoadLocalMspFiles loadLocalMspFiles = new LoadLocalMspFiles();
        //国密证书部分
        List<byte[]> caCerts = loadLocalMspFiles.getCertFromDir(cacertDir);
        List<byte[]> signCerts = loadLocalMspFiles.getCertFromDir(signcertDir);
        List<byte[]> adminCerts = loadLocalMspFiles.getCertFromDir(admincertDir);
        List<byte[]> intermediateCerts = loadLocalMspFiles.getCertFromDir(intermediatecertsDir);
        List<byte[]> CRLs = loadLocalMspFiles.getCertFromDir(crlsDir);
        List<byte[]> configFileContents = loadLocalMspFiles.getCertFromDir(configFile);
        List<byte[]> tlscaCerts = loadLocalMspFiles.getCertFromDir(tlscacertDir);
        List<byte[]> tlsintermediateCerts = loadLocalMspFiles.getCertFromDir(tlsintermediatecertsDir);


        MspConfigPackage.MSPConfig.Builder mspConfigBuilder = null;
        try {
            //构建节点单元配置
            buildJuLongNodeOUs(mspPath);
            mspConfigBuilder = MspConfigBuilder.mspConfigBuilder(mspId, caCerts, signCerts,
                    adminCerts, intermediateCerts, CRLs, configFileContents, tlscaCerts, tlsintermediateCerts);
        } catch (MspException e) {
            log.info(e.getMessage());
        }
        return mspConfigBuilder.build();
    }

    /**
     * 构建配置,添加国密证书
     *
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
    public static MspConfigPackage.MSPConfig.Builder mspConfigBuilder(String mspId, List<byte[]> cacert, List<byte[]> signcert,
                                                                      List<byte[]> admincert, List<byte[]> intermediatecerts,
                                                                      List<byte[]> crls, List<byte[]> configFileContent,
                                                                      List<byte[]> tlscacert, List<byte[]> tlsintermediatecerts) throws MspException {
        MspConfigPackage.MSPConfig.Builder mspConfigBuilder = MspConfigPackage.MSPConfig.newBuilder();

        MspConfigPackage.JuLongMSPConfig.Builder julongMspConfigBuilder = MspConfigPackage.JuLongMSPConfig.newBuilder();
        julongMspConfigBuilder.setName(mspId);
        //设置管理员证书
        julongMspConfigBuilder.addAdmins(ByteString.copyFrom(admincert.get(0)));
        //设置根CA证书
        julongMspConfigBuilder.addRootCerts(ByteString.copyFrom(cacert.get(0)));

        //构建中间CA证书,中间CA证书在密码材料生成工具中没有生成
        if (intermediatecerts == null || intermediatecerts.size() == 0) {
        } else {
            julongMspConfigBuilder.addIntermediateCerts(ByteString.copyFrom(intermediatecerts.get(0)));
        }
        if (crls == null || crls.size() == 0) {
        }
        if (configFileContent == null || configFileContent.size() == 0) {
        }
        if (tlsintermediatecerts == null || tlsintermediatecerts.size() == 0) {
        }
        //设置TLS 根证书
        julongMspConfigBuilder.addTlsRootCerts(ByteString.copyFrom(tlscacert.get(0)));
        //将签名证书设置到签名身份信息中
        MspConfigPackage.SigningIdentityInfo.Builder signingIdentityInfoBuilder = MspConfigPackage.SigningIdentityInfo.newBuilder();
        signingIdentityInfoBuilder.setPublicSigner(ByteString.copyFrom(signcert.get(0)));
        julongMspConfigBuilder.setSigningIdentity(signingIdentityInfoBuilder);

        julongMspConfigBuilder.setJuLongNodeOUs(defaultJuLongNodeOUsBuilder);
        julongMspConfigBuilder.addOrganizationalUnitIdentifiers(defaultJuLongOUIdentifier);
        mspConfigBuilder.setConfig(ByteString.copyFrom(julongMspConfigBuilder.build().toByteArray()));
        return mspConfigBuilder;
    }


    /**
     * 构建节点组织
     *
     * @param mspPath
     * @return
     * @throws MspException
     */
    public static MspConfigPackage.JuLongNodeOUs.Builder buildJuLongNodeOUs(String mspPath) throws MspException {
        Config config = null;
        MspConfigPackage.JuLongOUIdentifier[] juLongOUIdentifiers = null;
        try {
            config = ConfigFactory.loadConfig();
        } catch (FileNotFoundException e) {
            throw new MspException(e.getMessage());
        }


        if (config.getOrganizationalUnitIdentifiers().size() > 0) {
            Map<String, String> map = config.getOrganizationalUnitIdentifiers();
            //TODO 装载organizationalUnitIdentifier
            String key = map.get("certificate");
            MspConfigPackage.JuLongOUIdentifier.Builder juLongOUIdentifierBuilder = MspConfigPackage.JuLongOUIdentifier.newBuilder();
            byte[] raw = null;
            String caCertPath = filePath(mspPath, key);
            try {
                raw = FileUtils.readFileBytes(caCertPath);
                juLongOUIdentifierBuilder.setCertificate(ByteString.copyFrom(raw));
            } catch (IOException e) {
                throw new MspException(e.getMessage());
            }
            String ouid = map.get("organizationalUnitIdentifier");

            juLongOUIdentifierBuilder.setOrganizationalUnitIdentifier(ouid);
            // juLongOUIdentifiers= ArrayUtils.add(juLongOUIdentifiers,juLongOUIdentifierBuilder.build());
            defaultJuLongOUIdentifier = juLongOUIdentifierBuilder.build();

        }

        log.info("Loading NodeOus");
        if (config.getNodeOUs().getClientOUIdentifier() == null || config.getNodeOUs().getClientOUIdentifier().get(MspConstant.OUIDENTIFIER) == null) {
            throw new MspException("Failed loading NodeOUs. ClientOU must be different from nil.");
        }
        if (config.getNodeOUs().getClientOUIdentifier() == null || config.getNodeOUs().getNodeOUIdentifier().get(MspConstant.OUIDENTIFIER) == null) {
            throw new MspException("Failed loading NodeOUs. PeerOU must be different from nil.");
        }
        MspConfigPackage.JuLongNodeOUs.Builder juLongNodeOUsBuilder = MspConfigPackage.JuLongNodeOUs.newBuilder();
        // Read certificates, if defined
        // ClientOU
        String clientCertPath = filePath(mspPath, config.getNodeOUs().getClientOUIdentifier().get(MspConstant.CERT));
        byte[] clientCert = null;
        byte[] nodeCert = null;
        try {
            clientCert = FileUtils.readFileBytes(clientCertPath);
        } catch (IOException e) {
            throw new MspException(e.getMessage());
        }

        // NodeOU
        String nodeCertPath = filePath(mspPath, config.getNodeOUs().getNodeOUIdentifier().get(MspConstant.CERT));
        try {
            nodeCert = FileUtils.readFileBytes(nodeCertPath);
        } catch (IOException e) {
            throw new MspException(e.getMessage());
        }

        MspConfigPackage.JuLongOUIdentifier.Builder clientIdentifierBuilder = MspConfigPackage.JuLongOUIdentifier.newBuilder()
                .setCertificate(ByteString.copyFrom(clientCert))
                .setOrganizationalUnitIdentifier(config.getNodeOUs().getClientOUIdentifier().get(MspConstant.OUIDENTIFIER));

        MspConfigPackage.JuLongOUIdentifier.Builder nodeIdentifierBuilder = MspConfigPackage.JuLongOUIdentifier.newBuilder()
                .setCertificate(ByteString.copyFrom(nodeCert))
                .setOrganizationalUnitIdentifier(config.getNodeOUs().getNodeOUIdentifier().get(MspConstant.OUIDENTIFIER));

        juLongNodeOUsBuilder.setEnable(Boolean.valueOf(config.getNodeOUs().getIsEnable()));
        juLongNodeOUsBuilder.setClientOUIdentifier(clientIdentifierBuilder);
        juLongNodeOUsBuilder.setNodeOUIdentifier(nodeIdentifierBuilder);


        defaultJuLongNodeOUsBuilder = juLongNodeOUsBuilder;
        return juLongNodeOUsBuilder;

    }


    /**
     * 拼接字符串
     *
     * @param prefixDir
     * @param suffixDir
     * @return
     */
    public static String filePath(String prefixDir, String suffixDir) {
        String absolutePath;
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith(WINDOWS)) {
            absolutePath = prefixDir + "\\" + suffixDir;
        } else {
            absolutePath = prefixDir + "/" + suffixDir;
        }
        return absolutePath;
    }

}
