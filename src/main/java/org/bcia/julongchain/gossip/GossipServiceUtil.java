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

import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/06/08
 * @company Dingxuan
 */
public class GossipServiceUtil {

  public static final Map<String, GossipService> addressAndGossipServiceMap = new HashMap<>();
  public static final Map<String, String> addressAndGroupMap = new HashMap<>();
  public static final Map<String, GossipService> idAndGossipServiceMap = new HashMap<>();

  /**
   * 启动gossip server服务
   *
   * @param id 指定节点gossip服务ID
   * @param group 节点gossip服务所属的group
   * @param address 节点的地址，格式为ip:port
   * @throws UnknownHostException
   * @throws InterruptedException
   */
  public static GossipService newGossipService(String id, String group, String address)
      throws GossipException {

    GossipService gossipService = null;
    try {
      gossipService =
          new GossipService(
              group,
              URI.create("udp://" + address),
              id,
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

    addressAndGossipServiceMap.put(address, gossipService);
    addressAndGroupMap.put(address, group);
    idAndGossipServiceMap.put(id, gossipService);

    return gossipService;
  }

  public static GossipService newGossipService(
      String id, String group, String address, String id_leader, String address_leader)
      throws GossipException {

    if (StringUtils.isEmpty(id_leader) || StringUtils.isEmpty(address_leader)) {
      return newGossipService(id, group, address);
    }

    List<GossipMember> gossipMembers = new ArrayList<>();
    RemoteGossipMember remoteGossipMember =
        new RemoteGossipMember(group, URI.create("udp://" + address_leader), id_leader);
    gossipMembers.add(remoteGossipMember);

    GossipService gossipService = null;
    try {
      gossipService =
          new GossipService(
              group,
              URI.create("udp://" + address),
              id,
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

    addressAndGossipServiceMap.put(address, gossipService);
    addressAndGroupMap.put(address, group);
    idAndGossipServiceMap.put(id, gossipService);

    return gossipService;
  }

  /**
   * 节点上传区块
   *
   * @param address 上传数据的源始ip和端口，格式为ip:port
   * @param seqNum 区块的num
   * @param data String格式的区块
   */
  public static void addData(String address, Long seqNum, String data) throws GossipException {

    if (StringUtils.isEmpty(address) || seqNum == null || data == null) {
      throw new GossipException("Gossip服务器地址，区块高度，区块文件不能为空。");
    }

    GossipService gossipService = addressAndGossipServiceMap.get(address);
    if (gossipService == null) {
      throw new GossipException("Gossip服务异常，请检查是否已经启动。");
    }
    String group = addressAndGroupMap.get(address);
    if (StringUtils.isEmpty(group)) {
      throw new GossipException("Gossip服务所属group不能为空。");
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
   * @param address 读取节点的地址，格式为ip:port
   * @param seqNum 区块的num
   * @return
   */
  public static Object getData(String address, Long seqNum) throws GossipException {
    if (StringUtils.isEmpty(address) || seqNum == null) {
      throw new GossipException("Gossip服务器地址，区块高度不能为空。");
    }
    GossipService gossipService = addressAndGossipServiceMap.get(address);
    if (gossipService == null) {
      throw new GossipException("Gossip服务异常，请检查是否启动。");
    }
    String group = addressAndGroupMap.get(address);
    if (StringUtils.isEmpty(group)) {
      throw new GossipException("Gossip服务所属group不能为空。");
    }
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

  // public static void startGossipService(String address, List<String> groups, String id_seed,
  // String address_seed) {
  //   for (String group : groups) {
  //     newGossipService(UUID.randomUUID().toString(), group, address, , ).start();
  //   }
  // }

}
