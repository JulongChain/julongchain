/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract.shim.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import org.bcia.javachain.core.ledger.CompositeKey;
import org.bcia.javachain.core.ledger.IKeyModification;
import org.bcia.javachain.core.ledger.IKeyValue;
import org.bcia.javachain.core.ledger.IQueryResultsIterator;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.queryresult.KvQueryResult;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.SmartContractEventPackage;
import org.bcia.javachain.protos.node.SmartcontractShim;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class SmartContractStub implements ISmartContractStub {

	private static final String UNSPECIFIED_KEY = new String(Character.toChars(0x000001));
	private final String groupId;
	private final String txId;
	private final Handler handler;
	private final List<ByteString> args;
	private final ProposalPackage.SignedProposal signedProposal;
	private final Instant txTimestamp;
	private final ByteString creator;
	private final Map<String, ByteString> transientMap;
	private final byte[] binding;
	private SmartContractEventPackage.SmartContractEvent event;

	public SmartContractStub(String groupId, String txId, Handler handler, List<ByteString> args, ProposalPackage.SignedProposal signedProposal) {
		this.groupId = groupId;
		this.txId = txId;
		this.handler = handler;
		this.args = Collections.unmodifiableList(args);
		this.signedProposal = signedProposal;
		if(this.signedProposal == null) {
			this.creator = null;
			this.txTimestamp = null;
			this.transientMap = Collections.emptyMap();
			this.binding = null;
		} else {
			try {
				final ProposalPackage.Proposal proposal = ProposalPackage.Proposal.parseFrom(signedProposal.getProposalBytes());
				final Common.Header header = Common.Header.parseFrom(proposal.getHeader());
				final Common.GroupHeader channelHeader = Common.GroupHeader.parseFrom(header.getGroupHeader());
				validateProposalType(channelHeader);
				final Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(header.getSignatureHeader());
				final ProposalPackage.SmartContractProposalPayload chaincodeProposalPayload = ProposalPackage.SmartContractProposalPayload.parseFrom(proposal.getPayload());
				final Timestamp timestamp = channelHeader.getTimestamp();

				this.txTimestamp = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
				this.creator = signatureHeader.getCreator();
				this.transientMap = chaincodeProposalPayload.getTransientMapMap();
				this.binding = computeBinding(channelHeader, signatureHeader);
			} catch (InvalidProtocolBufferException | NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private byte[] computeBinding(final Common.GroupHeader channelHeader, final Common.SignatureHeader signatureHeader) throws NoSuchAlgorithmException {
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(signatureHeader.getNonce().asReadOnlyByteBuffer());
		messageDigest.update(this.creator.asReadOnlyByteBuffer());
		final ByteBuffer epochBytes = ByteBuffer.allocate(Long.BYTES)
				.order(ByteOrder.LITTLE_ENDIAN)
				.putLong(channelHeader.getEpoch());
		epochBytes.flip();
		messageDigest.update(epochBytes);
		return messageDigest.digest();
	}

	private void validateProposalType(Common.GroupHeader channelHeader) {
		switch (Common.HeaderType.forNumber(channelHeader.getType())) {
		case ENDORSER_TRANSACTION:
		case CONFIG:
			return;
		default:
			throw new RuntimeException(String.format("Unexpected transaction type: %s", Common.HeaderType.forNumber(channelHeader.getType())));
		}
	}

	@Override
	public List<byte[]> getArgs() {
		return args.stream().map(x -> x.toByteArray()).collect(Collectors.toList());
	}

	@Override
	public List<String> getStringArgs() {
		return args.stream().map(x -> x.toStringUtf8()).collect(Collectors.toList());
	}

	@Override
	public String getFunction() {
		return getStringArgs().size() > 0 ? getStringArgs().get(0) : null;
	}

	@Override
	public List<String> getParameters() {
		return getStringArgs().stream().skip(1).collect(toList());
	}

	@Override
	public void setEvent(String name, byte[] payload) {
		if (name == null || name.trim().length() == 0) throw new IllegalArgumentException("Event name cannot be null or empty string.");
		if (payload != null) {
			this.event = SmartContractEventPackage.SmartContractEvent.newBuilder()
					.setEventName(name)
					.setPayload(ByteString.copyFrom(payload))
					.build();
		} else {
			this.event = SmartContractEventPackage.SmartContractEvent.newBuilder()
					.setEventName(name)
					.build();
		}
	}

	@Override
	public SmartContractEventPackage.SmartContractEvent getEvent() {
		return event;
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public String getTxId() {
		return txId;
	}

	@Override
	public byte[] getState(String key) {
		return handler.getState(groupId, txId, key).toByteArray();
	}

	@Override
	public void putState(String key, byte[] value) {
		if(key == null) throw new NullPointerException("key cannot be null");
		if(key.length() == 0) throw new IllegalArgumentException("key cannot not be an empty string");
		handler.putState(groupId, txId, key, ByteString.copyFrom(value));
	}

	@Override
	public void delState(String key) {
		handler.deleteState(groupId, txId, key);
	}

	@Override
	public IQueryResultsIterator<IKeyValue> getStateByRange(String startKey, String endKey) {
		if (startKey == null || startKey.isEmpty()) startKey = UNSPECIFIED_KEY;
		if (endKey == null || endKey.isEmpty()) endKey = UNSPECIFIED_KEY;
		CompositeKey.validateSimpleKeys(startKey, endKey);

		return new QueryResultsIteratorImpl<IKeyValue>(this.handler, getGroupId(), getTxId(),
				handler.getStateByRange(getGroupId(), getTxId(), startKey, endKey),
				queryResultBytesToKv.andThen(KeyValue::new)
				);
	}

	private Function<SmartcontractShim.QueryResultBytes, KvQueryResult.KV> queryResultBytesToKv = new Function<SmartcontractShim.QueryResultBytes, KvQueryResult.KV>() {
		public KvQueryResult.KV apply(SmartcontractShim.QueryResultBytes queryResultBytes) {
			try {
				return KvQueryResult.KV.parseFrom(queryResultBytes.getResultBytes());
			} catch (InvalidProtocolBufferException e) {
				throw new RuntimeException(e);
			}
		};
	};

	@Override
	public IQueryResultsIterator<IKeyValue> getStateByPartialCompositeKey(String compositeKey) {
		if (compositeKey == null || compositeKey.isEmpty()) {
			compositeKey = UNSPECIFIED_KEY;
		}
		return getStateByRange(compositeKey, compositeKey + "\udbff\udfff");
	}

	@Override
	public CompositeKey createCompositeKey(String objectType, String... attributes) {
		return new CompositeKey(objectType, attributes);
	}

	@Override
	public CompositeKey splitCompositeKey(String compositeKey) {
		return CompositeKey.parseCompositeKey(compositeKey);
	}

	@Override
	public IQueryResultsIterator<IKeyValue> getQueryResult(String query) {
		return new QueryResultsIteratorImpl<IKeyValue>(this.handler, getGroupId(), getTxId(),
				handler.getQueryResult(getGroupId(), getTxId(), query),
				queryResultBytesToKv.andThen(KeyValue::new)
				);
	}

	@Override
	public IQueryResultsIterator<IKeyModification> getHistoryForKey(String key) {
		return new QueryResultsIteratorImpl<IKeyModification>(this.handler, getGroupId(), getTxId(),
				handler.getHistoryForKey(getGroupId(), getTxId(), key),
				queryResultBytesToKeyModification.andThen(KeyModification::new)
				);
	}

	private Function<SmartcontractShim.QueryResultBytes, KvQueryResult.KeyModification> queryResultBytesToKeyModification = new Function<SmartcontractShim.QueryResultBytes, KvQueryResult.KeyModification>() {
		public KvQueryResult.KeyModification apply(SmartcontractShim.QueryResultBytes queryResultBytes) {
			try {
				return KvQueryResult.KeyModification.parseFrom(queryResultBytes.getResultBytes());
			} catch (InvalidProtocolBufferException e) {
				throw new RuntimeException(e);
			}
		};
	};

	@Override
	public Response invokeSmartContract(final String chaincodeName, final List<byte[]> args, final String channel) {
		// internally we handle chaincode name as a composite name
		final String compositeName;
		if (channel != null && channel.trim().length() > 0) {
			compositeName = chaincodeName + "/" + channel;
		} else {
			compositeName = chaincodeName;
		}
		return handler.invokeSmartContract(this.groupId, this.txId, compositeName, args);
	}

	@Override
	public ProposalPackage.SignedProposal getSignedProposal() {
		return signedProposal;
	}

	@Override
	public Instant getTxTimestamp() {
		return txTimestamp;
	}

	@Override
	public byte[] getCreator() {
		if(creator == null) return null;
		return creator.toByteArray();
	}

	@Override
	public Map<String, byte[]> getTransient() {
		return transientMap.entrySet().stream().collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue().toByteArray()));
	}

	@Override
	public byte[] getBinding() {
		return this.binding;
	}
}
