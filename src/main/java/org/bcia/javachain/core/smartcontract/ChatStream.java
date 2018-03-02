/*
 * Copyright IBM Corp., DTCC All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 * Modified by Dingxuan sunianle on 2018-03-01
 */

package org.bcia.javachain.core.smartcontract;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.node.SmartContractSupportGrpc;
import org.bcia.javachain.protos.node.SmartcontractShim;


import static java.lang.String.format;

public class ChatStream implements StreamObserver<SmartcontractShim.SmartContractMessage> {

	private static final JavaChainLog log = JavaChainLogFactory.getLog(ChatStream.class);

	private final ManagedChannel connection;
	private final Handler handler;
	private StreamObserver<SmartcontractShim.SmartContractMessage> streamObserver;

	public ChatStream(ManagedChannel connection, ISmartContract smartcontract) {
		// Establish stream with validating peer
		SmartContractSupportGrpc.SmartContractSupportStub stub = SmartContractSupportGrpc.newStub(connection);

		log.info("Connecting to peer.");

		try {
			this.streamObserver = stub.register(this);
		} catch (Exception e) {
			log.error("Unable to connect to peer server", e);
			System.exit(-1);
		}
		this.connection = connection;

		// Create the org.hyperledger.fabric.shim handler responsible for all
		// control logic
		this.handler = new Handler(this, smartcontract);
	}

	public synchronized void serialSend(SmartcontractShim.SmartContractMessage message) {
		if(log.isDebugEnabled()) {
			log.debug(format("[%-8s]Sending %s message to peer.", message.getTxid(), message.getType()));
		}
		if (log.isTraceEnabled()) {
			log.trace(format("[%-8s]ChaincodeMessage: %s", message.getTxid(), toJsonString(message)));
		}
		try {
			this.streamObserver.onNext(message);
			if (log.isTraceEnabled()) {
				log.trace(format("[%-8s]%s message sent.", message.getTxid(), message.getType()));
			}
		} catch (Exception e) {
			log.error(String.format("[%-8s]Error sending %s: %s", message.getTxid(), message.getType(), e));
			throw new RuntimeException(format("Error sending %s: %s", message.getType(), e));
		}
	}

	@Override
	public void onNext(SmartcontractShim.SmartContractMessage message) {
		if(log.isDebugEnabled()) {
			log.debug("Got message from peer: " + toJsonString(message));
		}
		try {
			if(log.isDebugEnabled()) {
				log.debug(String.format("[%-8s]Received message %s from org.hyperledger.fabric.shim", message.getTxid(), message.getType()));
			}
			handler.handleMessage(message);
		} catch (Exception e) {
			log.error(String.format("[%-8s]Error handling message %s: %s", message.getTxid(), message.getType(), e));
			System.exit(-1);
		}
	}

	@Override
	public void onError(Throwable e) {
		log.error("Unable to connect to peer server: " + e.getMessage());
		System.exit(-1);
	}

	@Override
	public void onCompleted() {
		connection.shutdown();
		handler.nextState.close();
	}

	static String toJsonString(SmartcontractShim.SmartContractMessage message) {
		try {
			return JsonFormat.printer().print(message);
		} catch (InvalidProtocolBufferException e) {
			return String.format("{ Type: %s, TxId: %s }", message.getType(), message.getTxid());
		}
	}

	public void receive() throws Exception {
		NextStateInfo nsInfo = handler.nextState.take();
		SmartcontractShim.SmartContractMessage message = nsInfo.message;
		onNext(message);

		// keepalive messages are PONGs to the fabric's PINGs
		if (nsInfo.sendToCC || message.getType() == SmartcontractShim.SmartContractMessage.Type.KEEPALIVE) {
			if (message.getType() == SmartcontractShim.SmartContractMessage.Type.KEEPALIVE) {
				log.info("Sending KEEPALIVE response");
			} else {
				log.info(String.format("[%-8s]Send state message %s", message.getTxid(), message.getType()));
			}
			serialSend(message);
		}
	}
}
