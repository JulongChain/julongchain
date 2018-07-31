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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
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
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractEventPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 背书节点
 *
 * @author zhouhui
 * @date 2018/3/13
 * @company Dingxuan
 */
@Component
public class Endorser implements IEndorserServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Endorser.class);

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
        ProposalResponsePackage.ProposalResponse.Builder proposalResponseBuilder = ProposalResponsePackage
                .ProposalResponse.newBuilder();
        ProposalResponsePackage.Response.Builder responseBuilder = ProposalResponsePackage.Response.newBuilder();
        responseBuilder.setStatus(Common.Status.INTERNAL_SERVER_ERROR_VALUE);
        responseBuilder.setMessage(errorMsg);
        proposalResponseBuilder.setResponse(responseBuilder.build());
        return proposalResponseBuilder.build();
    }

    @Override
    public ProposalResponsePackage.ProposalResponse processProposal(ProposalPackage.SignedProposal signedProposal)
            throws NodeException {
        //TODO：获取客户端的IP和端口，暂无找到有效方法
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

        SmartContractPackage.SmartContractID.Builder scIdBuilder = SmartContractPackage.SmartContractID.newBuilder(extension
                .getSmartContractId());
        String scName = scIdBuilder.getName();

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
            return buildErrorResponse("simulateProposal fail");
        }

        //无合约提案不需要背书，例如cssc
//        simulateResults = new byte[]{0, 1, 2};//TODO:for test 使得测试通过
//        if (txReadWriteSetBytes == null || txReadWriteSetBytes.length <= 0) {
//            txReadWriteSetBytes = new byte[]{0, 1, 2};
//        }

        if (StringUtils.isBlank(scName) || CommConstant.CSSC.equals(scName)) {
            if (!response.getPayload().isEmpty()) {
                return ProposalResponseUtils.buildProposalResponse(response.getPayload());
            } else {
                return ProposalResponseUtils.buildProposalResponse(response);
            }
        } else {
            //3、背书提案
            ProposalResponsePackage.Response endorseResponse = endorseProposal(groupHeader.getGroupId(), groupHeader
                            .getTxId(), signedProposal, proposal, scIdBuilder,
                    response, txReadWriteSetBytes, scEvent, extension.getPayloadVisibility().toByteArray(), scDefinition);


            ProposalResponsePackage.ProposalResponse proposalResponse = null;

            ProposalResponsePayloadVO proposalResponsePayloadVO = new ProposalResponsePayloadVO();
            try {
                proposalResponse = ProposalResponsePackage.ProposalResponse.parseFrom(endorseResponse.getPayload());
                proposalResponsePayloadVO.parseFrom(ProposalResponsePackage.ProposalResponsePayload.parseFrom(proposalResponse.getPayload()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            } catch (ValidateException e) {
                e.printStackTrace();
            }

            return ProposalResponseUtils.buildProposalResponse(proposalResponse.getPayload());
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
        ValidateUtils.isNotNull(signedProposal, "signedProposal can not be null");
        ValidateUtils.isNotNull(signedProposal.getProposalBytes(), "signedProposal.proposal can not be null");
        ValidateUtils.isNotNull(signedProposal.getSignature(), "signedProposal.signature can not be null");

        //校验Proposal字段
        ProposalPackage.Proposal proposal = null;
        try {
            proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            //转化不成功，说明是错误的Proposal字段
            throw new NodeException("Wrong proposal");
        }
        ValidateUtils.isNotNull(proposal, "proposal can not be null");

        //校验Proposal头部
        ValidateUtils.isNotNull(proposal.getHeader(), "proposal.header can not be null");
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
        ValidateUtils.isNotBlank(groupHeader.getTxId(), "txId can not be empty");
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
            throw new NodeException("isSysCCAndNotInvokableExternal");
        }

        if (StringUtils.isNotBlank(groupHeader.getGroupId())) {
            //校验重复交易
            if (endorserSupport.getTransactionById(groupHeader.getGroupId(), groupHeader.getTxId()) != null) {
                throw new NodeException("duplicate transaction, creator:" + signatureHeader.getCreator());
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
            throw new NodeException("callSmartContract fail");
        }

        byte[] publicSimulateBytes = new byte[0];
        if (groupId != null && txSimulator != null) {
            TxSimulationResults simulationResults = null;
            try {
                simulationResults = txSimulator.getTxSimulationResults();
            } catch (LedgerException e) {
                log.error(e.getMessage(), e);
                throw new NodeException("get TxSimulation fail");
            }

            if (simulationResults.getPrivateReadWriteSet() != null) {
                if (scIDBuilder.getName().equals(CommConstant.LSSC)) {
                    log.error("should not be lssc here");
                    throw new NodeException("should not be lssc here");
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
    public ProposalResponsePackage.Response endorseProposal(String groupId, String txId, ProposalPackage
            .SignedProposal signedProposal, ProposalPackage.Proposal proposal, SmartContractPackage.SmartContractID.Builder
                                                                    smartContractIDBuilder, ProposalResponsePackage
                                                                    .Response response, byte[] simulateResults,
                                                            SmartContractEventPackage.SmartContractEvent event,
                                                            byte[] visibility, ISmartContractDefinition
                                                                    smartContractDefinition) throws NodeException {
        log.info("begin endorseProposal");

        boolean useSysSmartContract = false;
        if (smartContractDefinition == null) {
            useSysSmartContract = true;
        }

        useSysSmartContract = true;//TODO:for test

        //背书系统智能合约名称
        String essc = null;
        if (useSysSmartContract) {
            essc = CommConstant.ESSC;
            //TODO:应该从某个配置文件里面读取
            smartContractIDBuilder.setVersion(CommConstant.METADATA_VERSION);
        } else {
            essc = smartContractDefinition.getEndorsement();
            smartContractIDBuilder.setVersion(smartContractDefinition.getSmartContractVersion());
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
        SmartContractPackage.SmartContractInvocationSpec invocationSpec = SpecHelper.buildInvocationSpec(essc, args);
        String version = CommConstant.METADATA_VERSION;
        //开始调用essc
        ProposalResponsePackage.Response esscResponse = (ProposalResponsePackage.Response) callSmartContract(groupId, essc, version, txId,
                signedProposal, proposal, invocationSpec)[0];
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

        Object[] objs = endorserSupport.execute(groupId, scName,
                scVersion, txId, isSysSmartContract, signedProposal, proposal, spec);
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