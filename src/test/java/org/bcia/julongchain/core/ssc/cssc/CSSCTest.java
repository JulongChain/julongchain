package org.bcia.julongchain.core.ssc.cssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.common.util.proto.TxUtils;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.smartcontract.shim.impl.MockStub;
import org.bcia.julongchain.node.common.helper.ConfigTreeHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.Query;
import org.bcia.julongchain.protos.node.ResourcesPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

/**
 * CSSC的单元测试类
 *
 * @author sunianle
 * @date 4/4/18
 * @company Dingxuan
 */
public class CSSCTest extends BaseJunit4Test {
    @Autowired
    private CSSC cssc;
    private MockStub mockStub;

    @Before
    public void beforeTest(){
        mockStub = new MockStub(CommConstant.CSSC, cssc);
    }

    @Test
    public void init() {
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void invoke() {

    }

    @Test
    public void testJoinGroupsWithCorrectParams(){
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("0",new LinkedList<ByteString>());
        List<ByteString> args0 = new LinkedList<ByteString>();
        //先删除账本
        FileUtils.deleteDir(new File(LedgerConfig.getRootPath()));
        try {
            LedgerManager.initialize(null);
        } catch (LedgerException e) {
            e.printStackTrace();
            return;
        }
        byte[] blockBytes=null;
        try {
            blockBytes = mockConfigBlock();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (JulongChainException e) {
            e.printStackTrace();
            return;
        }
        ProposalPackage.SignedProposal sp= null;
        try {
            sp = TxUtils.mockSignedEndorserProposalOrPanic("",
                    SmartContractPackage.SmartContractSpec.newBuilder().build());
        } catch (JulongChainException e) {
            e.printStackTrace();
            return;
        }
        args0.add(ByteString.copyFromUtf8(CSSC.JOIN_GROUP));
        args0.add(ByteString.copyFrom(blockBytes));
        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("0", args0,sp);
        //已调通，20180530
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));

        List<ByteString> args1 = new LinkedList<ByteString>();
        args1.add(ByteString.copyFromUtf8(CSSC.GET_CONFIG_BLOCK));
        args1.add(ByteString.copyFromUtf8("mytestchainid"));
        ISmartContract.SmartContractResponse res1 = mockStub.mockInvoke("1", args1);
        //未调通，20180530，Group.java的getBlock()返回空值
        assertThat(res1.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        try {
            Common.Block block = Common.Block.parseFrom(res1.getPayload());
            assertNotNull(block);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return;
        }

        List<ByteString> args2 = new LinkedList<ByteString>();
        args2.add(ByteString.copyFromUtf8(CSSC.GET_GROUPS));
        ISmartContract.SmartContractResponse res2 = mockStub.mockInvokeWithSignedProposal("2", args2,sp);
        assertThat(res2.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        try {
            Query.GroupQueryResponse groups = Query.GroupQueryResponse.parseFrom(res2.getPayload());
            assertNotNull(groups);
            assertThat(groups.getGroupsCount(),is(1));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return;
        }


        List<ByteString> args3 = new LinkedList<ByteString>();
        args3.add(ByteString.copyFromUtf8(CSSC.GET_CONFIG_TREE));
        args3.add(ByteString.copyFromUtf8("mytestchainid"));
        ISmartContract.SmartContractResponse res3 = mockStub.mockInvokeWithSignedProposal("3", args3,sp);
        assertThat(res3.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        try {
            ResourcesPackage.RootConfigTree tree = ResourcesPackage.RootConfigTree.parseFrom(res3.getPayload());
            assertNotNull(tree);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return;
        }
    }


    @Test
    public void testJoinGroupsWithWrongParams(){

    }

    @Test
    public void testJoinGroupsMissingParams(){

    }

    @Test
    public void testInvokeWithInvalidParams(){

    }

    @Test
    public void testSubmittingOrdererGenesis(){

    }



    private byte[] mockConfigBlock() throws IOException, ValidateException, JulongChainException {
        GenesisConfig.Profile profile=GenesisConfigFactory.getGenesisConfig().getCompletedProfile("SampleDevModeSolo");
        Configtx.ConfigTree tree = ConfigTreeHelper.buildGroupTree(profile);
        GenesisBlockFactory factory=new GenesisBlockFactory(tree);
        Common.Block block = factory.getGenesisBlock("mytestchainid");
        return block.toByteArray();
    }
}