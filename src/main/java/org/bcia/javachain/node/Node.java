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
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.factory.NodeCmdFactory;
import org.bcia.javachain.node.util.Constant;
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

    public void execCmd(String[] args) throws ParseException {
        log.info("Node Command Start");

        if (args.length <= 0) {
            log.warn("Node command need more args-----");
            return;
        }

        int cmdWordCount;//记录命令单词数量
        String command = args[0];
        if (args.length == 1 && !Constant.VERSION.equalsIgnoreCase(command)) {
            log.warn("Node " + command + " need more args-----");
            return;
        } else if (args.length == 1 && Constant.VERSION.equalsIgnoreCase(command)) {
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
            nodeCmd.execCmd(cleanArgs);//只传给子命令正式的参数值
        }


//        String[] testArgs = new String[]{"channel", "-start","hehe","-end","hehe"};
//
//        Options options = new Options();
//        //无需参数，打印版本信息
//        options.addOption(Constant.VERSION, false, "display Node version");
//        //需要支持Node node start/Node node status
//        options.addOption(Constant.NODE, true, "Node node functions");
//        //需要支持Node chaincode install -n
//        options.addOption(Option.builder(Constant.CHAINCODE).desc("Node chaincode functions").numberOfArgs(Option.UNLIMITED_VALUES).optionalArg(true).build());
//        //需要支持Node logging getlevel
//        options.addOption(Option.builder(Constant.CLI_LOG).desc("Node logging functions").numberOfArgs(Option.UNLIMITED_VALUES).optionalArg(true).build());
//        //需要支持Node channel create -o
//        options.addOption(Option.builder(Constant.CHANNEL).desc("Node channel functions").numberOfArgs(Option.UNLIMITED_VALUES).optionalArg(true).build());
//
//        CommandLineParser parser = new NodeCLIParser();
//        CommandLine cmd = parser.parse(options, testArgs);
//
//        if (cmd.hasOption(Constant.VERSION)) {
//            System.out.println("version 0.1");
//        }
//
//        String defaultValue = "UnKown";
//        if (cmd.hasOption(Constant.NODE)) {
//            String operations = cmd.getOptionValue(Constant.NODE, defaultValue);
//            System.out.println(operations + " Done!");
//        }
//
//        if (cmd.hasOption(Constant.CHAINCODE)) {
//            String operations = cmd.getOptionValue(Constant.CHAINCODE, defaultValue);
//            System.out.println(operations + " Done!");
//        }
//
//        if (cmd.hasOption(Constant.CLI_LOG)) {
//            String operations = cmd.getOptionValue(Constant.CLI_LOG, defaultValue);
//            System.out.println(operations + " Done!");
//        }
//
//        if (cmd.hasOption(Constant.CHANNEL)) {
//            String[] operations = cmd.getOptionValues(Constant.CHANNEL);
//
//            for (String operation : operations) {
//                log.info("operation-----$" + operation);
//            }
//        }

        log.info("Node Command end");
    }
}
