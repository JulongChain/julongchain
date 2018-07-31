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
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.exception.VerifyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2SignerOpts;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.entity.IdentityIdentifier;
import org.bcia.julongchain.msp.entity.OUIdentifier;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.msp.Identities;
import org.bouncycastle.asn1.x509.Certificate;

import java.io.IOException;
import java.util.Date;

import static org.bcia.julongchain.common.util.Convert.bytesToHexString;
import static org.bcia.julongchain.csp.factory.CspManager.getDefaultCsp;

/**
 * @author zhangmingyang
 * @Date: 2018/4/17
 * @company Dingxuan
 */
public class Identity implements IIdentity {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Identity.class);
    /**
     * 身份
     */
    private IdentityIdentifier identityIdentifier;
    /**
     * 证书
     */
    private Certificate certificate;
    /**
     * 公钥
     */
    private IKey pk;
    /**
     * msp
     */
    private Msp msp;

    public Identity() {
    }

    public Identity(Certificate certificate, IKey pk, Msp msp) {

        Certificate cert = msp.sanitizeCert(certificate);
        byte[] digest = new byte[0];
        try {
            digest = msp.getCsp().hash(cert.getEncoded(), null);
        } catch (JavaChainException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        IdentityIdentifier id = new IdentityIdentifier(msp.getName(), bytesToHexString(digest));
        this.certificate = cert;
        this.msp = msp;
        this.identityIdentifier = id;
        this.pk = pk;
    }

    public Identity(IdentityIdentifier identityIdentifier, Certificate certificate, IKey pk, Msp msp) {
        this.identityIdentifier = identityIdentifier;
        this.certificate = certificate;
        this.pk = pk;
        this.msp = msp;
    }

    @Override
    public Date expireAt() {
        return certificate.getEndDate().getDate();
    }

    @Override
    public IdentityIdentifier getIdentifier() {
        return identityIdentifier;
    }

    @Override
    public String getMSPIdentifier() {
        return this.identityIdentifier.getMspid();
    }

    @Override
    public void validate() throws MspException {
        msp.validate(this);
    }

    @Override
    public OUIdentifier[] getOrganizationalUnits() throws MspException {
        //TODO 通过msp获取证书链身份
        if (certificate.equals(null)) {
            return null;
        }
        return new OUIdentifier[0];
    }

    @Override
    public void verify(byte[] msg, byte[] sig) throws VerifyException {
        boolean verify = false;
        try {
            //TODO 后续根据具体的csp,构造具体的签名选项
            verify = msp.getCsp().verify(pk, sig, msg, new SM2SignerOpts());
            if (verify == false) {
                throw new VerifyException("Veify the sign is fail");
            }
        } catch (JavaChainException e) {
            throw new VerifyException(e.getMessage());
        }

    }

    @Override
    public byte[] serialize() {

        Identities.SerializedIdentity.Builder serializedIdentity = Identities.SerializedIdentity.newBuilder();
        serializedIdentity.setMspid(this.identityIdentifier.getMspid());
        try {
            serializedIdentity.setIdBytes(ByteString.copyFrom(certificate.getEncoded()));
            return serializedIdentity.build().toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void satisfiesPrincipal(MspPrincipal.MSPPrincipal principal) throws IOException, MspException {
        msp.satisfiesPrincipal(this, principal);
    }

    public IdentityIdentifier getIdentityIdentifier() {
        return identityIdentifier;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public IKey getPk() {
        return pk;
    }

    public Msp getMsp() {
        return msp;
    }
}
