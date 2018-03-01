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
package org.bcia.javachain.node.cmd.channel;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.springframework.stereotype.Component;

/**
 * 完成节点创建通道命令的解析
 * node channel create -c localhost:7050 -ci mychannel -f /home/javachain/channel.tx
 *
 * @author zhouhui
 * @date 2018/2/24
 * @company Dingxuan
 */
@Component
public class ChannelCreateCmd extends NodeChannelCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ChannelCreateCmd.class);

    //参数：consenter地址
    private static final String ARG_CONSENTER = "c";
    //参数：channelId
    private static final String ARG_CHANNEL_ID = "ci";
    //参数：channel配置文件路径
    private static final String ARG_FILE_PATH = "f";
    //参数：超时时间
    private static final String ARG_TIMEOUT = "t";
    //参数：是否使用TLS传输
    private static final String ARG_USE_TLS = "tls";
    //参数：CA文件位置
    private static final String ARG_CA = "ca";

    @Override
    public void execCmd(String[] args) throws ParseException {

        Options options = new Options();
        options.addOption(ARG_CONSENTER, true, "input IP and port");
        options.addOption(ARG_CHANNEL_ID, true, "input channel id");
        options.addOption(ARG_FILE_PATH, true, "channel config file");
        options.addOption(ARG_TIMEOUT, true, "channel create timeout");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String defaultValue = "UnKown";

        String consenter = null;
        if (cmd.hasOption(ARG_CONSENTER)) {
            consenter = cmd.getOptionValue(ARG_CONSENTER, defaultValue);
            log.info("consenter-----$" + consenter);
        }

        String channelId = null;
        if (cmd.hasOption(ARG_CHANNEL_ID)) {
            channelId = cmd.getOptionValue(ARG_CHANNEL_ID, defaultValue);
            log.info("channelId-----$" + channelId);
        }

        String channelTxFile = null;
        if (cmd.hasOption(ARG_FILE_PATH)) {
            channelTxFile = cmd.getOptionValue(ARG_FILE_PATH, defaultValue);
            log.info("channel TxFile-----$" + channelTxFile);
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

        if (StringUtils.isBlank(consenter)) {
            log.error("Consenter should not be null, Please input it");
            return;
        }

        String[] ipAndPort = consenter.split(":");
        if (ipAndPort.length <= 1) {
            log.error("Consenter is not valid");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(ipAndPort[1]);
        } catch (NumberFormatException ex) {
            log.error("Consenter's port is not valid");
            return;
        }

        nodeChannel.createChannel(ipAndPort[0], port, channelId);

        log.info("channel created!");
    }

}
