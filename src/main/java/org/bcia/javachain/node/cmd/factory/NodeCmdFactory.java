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
package org.bcia.javachain.node.cmd.factory;

import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.group.GroupCreateCmd;
import org.bcia.javachain.node.cmd.group.GroupJoinCmd;
import org.bcia.javachain.node.cmd.group.GroupListCmd;
import org.bcia.javachain.node.cmd.group.GroupUpdateCmd;
import org.bcia.javachain.node.cmd.sc.ContractInstallCmd;
import org.bcia.javachain.node.cmd.sc.ContractInstantiateCmd;
import org.bcia.javachain.node.cmd.sc.ContractInvokeCmd;
import org.bcia.javachain.node.cmd.server.ServerStartCmd;
import org.bcia.javachain.node.cmd.server.ServerStatusCmd;
import org.bcia.javachain.node.cmd.util.NodeCmdConstant;
import org.bcia.javachain.node.cmd.version.VersionCmd;
import org.bcia.javachain.node.util.NodeConstant;

/**
 * 节点命令工厂
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class NodeCmdFactory {
    public static INodeCmd getInstance(String command, String subCommand) {
        if (NodeConstant.NODE.equalsIgnoreCase(command)) {
            if (NodeCmdConstant.START_SERVER.equalsIgnoreCase(subCommand)) {
                return new ServerStartCmd();
            } else if (NodeCmdConstant.SERVER_STATUS.equalsIgnoreCase(subCommand)) {
                return new ServerStatusCmd();
            }
        } else if (NodeConstant.GROUP.equalsIgnoreCase(command)) {
            if (NodeCmdConstant.CREATE_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupCreateCmd();
            } else if (NodeCmdConstant.JOIN_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupJoinCmd();
            }else if(NodeCmdConstant.UPDATE_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupUpdateCmd();
            }else if(NodeCmdConstant.LIST_GROUP.equalsIgnoreCase(subCommand)) {
                return new GroupListCmd();
            }
        } else if (NodeConstant.SMART_CONTRACT.equalsIgnoreCase(command)) {
            if (NodeCmdConstant.INSTALL_CONTRACT.equalsIgnoreCase(subCommand)) {
                return new ContractInstallCmd();
            } else if (NodeCmdConstant.INSTANCE_CONTRACT.equalsIgnoreCase(subCommand)) {
                return new ContractInstantiateCmd();
            }else if (NodeCmdConstant.INVOKE_CONTRACT.equalsIgnoreCase(subCommand)) {
                return new ContractInvokeCmd();
            }
        } else if (NodeConstant.VERSION.equalsIgnoreCase(command)) {
            return new VersionCmd();
        }

        return null;
    }

}
