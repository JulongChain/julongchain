package org.bcia.javachain.common.util.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.protos.node.Smartcontract;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/8/18
 * @company Dingxuan
 */
public class ProtoUtilsTest {

    @Test
    public void unmarshalSmartcontractID() {
        Smartcontract.SmartContractID.Builder idBuilder=Smartcontract.SmartContractID.newBuilder();
        idBuilder.setName("ESSC").setPath("core/ssc/essc").setVersion("1.0");
        Smartcontract.SmartContractID esscID = idBuilder.build();
        ByteString byteString = esscID.toByteString();
        try {
            Smartcontract.SmartContractID id=ProtoUtils.unmarshalSmartcontractID(byteString.toByteArray());
            assertThat(id.getName(),is("ESSC"));
            assertThat(id.getPath(),is("core/ssc/essc"));
            assertThat(id.getVersion(),is("1.0"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}