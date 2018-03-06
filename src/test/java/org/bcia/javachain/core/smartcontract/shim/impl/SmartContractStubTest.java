/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract.shim.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.bcia.javachain.core.ledger.CompositeKey;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.queryresult.KvQueryResult;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.SmartContractEventPackage;
import org.bcia.javachain.protos.node.SmartcontractShim;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bcia.javachain.protos.common.Common.HeaderType.ENDORSER_TRANSACTION_VALUE;
import static org.junit.Assert.*;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import javax.xml.bind.DatatypeConverter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.nio.charset.StandardCharsets.UTF_8;
/**
 * 智能合约桩单元测试
 *
 * @author sunianle
 * @date 3/7/18
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
        List<ByteString> args = Arrays.asList(
                ByteString.copyFromUtf8("arg0"),
                ByteString.copyFromUtf8("arg1"),
                ByteString.copyFromUtf8("arg2"));
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, args, null);
        List<String> list = stub.getStringArgs();
        for (String item:list
             ) {
            System.out.println(item);
        }
        assertThat(stub.getStringArgs(), contains(args.stream().map(ByteString::toStringUtf8).toArray()));
    }

    @Test
    public void getFunction() {
        List<ByteString> args = Arrays.asList(
                ByteString.copyFromUtf8("function"),
                ByteString.copyFromUtf8("arg0"),
                ByteString.copyFromUtf8("arg1"));
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, args, null);
        assertThat(stub.getFunction(), is("function"));
    }

    @Test
    public void getParameters() {
        List<ByteString> args = Arrays.asList(
                ByteString.copyFromUtf8("function"),
                ByteString.copyFromUtf8("arg0"),
                ByteString.copyFromUtf8("arg1"));
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, args, null);
        assertThat(stub.getParameters(), contains("arg0", "arg1"));
    }

    @Test
    public void setEvent() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final byte[] payload = new byte[]{0x10, 0x20, 0x20};
        final String eventName = "event_name";
        stub.setEvent(eventName, payload);
        SmartContractEventPackage.SmartContractEvent event = stub.getEvent();
        assertThat(event, hasProperty("eventName", equalTo(eventName)));
        assertThat(event, hasProperty("payload", equalTo(ByteString.copyFrom(payload))));
    }

    @Test
    public void getEvent() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final byte[] payload = new byte[]{0x10, 0x20, 0x20};
        final String eventName = "event_name";
        stub.setEvent(eventName, payload);
        SmartContractEventPackage.SmartContractEvent event = stub.getEvent();
        assertThat(event, hasProperty("eventName", equalTo(eventName)));
        assertThat(event, hasProperty("payload", equalTo(ByteString.copyFrom(payload))));

        stub.setEvent(eventName, null);
        event = stub.getEvent();
        assertNotNull(event);
        assertThat(event, hasProperty("eventName", equalTo(eventName)));
        assertThat(event, hasProperty("payload", equalTo(ByteString.copyFrom(new byte[0]))));
    }

    @Test
    public void getGroupId() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        assertThat(stub.getGroupId(), is("myc"));
    }

    @Test
    public void getTxId() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        assertThat(stub.getTxId(), is("txId"));
    }

    @Test
    public void getState() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final byte[] value = new byte[]{0x10, 0x20, 0x30};
        when(handler.getState("myc", "txId", "key")).thenReturn(ByteString.copyFrom(value));
        assertThat(stub.getState("key"), is(value));
    }

    @Test
    public void putState() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final byte[] value = new byte[]{0x10, 0x20, 0x30};
        stub.putState("key", value);
        verify(handler).putState("myc", "txId", "key", ByteString.copyFrom(value));
    }

    @Test
    public void delState() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        stub.delState("key");
        verify(handler).deleteState("myc", "txId", "key");
    }

    @Test
    public void getStateByRange() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final String startKey = "START";
        final String endKey = "END";
        final KvQueryResult.KV[] keyValues = new KvQueryResult.KV[]{
                KvQueryResult.KV.newBuilder()
                        .setKey("A")
                        .setValue(ByteString.copyFromUtf8("Value of A"))
                        .build(),
                KvQueryResult.KV.newBuilder()
                        .setKey("B")
                        .setValue(ByteString.copyFromUtf8("Value of B"))
                        .build()
        };
        final SmartcontractShim.QueryResponse value = SmartcontractShim.QueryResponse.newBuilder()
                .setHasMore(false)
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyValues[0].toByteString()))
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyValues[1].toByteString()))
                .build();
        when(handler.getStateByRange("myc", "txId", startKey, endKey)).thenReturn(value);
        assertThat(stub.getStateByRange(startKey, endKey), contains(Arrays.stream(keyValues).map(KeyValue::new).toArray()));
    }

    @Test
    public void getStateByPartialCompositeKey() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final KvQueryResult.KV[] keyValues = new KvQueryResult.KV[]{
                KvQueryResult.KV.newBuilder()
                        .setKey("A")
                        .setValue(ByteString.copyFromUtf8("Value of A"))
                        .build(),
                KvQueryResult.KV.newBuilder()
                        .setKey("B")
                        .setValue(ByteString.copyFromUtf8("Value of B"))
                        .build()
        };
        final SmartcontractShim.QueryResponse value = SmartcontractShim.QueryResponse.newBuilder()
                .setHasMore(false)
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyValues[0].toByteString()))
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyValues[1].toByteString()))
                .build();
        when(handler.getStateByRange(anyString(), anyString(), anyString(), anyString())).thenReturn(value);
        stub.getStateByPartialCompositeKey("KEY");
        verify(handler).getStateByRange("myc", "txId", "KEY", "KEY\udbff\udfff");

        stub.getStateByPartialCompositeKey(null);
        verify(handler).getStateByRange("myc", "txId", "\u0001", "\u0001\udbff\udfff");

    }

    @Test
    public void createCompositeKey() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final CompositeKey key = stub.createCompositeKey("abc", "def", "ghi", "jkl", "mno");
        assertThat(key, hasProperty("objectType", equalTo("abc")));
        assertThat(key, hasProperty("attributes", hasSize(4)));
        assertThat(key, Matchers.hasToString(equalTo("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000")));
    }


    @Test
    public void splitCompositeKey() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final CompositeKey key = stub.splitCompositeKey("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000");
        assertThat(key, hasProperty("objectType", equalTo("abc")));
        assertThat(key, hasProperty("attributes", contains("def", "ghi", "jkl", "mno")));
        assertThat(key, Matchers.hasToString(equalTo("\u0000abc\u0000def\u0000ghi\u0000jkl\u0000mno\u0000")));
    }

    @Test
    public void getQueryResult() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final KvQueryResult.KV[] keyValues = new KvQueryResult.KV[]{
                KvQueryResult.KV.newBuilder()
                        .setKey("A")
                        .setValue(ByteString.copyFromUtf8("Value of A"))
                        .build(),
                KvQueryResult.KV.newBuilder()
                        .setKey("B")
                        .setValue(ByteString.copyFromUtf8("Value of B"))
                        .build()
        };
        final SmartcontractShim.QueryResponse value = SmartcontractShim.QueryResponse.newBuilder()
                .setHasMore(false)
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyValues[0].toByteString()))
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyValues[1].toByteString()))
                .build();
        when(handler.getQueryResult("myc", "txId", "QUERY")).thenReturn(value);
        assertThat(stub.getQueryResult("QUERY"), contains(Arrays.stream(keyValues).map(KeyValue::new).toArray()));
    }

    @Test
    public void getHistoryForKey() {
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), null);
        final KvQueryResult.KeyModification[] keyModifications = new KvQueryResult.KeyModification[]{
                KvQueryResult.KeyModification.newBuilder()
                        .setTxId("tx0")
                        .setTimestamp(Timestamp.getDefaultInstance())
                        .setValue(ByteString.copyFromUtf8("Value A"))
                        .build(),
                KvQueryResult.KeyModification.newBuilder()
                        .setTxId("tx1")
                        .setTimestamp(Timestamp.getDefaultInstance())
                        .setValue(ByteString.copyFromUtf8("Value B"))
                        .build()
        };
        final SmartcontractShim.QueryResponse value = SmartcontractShim.QueryResponse.newBuilder()
                .setHasMore(false)
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyModifications[0].toByteString()))
                .addResults(SmartcontractShim.QueryResultBytes.newBuilder().setResultBytes(keyModifications[1].toByteString()))
                .build();
        when(handler.getHistoryForKey("myc", "txId", "KEY")).thenReturn(value);
        assertThat(stub.getHistoryForKey("KEY"), contains(Arrays.stream(keyModifications).map(KeyModification::new).toArray()));
    }

    @Test
    public void invokeSmartContract() {
        final String txId = "txId", chaincodeName = "CHAINCODE_ID", channel = "CHAINCODE_CHANNEL";
        final SmartContractStub stub = new SmartContractStub(channel, txId, handler, Collections.emptyList(), null);
        final Response expectedResponse = new Response(Response.Status.SUCCESS, "MESSAGE", "PAYLOAD".getBytes(UTF_8));
        when(handler.invokeChaincode(channel, txId, chaincodeName, Collections.emptyList())).thenReturn(expectedResponse);
        assertThat(stub.invokeSmartContract(chaincodeName, Collections.emptyList()), is(expectedResponse));

        when(handler.invokeChaincode(eq(channel), eq(txId), eq(chaincodeName + "/" + channel), anyList())).thenReturn(expectedResponse);
        assertThat(stub.invokeSmartContract(chaincodeName, Collections.emptyList(), channel), is(expectedResponse));
    }

    @Test
    public void getSignedProposal() {
        final ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
                .setProposalBytes(ProposalPackage.Proposal.newBuilder()
                        .setHeader(Common.Header.newBuilder()
                                .setGroupHeader(Common.GroupHeader.newBuilder()
                                        .setType(ENDORSER_TRANSACTION_VALUE)
                                        .setTimestamp(Timestamp.getDefaultInstance())
                                        .build().toByteString()
                                )
                                .build().toByteString()
                        )
                        .build().toByteString()
                ).build();
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), signedProposal);
        assertThat(stub.getSignedProposal(), is(signedProposal));
    }

    @Test
    public void getTxTimestamp() {
        final Instant instant = Instant.now();
        final Timestamp timestamp = Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
        final ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
                .setProposalBytes(ProposalPackage.Proposal.newBuilder()
                        .setHeader(Common.Header.newBuilder()
                                .setGroupHeader(Common.GroupHeader.newBuilder()
                                        .setType(ENDORSER_TRANSACTION_VALUE)
                                        .setTimestamp(timestamp)
                                        .build().toByteString()
                                )
                                .build().toByteString()
                        )
                        .build().toByteString()
                ).build();
        final SmartContractStub stub = new SmartContractStub("myc", "txId", handler, Collections.emptyList(), signedProposal);
        assertThat(stub.getTxTimestamp(), is(instant));
    }

    @Test
    public void getCreator() {
        final Instant instant = Instant.now();
        final byte[] creator = "CREATOR".getBytes(UTF_8);
        final Timestamp timestamp = Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
        final ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
                .setProposalBytes(ProposalPackage.Proposal.newBuilder()
                        .setHeader(Common.Header.newBuilder()
                                .setGroupHeader(Common.GroupHeader.newBuilder()
                                        .setType(ENDORSER_TRANSACTION_VALUE)
                                        .setTimestamp(timestamp)
                                        .build().toByteString()
                                )
                                .setSignatureHeader(Common.SignatureHeader.newBuilder()
                                        .setCreator(ByteString.copyFrom(creator))
                                        .build().toByteString()
                                )
                                .build().toByteString()
                        )
                        .build().toByteString()
                ).build();
        final SmartContractStub stub = new SmartContractStub("myc", "txid", handler, new ArrayList<>(), signedProposal);
        assertThat(stub.getCreator(), is(creator));
    }

    @Test
    public void getTransient() {
        final ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
                .setProposalBytes(ProposalPackage.Proposal.newBuilder()
                        .setHeader(Common.Header.newBuilder()
                                .setGroupHeader(Common.GroupHeader.newBuilder()
                                        .setType(ENDORSER_TRANSACTION_VALUE)
                                        .setTimestamp(Timestamp.getDefaultInstance())
                                        .build().toByteString()
                                )
                                .build().toByteString()
                        )
                        .setPayload(ProposalPackage.SmartContractProposalPayload.newBuilder()
                                .putTransientMap("key0", ByteString.copyFromUtf8("value0"))
                                .putTransientMap("key1", ByteString.copyFromUtf8("value1"))
                                .build().toByteString()
                        )
                        .build().toByteString()
                ).build();
        final SmartContractStub stub = new SmartContractStub("myc", "txid", handler, new ArrayList<>(), signedProposal);
        assertThat(stub.getTransient(), allOf(
                hasEntry("key0", "value0".getBytes(UTF_8)),
                hasEntry("key1", "value1".getBytes(UTF_8))
        ));
    }

    @Test
    public void getBinding() {
        final byte[] expectedDigest = DatatypeConverter.parseHexBinary("5093dd4f4277e964da8f4afbde0a9674d17f2a6a5961f0670fc21ae9b67f2983");
        final ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
                .setProposalBytes(ProposalPackage.Proposal.newBuilder()
                        .setHeader(Common.Header.newBuilder()
                                .setGroupHeader(Common.GroupHeader.newBuilder()
                                        .setType(ENDORSER_TRANSACTION_VALUE)
                                        .setTimestamp(Timestamp.getDefaultInstance())
                                        .setEpoch(10)
                                        .build().toByteString()
                                )
                                .setSignatureHeader(Common.SignatureHeader.newBuilder()
                                        .setNonce(ByteString.copyFromUtf8("nonce"))
                                        .setCreator(ByteString.copyFromUtf8("creator"))
                                        .build().toByteString()
                                )
                                .build().toByteString()
                        )
                        .build().toByteString()
                ).build();
        final SmartContractStub stub = new SmartContractStub("myc", "txid", handler, new ArrayList<>(), signedProposal);
        assertThat(stub.getBinding(), is(expectedDigest));
    }
}