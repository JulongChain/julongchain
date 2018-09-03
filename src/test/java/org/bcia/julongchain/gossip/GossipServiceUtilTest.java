package org.bcia.julongchain.gossip;

import org.apache.gossip.manager.GossipManager;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/06/08
 * @company Dingxuan
 */
public class GossipServiceUtilTest {

    private static JulongChainLog log = JulongChainLogFactory.getLog(GossipServiceUtilTest.class);

    @Test
    /** 测试启动gossip服务 */
    public void newGossipService() throws GossipException {
        Integer port = 7052;
        String address = "localhost:" + port;
        GossipServiceUtil.newGossipService(address).init();
        boolean portUsing = true;
        try{
            new ServerSocket(port);
            portUsing = false;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        Assert.assertTrue(portUsing);

    }

    @Test
    /** 测试上传数据 */
    public void addData() throws GossipException {
        String address = "localhost:7052";
        String group = "group";
        GossipManager gossipService = GossipServiceUtil.newGossipService(address);
        gossipService.init();
        String data = "hello";
        GossipServiceUtil.shareData(gossipService, group, 1L, data);
    }

    @Test
    /** 测试上传数据，另一个节点读取数据 */
    public void getData() throws GossipException {

        String address_addData = "localhost:7052";
        String address_readData = "localhost:7053";
        String address_readData2 = "localhost:7054";

        String group = "group";

        GossipManager gossipService_add = GossipServiceUtil.newGossipService(address_addData);

        GossipManager gossipService_read =
                GossipServiceUtil.newGossipService(address_readData, address_addData);

        GossipManager gossipService_read2 =
                GossipServiceUtil.newGossipService(address_readData2, address_addData);

        gossipService_add.init();
        gossipService_read.init();
        gossipService_read2.init();

        String data = "hello gossip";

        Long seqNum = 1L;

        GossipServiceUtil.shareData(gossipService_add, group, seqNum, data);

        try {
            Thread.sleep(1000);
            Assert.assertEquals(GossipServiceUtil.findData(gossipService_read, group, seqNum), data);
            Assert.assertEquals(GossipServiceUtil.findData(gossipService_read2, group, seqNum), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    /** 测试上传数据，另一个节点读取数据 */
    public void getDataFromRemote() throws GossipException {

        String address_readData = "192.168.1.213:50000";
        String address_seed = "192.168.1.71:50000";
        String group = "group";
        Long seqNum = 1L;

        GossipManager gossipService =
                GossipServiceUtil.newGossipService(address_readData, address_seed);
        gossipService.init();

        try {
            Thread.sleep(2000);
            Assert.assertEquals(GossipServiceUtil.findData(gossipService, group, seqNum), "hello world");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
