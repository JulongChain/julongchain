/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain;

import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.common.server.ConsenterServer;
import org.bcia.javachain.node.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 这是本项目的入口类（描述该类的功能）
 *
 * @author zhouhui   （编码人员）
 * @date 2018/1/25  （创建日期)
 * @company Dingxuan （公司名称）
 */
public class App {
    private static JavaChainLog log = JavaChainLogFactory.getLog(App.class);

    public static void main(String[] args) {
        log.info("JavaChain begin, This is a right log");
        System.out.println("JavaChain begin, This is a wrong log");

        //示例异常日志的打印方式
        try {
            throw new NodeException("I make a node exception");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        //异步启动Consenter服务
        new Thread() {
            @Override
            public void run() {
                ConsenterServer server = new ConsenterServer();
                try {
                    server.start();
                    server.blockUntilShutdown();
                } catch (IOException ex) {
                    log.error(ex.getMessage(), ex);
                } catch (InterruptedException ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }.start();

        //等待1秒，让Consenter服务完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
        }

        //引入Spring配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Node node = context.getBean(Node.class);

        //开始解析执行命令行
        try {
            node.execCmd(args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}