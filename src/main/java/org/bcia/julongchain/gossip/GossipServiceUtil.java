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
import com.google.protobuf.InvalidProtocolBufferException;
import jdk.nashorn.internal.runtime.options.LoggingOption;
import org.apache.commons.lang3.StringUtils;
import org.apache.gossip.GossipMember;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteGossipMember;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.apache.gossip.model.SharedGossipDataMessage;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.localconfig.ConsenterConfigFactory;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.protos.common.Common;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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
  public static void addData(
      GossipService gossipService, String group, Long seqNum, Common.Block data)
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

    String blockStr = "";
    try {
      blockStr = new String(data.toByteArray(), "ISO8859-1");
      // log.info("blockStr:" + blockStr);
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      throw new GossipException(e);
    }

    int fileNumber = blockStr.length() / messageLength;
    if (blockStr.length() % messageLength > 0) {
      fileNumber = fileNumber + 1;
    }
    log.info("fileNumber:" + fileNumber);

    SharedGossipDataMessage m = new SharedGossipDataMessage();
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

      SharedGossipDataMessage msg = new SharedGossipDataMessage();
      msg.setExpireAt(Long.MAX_VALUE);
      msg.setKey(group + "-" + seqNum + "-" + i);
      msg.setPayload(str);
      msg.setTimestamp(System.currentTimeMillis());
      gossipService.gossipSharedData(msg);

      log.info("send gossip detail:[" + group + "-" + seqNum + "-" + i + "]");
    }
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
  public static Common.Block getData(GossipService gossipService, String group, Long seqNum)
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

    SharedGossipDataMessage sharedData = gossipService.findSharedData(group + "-" + seqNum);

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
      SharedGossipDataMessage m = gossipService.findSharedData(group + "-" + seqNum + "-" + i);
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

  /**
   * 从配置文件读取consenter地址，并启动gossip服务
   *
   * @return
   * @throws GossipException
   */
  public static GossipService startConsenterGossip() throws GossipException {
    String consenterAddress =
        ConsenterConfigFactory.loadConsenterConfig().getGeneral().getGossipAddress();
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

  public static void main(String[] args) {
    String str = "11122233344455566";
    int number = str.length() / 3;
    if (str.length() % 3 > 0) {
      number = number + 1;
    }
    int current = 0;
    for (int i = 0; i < number; i++) {
      int start = i * 3;
      int end = (i + 1) * 3;
      if (end > str.length()) {
        end = str.length();
      }
      String s = str.substring(start, end);
      System.out.println(s);
    }
  }
}
