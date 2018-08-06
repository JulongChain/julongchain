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
package org.bcia.julongchain.core.container.inproccontroller;

import org.bcia.julongchain.common.exception.InprocVMException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.container.scintf.ISmartContractStream;
import org.bcia.julongchain.core.smartcontract.shim.SmartContractBase;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.core.smartcontract.shim.impl.ChatStream;
import org.bcia.julongchain.protos.node.SmartContractShim;

import java.io.InputStream;

/**
 * In-Process VM
 * Implement 2 methods:recv() & send()
 * Holds 2 Channel recv & send
 *
 * @author sunzongyu
 * @date 2018/4/2
 * @company Dingxuan
 */
public class InProcStream extends ChatStream implements ISmartContractStream {
	private static final JavaChainLog log = JavaChainLogFactory.getLog(InputStream.class);

	private Channel<SmartContractShim.SmartContractMessage> recv;
	private Channel<SmartContractShim.SmartContractMessage> send;

	public InProcStream(Channel<SmartContractShim.SmartContractMessage> recv, Channel<SmartContractShim.SmartContractMessage> send, SmartContractBase sc) {
		super(sc.newPeerClientConnection(), sc);
		this.recv = recv;
		this.send = send;
	}

	@Override
	public SmartContractShim.SmartContractMessage recv() throws InprocVMException {
		try {
			return recv.take();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			throw new InprocVMException(e.getMessage());
		}
	}

    @Override
	public void send(SmartContractShim.SmartContractMessage msg) {
		send.add(msg);
	}

}
