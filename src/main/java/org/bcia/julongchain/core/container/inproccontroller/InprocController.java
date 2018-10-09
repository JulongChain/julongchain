/*
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
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.ssc.SystemSmartContractBase;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * In-process VM 控制器
 *
 * @author sunianle, sunzongyu
 * @date 2018/05/17
 * @company Dingxuan
 */
@Component
public class InprocController {
	private static JulongChainLog log = JulongChainLogFactory.getLog(InprocContainer.class);

	private static Map<String, InprocContainer> containers = new HashMap<>();

	public void register(SystemSmartContractBase sysSmartcontract) throws InprocVMException{
		String sscName = sysSmartcontract.getSystemSmartContractDescriptor().getSSCName();
		if (isRegistered(sscName)) {
			String msg = "Before register, [" + sscName + "] is already registered";
			log.info(msg);
			return;
		}
		containers.put(sscName, null);
		log.info(sscName + " register success");
	}

	public void logoff(String sscName) throws InprocVMException {
		if (!isRegistered(sscName)) {
			String msg = "Before logoff, [" + sscName + "] is not registered";
			log.error(msg);
			throw new InprocVMException(msg);
		}
		containers.remove(sscName);
		log.info("Logoff : [" + sscName + "] successful.");
	}

	public void deploy(SystemSmartContractBase sysSmartcontract,
					   String[] args) throws InprocVMException {
		String sscName = sysSmartcontract.getSystemSmartContractDescriptor().getSSCName();
		if (!isRegistered(sscName)) {
			String msg = "Before deploy, [" + sscName + "] is not registered";
			log.error(msg);
			throw new InprocVMException(msg);
		}
		if (isDeployed(sscName)) {
			String msg = "Before deploy, [" + sscName + "] is already deployed";
			log.info(msg);
			return;
		}
		InprocContainer container = new InprocContainer(sysSmartcontract, args);
		containers.put(sscName, container);
		log.info("Deployed : [" + sscName + "] successful.");
	}

	public void deDeploy(String sscName) throws InprocVMException {
		if (!isDeployed(sscName)) {
			String msg = "Before deDeploy, [" + sscName + "] is not deployed";
			log.error(msg);
			throw new InprocVMException(msg);
		}
		containers.put(sscName, null);
		log.info("Dedeployed : [" + sscName + "] successful.");
	}

	public boolean isRegistered(String sscName) {
		return containers.containsKey(sscName);
	}

	public boolean isDeployed(String sscName) {
		return containers.get(sscName) != null;
	}

	public void launch(String sscName) throws InprocVMException {
		if (!isDeployed(sscName)) {
			String msg = "Before launch, [" + sscName + "] is not deployed";
			log.error(msg);
			throw new InprocVMException(msg);
		}
		containers.get(sscName).startContainer();
	}

	public static Map<String, InprocContainer> getContainers() {
		return containers;
	}

	public static void setContainers(Map<String, InprocContainer> containers) {
		InprocController.containers = containers;
	}
}
