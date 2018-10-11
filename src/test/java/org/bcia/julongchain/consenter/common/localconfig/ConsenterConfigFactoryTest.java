package org.bcia.julongchain.consenter.common.localconfig;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * consenter配置工厂测试
 *
 * @author zhangmingyang
 * @date 2018/08/26
 * @company Dingxuan
 */
public class ConsenterConfigFactoryTest {

    @Test
    public void getConsenterConfigTest() {
        ConsenterConfig consenterConfig = ConsenterConfigFactory.getConsenterConfig();
        System.out.println("------------------------consenter.yaml文件结构--------------------------");
        System.out.println(consenterConfig.getFileLedger().toString());
        System.out.println(consenterConfig.getGeneral().toString());
        System.out.println(consenterConfig.getRamLedger().toString());
        System.out.println(consenterConfig.getDebug().toString());
    }
}