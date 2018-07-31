/**
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.node.cmd.server;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.entity.NodeServer;

/**
 * 节点服务器命令
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public abstract class AbstractNodeServerCmd implements INodeCmd {

	private Node node;

	protected NodeServer nodeServer;

	public AbstractNodeServerCmd() {
	}

	public AbstractNodeServerCmd(Node node){
		this.node = node;
		nodeServer = new NodeServer(node);
	}

	@Override
	public abstract void execCmd(String[] args) throws ParseException, NodeException;

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
