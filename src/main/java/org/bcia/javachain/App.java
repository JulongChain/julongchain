/**
 * Copyright DingXuan. All Rights Reserved.
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

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.exception.PeerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.orderer.broadcast.BroadCastServer;
import org.bcia.javachain.peer.Peer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 这是本项目的入口类（描述该类的功能）
 *
 * @author zhouhui   （编码人员）
 * @date 2018-01-25  （创建日期)
 * @company Dingxuan （公司名称）
 */
public class App {
    private static JavaChainLog log = JavaChainLogFactory.getLog(App.class);

    public static void main(String[] args) {
        log.info("JavaChain begin, This is a right log");
        System.out.println("JavaChain begin, This is a wrong log");

        try {
            throw new PeerException("I make a peer exception");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        new Thread() {
            @Override
            public void run() {
                final BroadCastServer server = new BroadCastServer();
                try {
                    server.start();
                    server.blockUntilShutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
//        Peer peer = context.getBean(Peer.class);

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Peer peer = context.getBean(Peer.class);

//        Peer peer = new Peer();
        try {
            peer.execCmd(args);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
    }
}