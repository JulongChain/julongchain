package org.bcia.julongchain.tools.configtxgen.helper;

import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.ConfigtxToolsException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;
import org.junit.Test;

import java.io.File;

/**
 * 配置交易帮助类
 *
 * @author zhouhui
 * @date 2018/10/20
 * @company Dingxuan
 */
public class ConfigtxHelperTest extends BaseJunit4Test {

    @Test
    public void doOutputBlock() throws ConfigtxToolsException, ValidateException, InterruptedException {
        String blockPath = "/opt/BCIA/JavaChain/myGroup_configtx.block";
        FileUtils.deleteDir(new File(blockPath));

        GenesisConfig.Profile profile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile
                ("SampleSingleMSPSolo");
        ConfigtxHelper.doOutputBlock(profile, "myGroup_configtx", blockPath);

        assertNotNull(profile);
    }

    @Test
    public void doOutputGroupCreateTx() throws ConfigtxToolsException, ValidateException {
        String txPath = "/opt/BCIA/JavaChain/myGroup_configtx.tx";
        FileUtils.deleteDir(new File(txPath));

        GenesisConfig.Profile profile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile
                ("SampleSingleMSPGroup");
        ConfigtxHelper.doOutputGroupCreateTx(profile, "myGroup_configtx", txPath);

        assertNotNull(profile);
    }

    @Test
    public void doOutputAnchorNodesUpdate() throws ConfigtxToolsException, ValidateException {
        String txPath = "/opt/BCIA/JavaChain/anchor_configtx.tx";
        FileUtils.deleteDir(new File(txPath));

        GenesisConfig.Profile profile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile
                ("SampleSingleMSPGroup");
        ConfigtxHelper.doOutputAnchorNodesUpdate(profile, "myGroup_configtx", txPath, "SampleOrg");

        assertNotNull(profile);
    }
}