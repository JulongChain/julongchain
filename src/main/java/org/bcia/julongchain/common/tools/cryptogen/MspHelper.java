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

import org.bcia.julongchain.common.exception.JavaChainException;
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
                                        CaHelper tlsCA, int nodeType, boolean nodeOUs) throws JavaChainException {
        //create folder structure
        String mspDir = Paths.get(baseDir, "msp").toString();
        String tlsDir = Paths.get(baseDir, "tls").toString();

        createFolderStructure(mspDir, true);

        FileUtil.mkdirAll(Paths.get(tlsDir));

        /**
         * Create the MSP identity artifacts
         */
        //get keystore path
        String keystore = Paths.get(mspDir, "keystore").toString();

        //generate private key
        IKey priv = CspHelper.generatePrivateKey(keystore);

        //get public key
        ECPublicKey ecPubKey = CspHelper.getSM2PublicKey(priv);

        //generate X509 certificate using signing CaHelper
        String[] ous = null;
        if (nodeOUs) {
            ous = new String[]{nodeOUMap[nodeType]};

        }

        // TODO remove dependency of X509Certificate
        X509Certificate cert = signCA.signCertificate(Paths.get(mspDir, "signcerts").toString(),
                name, ous, null, ecPubKey, KeyUsage.digitalSignature, new int[]{});

        // the signing CaHelper certificate goes into cacerts
        x509Export(Paths.get(mspDir, "cacerts", x509Filename(signCA.getName())).toString(), signCA.getSignCert());

        // the TLS CaHelper certificate goes into tlscacerts
        x509Export(Paths.get(mspDir, "tlscacerts", x509Filename(tlsCA.getName())).toString(), tlsCA.getSignCert());

        if (nodeOUs && nodeType == NODE) {
            exportConfig(mspDir, "cacerts/" + x509Filename(signCA.getName()), true);

        }
        // the signing identity goes into admincerts.
        // This means that the signing identity
        // of this MSP is also an admin of this MSP
        // NOTE: the admincerts folder is going to be
        // cleared up anyway by copyAdminCert, but
        // we leave a valid admin for now for the sake
        // of unit tests
        try {
            x509Export(Paths.get(mspDir, "admincerts", x509Filename(name)).toString(), Certificate.getInstance(cert.getEncoded()));
        } catch (CertificateEncodingException e) {
            throw new JavaChainException("generateLocalMSP failed while export admincerts: " + e.getMessage());
        }
        /*
		Generate the TLS artifacts in the TLS folder
	    */

        // generate private key
        IKey tlsPrivKey = CspHelper.generatePrivateKey(tlsDir);

        //get public key
        ECPublicKey tlsPubKey = CspHelper.getSM2PublicKey(tlsPrivKey);

        //generate X509 certificate using TLS CaHelper
        tlsCA.signCertificate(Paths.get(tlsDir).toString(),
                name,
                null,
                sans,
                tlsPubKey,
                KeyUsage.digitalSignature | KeyUsage.keyEncipherment,
                new int[]{Util.EXT_KEY_USAGE_SERVER_AUTH, Util.EXT_KEY_USAGE_CLIENT_AUTH});

        x509Export(Paths.get(tlsDir, "ca.crt").toString(), tlsCA.getSignCert());

        //rename the generated TLS X509 cert
        String tlsFilePrefix = "server";
        if (nodeType == CLIENT) {
            tlsFilePrefix = "client";
        }

        try {
            Files.move(Paths.get(tlsDir, x509Filename(name)), Paths.get(tlsDir, tlsFilePrefix + ".crt"));
        } catch (IOException e) {
            throw new JavaChainException("generateLocalMSP failed while move file: " + e.getMessage());
        }
        keyExport(tlsDir, Paths.get(tlsDir, tlsFilePrefix + ".key").toString(), tlsPrivKey);

    }

    public static void generateVerifyingMSP(String baseDir, CaHelper signCA, CaHelper tlsCA, boolean nodeOUs) throws JavaChainException {
        // create folder structure and write artifacts to proper locations
        createFolderStructure(baseDir, false);

        // the signing CaHelper certificate goes into cacerts
        x509Export(Paths.get(baseDir, "cacerts", x509Filename(signCA.getName())).toString(), signCA.getSignCert());

        // the TLS CaHelper certificate goes into tlscacerts
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

    private static void createFolderStructure(String rootDir, boolean local) throws JavaChainException {
        // create admincerts, cacerts, keystore and signcerts folders
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

    private static void x509Export(String path, Certificate cert) throws JavaChainException {
        try {
            pemExport(path, "CERTIFICATE", cert.getEncoded());
        } catch (Exception e) {
            throw new JavaChainException("An error occurred on x509Export:" + e.getMessage());
        }
    }

    private static void keyExport(String keystore, String output, IKey key) throws JavaChainException {

        String id = Hex.toHexString(key.ski());
        String keyPath = Paths.get(keystore, id + "_sk").toString();

        try {
            Files.move(Paths.get(keyPath), Paths.get(output));
        } catch (IOException e) {
            throw new JavaChainException("renamed unsuccessfully on keyExport");
        }
    }

    private static void pemExport(String path, String pemType, byte[] bytes) throws JavaChainException {
        Util.pemExport(path, pemType, bytes);
    }

    public static void exportConfig(String mspDir, String caFile, boolean enable) throws JavaChainException {

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
            throw new JavaChainException("An error occurred on exportConfig:" + e.getMessage());
        }
    }

}
