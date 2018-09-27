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

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.protos.ProposalResponsePayloadVO;
import org.bcia.julongchain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.SpringContext;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.ProposalResponseUtils;
import org.bcia.julongchain.core.common.validation.MsgValidation;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.TxSimulationResults;
import org.bcia.julongchain.core.smartcontract.node.TransactionRunningUtil;
import org.bcia.julongchain.node.common.helper.SpecHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.node.*;
import org.springframework.stereotype.Component;

/**
 * 背书节点
 *
 * @author zhouhui
 * @date 2018/3/13
 * @company Dingxuan
 */
@Component
public class Endorser implements IEndorserServer {
    private static JulongChainLog log = JulongChainLogFactory.getLog(Endorser.class);

    private IEndorserSupport endorserSupport;

    private IPrivateDataDistributor distributor;

    public Endorser() {
    }

    public Endorser(IPrivateDataDistributor distributor) {
        this.distributor = distributor;
        endorserSupport = SpringContext.getInstance().getBean(EndorserSupport.class);
    }

    /**
     * 构造带错误消息的响应
     *
     * @param errorMsg
     * @return
     */
    private ProposalResponsePackage.ProposalResponse buildErrorResponse(String errorMsg) {
        return ProposalResponseUtils.buildErrorProposalResponse(Common.Status.INTERNAL_SERVER_ERROR, errorMsg);
    }

    @Override
    public ProposalResponsePackage.ProposalResponse processProposal(ProposalPackage.SignedProposal signedProposal)
            throws NodeException {
        Object[] objs = null;
        try {
            //1、预处理，主要是完成验证和检查
            objs = preProcess(signedProposal);
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
            return buildErrorResponse(e.getMessage());
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            return buildErrorResponse(e.getMessage());
        }

        ProposalPackage.Proposal proposal = (ProposalPackage.Proposal) objs[0];
        Common.GroupHeader groupHeader = (Common.GroupHeader) objs[1];
        ProposalPackage.SmartContractHeaderExtension extension = (ProposalPackage.SmartContractHeaderExtension) objs[2];

        SmartContractPackage.SmartContractID.Builder scIdBuilder = SmartContractPackage.SmartContractID.newBuilder
                (extension.getSmartContractId());
        String scName = scIdBuilder.getName();

        boolean isSysSC = endorserSupport.isSysSmartContract(scName);

        //2、开始模拟提案
        Object[] simulateObjs = simulateProposal(groupHeader.getGroupId(), scName, groupHeader.getTxId(),
                signedProposal, proposal, scIdBuilder);

        ProposalResponsePackage.Response response = (ProposalResponsePackage.Response) simulateObjs[0];
        byte[] txReadWriteSetBytes = (byte[]) simulateObjs[1];
        ISmartContractDefinition scDefinition = (ISmartContractDefinition) simulateObjs[2];
        SmartContractEventPackage.SmartContractEvent scEvent = (SmartContractEventPackage.SmartContractEvent)
                simulateObjs[3];


        if (response.getStatus() != Common.Status.SUCCESS_VALUE) {
            //TODO:是否要封装错误消息
            return buildErrorResponse("SimulateProposal fail");
        }

        //对所有的结果都背书可以确保处理消息的完整性，但为了性能折中，不需要Consenter节点参与的消息，不强求背书
        if (StringUtils.isBlank(scName) || CommConstant.CSSC.equals(scName) || CommConstant.QSSC.equals(scName)) {
            if (!response.getPayload().isEmpty()) {
                return ProposalResponseUtils.buildProposalResponse(response.getPayload(), response.getMessage());
            } else {
                return ProposalResponseUtils.buildProposalResponse(response);
            }
        } else {
            //3、背书提案
            ProposalResponsePackage.Response endorseResponse = endorseProposal(groupHeader.getGroupId(), groupHeader
                            .getTxId(), signedProposal, proposal, scIdBuilder, response, txReadWriteSetBytes,
                    scEvent, extension.getPayloadVisibility().toByteArray(), isSysSC, scDefinition);

            return ProposalResponseUtils.buildProposalResponse(endorseResponse.getStatus(), endorseResponse
                    .getPayload(), endorseResponse.getMessage());

//            SmartContractShim.SmartContractMessage smartContractMessage = TransactionRunningUtil.getTxMessage(scName, groupHeader.getTxId());
//            TransactionRunningUtil.clearMap(scName, groupHeader.getTxId());
//            ProposalResponsePackage.Response smartContractResponse = null;
//            try {
//                smartContractResponse = ProposalResponsePackage.Response.parseFrom(smartContractMessage.getPayload());
//            } catch (InvalidProtocolBufferException e) {
//                log.error(e.getMessage(), e);
//            }
//
//            return ProposalResponseUtils.buildProposalResponse(proposalResponse.getPayload() != null ?
//                    proposalResponse.getPayload() : smartContractResponse.getPayload(), smartContractResponse.getMessage());
        }
    }

    /**
     * 预处理签名提案：主要是校验操作
     *
     * @param signedProposal
     * @return
     * @throws NodeException
     * @throws ValidateException
     */
    private Object[] preProcess(ProposalPackage.SignedProposal signedProposal) throws NodeException, ValidateException {
        ValidateUtils.isNotNull(signedProposal, "SignedProposal can not be null");
        ValidateUtils.isNotNull(signedProposal.getProposalBytes(), "SignedProposal.proposal can not be null");
        ValidateUtils.isNotNull(signedProposal.getSignature(), "SignedProposal.signature can not be null");

        //校验Proposal字段
        ProposalPackage.Proposal proposal = null;
        try {
            proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            //转化不成功，说明是错误的Proposal字段
            throw new NodeException("Wrong proposal");
        }
        ValidateUtils.isNotNull(proposal, "Proposal can not be null");

        //校验Proposal头部
        ValidateUtils.isNotNull(proposal.getHeader(), "Proposal.header can not be null");
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
            throw new NodeException(e);
        }

        Common.GroupHeader groupHeader = (Common.GroupHeader) objs[0];
        Common.SignatureHeader signatureHeader = (Common.SignatureHeader) objs[1];
        ProposalPackage.SmartContractHeaderExtension extension = (ProposalPackage.SmartContractHeaderExtension) objs[2];

        //校验签名(验签)
        try {
            MsgValidation.checkSignature(signedProposal.getSignature().toByteArray(), signedProposal.getProposalBytes()
                    .toByteArray(), signatureHeader.getCreator().toByteArray(), groupHeader.getGroupId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }

        //校验交易ID
        ValidateUtils.isNotBlank(groupHeader.getTxId(), "TxId can not be empty");
        try {
            MsgValidation.checkProposalTxId(groupHeader.getTxId(), signatureHeader.getCreator().toByteArray(),
                    signatureHeader.getNonce().toByteArray());
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }

        //当包含智能合约ID时，校验智能合约ID,是否允许执行
        if (extension != null && extension.getSmartContractId() != null && endorserSupport
                .isSysSCAndNotInvokableExternal(extension.getSmartContractId().getName())) {
            throw new NodeException("Is SysCC and not invokable external");
        }

        if (StringUtils.isNotBlank(groupHeader.getGroupId())) {
            //校验重复交易
            if (endorserSupport.getTransactionById(groupHeader.getGroupId(), groupHeader.getTxId()) != null) {
                throw new NodeException("Duplicate transaction, creator:" + signatureHeader.getCreator());
            }

            //校验权限:仅应用智能合约校验
            if (extension != null && extension.getSmartContractId() != null && !endorserSupport.isSysSmartContract
                    (extension.getSmartContractId().getName())) {
                endorserSupport.checkACL(signedProposal, groupHeader, signatureHeader, extension);
            }
        }

        return new Object[]{proposal, groupHeader, extension};
    }

    public IEndorserSupport getEndorserSupport() {
        return endorserSupport;
    }

    public void setEndorserSupport(IEndorserSupport endorserSupport) {
        this.endorserSupport = endorserSupport;
    }

    /**
     * 模拟提案
     *
     * @param groupId
     * @param scName
     * @param txId
     * @param signedProposal
     * @param proposal
     * @param scIDBuilder
     * @return
     * @throws NodeException
     */
    private Object[] simulateProposal(String groupId, String scName, String txId, ProposalPackage.SignedProposal
            signedProposal, ProposalPackage.Proposal proposal, SmartContractPackage.SmartContractID.Builder scIDBuilder)
            throws NodeException {
        //获取SmartContractInvocationSpec
        SmartContractPackage.SmartContractInvocationSpec invocationSpec = getInvocationSpec(proposal);

        ITxSimulator txSimulator = endorserSupport.getTxSimulator(groupId, txId);

        String version = null;
        ISmartContractDefinition scDefinition = null;
        if (endorserSupport.isSysSmartContract(scName)) {
            //TODO:从配置文件中读取?
            version = CommConstant.METADATA_VERSION;
        } else {
            scDefinition = endorserSupport.getSmartContractDefinition(groupId, scName, txId, signedProposal,
                    proposal, txSimulator);
            version = scDefinition.getSmartContractVersion();

            //TODO：检查实例化策略
            endorserSupport.checkInstantiationPolicy(scName, version, scDefinition);
        }

        Object[] objs = callSmartContract(groupId, scName, version, txId, signedProposal, proposal, invocationSpec);
        ProposalResponsePackage.Response response = (ProposalResponsePackage.Response) objs[0];
        SmartContractEventPackage.SmartContractEvent scEvent = (SmartContractEventPackage.SmartContractEvent) objs[1];

        if (response.getStatus() >= Common.Status.BAD_REQUEST_VALUE) {
            throw new NodeException("Call smart contract fail");
        }

        byte[] publicSimulateBytes = new byte[0];
        if (groupId != null && txSimulator != null) {
            TxSimulationResults simulationResults = null;
            try {
                simulationResults = txSimulator.getTxSimulationResults();
            } catch (LedgerException e) {
                log.error(e.getMessage(), e);
                throw new NodeException("Get TxSimulation fail");
            }

            if (simulationResults.getPrivateReadWriteSet() != null) {
                if (scIDBuilder.getName().equals(CommConstant.LSSC)) {
                    log.error("Should not be lssc here");
                    throw new NodeException("Should not be lssc here");
                }

                distributor.distributePrivateData(groupId, txId, simulationResults.getPrivateReadWriteSet());
            }

            Rwset.TxReadWriteSet readWriteSet = simulationResults.getPublicReadWriteSet();
            if (readWriteSet != null) {
                publicSimulateBytes = readWriteSet.toByteArray();
            }
        }

        return new Object[]{response, publicSimulateBytes, scDefinition, scEvent};
    }

    /**
     * 从提案中提取智能合约调用规格
     *
     * @param proposal
     * @return
     * @throws NodeException
     */
    private SmartContractPackage.SmartContractInvocationSpec getInvocationSpec(ProposalPackage.Proposal proposal) throws
            NodeException {
        if (proposal != null) {
            //获取Payload字段
            ProposalPackage.SmartContractProposalPayload proposalPayload = null;
            try {
                proposalPayload = ProposalPackage
                        .SmartContractProposalPayload.parseFrom(proposal.getPayload());
                return SmartContractPackage.SmartContractInvocationSpec.parseFrom(proposalPayload.getInput());
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage(), e);
                throw new NodeException("Wrong proposal, wrong payload");
            }
        }

        return null;
    }

    /**
     * 调用essc背书提案
     *
     * @param signedProposal
     * @return
     */
    public ProposalResponsePackage.Response endorseProposal(
            String groupId, String txId, ProposalPackage.SignedProposal signedProposal, ProposalPackage.Proposal
            proposal, SmartContractPackage.SmartContractID.Builder scIdBuilder, ProposalResponsePackage
                    .Response response, byte[] simulateResults, SmartContractEventPackage.SmartContractEvent scEvent,
            byte[] visibility, boolean isSysSC, ISmartContractDefinition scDefinition) throws NodeException {
        log.info("Begin endorseProposal");

        //背书系统智能合约名称
        String essc = null;
        if (isSysSC) {
            essc = CommConstant.ESSC;
            //TODO:应该从某个配置文件里面读取
            scIdBuilder.setVersion(CommConstant.METADATA_VERSION);
        } else {
            essc = scDefinition.getEndorsement();
            scIdBuilder.setVersion(scDefinition.getSmartContractVersion());
        }

        // 参数列表:
        // args[0] - 暂未使用（预留作为函数名）
        // args[1] - 头部数据
        // args[2] - 提案负载数据
        // args[3] - 智能合约标识
        // args[4] - 智能合约执行结果
        // args[5] - 模拟结果数据
        // args[6] - 事件数据
        // args[7] - 负载的可见度数据
        byte[][] args = new byte[][]{new byte[0], proposal.getHeader().toByteArray(), proposal.getPayload()
                .toByteArray(), scIdBuilder.build().toByteArray(), response.toByteArray(), simulateResults, scEvent
                .toByteArray(), visibility};
        SmartContractPackage.SmartContractInvocationSpec invocationSpec = SpecHelper.buildInvocationSpec(essc, args);
        String version = CommConstant.METADATA_VERSION;
        //开始调用essc
        ProposalResponsePackage.Response esscResponse = (ProposalResponsePackage.Response) callSmartContract(groupId,
                essc, version, txId, signedProposal, proposal, invocationSpec)[0];
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
    public Object[] callSmartContract(String groupId, String scName, String scVersion, String txId, ProposalPackage
            .SignedProposal signedProposal, ProposalPackage.Proposal proposal, SmartContractPackage
                                              .SmartContractInvocationSpec spec) throws NodeException {
        log.info("Begin callSmartContract: " + scName);

        boolean isSysSmartContract = endorserSupport.isSysSmartContract(scName);

        Object[] objs = endorserSupport.execute(groupId, scName, scVersion, txId, isSysSmartContract, signedProposal,
                proposal, spec);
        ProposalResponsePackage.Response response = (ProposalResponsePackage.Response) objs[0];

        if (response.getStatus() >= Common.Status.BAD_REQUEST_VALUE) {
            //大于等于400意味着出错，一般为200 OK
            return objs;
        }

        if (CommConstant.LSSC.equalsIgnoreCase(scName)) {
            SmartContractPackage.SmartContractInput input = spec.getSmartContractSpec().getInput();
            //参数必须3个及以上，并且第3个参数不为空
            if (input != null && input.getArgsCount() >= 3 && input.getArgs(2) != null) {
                String action = input.getArgs(0).toStringUtf8();
                if (isDeployAction(action)) {
                    try {
                        SmartContractPackage.SmartContractDeploymentSpec deploymentSpec = SmartContractPackage
                                .SmartContractDeploymentSpec.parseFrom(input.getArgs(2));
                        SmartContractPackage.SmartContractID deployScId = deploymentSpec.getSmartContractSpec()
                                .getSmartContractId();
                        String deployScName = deployScId.getName();
                        String deployScVersion = deployScId.getVersion();
                        if (!endorserSupport.isSysSmartContract(deployScName)) {
                            endorserSupport.execute(groupId, deployScName, deployScVersion, txId, false,
                                    signedProposal, proposal, deploymentSpec);
                        } else {
                            throw new NodeException("Should not be system smart contract");
                        }
                    } catch (InvalidProtocolBufferException e) {
                        log.error(e.getMessage(), e);
                        throw new NodeException(e);
                    }
                }
            }
        }

        return objs;
    }

    /**
     * 是否是一个部署动作
     *
     * @param action
     * @return
     */
    private boolean isDeployAction(String action) {
        return ArrayUtils.contains(new String[]{CommConstant.DEPLOY, CommConstant.UPGRADE}, action);
    }
}