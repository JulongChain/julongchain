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
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gossip测试节点，第一个节点
 *
 * @author wanliangbing
 * @date 2018-02-28
 * @company Dingxuan
 */
public class Node1 {
	
	private static JavaChainLog log = JavaChainLogFactory.getLog(Node1.class);

	public static void main(String[] args) throws Exception {
		// 种子节点
		// 启动时不需要加入其它节点
		List<GossipMember> gossipMembers = new ArrayList<GossipMember>();
		// 各种配置默认，为空
		GossipSettings settings = new GossipSettings();
		GossipListener listener = new GossipListener() {
			@Override
			public void gossipEvent(GossipMember member, GossipState state) {
			}
		};
		MetricRegistry registry = new MetricRegistry();
		// 创建gossip service
		HashMap<String, String> properties = new HashMap<>(0);
		GossipService gossipService = new GossipService("bcia-gossip-cluster", URI.create("udp://localhost:3000"), "1",
				properties, gossipMembers, settings, listener, registry);
		// 启动gossip service
		gossipService.start();

		while (true) {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			// 打印live的节点
			log.info("Live:" + gossipService.getGossipManager().getLiveMembers());
			// 打印dead的节点
			log.info("Dead:" + gossipService.getGossipManager().getDeadMembers());
			// 打印节点间共享的内容
			log.info("---------- " + (gossipService.getGossipManager().findCrdt("abc") == null ? ""
					: gossipService.getGossipManager().findCrdt("abc").value()));
			// 打印节点间共享的内容
			log.info("********** " + gossipService.getGossipManager().findCrdt("abc"));
		}

	}

}
