package org.bcia.javachain.msp.mspconfig;

import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

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