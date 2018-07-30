/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.julongchain.node.gossip;

import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policycheck.policies.IGroupPolicyManagerGetter;
import org.bcia.julongchain.gossip.api.IMessageCryptoService;

import java.sql.Timestamp;

public class MspMessageCryptoService implements IMessageCryptoService{

    private static final JavaChainLog log = JavaChainLogFactory.getLog(MspMessageCryptoService.class);
    private IGroupPolicyManagerGetter channelPolicyManagerGetter;
    private ILocalSigner localSigner;

    @Override
    public byte[] getPKIidOFCert(byte[] peerIdentity) {
        return new byte[0];
    }

    @Override
    public void verifyBlock(byte[] chainID, Long seqNum, byte[] signedBlock) throws GossipException {

    }

    @Override
    public byte[] sign(byte[] msg) throws GossipException {
        return new byte[0];
    }

    @Override
    public void verify(byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException {

    }

    @Override
    public void verifyByChannel(byte[] chainID, byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException {

    }

    @Override
    public void validateIdentity(byte[] peerIdentity) throws GossipException {

    }

    @Override
    public Timestamp expiration(byte[] peerIdentity) throws GossipException {
        return null;
    }
}
