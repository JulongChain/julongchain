package org.bcia.julongchain.node.common.helper;

import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.localmsp.ILocalSigner;
import org.bcia.julongchain.common.localmsp.impl.LocalSigner;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.protos.common.Common;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/6
 * @company Dingxuan
 */
public class EnvelopeHelperTest {

    @Mock
    private ILocalSigner localSigner;

    @Mock
    private Message data;

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
    public void buildPayload() throws NodeException, IOException {
        localSigner = new LocalSigner();
        data = EnvelopeHelper.buildGroupHeader(Common.HeaderType.CONFIG_UPDATE_VALUE, 0,
                "myGroup", 30);

        Common.Payload payload = EnvelopeHelper.buildPayload(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, "myGroup", localSigner, data, 30);
        assertArrayEquals(data.toByteArray(), payload.getData().toByteArray());

        Common.GroupHeader groupHeader1 = EnvelopeHelper.buildGroupHeader(Common.HeaderType.CONFIG_UPDATE_VALUE, 0,
                "myGroup", 30);
//        assertArrayEquals(groupHeader1.toByteArray(), payload.getHeader().getGroupHeader().toByteArray());
        assertEquals(groupHeader1.toByteArray().length, payload.getHeader().getGroupHeader().toByteArray().length);

        Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader()
                .toByteArray());
        assertEquals(2, groupHeader.getType());
        assertEquals(0, groupHeader.getVersion());
        assertEquals("myGroup", groupHeader.getGroupId());
        assertEquals(30, groupHeader.getEpoch());
        assertNotNull(payload.getHeader().getSignatureHeader().toByteArray());

        Common.GroupHeader groupHeader2 = Common.GroupHeader.parseFrom(groupHeader1.toByteArray());
        assertEquals(2, groupHeader2.getType());
        assertEquals(0, groupHeader2.getVersion());
        assertEquals("myGroup", groupHeader2.getGroupId());
        assertEquals(30, groupHeader2.getEpoch());
        assertNotNull(payload.getHeader().getSignatureHeader().toByteArray());
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