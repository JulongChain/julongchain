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
package org.bcia.julongchain.node.util;

import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.gossip.GossipMember;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.crdt.Crdt;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.apache.gossip.model.SharedGossipDataMessage;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/11
 * @company Dingxuan
 */
public class NodeGossipManager {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeGossipManager.class);

    private Map<String, GossipService> gossipServiceMap = new ConcurrentHashMap<>();

    public void addGossipService(String groupId, String nodeId, String nodeAddress, String consenterId,
                                 String consenterAddress) throws GossipException {
        List<GossipMember> gossipMembers = new ArrayList<>();
        if (StringUtils.isNotBlank(consenterId) && StringUtils.isNotBlank(consenterAddress)) {
            RemoteGossipMember remoteGossipMember = new RemoteGossipMember(groupId,
                    URI.create("udp://" + consenterAddress), consenterId);
            gossipMembers.add(remoteGossipMember);
        }

        try {
            GossipService gossipService = new GossipService(groupId, URI.create("udp://" + nodeAddress), nodeId,
                    new HashMap<>(), gossipMembers, new GossipSettings(), new GossipListener() {
                @Override
                public void gossipEvent(GossipMember member, GossipState state) {
                }
            }, new MetricRegistry());

            gossipServiceMap.put(groupId, gossipService);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new GossipException(e);
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
            throw new GossipException(e);
        }
    }

    public void sendMessage(String groupId, long blockNumber, Common.Block block) throws ValidateException {
        ValidateUtils.isNotBlank(groupId, "groupId can not be empty");
        ValidateUtils.isNotNull(block, "block can not be null");

        GossipService gossipService = gossipServiceMap.get(groupId);
        ValidateUtils.isNotNull(gossipService, "gossipService can not be null");

        SharedGossipDataMessage m = new SharedGossipDataMessage();
        m.setExpireAt(Long.MAX_VALUE);
        m.setKey(groupId + "-" + blockNumber);
        m.setPayload(block);
        m.setTimestamp(System.currentTimeMillis());
        gossipService.getGossipManager().merge(m);
    }

    public Common.Block acquireMessage(String groupId, long blockNumber) throws ValidateException {
        ValidateUtils.isNotBlank(groupId, "groupId can not be empty");

        GossipService gossipService = gossipServiceMap.get(groupId);
        ValidateUtils.isNotNull(gossipService, "gossipService can not be null");

        Crdt crdt = gossipService.getGossipManager().findCrdt(groupId + "-" + blockNumber);
        if (crdt == null || crdt.value() == null) {
            return null;
        }

        return (Common.Block) crdt.value();
    }
}