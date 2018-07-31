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
package org.bcia.julongchain;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.Consenter;
import org.bcia.julongchain.node.Node;

import java.util.Arrays;

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
        //打印异常日志的正反样例
//        log.info("JulongChain begin, This is a right log");
//        System.out.println("JulongChain begin, This is a wrong log");

        //示例异常日志的打印方式
//        try {
//            throw new NodeException("I make a node exception");
//        } catch (Exception ex) {
//            log.error(ex.getMessage(), ex);
//        }

        //引入Spring配置文件的两种方式
//        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
//        Node node = context.getBean(Node.class);
//        Node node = SpringContext.getInstance().getBean(Node.class);

        log.info("Args: " + Arrays.toString(args));
        //开始解析执行命令行
        try {
            if (args.length > 0 && args[0].equals("consenter")) {
                Consenter consenter = new Consenter();

                String[] cleanArgs = new String[args.length - 1];
                System.arraycopy(args, 1, cleanArgs, 0, cleanArgs.length);
                consenter.execCmd(cleanArgs);
            } else {
                Node node = Node.getInstance();
                node.execCmd(args);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}