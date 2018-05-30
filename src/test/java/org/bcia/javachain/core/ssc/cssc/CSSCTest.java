package org.bcia.javachain.core.ssc.cssc;

import com.google.protobuf.ByteString;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.groupconfig.config.GroupConfig;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.core.smartcontract.shim.ISmartContract;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.smartcontract.shim.impl.MockStub;
import org.bcia.javachain.core.ssc.lssc.LSSC;
import org.bcia.javachain.node.common.helper.ConfigTreeHelper;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
    public void testJoinChains(){
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
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
        } catch (JavaChainException e) {
            e.printStackTrace();
            return;
        }
        args0.add(ByteString.copyFromUtf8(CSSC.JOIN_GROUP));
        args0.add(ByteString.copyFrom(blockBytes));
        ISmartContract.SmartContractResponse res = mockStub.mockInvoke("2", args0);
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void testGetConfigBlock(){

    }

    @Test
    public void testGetGroups(){

    }

    @Test
    public void testGetConfigTree(){

    }

    @Test
    public void testSimulateConfigTreeUpdate(){

    }


    private byte[] mockConfigBlock() throws IOException, ValidateException,JavaChainException {
        GenesisConfig.Profile profile=GenesisConfigFactory.loadGenesisConfig().getCompletedProfile("SampleDevModeSolo");
        Configtx.ConfigTree tree = ConfigTreeHelper.buildGroupTree(profile);
        GenesisBlockFactory factory=new GenesisBlockFactory(tree);
        Common.Block block = factory.getGenesisBlock("mytestchainid");
        return block.toByteArray();
    }
}