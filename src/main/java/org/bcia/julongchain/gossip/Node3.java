/**
 * Copyright Dingxuan. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.bcia.julongchain.gossip;

import com.codahale.metrics.MetricRegistry;
import org.apache.gossip.GossipMember;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.apache.gossip.model.SharedGossipDataMessage;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Gossip测试节点，第三个节点
 *
 * @author wanliangbing
 * @date 2018-02-28
 * @company Dingxuan
 */
public class Node3 {
	private static JavaChainLog log = JavaChainLogFactory.getLog(Node3.class);

	public static void main(String[] args) throws Exception {
		// 非种子节点，需要把种子节点加入gossipMember
		List<GossipMember> gossipMembers = new ArrayList<>();
		GossipMember gossipMember = new RemoteGossipMember("bcia-gossip-cluster", URI.create("udp://localhost:3000"),
				"1");
		gossipMembers.add(gossipMember);
		// 各种配置默认，为空
		GossipSettings settings = new GossipSettings();
		GossipListener listener = new GossipListener() {
			@Override
			public void gossipEvent(GossipMember member, GossipState state) {
			}
		};
		MetricRegistry registry = new MetricRegistry();
		// 创建gossip service
		GossipService gossipService = new GossipService("bcia-gossip-cluster", URI.create("udp://localhost:3002"), "3",
				new HashMap<>(0), gossipMembers, settings, listener, registry);
		// 启动gossip service
		gossipService.start();

		while (true) {
			// 休眠2秒
			Thread.sleep(2000);
			// 设置一个节点的同步数据
			SharedGossipDataMessage m = new SharedGossipDataMessage();
			m.setExpireAt(Long.MAX_VALUE);
			m.setKey("abc");
			m.setPayload(new OrSet<String>("from node3:" + UUID.randomUUID().toString()));
			m.setTimestamp(System.currentTimeMillis());
			gossipService.getGossipManager().merge(m);
			// 打印当前节点信息
			log.info("Live:" + gossipService.getGossipManager().getLiveMembers());
			log.info("Dead:" + gossipService.getGossipManager().getDeadMembers());
		}

	}

}
