/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.smartcontract;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.DuplicateChaincodeHandlerException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.Utils;
import org.bcia.julongchain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractProvider;
import org.bcia.julongchain.core.container.DockerUtil;
import org.bcia.julongchain.core.container.VMController;
import org.bcia.julongchain.core.container.inproccontroller.InprocController;
import org.bcia.julongchain.core.container.scintf.ISCSupport;
import org.bcia.julongchain.core.container.scintf.ISmartContractStream;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.core.smartcontract.accesscontrol.Authenticator;
import org.bcia.julongchain.core.smartcontract.accesscontrol.CertAndPrivKeyPair;
import org.bcia.julongchain.core.smartcontract.accesscontrol.ICA;
import org.bcia.julongchain.core.smartcontract.client.SmartContractSupportClient;
import org.bcia.julongchain.core.smartcontract.node.SmartContractRunningUtil;
import org.bcia.julongchain.core.smartcontract.node.SmartContractSupportService;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;

import javax.naming.Context;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bcia.julongchain.protos.node.SmartContractPackage.*;
import static org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage;
import static org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage.Type;

/**
 * 类描述
 *
 * @author wanliangbing, sunzongyu
 * @date 2018/3/14
 * @company Dingxuan
 */
public class SmartContractSupport implements ISCSupport {

	private static JavaChainLog log = JavaChainLogFactory.getLog(SmartContractSupport.class);

	private ICA ca;
	private Authenticator auth;
	private RunningSmartContract runningSmartContract;
	private String nodeAddress;
	private Duration scStartupTimeout;
	private String nodeNetworkID;
	private String nodeID;
	private Duration keepalive;
	private String smartContractLogLevel;
	private String shimLogLevel;
	private String logFormat;
	private Duration executetimeout;
	private boolean userRunsSC;
	private boolean nodeTLS;

	/** DevModeUserRunsChaincode property allows user to run chaincode in development environment */
	public static final String DevModeUserRunsChaincode = "dev";

	public static final int chaincodeStartupTimeoutDefault = 5000;
	public static final String peerAddressDefault = "0.0.0.0:7051";

	/** TXSimulatorKey is used to attach ledger simulation context */
	public static String TXSimulatorKey = "txsimulatorkey";

	/** HistoryQueryExecutorKey is used to attach ledger history query executor context */
	public static String HistoryQueryExecutorKey = "historyqueryexecutorkey";

	/** Mutual TLS auth client key and cert paths in the chaincode container */
	String TLS_CLIENT_KEY_PATH = "/etc/hyperledger/fabric/client.key";
	String TLS_CLIENT_CERT_PATH = "/etc/hyperledger/fabric/client.crt";
	String TLS_CLIENT_ROOT_CERT_PATH = "/etc/hyperledger/fabric/peer.crt";

	public SmartContractSupport() {
		this.runningSmartContract = new RunningSmartContract();
	}

	/**
	 * use this for ledger access and make sure TXSimulator is being used
	 *
	 * @param context
	 * @return
	 */
	public ITxSimulator getTxSimulator(Context context) {
		try {
			return (ITxSimulator) context.lookup(TXSimulatorKey);
		} catch (Exception e) {
			log.error("Got error when lookup " + TXSimulatorKey + "\n" + e.getMessage());
			return null;
		}
	}

	/**
	 * use this for ledger access and make sure IHistoryQueryExecutor is being used
	 *
	 * @param context
	 * @return
	 */
	public IHistoryQueryExecutor getHistoryQueryExecutor(Context context) {
		try {
			return ((IHistoryQueryExecutor) context.lookup(HistoryQueryExecutorKey));
		} catch (Exception e) {
			log.error("Got error when lookup " + HistoryQueryExecutorKey + "\n" + e.getMessage());
			return null;
		}
	}

	/**
	 * register placeholder Handler. This will be transferred in registerHandler
	 * NOTE: from this point, existence of handler for this chaincode means the chaincode
	 * is in the process of getting started (or has been started)
	 *
	 * @param smartcontract
	 * @param notfy
	 */
	public void preLaunchSetup(String smartcontract, Channel<Boolean> notfy) {
		Handler handler = new Handler();
		handler.setReadyNotify(notfy);
		SmartContractRTEnv smartContractRTEnv = new SmartContractRTEnv(handler);
		this.runningSmartContract.getSmartContractRTEnvMap().put(smartcontract, smartContractRTEnv);
	}

	/**
	 * call this under lock
	 *
	 * @param smartcontract
	 * @return
	 */
	public SmartContractRTEnv smartcontractHasBeenLaunched(String smartcontract) {
		return this.runningSmartContract.getSmartContractRTEnvMap().get(smartcontract);
	}

	/**
	 * call this under lock
	 *
	 * @param smartcontract
	 * @return
	 */
	public boolean launchStarted(String smartcontract) {
		if (this.runningSmartContract.getSmartContractRTEnvMap().containsKey(smartcontract)) {
			return true;
		}
		return false;
	}

	/**
	 * NewChaincodeSupport creates a new ChaincodeSupport instance
	 *
	 * @param
	 * @return
	 */
	public SmartContractSupport(String scEndpoint, boolean userRunsSC, Duration scStartupTimeOut, ICA ca) {
		String pnid = NodeConfigFactory.getNodeConfig().getNode().getNetworkId();
		String pid = NodeConfigFactory.getNodeConfig().getNode().getId();
		boolean tlsEnable = NodeConfigFactory.getNodeConfig().getNode().getTls().isEnabled();
		String ka = NodeConfigFactory.getNodeConfig().getSmartContract().getKeepalive();
		String executetimeout = NodeConfigFactory.getNodeConfig().getSmartContract().getExecutetimeout();
		Map<String, String> logging = NodeConfigFactory.getNodeConfig().getSmartContract().getLogging();

		// TODO: 7/24/18 do not implement
		this.ca = ca;
		this.runningSmartContract = new RunningSmartContract();
		this.nodeNetworkID = pnid;
		this.nodeID = pid;

		// TODO: 7/24/18 do not implement
		this.auth = new Authenticator();
		this.nodeAddress = scEndpoint;

		log.debug("Smartcontract support using nodeAddress:[" + this.nodeAddress + "]");

		this.userRunsSC = userRunsSC;
		this.scStartupTimeout = scStartupTimeout;
		this.nodeTLS = tlsEnable;

		if (this.nodeTLS) {
			this.auth.disableAccessCheck();
		}

		int kadef = 0;
		if (StringUtils.isEmpty(ka)) {
			this.keepalive = Duration.ofSeconds(kadef);
		} else {
			int t = 0;
			try {
				t = Integer.valueOf(ka);
			} catch (NumberFormatException e) {
				log.error("Invalid keeplive value " + ka + " defaulting to " + kadef);
			}
			if (t <= 0) {
				t = kadef;
			}
			this.keepalive = Duration.ofSeconds(t);
		}

		Duration execto = Duration.ofSeconds(30);
		int eto = Integer.valueOf(executetimeout);
		if (eto <= 1) {
			log.error("Invalid execute timeout value " + eto + " defaulting " + execto.toString());
		} else {
			log.debug("Setting execute timeout value to " + eto);
			execto = Duration.ofSeconds(eto);
		}

		this.executetimeout = execto;

		this.smartContractLogLevel = getLogLevel("level");
		this.shimLogLevel = getLogLevel("shim");
		this.logFormat = getLogLevel("format");
	}

	/**
	 * getLogLevelFromViper gets the chaincode container log levels from viper
	 *
	 * @param module
	 * @return
	 */
	public static String getLogLevel(String module) {
		Map<String, String> logging = NodeConfigFactory.getNodeConfig().getSmartContract().getLogging();
		return logging.get(module);
	}

	/**
	 * 启动智能合约
	 *
	 * @param scContext scContext
	 * @param spec smartContractInvocationSpec
	 * @return
	 * @throws SmartContractException
	 */
	public SmartContractInput launch(SmartContractContext scContext, Object spec)
			throws SmartContractException {

		log.info("call SmartContractSupport launch");

		String smartContractId = scContext.getName();

		String nodeId = NodeConfigFactory.getNodeConfig().getNode().getId();

		String version = scContext.getVersion();

		Boolean isSystemContract = SmartContractSupportClient.checkSystemSmartContract(smartContractId);

		boolean scRunning = false;

		if(BooleanUtils.isTrue(isSystemContract)){
			scRunning = SmartContractRunningUtil.checkSmartContractRunning(smartContractId);
		}else{
			scRunning = SmartContractRunningUtil.checkSmartContractRunning(nodeId + "-" + smartContractId);
		}

		if (!scRunning) {

			if (isSystemContract) {
				SmartContractSupportClient.launch(smartContractId);
				while (!SmartContractRunningUtil.checkSmartContractRunning(smartContractId)) {
					try {
						log.info("wait smart contract register[" + smartContractId + "]");
						Thread.sleep(1000);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			} else {
				try {
					String imageName = nodeId + "-" + smartContractId + "-" + version;
					String containerName = nodeId + "-" + smartContractId;

					List<String> images = DockerUtil.listImages(imageName);

					if (CollectionUtils.isEmpty(images)) {

						log.info("==========================images is null");

						// 清空instantiate目录
						String basePath =
								NodeConfigFactory.getNodeConfig().getSmartContract().getInstantiatePath()
										+ "/"
										+ smartContractId
										+ "-"
										+ version;
						File basePathFile = new File(basePath);
						if (!basePathFile.exists()) {
							FileUtils.forceMkdir(basePathFile);
						} else {
							File pomFile = new File(basePath + File.separator + "pom.xml");
							if (pomFile.exists()) {
								FileUtils.forceDelete(pomFile);
							}
							File srcFile = new File(basePath + File.separator + "src");
							if (srcFile.exists()) {
								FileUtils.deleteDirectory(srcFile);
							}
						}

						// 从文件系统读取安装的文件
						ISmartContractPackage smartContractPackage =
								SmartContractProvider.getSmartContractFromFS(smartContractId, version);
						ByteString codePackage = smartContractPackage.getDepSpec().getCodePackage();
						// 压缩文件
						byte[] gzipBytes = IoUtil.gzipReader(codePackage.toByteArray(), 1024);
						// 读取文件目录和文件内容
						Map<String, byte[]> scFileBytesMap = IoUtil.tarReader(gzipBytes, 1024);
						// 保存文件到instantiate目录
						IoUtil.fileWriter(scFileBytesMap, basePath);
						// 复制Dockerfile文件
						String dockerFile = NodeConfigFactory.getNodeConfig().getSmartContract().getDockerFile();
						FileUtils.copyFileToDirectory(
								new File(dockerFile),
								new File(basePath));
						// 设置Docker容器的CORE_NODE_ADDRESS环境变量
						String coreNodeAddress =
								NodeConfigFactory.getNodeConfig().getSmartContract().getCoreNodeAddress();

						log.info("============================ core node address:" + coreNodeAddress);

						String coreNodeAddressPort = NodeConfigFactory.getNodeConfig().getSmartContract().getCoreNodeAddressPort();

						log.info("============================= core node address port:" + coreNodeAddressPort);

						String coreNodeAddressAndPortArgus = " -a " + coreNodeAddress + ":" + coreNodeAddressPort;

						Utils.replaceFileContent(
								basePath + File.separator + "Dockerfile.in",
								"#core_node_address#",
								coreNodeAddress);
						// build镜像
						String imageId =
								DockerUtil.buildImage(basePath + "/Dockerfile.in", imageName);

						log.info("====================image id :" + imageId);


						// 创建容器
						// "/bin/sh",
						// "-c",
						// "java -jar /root/julongchain/target/julongchain-smartcontract-java-jar-with-dependencies.jar -i " + containerName
						String containerId = DockerUtil.createContainer(imageId, containerName, "/bin/sh", "-c", "java -jar /root/julongchain/target/julongchain-smartcontract-java-jar-with-dependencies.jar -i " + containerName + coreNodeAddressAndPortArgus);

						log.info("========================containerId:" + containerId);

						// 启动容器
						DockerUtil.startContainer(containerId);

						while (!SmartContractRunningUtil.checkSmartContractRunning(containerName)) {
							log.info("wait smart contract register[" + containerName + "]");
							Thread.sleep(1000);
						}
					} else {
						log.info("========================container id ==============" + containerName);
						DockerUtil.startContainer(containerName);
						while (!SmartContractRunningUtil.checkSmartContractRunning(containerName)) {
							log.info("wait smart contract register[" + containerName + "]");
							Thread.sleep(1000);
						}
					}
				} catch (Exception e) {
					throw new SmartContractException(e);
				}
			}
		}

		if (spec instanceof SmartContractDeploymentSpec) {
			log.info("================ deployment");
			log.info(version);
			SmartContractDeploymentSpec deploymentSpec = (SmartContractDeploymentSpec) spec;
			return deploymentSpec.getSmartContractSpec().getInput();
		}

		if (spec instanceof SmartContractInvocationSpec) {
			log.info("================ invocation");
			SmartContractInvocationSpec invocationSpec = (SmartContractInvocationSpec) spec;
			return invocationSpec.getSmartContractSpec().getInput();
		}

		return SmartContractInput.newBuilder().build();
	}

	/**
	 * 执行智能合约
	 *
	 * @param scContext scContext
	 * @param scMessage scMessage
	 * @param timeout 超时时间
	 * @return
	 * @throws SmartContractException
	 */
	public SmartContractMessage execute(
			SmartContractContext scContext, SmartContractMessage scMessage, long timeout)
			throws SmartContractException {

		log.info("call SmartContractSupport execute");

		// TODO:add by zhouhui for test,返回一个空对象，实际处理待万良兵补充

		String smartContractId = scContext.getName();

		Boolean isSystemContract = SmartContractSupportClient.checkSystemSmartContract(smartContractId);
		if(!BooleanUtils.isTrue(isSystemContract)){
			smartContractId = NodeConfigFactory.getNodeConfig().getNode().getId() + "-" + smartContractId;
		}

		SmartContractMessage responseMessage =
				SmartContractSupportService.invoke(smartContractId, scMessage);

		return responseMessage;
	}

	public synchronized void registerHandler(Handler handler) throws DuplicateChaincodeHandlerException, SmartContractException {
		String key = handler.getSmartContractID().getName();

		SmartContractRTEnv scret = smartcontractHasBeenLaunched(key);
		if (scret != null && scret.getHandler().getRegistered()) {
			log.debug("duplicate registered handler(key:" + key + ") return error");
			throw new DuplicateChaincodeHandlerException(handler);
		}

		if (scret != null) {
			handler.setReadyNotify(scret.getHandler().getReadyNotify());
			scret.setHandler(handler);
		} else {
			if (!this.userRunsSC) {
				throw new SmartContractException("Node will not accept external smartcontract connection [" + key + "]");
			}
			this.runningSmartContract.getSmartContractRTEnvMap().put(key, new SmartContractRTEnv(handler));
		}

		handler.setRegistered(true);

		handler.setTxCtxs(new HashMap<>());
		handler.setTxidMap(new HashMap<>());

		log.debug("Registered handler complete for smartcontract [" + key + "]");
	}

	public synchronized void deregisterHandler(Handler handler) throws SmartContractException{
		for (Map.Entry<String, TransactionContext> en : handler.getTxCtxs().entrySet()) {
			TransactionContext txContext = en.getValue();
			for (Map.Entry<String, IResultsIterator> entry : txContext.getQueryIteratorMap().entrySet()) {
				try {
					entry.getValue().close();
				} catch (LedgerException e) {
					log.error("Got error when close result iterator");
					log.error(e.getMessage());
					throw new SmartContractException(e);
				}
			}
		}

		String key = handler.getSmartContractID().getName();
		log.debug("Deregister handler: " + key);
		if (smartcontractHasBeenLaunched(key) == null) {
			String errMsg = "Error deregistering handler, could not find handler with key " + key;
			log.error(errMsg);
			throw new SmartContractException(errMsg);
		}
		runningSmartContract.getSmartContractRTEnvMap().remove(key);
		log.debug("Deregistered handler with key " + key);
	}

	public synchronized void sendReady(
			Context context, SmartContractContext scContext, Duration timeout) throws SmartContractException{
		String canName = scContext.getCanonicalName();
		SmartContractRTEnv scrte = smartcontractHasBeenLaunched(canName);
		if (scrte == null) {
			String errMsg = "Handler nopt found for smartcontract " + canName;
			log.error(errMsg);
			throw new SmartContractException(errMsg);
		}

		Channel<String> errChannel = new Channel<>();
		Channel<SmartContractMessage> notfy = scrte.getHandler().ready(context, scContext.getChainID(), scContext.getTxID(), scContext.getSignedProposal(), scContext.getProposal());
		//get channel of [READY] successful and waiting for message from shim
		if (notfy != null) {
			Thread getMsg = new Thread(() -> {
				try {
					SmartContractMessage scMsg = notfy.take();
					if (Type.ERROR.equals(scMsg.getType())) {
						String errMsg = "Error initializing container " + canName + ": " + scMsg.getPayload().toStringUtf8();
						log.error(errMsg);
						errChannel.add(errMsg);
					}
					if (Type.COMPLETED.equals(scMsg.getType())) {
						ProposalResponsePackage.Response response = null;
						try {
							response = ProposalResponsePackage.Response.parseFrom(scMsg.getPayload());
						} catch (InvalidProtocolBufferException e) {
							String errMsg = "Error Unmarshal scMsg payload. " + canName + ": " + scMsg.getPayload().toStringUtf8();
							log.error(errMsg);
							errChannel.add(errMsg);
						}
						if (response.getStatus() != 200) {
							String errMsg = "Error initializing container " + canName + ": " + scMsg.getPayload().toStringUtf8();
							log.error(errMsg);
							errChannel.add(errMsg);
						}
						String errMsg = "Success initializing container " + canName + ": " + scMsg.getPayload().toStringUtf8();
						log.debug(errMsg);
						errChannel.add(errMsg);
					}
				} catch (InterruptedException e) {
					String errMsg = "Timeout expired while executing send init message";
					log.error(errMsg);
					errChannel.add(errMsg);
				}
			});
			//start thread [getMsg]
			getMsg.start();
			try {
				//set timeout
				getMsg.join(timeout.toMillis());
			} catch (InterruptedException e) {
				log.debug("Thread was interrupted.\n" + e.getMessage());
			}
			//interrupt thread
			getMsg.interrupt();
			scrte.getHandler().deleteTxContext(scContext.getChainID(), scContext.getTxID());
			//judge type of thread[getMsg] return
			String errMsg = null;
			try {
				errMsg = errChannel.take();
			} catch (InterruptedException e) {
				log.error("Got InterruptedException when handle errMsg in sendReady()");
			}
			if (!errMsg.startsWith("S")) {
				throw new SmartContractException(errMsg);
			}
		}

	}

	private Map<String, byte[]> getTLSFiles(CertAndPrivKeyPair keyPair) {
		if (keyPair == null) {
			return null;
		}

		return new HashMap<String, byte[]>(){{
			put(TLS_CLIENT_KEY_PATH, keyPair.getKey().getBytes());
			put(TLS_CLIENT_CERT_PATH, keyPair.getCert().getBytes());
			put(TLS_CLIENT_ROOT_CERT_PATH, ca.certBytes());
		}};
	}

	/**
	 * index	0:args
	 * 			1:envs
	 * 			2:fileToUpload
	 * @param sccid
	 * @param type
	 * @return
	 * @throws SmartContractException
	 */
	public Object[] getLaunchConfigs(SmartContractContext sccid, SmartContractSpec.Type type) throws SmartContractException{
		String canName = sccid.getName();
		String[] envs = new String[]{"CORE_CHAINCODE_ID_NAME=" + canName};
		String[] args = new String[]{};
		Map<String, byte[]> filesToUpload = new HashMap<>();

		// ----------------------------------------------------------------------------
		// Pass TLS options to chaincode
		// ----------------------------------------------------------------------------
		// Note: The peer certificate is only baked into the image during the build
		// phase (see core/chaincode/platforms).  This logic below merely assumes the
		// image is already configured appropriately and is simply toggling the feature
		// on or off.  If the peer's x509 has changed since the chaincode was deployed,
		// the image may be stale and the admin will need to remove the current containers
		// before restarting the peer.
		// ----------------------------------------------------------------------------
		CertAndPrivKeyPair certKeyPair = new CertAndPrivKeyPair();
		if (nodeTLS) {
			try {
				certKeyPair = auth.generate(sccid.getCanonicalName());
			} catch (SmartContractException e) {
				log.error("Failed generating TLS cert for " + sccid.getCanonicalName());
				throw e;
			}
			ArrayUtils.add(envs, "CORE_PEER_TLS_ENABLED=true");
			ArrayUtils.add(envs, "CORE_TLS_CLIENT_KEY_PATH=" + TLS_CLIENT_KEY_PATH);
			ArrayUtils.add(envs, "CORE_TLS_CLIENT_CERT_PATH=" + TLS_CLIENT_CERT_PATH);
			ArrayUtils.add(envs, "CORE_TLS_CLIENT_ROOT_CERT_PATH=" + TLS_CLIENT_ROOT_CERT_PATH);
		} else {
			ArrayUtils.add(envs, "CORE_PEER_TLS_ENABLED=false");
		}

		if (!StringUtils.isEmpty(smartContractLogLevel)) {
			ArrayUtils.add(envs, "CORE_CHAINCODE_LOGGING_LEVEL=" + smartContractLogLevel);
		}

		if (!StringUtils.isEmpty(shimLogLevel)) {
			ArrayUtils.add(envs, "CORE_CHAINCODE_LOGGING_SHIM=" + shimLogLevel);
		}

		if (!StringUtils.isEmpty(logFormat)) {
			ArrayUtils.add(envs, "CORE_CHAINCODE_LOGGING_FORMAT=" + logFormat);
		}

		switch (type.getNumber()) {
			case SmartContractSpec.Type.GOLANG_VALUE:
			case SmartContractSpec.Type.CAR_VALUE:
				args = new String[]{
						"smartcontract", "-node.address=" + nodeAddress
				};
				break;
			case SmartContractSpec.Type.JAVA_VALUE:
				args = new String[]{
						"-i" + sccid.getName()
				};
				break;
			case SmartContractSpec.Type.NODE_VALUE:
				args = new String[]{
						"/bin/sh", "-c", "cd /usr/local/src; npm start -- --node.address " + nodeAddress
				};
				break;
			default:
				throw new SmartContractException("Unknown smartcontract type: " + type);
		}

//		filesToUpload = getTLSFiles(certKeyPair);
		int i = 0;
		for (String arg : args) {
			log.debug("arg" + i + ": " + arg);
		}
		i = 0;
		for (String env : envs) {
			log.debug("env" + i + ": " + env);
		}

		return new Object[]{args, envs, filesToUpload};
	}

	public String[][] getArgsAndEnv(
			SmartContractContext smartContractContext, SmartContractSpec.Type type) {

		return null;
	}

//	public synchronized void launchAndWaitForRegister(SmartContractContext scc, ILaunchIntf launch, SmartContractDeploymentSpec smartContractDeploymentSpec) throws SmartContractException {
//		String canName = scc.getCanonicalName();
//		if (StringUtils.isEmpty(canName)) {
//			throw new SmartContractException("Smartcontract name is not set");
//		}
//		if (smartcontractHasBeenLaunched(canName) != null) {
//			throw new SmartContractException("Smartcontract [" + canName + "] has been launched");
//		}
//		if (launchStarted(canName)) {
//			throw new SmartContractException("Smartcontract [" + canName + "] is already launching.");
//		}
//		log.debug("Smartcontract [" + canName + "] is launching");
//		runningSmartContract.getLaunchStarted().put(canName, true);
//		Channel<Boolean> notyf = new Channel<>();
//		Channel<String> errChannel = new Channel<>();
//
//		try {
//			Thread t = new Thread(() -> {
//				VMCResp resp = null;
//				try {
//					resp = launch.launch(scc.toContext(), notyf);
//				} catch (SmartContractException e) {
//					errChannel.add("Got error when starting container");
//				}
//				if (resp.getE() != null) {
//					errChannel.add("Got error when starting container\n" + resp.getE().getMessage());
//				} else {
//					try {
//						errChannel.add(notyf.take().toString());
//					} catch (InterruptedException e) {
//						errChannel.add("Got error when get notyf message");
//					}
//				}
//			});
//			t.start();
////			t.join(scStartupTimeout.toMillis());
//			t.interrupt();
//			String msg = errChannel.take();
//			if (!"true".equalsIgnoreCase(msg)) {
//				String errMsg = "Registration failed.ID:[" + canName + "], networkid:[" + nodeNetworkID + "], nodeID:[" + nodeID + "], txid:[" + scc.getTxID() + "]";
//				log.error(errMsg);
//				log.error(msg);
//				throw new SmartContractException(errMsg);
//			}
//		} catch (InterruptedException e) {
//			log.error(e.getMessage());
//			throw new SmartContractException(e);
//		} finally {
//			runningSmartContract.getLaunchStarted().remove(canName);
//			log.debug("Smartcontract [" + canName + "] launch seq completed");
//		}
//	}

	public void stop(
			Context context,
			SmartContractContext smartContractContext,
			SmartContractDeploymentSpec smartContractDeploymentSpec) {
		// TODO: 7/20/18  
	}

	public String getVMType(SmartContractDeploymentSpec smartContractDeploymentSpec) {
		if (SmartContractDeploymentSpec.ExecutionEnvironment.SYSTEM.equals(smartContractDeploymentSpec.getExecEnv())) {
			return VMController.SYSTEM;
		}
		return VMController.DOCKER;
	}

	@Override
	public void handleSmartContractStream(
			Context context, ISmartContractStream smartContractStream) {
	}

	public void register() {
	}

	public SmartContractMessage createSmartContractMessage(
			Type smartContractMessageType, String txid, SmartContractInput smartContractInput) {
		return null;
	}

	public SmartContractMessage execute(
			Context context,
			SmartContractContext smartContractContext,
			SmartContractMessage smartContractMessage,
			Duration timeout) {
		return null;
	}

	public ICA getCa() {
		return ca;
	}

	public void setCa(ICA ca) {
		this.ca = ca;
	}

	public Authenticator getAuth() {
		return auth;
	}

	public void setAuth(Authenticator auth) {
		this.auth = auth;
	}

	public RunningSmartContract getRunningSmartContract() {
		return runningSmartContract;
	}

	public void setRunningSmartContract(RunningSmartContract runningSmartContract) {
		this.runningSmartContract = runningSmartContract;
	}

	public String getNodeAddress() {
		return nodeAddress;
	}

	public void setNodeAddress(String nodeAddress) {
		this.nodeAddress = nodeAddress;
	}

	public Duration getScStartupTimeout() {
		return scStartupTimeout;
	}

	public void setScStartupTimeout(Duration scStartupTimeout) {
		this.scStartupTimeout = scStartupTimeout;
	}

	public String getNodeNetworkID() {
		return nodeNetworkID;
	}

	public void setNodeNetworkID(String nodeNetworkID) {
		this.nodeNetworkID = nodeNetworkID;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public Duration getKeepalive() {
		return keepalive;
	}

	public void setKeepalive(Duration keepalive) {
		this.keepalive = keepalive;
	}

	public String getSmartContractLogLevel() {
		return smartContractLogLevel;
	}

	public void setSmartContractLogLevel(String smartContractLogLevel) {
		this.smartContractLogLevel = smartContractLogLevel;
	}

	public String getShimLogLevel() {
		return shimLogLevel;
	}

	public void setShimLogLevel(String shimLogLevel) {
		this.shimLogLevel = shimLogLevel;
	}

	public String getLogFormat() {
		return logFormat;
	}

	public void setLogFormat(String logFormat) {
		this.logFormat = logFormat;
	}

	public Duration getExecutetimeout() {
		return executetimeout;
	}

	public void setExecutetimeout(Duration executetimeout) {
		this.executetimeout = executetimeout;
	}

	public boolean isUserRunsSC() {
		return userRunsSC;
	}

	public void setUserRunsSC(boolean userRunsSC) {
		this.userRunsSC = userRunsSC;
	}

	public boolean isNodeTLS() {
		return nodeTLS;
	}

	public void setNodeTLS(boolean nodeTLS) {
		this.nodeTLS = nodeTLS;
	}
}
