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
package org.bcia.julongchain.core.endorser;

import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.kvledger.history.IHistoryQueryExecutor;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 背书能力支持接口
 *
 * @author zhouhui
 * @date 2018/3/13
 * @company Dingxuan
 */
public interface IEndorserSupport {
    /**
     * 是系统智能合约并且不允许外部调用
     *
     * @param scName 智能合约名称
     * @return
     */
    boolean isSysSCAndNotInvokableExternal(String scName);

    /**
     * 获得某交易的模拟器
     *
     * @param ledgerName 账本名称，通常与groupId相同
     * @param txId       交易ID
     * @return
     */
    ITxSimulator getTxSimulator(String ledgerName, String txId) throws NodeException;

    /**
     * 获得某账本的历史库查询执行器
     *
     * @param ledgerName 账本名称
     * @return
     */
    IHistoryQueryExecutor getHistoryQueryExecutor(String ledgerName) throws NodeException;

    /**
     * 通过某交易ID获取交易详情
     *
     * @param groupId 群组ID
     * @param txId    交易ID
     * @return
     */
    TransactionPackage.ProcessedTransaction getTransactionById(String groupId, String txId) throws NodeException;

    /**
     * 是否是系统智能合约
     *
     * @param scName
     * @return
     */
    boolean isSysSmartContract(String scName);

    /**
     * 执行智能合约
     *
     * @param groupId        群组ID
     * @param scName         智能合约名称
     * @param scVersion      智能合约版本
     * @param txId           交易ID
     * @param sysSC          是否系统智能合约
     * @param signedProposal 带签名提案
     * @param proposal       原始提案
     * @param spec           智能合约调用规格
     * @return
     */
    Object[] execute(String groupId, String scName, String scVersion, String txId, boolean
            sysSC, ProposalPackage.SignedProposal signedProposal, ProposalPackage.Proposal proposal, SmartContractPackage
                             .SmartContractInvocationSpec spec) throws NodeException;

    /**
     * 执行智能合约
     *
     * @param groupId        群组ID
     * @param scName         智能合约名称
     * @param scVersion      智能合约版本
     * @param txId           交易ID
     * @param sysSC          是否系统智能合约
     * @param signedProposal 带签名提案
     * @param proposal       原始提案
     * @param spec           智能合约部署规格
     * @return
     */
    Object[] execute(String groupId, String scName, String scVersion, String txId, boolean
            sysSC, ProposalPackage.SignedProposal signedProposal, ProposalPackage.Proposal proposal, SmartContractPackage
                             .SmartContractDeploymentSpec spec) throws NodeException;

    /**
     * 获取智能合约定义
     *
     * @param groupId        群组ID
     * @param scName         智能合约名称
     * @param txId           交易ID
     * @param signedProposal 带签名提案
     * @param proposal       原始提案
     * @param txSimulator    交易模拟器
     * @return
     */
    ISmartContractDefinition getSmartContractDefinition(String groupId, String scName, String txId, ProposalPackage
            .SignedProposal signedProposal, ProposalPackage.Proposal proposal, ITxSimulator txSimulator) throws
            NodeException;

    /**
     * 检查访问控制清单
     *
     * @param signedProposal  带签名提案
     * @param groupHeader     群组头部
     * @param signatureHeader 签名头部
     * @param extension       智能合约头部扩展
     */
    void checkACL(ProposalPackage.SignedProposal signedProposal, Common.GroupHeader groupHeader, Common.SignatureHeader
            signatureHeader, ProposalPackage.SmartContractHeaderExtension extension);

    /**
     * 是否是Java智能合约(目前未使用，因为默认支持Java)
     *
     * @param buffer
     * @return
     */
    boolean isJavaSC(byte[] buffer);

    /**
     * 检查实例化策略
     *
     * @param scName       智能合约名称
     * @param scVersion    智能合约版本
     * @param scDefinition 智能合约定义
     */
    void checkInstantiationPolicy(String scName, String scVersion, ISmartContractDefinition scDefinition);
}
