package org.bcia.javachain.node.common;

import com.google.protobuf.Timestamp;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/6
 * @company Dingxuan
 */
public class EnvelopeHelperTest {

    @Test
    public void sendCreateGroupTransaction() {
    }

    @Test
    public void sendTransaction() {
    }

    @Test
    public void sanityCheckAndSignConfigTx() {
    }

    @Test
    public void buildSignedEnvelope() {
    }

    @Test
    public void buildPayload() {
    }

    @Test
    public void buildGroupHeader() {
    }

    @Test
    public void nowTimestamp() {
        Timestamp timestamp = EnvelopeHelper.nowTimestamp();
        System.out.println(timestamp.getSeconds());
        System.out.println(timestamp.getNanos());

    }
}