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
import org.apache.commons.lang3.ArrayUtils;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.exception.VerifyException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2SignerOpts;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.entity.IdentityIdentifier;
import org.bcia.julongchain.msp.entity.OUIdentifier;
import org.bcia.julongchain.msp.util.MspConstant;
import org.bcia.julongchain.msp.util.MspUtil;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.msp.Identities;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static org.bcia.julongchain.common.util.Convert.bytesToHexString;

/**
 * 身份实体
 *
 * @author zhangmingyang
 * @Date: 2018/4/17
 * @company Dingxuan
 */
public class Identity implements IIdentity {
    private static JulongChainLog log = JulongChainLogFactory.getLog(Identity.class);
    private IdentityIdentifier identityIdentifier;
    private Certificate certificate;
    private IKey pk;
    private Msp msp;


    public Identity() {
    }

    public Identity(Certificate certificate, IKey pk, Msp msp) {

        Certificate cert = msp.sanitizeCert(certificate);
        byte[] digest = new byte[0];
        try {
            digest = msp.getCsp().hash(cert.getEncoded(), null);
        } catch (JulongChainException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        IdentityIdentifier id = new IdentityIdentifier(msp.getName(), bytesToHexString(digest));
        this.certificate = cert;
        this.msp = msp;
        this.identityIdentifier = id;
        this.pk = pk;
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
        return this.identityIdentifier.getMspId();
    }

    @Override
    public void validate() throws MspException {
        msp.validate(this);
    }

    @Override
    public OUIdentifier[] getOrganizationalUnits() throws MspException {
        if (certificate == null) {
            throw new MspException("certificate is null");
        }
        byte[] cid = this.msp.getCertChainIdentifier(this);
        OUIdentifier[] res = null;
        Map<String, String> subject = MspUtil.parseFromString(certificate.getSubject().toString());
        OUIdentifier ouIdentifier = new OUIdentifier();
        ouIdentifier.setOrganizationalUnitIdentifier(subject.get(MspConstant.ORGANIZATION_UNIT));
        ouIdentifier.setCertifiersIdentifier(cid);

        res = (OUIdentifier[]) ArrayUtils.add(res, ouIdentifier);
        return res;
    }

    @Override
    public void verify(byte[] msg, byte[] sig) throws VerifyException {
        boolean verify = false;
        try {
            verify = msp.getCsp().verify(pk, sig, msg, new SM2SignerOpts());
            if (verify == false) {
                throw new VerifyException("Veify the sign is fail");
            }
        } catch (JulongChainException e) {
            throw new VerifyException(e.getMessage());
        }
    }

    @Override
    public byte[] serialize() {
        byte[] serializedIdentityBytes = null;
        Identities.SerializedIdentity.Builder serializedIdentity = Identities.SerializedIdentity.newBuilder();
        serializedIdentity.setMspid(this.identityIdentifier.getMspId());
        try {
            serializedIdentity.setIdBytes(ByteString.copyFrom(certificate.getEncoded()));
            serializedIdentityBytes = serializedIdentity.build().toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serializedIdentityBytes;
    }

    @Override
    public void satisfiesPrincipal(MspPrincipal.MSPPrincipal principal) throws MspException {
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
