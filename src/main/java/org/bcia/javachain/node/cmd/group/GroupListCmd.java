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
package org.bcia.javachain.node.cmd.group;

import org.apache.commons.cli.*;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.core.ssc.cssc.CSSC;


/**
 * 完成节点查看通道列表命令的解析,List命令无参数
 * node channel list
 *
 * @author wanglei
 * @date 18-3-22
 * @company Dingxuan
 */
public class GroupListCmd extends AbstractNodeGroupCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GroupCreateCmd.class);


    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        Options options = new Options();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        //配置系统智能合约,获取加入群组
        String groupId = nodeGroup.listGroup(CommConstant.CSSC, CSSC.GET_GROUPS, null);

        log.info("Group List info:" + groupId + "!");
    }
}
