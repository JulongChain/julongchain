/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

Modified by Dingxuan sunianle on 2018-03-01
*/

package org.bcia.javachain.core.smartcontract;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.javachain.core.smartcontract.fsm.CBDesc;
import org.bcia.javachain.core.smartcontract.fsm.Event;
import org.bcia.javachain.core.smartcontract.fsm.EventDesc;
import org.bcia.javachain.core.smartcontract.fsm.FSM;
import org.bcia.javachain.core.smartcontract.fsm.exceptions.CancelledException;
import org.bcia.javachain.core.smartcontract.fsm.exceptions.NoTransitionException;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.SmartContractEventPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.SmartcontractShim;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.lang.String.format;
import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage.Type.*;
import static org.bcia.javachain.core.smartcontract.fsm.CallbackType.AFTER_EVENT;
import static org.bcia.javachain.core.smartcontract.fsm.CallbackType.BEFORE_EVENT;


public class Handler {

	private static Log logger = LogFactory.getLog(Handler.class);

	private ChatStream chatStream;
	private ISmartContract chaincode;

	private Map<String, Boolean> isTransaction;
	private Map<String, Channel<SmartcontractShim.SmartContractMessage>> responseChannel;
	Channel<NextStateInfo> nextState;

	private FSM fsm;

	public Handler(ChatStream chatStream, ISmartContract chaincode) {
		this.chatStream = chatStream;
		this.chaincode = chaincode;

		responseChannel = new HashMap<String, Channel<SmartcontractShim.SmartContractMessage>>();
		isTransaction = new HashMap<String, Boolean>();
		nextState = new Channel<NextStateInfo>();

		fsm = new FSM("created");

		fsm.addEvents(
				//            Event Name              From           To
				new EventDesc(REGISTERED.toString(),  "created",     "established"),
				new EventDesc(READY.toString(),       "established", "ready"),
				new EventDesc(ERROR.toString(),       "init",        "established"),
				new EventDesc(RESPONSE.toString(),    "init",        "init"),
				new EventDesc(INIT.toString(),        "ready",       "ready"),
				new EventDesc(TRANSACTION.toString(), "ready",       "ready"),
				new EventDesc(RESPONSE.toString(),    "ready",       "ready"),
				new EventDesc(ERROR.toString(),       "ready",       "ready"),
				new EventDesc(COMPLETED.toString(),   "init",        "ready"),
				new EventDesc(COMPLETED.toString(),   "ready",       "ready")
				);

		fsm.addCallbacks(
				//         Type          Trigger                Callback
				new CBDesc(BEFORE_EVENT, REGISTERED.toString(), (event) -> beforeRegistered(event)),
				new CBDesc(AFTER_EVENT,  RESPONSE.toString(),   (event) -> afterResponse(event)),
				new CBDesc(AFTER_EVENT,  ERROR.toString(),      (event) -> afterError(event)),
				new CBDesc(BEFORE_EVENT, INIT.toString(),       (event) -> beforeInit(event)),
				new CBDesc(BEFORE_EVENT, TRANSACTION.toString(),(event) -> beforeTransaction(event))
				);
	}

	private String getTxKey(final String channelId, final String txid) {
		return channelId+txid;
	}

	private void triggerNextState(SmartcontractShim.SmartContractMessage message, boolean send) {
		if (logger.isTraceEnabled()) logger.trace("triggerNextState for message " + message);
		nextState.add(new NextStateInfo(message, send));
	}

	private synchronized Channel<SmartcontractShim.SmartContractMessage> aquireResponseChannelForTx(final String channelId, final String txId) {
		final Channel<SmartcontractShim.SmartContractMessage> channel = new Channel<>();
		String key = getTxKey(channelId, txId);
		if (this.responseChannel.putIfAbsent(key, channel) != null) {
			throw new IllegalStateException(format("[%-8s]Response channel already exists. Another request must be pending.", txId));
		}
		if (logger.isTraceEnabled()) logger.trace(format("[%-8s]Response channel created.", txId));
		return channel;
	}

	private synchronized void sendChannel(SmartcontractShim.SmartContractMessage message) {
		String key = getTxKey(message.getChannelId(), message.getTxid());
		if (!responseChannel.containsKey(key)) {
			throw new IllegalStateException(format("[%-8s]sendChannel does not exist", message.getTxid()));
		}

		logger.debug(String.format("[%-8s]Before send", message.getTxid()));
		responseChannel.get(key).add(message);
		logger.debug(String.format("[%-8s]After send", message.getTxid()));
	}

	private SmartcontractShim.SmartContractMessage receiveChannel(Channel<SmartcontractShim.SmartContractMessage> channel) {
		try {
			return channel.take();
		} catch (InterruptedException e) {
			logger.debug("channel.take() failed with InterruptedException");

			// Channel has been closed?
			// TODO
			return null;
		}
	}

	private synchronized void releaseResponseChannelForTx(String channelId, String txId) {
		String key = getTxKey(channelId, txId);
		final Channel<SmartcontractShim.SmartContractMessage> channel = responseChannel.remove(key);
		if (channel != null) channel.close();
		if (logger.isTraceEnabled()) logger.trace(format("[%-8s]Response channel closed.",txId));
	}

	/**
	 * Marks a CHANNELID+UUID as either a transaction or a query
	 *
	 * @param uuid
	 *            ID to be marked
	 * @param isTransaction
	 *            true for transaction, false for query
	 * @return whether or not the UUID was successfully marked
	 */
	private synchronized boolean markIsTransaction(String channelId, String uuid, boolean isTransaction) {
		if (this.isTransaction == null) {
			return false;
		}

		String key = getTxKey(channelId, uuid);
		this.isTransaction.put(key, isTransaction);
		return true;
	}

	private synchronized void deleteIsTransaction(String channelId, String uuid) {
		String key = getTxKey(channelId, uuid);
		isTransaction.remove(key);
	}

	private void beforeRegistered(Event event) {
		extractMessageFromEvent(event);
		logger.debug(String.format("Received %s, ready for invocations", REGISTERED));
	}

	/**
	 * Handles requests to initialize chaincode
	 *
	 * @param message
	 *            chaincode to be initialized
	 */
	private void handleInit(SmartcontractShim.SmartContractMessage message) {
		new Thread(() -> {
			try {

				// Get the function and args from Payload
				final Smartcontract.SmartContractInput input = Smartcontract.SmartContractInput.parseFrom(message.getPayload());

				// Mark as a transaction (allow put/del state)
				markIsTransaction(message.getChannelId(), message.getTxid(), true);

				// Create the ChaincodeStub which the chaincode can use to
				// callback
				final ISmartContractStub stub = new SmartContractStub(message.getChannelId(), message.getTxid(), this, input.getArgsList(), message.getProposal());

				// Call chaincode's init
				final Response result = chaincode.init(stub);

				if (result.getStatus().getCode() >= Response.Status.INTERNAL_SERVER_ERROR.getCode()) {
					// Send ERROR with entire result.Message as payload
					logger.error(String.format("[%-8s]Init failed. Sending %s", message.getTxid(), ERROR));
					triggerNextState(newErrorEventMessage(message.getChannelId(), message.getTxid(), result.getMessage(), stub.getEvent()), true);
				} else {
					// Send COMPLETED with entire result as payload
					logger.debug(String.format(String.format("[%-8s]Init succeeded. Sending %s", message.getTxid(), COMPLETED)));
					triggerNextState(newCompletedEventMessage(message.getChannelId(), message.getTxid(), result, stub.getEvent()), true);
				}

			} catch (InvalidProtocolBufferException | RuntimeException e) {
				logger.error(String.format("[%-8s]Init failed. Sending %s", message.getTxid(), ERROR), e);
				triggerNextState(newErrorEventMessage(message.getChannelId(), message.getTxid(), e), true);
			} finally {
				// delete isTransaction entry
				deleteIsTransaction(message.getChannelId(), message.getTxid());
			}
		}).start();
	}

	// enterInitState will initialize the chaincode if entering init from established.
	private void beforeInit(Event event) {
		logger.debug(String.format("Before %s event.", event.name));
		logger.debug(String.format("Current state %s", fsm.current()));
		final SmartcontractShim.SmartContractMessage message = extractMessageFromEvent(event);
		logger.debug(String.format("[%-8s]Received %s, initializing chaincode", message.getTxid(), message.getType()));
		if (message.getType() == INIT) {
			// Call the chaincode's Run function to initialize
			handleInit(message);
		}
	}

	// handleTransaction Handles request to execute a transaction.
	private void handleTransaction(SmartcontractShim.SmartContractMessage message) {
		new Thread(() -> {
			try {

				// Get the function and args from Payload
				final Smartcontract.SmartContractInput input = Smartcontract.SmartContractInput.parseFrom(message.getPayload());

				// Mark as a transaction (allow put/del state)
				markIsTransaction(message.getChannelId(), message.getTxid(), true);

				// Create the ChaincodeStub which the chaincode can use to
				// callback
				final ISmartContractStub stub = new SmartContractStub(message.getChannelId(), message.getTxid(), this, input.getArgsList(), message.getProposal());

				// Call chaincode's invoke
				final Response result = chaincode.invoke(stub);

				if (result.getStatus().getCode() >= Response.Status.INTERNAL_SERVER_ERROR.getCode()) {
					// Send ERROR with entire result.Message as payload
					logger.error(String.format("[%-8s]Invoke failed. Sending %s", message.getTxid(), ERROR));
					triggerNextState(newErrorEventMessage(message.getChannelId(), message.getTxid(), result.getMessage(), stub.getEvent()), true);
				} else {
					// Send COMPLETED with entire result as payload
					logger.debug(String.format(String.format("[%-8s]Invoke succeeded. Sending %s", message.getTxid(), COMPLETED)));
					triggerNextState(newCompletedEventMessage(message.getChannelId(), message.getTxid(), result, stub.getEvent()), true);
				}

			} catch (InvalidProtocolBufferException | RuntimeException e) {
				logger.error(String.format("[%-8s]Invoke failed. Sending %s", message.getTxid(), ERROR), e);
				triggerNextState(newErrorEventMessage(message.getChannelId(), message.getTxid(), e), true);
			} finally {
				// delete isTransaction entry
				deleteIsTransaction(message.getChannelId(), message.getTxid());
			}
		}).start();
	}

	// enterTransactionState will execute chaincode's Run if coming from a TRANSACTION event.
	private void beforeTransaction(Event event) {
		SmartcontractShim.SmartContractMessage message = extractMessageFromEvent(event);
		logger.debug(String.format("[%-8s]Received %s, invoking transaction on chaincode(src:%s, dst:%s)", message.getTxid(), message.getType().toString(), event.src, event.dst));
		if (message.getType() == TRANSACTION) {
			// Call the chaincode's Run function to invoke transaction
			handleTransaction(message);
		}
	}

	// afterResponse is called to deliver a response or error to the chaincode stub.
	private void afterResponse(Event event) {
		SmartcontractShim.SmartContractMessage message = extractMessageFromEvent(event);
		try {
			sendChannel(message);
			logger.debug(String.format("[%-8s]Received %s, communicated (state:%s)", message.getTxid(), message.getType(), fsm.current()));
		} catch (Exception e) {
			logger.error(String.format("[%-8s]error sending %s (state:%s): %s", message.getTxid(), message.getType(), fsm.current(), e));
		}
	}

	private SmartcontractShim.SmartContractMessage extractMessageFromEvent(Event event) {
		try {
			return (SmartcontractShim.SmartContractMessage) event.args[0];
		} catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
			final RuntimeException error = new RuntimeException("No chaincode message found in event.", e);
			event.cancel(error);
			throw error;
		}
	}

	private void afterError(Event event) {
		SmartcontractShim.SmartContractMessage message = extractMessageFromEvent(event);
		/*
		 * TODO- revisit. This may no longer be needed with the
		 * serialized/streamlined messaging model There are two situations in
		 * which the ERROR event can be triggered:
		 *
		 * 1. When an error is encountered within handleInit or
		 * handleTransaction - some issue at the chaincode side; In this case
		 * there will be no responseChannel and the message has been sent to the
		 * validator.
		 *
		 * 2. The chaincode has initiated a request (get/put/del state) to the
		 * validator and is expecting a response on the responseChannel; If
		 * ERROR is received from validator, this needs to be notified on the
		 * responseChannel.
		 */
		try {
			sendChannel(message);
		} catch (Exception e) {
			logger.debug(String.format("[%-8s]Error received from validator %s, communicated(state:%s)", message.getTxid(), message.getType(), fsm.current()));
		}
	}

	// handleGetState communicates with the validator to fetch the requested state information from the ledger.
	ByteString getState(String channelId, String txId, String key) {
		return invokeChaincodeSupport(newGetStateEventMessage(channelId, txId, key));
	}

	private boolean isTransaction(String channelId, String uuid) {
		String key = getTxKey(channelId, uuid);
		return isTransaction.containsKey(key) && isTransaction.get(key);
	}

	void putState(String channelId, String txId, String key, ByteString value) {
		logger.debug(format("[%-8s]Inside putstate (\"%s\":\"%s\"), isTransaction = %s", txId, key, value, isTransaction(channelId, txId)));
		if (!isTransaction(channelId, txId)) throw new IllegalStateException("Cannot put state in query context");
		invokeChaincodeSupport(newPutStateEventMessage(channelId, txId, key, value));
	}

	void deleteState(String channelId, String txId, String key) {
		if (!isTransaction(channelId, txId)) throw new RuntimeException("Cannot del state in query context");
		invokeChaincodeSupport(newDeleteStateEventMessage(channelId, txId, key));
	}

	SmartcontractShim.QueryResponse getStateByRange(String channelId, String txId, String startKey, String endKey) {
		return invokeQueryResponseMessage(channelId, txId, GET_STATE_BY_RANGE, SmartcontractShim.GetStateByRange.newBuilder()
				.setStartKey(startKey)
				.setEndKey(endKey)
				.build().toByteString());
	}

	SmartcontractShim.QueryResponse queryStateNext(String channelId, String txId, String queryId) {
		return invokeQueryResponseMessage(channelId, txId, QUERY_STATE_NEXT, SmartcontractShim.QueryStateNext.newBuilder()
				.setId(queryId)
				.build().toByteString());
	}

	void queryStateClose(String channelId, String txId, String queryId) {
		invokeQueryResponseMessage(channelId, txId, QUERY_STATE_CLOSE, SmartcontractShim.QueryStateClose.newBuilder()
				.setId(queryId)
				.build().toByteString());
	}

    SmartcontractShim.QueryResponse getQueryResult(String channelId, String txId, String query) {
		return invokeQueryResponseMessage(channelId, txId, GET_QUERY_RESULT, SmartcontractShim.GetQueryResult.newBuilder()
				.setQuery(query)
				.build().toByteString());
	}

    SmartcontractShim.QueryResponse getHistoryForKey(String channelId, String txId, String key) {
		return invokeQueryResponseMessage(channelId, txId, SmartcontractShim.SmartContractMessage.Type.GET_HISTORY_FOR_KEY, SmartcontractShim.GetQueryResult.newBuilder()
				.setQuery(key)
				.build().toByteString());
	}

	private SmartcontractShim.QueryResponse invokeQueryResponseMessage(String channelId, String txId, SmartcontractShim.SmartContractMessage.Type type, ByteString payload) {
		try {
			return SmartcontractShim.QueryResponse.parseFrom(invokeChaincodeSupport(newEventMessage(type, channelId, txId, payload)));
		} catch (InvalidProtocolBufferException e) {
			logger.error(String.format("[%-8s]unmarshall error", txId));
			throw new RuntimeException("Error unmarshalling QueryResponse.", e);
		}
	}

	private ByteString invokeChaincodeSupport(final SmartcontractShim.SmartContractMessage message) {
		final String channelId = message.getChannelId();
		final String txId = message.getTxid();

		try {
			// create a new response channel
			Channel<SmartcontractShim.SmartContractMessage> responseChannel = aquireResponseChannelForTx(channelId, txId);

			// send the message
			chatStream.serialSend(message);

			// wait for response
			final SmartcontractShim.SmartContractMessage response = receiveChannel(responseChannel);
			logger.debug(format("[%-8s]%s response received.", txId, response.getType()));

			// handle response
			switch (response.getType()) {
			case RESPONSE:
				logger.debug(format("[%-8s]Successful response received.", txId));
				return response.getPayload();
			case ERROR:
				logger.error(format("[%-8s]Unsuccessful response received.", txId));
				throw new RuntimeException(format("[%-8s]Unsuccessful response received.", txId));
			default:
				logger.error(format("[%-8s]Unexpected %s response received. Expected %s or %s.", txId, response.getType(), RESPONSE, ERROR));
				throw new RuntimeException(format("[%-8s]Unexpected %s response received. Expected %s or %s.", txId, response.getType(), RESPONSE, ERROR));
			}
		} finally {
			releaseResponseChannelForTx(channelId, txId);
		}
	}

	Response invokeChaincode(String channelId, String txId, String chaincodeName, List<byte[]> args) {
		try {
			// create invocation specification of the chaincode to invoke
			final Smartcontract.SmartContractSpec invocationSpec = Smartcontract.SmartContractSpec.newBuilder()
					.setSmartContractId(Smartcontract.SmartContractID.newBuilder()
							.setName(chaincodeName)
							.build())
					.setInput(Smartcontract.SmartContractInput.newBuilder()
							.addAllArgs(args.stream().map(ByteString::copyFrom).collect(Collectors.toList()))
							.build())
					.build();

			// invoke other chaincode
			final ByteString payload = invokeChaincodeSupport(newInvokeChaincodeMessage(channelId, txId, invocationSpec.toByteString()));

			// response message payload should be yet another chaincode
			// message (the actual response message)
			final SmartcontractShim.SmartContractMessage responseMessage = SmartcontractShim.SmartContractMessage.parseFrom(payload);
			// the actual response message must be of type COMPLETED
			logger.debug(String.format("[%-8s]%s response received from other chaincode.", txId, responseMessage.getType()));
			if (responseMessage.getType() == COMPLETED) {
				// success
				return toChaincodeResponse(ProposalResponsePackage.Response.parseFrom(responseMessage.getPayload()));
			} else {
				// error
				return newErrorChaincodeResponse(responseMessage.getPayload().toStringUtf8());
			}
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}
	}

	// handleMessage message handles loop for org.hyperledger.fabric.shim side
	// of chaincode/validator stream.
	public synchronized void handleMessage(SmartcontractShim.SmartContractMessage message) throws Exception {

		if (message.getType() == SmartcontractShim.SmartContractMessage.Type.KEEPALIVE) {
			logger.debug(String.format("[%-8s] Recieved KEEPALIVE message, do nothing", message.getTxid()));
			// Received a keep alive message, we don't do anything with it for
			// now and it does not touch the state machine
			return;
		}

		logger.debug(String.format("[%-8s]Handling ChaincodeMessage of type: %s(state:%s)", message.getTxid(), message.getType(), fsm.current()));

		if (fsm.eventCannotOccur(message.getType().toString())) {
			String errStr = String.format("[%s]Chaincode handler org.hyperledger.fabric.shim.fsm cannot handle message (%s) with payload size (%d) while in state: %s", message.getTxid(), message.getType(), message.getPayload().size(), fsm.current());
			chatStream.serialSend(newErrorEventMessage(message.getChannelId(), message.getTxid(), errStr));
			throw new RuntimeException(errStr);
		}

		// Filter errors to allow NoTransitionError and CanceledError
		// to not propagate for cases where embedded Err == nil.
		try {
			fsm.raiseEvent(message.getType().toString(), message);
		} catch (NoTransitionException e) {
			if (e.error != null) throw e;
			logger.debug(format("[%-8s]Ignoring NoTransitionError", message.getTxid()));
		} catch (CancelledException e) {
			if (e.error != null) throw e;
			logger.debug(format("[%-8s]Ignoring CanceledError", message.getTxid()));
		}
	}

	private static String toJsonString(SmartcontractShim.SmartContractMessage message) {
		try {
			return JsonFormat.printer().print(message);
		} catch (InvalidProtocolBufferException e) {
			return String.format("{ Type: %s, TxId: %s }", message.getType(), message.getTxid());
		}
	}

	private static Response newErrorChaincodeResponse(String message) {
		return new Response(Response.Status.INTERNAL_SERVER_ERROR, message, null);
	}

	private static SmartcontractShim.SmartContractMessage newGetStateEventMessage(final String channelId, final String txId, final String key) {
		return newEventMessage(GET_STATE, channelId, txId, ByteString.copyFromUtf8(key));
	}

	private static SmartcontractShim.SmartContractMessage newPutStateEventMessage(final String channelId, final String txId, final String key, final ByteString value) {
		return newEventMessage(PUT_STATE, channelId, txId, SmartcontractShim.PutStateInfo.newBuilder()
				.setKey(key)
				.setValue(value)
				.build().toByteString());
	}

	private static SmartcontractShim.SmartContractMessage newDeleteStateEventMessage(final String channelId, final String txId, final String key) {
		return newEventMessage(DEL_STATE, channelId, txId, ByteString.copyFromUtf8(key));
	}

	private static SmartcontractShim.SmartContractMessage newErrorEventMessage(final String channelId, final String txId, final Throwable throwable) {
		return newErrorEventMessage(channelId, txId, printStackTrace(throwable));
	}

	private static SmartcontractShim.SmartContractMessage newErrorEventMessage(final String channelId, final String txId, final String message) {
		return newErrorEventMessage(channelId, txId, message, null);
	}

	private static SmartcontractShim.SmartContractMessage newErrorEventMessage(final String channelId, final String txId, final String message, final SmartContractEventPackage.SmartContractEvent event) {
		return newEventMessage(ERROR, channelId, txId, ByteString.copyFromUtf8(message), event);
	}

	private static SmartcontractShim.SmartContractMessage newCompletedEventMessage(final String channelId, final String txId, final Response response, final SmartContractEventPackage.SmartContractEvent event) {
		return newEventMessage(COMPLETED, channelId, txId, toProtoResponse(response).toByteString(), event);
	}

	private static SmartcontractShim.SmartContractMessage newInvokeChaincodeMessage(final String channelId, final String txId, final ByteString payload) {
		return newEventMessage(INVOKE_CHAINCODE, channelId, txId, payload, null);
	}

	private static SmartcontractShim.SmartContractMessage newEventMessage(final SmartcontractShim.SmartContractMessage.Type type, final String channelId, final String txId, final ByteString payload) {
		return newEventMessage(type, channelId, txId, payload, null);
	}

	private static SmartcontractShim.SmartContractMessage newEventMessage(final SmartcontractShim.SmartContractMessage.Type type, final String channelId, final String txId, final ByteString payload, final SmartContractEventPackage.SmartContractEvent event) {
		if (event == null) {
			return SmartcontractShim.SmartContractMessage.newBuilder()
					.setType(type)
					.setChannelId(channelId)
					.setTxid(txId)
					.setPayload(payload)
					.build();
		} else {
			return SmartcontractShim.SmartContractMessage.newBuilder()
					.setType(type)
					.setChannelId(channelId)
					.setTxid(txId)
					.setPayload(payload)
					.setSmartcontractEvent(event)
					.build();
		}
	}

	private static ProposalResponsePackage.Response toProtoResponse(Response response) {
		final ProposalResponsePackage.Response.Builder builder = ProposalResponsePackage.Response.newBuilder();
		builder.setStatus(response.getStatus().getCode());
		if (response.getMessage() != null) builder.setMessage(response.getMessage());
		if (response.getPayload() != null) builder.setPayload(ByteString.copyFrom(response.getPayload()));
		return builder.build();
	}

	private static Response toChaincodeResponse(ProposalResponsePackage.Response response) {
		return new Response(
				Response.Status.forCode(response.getStatus()),
				response.getMessage(),
				response.getPayload() == null ? null : response.getPayload().toByteArray()
		);
	}

	private static String printStackTrace(Throwable throwable) {
		if (throwable == null) return null;
		final StringWriter buffer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(buffer));
		return buffer.toString();
	}

}
