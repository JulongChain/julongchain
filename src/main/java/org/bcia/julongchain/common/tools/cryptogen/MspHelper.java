/**
 * Copyright BCIA. All Rights Reserved.
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
package org.bcia.julongchain.common.tools.cryptogen;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.tools.cryptogen.bean.Configuration;
import org.bcia.julongchain.common.tools.cryptogen.bean.NodeOUs;
import org.bcia.julongchain.common.tools.cryptogen.bean.OrgUnitIdentifiersConfig;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2KeyGenOpts;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.util.encoders.Hex;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * MSP 相关密码材料生成
 *
 * @author chenhao, yegangcheng
 * @date 2018/4/3
 * @company Excelsecu
 */
public class MspHelper {

    public static final int CLIENT = 0;
    public static final int CONSENTER = 1;
    public static final int NODE = 2;
    public static final String CLIENT_OU = "client";
    public static final String NODE_OU = "node";

    private static String[] nodeOUMap = {null, "client", "node"};

    public static void generateLocalMSP(String baseDir, String name, List<String> sans, CaHelper signCA,
                                        CaHelper tlsCA, int nodeType, boolean nodeOUs) throws JulongChainException {
        String mspDir = Paths.get(baseDir, "msp").toString();
        String tlsDir = Paths.get(baseDir, "tls").toString();

        createFolderStructure(mspDir, true);

        FileUtil.mkdirAll(Paths.get(tlsDir));

        String keystore = Paths.get(mspDir, "keystore").toString();

        IKey priv = CspHelper.generatePrivateKey(keystore);

        ECPublicKey ecPubKey = CspHelper.getSM2PublicKey(priv);

        // 使用签名CA证书生成X509证书
        String[] ous = null;
        if (nodeOUs) {
            ous = new String[]{nodeOUMap[nodeType]};

        }

        // TODO 使用 BC 库的生成功能代替 X509Certificate
        X509Certificate cert = signCA.signCertificate(Paths.get(mspDir, "signcerts").toString(),
                name, ous, null, ecPubKey, KeyUsage.digitalSignature, new int[]{});

        // 签名CA证书放入cacerts
        x509Export(Paths.get(mspDir, "cacerts", x509Filename(signCA.getName())).toString(), signCA.getSignCert());

        // TLS CA证书放入tlscacerts
        x509Export(Paths.get(mspDir, "tlscacerts", x509Filename(tlsCA.getName())).toString(), tlsCA.getSignCert());

        if (nodeOUs && nodeType == NODE) {
            exportConfig(mspDir, "cacerts/" + x509Filename(signCA.getName()), true);

        }

        // 个人签名证书放入admincerts
        try {
            x509Export(Paths.get(mspDir, "admincerts", x509Filename(name)).toString(), Certificate.getInstance(cert.getEncoded()));
        } catch (CertificateEncodingException e) {
            throw new JulongChainException("generateLocalMSP failed while export admincerts: " + e.getMessage());
        }

        IKey tlsPrivKey = CspHelper.generatePrivateKey(tlsDir);

        ECPublicKey tlsPubKey = CspHelper.getSM2PublicKey(tlsPrivKey);

        tlsCA.signCertificate(Paths.get(tlsDir).toString(),
                name,
                null,
                sans,
                tlsPubKey,
                KeyUsage.digitalSignature | KeyUsage.keyEncipherment,
                new int[]{Util.EXT_KEY_USAGE_SERVER_AUTH, Util.EXT_KEY_USAGE_CLIENT_AUTH});

        x509Export(Paths.get(tlsDir, "ca.crt").toString(), tlsCA.getSignCert());

        // 根据类型重命名tls证书名
        String tlsFilePrefix = "server";
        if (nodeType == CLIENT) {
            tlsFilePrefix = "client";
        }

        try {
            Files.move(Paths.get(tlsDir, x509Filename(name)), Paths.get(tlsDir, tlsFilePrefix + ".crt"));
        } catch (IOException e) {
            throw new JulongChainException("generateLocalMSP failed while move file: " + e.getMessage());
        }
        keyExport(tlsDir, Paths.get(tlsDir, tlsFilePrefix + ".key").toString(), tlsPrivKey);

    }

    public static void generateVerifyingMSP(String baseDir, CaHelper signCA, CaHelper tlsCA, boolean nodeOUs) throws JulongChainException {
        createFolderStructure(baseDir, false);

        // 签名CA证书放入cacerts
        x509Export(Paths.get(baseDir, "cacerts", x509Filename(signCA.getName())).toString(), signCA.getSignCert());

        // TLS CA证书放入tlscacerts
        x509Export(Paths.get(baseDir, "tlscacerts", x509Filename(tlsCA.getName())).toString(), tlsCA.getSignCert());

        if (nodeOUs) {
            exportConfig(baseDir, "cacerts/" + x509Filename(signCA.getName()), true);
        }

        ICsp csp = CspHelper.getCsp();
        IKey priv = csp.keyGen(new SM2KeyGenOpts() {
            @Override
            public boolean isEphemeral() {
                return true;
            }
        });
        ECPublicKey ecPublickey = CspHelper.getSM2PublicKey(priv);
        signCA.signCertificate(Paths.get(baseDir, "admincerts").toString(),
                signCA.getName(),
                null,
                null,
                ecPublickey,
                KeyUsage.digitalSignature,
                new int[]{});
    }

    private static void createFolderStructure(String rootDir, boolean local) throws JulongChainException {
        List<Path> folders = new ArrayList<>();
        folders.add(Paths.get(rootDir, "admincerts"));
        folders.add(Paths.get(rootDir, "cacerts"));
        folders.add(Paths.get(rootDir, "tlscacerts"));

        if (local) {
            folders.add(Paths.get(rootDir, "keystore"));
            folders.add(Paths.get(rootDir, "signcerts"));
        }

        for (Path folder : folders) {
            FileUtil.mkdirAll(folder);
        }
    }

    private static String x509Filename(String name) {
        return name + "-cert.pem";
    }

    private static void x509Export(String path, Certificate cert) throws JulongChainException {
        try {
            pemExport(path, "CERTIFICATE", cert.getEncoded());
        } catch (Exception e) {
            throw new JulongChainException("An error occurred on x509Export:" + e.getMessage());
        }
    }

    private static void keyExport(String keystore, String output, IKey key) throws JulongChainException {
        String id = Hex.toHexString(key.ski());
        String keyPath = Paths.get(keystore, id + "_sk").toString();

        try {
            Files.move(Paths.get(keyPath), Paths.get(output));
        } catch (IOException e) {
            throw new JulongChainException("renamed unsuccessfully on keyExport");
        }
    }

    private static void pemExport(String path, String pemType, byte[] bytes) throws JulongChainException {
        Util.pemExport(path, pemType, bytes);
    }

    public static void exportConfig(String mspDir, String caFile, boolean enable) throws JulongChainException {
        OrgUnitIdentifiersConfig clientOUIdentifier = new OrgUnitIdentifiersConfig();
        clientOUIdentifier.setCertificate(caFile);
        clientOUIdentifier.setOrganizationalUnitIdentifier(CLIENT_OU);

        OrgUnitIdentifiersConfig nodeOUIdentifier = new OrgUnitIdentifiersConfig();
        nodeOUIdentifier.setCertificate(caFile);
        nodeOUIdentifier.setOrganizationalUnitIdentifier(NODE_OU);

        NodeOUs nodeOUs = new NodeOUs();
        nodeOUs.setClientOUIdentifier(clientOUIdentifier);
        nodeOUs.setNodeOUIdentifier(nodeOUIdentifier);
        nodeOUs.setEnable(enable);

        Configuration configuration = new Configuration();
        configuration.setNodeOUs(nodeOUs);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String path = Paths.get(mspDir, "config.yaml").toString();
        try {
            yaml.dump(configuration.getPropertyMap(), new FileWriter(path));
        } catch (IOException e) {
            throw new JulongChainException("An error occurred on exportConfig:" + e.getMessage());
        }
    }

}
