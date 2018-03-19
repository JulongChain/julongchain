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
package org.bcia.javachain.node;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.factory.NodeCmdFactory;
import org.bcia.javachain.node.util.NodeConstant;
import org.springframework.stereotype.Component;

/**
 * 节点对象
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
@Component
public class Node {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Node.class);

    /**
     * 对命令行的支持
     */
    private INodeCmd nodeCmd;

    public void execCmd(String[] args) throws ParseException, NodeException {
        log.info("Node Command Start");

        if (args.length <= 0) {
            log.warn("Node command need more args-----");
            return;
        }

        int cmdWordCount;//记录命令单词数量
        String command = args[0];
        if (args.length == 1 && !NodeConstant.VERSION.equalsIgnoreCase(command)) {
            log.warn("Node " + command + " need more args-----");
            return;
        } else if (args.length == 1 && NodeConstant.VERSION.equalsIgnoreCase(command)) {
            //只有version命令只有一个单词，其余都是"命令+子命令"的形式,如"node server start"
            cmdWordCount = 1;
            nodeCmd = NodeCmdFactory.getInstance(command, null);
        } else {
            cmdWordCount = 2;
            String subCommand = args[1];
            nodeCmd = NodeCmdFactory.getInstance(command, subCommand);
        }

        if (nodeCmd != null) {
            String[] cleanArgs = new String[args.length - cmdWordCount];
            System.arraycopy(args, cmdWordCount, cleanArgs, 0, cleanArgs.length);
            //只传给子命令正式的参数值
            nodeCmd.execCmd(cleanArgs);
        }

        log.info("Node Command end");
    }
}
