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
package org.bcia.javachain.node.entity;

import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.common.util.proto.ProposalUtils;
import org.bcia.javachain.consenter.common.broadcast.BroadCastClient;
import org.bcia.javachain.core.endorser.Endorser;
import org.bcia.javachain.core.ssc.cssc.CSSC;
import org.bcia.javachain.core.ssc.lssc.LSSC;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.mgmt.Mgmt;
import org.bcia.javachain.node.common.client.EndorserClient;
import org.bcia.javachain.node.common.helper.SpecHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.springframework.expression.spel.ast.NullLiteral;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 节点智能合约
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class NodeSmartContract {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeSmartContract.class);

    public void install() {

    }
    public void install(String scName, String ctor, String scVersion, String scLanguage) throws NodeException {
            //生成proposal  Type=ENDORSER_TRANSACTION
            Smartcontract.SmartContractInvocationSpec spec = SpecHelper.buildInvocationSpec(scName, ctor, null);

            ISigningIdentity identity = new MockSigningIdentity();
            byte[] creator = identity.serialize();

            byte[] nonce = MockCrypto.getRandomNonce();

            String txId = null;
            try {
                txId = ProposalUtils.computeProposalTxID(creator, nonce);
            } catch (JavaChainException e) {
                log.error(e.getMessage(), e);
                throw new NodeException("Generate txId fail");
            }

            //生成proposal  Type=ENDORSER_TRANSACTION
            ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                    "", txId, spec, nonce, creator, null);
            ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

            //获取背书节点返回信息
            EndorserClient client = new EndorserClient( LSSC.DEFAULT_HOST, LSSC.DEFAULT_PORT);
            ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);
            log.info("Query Result: " + proposalResponse.getPayload().toString(Charset.forName(CommConstant.DEFAULT_CHARSET)));
    }

    public void instantiate(String scName, String scVersion, String ctor) {
        Smartcontract.SmartContractDeploymentSpec deploymentSpec = SpecHelper.buildDeploymentSpec(scName, scVersion,
                ctor.getBytes());

        ISigningIdentity identity = new Mgmt().getLocalMsp().getDefaultSigningIdentity();

    }

    public void invoke() {

    }
    public void invoke(String IP, int port, String groupId, String scName, String ctor, String scLanguage)
            throws NodeException {
        Smartcontract.SmartContractInvocationSpec sciSpec = SpecHelper.buildInvocationSpec(scName, ctor, null);

        ISigningIdentity identity = new MockSigningIdentity();
        byte[] creator = identity.serialize();

        byte[] nonce = MockCrypto.getRandomNonce();

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
        EndorserClient client = new EndorserClient("127.0.0.1", 7015);
        ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);

        //envelop V0.25
        BroadCastClient broadCastClient = new BroadCastClient();
        try {
            broadCastClient.send(IP, port, Common.Envelope.newBuilder().build(), (StreamObserver<Ab.BroadcastResponse>) this);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        //TODO:待实现   build Envelop = proposal + identity + proposalResponse
        Common.Envelope envelope = null;
        //Common.Envelope envelope = EnvelopeHelper.createInvokeEnvelopeSignedTx(proposal, identity, proposalResponse);
    }

    public void query(String groupId, String smartContractName, String ctor) throws NodeException {
        Smartcontract.SmartContractInvocationSpec spec = SpecHelper.buildInvocationSpec(smartContractName, ctor, null);

        ISigningIdentity identity = new MockSigningIdentity();
        byte[] creator = identity.serialize();

        byte[] nonce = MockCrypto.getRandomNonce();

        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JavaChainException e) {
            log.error(e.getMessage(), e);
            throw new NodeException("Generate txId fail");
        }

        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                "", txId, spec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        EndorserClient client = new EndorserClient("127.0.0.1", 7015);
        ProposalResponsePackage.ProposalResponse proposalResponse = client.sendProcessProposal(signedProposal);

        log.info("Query Result: " + proposalResponse.getPayload().toString(Charset.forName(CommConstant.DEFAULT_CHARSET)));

    }
}
