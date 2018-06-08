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
package org.bcia.julongchain.node.cmd.factory;

import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.cmd.group.GroupCreateCmd;
import org.bcia.julongchain.node.cmd.group.GroupJoinCmd;
import org.bcia.julongchain.node.cmd.group.GroupListCmd;
import org.bcia.julongchain.node.cmd.group.GroupUpdateCmd;
import org.bcia.julongchain.node.cmd.sc.ContractInstallCmd;
import org.bcia.julongchain.node.cmd.sc.ContractInstantiateCmd;
import org.bcia.julongchain.node.cmd.sc.ContractInvokeCmd;
import org.bcia.julongchain.node.cmd.sc.ContractQueryCmd;
import org.bcia.julongchain.node.cmd.server.ServerStartCmd;
import org.bcia.julongchain.node.cmd.server.ServerStatusCmd;
import org.bcia.julongchain.node.cmd.util.NodeCmdConstant;
import org.bcia.julongchain.node.cmd.version.NodeVersionCmd;
import org.bcia.julongchain.node.util.NodeConstant;

/**
 * 节点命令工厂
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class NodeCmdFactory {
    public static INodeCmd getInstance(Node node, String command, String subCommand) {
        if (NodeConstant.NODE.equalsIgnoreCase(command)) {
            if (NodeCmdConstant.START_SERVER.equalsIgnoreCase(subCommand)) {
                return new ServerStartCmd(node);
            } else if (NodeCmdConstant.SERVER_STATUS.equalsIgnoreCase(subCommand)) {
                return new ServerStatusCmd(node);
            }
        } else if (NodeConstant.GROUP.equalsIgnoreCase(command)) {
            if (NodeCmdConstant.CREATE_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupCreateCmd(node);
            } else if (NodeCmdConstant.JOIN_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupJoinCmd(node);
            } else if (NodeCmdConstant.UPDATE_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupUpdateCmd(node);
            } else if (NodeCmdConstant.LIST_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupListCmd(node);
            }
        } else if (NodeConstant.SMART_CONTRACT.equalsIgnoreCase(command)) {
            if (NodeCmdConstant.INSTALL_CONTRACT.equalsIgnoreCase(subCommand)) {
                return new ContractInstallCmd(node);
            } else if (NodeCmdConstant.INSTANCE_CONTRACT.equalsIgnoreCase(subCommand)) {
                return new ContractInstantiateCmd(node);
            } else if (NodeCmdConstant.INVOKE_CONTRACT.equalsIgnoreCase(subCommand)) {
                return new ContractInvokeCmd(node);
            }else if (NodeCmdConstant.QUERY_CONTRACT.equalsIgnoreCase(subCommand)) {
                return new ContractQueryCmd(node);
            }
        } else if (NodeConstant.VERSION.equalsIgnoreCase(command)) {
            return new NodeVersionCmd(node);
        }

        return null;
    }

}
