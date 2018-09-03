package org.bcia.julongchain.tools.configtxgen.entity;

import org.bcia.julongchain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 创世区块配置工厂测试类
 *
 * @author zhouhui
 * @date 2018/3/20
 * @company Dingxuan
 */
public class GenesisConfigFactoryTest extends BaseJunit4Test {
    
    @Autowired
    private GenesisConfigFactory genesisConfigFactory;

    @Test
    public void loadGenesisConfig() throws Exception {
        GenesisConfigFactory.getGenesisConfig();
    }
}