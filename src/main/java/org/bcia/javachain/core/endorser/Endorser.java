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
import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.common.validation.MsgValidation;
import org.bcia.javachain.core.ledger.IHistoryQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;

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

        Common.GroupHeader groupHeader = (Common.GroupHeader) objs[0];
        Common.SignatureHeader signatureHeader = (Common.SignatureHeader) objs[1];
        ProposalPackage.SmartContractHeaderExtension extension = (ProposalPackage.SmartContractHeaderExtension) objs[2];

        ITxSimulator txSimulator = null;
        IHistoryQueryExecutor historyQueryExecutor = null;

        if (StringUtils.isNotBlank(groupHeader.getGroupId())) {
            txSimulator = endorserSupport.getTxSimulator(groupHeader.getGroupId(), groupHeader.getTxId());
            historyQueryExecutor = endorserSupport.getHistoryQueryExecutor(groupHeader.getGroupId());
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
        if (endorserSupport.isSysCCAndNotInvokableExternal(extension.getSmartContractId().getName())) {
            throw new NodeException("isSysCCAndNotInvokableExternal");
        }

        if (StringUtils.isNotBlank(groupHeader.getGroupId())) {
            //校验重复交易
            if (endorserSupport.getTransactionByID(groupHeader.getGroupId(), groupHeader.getTxId()) != null) {
                throw new NodeException("duplicate transaction, creator:" + signatureHeader.getCreator());
            }

            //校验权限:仅应用智能合约校验
            if (!endorserSupport.isSysCC(extension.getSmartContractId().getName())) {
                endorserSupport.checkACL(signedProposal, groupHeader, signatureHeader, extension);
            }
        }

        return new Object[]{groupHeader, signatureHeader, extension};
    }

    public IEndorserSupport getEndorserSupport() {
        return endorserSupport;
    }

    public void setEndorserSupport(IEndorserSupport endorserSupport) {
        this.endorserSupport = endorserSupport;
    }

    private Object[] simulateProposal(ProposalPackage.SignedProposal signedProposal) {
        return null;

    }

    private Object[] callChaincode(ProposalPackage.SignedProposal signedProposal) {
        return null;

    }

//    //call specified chaincode (system or user)
//    func(e *Endorser) callChaincode(
//    ctxt context.Context,
//    chainID string, version
//    string,
//    txid string, signedProp *pb.SignedProposal,prop *pb.Proposal,cis *pb.ChaincodeInvocationSpec,cid *pb.ChaincodeID,
//    txsim ledger.TxSimulator)(*pb.Response,*pb.ChaincodeEvent,error)
//
//    {
//
//        var err error
//        var res *pb.Response
//        var ccevent *pb.ChaincodeEvent
//
//        if txsim != nil {
//        ctxt = context.WithValue(ctxt, chaincode.TXSimulatorKey, txsim)
//    }
//
//        //is this a system chaincode
//        scc:=e.s.IsSysCC(cid.Name)
//
//        res, ccevent, err = e.s.Execute(ctxt, chainID, cid.Name, version, txid, scc, signedProp, prop, cis)
//        if err != nil {
//        return nil,nil, err
//    }
//
//        //per doc anything < 400 can be sent as TX.
//        //fabric errors will always be >= 400 (ie, unambiguous errors )
//        //"lscc" will respond with status 200 or 500 (ie, unambiguous OK or ERROR)
//        if res.Status >= shim.ERRORTHRESHOLD {
//        return res,nil, nil
//    }
//
//        //----- BEGIN -  SECTION THAT MAY NEED TO BE DONE IN LSCC ------
//        //if this a call to deploy a chaincode, We need a mechanism
//        //to pass TxSimulator into LSCC. Till that is worked out this
//        //special code does the actual deploy, upgrade here so as to collect
//        //all state under one TxSimulator
//        //
//        //NOTE that if there's an error all simulation, including the chaincode
//        //table changes in lscc will be thrown away
//        if
//        cid.Name == "lscc" && len(cis.ChaincodeSpec.Input.Args) >= 3 && (string(cis.ChaincodeSpec.Input.Args[0]) == "deploy" || string(cis.ChaincodeSpec.Input.Args[0]) == "upgrade")
//        {
//            var cds *pb.ChaincodeDeploymentSpec
//                cds, err = putils.GetChaincodeDeploymentSpec(cis.ChaincodeSpec.Input.Args[2])
//            if err != nil {
//            return nil,nil, err
//        }
//
//            //this should not be a system chaincode
//            if e.s.IsSysCC(cds.ChaincodeSpec.ChaincodeId.Name) {
//            return nil,
//            nil, errors.Errorf("attempting to deploy a system chaincode %s/%s", cds.ChaincodeSpec.ChaincodeId.Name, chainID)
//        }
//
//            _, _, err = e.s.Execute(ctxt, chainID, cds.ChaincodeSpec.ChaincodeId.Name, cds.ChaincodeSpec.ChaincodeId.Version, txid, false, signedProp, prop, cds)
//            if err != nil {
//            return nil,nil, err
//        }
//        }
//        //----- END -------
//
//        return res,ccevent, err
//    }


}
