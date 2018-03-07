package org.bcia.javachain.node.common;

import com.google.protobuf.Timestamp;
import org.bcia.javachain.protos.common.Common;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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
        Common.GroupHeader groupHeader = EnvelopeHelper.buildGroupHeader(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, "myGroup", 30);
        assertEquals(2, groupHeader.getType());
        assertEquals(0, groupHeader.getVersion());
        assertEquals("myGroup", groupHeader.getGroupId());
        assertEquals(30, groupHeader.getEpoch());
    }

    @Test
    public void nowTimestamp() {
        Timestamp timestamp = EnvelopeHelper.nowTimestamp();
        System.out.println(timestamp.getSeconds());
        System.out.println(timestamp.getNanos());
        assertEquals(System.currentTimeMillis() / 1000, timestamp.getSeconds());
    }
}