package org.bcia.javachain.msp;

import org.bcia.javachain.msp.util.LoadLocalMspFiles;
import org.junit.Test;

/**
 * @author zhangmingyang
 * @Date: 2018/4/8
 * @company Dingxuan
 */
public class ConfigBuilderTest {

    @Test
    public void iteratorPath() {
    }

    @Test
    public void init() {
        LoadLocalMspFiles.init("E:/msp");
        System.out.println(LoadLocalMspFiles.mspMap.get("E:\\msp\\keystore\\privatekey.pem"));
    }
}