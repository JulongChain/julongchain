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
package org.bcia.javachain.core.smartcontract;

import static org.bcia.javachain.protos.node.Smartcontract.SmartContractDeploymentSpec;
import static org.bcia.javachain.protos.node.Smartcontract.SmartContractInput;
import static org.bcia.javachain.protos.node.Smartcontract.SmartContractInvocationSpec;
import static org.bcia.javachain.protos.node.Smartcontract.SmartContractSpec;
import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage;
import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage.Type;
import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage.newBuilder;

import java.time.Duration;
import javax.naming.Context;

import org.bcia.javachain.common.exception.SmartContractException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.javachain.core.container.api.IBuildSpecFactory;
import org.bcia.javachain.core.container.scintf.ISmartContractStream;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.javachain.core.smartcontract.client.SmartContractSupportClient;
import org.bcia.javachain.core.smartcontract.node.SmartContractRunningUtil;
import org.bcia.javachain.core.smartcontract.node.SmartContractSupportService;
import org.bcia.javachain.core.smartcontract.shim.SmartContractProvider;

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

        // TODO:add by zhouhui for test,返回一个空对象，实际处理待万良兵补充

        String smartContractId = scContext.getName();
        boolean scRunning = SmartContractRunningUtil.checkSmartContractRunning(smartContractId);
        if (!scRunning) {
            SmartContractSupportClient.launch(smartContractId);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (spec instanceof SmartContractInvocationSpec) {
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

        SmartContractSupportService.invoke(smartContractId, scMessage);

        return newBuilder().setType(Type.COMPLETED).build();
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
