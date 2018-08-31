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
package org.bcia.julongchain.core.ssc;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.InprocVMException;
import org.bcia.julongchain.common.exception.SysSmartContractException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.core.container.inproccontroller.InprocController;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.ssc.cssc.CSSC;
import org.bcia.julongchain.core.ssc.essc.ESSC;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.core.ssc.qssc.QSSC;
import org.bcia.julongchain.core.ssc.vssc.VSSC;
import org.bcia.julongchain.protos.node.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * 系统智能合约管理器,整合管理功能函数接口
 * * 各接口被调用的时机：
 * 　1.Node启动时，调用registerSysSmartContracts()
 * 2.（SystemSmartContractManager内部）,调用loadSysSmartContracts()加载外部系统合约，与本地系统合约形成系统合约集合；
 * ３.（SystemSmartContractManager内部）,对每个系统合约，调用registerSysSmartContract(String smartContractID)
 * ４.调用deploySysSmartContracts("");
 * ５.(SystemSmartContractManager内部)deploySysSmartContracts调用buildSysSmartContracts编译智能合约，形成智能合约部署规范(DeploymentSpec);
 * ６.分别为每个组，调用deploySysSmartContracts(groupID);
 * ７.(SystemSmartContractManager内部)deploySysSmartContracts(groupID)调用buildSysSmartContracts编译智能合约，形成智能合约部署规范(DeploymentSpec);
 *
 * @author sunianle, sunzongyu1
 * @date 3/6/18
 * @company Dingxuan
 */

@Component
public class SystemSmartContractManager implements ISystemSmartContractManager {
    private SystemSmartContractDescriptor[] embedContractDescriptors = new SystemSmartContractDescriptor[5];
    private Map<String, ISystemSmartContract> sysSCMap = new HashMap<String, ISystemSmartContract>();
    private static JulongChainLog log = JulongChainLogFactory.getLog(SystemSmartContractManager.class);
	private static final String PLUGIN_CLASS = "Plugin";
    private static final String PLUGIN_METHOD = "plugin";
    private static final String PLUGIN_SUFFIX = ".jar";
    private static final List<SystemSmartContractDescriptor> sscPlugins = new ArrayList<>();
    @Autowired
    private CSSC cssc;
    @Autowired
    private ESSC essc;
    @Autowired
    private LSSC lssc;
    @Autowired
    private QSSC qssc;
    @Autowired
    private VSSC vssc;
    @Autowired
    private InprocController controller;

    @Autowired
    public SystemSmartContractManager() {
		// TODO: 7/31/18 Spring IOC invalid
    	cssc = new CSSC();
    	essc = new ESSC();
		lssc = new LSSC();
		qssc = new QSSC();
		vssc = new VSSC();
		init();
		controller = new InprocController();
        log.debug("Construct systemSmartContractManager");
    }

    @PostConstruct
    private void init() {
        log.debug("Init systemSmartContractManager");
        String[] args = new String[0];
        embedContractDescriptors[0] = new SystemSmartContractDescriptor(
                "cssc",
                CSSC.class.getName(),
                "development build",
                args,
                true,
                false,
				NodeConfigFactory.getNodeConfig().getSmartContract().getSystem().get("cssc").contains("enable")
        );
        embedContractDescriptors[1] = new SystemSmartContractDescriptor(
                "essc",
				ESSC.class.getName(),
				"development build",
                args,
                false,
                false,
				NodeConfigFactory.getNodeConfig().getSmartContract().getSystem().get("essc").contains("enable")
        );
        embedContractDescriptors[2] = new SystemSmartContractDescriptor(
                "lssc",
				LSSC.class.getName(),
				"development build",
                args,
                true,
                true,
				NodeConfigFactory.getNodeConfig().getSmartContract().getSystem().get("lssc").contains("enable")
        );
        embedContractDescriptors[3] = new SystemSmartContractDescriptor(
                "qssc",
				QSSC.class.getName(),
				"development build",
                args,
                true,
                true,
				NodeConfigFactory.getNodeConfig().getSmartContract().getSystem().get("qssc").contains("enable")
        );
        embedContractDescriptors[4] = new SystemSmartContractDescriptor(
                "vssc",
				VSSC.class.getName(),
				"development build",
                args,
                false,
                false,
				NodeConfigFactory.getNodeConfig().getSmartContract().getSystem().get("vssc").contains("enable")
        );
        cssc.setSystemSmartContractDescriptor(embedContractDescriptors[0]);
        essc.setSystemSmartContractDescriptor(embedContractDescriptors[1]);
        lssc.setSystemSmartContractDescriptor(embedContractDescriptors[2]);
        qssc.setSystemSmartContractDescriptor(embedContractDescriptors[3]);
        vssc.setSystemSmartContractDescriptor(embedContractDescriptors[4]);
    }


    @Override
    public void registerSysSmartContracts() {
        log.info("Register system contracts...");
        registerSysSmartContract(essc);
        registerSysSmartContract(lssc);
        registerSysSmartContract(cssc);
        registerSysSmartContract(qssc);
        registerSysSmartContract(vssc);
    }

    /**
     * 注册系统智能合约,相当于应用智能合约的Install
     *
     * @param contract 要注册的系统合约
     * @return 是否注册成功
     */
    private boolean registerSysSmartContract(SystemSmartContractBase contract){
        if(!contract.getSystemSmartContractDescriptor().isEnabled() ||
				!isWhitelisted(contract)){
            log.info("System Smartcontract ({},{},{}) disabled",
                    contract.getSystemSmartContractDescriptor().getSSCName(),
                    contract.getSystemSmartContractDescriptor().getSSCPath(),
                    contract.getSystemSmartContractDescriptor().isEnabled());
            return false;
        }

        String contractID = contract.getSmartContractID();
        sysSCMap.put(contractID, contract);
		try {
			controller.register(contract);
		} catch (InprocVMException e) {
			log.error("Register system contract {} failed:{}",contract.getSmartContractID(),e.getMessage());
			return false;
		}
        return true;
    }

    /**
	 * 系统智能合约部署
	 * 相当于应用智能合约的Instantiate
	 * TODO: 7/25/18 exit program when catch exception
     */
    @Override
    public void deploySysSmartContracts(String groupID){
		for (Map.Entry<String, ISystemSmartContract> entry : sysSCMap.entrySet()) {
			String sscName = entry.getKey();
			SystemSmartContractBase ssc = (SystemSmartContractBase) entry.getValue();
			String[] args = new String[]{
				"-i" + sscName
			};
			try {
				controller.deploy(ssc, args);
			} catch (InprocVMException e) {
				log.error("Deploy " + sscName + "failed");
				log.error(e.getMessage());
			}

		}
    }

    @Override
    public void deDeploySysSmartContracts(String groupID){
		for(Map.Entry<String, ISystemSmartContract> entry : sysSCMap.entrySet()) {
			String sscName = entry.getKey();
			try {
				controller.deDeploy(sscName);
			} catch (InprocVMException e) {
				log.error("Dedeploy " + sscName + "failed");
				log.error(e.getMessage());
			}
		}
    }

    private List<ByteString> getInitArgsByteStringList(String[] initArgs) {
    	List<ByteString> list = new ArrayList<>();
		for (String initArg : initArgs) {
			list.add(ByteString.copyFromUtf8(initArg));
		}
		return list;
	}

    @Override
    public boolean isSysSmartContract(String smartContractID) {
        for (SystemSmartContractDescriptor smartcontract : embedContractDescriptors) {
            if (smartContractID.equals(smartcontract.getSSCName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isWhitelisted(ISystemSmartContract contract) {
		Map<String, String> smartcontract = NodeConfigFactory.getNodeConfig().getSmartContract().getSystem();
		String value = smartcontract.get(contract.getSmartContractID()).toLowerCase();
		return "enable".equals(value) || "true".equals(value) || "yes".equals(value);
	}

    @Override
    public ISystemSmartContract getSystemSmartContract(String smartContractID) {
        return null;
    }

    @Override
    public boolean isSysSmartContractAndNotInvokableExternal(String smartContractID) {
		for(SystemSmartContractDescriptor sscd : embedContractDescriptors){
			if(smartContractID.equals(sscd.getSSCName())){
				return !sscd.isInvokableExternal();
			}
		}
        return false;
    }

    @Override
    public boolean isSysSmartContractAndNotInvokableSC2SC(String smartContractID) {
        for(SystemSmartContractDescriptor sscd : embedContractDescriptors){
            if(smartContractID.equals(sscd.getSSCName())){
                return !sscd.isInvokaleSC2SC();
            }
        }
        return false;
    }


	/**
	 * 编译智能合约，形成智能合约部署规范(DeploymentSpec);
	 */
    private SmartContractPackage.SmartContractDeploymentSpec buildSysSmartContract(SmartContractPackage.SmartContractSpec spec) {
    	return SmartContractPackage.SmartContractDeploymentSpec.newBuilder()
				.setExecEnv(SmartContractPackage.SmartContractDeploymentSpec.ExecutionEnvironment.SYSTEM)
				.setSmartContractSpec(spec)
				// TODO: 7/19/18 new byte[] in fabric
				.setCodePackage(ByteString.EMPTY)
				.build();
    }

	/**
	 * 加载外部系统智能合约插件
	 */
    private List<SystemSmartContractDescriptor> loadSysSmartContracts() throws SysSmartContractException {
    	List<PluginConfig> config = new ArrayList<>();
		Map<String, String> systemPlugins = NodeConfigFactory.getNodeConfig().getSmartContract().getSystemPlugins();
		// TODO: 7/19/18 yaml system plugins
		loadSysSmartContractWithConfig(config);
		return sscPlugins;
	}

	/**
	 * 根据配置加载系统智能合约
	 */
	private static void loadSysSmartContractWithConfig(List<PluginConfig> config) throws SysSmartContractException{
		for (PluginConfig conf : config) {
			SystemSmartContractDescriptor sc = new SystemSmartContractDescriptor(conf.getName(),
					conf.getPath(),
					null,
					null,
					conf.isInvokableExternal(),
					conf.isInvokableSC2SC(),
					conf.isEnable());
			ISmartContract plugin = loadPlugin(conf.getPath());
			if (plugin == null) {
				log.info("Load System Smartcontract {} from path {} unsuccessfully.", sc.getSSCName(), sc.getSSCPath());
				continue;
			}
			sc.setSmartContract(plugin);
			sscPlugins.add(sc);
			log.info("Successfully load System Smartcontract {} from path {}.", sc.getSSCName(), sc.getSSCPath());
		}
	}

	/**
	 * java插件以jar包形式存在
	 * TODO: 7/19/18 具体内部细节待定
	 */
	private static ISmartContract loadPlugin(String path) throws SysSmartContractException{
		if (!path.endsWith(PLUGIN_SUFFIX)) {
			String errMsg = "Java plugin must be a jar file";
			log.error(errMsg);
			return null;
		}
		File file = new File(path);
		if (!file.exists()) {
			String errMsg = "Can not find plugin at path [" + path + "]";
			log.error(errMsg);
			return null;
		}
		ISmartContract ssc = null;
		try {
			URL url = new URL("file:" + path);
			URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
			Class<?> clazz = urlClassLoader.loadClass(PLUGIN_CLASS);
			Method method = clazz.getMethod(PLUGIN_METHOD);
			ssc = ((ISmartContract) method.invoke(clazz.newInstance()));
		} catch (Exception e) {
			String errMsg = "Got error when load plugin. Err:{\n" + e.getMessage() + "\n}";
			log.error(errMsg);
			throw new SysSmartContractException(errMsg);
		}
		return ssc;
	}
}
