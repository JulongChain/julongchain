package org.bcia.julongchain.msp.mspconfig;

import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * @author zhangmingyang
 * @Date: 2018/4/18
 * @company Dingxuan
 */
public class MspConfigFactoryTest {

    @Test
    public void loadMspConfig() {
        try {
            MspConfigFactory.loadMspConfig();
            System.out.println("默认csp配置："+MspConfigFactory.loadMspConfig().getNode().getCsp().getDefaultValue());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}