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

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.csp.gm.dxct.util.CryptoUtil;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.entity.OUIdentifier;
import org.bcia.julongchain.msp.entity.VerifyOptions;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;

/**
 * 验证msp相关身份
 * @author zhangmingyang
 * @date 2018/07/04
 * @company Dingxuan
 */
public class MspValidate {
    /**
     * 验证身份
     * @param msp
     * @param identity
     * @throws MspException
     */
    public static void validateIdentity(Msp msp, Identity identity) throws MspException {
        X509Certificate[] validationChain = msp.getCertChainForCspIdentity(identity);
        validateIdentityAgainstChain(identity, validationChain);
    }

    /**
     * 验证CA身份
     * @param msp
     * @param identity
     * @throws MspException
     */
    public static void validateCAIdentity(Msp msp, Identity identity) throws MspException {
        if (!isCA(identity.getCertificate())) {
            throw new MspException("Only CA identities can be validated");
        }
        X509Certificate[] validationChain = msp.getUniqueValidationChain(identity.getCertificate(), false);
        if (validationChain.length == 1) {
            return;
        }
        validateIdentityAgainstChain(identity, validationChain);
    }

    /**
     * 验证TLSCA身份
     * @param msp
     * @param certificate
     * @param options
     * @throws MspException
     */
    public static void validateTlsCaIdentity(Msp msp, Certificate certificate, VerifyOptions options) throws MspException {
        if (!isCA(certificate)) {
            throw new MspException("Only CA identities can be validated");
        }
        X509Certificate[] validationChain = msp.getUniqueValidationChain(certificate, false);
        if (validationChain.length == 1) {
            //validationChain[0] is the root CA certificate
            return;
        }
        validateCertAgainstChain(certificate, validationChain);
    }


    public static void validateIdentityAgainstChain(Identity identity, X509Certificate[] validationChain) throws MspException {
        validateCertAgainstChain(identity.getCertificate(), validationChain);
    }

    /**
     * 验证证书连
     * @param certificate
     * @param chains
     * @throws MspException
     */
    public static void validateCertAgainstChain(Certificate certificate, X509Certificate[] chains) throws MspException {
        Security.addProvider(new BouncyCastleProvider());
        X509Certificate x509Certificate = null;
        try {
            x509Certificate = CryptoUtil.getX509Certificate(certificate.getEncoded());
        } catch (Exception e) {
            throw new MspException(e.getMessage());
        }

        //验证时间
        Date timeNow = new Date();
        try {
            x509Certificate.checkValidity(timeNow);
        } catch (CertificateExpiredException e) {
            throw new MspException(e.getMessage());
        } catch (CertificateNotYetValidException e) {
            throw new MspException(e.getMessage());
        }

        //验证证书链

        boolean result=CryptoUtil.verify(x509Certificate,chains);
        if(result==false){
            throw new MspException("This cert chains is not valild");
        }
    }

    /**
     * 验证身份组织
     * @param msp
     * @param identity
     * @throws MspException
     */
    public static void validateIdentityOus(Msp msp, Identity identity) throws MspException {

        if (msp.getOuIdentifiers().size() > 0) {
            boolean found = false;
            for (OUIdentifier ou : identity.getOrganizationalUnits()) {
                byte[][] certificationIDs = msp.getOuIdentifiers().get(ou.getOrganizationalUnitIdentifier());
                if (certificationIDs != null) {
                    for (byte[] certificationID : certificationIDs) {
                        if (Arrays.equals(certificationID, ou.getCertifiersIdentifier())) {
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (!found) {
                if (identity.getOrganizationalUnits().length == 0) {
                    throw new MspException("the identity certificate does not contain an Organizational Unit (OU)");
                }
                throw new MspException(String.format("none of the identity's organizational units [%s] are in MSP %s", identity.getMSPIdentifier(), msp.getName()));
            }
        }

        if (!msp.isOuEnforcement()) {
            return;
        }

        int counter = 0;
        for (OUIdentifier ou : identity.getOrganizationalUnits()) {
            OUIdentifier nodeOUIdentifier = null;

            if (ou.getOrganizationalUnitIdentifier().equals(msp.getClientOU().getOrganizationalUnitIdentifier())) {
                nodeOUIdentifier = msp.getClientOU();
            } else if (ou.getOrganizationalUnitIdentifier().equals(msp.getNodeOU().getOrganizationalUnitIdentifier())) {
                nodeOUIdentifier = msp.getNodeOU();
            }

            if ((nodeOUIdentifier.getCertifiersIdentifier()!= null) && !(Arrays.equals(nodeOUIdentifier.getCertifiersIdentifier(),ou.getCertifiersIdentifier()))) {
               throw new MspException(String.format("certifiersIdentifier does not match: [%s], MSP: [%s]", identity.getOrganizationalUnits(), msp.getName()));
            }
            counter++;
            if (counter > 1) {
                break;
            }
        }
        if (counter != 1) {
            throw new MspException(String.format("the identity must be a client, a peer or an orderer identity to be valid, " +
                    "not a combination of them. OUs: [%s], MSP: [%s]", identity.getOrganizationalUnits(), msp.getName()));
        }
    }
    /**
     * 判断是否为根CA证书
     * @param certificate
     * @return
     */
    public static boolean isCA(Certificate certificate) {
        if (certificate.getIssuer().equals(certificate.getSubject())) {
            return true;
        } else {
            return false;
        }
    }
}
