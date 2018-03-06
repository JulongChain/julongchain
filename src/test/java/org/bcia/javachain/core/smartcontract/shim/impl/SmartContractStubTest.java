package org.bcia.javachain.core.smartcontract.shim.impl;

import com.google.protobuf.ByteString;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

/**
 * 智能合约桩单元测试
 *
 * @author sunianle
 * @date 3/6/18
 * @company Dingxuan
 */
public class SmartContractStubTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private Handler handler;

    @Test
    public void getArgs() {
        List<ByteString> args = Arrays.asList(
                ByteString.copyFromUtf8("arg0"),
                ByteString.copyFromUtf8("arg1"),
                ByteString.copyFromUtf8("arg2"));
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, args, null);
        assertThat(stub.getArgs(), contains(args.stream().map(ByteString::toByteArray).toArray()));
    }

    @Test
    public void getStringArgs() {
    }

    @Test
    public void getFunction() {
    }

    @Test
    public void getParameters() {
    }

    @Test
    public void setEvent() {
    }

    @Test
    public void getEvent() {
    }

    @Test
    public void getGroupId() {
    }

    @Test
    public void getTxId() {
    }

    @Test
    public void getState() {
    }

    @Test
    public void putState() {
    }

    @Test
    public void delState() {
    }

    @Test
    public void getStateByRange() {
    }

    @Test
    public void getStateByPartialCompositeKey() {
    }

    @Test
    public void createCompositeKey() {
    }

    @Test
    public void splitCompositeKey() {
    }

    @Test
    public void getQueryResult() {
    }

    @Test
    public void getHistoryForKey() {
    }

    @Test
    public void invokeSmartContract() {
    }

    @Test
    public void getSignedProposal() {
    }

    @Test
    public void getTxTimestamp() {
    }

    @Test
    public void getCreator() {
    }

    @Test
    public void getTransient() {
    }

    @Test
    public void getBinding() {
    }
}