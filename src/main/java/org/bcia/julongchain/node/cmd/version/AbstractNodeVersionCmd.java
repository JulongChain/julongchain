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
package org.bcia.julongchain.node.cmd.version;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.entity.NodeVersion;

/**
 * 节点版本命令
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public abstract class AbstractNodeVersionCmd implements INodeCmd {

	private Node node;

	protected NodeVersion nodeVersion;

	public AbstractNodeVersionCmd(){
	}

	public AbstractNodeVersionCmd(Node node) {
		this.node = node;

		nodeVersion = new NodeVersion(node);
	}

	@Override
	public abstract void execCmd(String[] args) throws ParseException;
}
