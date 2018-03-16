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
package org.bcia.javachain.core.smartcontract;

import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;

import javax.naming.Context;
import java.time.Duration;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/14
 * @company Dingxuan
 */
public class SmartContractSupport {

    /** DevModeUserRunsChaincode property allows user to run chaincode in development environment
     *
     */
    public static final String DevModeUserRunsChaincode = "dev";
    public static final Integer chaincodeStartupTimeoutDefault = 5000;
    public static final String peerAddressDefault = "0.0.0.0:7051";

    /** TXSimulatorKey is used to attach ledger simulation context
     *
     */
    public static String TXSimulatorKey = "txsimulatorkey";

    /** HistoryQueryExecutorKey is used to attach ledger history query executor context
     *
     */
    public static String HistoryQueryExecutorKey = "historyqueryexecutorkey";

    /** use this for ledger access and make sure TXSimulator is being used
     *
     * @param Contextcontext
     * @return
     */
    public ITxSimulator getTxSimulator(Context Contextcontext) {
        return null;
    }

    /** use this for ledger access and make sure HistoryQueryExecutor is being used
     *
     * @param context
     * @return
     */
    public IHistoryQueryExecutor getHistoryQueryExecutor(Context context) {
        return null;
    }

    /** GetChain returns the chaincode framework support object
     *
     * @return
     */
    public SmartContractSupport GetChain()  {
        return null;
    }

    /**
     *
     * @param chaincode
     * @return
     */
    public Boolean preLaunchSetup(String chaincode) {
        return Boolean.FALSE;
    }

    /** call this under lock
     *
     * @param chaincode
     * @return
     */
    public Boolean chaincodeHasBeenLaunched(String chaincode) {
        return Boolean.FALSE;
    }

    /** call this under lock
     *
     * @param chaincode
     * @return
     */
    public Boolean launchStarted(String chaincode) {
        return Boolean.FALSE;
    }

    /** NewChaincodeSupport creates a new ChaincodeSupport instance
     *
     * @param func
     * @return
     */
    public SmartContractSupport newChaincodeSupport(Boolean userrunsCC, Duration ccstartuptimeout) {
        return null;
    }

    /** getLogLevelFromViper gets the chaincode container log levels from viper
     *
     * @param module
     * @return
     */
    public String getLogLevelFromViper(String module) {
        return null;
    }

}
