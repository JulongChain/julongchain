package org.bcia.javachain.consenter.common.deliver;

import org.bcia.javachain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
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
        int port=7051;
        String mess="testmessage";
//        deliverClient.send(ip,port,mess);
    }
}
