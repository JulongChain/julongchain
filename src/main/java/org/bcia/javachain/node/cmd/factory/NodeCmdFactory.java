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
package org.bcia.javachain.node.cmd.factory;

import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.NodeServerCmd;
import org.bcia.javachain.node.cmd.channel.ChannelCreateCmd;
import org.bcia.javachain.node.util.Constant;

/**
 * Peer命令工厂
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class NodeCmdFactory {
	public static INodeCmd getInstance(String command, String subCommand) {
		if(Constant.NODE.equalsIgnoreCase(command)){
			return new NodeServerCmd();
		}else if(Constant.CHANNEL.equalsIgnoreCase(command)){
			if(Constant.CREATE.equalsIgnoreCase(subCommand)){
				return new ChannelCreateCmd();
			}
		}else if(Constant.NODE.equalsIgnoreCase(command)){
			return new NodeServerCmd();
		}else if(Constant.NODE.equalsIgnoreCase(command)){
			return new NodeServerCmd();
		}else if(Constant.NODE.equalsIgnoreCase(command)){
			return new NodeServerCmd();
		}

		return null;
	}

}
