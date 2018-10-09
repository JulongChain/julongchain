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
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.tools.cryptogen.sm2cert.SM2X509CertImpl;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2Key;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.util.IPAddress;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.security.x509.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.List;
import java.util.Random;

/**
 * CA 相关的密码材料生成
 *
 * @author chenhao, yegangcheng
 * @date 2018/4/3
 * @company Excelsecu
 */
public class CaHelper {
    private String mName;
    private String mCountry;
    private String mProvince;
    private String mLocality;
    private String mOrganizationalUnit;
    private String mStreetAddress;
    private String mPostalCode;
    private IKey mSigner;
    private Certificate mSignCert;

    private static JulongChainLog log = JulongChainLogFactory.getLog(CaHelper.class);
    private static String SEPARATOR = "/";


    public void setName(String name) {
        this.mName = name;
    }

    public CaHelper(String name,
                    String country,
                    String province,
                    String locality,
                    String organizationalUnit,
                    String streetAddress,
                    String postalCode,
                    IKey signer,
                    Certificate signCert) {
        this.mName = name;
        this.mCountry = country;
        this.mProvince = province;
        this.mLocality = locality;
        this.mOrganizationalUnit = organizationalUnit;
        this.mStreetAddress = streetAddress;
        this.mPostalCode = postalCode;
        this.mSigner = signer;
        this.mSignCert = signCert;
    }

    public X509Certificate signCertificate(String baseDir, String name, String[] ous, List<String> sans,
                                           ECPublicKey pub, int ku, int[] eku) throws JulongChainException {

        if (name.contains(SEPARATOR)) {
            throw new JulongChainException("the name is illegal");
        }

        try {
            X509CertInfo x509CertInfo = new X509CertInfo();
            x509CertInfo.set("version", new CertificateVersion(2));
            x509CertInfo.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & 2147483647));
            x509CertInfo.set("key", new CertificateX509Key(pub));

            String[] orgUnits;
            if (ous != null) {
                orgUnits = new String[ous.length + 1];
                System.arraycopy(ous, 0, orgUnits, 0, ous.length);
                orgUnits[ous.length] = mOrganizationalUnit;
            } else {
                orgUnits = new String[]{mOrganizationalUnit};
            }

            // 设置证书 organization 字段
            X500Name subject = CaHelper.subjectTemplateAdditional(name,
                    mCountry,
                    mProvince,
                    mLocality,
                    null,
                    orgUnits,
                    mStreetAddress,
                    mPostalCode);
            x509CertInfo.set("subject", subject);

            AlgorithmId algorithmId = SM2X509CertImpl.SM3_WITH_SM2_ALGORITHM_ID;
            x509CertInfo.set("algorithmID", new CertificateAlgorithmId(algorithmId));

            x509CertInfo.set("validity", Util.getCertificateValidity(10, 0, 0));

            X500Name issuer = new X500Name(mSignCert.getIssuer().toString());
            x509CertInfo.set("issuer", issuer);

            CertificateExtensions exts = new CertificateExtensions();

            exts.set("keyUsage", Util.parseKeyUsage(ku));

            if (eku != null && eku.length > 0) {
                exts.set("extendedKeyUsage", Util.parseExtendedKeyUsage(eku));
            }

            if (sans != null && sans.size() > 0) {
                GeneralNames generalNames = new GeneralNames();
                for (String san : sans) {
                    // 尝试解析为 IP 地址
                    if (IPAddress.isValid(san)) {
                        generalNames.add(new GeneralName(new IPAddressName(san)));
                    } else {
                        generalNames.add(new GeneralName(new DNSName(san)));
                    }
                }

                SubjectAlternativeNameExtension sanExt = new SubjectAlternativeNameExtension(false, generalNames);
                exts.set("subjectAlternativeName", sanExt);
            }

            x509CertInfo.set("extensions", exts);

            SM2X509CertImpl x509CertImpl = new SM2X509CertImpl(x509CertInfo);

            return genCertificateSM2(baseDir, name, x509CertImpl, algorithmId, mSigner);
        } catch (Exception e) {
            log.error("An error occurred on signCertificate:", e);
            throw new JulongChainException("An error occurred on signCertificate");
        }
    }


    public static CaHelper newCA(String baseDir, String org, String name, String country, String province, String locality,
                                 String orgUnit, String streetAddress, String postalCode) throws JulongChainException {

        FileUtil.mkdirAll(Paths.get(baseDir));

        IKey privateKey = CspHelper.generatePrivateKey(baseDir);

        ECPublicKey ecPublicKey = CspHelper.getSM2PublicKey(privateKey);

        try {
            if (!(privateKey instanceof SM2Key)) {
                throw new JulongChainException("privateKey is not the instance of SM2Key");
            }

            X509CertInfo x509CertInfo = new X509CertInfo();
            x509CertInfo.set("version", new CertificateVersion(2));
            x509CertInfo.set("serialNumber",
                    new CertificateSerialNumber((new Random()).nextInt() & 2147483647));

            x509CertInfo.set("key", new CertificateX509Key(ecPublicKey));

            // 设置证书 organization 字段
            X500Name subject = subjectTemplateAdditional(name,
                    country,
                    province,
                    locality,
                    org,
                    new String[]{orgUnit},
                    streetAddress,
                    postalCode);
            x509CertInfo.set("subject", subject);
            x509CertInfo.set("issuer", subject);

            AlgorithmId algorithmId = SM2X509CertImpl.SM3_WITH_SM2_ALGORITHM_ID;
            x509CertInfo.set("algorithmID", new CertificateAlgorithmId(algorithmId));

            x509CertInfo.set("validity", Util.getCertificateValidity(10, 0, 0));

            CertificateExtensions exts = new CertificateExtensions();
            exts.set("keyUsage", Util.parseKeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.keyEncipherment | KeyUsage.cRLSign));
            exts.set("extendedKeyUsage", Util.parseExtendedKeyUsage(new int[]{Util.EXT_KEY_USAGE_ANY}));
            x509CertInfo.set("extensions", exts);

            exts.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension(privateKey.ski()));

            SM2X509CertImpl caCert = new SM2X509CertImpl(x509CertInfo);
            genCertificateSM2(baseDir, name, caCert, algorithmId, privateKey);

            return new CaHelper(name,
                    country,
                    province,
                    locality,
                    orgUnit,
                    streetAddress,
                    postalCode,
                    privateKey,
                    Certificate.getInstance(caCert.getEncoded()));
        } catch (Exception e) {
            throw new JulongChainException("An error occurred on newCA: " + e.getMessage());
        }

    }

    private static X500Name subjectTemplateAdditional(String commonName, String country, String province, String locality,
                                                      String org, String[] orgUnit, String streetAddress, String postalCode) {
        X500Name x500Name = null;
        StringBuilder sb = new StringBuilder();
        if (commonName != null) {
            sb.append("CN=").append(commonName);
        }
        if (org != null) {
            sb.append(",O=").append(org);
        }
        if (country != null) {
            sb.append(",C=").append(country);
        }
        if (orgUnit != null) {
            for (String ou : orgUnit) {
                sb.append(",OU=").append(ou);
            }
        }
        if (locality != null) {
            sb.append(",L=").append(locality);
        }
        if (province != null) {
            sb.append(",ST=").append(province);
        }
        if (streetAddress != null) {
            sb.append(",STREET=").append(streetAddress);
        }
        try {
            x500Name = new X500Name(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return x500Name;
    }

    private static X509Certificate genCertificateSM2(String baseDir, String name, SM2X509CertImpl template,
                                                     AlgorithmId algorithmId, IKey privateKey) throws JulongChainException {

        try {
            template.sm2Sign(privateKey, algorithmId);
        } catch (Exception e) {
            throw new JulongChainException("An error occurred on genCertificateSM2:" + e);
        }

        String path = Paths.get(baseDir, name + "-cert.pem").toString();
        try {
            byte[] certBytes = template.getEncoded();
            Util.pemExport(path, "CERTIFICATE", certBytes);
        } catch (Exception e) {
            log.error("An error occurred on genCertificateSM2:{}", e.getMessage());
        }
        return template;
    }

    public static Certificate loadCertificateSM2(String certPath) throws JulongChainException {
        File certDir = new File(certPath);
        File[] files = certDir.listFiles();
        if (!certDir.isDirectory() || files == null) {
            log.error("invalid directory for certPath " + certPath);
            return null;
        }
        for (File file : files) {
            if (!file.getName().endsWith(".pem")) {
                continue;
            }
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                PemReader pemReader = new PemReader(reader);
                PemObject pemObject = pemReader.readPemObject();
                reader.close();
                byte[] certBytes = pemObject.getContent();
                return Certificate.getInstance(certBytes);
            } catch (Exception e) {
                throw new JulongChainException("An error occurred :" + e.getMessage());
            }
        }
        throw new JulongChainException("no pem file found");
    }

    public String getName() {
        return mName;
    }

    public IKey getSigner() {
        return mSigner;
    }

    public Certificate getSignCert() {
        return mSignCert;
    }
}
