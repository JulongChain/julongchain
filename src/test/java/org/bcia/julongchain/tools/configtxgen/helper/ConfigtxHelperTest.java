package org.bcia.julongchain.tools.configtxgen.helper;

import org.bcia.julongchain.common.exception.ConfigtxToolsException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 配置交易帮助类
 *
 * @author zhouhui
 * @date 2018/10/20
 * @company Dingxuan
 */
public class ConfigtxHelperTest {

    @Test
    public void doOutputBlock() throws ConfigtxToolsException, ValidateException {
        String blockPath = "/opt/BCIA/JavaChain/myGroup_configtx.block";

        GenesisConfig.Profile profile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile
                ("SampleSingleMSPSolo");
        ConfigtxHelper.doOutputBlock(profile, "myGroup_configtx", blockPath);

        ConfigtxHelper.doInspectBlock(blockPath);
    }

    @Test
    public void doOutputGroupCreateTx() throws ConfigtxToolsException, ValidateException {
        String txPath = "/opt/BCIA/JavaChain/myGroup_configtx.tx";

        GenesisConfig.Profile profile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile
                ("SampleSingleMSPGroup");
        ConfigtxHelper.doOutputGroupCreateTx(profile, "myGroup_configtx", txPath);

        ConfigtxHelper.doInspectGroupCreateTx(txPath);
    }

    @Test
    public void doOutputAnchorNodesUpdate() throws ConfigtxToolsException, ValidateException {
        String txPath = "/opt/BCIA/JavaChain/anchor_configtx.tx";

        GenesisConfig.Profile profile = GenesisConfigFactory.getGenesisConfig().getCompletedProfile
                ("SampleSingleMSPGroup");
        ConfigtxHelper.doOutputAnchorNodesUpdate(profile, "myGroup_configtx", txPath, "SampleOrg");

        ConfigtxHelper.doInspectGroupCreateTx(txPath);
    }
}