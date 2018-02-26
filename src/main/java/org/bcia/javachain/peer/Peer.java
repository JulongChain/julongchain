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
package org.bcia.javachain.peer;

import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.peer.cmd.IPeerCmd;
import org.bcia.javachain.peer.cmd.factory.PeerCmdFactory;
import org.bcia.javachain.peer.util.Constant;
import org.springframework.stereotype.Component;

/**
 * Peer节点
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
@Component
public class Peer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Peer.class);

    /**
     * 对命令行的支持
     */
    private IPeerCmd peerCmd;

    public void execCmd(String[] args) throws ParseException {
        log.info("Peer Command Start");

//        //TODO:測試語句，正式版本去除begin------
//        String[] testArgs = new String[]{"channel", "create", "-o","localhost:7050","-c","mychannel","-f","filefile"};
//        args = testArgs;
//
//        for (String str : args) {
//            log.info("arg-----$" + str);
//        }
//        //TODO:測試語句，正式版本去除end------

        if(args.length <= 0){
            log.warn("peer command need more args-----");
            return;
        }

        int cmdWordCount;//记录命令单词数量
        String command = args[0];
        if(args.length == 1 && !Constant.VERSION.equalsIgnoreCase(command)){
            log.warn("peer "+ command +" need more args-----");
            return;
        }else if(args.length == 1 && Constant.VERSION.equalsIgnoreCase(command)){
            cmdWordCount = 1;
            peerCmd = PeerCmdFactory.getInstance(command, null);
        }else{
            cmdWordCount = 2;
            String subCommand = args[1];
            peerCmd = PeerCmdFactory.getInstance(command, subCommand);
        }

        if(peerCmd != null){
            String[] cleanArgs = new String[args.length - cmdWordCount];
            System.arraycopy(args, cmdWordCount, cleanArgs, 0, cleanArgs.length);
            peerCmd.execCmd(cleanArgs);//只传给子命令正式的参数值
        }


//        String[] testArgs = new String[]{"channel", "-start","hehe","-end","hehe"};
//
//        Options options = new Options();
//        //无需参数，打印版本信息
//        options.addOption(Constant.VERSION, false, "display peer version");
//        //需要支持peer node start/peer node status
//        options.addOption(Constant.NODE, true, "peer node functions");
//        //需要支持peer chaincode install -n
//        options.addOption(Option.builder(Constant.CHAINCODE).desc("peer chaincode functions").numberOfArgs(Option.UNLIMITED_VALUES).optionalArg(true).build());
//        //需要支持peer logging getlevel
//        options.addOption(Option.builder(Constant.CLI_LOG).desc("peer logging functions").numberOfArgs(Option.UNLIMITED_VALUES).optionalArg(true).build());
//        //需要支持peer channel create -o
//        options.addOption(Option.builder(Constant.CHANNEL).desc("peer channel functions").numberOfArgs(Option.UNLIMITED_VALUES).optionalArg(true).build());
//
//        CommandLineParser parser = new PeerCLIParser();
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

        log.info("Peer Command end");
    }
}
