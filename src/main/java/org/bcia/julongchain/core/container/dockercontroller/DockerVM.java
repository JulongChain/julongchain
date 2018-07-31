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
package org.bcia.julongchain.core.container.dockercontroller;

import org.bcia.julongchain.common.exception.VMException;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.julongchain.core.container.scintf.SCID;
import org.bcia.julongchain.core.container.api.IBuildSpecFactory;
import org.bcia.julongchain.core.container.api.IFormatter;
import org.bcia.julongchain.core.container.api.IPrelaunchFunc;
import org.bcia.julongchain.core.container.api.VM;
import org.bcia.julongchain.core.smartcontract.shim.SmartContractBase;

import javax.naming.Context;
import java.io.Reader;
import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public class DockerVM implements VM{
	@Override
	public void deploy(SmartContractBase smartContract, SmartContractContext scc, String[] args, String envs) throws VMException {

	}

	@Override
	public void start() throws VMException {

	}

	@Override
	public void stop() throws VMException {

	}

	@Override
	public void destroy() throws VMException {

	}

	@Override
	public String getVMName(SCID ccID, IFormatter format) throws VMException {
		return null;
	}
}
