package org.bcia.javachain.tools.configtxgen.entity;

import org.bcia.javachain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * 对象
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
        GenesisConfigFactory.loadGenesisConfig();
    }
}