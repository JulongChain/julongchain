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
package org.bcia.julongchain.msp.mgmt;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.csp.factory.CspOptsManager;
import org.bcia.julongchain.csp.gm.dxct.GmCsp;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2PrivateKeyImportOpts;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2PublicKeyImportOpts;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.csp.intfs.ICsp;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.entity.IdentityIdentifier;
import org.bcia.julongchain.msp.entity.OUIdentifier;
import org.bcia.julongchain.msp.entity.VerifyOptions;
import org.bcia.julongchain.msp.signer.NodeSigner;
import org.bcia.julongchain.msp.util.LoadLocalMspFiles;
import org.bcia.julongchain.msp.util.MspConstant;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.msp.Identities;
import org.bcia.julongchain.protos.msp.MspConfigPackage;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/3/27
 * @company Dingxuan
 */
public class Msp implements IMsp {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Msp.class);
    private final static int JULONG_MSP = 0;
    private int mspVersion;
    private IIdentity[] rootCerts;
    private IIdentity[] intermediateCerts;
    private byte[][] tlsRootCerts;
    private byte[][] tlsIntermediateCerts;
    private HashMap<String, Boolean> certificationTreeInternalNodesMap;
    private SignIdentity signer;
    private IIdentity[] admins;
    private String name;
    private ICsp csp;
    private VerifyOptions verifyOptions;
    private CertificateList CRL[];
    private Map<String, Byte[][]> ouIdentifiers;
    private MspConfigPackage.FabricCryptoConfig fabricCryptoConfig;
    private boolean ouEnforcement;
    private OUIdentifier clientOU;
    private OUIdentifier nodeOU;
    private OUIdentifier orderOU;
    private MspConfigPackage.MSPConfig mspConfig;

    public Msp() {
    }

    public Msp(MspConfigPackage.MSPConfig config) {
        this.mspConfig = config;
    }

    @Override
    public IMsp setup(MspConfigPackage.MSPConfig config) {
        Msp msp = null;
        try {
            MspConfigPackage.FabricMSPConfig fabricMSPConfig = MspConfigPackage.FabricMSPConfig.parseFrom(config.getConfig());
            this.name = fabricMSPConfig.getName();
            this.csp = CspManager.getDefaultCsp();
            msp = internalSetupFunc(fabricMSPConfig);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (MspException e) {
            log.error(e.getMessage(), e);
        } catch (CspException e) {
            log.error(e.getMessage(), e);
        }
        return msp;
    }

    @Override
    public int getVersion() {
        return this.mspVersion;
    }

    @Override
    public int getType() {
        return JULONG_MSP;
    }

    @Override
    public String getIdentifier() {
        return this.name;
    }

    @Override
    public ISigningIdentity getSigningIdentity(IdentityIdentifier identityIdentifier) {
        return null;
    }

    @Override
    public ISigningIdentity getDefaultSigningIdentity() {
        return this.signer;
    }

    @Override
    public byte[][] getTLSRootCerts() {
        return this.tlsRootCerts;
    }

    @Override
    public byte[][] getTLSIntermediateCerts() {
        return this.tlsIntermediateCerts;
    }

    @Override
    public void validate(IIdentity id) throws MspException {
        MspValidate.validateIdentity(this, id);
    }

    @Override
    public void satisfiesPrincipal(IIdentity id, MspPrincipal.MSPPrincipal principal) throws IOException, MspException {
//        switch (principal.getPrincipalClassification()) {
//            case ROLE:
//                MspPrincipal.MSPRole mspRole = MspPrincipal.MSPRole.parseFrom(principal.toByteArray());
//                if (!mspRole.getMspIdentifier().equals(name)) {
//                    throw new MspException(String.format("the identity is a member of a different MSP (expected %s, got %s)", mspRole.getMspIdentifier(), id.getMSPIdentifier()));
//                }
//
//                switch (mspRole.getRole()) {
//                    case MEMBER:
//                        log.debug(String.format("Checking if identity satisfies MEMBER role for %s", name));
//                        validate(id);
//                        break;
//                    case ADMIN:
//                        log.debug(String.format("Checking if identity satisfies MEMBER role for %s", name));
//                        break;
//                    case CLIENT:
//                    case NODE:
//                        log.debug(String.format("Checking if identity satisfies role [%s] for %s", MspPrincipal.MSPRole.MSPRoleType.valueOf(mspRole.getRoleValue()), name));
//                        validate(id);
//                        break;
//                    default:
//                        throw new MspException(String.format("invalid MSP role type %d", mspRole.getRoleValue()));
//                }
//                break;
//            case IDENTITY:
//                Identity principalId = (Identity) deserializeIdentity(principal.getPrincipal().toByteArray());
//                if (((Identity) id).getCertificate().getEncoded().equals(principalId.getCertificate().getEncoded())) {
//                    principalId.validate();
//                } else {
//                    throw new MspException("The identities do not match");
//                }
//                break;
//
//            case ORGANIZATION_UNIT:
//                MspPrincipal.OrganizationUnit organizationUnit = MspPrincipal.OrganizationUnit.parseFrom(principal.getPrincipal().toByteArray());
//
//                if (!organizationUnit.getMspIdentifier().equals(name)) {
//                    throw new MspException(String.format("the identity is a member of a different MSP (expected %s, got %s)", organizationUnit.getMspIdentifier(), id.getMSPIdentifier()));
//                }
//                validate(id);
//
//                for (OUIdentifier ouIdentifiers : id.getOrganizationalUnits()) {
//                    if (ouIdentifiers.getCertifiersIdentifier().equals(organizationUnit.getOrganizationalUnitIdentifier()) && ouIdentifiers.getCertifiersIdentifier().equals(organizationUnit.getCertifiersIdentifier().toByteArray())) {
//                        //TODO 源码返回null
//                    }
//                }
//
//
//                break;
//            default:
//                throw new MspException(String.format("invalid principal type %d", principal.getPrincipalClassificationValue()));
//        }

    }


    public void hasOURole(IIdentity id, MspPrincipal.MSPRole.MSPRoleType mspRole) throws MspException {
        if (!ouEnforcement) {
            throw new MspException("NodeOus not activated, Cannot tell apart identities.");
        }
        log.debug(String.format("MSP %s checking if the identity is a client", name));

        if (id instanceof Identity) {
            Identity identity = (Identity) id;
        } else {
            throw new MspException("Identity type not recognized");
        }

    }


    public void hasOURoleInternal(Identity identity, MspPrincipal.MSPRole.MSPRoleType mspRole) throws MspException {
        String nodeOUValue = null;
        switch (mspRole) {
            case CLIENT:
                nodeOUValue = clientOU.getOrganizationalUnitIdentifier();
                break;
            case NODE:
                nodeOUValue = nodeOU.getOrganizationalUnitIdentifier();
                break;
            default:
                log.error("Invalid MSPRoleType. It must be CLIENT, node or ORDERER");
        }

        for (OUIdentifier ouIdentifiers : identity.getOrganizationalUnits()) {

            if (ouIdentifiers.getCertifiersIdentifier().equals(nodeOUValue)) {
                //TODO 源码中返回null
            }
        }
        throw new MspException(String.format("The identity does not contain OU [%s], MSP: [%s]", mspRole, name));
    }


    @Override
    public IIdentity deserializeIdentity(byte[] serializedIdentity) {
        try {
            Identities.SerializedIdentity sId = Identities.SerializedIdentity.parseFrom(serializedIdentity);
            Certificate cert = Certificate.getInstance(sId.getIdBytes().toByteArray());
            byte[] pbBytes = cert.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
            IKey certPubK = csp.keyImport(pbBytes, new SM2PublicKeyImportOpts(true));
            String identifier_Id;
            byte[] resultBytes = csp.hash(cert.toString().getBytes(), null);
            //转换成十六进制字符串表示
            identifier_Id = Hex.toHexString(resultBytes);
            IdentityIdentifier identityIdentifier = new IdentityIdentifier(sId.getMspid(), identifier_Id);
            IIdentity identity = new Identity(identityIdentifier, cert, certPubK, this);
            return identity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void isWellFormed(Identities.SerializedIdentity identity) {

    }


    IIdentity deserializeIdentityInternal(byte[] serializedIdentity) {

        return null;
    }

    public Msp internalSetupFunc(MspConfigPackage.FabricMSPConfig mspConfig) throws IOException, MspException, CspException {
        log.info("Setup the config by internalSetupFunc");
        preSetup(mspConfig);
        setupNodeOus(mspConfig);
        //  postSetup();
        return this;
    }

    private Msp internalValidateIdentityOusFunc() {
        return null;
    }

    public void preSetup(MspConfigPackage.FabricMSPConfig mspConfig) throws IOException, MspException, CspException {
        setupCrypto(mspConfig);
        setupCAs(mspConfig);
        setupAdmins(mspConfig);
        setupCRLs(mspConfig);
        finalizeSetupCAs(mspConfig);
        setupSigningIdentity(mspConfig);
        setupTLSCAs(mspConfig);
        setupOUs(mspConfig);
    }

    public void setupNodeOus(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public static void postSetup(MspConfigPackage.FabricMSPConfig mspConfig) {


    }


    public void setupCrypto(MspConfigPackage.FabricMSPConfig mspConfig) {
        this.fabricCryptoConfig = mspConfig.getCryptoConfig();

    }

    public void setupCAs(MspConfigPackage.FabricMSPConfig mspConfig) throws MspException {
        if (mspConfig.getRootCertsList().size() == 0) {
            throw new MspException("Expected at least one CA Cert");
        }

    }

    public void setupAdmins(MspConfigPackage.FabricMSPConfig mspConfig) throws IOException, MspException {

        IIdentity[] identities = new IIdentity[mspConfig.getAdminsList().size()];
        for (int i = 0; i < mspConfig.getAdminsList().size(); i++) {
            getIdentityFromConf(mspConfig.getSigningIdentity().getPublicSigner().toByteArray());
            Map<String, Object> identityMap = getIdentityFromConf(mspConfig.getAdmins(i).toByteArray());
            identities[i] = (IIdentity)identityMap.get("Identity");
        }
        setAdmins(identities);
    }

    public void setupCRLs(MspConfigPackage.FabricMSPConfig mspConfig) {
        //TODO 配置中没有CRL


    }

    public void finalizeSetupCAs(MspConfigPackage.FabricMSPConfig mspConfig) {
        //TODO 拼接根证书,中间证书

    }

    public void setupSigningIdentity(MspConfigPackage.FabricMSPConfig mspConfig) throws IOException, MspException, CspException {
        //通过配置获取签名者,并且赋值
        this.signer = getSigningIdentityFromConf(mspConfig.getSigningIdentity());
    }

    public void setupTLSCAs(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public void setupOUs(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public SignIdentity getSigningIdentityFromConf(MspConfigPackage.SigningIdentityInfo signingIdentityInfo) throws IOException, MspException, CspException {
        //通过配置获取sidinfo的publicSigner获取公钥和一个身份实例
        SignIdentity signIdentity = null;
        try {
            HashMap<String, Object> map = getIdentityFromConf(signingIdentityInfo.getPublicSigner().toByteArray());
            //TODO 源码中通过公钥的ski值，获取到私钥
            Identity id = (Identity) map.get(MspConstant.IDENTITY);

            //TODO 根据配置的密钥path,获取私钥
            String sk_path = CspOptsManager.getInstance().getDefaultFactoryOpts().getKeyStore();
            byte[] skBytes = new LoadLocalMspFiles().getSkFromDir(sk_path).get(0);

            IKey privateKey = null;
            try {
                privateKey = csp.keyImport(skBytes, new SM2PrivateKeyImportOpts(true));
            } catch (JavaChainException e) {
                log.error(e.getMessage());
            }
            signIdentity = new SignIdentity(new Identity(id.getCertificate(), id.getPk(), this), new NodeSigner(this.csp, privateKey), this);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return signIdentity;
    }


    public HashMap<String, Object> getIdentityFromConf(byte[] idBytes) throws IOException, MspException {
        if (idBytes.equals(null)) {
            throw new MspException("GetIdentityFromBytes error idBytes is null");
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        Certificate certificate = getCertFromPem(idBytes);
        //获取证书中的公钥
        byte[] pubKeyInfo = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        IKey certPubK = null;
        try {
            certPubK = csp.keyImport(pubKeyInfo, new SM2PublicKeyImportOpts(true));
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
        }
        map.put(MspConstant.PUBLIC_KEY, certPubK);
        Identity mspIdentity = new Identity(certificate, certPubK, this);
        map.put(MspConstant.IDENTITY, mspIdentity);
        return map;
    }

    public Certificate getCertFromPem(byte[] idBytes) throws IOException, MspException {
        Certificate certificate = null;
        if (idBytes.equals(null)) {
            throw new MspException("GetCertFrom Pem error the idBytes is null");
        }
        certificate = Certificate.getInstance(new PemReader
                (new InputStreamReader(new ByteArrayInputStream(idBytes))).readPemObject().getContent());
        return certificate;
    }


    public Certificate sanitizeCert(Certificate certificate) {
        return certificate;
    }

    public int getMspVersion() {
        return mspVersion;
    }

    public IIdentity[] getRootCerts() {
        return rootCerts;
    }

    public IIdentity[] getIntermediateCerts() {
        return intermediateCerts;
    }

    public byte[][] getTlsRootCerts() {
        return tlsRootCerts;
    }

    public byte[][] getTlsIntermediateCerts() {
        return tlsIntermediateCerts;
    }

    public HashMap<String, Boolean> getCertificationTreeInternalNodesMap() {
        return certificationTreeInternalNodesMap;
    }

    public SignIdentity getSigner() {
        return signer;
    }

    public IIdentity[] getAdmins() {
        return admins;
    }

    public String getName() {
        return name;
    }

    public ICsp getCsp() {
        return csp;
    }

    public VerifyOptions getVerifyOptions() {
        return verifyOptions;
    }

    public CertificateList[] getCRL() {
        return CRL;
    }

    public Map<String, Byte[][]> getOuIdentifiers() {
        return ouIdentifiers;
    }

    public MspConfigPackage.FabricCryptoConfig getFabricCryptoConfig() {
        return fabricCryptoConfig;
    }

    public boolean isOuEnforcement() {
        return ouEnforcement;
    }

    public OUIdentifier getClientOU() {
        return clientOU;
    }

    public OUIdentifier getNodeOU() {
        return nodeOU;
    }

    public OUIdentifier getOrderOU() {
        return orderOU;
    }

    public MspConfigPackage.MSPConfig getMspConfig() {
        return mspConfig;
    }

    public void setMspVersion(int mspVersion) {
        this.mspVersion = mspVersion;
    }

    public void setRootCerts(IIdentity[] rootCerts) {
        this.rootCerts = rootCerts;
    }

    public void setIntermediateCerts(IIdentity[] intermediateCerts) {
        this.intermediateCerts = intermediateCerts;
    }

    public void setTlsRootCerts(byte[][] tlsRootCerts) {
        this.tlsRootCerts = tlsRootCerts;
    }

    public void setTlsIntermediateCerts(byte[][] tlsIntermediateCerts) {
        this.tlsIntermediateCerts = tlsIntermediateCerts;
    }

    public void setCertificationTreeInternalNodesMap(HashMap<String, Boolean> certificationTreeInternalNodesMap) {
        this.certificationTreeInternalNodesMap = certificationTreeInternalNodesMap;
    }

    public void setSigner(SignIdentity signer) {
        this.signer = signer;
    }

    public void setAdmins(IIdentity[] admins) {
        this.admins = admins;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCsp(GmCsp csp) {
        this.csp = csp;
    }

    public void setVerifyOptions(VerifyOptions verifyOptions) {
        this.verifyOptions = verifyOptions;
    }

    public void setCRL(CertificateList[] CRL) {
        this.CRL = CRL;
    }

    public void setOuIdentifiers(Map<String, Byte[][]> ouIdentifiers) {
        this.ouIdentifiers = ouIdentifiers;
    }

    public void setFabricCryptoConfig(MspConfigPackage.FabricCryptoConfig fabricCryptoConfig) {
        this.fabricCryptoConfig = fabricCryptoConfig;
    }

    public void setOuEnforcement(boolean ouEnforcement) {
        this.ouEnforcement = ouEnforcement;
    }

    public void setClientOU(OUIdentifier clientOU) {
        this.clientOU = clientOU;
    }


    public void setNodeOU(OUIdentifier nodeOU) {
        this.nodeOU = nodeOU;
    }

    public void setOrderOU(OUIdentifier orderOU) {
        this.orderOU = orderOU;
    }

    public void setMspConfig(MspConfigPackage.MSPConfig mspConfig) {
        this.mspConfig = mspConfig;
    }
}
