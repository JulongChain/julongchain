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

import org.apache.commons.io.FileUtils;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.tools.cryptogen.bean.Configuration;
import org.bcia.julongchain.common.tools.cryptogen.utils.X509CertificateUtil;
import org.bcia.julongchain.msp.mgmt.Msp;
import org.bcia.julongchain.msp.util.MspConfigBuilder;
import org.bcia.julongchain.protos.msp.MspConfigPackage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;


/**
 * MspHelper 测试类
 *
 * @author chenhao, liuxifeng
 * @date 2018/7/12
 * @company Excelsecu
 */
public class MspHelperTest {

    private static final String testCAOrg = "example.com";
    private static final String testCAName = "ca" + "." + testCAOrg;
    private static final String testName = "node0";
    private static final String testCountry = "China";
    private static final String testProvince = "Guangdong";
    private static final String testLocality = "ShenZhen";
    private static final String testOrganizationUnit = "BCIA";
    private static final String testStreetAddress = "testStreetAddress";
    private static final String testPostalCode = "123456";
    private String testDir;

    {
        try {
            Path tempDirPath = Files.createTempDirectory(null);
            testDir = Paths.get(tempDirPath.toString(), "msp-test").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Rule
    public ExpectedException expectedRule = ExpectedException.none();

    @Test
    public void generateLocalMSP() throws JavaChainException, IOException {
        FileUtil.removeAll(testDir);

        String caDir = Paths.get(testDir, "ca").toString();
        String tlsCADir = Paths.get(testDir, "tlsca").toString();
        String mspDir = Paths.get(testDir, "msp").toString();
        String tlsDir = Paths.get(testDir, "tls").toString();
        // generate  CaHelper
        CaHelper signCA = CaHelper.newCA(caDir, testCAOrg, testCAName, testCountry, testProvince, testLocality, testOrganizationUnit, testStreetAddress, testPostalCode);
        CaHelper tlsCA = CaHelper.newCA(tlsCADir, testCAOrg, testCAName, testCountry, testProvince, testLocality, testOrganizationUnit, testStreetAddress, testPostalCode);

        String subjectDN = signCA.getSignCert().getSubject().toString();
        Assert.assertEquals(testCountry, X509CertificateUtil.getSubject(subjectDN).getCountry().get(0));
        Assert.assertEquals(testProvince, X509CertificateUtil.getSubject(subjectDN).getStateOrProvince().get(0));
        Assert.assertEquals(testLocality, X509CertificateUtil.getSubject(subjectDN).getLocality().get(0));
        Assert.assertEquals(testOrganizationUnit, X509CertificateUtil.getSubject(subjectDN).getOrganizationalUnit().get(0));
        Assert.assertEquals(testStreetAddress, X509CertificateUtil.getSubject(subjectDN).getStreetAddress().get(0));
        // generate local MSP for nodeType=NODE
        MspHelper.generateLocalMSP(testDir, testName, null, signCA, tlsCA, MspHelper.NODE, true);
        // check to see that the right files were generated/saved
        List<String> mspFiles = new ArrayList<>();
        mspFiles.add(Paths.get(mspDir, "admincerts", testName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "cacerts", testCAName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "tlscacerts", testCAName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "keystore").toString());
        mspFiles.add(Paths.get(mspDir, "signcerts", testName + "-cert.pem").toString());
        mspFiles.add(Paths.get(mspDir, "config.yaml").toString());

        List<String> tlsFiles = new ArrayList<>();
        tlsFiles.add(Paths.get(tlsDir, "ca.crt").toString());
        tlsFiles.add(Paths.get(tlsDir, "server.key").toString());
        tlsFiles.add(Paths.get(tlsDir, "server.crt").toString());

        for (String mspFile : mspFiles) {
            assertTrue(new File(mspFile).exists());
        }
        for (String tlsFile : tlsFiles) {
            assertTrue(new File(tlsFile).exists());
        }
        // generate local MSP for nodeType = CLIENT
        MspHelper.generateLocalMSP(testDir, testName, null, signCA, tlsCA, MspHelper.CLIENT, true);
        tlsFiles = new ArrayList<>();
        tlsFiles.add(Paths.get(tlsDir, "ca.crt").toString());
        tlsFiles.add(Paths.get(tlsDir, "client.key").toString());
        tlsFiles.add(Paths.get(tlsDir, "client.crt").toString());

        for (String tlsFile : tlsFiles) {
            assertTrue(new File(tlsFile).exists());
        }
        // finally check to see if we can load this as a local MSP config
        setupMspConfig(mspDir);
    }

    @Test
    public void generateVerifyingMSP() throws JavaChainException, IOException {
        System.out.println("testDir=" + testDir);

        String caDir = Paths.get(testDir, "ca").toString();
        String tlsCADir = Paths.get(testDir, "tlsca").toString();
        String mspDir = Paths.get(testDir, "msp").toString();
        //generate  CaHelper
        CaHelper signCA = CaHelper.newCA(caDir,
                testCAOrg,
                testCAName,
                testCountry,
                testProvince,
                testLocality,
                testOrganizationUnit,
                testStreetAddress,
                testPostalCode);
        CaHelper tlsCA = CaHelper.newCA(tlsCADir,
                testCAOrg,
                testCAName,
                testCountry,
                testProvince,
                testLocality,
                testOrganizationUnit,
                testStreetAddress,
                testPostalCode);
        MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, true);

        List<String> files = new ArrayList<>();
        files.add(Paths.get(mspDir, "admincerts", testCAName + "-cert.pem").toString());
        files.add(Paths.get(mspDir, "cacerts", testCAName + "-cert.pem").toString());
        files.add(Paths.get(mspDir, "tlscacerts", testCAName + "-cert.pem").toString());
        files.add(Paths.get(mspDir, "config.yaml").toString());

        for (String file : files) {
            assertTrue(new File(file).exists());
        }
        // finally check to see if we can load this as a verifying MSP config
        expectedRule.expect(JavaChainException.class);
        expectedRule.expectMessage("the name is illegal");
        tlsCA.setName("test/fail");
        MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, true);
        signCA.setName("test/fail");
        MspHelper.generateVerifyingMSP(mspDir, signCA, tlsCA, true);

        FileUtil.removeAll(testDir);
    }


    @Test
    public void exportConfig() throws JavaChainException {

        String path = Paths.get(testDir, "export-test").toString();
        String configFile = Paths.get(path, "config.yaml").toString();
        String caFile = "ca.pem";
        System.out.print(path);

        FileUtil.mkdirAll(Paths.get(path));

        try {
            MspHelper.exportConfig(path, caFile, true);
        } catch (JavaChainException e) {
            throw new JavaChainException("failed to read config file: {}", e);
        }
        Yaml yaml = new Yaml();
        Configuration configuration;
        try {
            Map<String, Object> map = yaml.load(new FileInputStream(configFile));
            configuration = Configuration.parse(map);
        } catch (FileNotFoundException e) {
            throw new JavaChainException(configFile + " is not found");
        }
        assertTrue(configuration.getNodeOUs().getEnable());
        Assert.assertEquals(caFile, configuration.getNodeOUs().getClientOUIdentifier().getCertificate());
        Assert.assertEquals(MspHelper.CLIENT_OU, configuration.getNodeOUs().getClientOUIdentifier().getOrganizationalUnitIdentifier());
        Assert.assertEquals(caFile, configuration.getNodeOUs().getNodeOUIdentifier().getCertificate());
        Assert.assertEquals(MspHelper.NODE_OU, configuration.getNodeOUs().getNodeOUIdentifier().getOrganizationalUnitIdentifier());
    }

    private void setupMspConfig(String mspDir) throws IOException {
        List<String> caCert = new ArrayList<>();
        File caCertFile = new File(Paths.get(mspDir, "cacerts").toString());
        for (File file : Objects.requireNonNull(caCertFile.listFiles())) {
            caCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> adminCert = new ArrayList<>();
        File adminCertFile = new File(Paths.get(mspDir, "admincerts").toString());
        for (File file : Objects.requireNonNull(adminCertFile.listFiles())) {
            adminCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> keyStore = new ArrayList<>();
        File keyStoreFile = new File(Paths.get(mspDir, "keystore").toString());
        for (File file : Objects.requireNonNull(keyStoreFile.listFiles())) {
            keyStore.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> tlsCaCert = new ArrayList<>();
        File tlsCertFile = new File(Paths.get(mspDir, "tlscacerts").toString());
        for (File file : Objects.requireNonNull(tlsCertFile.listFiles())) {
            tlsCaCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        List<String> configContent = new ArrayList<>();
        File configFile = new File(Paths.get(mspDir, "config.yaml").toString());
        configContent.add(FileUtils.readFileToString(configFile, Charset.forName("UTF-8")));

        List<String> signCert = new ArrayList<>();
        File signCertFile = new File(Paths.get(mspDir, "signcerts").toString());
        for (File file : Objects.requireNonNull(signCertFile.listFiles())) {
            signCert.add(FileUtils.readFileToString(file, Charset.forName("UTF-8")));
        }

        MspConfigPackage.MSPConfig mspConfig = MspConfigBuilder.buildMspConfig(
                "testMsp", caCert, keyStore, signCert, adminCert, new ArrayList<>(), new ArrayList<>(), configContent, tlsCaCert, new ArrayList<>());
        Msp msp = new Msp();
        msp.setup(mspConfig);
    }
}