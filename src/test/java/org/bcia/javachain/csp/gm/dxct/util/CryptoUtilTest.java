package org.bcia.javachain.csp.gm.dxct.util;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.io.IOException;

/**
 * @author zhangmingyang
 * @Date: 2018/5/3
 * @company Dingxuan
 */
public class CryptoUtilTest {

    @Test
    public void publicKeyFileGen() {
    }

    @Test
    public void privateKeyFileGen() {
    }

    @Test
    public void loadKeyFile() throws IOException {
        byte[] keyContent=CryptoUtil.loadKeyFile("3c6b5f93774dcc7746067a107bfd9081c628a45ec725c3e79454cbe5c2b8a974_sk");
        System.out.println(Hex.toHexString(keyContent));
    }
}