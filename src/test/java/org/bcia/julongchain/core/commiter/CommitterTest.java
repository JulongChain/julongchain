package org.bcia.julongchain.core.commiter;

import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.common.ledger.util.Utils;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.node.common.helper.ConfigTreeHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

/**
 * 提交者测试类
 *
 * @author zhouhui
 * @date 2018/09/28
 * @company Dingxuan
 */
public class CommitterTest extends BaseJunit4Test {
    /**
     * 创建群组时的Profile名称
     */
    private static final String PROFILE_CREATE_GROUP = "SampleSingleMSPSolo";
    private static final String GROUP_ID = "zhouhuitest";


    private ICommitter committer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws JulongChainException {
        //删除所有文件
        FileUtils.deleteDir(new File(LedgerConfig.getRootPath()));

//        INodeLedger nodeLedger = null;
//        try {
//            nodeLedger = Utils.constructDefaultLedger();
//        } catch (Exception e) {
//
//        }

        //初始化账本
        try {
            LedgerManager.initialize(null);
        }catch (Exception ex){

        }

        //构造创世区块
        GenesisConfig.Profile profile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile
                (PROFILE_CREATE_GROUP);
        Configtx.ConfigTree groupTree = ConfigTreeHelper.buildGroupTree(profile);
        Common.Block genesisBlock = new GenesisBlockFactory(groupTree).getGenesisBlock(GROUP_ID);

        //创建账本
        INodeLedger ledger = LedgerManager.createLedger(genesisBlock);

        //构建提交者
        committer = new Committer(ledger, null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void commitWithPrivateData() throws Exception {
        //正确区块提交
        long height = committer.getLedgerHeight();

        Common.Block block = Utils.constructDefaultBlock(((Committer) committer).getNodeLedger(), ((Committer) committer).getNodeLedger()
                .getBlockByNumber(height - 1), GROUP_ID, "mycc");

        committer.commitWithPrivateData(new BlockAndPvtData(block, null, null));
        assertSame(height + 1, committer.getLedgerHeight());

        //错误区块提交
        block = Utils.constructDefaultBlock(((Committer) committer).getNodeLedger(), ((Committer) committer).getNodeLedger()
                .getBlockByNumber(height - 2), GROUP_ID, "mycc");
        thrown.expect(LedgerException.class);
        committer.commitWithPrivateData(new BlockAndPvtData(block, null, null));
    }

    @Test
    public void getPrivateDataAndBlockByNum() {
    }

    @Test
    public void getLedgerHeight() {
    }

    @Test
    public void getBlocks() {
    }
}