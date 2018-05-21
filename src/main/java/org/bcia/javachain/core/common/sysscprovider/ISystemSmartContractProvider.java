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
package org.bcia.javachain.core.common.sysscprovider;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.groupconfig.config.IApplicationConfig;
import org.bcia.javachain.common.policies.IManager;
import org.bcia.javachain.core.ledger.IQueryExecutor;

/**
 * SystemChaincodeProvider provides an abstraction layer that is
 * used for different packages to interact with code in the
 * system chaincode package without importing it; more methods
 * should be added below if necessary
 *
 * @author sunianle, sunzongyu
 * @date 3/13/18
 * @company Dingxuan
 */
public interface ISystemSmartContractProvider {
    /**
     * IsSysCC returns true if the supplied chaincode is a system chaincode
     * @param name
     * @return
     */
    boolean isSysSmartContract(String name);

    /**
     * IsSysCCAndNotInvokableCC2CC returns true if the supplied chaincode
     * is a system chaincode and is not invokable through a cc2cc invocation
     */
    boolean isSysSCAndNotInvokableSC2SC(String name);

    /**
     * IsSysCCAndNotInvokable returns true if the supplied chaincode
     * is a system chaincode and is not invokable through a proposal
     * @param name
     * @return
     */
    boolean isSysSCAndNotInvkeableExternal(String name);

    /**
     * GetQueryExecutorForLedger returns a query executor for the
     * ledger of the supplied channel.
     * That's useful for system chaincodes that require unfettered
     * access to the ledger
     * @param groupID
     * @return
     * @throws JavaChainException
     */
    IQueryExecutor getQueryExecutorForLedger(String groupID) throws JavaChainException;

    /**
     * GetApplicationConfig returns the configtxapplication.SharedConfig for the channel
     * and whether the Application config exists
     * @param groupId
     * @return
     */
    IApplicationConfig getApplicationConfig(String groupId);

    /**
     * Returns the policy manager associated to the passed channel
     * and whether the policy manager exists
     * @param groupID
     * @return
     */
    IManager policyManager(String groupID);
}
