package org.bcia.julongchain.consenter.common.deliver;

import org.bcia.julongchain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Deliver 客户端测试类
 * @author zhangmingyang
 * @Date 2018-02-26
 * @company Dingxuan
 */
public class DeliverClientTest extends BaseJunit4Test{
    @Autowired
    public DeliverClient deliverClient;
    @Test
    public void send() {
        String ip="localhost";
        int port=7050;
        String mess="testmessage";
//        deliverClient.send(ip,port,mess);
    }
}
