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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang.ArrayUtils;
import org.bcia.julongchain.common.exception.CspException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
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
import org.bouncycastle.util.io.pem.PemReader;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bcia.julongchain.msp.mgmt.MspValidate.validateIdentityOus;

/**
 * @author zhangmingyang
 * @Date: 2018/3/27
 * @company Dingxuan
 */
public class Msp implements IMsp {
    private static JulongChainLog log = JulongChainLogFactory.getLog(Msp.class);
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
    private Map<String, byte[][]> ouIdentifiers = new HashMap<>();
    private MspConfigPackage.JuLongCryptoConfig juLongCryptoConfig;
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
            MspConfigPackage.JuLongMSPConfig fabricMSPConfig = MspConfigPackage.JuLongMSPConfig.parseFrom(config.getConfig());
            this.name = fabricMSPConfig.getName();
            this.csp = CspManager.getDefaultCsp();
            msp = internalSetupFunc(fabricMSPConfig);
            Identity id = (Identity) msp.getDefaultSigningIdentity().getIdentity();
            validateIdentityOus(this, id);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
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
        if (id instanceof Identity) {
            Identity identity = (Identity) id;
            MspValidate.validateIdentity(this, identity);
        }

    }

    @Override
    public void satisfiesPrincipal(IIdentity id, MspPrincipal.MSPPrincipal principal) throws MspException {
        switch (principal.getPrincipalClassification()) {
            case ROLE:
                MspPrincipal.MSPRole mspRole = null;
                try {
                    mspRole = MspPrincipal.MSPRole.parseFrom(principal.getPrincipal().toByteArray());
                } catch (InvalidProtocolBufferException e) {
                    throw new MspException(e.getMessage());
                }
                log.info(String.format("MspRole type is %s", mspRole.getMspIdentifier()));
                if (!mspRole.getMspIdentifier().equals(name)) {
                    throw new MspException(String.format("the identity is a member of a different MSP (expected %s, got %s)", mspRole.getMspIdentifier(), id.getMSPIdentifier()));
                }
                switch (mspRole.getRole()) {
                    case MEMBER:
                        log.debug(String.format("Checking if identity satisfies MEMBER role for %s", name));
                        validate(id);
                        break;
                    case ADMIN:
                        log.debug(String.format("Checking if identity satisfies MEMBER role for %s", name));
                        for (IIdentity identity : admins) {
                            if (identity instanceof Identity && id instanceof Identity) {
                                Identity identityInstance = (Identity) identity;
                                Identity iden = (Identity) id;
                                if (identityInstance.getCertificate().equals(iden.getCertificate())) {
                                    log.info("This identity's cert is admin cert");
                                } else {
                                    throw new MspException("This identity is not an admin");
                                }
                            } else {
                                throw new MspException("This identity is not an admin");
                            }
                        }
                        break;
                    case CLIENT:

                    case NODE:
                        log.debug(String.format("Checking if identity satisfies role [%s] for %s", MspPrincipal.MSPRole.MSPRoleType.valueOf(mspRole.getRoleValue()), name));
                        validate(id);
                        hasOURole(id, mspRole.getRole());
                        break;
                    default:
                        throw new MspException(String.format("invalid MSP role type %d", mspRole.getRoleValue()));
                }
                break;
            case IDENTITY:
                Identity principalId = (Identity) deserializeIdentity(principal.getPrincipal().toByteArray());
                if (((Identity) id).getCertificate().equals(principalId.getCertificate())) {
                    principalId.validate();
                } else {
                    throw new MspException("The identities do not match");
                }
                break;

            case ORGANIZATION_UNIT:
                MspPrincipal.OrganizationUnit organizationUnit = null;
                try {
                    organizationUnit = MspPrincipal.OrganizationUnit.parseFrom(principal.getPrincipal().toByteArray());
                } catch (InvalidProtocolBufferException e) {
                    throw new MspException(e.getMessage());
                }

                if (!organizationUnit.getMspIdentifier().equals(name)) {
                    throw new MspException(String.format("the identity is a member of a different MSP (expected %s, got %s)", organizationUnit.getMspIdentifier(), id.getMSPIdentifier()));
                }
                validate(id);
                for (OUIdentifier ouIdentifiers : id.getOrganizationalUnits()) {
                    if (ouIdentifiers.getCertifiersIdentifier().equals(organizationUnit.getOrganizationalUnitIdentifier()) && ouIdentifiers.getCertifiersIdentifier().equals(organizationUnit.getCertifiersIdentifier().toByteArray())) {
                    } else {
                        throw new MspException("The identities do not match");
                    }
                }
                break;
            default:
                throw new MspException(String.format("invalid principal type %d", principal.getPrincipalClassificationValue()));
        }

    }

    public void hasOURole(IIdentity id, MspPrincipal.MSPRole.MSPRoleType mspRole) throws MspException {
        if (!ouEnforcement) {
            throw new MspException("NodeOus not activated, Cannot tell apart identities.");
        }
        log.debug(String.format("MSP %s checking if the identity is a client", name));

        if (id instanceof Identity) {
            Identity identity = (Identity) id;
            hasOURoleInternal(identity, mspRole);
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

            if (nodeOUValue.equals(ouIdentifiers.getOrganizationalUnitIdentifier())) {
                return;
            }
        }
        throw new MspException(String.format("The identity does not contain OU [%s], MSP: [%s]", mspRole, name));
    }

    @Override
    public IIdentity deserializeIdentity(byte[] serializedIdentity) throws MspException {

        Identities.SerializedIdentity sId = null;
        try {
            sId = Identities.SerializedIdentity.parseFrom(serializedIdentity);
        } catch (InvalidProtocolBufferException e) {
            throw new MspException(e.getMessage());
        }
        if (!sId.getMspid().equals(name)) {
            throw new MspException(String.format("expected MSP ID %s, received %s", name, sId.getMspid()));
        }
        return deserializeIdentityInternal(sId.getIdBytes().toByteArray());
    }

    public IIdentity deserializeIdentityInternal(byte[] serializedIdentity) throws MspException {
        Certificate cert = Certificate.getInstance(serializedIdentity);
        byte[] pbBytes = cert.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        IKey certPubK = null;
        try {
            certPubK = csp.keyImport(pbBytes, new SM2PublicKeyImportOpts(true));
        } catch (JulongChainException e) {
            throw new MspException(e.getMessage());
        }
        IIdentity identity = new Identity(cert, certPubK, this);
        return identity;
    }

    @Override
    public void isWellFormed(Identities.SerializedIdentity identity) {
        //identity.getIdBytes()
    }

    public Msp internalSetupFunc(MspConfigPackage.JuLongMSPConfig mspConfig) throws IOException, MspException, CspException {
        log.info("Setup the MspConfig by internalSetupFunc");
        preSetup(mspConfig);
        setupNodeOus(mspConfig);
        postSetup(mspConfig);
        return this;
    }

    public void preSetup(MspConfigPackage.JuLongMSPConfig mspConfig) throws IOException, MspException, CspException {
        setupCrypto(mspConfig);
        setupCAs(mspConfig);
        setupAdmins(mspConfig);
        setupCRLs(mspConfig);
        finalizeSetupCAs(mspConfig);
        setupSigningIdentity(mspConfig);
        setupTLSCAs(mspConfig);
        setupOUs(mspConfig);
    }

    /**
     * 装载节点组织单元
     *
     * @param juLongMspConfig
     * @throws MspException
     */
    public void setupNodeOus(MspConfigPackage.JuLongMSPConfig juLongMspConfig) throws MspException {
        if (juLongMspConfig.getJuLongNodeOUs() != null) {
            this.ouEnforcement = juLongMspConfig.getJuLongNodeOUs().getEnable();

            //clientOU
            OUIdentifier clientOUIdentifier = new OUIdentifier();

            clientOUIdentifier.setOrganizationalUnitIdentifier(juLongMspConfig.getJuLongNodeOUs()
                    .getClientOUIdentifier().getOrganizationalUnitIdentifier());

            clientOUIdentifier.setCertifiersIdentifier(getCertifiersIdentifier(juLongMspConfig.getJuLongNodeOUs().getClientOUIdentifier().getCertificate().toByteArray()));
            this.clientOU = clientOUIdentifier;

            //nodeOU
            OUIdentifier nodeOUIdentifier = new OUIdentifier();
            nodeOUIdentifier.setOrganizationalUnitIdentifier(juLongMspConfig.getJuLongNodeOUs().getNodeOUIdentifier().getOrganizationalUnitIdentifier());
            if (juLongMspConfig.getJuLongNodeOUs().getNodeOUIdentifier().getCertificate().size() != 0) {
                byte[] nodeCert = getCertifiersIdentifier(juLongMspConfig.getJuLongNodeOUs().getNodeOUIdentifier().getCertificate().toByteArray());

                nodeOUIdentifier.setCertifiersIdentifier(nodeCert);
                this.nodeOU = nodeOUIdentifier;
            }
        } else {
            this.ouEnforcement = false;
        }
    }

    public void postSetup(MspConfigPackage.JuLongMSPConfig mspConfig) throws MspException {
        if (!ouEnforcement) {
            for (IIdentity admin : admins) {
                try {
                    validate(admin);
                } catch (MspException e) {
                    throw new MspException(String.format("admin is invalid"));
                }
            }
        }

        // Check that admins are clients
        MspPrincipal.MSPRole.Builder princialBytes = MspPrincipal.MSPRole.newBuilder()
                .setMspIdentifier(name)
                .setRole(MspPrincipal.MSPRole.MSPRoleType.CLIENT);

        MspPrincipal.MSPPrincipal.Builder principal = MspPrincipal.MSPPrincipal.newBuilder()
                .setPrincipal(ByteString.copyFrom(princialBytes.build().toByteArray()))
                .setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE);

        for (IIdentity admin : admins) {
            try {
                satisfiesPrincipal(admin, principal.build());
            } catch (MspException e) {
                throw new MspException(e.getMessage());
            }
        }

    }

    public void setupCrypto(MspConfigPackage.JuLongMSPConfig mspConfig) {
        this.juLongCryptoConfig = mspConfig.getCryptoConfig();
    }

    /**
     * 装载根CA证书
     * @param mspConfig
     * @throws MspException
     */
    public void setupCAs(MspConfigPackage.JuLongMSPConfig mspConfig) throws MspException {
        if (mspConfig.getRootCertsList().size() == 0) {
            throw new MspException("Expected at least one CA Cert");
        }
        //根证书转换IIdentity
        IIdentity[] identities = new IIdentity[mspConfig.getRootCertsList().size()];
        List<ByteString> rootCerts = mspConfig.getRootCertsList();
        for (int i = 0; i < rootCerts.size(); i++) {
            Map<String, Object> rootCertMap = getIdentityFromConf(rootCerts.get(i).toByteArray());
            identities[i] = (IIdentity) rootCertMap.get(MspConstant.IDENTITY);
        }
        setRootCerts(identities);
    }

    /**
     * 装载admin证书
     * @param mspConfig
     * @throws IOException
     * @throws MspException
     */
    public void setupAdmins(MspConfigPackage.JuLongMSPConfig mspConfig) throws IOException, MspException {

        IIdentity[] identities = new IIdentity[mspConfig.getAdminsList().size()];
        for (int i = 0; i < mspConfig.getAdminsList().size(); i++) {
            Map<String, Object> identityMap = getIdentityFromConf(mspConfig.getAdmins(i).toByteArray());
            identities[i] = (IIdentity) identityMap.get(MspConstant.IDENTITY);
        }
        this.admins = identities;
    }

    /**
     * 获取证书链的身份标识
     * @param identity
     * @return
     * @throws MspException
     */
    public byte[] getCertChainIdentifier(IIdentity identity) throws MspException {
        X509Certificate[] chain = getCertChain(identity);
        if (chain == null) {
            throw new MspException(String.format("Failed getting certification chain for %s", identity.getMSPIdentifier()));
        }
        return getCertChainIdentifierFromChain(chain);
    }

    /**
     * 获取证书链
     * @param identity
     * @return
     * @throws MspException
     */
    private X509Certificate[] getCertChain(IIdentity identity) throws MspException {
        log.debug(String.format("MSP %s getting certification chain", name));
        if (identity instanceof Identity) {
            Identity id = (Identity) identity;
            return getCertChainForCspIdentity(id);
        } else {
            throw new MspException("Identity type not recognized");
        }
    }

    /**
     * 获取证书链的Identifier
     *
     * @param chains
     * @return
     * @throws MspException
     */
    public byte[] getCertChainIdentifierFromChain(X509Certificate[] chains) throws MspException {
        byte[] identifier = null;
        byte[] chainsBytes = null;
        for (int i = 0; i < chains.length; i++) {
            try {
                chainsBytes = ArrayUtils.addAll(chainsBytes, chains[i].getEncoded());
            } catch (CertificateEncodingException e) {
                throw new MspException(e.getMessage());
            }
        }
        try {
            identifier = this.csp.hash(chainsBytes, null);
        } catch (JulongChainException e) {
            throw new MspException(e.getMessage());
        }
        return identifier;

    }

    /**
     * 获取证书链
     *
     * @param identity
     * @return
     * @throws MspException
     */
    public X509Certificate[] getCertChainForCspIdentity(Identity identity) throws MspException {
        if (identity == null) {
            throw new MspException("Invalid csp identity,Must be different from null");
        }
//        if (verifyOptions==null) {
//            throw new MspException("Invalid msp instance");
//        }
        if (MspValidate.isCA(identity.getCertificate())) {
            throw new MspException("An X509 certificate with Basic Constraint: " +
                    "Certificate Authority equals true cannot be used as an identity");
        }
        return getValidationChain(identity.getCertificate(), false);
    }

    /**
     * 获取有效的证书链
     * @param certificate
     * @param isIntermediateChain
     * @return
     * @throws MspException
     */
    private X509Certificate[] getValidationChain(Certificate certificate, boolean isIntermediateChain) throws MspException {

        if (isIntermediateChain) {
            //如果有中间证书,则将中间证书装载入证书链
        }
        return getUniqueValidationChain(certificate, isIntermediateChain);
    }

    /**
     * 获取唯一有效的验证证书链
     * @param certificate
     * @param isIntermediateChain
     * @return
     * @throws MspException
     */
    public X509Certificate[] getUniqueValidationChain(Certificate certificate, boolean isIntermediateChain) throws MspException {
        X509Certificate[] chains = new X509Certificate[rootCerts.length];
        for (int i = 0; i < rootCerts.length; i++) {
            IIdentity identity = rootCerts[i];
            if (identity instanceof Identity) {
                try {
                    X509Certificate x509Certificate = CryptoUtil.getX509Certificate(((Identity) identity).getCertificate().getEncoded());
                    chains[i] = x509Certificate;
                } catch (Exception e) {
                    throw new MspException(e.getMessage());
                }
            }
        }
        return chains;
    }

    public void setupCRLs(MspConfigPackage.JuLongMSPConfig mspConfig) {
        //TODO crypto tools 中暂时还没有CRL列表
        if(mspConfig.getRevocationListList().size()==0){
            return;
        }
    }

    public void finalizeSetupCAs(MspConfigPackage.JuLongMSPConfig mspConfig) {

    }

    public void setupSigningIdentity(MspConfigPackage.JuLongMSPConfig mspConfig) throws MspException {
        //通过配置获取签名者,并且赋值
        this.signer = getSigningIdentityFromConf(mspConfig.getSigningIdentity());
    }

    /**
     * 装载TLSCA证书
     * @param julongMspConfig
     */
    public void setupTLSCAs(MspConfigPackage.JuLongMSPConfig julongMspConfig) {

    }

    /**
     * 装载组织单元
     * @param julongMspConfig
     * @throws MspException
     */
    public void setupOUs(MspConfigPackage.JuLongMSPConfig julongMspConfig) throws MspException {

        for (MspConfigPackage.JuLongOUIdentifier ouIdentifier : julongMspConfig.getOrganizationalUnitIdentifiersList()) {
            byte[] certifiersIdentifier;
            try {
                certifiersIdentifier = getCertifiersIdentifier(ouIdentifier.getCertificate().toByteArray());
            } catch (MspException e) {
                throw new MspException(e.getMessage());
            }
            //检查是否重复
            boolean found = false;
            if (ouIdentifiers.size() == 0 || ouIdentifiers == null) {
                byte[][] certifiersIdentifiers = new byte[0][0];
                certifiersIdentifiers = (byte[][]) ArrayUtils.add(certifiersIdentifiers, certifiersIdentifier);
                ouIdentifiers.put(ouIdentifier.getOrganizationalUnitIdentifier(), certifiersIdentifiers);
            }
            for (byte[] id : ouIdentifiers.get(ouIdentifier.getOrganizationalUnitIdentifier())) {
                if (id.equals(certifiersIdentifier)) {
                    log.debug(String.format("Duplicate found in ou identifiers [%s, %s]", ouIdentifier.getOrganizationalUnitIdentifier(), id));
                    found = true;
                    break;
                }
            }
            if (!found) {
                byte[][] ous = ouIdentifiers.get(ouIdentifier.getOrganizationalUnitIdentifier());
                ouIdentifiers.put(ouIdentifier.getOrganizationalUnitIdentifier(), (byte[][]) ArrayUtils.add(ous, certifiersIdentifier));
            }
        }
    }

    /**
     * 获取证书Identifier
     * @param certRaw
     * @return
     * @throws MspException
     */
    private byte[] getCertifiersIdentifier(byte[] certRaw) throws MspException {
        X509Certificate x509Certificate = null;
        try {
            x509Certificate = CryptoUtil.getX509Certificate(certRaw);
        } catch (Exception e) {
            throw new MspException(e.getMessage());
        }
        X509Certificate[] chains = {x509Certificate};
        return getCertChainIdentifierFromChain(chains);
    }

    /**
     * 根据配置获取签名身份
     * @param signingIdentityInfo
     * @return
     * @throws MspException
     */
    public SignIdentity getSigningIdentityFromConf(MspConfigPackage.SigningIdentityInfo signingIdentityInfo) throws MspException {
        //通过配置获取sidinfo的publicSigner获取公钥和一个身份实例
        SignIdentity signIdentity = null;
        try {
            HashMap<String, Object> map = getIdentityFromConf(signingIdentityInfo.getPublicSigner().toByteArray());
            //TODO 源码中通过公钥的ski值，获取到私钥
            Identity id = (Identity) map.get(MspConstant.IDENTITY);
            //TODO 根据配置的密钥path,获取私钥
            String skPath = CspOptsManager.getInstance().getDefaultFactoryOpts().getKeyStore();
            byte[] skBytes = new LoadLocalMspFiles().getSkFromDir(skPath).get(0);
            IKey privateKey = null;
            try {
                privateKey = csp.keyImport(skBytes, new SM2PrivateKeyImportOpts(true));
            } catch (JulongChainException e) {
                log.error(e.getMessage());
                throw new MspException(e.getMessage());
            }
            signIdentity = new SignIdentity(new Identity(id.getCertificate(), id.getPk(), this), new NodeSigner(this.csp, privateKey), this);
        } catch (MspException e) {
            throw new MspException(e.getMessage());
        }
        return signIdentity;
    }

    /**
     * 根据配置获取身份的map
     * @param idBytes
     * @return
     * @throws MspException
     */
    public HashMap<String, Object> getIdentityFromConf(byte[] idBytes) throws MspException {
        if (idBytes == null) {
            throw new MspException("GetIdentityFromBytes error idBytes is null");
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        IKey certPubK = null;
        Certificate certificate = null;
        try {
            certificate = getCertFromPem(idBytes);
            //获取证书中的公钥
            byte[] pubKeyInfo = certificate.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
            certPubK = csp.keyImport(pubKeyInfo, new SM2PublicKeyImportOpts(true));
        } catch (JulongChainException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        map.put(MspConstant.PUBLIC_KEY, certPubK);
        Identity mspIdentity = new Identity(certificate, certPubK, this);
        map.put(MspConstant.IDENTITY, mspIdentity);
        return map;
    }

    /**
     * 解析x509证书
     *
     * @param idBytes
     * @return
     * @throws IOException
     * @throws MspException
     */
    public Certificate getCertFromPem(byte[] idBytes) throws IOException, MspException {
        Certificate certificate = null;
        if (idBytes == null) {
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

    public Map<String, byte[][]> getOuIdentifiers() {
        return ouIdentifiers;
    }

    public MspConfigPackage.JuLongCryptoConfig getJuLongCryptoConfig() {
        return juLongCryptoConfig;
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

    public void setOuIdentifiers(Map<String, byte[][]> ouIdentifiers) {
        this.ouIdentifiers = ouIdentifiers;
    }

    public void setJuLongCryptoConfig(MspConfigPackage.JuLongCryptoConfig juLongCryptoConfig) {
        this.juLongCryptoConfig = juLongCryptoConfig;
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
