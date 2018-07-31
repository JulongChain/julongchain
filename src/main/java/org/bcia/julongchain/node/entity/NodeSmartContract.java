/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.node.entity;

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.protos.EnvelopeVO;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.common.util.proto.ProposalUtils;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.common.client.BroadcastClient;
import org.bcia.julongchain.node.common.client.EndorserClient;
import org.bcia.julongchain.node.common.client.IBroadcastClient;
import org.bcia.julongchain.node.common.helper.SpecHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;

/**
 * 节点智能合约
 *
 * @author zhouhui wanglei
 * @date 2018/2/23
 * @company Dingxuan
 */
public class NodeSmartContract {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeSmartContract.class);

    private Node node;

    public NodeSmartContract() {
    }

    public NodeSmartContract(Node node) {
        this.node = node;
    }

    public void install(String nodeHost, int nodePort, String scName, String scVersion, String scPath) throws NodeException {
        SmartContractPackage.SmartContractDeploymentSpec deploymentSpec = SpecHelper.buildDeploymentSpec(scName, scVersion,
                scPath, null);

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();

        byte[] creator = identity.getIdentity().serialize();

        byte[] nonce = new byte[0];
        try {
            nonce = CspManager.getDefaultCsp().rng(24, null);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
        }

        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Generate txId fail");
        }

        SmartContractPackage.SmartContractInvocationSpec lsscSpec = SpecHelper.buildInvocationSpec(CommConstant.LSSC,
                LSSC.INSTALL.getBytes(), deploymentSpec.toByteArray());
        //生成proposal  Type=ENDORSER_TRANSACTION
        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType
                .ENDORSER_TRANSACTION, "", txId, lsscSpec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        //获取背书节点返回信息
        EndorserClient client = new EndorserClient(nodeHost, nodePort);
        ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);
        log.info("Install Result: " + proposalResponse.getResponse().getStatus());
    }

    public void instantiate(String nodeHost, int nodePort, String consenterHost, int consenterPort, String groupId,
                            String scName, String scVersion, SmartContractPackage.SmartContractInput input) throws NodeException {
        SmartContractPackage.SmartContractDeploymentSpec deploymentSpec = SpecHelper.buildDeploymentSpec(scName, scVersion,
                null, input);

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();

        byte[] creator = identity.getIdentity().serialize();

        byte[] nonce = new byte[0];
        try {
            nonce = CspManager.getDefaultCsp().rng(24, null);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
        }

        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Generate txId fail");
        }

        SmartContractPackage.SmartContractInvocationSpec lsscSpec = SpecHelper.buildInvocationSpec(CommConstant.LSSC,
                CommConstant.DEPLOY.getBytes(), groupId.getBytes(), deploymentSpec.toByteArray());
        //生成proposal  Type=ENDORSER_TRANSACTION
        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType
                .ENDORSER_TRANSACTION, groupId, txId, lsscSpec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        //获取背书节点返回信息
        EndorserClient client = new EndorserClient(nodeHost, nodePort);
        ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);

        try {
            Common.Envelope signedTxEnvelope = EnvelopeHelper.createSignedTxEnvelope(proposal, identity, proposalResponse);

//            EnvelopeVO envelopeVO = new EnvelopeVO();
//            try {
//                envelopeVO.parseFrom(signedTxEnvelope);
//            } catch (InvalidProtocolBufferException e) {
//                log.error(e.getMessage(), e);
//            }

            IBroadcastClient broadcastClient = new BroadcastClient(consenterHost, consenterPort);
            broadcastClient.send(signedTxEnvelope, new StreamObserver<Ab.BroadcastResponse>() {
                @Override
                public void onNext(Ab.BroadcastResponse value) {
                    log.info("Broadcast onNext");
                    broadcastClient.close();

                    //收到响应消息，判断是否是200消息
                    if (Common.Status.SUCCESS.equals(value.getStatus())) {
                        log.info("instantiate success");
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error(t.getMessage(), t);
                    broadcastClient.close();
                }

                @Override
                public void onCompleted() {
                    log.info("Broadcast completed");
                    broadcastClient.close();
                }
            });

        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }

        try {
            Thread.sleep(900000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

    }

    public void invoke(String nodeHost, int nodePort, String consenterHost, int consenterPort, String groupId,
                       String scName, SmartContractPackage.SmartContractInput input) throws NodeException {
        SmartContractPackage.SmartContractInvocationSpec sciSpec = SpecHelper.buildInvocationSpec(scName, input);

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();

        byte[] creator = identity.getIdentity().serialize();

        byte[] nonce = new byte[0];
        try {
            nonce = CspManager.getDefaultCsp().rng(24, null);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
        }

        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Generate txId fail");
        }

        //build proposal
        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                groupId, txId, sciSpec, nonce, creator, null);
        //build signedProposal
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        //背书
        EndorserClient client = new EndorserClient(nodeHost, nodePort);
        ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);

        try {
            Common.Envelope signedTxEnvelope = EnvelopeHelper.createSignedTxEnvelope(proposal, identity, proposalResponse);

//            EnvelopeVO envelopeVO = new EnvelopeVO();
//            envelopeVO.parseFrom(signedTxEnvelope);

            IBroadcastClient broadcastClient = new BroadcastClient(consenterHost, consenterPort);
            broadcastClient.send(signedTxEnvelope, new StreamObserver<Ab.BroadcastResponse>() {
                @Override
                public void onNext(Ab.BroadcastResponse value) {
                    log.info("Broadcast onNext");
                    broadcastClient.close();

                    //收到响应消息，判断是否是200消息
                    if (Common.Status.SUCCESS.equals(value.getStatus())) {
                        log.info("Invoke success");
                    } else {
                        log.info("Invoke fail: " + value.getStatus());
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error(t.getMessage(), t);
                    broadcastClient.close();
                }

                @Override
                public void onCompleted() {
                    log.info("Broadcast completed");
                    broadcastClient.close();
                }
            });

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }

        try {
            Thread.sleep(900000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void query(String groupId, String smartContractName, SmartContractPackage.SmartContractInput input) throws NodeException {
        SmartContractPackage.SmartContractInvocationSpec spec = SpecHelper.buildInvocationSpec(smartContractName, input);

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();

        byte[] creator = identity.getIdentity().serialize();

        byte[] nonce = new byte[0];
        try {
            nonce = CspManager.getDefaultCsp().rng(24, null);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
        }

        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Generate txId fail");
        }

        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType
                .ENDORSER_TRANSACTION, groupId, txId, spec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        EndorserClient client = new EndorserClient(LSSC.DEFAULT_HOST, LSSC.DEFAULT_PORT);
        ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);

        log.info("Query Result: " + proposalResponse.getPayload().toStringUtf8());
    }
}
