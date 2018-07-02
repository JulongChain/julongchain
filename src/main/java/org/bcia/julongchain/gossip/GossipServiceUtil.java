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

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.Member;
import org.apache.gossip.RemoteMember;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.manager.GossipManagerBuilder;
import org.apache.gossip.model.SharedDataMessage;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfigFactory;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.protos.common.Common;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

  private static final Integer messageLength = 10000;

  private static final String cluster = "julongchain";

  public static GossipManager newGossipService(String address) throws GossipException {

    try {
      GossipSettings settings = new GossipSettings();
      settings.setPersistRingState(false);
      settings.setPersistDataState(false);

      List<Member> startupMembers = new ArrayList<Member>();

      // RemoteMember member =
      //     new RemoteMember(cluster, new URI("udp://" + address), UUID.randomUUID().toString());
      // startupMembers.add(member);

      GossipManager gossipService =
          GossipManagerBuilder.newBuilder()
              .cluster(cluster)
              .uri(new URI("udp://" + address))
              .id(UUID.randomUUID().toString())
              .gossipMembers(startupMembers)
              .gossipSettings(settings)
              .build();

      return gossipService;
    } catch (Exception e) {
      throw new GossipException(e.getMessage(), e);
    }
  }

  public static GossipManager newGossipService(String address, String seedAddress)
      throws GossipException {
    try {
      GossipSettings settings = new GossipSettings();
      settings.setPersistRingState(false);
      settings.setPersistDataState(false);

      List<Member> startupMembers = new ArrayList<Member>();

      RemoteMember seed =
          new RemoteMember(cluster, new URI("udp://" + seedAddress), UUID.randomUUID().toString());
      startupMembers.add(seed);

      // RemoteMember member2 =
      //     new RemoteMember(cluster, new URI("udp://" + address), UUID.randomUUID().toString());
      // startupMembers.add(member2);

      GossipManager gossipService =
          GossipManagerBuilder.newBuilder()
              .cluster(cluster)
              .uri(new URI("udp://" + address))
              .id(UUID.randomUUID().toString())
              .gossipMembers(startupMembers)
              .gossipSettings(settings)
              .build();

      return gossipService;
    } catch (Exception e) {
      throw new GossipException(e.getMessage(), e);
    }
  }

  public static void addData(
      GossipManager gossipService, String group, Long seqNum, Common.Block data)
      throws GossipException {

    if (gossipService == null) {
      throw new GossipException("Gossip not start。");
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
      throw new GossipException("group,blockNum,blockData is null。");
    }

    String blockStr = "";
    try {
      blockStr = new String(data.toByteArray(), "ISO8859-1");
      log.info("=================================================");
      //       log.info("blockStr:" + blockStr);
      log.info("=================================================");
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      throw new GossipException(e);
    }

    int fileNumber = blockStr.length() / messageLength;
    if (blockStr.length() % messageLength > 0) {
      fileNumber = fileNumber + 1;
    }
    log.info("fileNumber:" + fileNumber);

    SharedDataMessage m = new SharedDataMessage();
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey(group + "-" + seqNum);
    m.setPayload(fileNumber);
    m.setTimestamp(System.currentTimeMillis());
    gossipService.gossipSharedData(m);
    log.info("send gossip:[" + group + "-" + seqNum + "]");

    for (int i = 0; i < fileNumber; i++) {

      int start = i * messageLength;
      int end = (i + 1) * messageLength;
      if (end > blockStr.length()) {
        end = blockStr.length();
      }

      String str = blockStr.substring(start, end);

      SharedDataMessage msg = new SharedDataMessage();
      msg.setExpireAt(Long.MAX_VALUE);
      msg.setKey(group + "-" + seqNum + "-" + i);
      msg.setPayload(str);
      msg.setTimestamp(System.currentTimeMillis());
      gossipService.gossipSharedData(msg);

      log.info("send gossip detail:[" + group + "-" + seqNum + "-" + i + "]");
    }
  }

  public static Common.Block getData(GossipManager gossipService, String group, Long seqNum)
      throws GossipException {

    if (gossipService == null) {
      throw new GossipException("Gossip not start");
    }
    if (StringUtils.isEmpty(group) || seqNum == null) {
      throw new GossipException("group, blockNum is null");
    }

    log.info(
        "getData(gossipService["
            + gossipService.toString()
            + "],group["
            + group
            + "],seqNum["
            + seqNum
            + "])");

    SharedDataMessage sharedData = gossipService.findSharedGossipData(group + "-" + seqNum);

    log.info("gossip get data, check [" + group + "-" + seqNum + "]");
    if (sharedData == null) {
      log.info("[" + group + "-" + seqNum + "] is null");
      return null;
    }

    Object fileNumberPayload = sharedData.getPayload();

    Integer fileNumber = (Integer) fileNumberPayload;
    log.info("=====================fileNumber:" + fileNumber);

    if (fileNumber < 1) {
      return null;
    }

    StringBuffer sb = new StringBuffer("");

    for (int i = 0; i < fileNumber; i++) {
      SharedDataMessage m = gossipService.findSharedGossipData(group + "-" + seqNum + "-" + i);
      log.info("gossip get data detail, check [" + group + "-" + seqNum + "-" + i + "]");
      if (m == null) {
        log.info("=====================[" + group + "-" + seqNum + "-" + i + "] is null");
        return null;
      }
      Object p = m.getPayload();
      if (p == null) {
        return null;
      }
      String str = (String) p;
      sb.append(str);
    }

    byte[] bytes = new byte[0];
    try {
      bytes = sb.toString().getBytes("ISO8859-1");
    } catch (UnsupportedEncodingException e) {
      throw new GossipException(e);
    }

    Common.Block block = null;
    try {
      block = Common.Block.parseFrom(bytes);
    } catch (InvalidProtocolBufferException e) {
      throw new GossipException(e);
    }

    return block;
  }

  public static GossipManager startConsenterGossip() throws GossipException {
    String consenterAddress =
        ConsenterConfigFactory.loadConsenterConfig().getGeneral().getGossipAddress();
    log.info("consenter gossip address:" + consenterAddress);
    GossipManager gossipService = newGossipService(consenterAddress);
    gossipService.init();
    log.info("started consenter gossip: address[" + consenterAddress + "]");
    return gossipService;
  }

  public static GossipManager startCommitterGossip() throws GossipException {
    String consenterAddress =
        NodeConfigFactory.getNodeConfig().getNode().getGossip().getConsenterAddress();
    log.info("consenter gossip address:" + consenterAddress);
    String committerAddress =
        NodeConfigFactory.getNodeConfig().getNode().getGossip().getCommiterAddress();
    log.info("committer gossip address:" + committerAddress);
    GossipManager gossipService = newGossipService(committerAddress, consenterAddress);
    gossipService.init();
    log.info(
        "start committer gossip: address["
            + committerAddress
            + "] seedAddress:"
            + consenterAddress);
    return gossipService;
  }
}
