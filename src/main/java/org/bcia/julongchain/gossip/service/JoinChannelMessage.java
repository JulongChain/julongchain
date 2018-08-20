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
package org.bcia.julongchain.gossip.service;

import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.gossip.api.AnchorPeer;
import org.bcia.julongchain.gossip.api.IJoinChannelMessage;

import java.util.Map;
import java.util.Set;

/**
 * class description
 *
 * @author wanliangbing
 * @date 18-7-24
 * @company Dingxuan
 */
public class JoinChannelMessage implements IJoinChannelMessage{

    private static final JulongChainLog log = JulongChainLogFactory.getLog(JoinChannelMessage.class);
    private Long seqNum;
    private Map<String,AnchorPeer[]> members2AnchorPeers;

    @Override
    public Long sequenceNumber() {
        return seqNum;
    }

    /**
     * AnchorPeersOf returns the anchor peers of the given organization
     * @param orgIdentity
     * @return
     */
    @Override
    public AnchorPeer[] anchorPeersOf(byte[] orgIdentity) {
        String orgIdentityStr = new String(orgIdentity);
        AnchorPeer[] anchorPeers = members2AnchorPeers.get(orgIdentityStr);
        return anchorPeers;
    }

    /**
     * Members returns the organizations of the channel
     * @return
     */
    @Override
    public byte[][] members() {
        Set<Map.Entry<String, AnchorPeer[]>> entries = members2AnchorPeers.entrySet();
        int size = members2AnchorPeers.size();
        byte[][] members = new byte[size][];
        int i = 0;
        for (Map.Entry<String, AnchorPeer[]> entry : entries) {
            String orgIdentity = entry.getKey();
            members[i] = orgIdentity.getBytes();
            i++;
        }
        return members;
    }
}
