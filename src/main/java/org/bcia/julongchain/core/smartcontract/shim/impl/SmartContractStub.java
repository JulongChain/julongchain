/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.bcia.julongchain.core.smartcontract.shim.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.smartcontract.shim.ledger.CompositeKey;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyModification;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IKeyValue;
import org.bcia.julongchain.core.smartcontract.shim.ledger.IQueryResultsIterator;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Common.Header;
import org.bcia.julongchain.protos.common.Common.HeaderType;
import org.bcia.julongchain.protos.common.Common.SignatureHeader;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult.KV;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalPackage.Proposal;
import org.bcia.julongchain.protos.node.ProposalPackage.SignedProposal;
import org.bcia.julongchain.protos.node.SmartContractEventPackage;
import org.bcia.julongchain.protos.node.SmartContractShim;

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

class SmartContractStub implements ISmartContractStub {

	private static final String UNSPECIFIED_KEY = new String(Character.toChars(0x000001));
	private final String channelId;
	private final String txId;
	private final Handler handler;
	private final List<ByteString> args;
	private final SignedProposal signedProposal;
	private final Instant txTimestamp;
	private final ByteString creator;
	private final Map<String, ByteString> transientMap;
	private final byte[] binding;
	private SmartContractEventPackage.SmartContractEvent event;

	SmartContractStub(String channelId, String txId, Handler handler, List<ByteString> args, SignedProposal signedProposal) {
		this.channelId = channelId;
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
				final Proposal proposal = Proposal.parseFrom(signedProposal.getProposalBytes());
				final Header header = Header.parseFrom(proposal.getHeader());
				final Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(header.getGroupHeader());
				validateProposalType(groupHeader);
				final SignatureHeader signatureHeader = SignatureHeader.parseFrom(header.getSignatureHeader());
				final ProposalPackage.SmartContractProposalPayload smartcontractProposalPayload = ProposalPackage.SmartContractProposalPayload.parseFrom(proposal.getPayload());
				final Timestamp timestamp = groupHeader.getTimestamp();

				this.txTimestamp = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
				this.creator = signatureHeader.getCreator();
				this.transientMap = smartcontractProposalPayload.getTransientMapMap();
				this.binding = computeBinding(groupHeader, signatureHeader);
			} catch (InvalidProtocolBufferException | NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private byte[] computeBinding(final Common.GroupHeader groupHeader, final SignatureHeader signatureHeader) throws NoSuchAlgorithmException {
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(signatureHeader.getNonce().asReadOnlyByteBuffer());
		messageDigest.update(this.creator.asReadOnlyByteBuffer());
		final ByteBuffer epochBytes = ByteBuffer.allocate(Long.BYTES)
				.order(ByteOrder.LITTLE_ENDIAN)
				.putLong(groupHeader.getEpoch());
		epochBytes.flip();
		messageDigest.update(epochBytes);
		return messageDigest.digest();
	}

	private void validateProposalType(Common.GroupHeader groupHeader) {
		switch (Common.HeaderType.forNumber(groupHeader.getType())) {
		case ENDORSER_TRANSACTION:
		case CONFIG:
			return;
		default:
			throw new RuntimeException(String.format("Unexpected transaction type: %s", HeaderType.forNumber(groupHeader.getType())));
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
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Event name cannot be null or empty string.");
		}
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
		return channelId;
	}

	@Override
	public String getTxId() {
		return txId;
	}

	@Override
	public byte[] getState(String key) {
		return handler.getState(channelId, txId, key).toByteArray();
	}

	@Override
	public void putState(String key, byte[] value) {
		if(key == null) {
			throw new NullPointerException("key cannot be null");
		}
		if(key.length() == 0) {
			throw new IllegalArgumentException("key cannot not be an empty string");
		}
		handler.putState(channelId, txId, key, ByteString.copyFrom(value));
	}

	@Override
	public void delState(String key) {
		handler.deleteState(channelId, txId, key);
	}

	@Override
	public IQueryResultsIterator<IKeyValue> getStateByRange(String startKey, String endKey) {
		if (startKey == null || startKey.isEmpty()) {
			startKey = UNSPECIFIED_KEY;
		}
		if (endKey == null || endKey.isEmpty()) {
			endKey = UNSPECIFIED_KEY;
		}
		CompositeKey.validateSimpleKeys(startKey, endKey);

		return new QueryResultsIterator<IKeyValue>(this.handler, getGroupId(), getTxId(),
				handler.getStateByRange(getGroupId(), getTxId(), startKey, endKey),
				queryResultBytesToKv.andThen(KeyValue::new)
				);
	}

	private Function<SmartContractShim.QueryResultBytes, KV> queryResultBytesToKv = new Function<SmartContractShim.QueryResultBytes, KV>() {
		public KV apply(SmartContractShim.QueryResultBytes queryResultBytes) {
			try {
				return KV.parseFrom(queryResultBytes.getResultBytes());
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
		return new QueryResultsIterator<IKeyValue>(this.handler, getGroupId(), getTxId(),
				handler.getQueryResult(getGroupId(), getTxId(), query),
				queryResultBytesToKv.andThen(KeyValue::new)
				);
	}

	@Override
	public IQueryResultsIterator<IKeyModification> getHistoryForKey(String key) {
		return new QueryResultsIterator<IKeyModification>(this.handler, getGroupId(), getTxId(),
				handler.getHistoryForKey(getGroupId(), getTxId(), key),
				queryResultBytesToKeyModification.andThen(KeyModification::new)
				);
	}

	private Function<SmartContractShim.QueryResultBytes, KvQueryResult.KeyModification> queryResultBytesToKeyModification = new Function<SmartContractShim.QueryResultBytes, KvQueryResult.KeyModification>() {
		public KvQueryResult.KeyModification apply(SmartContractShim.QueryResultBytes queryResultBytes) {
			try {
				return KvQueryResult.KeyModification.parseFrom(queryResultBytes.getResultBytes());
			} catch (InvalidProtocolBufferException e) {
				throw new RuntimeException(e);
			}
		};
	};

	@Override
	public ISmartContract.SmartContractResponse invokeSmartContract(final String smartcontractName, final List<byte[]> args, final String channel) {
		// internally we handle smartcontract name as a composite name
		final String compositeName;
		if (channel != null && channel.trim().length() > 0) {
			compositeName = smartcontractName + "/" + channel;
		} else {
			compositeName = smartcontractName;
		}
		return handler.invokeSmartContract(this.channelId, this.txId, compositeName, args);
	}

	@Override
	public SignedProposal getSignedProposal() {
		return signedProposal;
	}

	@Override
	public Instant getTxTimestamp() {
		return txTimestamp;
	}

	@Override
	public byte[] getCreator() {
		if(creator == null) {
			return null;
		}
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
