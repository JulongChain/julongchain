package org.bcia.javachain.gossip;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.gossip.GossipMember;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import com.codahale.metrics.MetricRegistry;

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
		GossipService gossipService = new GossipService("bcia-gossip-cluster", URI.create("udp://localhost:3000"), "1",
				new HashMap<>(), gossipMembers, settings, listener, registry);
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
