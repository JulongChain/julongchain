/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.gossip;

import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.gossip.GossipMember;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.crdt.Crdt;
import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.apache.gossip.model.SharedGossipDataMessage;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.node.NodeConfigFactory;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Gossip服务的相关方法，<br>
 * 包括启动gossip服务，<br>
 * 向gossip网络广播数据，<br>
 * 从gossip网络读取数据
 *
 * <p>参考：https://github.com/apache/incubator-gossip
 *
 * @author wanliangbing
 * @date 2018/06/08
 * @company Dingxuan
 */
public class GossipServiceUtil {

  private static JavaChainLog log = JavaChainLogFactory.getLog(GossipServiceUtil.class);

  /**
   * 启动gossip server服务，（seed节点）
   *
   * @param address 节点的地址，格式为ip:port
   * @throws GossipException
   */
  public static GossipService newGossipService(String address) throws GossipException {
    log.info("newGossipService(address[" + address + "])");

    GossipService gossipService = null;
    try {
      gossipService =
          new GossipService(
              "julongchain",
              URI.create("udp://" + address),
              UUID.randomUUID().toString(),
              new HashMap<>(),
              new ArrayList<>(),
              new GossipSettings(),
              new GossipListener() {
                @Override
                public void gossipEvent(GossipMember member, GossipState state) {}
              },
              new MetricRegistry());
    } catch (InterruptedException e) {
      throw new GossipException(e.getMessage(), e);
    } catch (UnknownHostException e) {
      throw new GossipException(e.getMessage(), e);
    }

    return gossipService;
  }

  /**
   * 启动节点，并加入种子节点地址
   *
   * @param address 本机地址
   * @param seedAddress 种子节点地址
   * @return
   * @throws GossipException
   */
  public static GossipService newGossipService(String address, String seedAddress)
      throws GossipException {

    log.info("newGossipService(address[" + address + "],seedAddress[" + seedAddress + "])");

    if (StringUtils.isEmpty(seedAddress)) {
      return newGossipService(address);
    }

    List<GossipMember> gossipMembers = new ArrayList<>();
    RemoteGossipMember remoteGossipMember =
        new RemoteGossipMember(
            "julongchain", URI.create("udp://" + seedAddress), UUID.randomUUID().toString());
    gossipMembers.add(remoteGossipMember);

    GossipService gossipService = null;
    try {
      gossipService =
          new GossipService(
              "julongchain",
              URI.create("udp://" + address),
              UUID.randomUUID().toString(),
              new HashMap<>(),
              gossipMembers,
              new GossipSettings(),
              new GossipListener() {
                @Override
                public void gossipEvent(GossipMember member, GossipState state) {}
              },
              new MetricRegistry());
    } catch (InterruptedException e) {
      throw new GossipException(e.getMessage(), e);
    } catch (UnknownHostException e) {
      throw new GossipException(e.getMessage(), e);
    }

    return gossipService;
  }

  /**
   * 节点上传区块
   *
   * @param gossipService 上传数据的gossip service
   * @param group 上传数据的群组
   * @param seqNum 区块的num
   * @param data String格式的区块
   * @throws GossipException
   */
  public static void addData(GossipService gossipService, String group, Long seqNum, String data)
      throws GossipException {

    if (gossipService == null) {
      throw new GossipException("Gossip服务未启动。");
    }

    log.info(
        "addData(gossipService["
            + gossipService.toString()
            + "],group["
            + group
            + "],seqNum["
            + seqNum
            + "],data["
            + data
            + "])");

    if (StringUtils.isEmpty(group) || seqNum == null || data == null) {
      throw new GossipException("群组，区块高度，区块文件不能为空。");
    }

    SharedGossipDataMessage m = new SharedGossipDataMessage();
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey(group + "-" + seqNum);
    m.setPayload(new OrSet<String>(data));
    m.setTimestamp(System.currentTimeMillis());
    gossipService.getGossipManager().merge(m);
  }

  /**
   * 读取指定区块num的数据
   *
   * @param gossipService 读取数据的gossip service
   * @param group 读取数据的群组
   * @param seqNum 区块的num
   * @return
   * @throws GossipException
   */
  public static Object getData(GossipService gossipService, String group, Long seqNum)
      throws GossipException {
    if (gossipService == null) {
      throw new GossipException("Gossip服务未启动。");
    }
    if (StringUtils.isEmpty(group) || seqNum == null) {
      throw new GossipException("区块，区块高度不能为空。");
    }

    log.info(
        "getData(gossipService["
            + gossipService.toString()
            + "],group["
            + group
            + "],seqNum["
            + seqNum
            + "])");

    Crdt crdt = gossipService.getGossipManager().findCrdt(group + "-" + seqNum);
    if (crdt == null) {
      return null;
    }
    if (crdt.value() == null) {
      return null;
    }
    Set sets = (Set) crdt.value();
    Iterator iterator = sets.iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

  /**
   * 从配置文件读取consenter地址，并启动gossip服务
   *
   * @return
   * @throws GossipException
   */
  public static GossipService startConsenterGossip() throws GossipException {
    String consenterAddress =
        NodeConfigFactory.getNodeConfig().getNode().getGossip().getConsenterAddress();
    log.info("consenter gossip address:" + consenterAddress);
    GossipService gossipService = newGossipService(consenterAddress);
    gossipService.start();
    log.info("启动consenter gossip: address[" + consenterAddress + "]");
    return gossipService;
  }

  /**
   * 从配置文件读取committer地址，并启动gossip服务
   *
   * @return
   * @throws GossipException
   */
  public static GossipService startCommitterGossip() throws GossipException {
    String consenterAddress =
        NodeConfigFactory.getNodeConfig().getNode().getGossip().getConsenterAddress();
    log.info("consenter gossip address:" + consenterAddress);
    String committerAddress =
        NodeConfigFactory.getNodeConfig().getNode().getGossip().getCommiterAddress();
    log.info("committer gossip address:" + committerAddress);
    GossipService gossipService = newGossipService(committerAddress, consenterAddress);
    gossipService.start();
    log.info(
        "启动committer gossip: address[" + committerAddress + "] seedAddress:" + consenterAddress);
    return gossipService;
  }
}
