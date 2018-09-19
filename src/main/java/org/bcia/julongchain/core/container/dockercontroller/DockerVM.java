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

import com.google.protobuf.ByteString;
import org.apache.commons.io.FileUtils;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.VMException;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.Utils;
import org.bcia.julongchain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractProvider;
import org.bcia.julongchain.core.container.DockerUtil;
import org.bcia.julongchain.core.container.api.VM;
import org.bcia.julongchain.core.node.NodeConfig;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.core.smartcontract.node.SmartContractRunningUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public class DockerVM implements VM{

	private static JulongChainLog log = JulongChainLogFactory.getLog(DockerVM.class);

	private String smartContractId;
	private String version;

	private String getNodeIdFromYaml() {
		return NodeConfigFactory.getNodeConfig().getNode().getId();
	}

	private String getCoreNodeAddressPort() {
		return NodeConfigFactory.getNodeConfig().getSmartContract().getCoreNodeAddressPort();
	}

	private String getCoreNodeAddress() {
		return NodeConfigFactory.getNodeConfig().getSmartContract().getCoreNodeAddress();
	}

	private String getBaseImage() {
		return NodeConfigFactory.getNodeConfig().getSmartContract().getBaseImage();
	}

	private String createBaseDirectory() throws IOException {
		String basePath = NodeConfigFactory.getNodeConfig().getSmartContract().getInstantiatePath() + "/" + smartContractId + "-" + version;
		File file = new File(basePath);
		if (!file.exists()) {
			FileUtils.forceMkdir(file);
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
		return basePath;
	}

	public String getImageName() {
		String imageName = getNodeIdFromYaml() + "-" + smartContractId + "-" + version;
		return imageName;
	}

	public String getContainerName() {
		String containerName = getNodeIdFromYaml() + "-" + smartContractId;
		return containerName;
	}

	@Override
	public void deploy() throws VMException {
		// 清空instantiate目录
		String basePath = null;
		try {
			basePath = createBaseDirectory();
		} catch (IOException e) {
			throw new VMException(e.getMessage(), e);
		}

		// 从文件系统读取安装的文件
		ISmartContractPackage smartContractPackage = null;
		try {
			smartContractPackage = SmartContractProvider.getSmartContractFromFS(smartContractId, version);
		} catch (JulongChainException e) {
			throw new VMException(e.getMessage(), e);
		}
		ByteString codePackage = smartContractPackage.getDepSpec().getCodePackage();
		// 压缩文件
		byte[] gzipBytes = new byte[0];
		try {
			gzipBytes = IoUtil.gzipReader(codePackage.toByteArray(), 1024);
		} catch (JulongChainException e) {
			throw new VMException(e.getMessage(), e);
		}
		// 读取文件目录和文件内容
		Map<String, byte[]> scFileBytesMap = null;
		try {
			scFileBytesMap = IoUtil.tarReader(gzipBytes, 1024);
		} catch (JulongChainException e) {
			throw new VMException(e.getMessage(), e);
		}
		// 保存文件到instantiate目录
		try {
			IoUtil.fileWriter(scFileBytesMap, basePath);
		} catch (JulongChainException e) {
			throw new VMException(e.getMessage(), e);
		}
		// 复制Dockerfile文件
		String dockerFile = NodeConfigFactory.getNodeConfig().getSmartContract().getDockerFile();
		try {
			FileUtils.copyFileToDirectory(new File(dockerFile), new File(basePath));
		} catch (IOException e) {
			throw new VMException(e.getMessage(), e);
		}
		// replace core_node_address
		String dockerFilePath = basePath + File.separator + "Dockerfile";
		try {
			Utils.replaceFileContent(dockerFilePath, "#core_node_address#", getCoreNodeAddress());
			// replace smart_contract_id
			Utils.replaceFileContent(dockerFilePath, "#smart_contract_id", smartContractId);
			// replace core_node_address_and_port
			Utils.replaceFileContent(dockerFilePath, "#core_node_address_and_port",getCoreNodeAddress() + ":" + getCoreNodeAddressPort());
			// replace baseImage
			Utils.replaceFileContent(dockerFilePath, "#[base_image]", getBaseImage());
		} catch (IOException e) {
			throw new VMException(e.getMessage(), e);
		}

		// image name
		String imageName = getImageName();
		// build镜像
		String imageId = DockerUtil.buildImage(basePath + "/Dockerfile", imageName);
		log.info("image id :" + imageId);

		// container name
		String containerName = getContainerName();
		log.info("container name:" + containerName);
		// create container
		DockerUtil.createContainer(imageId, containerName);
	}

	@Override
	public void start() throws VMException {
		// container name
		String containerName = getContainerName();
		log.info("container id:" + containerName);
		// start container
		DockerUtil.startContainer(containerName);
		while (!SmartContractRunningUtil.checkSmartContractRunning(smartContractId)) {
			log.info("wait smart contract register[" + smartContractId + "]");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void stop() throws VMException {
		// container name
		String containerName = getContainerName();
		DockerUtil.stopContainer(containerName);
	}

	@Override
	public void destroy() throws VMException {
		// container name
		String containerName = getContainerName();
		DockerUtil.destroyContainer(containerName);
	}

	@Override
	public String getVMName() throws VMException {
		// container name
		String containerName = getContainerName();
		return containerName;
	}

	public String getSmartContractId() {
		return smartContractId;
	}

	public void setSmartContractId(String smartContractId) {
		this.smartContractId = smartContractId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
