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
package org.bcia.julongchain.node.cmd.group;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.protos.node.Query;

import java.util.List;


/**
 * 完成节点查看群组列表命令的解析,List命令无参数
 * node group list
 *
 * @author wanglei zhouhui
 * @date 2018/3/22
 * @company Dingxuan
 */
public class GroupListCmd extends AbstractNodeGroupCmd {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GroupListCmd.class);

    public GroupListCmd(Node node) {
        super(node);
    }

    @Override
    public void execCmd(String[] args) throws ParseException, NodeException {
        List<Query.GroupInfo> groupsList = nodeGroup.listGroups();

        if (groupsList != null) {
            int groupSize = groupsList.size();
            log.info("Group List size: " + groupSize);
            for (int i = 0; i < groupSize; i++) {
                String groupId = groupsList.get(i).getGroupId();
                log.info("Group List info: " + groupId + "\n");
            }
        } else {
            log.info("Group List : 0");
        }
    }
}
