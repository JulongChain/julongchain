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

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.InprocVMException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.container.scintf.ISCSupport;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.smartcontract.shim.SmartContractBase;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.core.smartcontract.shim.impl.ChatStream;
import org.bcia.julongchain.core.smartcontract.shim.impl.Handler;
import org.bcia.julongchain.core.smartcontract.shim.impl.NextStateInfo;
import org.bcia.julongchain.core.ssc.SystemSmartContractBase;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.protos.node.SmartContractShim;

import javax.naming.Context;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/7/18
 * @company Dingxuan
 */
public class InprocContainer {
	private static final JavaChainLog log = JavaChainLogFactory.getLog(InprocContainer.class);

	private SystemSmartContractBase sysSmartContract;
    private String[] args;

    public InprocContainer() {
	}

    public InprocContainer(SystemSmartContractBase sysSmartContract) {
		this();
        this.sysSmartContract = sysSmartContract;
    }

	public InprocContainer(SystemSmartContractBase sysSmartContract, String[] args) {
		this(sysSmartContract);
		this.args = args;
	}

	public void startContainer() {
    	logPath(sysSmartContract);
		logArgs(args);
		sysSmartContract.start(args);
	}

	private void logPath(SystemSmartContractBase ssc) {
    	log.debug("In-Process VM start system smartcontract [" + ssc.getSystemSmartContractDescriptor().getSSCPath() + "]");
	}

	private void logArgs(String[] args) {
		for (String arg : args) {
			log.debug("In-Process VM start with args " + arg);
		}
	}
}
