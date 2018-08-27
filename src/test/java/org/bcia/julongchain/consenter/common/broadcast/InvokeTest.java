package org.bcia.julongchain.consenter.common.broadcast;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.common.util.proto.ProposalUtils;
import org.bcia.julongchain.core.smartcontract.shim.helper.Channel;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
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
 * invok测试类
 * @author zhangmingyang
 * @date 2018/08/21
 * @company Dingxuan
 */
public class InvokeTest {
    public static void main(String[] args) throws Exception {
        for(int i=0;i<100000;i++){
        Channel<String> channel = send("myGroup", "mycc", SmartContractPackage.SmartContractInput.newBuilder()
                .addArgs(ByteString.copyFromUtf8("move"))
                .addArgs(ByteString.copyFromUtf8("a"))
                .addArgs(ByteString.copyFromUtf8("b"))
                .addArgs(ByteString.copyFromUtf8("10"))
                .build());
        System.out.println(channel.take());
        }
    }

    public static Channel<String> send(String groupID,
                                       String scName,
                                       SmartContractPackage.SmartContractInput input) throws Exception {
        String endorserHost = "127.0.0.1";
        int endorserPort = 7051;
        String consenterHost = "127.0.0.1";
        int consenterPort = 7050;
        Channel<String> responseChan = new Channel<>();
        //构建请求
        SmartContractPackage.SmartContractInvocationSpec sciSpec = SpecHelper.buildInvocationSpec(scName, input);
        ISigningIdentity identity;
        identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] creator = identity.getIdentity().serialize();
        byte[] nonce;
        nonce = CspManager.getDefaultCsp().rng(24, null);
        String txId;
        txId = ProposalUtils.computeProposalTxID(creator, nonce);
        //构建提案
        ProposalPackage.Proposal proposal =
                ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                        groupID, txId, sciSpec, nonce, creator, null);
        //构建带签名的提案
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);
        //向背书节点发送背书请求
        EndorserClient client = new EndorserClient(endorserHost, endorserPort);
        ProposalResponsePackage.ProposalResponse proposalResponse = null;
        try {
            proposalResponse = client.sendProcessProposal(signedProposal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.close();
        String message = proposalResponse.getResponse().getMessage();
        ByteString payload = proposalResponse.getResponse().getPayload();
        //广播背书结果
        Common.Envelope signedTxEnvelope = null;
        try {
            signedTxEnvelope = EnvelopeHelper.createSignedTxEnvelope(proposal, identity, proposalResponse);
        } catch (ValidateException e) {
            e.printStackTrace();
        }
        final IBroadcastClient broadcastClient = new BroadcastClient(consenterHost, consenterPort);
        broadcastClient.send(signedTxEnvelope, new StreamObserver<Ab.BroadcastResponse>() {
            @Override
            public void onNext(Ab.BroadcastResponse value) {
                broadcastClient.close();
                //返回响应消息
                responseChan.add(message);
            }

            @Override
            public void onError(Throwable t) {
                broadcastClient.close();
                //返回响应消息
                responseChan.add(t.getMessage());
            }

            @Override
            public void onCompleted() {
                broadcastClient.close();
            }
        });
        return responseChan;
    }
}