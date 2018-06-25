package org.bcia.julongchain.gossip;

import com.codahale.metrics.MetricRegistry;
import org.apache.gossip.GossipMember;
import org.apache.gossip.GossipService;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.event.GossipListener;
import org.apache.gossip.event.GossipState;
import org.apache.gossip.model.SharedGossipDataMessage;
import org.apache.gossip.udp.UdpSharedGossipDataMessage;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/06/08
 * @company Dingxuan
 */
public class GossipServiceUtilTest {

  private static JavaChainLog log = JavaChainLogFactory.getLog(GossipServiceUtilTest.class);

  @Test
  /** 测试启动gossip服务 */
  public void newGossipService() throws GossipException {
    String address = "localhost:7052";
    GossipServiceUtil.newGossipService(address).start();
  }

  @Test
  /** 测试上传数据 */
  public void addData() throws GossipException {
    String address = "localhost:7052";
    String group = "group";
    GossipService gossipService = GossipServiceUtil.newGossipService(address);
    gossipService.start();
    String data = "hello";
    GossipServiceUtil.addData(gossipService, group, 1l, null);
  }

  @Test
  /** 测试上传数据，另一个节点读取数据 */
  public void getData() throws GossipException {

    String address_addData = "localhost:7052";
    String address_readData = "localhost:7053";
    String address_readData2 = "localhost:7054";

    String group = "group111";

    GossipService gossipService_add = GossipServiceUtil.newGossipService(address_addData);

    GossipService gossipService_read =
        GossipServiceUtil.newGossipService(address_readData, address_addData);

    GossipService gossipService_read2 =
        GossipServiceUtil.newGossipService(address_readData2, address_addData);

    gossipService_add.start();
    gossipService_read.start();
    gossipService_read2.start();

    String data = "hello gossip";

    GossipServiceUtil.addData(gossipService_add, group, 1l, null);

    try {
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  /** 测试上传数据，另一个节点读取数据 */
  public void getDataFromRemote() throws GossipException {

    String id2 = UUID.randomUUID().toString();

    String address_readData = "192.168.1.50:7053";
    String group = "group";

    GossipService gossipService =
        GossipServiceUtil.newGossipService(address_readData, "192.168.1.110:7052");
    gossipService.start();

    try {
      Thread.sleep(2000);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Test
  public void test1(String[] args) throws Exception {
    System.out.println("hello world");

    GossipService gossipService1 =
        GossipServiceUtil.newGossipService("localhost:7060");
    gossipService1.start();

    GossipService gossipService2 =
        GossipServiceUtil.newGossipService("localhost:7070", "localhost:7060");
    gossipService2.start();


    TestGossip payload = new TestGossip();
    payload.setName("name");
    payload.setAge(1);


    UdpSharedGossipDataMessage message = new UdpSharedGossipDataMessage();
    message.setKey("testKey");
    message.setPayload(payload);
    message.setExpireAt(Long.MAX_VALUE);
    message.setTimestamp(System.currentTimeMillis());

    gossipService1.gossipSharedData(message);

    Thread.sleep(5000);

    SharedGossipDataMessage message1 = gossipService2.findSharedData("testKey");
    System.out.println(message1);

    Object payload1 = message1.getPayload();
    System.out.println(payload1);

    TestGossip testGossip = (TestGossip) payload1;
    System.out.println(testGossip.getName());
    System.out.println(testGossip.getAge());
  }

  @Test
  public void test2(String[] args) throws Exception {

    GossipService gossipService =
        GossipServiceUtil.newGossipService("localhost:7060");
    gossipService.start();


    while(true){
      Thread.sleep(20000);

      log.info(gossipService.getGossipManager().getLiveMembers().toString());

      SharedGossipDataMessage message = gossipService.findSharedData("testKey");
      SharedGossipDataMessage testKey = gossipService.getGossipManager().findSharedGossipData("testKey");
      System.out.println("testKey:" + testKey);

      if(message == null){
        log.info("message is null");
        continue;
      }

      Object payload = message.getPayload();
      if(payload == null){
        log.info("payload is null");
        continue;
      }

      if(!(payload instanceof Common.Block)) {
        continue;
      }

      Common.Block block = (Common.Block) payload;

      log.info(block.toString());

      log.info(block.getHeader().toString());
    }

  }

  @Test
  public void test3(String[] args) throws Exception {

    GossipService gossipService = new GossipService("cluster", URI.create("udp://localhost:7060"), "id1", new
        HashMap<String, String>(), new ArrayList<GossipMember>(), new GossipSettings(), new GossipListener() {

      @Override
      public void gossipEvent(GossipMember member, GossipState state) {

      }
    }, new MetricRegistry());

    gossipService.start();

    while(true){

      Thread.sleep(2000);
      log.info(gossipService.getGossipManager().getLiveMembers().toString());

      SharedGossipDataMessage message = gossipService.findSharedData("testKey");
      if(message == null){
        log.info("message is null");
        continue;
      }
      log.info(message.toString());

      TestGossip testGossip = (TestGossip) message.getPayload();

      log.info(testGossip.getName());
      log.info(testGossip.getAge().toString());
      log.info(testGossip.getBlock().toString());

    }

  }
}
