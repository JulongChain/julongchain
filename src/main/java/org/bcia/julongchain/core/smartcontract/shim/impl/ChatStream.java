/*
 * Copyright IBM Corp., DTCC All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.bcia.julongchain.core.smartcontract.shim.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.protos.node.SmartContractSupportGrpc;
import org.bcia.julongchain.protos.node.SmartContractShim;

public class ChatStream implements StreamObserver<SmartContractShim.SmartContractMessage> {

	private Log logger = LogFactory.getLog(ChatStream.class);

	private final ManagedChannel connection;
	private final Handler handler;
	private StreamObserver<SmartContractShim.SmartContractMessage> streamObserver;

	public ChatStream(ManagedChannel connection, ISmartContract smartcontract) {
		// Establish stream with validating peer
		SmartContractSupportGrpc.SmartContractSupportStub stub = SmartContractSupportGrpc.newStub(connection);

		logger.info("Connecting to peer.");

		try {
			this.streamObserver = stub.register(this);
		} catch (Exception e) {
			logger.error("Unable to connect to peer server", e);
			System.exit(-1);
		}
		this.connection = connection;

		// Create the org.hyperledger.fabric.shim handler responsible for all
		// control logic
		this.handler = new Handler(this, smartcontract);
	}

	public synchronized void serialSend(SmartContractShim.SmartContractMessage message) {
			logger.info(String.format("[%-8s]Sending %s message to peer.", message.getTxid(), message.getType()));
			logger.info(String.format("[%-8s]SmartContractMessage: %s", message.getTxid(), toJsonString(message)));
		try {
			this.streamObserver.onNext(message);
			logger.info(String.format("[%-8s]%s message sent.", message.getTxid(), message.getType()));
		} catch (Exception e) {
			logger.info(String.format("[%-8s]Error sending %s: %s", message.getTxid(), message.getType(), e));
			throw new RuntimeException(String.format("Error sending %s: %s", message.getType(), e));
		}
	}

	@Override
	public void onNext(SmartContractShim.SmartContractMessage message) {
			logger.info("Got message from peer: " + toJsonString(message));
		try {
			logger.info(String.format("[%-8s]Received message %s from org.hyperledger.fabric.shim", message.getTxid(), message.getType()));
			handler.handleMessage(message);
		} catch (Exception e) {
			System.exit(-1);
		}
	}

	@Override
	public void onError(Throwable e) {
		logger.error("Unable to connect to peer server: " + e.getMessage());
		System.exit(-1);
	}

	@Override
	public void onCompleted() {
		connection.shutdown();
		handler.nextState.close();
	}

	static String toJsonString(SmartContractShim.SmartContractMessage message) {
		try {
			String result =  JsonFormat.printer().print(message);
			int start = 0;
			int end = result.length();
			if(end > 100) {
				end = 100;
			}
      return StringUtils.substring(result, start, end);
		} catch (InvalidProtocolBufferException e) {
			return String.format("{ Type: %s, TxId: %s }", message.getType(), message.getTxid());
		}
	}

	public void receive() throws Exception {
		NextStateInfo nsInfo = handler.nextState.take();
		logger.info(nsInfo.toString());
		SmartContractShim.SmartContractMessage message = nsInfo.message;
		onNext(message);

		// keepalive messages are PONGs to the fabric's PINGs
		if (nsInfo.sendToSC || message.getType() == SmartContractShim.SmartContractMessage.Type.KEEPALIVE) {
			if (message.getType() == SmartContractShim.SmartContractMessage.Type.KEEPALIVE) {
				logger.info("Sending KEEPALIVE response");
			} else {
				logger.info(String.format("[%-8s]Send state message %s", message.getTxid(), message.getType()));
			}
			serialSend(message);
		}
	}
}
