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
package org.bcia.javachain.peer.cmd.channel;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

/**
 * 完成Peer创建通道命令的解析
 * peer channel create -o orderer.example.com:7050 -c $CHANNEL_NAME -f ./channel-artifacts/channel.tx >&log.txt
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
public class ChannelCreateCmd extends PeerChannelCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ChannelCreateCmd.class);

    //参数：orderer地址
    private static final String ARG_ORDERER = "o";
    //参数：channelId
    private static final String ARG_CHANNEL_ID = "c";
    //参数：channel配置文件路径
    private static final String ARG_FILE_PATH = "f";
    //参数：超时时间
    private static final String ARG_TIMEOUT = "t";

    @Override
    public void execCmd(String[] args) throws ParseException {

        Options options = new Options();
        options.addOption(ARG_ORDERER, true, "input IP and port");
        options.addOption(ARG_CHANNEL_ID, true, "channel name");
        options.addOption(ARG_FILE_PATH, true, "channel config file");
        options.addOption(ARG_TIMEOUT, true, "channel create timeout");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";

        String orderer = null;
        if (cmd.hasOption(ARG_ORDERER)) {
            orderer = cmd.getOptionValue(ARG_ORDERER, defaultValue);
            log.info("orderer-----$" + orderer);
        }

        String channelId = null;
        if (cmd.hasOption(ARG_CHANNEL_ID)) {
            channelId = cmd.getOptionValue(ARG_CHANNEL_ID, defaultValue);
            log.info("channelId-----$" + channelId);
        }

        String channelTxFile = null;
        if (cmd.hasOption(ARG_FILE_PATH)) {
            channelTxFile = cmd.getOptionValue(ARG_FILE_PATH, defaultValue);
            log.info("channelTxFile-----$" + channelTxFile);
        }

        String timeout = null;
        if (cmd.hasOption(ARG_TIMEOUT)) {
            timeout = cmd.getOptionValue(ARG_TIMEOUT, defaultValue);
            log.info("timeout-----$" + timeout);
        }

        if (StringUtils.isBlank(channelId)) {
            log.error("ChannelId should not be null, Please input it");
            return;
        }

        if (StringUtils.isBlank(orderer)) {
            log.error("Orderer should not be null, Please input it");
            return;
        }

        String[] ipAndPort = orderer.split(":");
        if (ipAndPort.length <= 1) {
            log.error("Orderer is not valid");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(ipAndPort[1]);
        } catch (NumberFormatException ex) {
            log.error("Orderer's port is not valid");
            return;
        }

        peerChannel.createChannel(ipAndPort[0], port, channelId);

        log.info("channel created!");
    }

}
