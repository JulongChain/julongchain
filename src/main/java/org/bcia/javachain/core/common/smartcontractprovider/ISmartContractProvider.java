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
package org.bcia.javachain.core.common.smartcontractprovider;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.SmartContractException;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.LedgerContext;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.SmartContractEventPackage;
import org.bcia.javachain.protos.node.Smartcontract;

import java.util.List;

/**
 * ISmartContractProvider provides an abstraction layer that is
 *used for different packages to interact with code in the
 * chaincode package without importing it; more methods
 * should be added below if necessary
 *
 * @author wanliangbing,sunianle
 * @date 2018/3/22
 * @company Dingxuan
 */
public interface ISmartContractProvider {
    /**
     * GetContext returns a ledger context and a tx simulator; it's the
     *caller's responsability to release the simulator by calling its
     * done method once it is no longer useful
     * @param ledger
     * @param txID
     * @return
     * @throws SmartContractException
     */
    LedgerContext getContext(INodeLedger ledger, String txID)throws SmartContractException;
    /**
     * GetSCContext returns an opaque smartcontract context
     */
    SmartContractContext getSCContext(String groupID,
                                      String name,
                                      String txID,
                                      boolean bSysSC,
                                      ProposalPackage.SignedProposal signedProposal,
                                      ProposalPackage.Proposal prop);

    /**
     * executeSmartContract executes the chaincode given context and args
     * @param ctxt
     * @param scContext
     * @param args
     * @return
     * @throws SmartContractException
     */
    SmartContractExecuteResult executeSmartContract(LedgerContext ctxt,
                                                    SmartContractContext scContext,
                                                    List<ByteString> args)throws SmartContractException;

    /**
     * SmartContractExecuteResult executes the smartcontract given context and spec (invocation or deploy)
     * @param ctxt
     * @param scContext
     * @param spec
     * @return
     * @throws SmartContractException
     */
    SmartContractExecuteResult execute(LedgerContext ctxt,
            SmartContractContext scContext,Object spec)throws SmartContractException;

    /**
     * executeWithErrorFilter executes the chaincode given context and spec and returns payload
     * @param ctxt
     * @param scContext
     * @param spec
     * @return
     * @throws SmartContractException
     */
    SmartContractExecuteWithFilterResult executeWithErrorFilter(LedgerContext ctxt,
                           SmartContractContext scContext,Object spec)throws SmartContractException;

    void stop(LedgerContext ctxt,
         SmartContractContext scContext,
         Smartcontract.SmartContractDeploymentSpec spec)throws SmartContractException;



    class SmartContractExecuteResult{
        ProposalResponsePackage.Response response;
        SmartContractEventPackage.SmartContractEvent event;
    }

    class SmartContractExecuteWithFilterResult{
        byte[] data;
        SmartContractEventPackage.SmartContractEvent event;
    }
}
