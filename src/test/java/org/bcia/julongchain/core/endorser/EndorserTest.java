package org.bcia.julongchain.core.endorser;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.proto.ProposalUtils;
import org.bcia.julongchain.core.ssc.cssc.CSSC;
import org.bcia.julongchain.csp.factory.CspManager;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.node.common.helper.SpecHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/29
 * @company Dingxuan
 */
public class EndorserTest extends BaseJunit4Test {

    private static final JulongChainLog logger = JulongChainLogFactory.getLog(EndorserTest.class);

    @Autowired
    private Endorser endorser;

    @Before
    public void setUp() throws Exception {

//        new Thread(() -> {
//            try {
//                SmartContractSupportServer server = new SmartContractSupportServer(7052);
//                server.start();
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
//        }).start();
//
//
//        while(true) {
//            try {
//                Thread.sleep(5000);
//                logger.info(new Date().toString());
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
//
//        }

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void processProposal() throws NodeException {
        SmartContractPackage.SmartContractInvocationSpec csscSpec = SpecHelper.buildInvocationSpec(CommConstant.CSSC, CSSC
                .GET_GROUPS, null);

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] creator = identity.getIdentity().serialize();

        byte[] nonce = null;
        try {
            nonce = CspManager.getDefaultCsp().rng(CommConstant.DEFAULT_NONCE_LENGTH, null);
        } catch (JulongChainException e) {
            throw new NodeException("Can not get nonce");
        }

        String txId = null;
        try {
            txId = ProposalUtils.computeProposalTxID(creator, nonce);
        } catch (JulongChainException e) {
            throw new NodeException("Generate txId fail");
        }

        //生成proposal  Type=ENDORSER_TRANSACTION
        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                "", txId, csscSpec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);
        ProposalResponsePackage.ProposalResponse endorserResponse = endorser.processProposal(signedProposal);

        assertEquals(200, endorserResponse.getResponse().getStatus());

    }

    @Test
    public void getEndorserSupport() {
    }

    @Test
    public void setEndorserSupport() {
    }

    @Test
    public void endorseProposal() throws NodeException {
        String groupId = "mygroup";
        String txId = "txId1";
        ProposalPackage.Proposal proposal = ProposalPackage.Proposal.newBuilder().build();
        ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder().build();
        SmartContractPackage.SmartContractID.Builder smartContractIDBuilder = SmartContractPackage.SmartContractID.newBuilder();
        ProposalResponsePackage.Response response = ProposalResponsePackage.Response.newBuilder().build();
        SmartContractEventPackage.SmartContractEvent event = SmartContractEventPackage.SmartContractEvent.newBuilder
                ().build();
        byte[] simulateResults = new byte[]{0, 1, 2};
        byte[] visibility = new byte[]{3, 4, 5};
        ISmartContractDefinition smartContractDefinition = new MockSmartContractDefinition();

        ProposalResponsePackage.Response esscResponse = endorser.endorseProposal(groupId, txId, signedProposal,
                proposal, smartContractIDBuilder, response, simulateResults, event, visibility,
                smartContractDefinition);
    }

    @Test
    public void callSmartContract() throws JulongChainException, InvalidProtocolBufferException {
        SmartContractPackage.SmartContractInvocationSpec csscSpec = SpecHelper.buildInvocationSpec(CommConstant.CSSC, CSSC
                .GET_GROUPS, null);

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] creator = identity.getIdentity().serialize();
        byte[] nonce = CspManager.getDefaultCsp().rng(CommConstant.DEFAULT_NONCE_LENGTH, null);

        String txId = ProposalUtils.computeProposalTxID(creator, nonce);

        //生成交易提案
        ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                "", txId, csscSpec, nonce, creator, null);
        ProposalPackage.SignedProposal signedProposal = ProposalUtils.buildSignedProposal(proposal, identity);

        ProposalResponsePackage.Response csscResponse = (ProposalResponsePackage.Response)endorser.callSmartContract("", CommConstant.CSSC, "0", txId,
                signedProposal, proposal, csscSpec)[0];

        Query.GroupQueryResponse groupQueryResponse = Query.GroupQueryResponse.parseFrom(csscResponse.getPayload());
        List<Query.GroupInfo> groupsList = groupQueryResponse.getGroupsList();

        for(Query.GroupInfo groupInfo: groupsList){
            System.out.println("GroupInfo: " + groupInfo.getGroupId());
        };
    }
}