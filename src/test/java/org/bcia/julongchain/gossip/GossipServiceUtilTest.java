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
  public void testGet() throws GossipException, InterruptedException {
    // GossipService gossipService = GossipServiceUtil.newGossipService("0.0.0.0:7080", "0.0.0.0:7060");
    // gossipService.start();
    GossipService gossipService = GossipServiceUtil.startConsenterGossip();

    UdpSharedGossipDataMessage m = new UdpSharedGossipDataMessage();
    m.setTimestamp(System.currentTimeMillis());
    m.setExpireAt(Long.MAX_VALUE);
    m.setKey("myGroup-1");
    m.setPayload("2222222");

    gossipService.gossipSharedData(m);

    for(int i = 0;i<20;i++){
      UdpSharedGossipDataMessage msg = new UdpSharedGossipDataMessage();
      msg.setTimestamp(System.currentTimeMillis());
      msg.setExpireAt(Long.MAX_VALUE);
      msg.setKey("myGroup-1-"+i);

      StringBuffer sb = new StringBuffer("");
      for(int j=0;j<10000;j++){
        sb.append(1+"");
      }

      msg.setPayload(sb.toString());

      gossipService.gossipSharedData(msg);
    }


    while(true){
    }
  }

  @Test
  public void testGet2() throws GossipException, InterruptedException {
    // GossipService gossipService = GossipServiceUtil.newGossipService("0.0.0.0:7080", "0.0.0.0:7060");
    // gossipService.start();
    GossipService gossipService = GossipServiceUtil.startCommitterGossip();
    while(true){
      SharedGossipDataMessage sharedData = gossipService.findSharedData("myGroup-1");
      if(sharedData == null){
        continue;
      }
      System.out.println("[myGroup-1]" +sharedData.getPayload());

      for(int i=0;i<20;i++){

        SharedGossipDataMessage sd = gossipService.findSharedData("myGroup-1-"+i);
        if(sd == null){
          log.info("[myGroup-1-"+i+"] is null");
          continue;
        }

        System.out.println("[myGroup-1-"+i+"] " + sd.getPayload().toString());
      }



      Thread.sleep(2000);
    }
  }

}
