/*
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
package org.bcia.julongchain.core.smartcontract;

import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policycheck.bean.Context;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.julongchain.core.container.scintf.SCID;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.protos.node.SmartContractPackage;

import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/07/25
 * @company Dingxuan
 */
public class SmartContractLauncherImpl implements ILaunchIntf {
	private static final JavaChainLog log = JavaChainLogFactory.getLog(SmartContractLauncherImpl.class);

	private Context ctxt;
	private SmartContractSupport scSupport;
	private SmartContractContext sccid;
	private SmartContractPackage.SmartContractDeploymentSpec sds;
//	builder   api.BuildSpecFactory

	@Override
	public Object launch(javax.naming.Context ctxt, Channel<Boolean> notfy) throws SmartContractException {
		Object[] launchConfigs = scSupport.getLaunchConfigs(sccid, sds.getSmartContractSpec().getType());
		String[] args = ((String[]) launchConfigs[0]);
		String[] envs = ((String[]) launchConfigs[1]);
		Map<String, byte[]> filesToUpload = ((Map<String, byte[]>) launchConfigs[2]);

		String canName = sccid.getCanonicalName();

		log.debug(String.format("Start container: %s(networkid:%s,peerid:%s)", canName, scSupport.getNodeNetworkID(), scSupport.getNodeID()));
		int i = 0;
		for (String arg : args) {
			log.debug("Start container: with arg" + i + ": " + arg);
		}
		i = 0;
		for (String env : envs) {
			log.debug("Start container: with env" + i + ": " + env);
		}

		SCID scid = new SCID(sds.getSmartContractSpec(), scSupport.getNodeNetworkID(), scSupport.getNodeID(), sccid.getVersion());


		return null;
	}

	public Context getCtxt() {
		return ctxt;
	}

	public void setCtxt(Context ctxt) {
		this.ctxt = ctxt;
	}

	public SmartContractSupport getScSupport() {
		return scSupport;
	}

	public void setScSupport(SmartContractSupport scSupport) {
		this.scSupport = scSupport;
	}

	public SmartContractContext getSccid() {
		return sccid;
	}

	public void setSccid(SmartContractContext sccid) {
		this.sccid = sccid;
	}

	public SmartContractPackage.SmartContractDeploymentSpec getSds() {
		return sds;
	}

	public void setSds(SmartContractPackage.SmartContractDeploymentSpec sds) {
		this.sds = sds;
	}
}
