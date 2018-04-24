package org.bcia.javachain.core.endorser;

import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.javachain.core.smartcontract.client.SmartContractSupportClient;
import org.bcia.javachain.core.smartcontract.node.SmartContractSupportServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;

import static org.bcia.javachain.protos.common.Common.*;
import static org.bcia.javachain.protos.node.ProposalPackage.Proposal;
import static org.bcia.javachain.protos.node.ProposalPackage.SignedProposal;
import static org.bcia.javachain.protos.node.ProposalResponsePackage.Response;
import static org.bcia.javachain.protos.node.SmartContractEventPackage.SmartContractEvent;
import static org.bcia.javachain.protos.node.Smartcontract.SmartContractID;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/29
 * @company Dingxuan
 */
public class EndorserTest extends BaseJunit4Test {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(EndorserTest.class);

    private SmartContractSupportServer server;
    private SmartContractSupportClient client;

    @Autowired
    private Endorser endorser;

    @Before
    public void setUp() throws Exception {

        new Thread() {
            @Override
            public void run() {
                server = new SmartContractSupportServer(7052);
                try {
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        Thread.sleep(5000);

        new Thread() {
            @Override
            public void run() {
                super.run();
                client = new SmartContractSupportClient();
                try {
                    client.lauch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Thread.sleep(5000);

    }

    @Test
    public void testGrpc() throws Exception {

        while(true) {
            Thread.sleep(2000);
            logger.info(new Date().toString());
        }

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void processProposal() {
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

        GroupHeader groupHeader = GroupHeader.newBuilder().setType(HeaderType.ENDORSER_TRANSACTION.getNumber()).build();
        Header header = Header.newBuilder().setGroupHeader(groupHeader.toByteString()).build();
        Proposal proposal = Proposal.newBuilder().setHeader(header.toByteString()).build();
        SignedProposal signedProposal = SignedProposal.newBuilder().setProposalBytes(proposal.toByteString()).build();

        SmartContractID.Builder smartContractIDBuilder = SmartContractID.newBuilder();
        Response response = Response.newBuilder().build();
        SmartContractEvent event = SmartContractEvent.newBuilder().build();
        byte[] simulateResults = new byte[]{0, 1, 2};
        byte[] visibility = new byte[]{3, 4, 5};
        ISmartContractDefinition smartContractDefinition = new MockSmartContractDefinition();

        endorser.endorseProposal(groupId, txId, signedProposal, proposal, smartContractIDBuilder, response, simulateResults, event, visibility, smartContractDefinition);
    }

}