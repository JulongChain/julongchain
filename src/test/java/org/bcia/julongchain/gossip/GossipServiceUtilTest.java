package org.bcia.julongchain.gossip;

import org.apache.gossip.GossipService;
import org.junit.Assert;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.UUID;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/06/08
 * @company Dingxuan
 */
public class GossipServiceUtilTest {

  @Test
  /** 测试启动gossip服务 */
  public void newGossipService() {
    String id = UUID.randomUUID().toString();
    String group = "group";
    String address = "localhost:7052";

    try {
      GossipServiceUtil.newGossipService(id, group, address).start();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    GossipService gossipService = GossipServiceUtil.idAndGossipServiceMap.get(id);
    Assert.assertNotNull(gossipService);
    String group_ = GossipServiceUtil.addressAndGroupMap.get(address);
    Assert.assertEquals(group_, group);
    GossipService gossipService1 = GossipServiceUtil.addressAndGossipServiceMap.get(address);
    Assert.assertNotNull(gossipService1);
    Assert.assertEquals(gossipService, gossipService1);
  }

  @Test
  /** 测试上传数据 */
  public void addData() {
    String id = UUID.randomUUID().toString();
    String address = "localhost:7052";
    String group = "group";
    try {
      GossipService gossipService = GossipServiceUtil.newGossipService(id, group, address);
      gossipService.start();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String data = "hello";
    GossipServiceUtil.addData(address, 1l, data);
  }

  @Test
  /** 测试上传数据，另一个节点读取数据 */
  public void getData() {

    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();

    String address_addData = "localhost:7052";
    String address_readData = "localhost:7053";
    String group = "group111";

    try {
      GossipService gossipService_add =
          GossipServiceUtil.newGossipService(id1, group, address_addData);
      GossipService gossipService_read =
          GossipServiceUtil.newGossipService(id2, group, address_readData);

      gossipService_add.start();
      gossipService_read.start();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    String data = "hello gossip";

    GossipServiceUtil.addData(address_addData, 1l, data);

    try {
      Thread.sleep(5000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String string = (String) GossipServiceUtil.getData(address_readData, 1l);
    System.out.println(string);

    Assert.assertEquals(string, data);
  }

  @Test
  /** 测试上传数据，另一个节点读取数据 */
  public void getDataFromRemote() {

    String id2 = UUID.randomUUID().toString();

    String address_readData = "192.168.1.50:7053";
    String group = "group";

    try {
      GossipService gossipService =
          GossipServiceUtil.newGossipService(
              id2, group, address_readData, UUID.randomUUID().toString(), "192.168.1.110:7052");
      gossipService.start();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    try {
      Thread.sleep(2000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String string = (String) GossipServiceUtil.getData(address_readData, 1l);
    System.out.println(string);

    Assert.assertEquals(string, "hello");
  }
}
