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
import org.bcia.julongchain.gossip.api.AnchorNode;
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
    private Map<String,AnchorNode[]> members2AnchorNodes;

    @Override
    public Long sequenceNumber() {
        return seqNum;
    }

    /**
     * AnchorNodesOf 返回给定公司的锚节点
     * @param orgIdentity
     * @return
     */
    @Override
    public AnchorNode[] anchorNodesOf(byte[] orgIdentity) {
        String orgIdentityStr = new String(orgIdentity);
        AnchorNode[] anchorNodes = members2AnchorNodes.get(orgIdentityStr);
        return anchorNodes;
    }

    /**
     * Members 返回给定公司的通道
     * @return
     */
    @Override
    public byte[][] members() {
        Set<Map.Entry<String, AnchorNode[]>> entries = members2AnchorNodes.entrySet();
        int size = members2AnchorNodes.size();
        byte[][] members = new byte[size][];
        int i = 0;
        for (Map.Entry<String, AnchorNode[]> entry : entries) {
            String orgIdentity = entry.getKey();
            members[i] = orgIdentity.getBytes();
            i++;
        }
        return members;
    }
}
