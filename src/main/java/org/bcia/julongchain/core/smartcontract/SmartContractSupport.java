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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.Utils;
import org.bcia.julongchain.core.common.smartcontractprovider.ISmartContractPackage;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractProvider;
import org.bcia.julongchain.core.container.DockerUtil;
import org.bcia.julongchain.core.container.api.IBuildSpecFactory;
import org.bcia.julongchain.core.container.scintf.ISmartContractStream;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.core.node.NodeConfig;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.core.smartcontract.client.SmartContractSupportClient;
import org.bcia.julongchain.core.smartcontract.node.SmartContractRunningUtil;
import org.bcia.julongchain.core.smartcontract.node.SmartContractSupportService;

import javax.naming.Context;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.bcia.julongchain.protos.node.SmartContractPackage.*;
import static org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage;
import static org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage.Type;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/14
 * @company Dingxuan
 */
public class SmartContractSupport {

    private static JavaChainLog log = JavaChainLogFactory.getLog(SmartContractSupport.class);

    private RunningSmartContract runningSmartContract;
    private String nodeAddress;
    private Duration scStartupTimeout;
    private String nodeNetworkID;
    private String nodeID;
    private String nodeTLSCertFile;
    private String nodeTLSSvrHostOrd;
    private Duration keepalive;
    private String smartContractLogLevel;
    private String shimLogLevel;
    private String logFormat;
    private Duration executetimeout;
    private Boolean userRunsSC;
    private Boolean nodeTLS;

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

    public String getNodeTLSCertFile() {
        return nodeTLSCertFile;
    }

    public void setNodeTLSCertFile(String nodeTLSCertFile) {
        this.nodeTLSCertFile = nodeTLSCertFile;
    }

    public String getNodeTLSSvrHostOrd() {
        return nodeTLSSvrHostOrd;
    }

    public void setNodeTLSSvrHostOrd(String nodeTLSSvrHostOrd) {
        this.nodeTLSSvrHostOrd = nodeTLSSvrHostOrd;
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

    public Boolean getUserRunsSC() {
        return userRunsSC;
    }

    public void setUserRunsSC(Boolean userRunsSC) {
        this.userRunsSC = userRunsSC;
    }

    public Boolean getNodeTLS() {
        return nodeTLS;
    }

    public void setNodeTLS(Boolean nodeTLS) {
        this.nodeTLS = nodeTLS;
    }

    /** DevModeUserRunsChaincode property allows user to run chaincode in development environment */
    public static final String DevModeUserRunsChaincode = "dev";

    public static final Integer chaincodeStartupTimeoutDefault = 5000;
    public static final String peerAddressDefault = "0.0.0.0:7051";

    /** TXSimulatorKey is used to attach ledger simulation context */
    public static String TXSimulatorKey = "txsimulatorkey";

    /** HistoryQueryExecutorKey is used to attach ledger history query executor context */
    public static String HistoryQueryExecutorKey = "historyqueryexecutorkey";

    /**
     * use this for ledger access and make sure TXSimulator is being used
     *
     * @param Contextcontext
     * @return
     */
    public ITxSimulator getTxSimulator(Context Contextcontext) {
        return null;
    }

    /**
     * use this for ledger access and make sure IHistoryQueryExecutor is being used
     *
     * @param context
     * @return
     */
    public IHistoryQueryExecutor getHistoryQueryExecutor(Context context) {
        return null;
    }

    /**
     * GetChain returns the chaincode framework support object
     *
     * @return
     */
    public SmartContractSupport GetChain() {
        return null;
    }

    /**
     * @param chaincode
     * @return
     */
    public Boolean preLaunchSetup(String chaincode) {
        return Boolean.FALSE;
    }

    /**
     * call this under lock
     *
     * @param chaincode
     * @return
     */
    public Boolean chaincodeHasBeenLaunched(String chaincode) {
        return Boolean.FALSE;
    }

    /**
     * call this under lock
     *
     * @param chaincode
     * @return
     */
    public Boolean launchStarted(String chaincode) {
        return Boolean.FALSE;
    }

    /**
     * NewChaincodeSupport creates a new ChaincodeSupport instance
     *
     * @param
     * @return
     */
    public SmartContractSupport newChaincodeSupport(Boolean userrunsCC, Duration ccstartuptimeout) {
        return null;
    }

    /**
     * getLogLevelFromViper gets the chaincode container log levels from viper
     *
     * @param module
     * @return
     */
    public String getLogLevelFromViper(String module) {
        return null;
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

    public void registerHandler(Handler handler) {
    }

    public void deregisterHandler(Handler handler) {
    }

    public void sendReady(
            Context context, SmartContractContext smartContractContext, Duration timeout) {
    }

    public String[][] getArgsAndEnv(
            SmartContractContext smartContractContext, SmartContractSpec.Type type) {
        return null;
    }

    public void launchAndWaitForRegister(
            Context ctxt,
            SmartContractProvider smartContractProvider,
            SmartContractDeploymentSpec smartContractDeploymentSpec,
            SmartContractSpec.Type smartContractLang,
            IBuildSpecFactory builder) {
    }

    public void stop(
            Context context,
            SmartContractContext smartContractContext,
            SmartContractDeploymentSpec smartContractDeploymentSpec) {
    }

    public String getVMType(SmartContractDeploymentSpec smartContractDeploymentSpec) {
        return null;
    }

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

    public Boolean isDevMode() {
        return Boolean.FALSE;
    }
}
