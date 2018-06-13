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
package org.bcia.julongchain.node.cmd.sc;

import org.apache.commons.cli.ParseException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.cmd.INodeCmd;
import org.bcia.julongchain.node.entity.NodeSmartContract;

/**
 * 节点合约命令
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public abstract class AbstractNodeContractCmd implements INodeCmd {

	private Node node;

	protected NodeSmartContract nodeSmartContract;

	public AbstractNodeContractCmd(){
		nodeSmartContract = new NodeSmartContract();
	}

	public AbstractNodeContractCmd(Node node) {
		this.node = node;

		nodeSmartContract = new NodeSmartContract(node);
	}

	@Override
	public abstract void execCmd(String[] args) throws ParseException, NodeException;
}
