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
package org.bcia.julongchain.gossip.api;

import org.bcia.julongchain.common.exception.GossipException;

import java.sql.Timestamp;

public interface IMessageCryptoService {

    public byte[] getPKIidOFCert(byte[] peerIdentity);

    public void verifyBlock(byte[] chainID, Long seqNum, byte[] signedBlock) throws GossipException;

    public byte[] sign(byte[] msg) throws GossipException;

    public void verify(byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException;

    public void verifyByChannel(byte[] chainID, byte[] peerIdentity, byte[] signature, byte[] message) throws GossipException;

    public void validateIdentity(byte[] peerIdentity) throws GossipException;

    public Timestamp expiration(byte[] peerIdentity) throws GossipException;

}
