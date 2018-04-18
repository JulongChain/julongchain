package org.bcia.javachain.common.localmsp.impl;

import org.junit.Test;

import static org.bcia.javachain.msp.mgmt.MspManager.getLocalMsp;
import static org.junit.Assert.*;

/**
 * @author zhangmingyang
 * @Date: 2018/4/18
 * @company Dingxuan
 */
public class LocalSignerTest {

    @Test
    public void newSignatureHeader() {
    }

    @Test
    public void sign() {
        LocalSigner localSigner=new LocalSigner();
        localSigner.sign("123".getBytes());
    }
}