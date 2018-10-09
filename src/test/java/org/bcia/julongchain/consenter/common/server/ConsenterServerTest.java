package org.bcia.julongchain.consenter.common.server;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * consenter服务测试
 * @author zhangmingyang
 * @Date: 2018/3/1
 * @company Dingxuan
 */
public class ConsenterServerTest {
@Autowired
public ConsenterServer consenterServer;
    @Test
    public void start() throws IOException {
        consenterServer.start();
    }

    @Test
    public void blockUntilShutdown() throws InterruptedException {
        consenterServer.blockUntilShutdown();
    }
}