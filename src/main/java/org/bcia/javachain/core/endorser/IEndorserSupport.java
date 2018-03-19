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
package org.bcia.javachain.core.endorser;

import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 背书能力支持接口
 *
 * @author zhouhui
 * @date 2018/3/13
 * @company Dingxuan
 */
public interface IEndorserSupport {
    boolean isSysCCAndNotInvokableExternal(String name);

    ITxSimulator getTxSimulator(String ledgerName, String txId);

    IHistoryQueryExecutor getHistoryQueryExecutor(String ledgerName);

    TransactionPackage.ProcessedTransaction getTransactionByID(String groupId, String txId);

    boolean isSysCC(String name);

    //TODO:两个参数？
    ProposalResponsePackage.Response execute(String cid, String name, String version, String txid, boolean syscc,
                                             ProposalPackage.SignedProposal signedProp, ProposalPackage.Proposal
                                                     prop, Object spec);

    void checkACL(ProposalPackage.SignedProposal signedProposal, Common.GroupHeader groupHeader, Common.SignatureHeader
            signatureHeader, ProposalPackage.SmartContractHeaderExtension extension);

    boolean isJavaCC(byte[] buffer);



    // GetChaincodeDefinition returns resourcesconfig.ChaincodeDefinition for the chaincode with the supplied name
//    GetChaincodeDefinition(ctx context.Context, chainID String, txid String, signedProp *pb.SignedProposal, prop *pb.Proposal, chaincodeID String, txsim ledger.TxSimulator) (resourcesconfig.ChaincodeDefinition,error)


    // CheckInstantiationPolicy returns an error if the instantiation in the supplied
    // ChaincodeDefinition differs from the instantiation policy stored on the ledger
//    CheckInstantiationPolicy(name, version String, cd resourcesconfig.ChaincodeDefinition) error

    // GetApplicationConfig returns the configtxapplication.SharedConfig for the channel
    // and whether the Application config exists
//    GetApplicationConfig(cid String) (channelconfig.Application,bool)


//    privateDataDistributor(channel String, txID String, privateData *rwset.TxPvtReadWriteSet)
}
