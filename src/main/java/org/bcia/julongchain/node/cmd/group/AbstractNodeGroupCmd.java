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
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.entity.NodeGroup;

/**
 * 节点通道命令
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public abstract class AbstractNodeGroupCmd implements INodeCmd {

    protected NodeGroup nodeGroup;

    private Node node;

    public AbstractNodeGroupCmd(Node node) {
        this.nodeGroup = new NodeGroup(node);

        this.node = node;
    }

    @Override
    public abstract void execCmd(String[] args) throws ParseException, NodeException;
}
