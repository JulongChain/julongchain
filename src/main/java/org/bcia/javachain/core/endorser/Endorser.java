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

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.proto.ProposalResponseUtils;
import org.bcia.javachain.core.common.validation.MsgValidation;
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.TxSimulationResults;
import org.bcia.javachain.node.common.helper.SpecHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.SmartContractEventPackage;
import org.bcia.javachain.protos.node.Smartcontract;

/**
 * 背书节点
 *
 * @author
 * @date 2018/3/13
 * @company Dingxuan
 */
public class Endorser implements IEndorserServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Endorser.class);

    //TODO:Spring
    private IEndorserSupport endorserSupport = new EndorserSupport();

    private IPrivateDataDistributor distributor;

    public Endorser(IPrivateDataDistributor distributor) {
        this.distributor = distributor;
    }

    /**
     * 构造带错误消息的响应
     *
     * @param errorMsg
     * @return
     */
    private ProposalResponsePackage.ProposalResponse buildErrorResponse(String errorMsg) {
        ProposalResponsePackage.ProposalResponse.Builder proposalResponseBuilder = ProposalResponsePackage
                .ProposalResponse.newBuilder();
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setStatus(Common.Status.INTERNAL_SERVER_ERROR_VALUE);
        responseBuilder.setMessage(errorMsg);
        proposalResponseBuilder.setResponse(responseBuilder.build());
        return proposalResponseBuilder.build();
    }

    @Override
    public ProposalResponsePackage.ProposalResponse processProposal(ProposalPackage.SignedProposal signedProposal) {
        //TODO：获取客户端的IP和端口，暂无找到有效方法
        Object[] objs = null;
        try {
            //预处理，主要是完成检查
            objs = preProcess(signedProposal);
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
            return buildErrorResponse(e.getMessage());
        }

        ProposalPackage.Proposal proposal = (ProposalPackage.Proposal) objs[0];
        Common.GroupHeader groupHeader = (Common.GroupHeader) objs[1];
        Common.SignatureHeader signatureHeader = (Common.SignatureHeader) objs[2];
        ProposalPackage.SmartContractHeaderExtension extension = (ProposalPackage.SmartContractHeaderExtension) objs[3];

        ITxSimulator txSimulator = null;
        IHistoryQueryExecutor historyQueryExecutor = null;

        if (StringUtils.isNotBlank(groupHeader.getGroupId())) {
            txSimulator = endorserSupport.getTxSimulator(groupHeader.getGroupId(), groupHeader.getTxId());
            historyQueryExecutor = endorserSupport.getHistoryQueryExecutor(groupHeader.getGroupId());
        }

        String scName = extension.getSmartContractId().getName();

        ProposalResponsePackage.Response response = simulateProposal(groupHeader.getGroupId(), scName, groupHeader
                        .getTxId(), signedProposal, proposal,
                Smartcontract.SmartContractID.newBuilder(extension.getSmartContractId()));
        if (response.getStatus() != Common.Status.SUCCESS_VALUE) {
            //TODO:是否要封装错误消息
            return buildErrorResponse("simulateProposal fail");
        }

        //无合约提案不需要背书，例如cssc
        if (StringUtils.isBlank(scName)) {
            return ProposalResponseUtils.buildProposalResponse(response.getPayload());
        } else {
//            byte[] bytes = ;
//            endorseProposal(groupHeader.getGroupId(), groupHeader.getTxId(), signedProposal, proposal, scIdBuilder,
//                    response, bytes, )
        }


        return null;
    }

    /**
     * 预处理签名提案：主要是校验操作
     *
     * @param signedProposal
     * @return
     * @throws NodeException
     */
    private Object[] preProcess(ProposalPackage.SignedProposal signedProposal) throws NodeException {
        if (signedProposal == null) {
            throw new NodeException("Missing signed proposal");
        }

        if (signedProposal.getProposalBytes() == null) {
            throw new NodeException("Missing proposal");
        }

        //校验Proposal字段
        ProposalPackage.Proposal proposal = null;
        try {
            proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            //转化不成功，说明是错误的Proposal字段
            throw new NodeException("Wrong proposal");
        }

        //校验Proposal头部
        Common.Header header = null;
        try {
            header = Common.Header.parseFrom(proposal.getHeader());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Wrong proposal header");
        }

        Object[] objs = null;
        try {
            objs = MsgValidation.validateCommonHeader(header);
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e.getMessage());
        }

        Common.GroupHeader groupHeader = (Common.GroupHeader) objs[0];
        Common.SignatureHeader signatureHeader = (Common.SignatureHeader) objs[1];
        ProposalPackage.SmartContractHeaderExtension extension = (ProposalPackage.SmartContractHeaderExtension) objs[2];

        //校验签名
        try {
            MsgValidation.checkSignature(signedProposal.getSignature().toByteArray(), signedProposal.getProposalBytes()
                    .toByteArray(), signatureHeader.getCreator().toByteArray(), groupHeader.getGroupId());
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e.getMessage());
        }

        //校验交易ID
        if (StringUtils.isBlank(groupHeader.getTxId())) {
            throw new NodeException("Missing proposal groupHeader txId");
        }
        try {
            MsgValidation.checkProposalTxID(groupHeader.getTxId(), signatureHeader.getCreator().toByteArray(),
                    signatureHeader.getNonce().toByteArray());
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e.getMessage());
        }

        //校验智能合约ID
        //TODO:似乎有逻辑漏洞，如何确保extension及里面的SmartContractId不会为空，虽然前面有所校验，但仅针对其中两个消息
        if (endorserSupport.isSysSCAndNotInvokableExternal(extension.getSmartContractId().getName())) {
            throw new NodeException("isSysCCAndNotInvokableExternal");
        }

        if (StringUtils.isNotBlank(groupHeader.getGroupId())) {
            //校验重复交易
            if (endorserSupport.getTransactionById(groupHeader.getGroupId(), groupHeader.getTxId()) != null) {
                throw new NodeException("duplicate transaction, creator:" + signatureHeader.getCreator());
            }

            //校验权限:仅应用智能合约校验
            if (!endorserSupport.isSysSmartContract(extension.getSmartContractId().getName())) {
                endorserSupport.checkACL(signedProposal, groupHeader, signatureHeader, extension);
            }
        }

        return new Object[]{proposal, groupHeader, signatureHeader, extension};
    }

    public IEndorserSupport getEndorserSupport() {
        return endorserSupport;
    }

    public void setEndorserSupport(IEndorserSupport endorserSupport) {
        this.endorserSupport = endorserSupport;
    }


    private ProposalResponsePackage.Response simulateProposal(String groupId, String scName, String txId,
                                                              ProposalPackage.SignedProposal signedProposal,
                                                              ProposalPackage.Proposal proposal, Smartcontract
                                                                      .SmartContractID.Builder scIDBuilder) {
        //获取Payload字段
        ProposalPackage.SmartContractProposalPayload proposalPayload = null;
        try {
            proposalPayload = ProposalPackage.SmartContractProposalPayload.parseFrom(proposal.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        //获取SmartContractInvocationSpec
        Smartcontract.SmartContractInvocationSpec invocationSpec = null;
        try {
            invocationSpec = Smartcontract.SmartContractInvocationSpec.parseFrom(proposalPayload.getInput());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        ITxSimulator txSimulator = endorserSupport.getTxSimulator(groupId, txId);

        String version = null;
        if (endorserSupport.isSysSmartContract(scIDBuilder.getName())) {
            version = CommConstant.METADATA_VERSION;
        } else {
            ISmartContractDefinition scDefinition = endorserSupport.getSmartContractDefinition(groupId, scName, txId,
                    signedProposal, proposal, txSimulator);
            version = scDefinition.smartContractVersion();

            //TODO：检查实例化策略
            endorserSupport.checkInstantiationPolicy(scIDBuilder.getName(), version, scDefinition);
        }

        ProposalResponsePackage.Response response = callSmartContract(groupId, scName, version, txId, signedProposal,
                proposal, invocationSpec);
        if (response.getStatus() >= Common.Status.BAD_REQUEST_VALUE) {
            return null;
        }

        TxSimulationResults simulationResults = null;
        try {
            simulationResults = txSimulator.getTxSimulationResults();
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        if (simulationResults.getPrivateReadWriteSet() != null) {
            if (scIDBuilder.getName().equals(CommConstant.LSSC)) {
                log.error("should not be lssc here");
                return null;
            }

            distributor.distributePrivateData(groupId, txId, simulationResults.getPrivateReadWriteSet());
        }


        return response;
    }

    /**
     * 调用essc背书提案
     *
     * @param signedProposal
     * @return
     */
    private ProposalResponsePackage.Response endorseProposal(String groupId, String txId, ProposalPackage
            .SignedProposal signedProposal, ProposalPackage.Proposal proposal, Smartcontract.SmartContractID.Builder
                                                                     smartContractIDBuilder, ProposalResponsePackage
                                                                     .Response response, byte[] simulateResults,
                                                             SmartContractEventPackage.SmartContractEvent event,
                                                             byte[] visibility, ISmartContractDefinition
                                                                     smartContractDefinition) {
        log.info("begin endorseProposal");

        boolean useSysSmartContract = false;
        if (smartContractDefinition == null) {
            useSysSmartContract = true;
        }

        //背书系统智能合约名称
        String essc = null;
        if (useSysSmartContract) {
            essc = CommConstant.ESSC;
            //TODO:应该从某个配置文件里面读取
            smartContractIDBuilder.setVersion(CommConstant.METADATA_VERSION);
        } else {
            essc = smartContractDefinition.endorsement();
            smartContractIDBuilder.setVersion(smartContractDefinition.smartContractVersion());
        }

        // arguments:
        // args[0] - function name (not used now)
        // args[1] - serialized Header object
        // args[2] - serialized ProposalPayload object
        // args[3] - SmartContractID of executing SmartContract
        // args[4] - result of executing SmartContract
        // args[5] - binary blob of simulation results
        // args[6] - serialized events
        // args[7] - payloadVisibility
        byte[][] args = new byte[][]{new byte[0], proposal.getHeader().toByteArray(), proposal.getPayload()
                .toByteArray(), smartContractIDBuilder.build().toByteArray(), response.toByteArray(),
                simulateResults, event.toByteArray(), visibility};
        Smartcontract.SmartContractInvocationSpec invocationSpec = SpecHelper.buildInvocationSpec(essc, args);
        String version = CommConstant.METADATA_VERSION;
        //开始调用essc
        ProposalResponsePackage.Response esscResponse = callSmartContract(groupId, essc, version, txId,
                signedProposal, proposal, invocationSpec);
        if (esscResponse.getStatus() >= Common.Status.BAD_REQUEST_VALUE) {
            //大于等于400意味着出错，一般为200 OK
            return esscResponse;
        }

        //TODO:是否要对负载进行处理
        return esscResponse;
    }

    /**
     * 调用智能合约
     *
     * @param groupId
     * @param scName
     * @param scVersion
     * @param txId
     * @param signedProposal
     * @param proposal
     * @param spec
     * @return
     */
    private ProposalResponsePackage.Response callSmartContract(String groupId, String scName, String
            scVersion, String txId, ProposalPackage.SignedProposal signedProposal, ProposalPackage.Proposal proposal,
                                                               Smartcontract.SmartContractInvocationSpec spec) {
        log.info("begin callSmartContract");

        boolean isSysSmartContract = endorserSupport.isSysSmartContract(scName);

        ProposalResponsePackage.Response response = endorserSupport.execute(groupId, scName,
                scVersion, txId, isSysSmartContract, signedProposal, proposal, spec);
        if (response.getStatus() >= Common.Status.BAD_REQUEST_VALUE) {
            //大于等于400意味着出错，一般为200 OK
            return response;
        }

        if (CommConstant.LSSC.equalsIgnoreCase(scName)) {
            Smartcontract.SmartContractInput input = spec.getSmartContractSpec().getInput();
            //参数必须3个及以上，并且第3个参数不为空
            if (input != null && input.getArgsCount() >= 3 && input.getArgs(2) != null) {
                String action = input.getArgs(0).toString();
                if (isDeployAction(action)) {
                    try {
                        Smartcontract.SmartContractDeploymentSpec deploymentSpec = Smartcontract.SmartContractDeploymentSpec
                                .parseFrom(input.getArgs(2));
                        if (!endorserSupport.isSysSmartContract(deploymentSpec.getSmartContractSpec()
                                .getSmartContractId().getName())) {
                            endorserSupport.execute(groupId, scName, scVersion, txId, isSysSmartContract,
                                    signedProposal, proposal, deploymentSpec);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        log.error(e.getMessage(), e);
                        return ProposalResponseUtils.buildErrorResponse(Common.Status.INTERNAL_SERVER_ERROR, e
                                .getMessage());
                    }
                }
            }
        }

        return response;
    }

    /**
     * 是否是一个部署动作
     *
     * @param action
     * @return
     */
    private boolean isDeployAction(String action) {
        return ArrayUtils.contains(new String[]{"deploy", "upgrade"}, action);
    }
}
