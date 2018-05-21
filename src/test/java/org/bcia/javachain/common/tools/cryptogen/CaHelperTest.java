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
package org.bcia.javachain.common.tools.cryptogen;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.tools.cryptogen.bean.Subject;
import org.bcia.javachain.csp.intfs.IKey;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.junit.Assert;
import org.junit.Test;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.X509CertImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class CaHelperTest {

    private static final String testCANAme = "root0";
    private static final String testCA2Name = "root1";
    private static final String testCA3Name = "root2";
    private static final String testName = "cert0";
    private static final String testName2 = "cert1";
    private static final String testName3 = "cert2";
    private static final String testIP = "192.168.1.1";
    private static final String testCountry = "China";
    private static final String testProvince = "Guangdong";
    private static final String testLocality = "Shen Zhen City";
    private static final String testOrganizationalUnit = "BCIA";
    private static final String testStreetAddress = "testStreetAddress";
    private static final String testPostalCode = "123456";

    private String testDir;

    {
        try {
            Path tempDirPath = Files.createTempDirectory(null);
            testDir = Paths.get(tempDirPath.toString(), "ca-test").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void newCA() {

        System.out.println("testDir=" + testDir);
        String caDir = Paths.get(testDir, "ca").toString();

        CaHelper rootCA = null;
        try {
            rootCA = CaHelper.newCA(caDir,
                    testCANAme,
                    testCANAme,
                    testCountry,
                    testProvince,
                    testLocality,
                    testOrganizationalUnit,
                    testStreetAddress,
                    testPostalCode);
        } catch (JavaChainException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(rootCA);
        Assert.assertNotNull(rootCA.getSigner());

        //check to make sure the root public key was stored
        File pemFile = new File(Paths.get(caDir, testCANAme + "-cert.pem").toString());
        Assert.assertTrue(pemFile.exists());

        String subjectDN = rootCA.getSignCert().getSubject().toString();

        Assert.assertEquals(testCountry, X509CertificateUtil.getSubject(subjectDN).getCountry().get(0));
        Assert.assertEquals(testProvince, X509CertificateUtil.getSubject(subjectDN).getStateOrProvince().get(0));
        Assert.assertEquals(testLocality, X509CertificateUtil.getSubject(subjectDN).getLocality().get(0));
        Assert.assertEquals(testOrganizationalUnit, X509CertificateUtil.getSubject(subjectDN).getOrganizationalUnit().get(0));
        Assert.assertEquals(testStreetAddress, X509CertificateUtil.getSubject(subjectDN).getStreetAddress().get(0));
        //Assert.assertEquals(testPostalCode, X509CertificateUtil.getSubject(subjectDN).getPostalCode().get(0));

        //FileUtil.removeAll(testDir);

    }

    private int parseKeyUsage(boolean[] bKeyUsage) {
        int[] value = new int[]{128, 64, 32, 16, 8, 4, 2, 1, 32768};
        int keyUsage = 0;
        for (int i = 0; i < bKeyUsage.length; i++) {
            if (bKeyUsage[i]) {
                keyUsage += value[i];
            }
        }
        return keyUsage;
    }


    @Test
    public void loadCertificateSM2() throws Exception {

        String caDir = Paths.get(testDir, "ca").toString();
        String certDir = Paths.get(testDir, "certs").toString();

        //generate private key
        IKey priv = CspHelper.generatePrivateKey(certDir);

        //get EC public key
        ECPublicKey ecPubKey = CspHelper.getSM2PublicKey(priv);
        Assert.assertNotNull(ecPubKey);

        //create our CaHelper
        CaHelper rootCA = CaHelper.newCA(caDir,
                testCA3Name,
                testCA3Name,
                testCountry,
                testProvince,
                testLocality,
                testOrganizationalUnit,
                testStreetAddress,
                testPostalCode);

        X509Certificate cert = rootCA.signCertificate(certDir,
                testName3,
                null,
                null,
                ecPubKey,
                KeyUsage.digitalSignature | KeyUsage.keyEncipherment,
                new int[]{Util.extKeyUsageAny});

        try {
            KeyUsageExtension keyUsageExt = (KeyUsageExtension) X509CertImpl.toImpl(cert).getExtension(new ObjectIdentifier(new int[]{2,5,29,15}));
            Assert.assertEquals(KeyUsage.digitalSignature | KeyUsage.keyEncipherment,
                    parseKeyUsage(keyUsageExt.getBits()));
        } catch (Exception e) {
            Assert.fail();
        }

        if (!certDir.endsWith(File.separator)) {
            certDir += File.separator;
        }
        Certificate bcCert = Certificate.getInstance(cert.getEncoded());
        Certificate loadedCert = CaHelper.loadCertificateSM2(certDir);
        Assert.assertNotNull(loadedCert);
        Assert.assertEquals(bcCert.getSerialNumber(), loadedCert.getSerialNumber());
        Assert.assertEquals(X509CertificateUtil.getSubject(cert.getSubjectDN().getName()).getCommonName(),
                X509CertificateUtil.getSubject(loadedCert.getSubject().toString()).getCommonName());

        FileUtil.removeAll(testDir);
    }

    @Test
    public void signCertificate() throws JavaChainException {

        System.out.println("testDir=" + testDir);

        String caDir = Paths.get(testDir, "ca").toString();
        String certDir = Paths.get(testDir, "certs").toString();
        System.out.println(testDir);

        //generate private key
        IKey priv = CspHelper.generatePrivateKey(certDir);

        //get EC public key
        ECPublicKey ecPubKey = CspHelper.getSM2PublicKey(priv);
        Assert.assertNotNull(ecPubKey);

        //create our CaHelper
        CaHelper rootCA = CaHelper.newCA(caDir,
                testCA2Name,
                testCA2Name,
                testCountry,
                testProvince,
                testLocality,
                testOrganizationalUnit,
                testStreetAddress,
                testPostalCode);

        X509Certificate cert;
        cert = rootCA.signCertificate(certDir,
                testName,
                null,
                null,
                ecPubKey,
                KeyUsage.digitalSignature | KeyUsage.keyEncipherment,
                new int[]{Util.extKeyUsageAny});

        // KeyUsage should be x509.KeyUsageDigitalSignature | x509.KeyUsageKeyEncipherment
        try {
            KeyUsageExtension keyUsageExt = (KeyUsageExtension) X509CertImpl.toImpl(cert).getExtension(new ObjectIdentifier(new int[]{2,5,29,15}));
            Assert.assertEquals(KeyUsage.digitalSignature | KeyUsage.keyEncipherment,
                    parseKeyUsage(keyUsageExt.getBits()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }


        // make sure ous are correctly set
        String[] ous = new String[]{"TestOU", "PeerOU"};
        cert = rootCA.signCertificate(certDir,
                testName,
                ous,
                null,
                ecPubKey,
                KeyUsage.digitalSignature,
                new int[]{});
        Subject subject = X509CertificateUtil.getSubject(cert.getSubjectDN().toString());
        List<String> ousList = subject.getOrganizationalUnit();
        Assert.assertTrue(ousList.contains(ous[0]));
        Assert.assertTrue(ousList.contains(ous[1]));

        // make sure sans are correctly set
        List<String> sans = new ArrayList<>();
        sans.add(testName2);
        sans.add(testIP);
        cert = rootCA.signCertificate(certDir,
                testName,
                null,
                sans,
                ecPubKey,
                KeyUsage.digitalSignature,
                new int[]{Util.extKeyUsageClientAuth, Util.extKeyUsageServerAuth});

        // make sure sans are correctly set
        boolean containDNSName = false;
        boolean containIPAddress = false;
        try {
            Collection altName = cert.getSubjectAlternativeNames();
            for (Object anAltName : altName) {
                List list = (List) anAltName;
                if (list.contains(testName2)) {
                    containDNSName = true;
                }
                if (list.contains(testIP)) {
                    containIPAddress = true;
                }
            }
        } catch (CertificateParsingException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(containDNSName);
        Assert.assertTrue(containIPAddress);

        // check to make sure the signed public key was stored
        String pemFile = Paths.get(certDir , testName + "-cert.pem").toString();
        File file = new File(pemFile);
        Assert.assertTrue(file.exists());

        boolean finishSign;
        try {
            rootCA.signCertificate(certDir,
                    "empty/CaHelper",
                    null,
                    sans,
                    ecPubKey,
                    KeyUsage.digitalSignature,
                    new int[]{});
            finishSign = true;
        } catch (JavaChainException e) {
            finishSign = false;
        }
        Assert.assertFalse(finishSign);

        FileUtil.removeAll(testDir);
    }
}