/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.core.smartcontract.node;

import static org.bcia.javachain.core.smartcontract.node.SmartContractRunningUtil.SMART_CONTRACT_STATUS_BUSY;
import static org.bcia.javachain.core.smartcontract.node.SmartContractRunningUtil.SMART_CONTRACT_STATUS_ERROR;
import static org.bcia.javachain.core.smartcontract.node.SmartContractRunningUtil.SMART_CONTRACT_STATUS_NEW;
import static org.bcia.javachain.core.smartcontract.node.SmartContractRunningUtil.SMART_CONTRACT_STATUS_READY;
import static org.bcia.javachain.core.smartcontract.node.SmartContractRunningUtil.getSmartContractStauts;
import static org.bcia.javachain.core.smartcontract.node.SmartContractRunningUtil.updateSmartContractStatus;
import static org.bcia.javachain.core.smartcontract.node.TransactionRunningUtil.TX_STATUS_COMPLETE;
import static org.bcia.javachain.core.smartcontract.node.TransactionRunningUtil.TX_STATUS_ERROR;
import static org.bcia.javachain.core.smartcontract.node.TransactionRunningUtil.TX_STATUS_START;
import static org.bcia.javachain.core.smartcontract.node.TransactionRunningUtil.addTxId;
import static org.bcia.javachain.core.smartcontract.node.TransactionRunningUtil.getSmartContractIdByTxId;
import static org.bcia.javachain.core.smartcontract.node.TransactionRunningUtil.getTxStatusById;
import static org.bcia.javachain.core.smartcontract.node.TransactionRunningUtil.updateTxStatus;
import static org.bcia.javachain.protos.common.Common.GroupHeader;
import static org.bcia.javachain.protos.common.Common.Header;
import static org.bcia.javachain.protos.common.Common.HeaderType;
import static org.bcia.javachain.protos.node.ProposalPackage.Proposal;
import static org.bcia.javachain.protos.node.ProposalPackage.SignedProposal;
import static org.bcia.javachain.protos.node.SmartContractEventPackage.SmartContractEvent;
import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.javachain.protos.node.SmartContractSupportGrpc;
import org.bcia.javachain.protos.node.Smartcontract;

/**
 * 智能合约service，负责接收和处理gRPC消息
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportService
	extends SmartContractSupportGrpc.SmartContractSupportImplBase {

  private static Log logger = LogFactory.getLog(SmartContractSupportService.class);

  /** 以smartContractId为key,保存gRPC客户端 */
  private static Map<String, StreamObserver<SmartContractMessage>>
	  smartContractIdAndStreamObserverMap =
		  Collections.synchronizedMap(new HashMap<String, StreamObserver<SmartContractMessage>>());

  /**
   * 处理智能合约register信息（命令）
   *
   * @param message 智能合约发送过来的信息（命令）
   * @param streamObserver 智能合约gRPC通道
   */
  private void handleRegister(
	  SmartContractMessage message, StreamObserver<SmartContractMessage> streamObserver) {
	try {
	  // 保存智能合约编号
	  saveChaincodeStreamObserver(message, streamObserver);

	  String smartContractId = getSmartContractId(message);

	  // 发送注册成功命令
	  SmartContractMessage responseMessage =
		  SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.REGISTERED).build();
	  streamObserver.onNext(responseMessage);

	  // 发送ready命令
	  responseMessage =
		  SmartContractMessage.newBuilder().setType(SmartContractMessage.Type.READY).build();
	  streamObserver.onNext(responseMessage);

	  // 发送init命令
	  GroupHeader groupHeader =
		  GroupHeader.newBuilder().setType(HeaderType.ENDORSER_TRANSACTION.getNumber()).build();
	  Header header = Header.newBuilder().setGroupHeader(groupHeader.toByteString()).build();
	  Proposal proposal = Proposal.newBuilder().setHeader(header.toByteString()).build();
	  SignedProposal signedProposal =
		  SignedProposal.newBuilder().setProposalBytes(proposal.toByteString()).build();
	  SmartContractEvent smartcontractEvent =
		  SmartContractEvent.newBuilder().setSmartContractId(smartContractId).build();
	  responseMessage =
		  SmartContractMessage.newBuilder()
			  .setType(SmartContractMessage.Type.INIT)
			  .setProposal(signedProposal)
			  .setSmartcontractEvent(smartcontractEvent)
			  .build();
	  streamObserver.onNext(responseMessage);

	  // 设置状态send_init
	  updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_READY);

	} catch (InvalidProtocolBufferException e) {
	  logger.error(e.getMessage(), e);
	}
  }

  @Override
  public StreamObserver<SmartContractMessage> register(
	  StreamObserver<SmartContractMessage> responseObserver) {

	return new StreamObserver<SmartContractMessage>() {

	  @Override
	  public void onNext(SmartContractMessage message) {

		String txId = message.getTxid();
		String groupId = message.getGroupId();
		String smartContractId = getSmartContractIdByTxId(txId);

		logger.info(
			String.format(
				"Got message: smartContractId[%s] groupId[%s] txId[%s] messageStr[%s]",
				smartContractId, groupId, txId, message.toString()));

		logger.info(
			String.format(
				"smart contract status: smartContractId[%s] smartContractStatus[%s]",
				smartContractId, getSmartContractStauts(smartContractId)));

		logger.info(
			String.format(
				"transaction status: txId[%s] smartContractId[%s] txStatus[%s]",
				txId, smartContractId, getTxStatusById(txId)));

		// 收到error信息
		if (message.getType().equals(SmartContractMessage.Type.ERROR)) {
		  updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_ERROR);
		  updateTxStatus(txId, TX_STATUS_ERROR);
		  return;
		}

		// 收到register消息
		if (message.getType().equals(SmartContractMessage.Type.REGISTER)) {
		  handleRegister(message, responseObserver);
		  return;
		}

		// 收到complete信息
		if (message.getType().equals(SmartContractMessage.Type.COMPLETED)) {
		  updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_READY);
		  updateTxStatus(txId, TX_STATUS_COMPLETE);
		  return;
		}

		// 收到keepalive信息
		if (message.getType().equals(SmartContractMessage.Type.KEEPALIVE)) {
		  responseObserver.onNext(message);
		  return;
		}

		// 收到getState信息
		if (message.getType().equals(SmartContractMessage.Type.GET_STATE)) {

		  ByteString payload = message.getPayload();
		  logger.info("===============>" + payload.toStringUtf8());

		  SmartContractMessage responseMessage =
			  SmartContractMessage.newBuilder()
				  .mergeFrom(message)
				  .setType(SmartContractMessage.Type.RESPONSE)
				  .setPayload(ByteString.copyFrom("aaa".getBytes()))
				  .setTxid(txId)
				  .setGroupId(groupId)
				  .build();
		  responseObserver.onNext(responseMessage);
		  return;
		}

		// 收到putState信息
		if (message.getType().equals(SmartContractMessage.Type.PUT_STATE)) {
		  SmartContractMessage responseMessage =
			  SmartContractMessage.newBuilder()
				  .mergeFrom(message)
				  .setType(SmartContractMessage.Type.RESPONSE)
				  .setTxid(txId)
				  .setGroupId(groupId)
				  .build();
		  responseObserver.onNext(responseMessage);
		  return;
		}

		// 收到delState信息
		if (message.getType().equals(SmartContractMessage.Type.DEL_STATE)) {
		  return;
		}

		// 收到getHistoryForKey信息
		if (message.getType().equals(SmartContractMessage.Type.GET_HISTORY_FOR_KEY)) {
		  return;
		}
	  }

	  @Override
	  public void onError(Throwable throwable) {
		logger.error(throwable.getMessage(), throwable);
	  }

	  @Override
	  public void onCompleted() {
		logger.info("SmartContract completed");
	  }
	};
  }

  /**
   * 保存gRPC客户端
   *
   * @param message 接收到的消息
   * @param streamObserver gRPC客户端
   * @throws InvalidProtocolBufferException
   */
  private void saveChaincodeStreamObserver(
	  SmartContractMessage message, StreamObserver<SmartContractMessage> streamObserver)
	  throws InvalidProtocolBufferException {
	// 只有注册时才保存
	if (!message.getType().equals(SmartContractMessage.Type.REGISTER)) {
	  return;
	}
	// 从message的payload中获取smartContractID
	Smartcontract.SmartContractID smartContractID =
		Smartcontract.SmartContractID.parseFrom(message.getPayload());
	String name = smartContractID.getName();
	if (name == null || name.length() == 0) {
	  return;
	}
	// 保存gRPC客户端
	smartContractIdAndStreamObserverMap.put(name, streamObserver);
	// 设置状态为new
	updateSmartContractStatus(name, SMART_CONTRACT_STATUS_NEW);
	logger.info(
		String.format(
			"add SmartContract streamObserver: name[%s] streamObserver[%s]",
			name, streamObserver.toString()));
  }

  /**
   * 发送消息给gRPC客户端
   *
   * @param smartContractId 智能合约编号
   * @param message 消息
   */
  public static void send(String smartContractId, SmartContractMessage message) {
	String str = "";
	Set<Map.Entry<String, StreamObserver<SmartContractMessage>>> entries =
		smartContractIdAndStreamObserverMap.entrySet();
	for (Map.Entry<String, StreamObserver<SmartContractMessage>> entry : entries) {
	  str = str + " " + entry.getKey();
	}
	logger.info("key : " + str);
	StreamObserver<SmartContractMessage> streamObserver =
		smartContractIdAndStreamObserverMap.get(smartContractId);
	if (streamObserver == null) {
	  logger.info(String.format("no stream observer for %s", smartContractId));
	  return;
	}
	streamObserver.onNext(message);
  }

  /**
   * 初始化智能合约
   *
   * @param smartContractId 智能合约编号
   * @param smartContractMessage 发送的消息
   */
  public static void init(String smartContractId, SmartContractMessage smartContractMessage) {
	logger.info("init " + smartContractId);
	// 设置消息的type为INIT
	SmartContractMessage message =
		SmartContractMessage.newBuilder()
			.mergeFrom(smartContractMessage)
			.setType(SmartContractMessage.Type.INIT)
			.build();
	send(smartContractId, message);
  }

  /**
   * invoke智能合约
   *
   * @param smartContractId 智能合约编号
   * @param smartContractMessage 消息
   */
  public static void invoke(String smartContractId, SmartContractMessage smartContractMessage) {
	logger.info("invoke " + smartContractId);
	// 修改消息的type为TRANSACTION
	SmartContractMessage message =
		SmartContractMessage.newBuilder()
			.mergeFrom(smartContractMessage)
			.setType(SmartContractMessage.Type.TRANSACTION)
			.build();
	send(smartContractId, message);
	updateSmartContractStatus(smartContractId, SMART_CONTRACT_STATUS_BUSY);
	String txId = smartContractMessage.getTxid();
	addTxId(txId, smartContractId);
	updateTxStatus(txId, TX_STATUS_START);
  }

  /**
   * 从message中获取智能合约编号
   *
   * @param message 消息
   * @return
   */
  private String getSmartContractId(SmartContractMessage message) {
	String smartContractIdStr = "";
	try {
	  smartContractIdStr = Smartcontract.SmartContractID.parseFrom(message.getPayload()).getName();
	} catch (InvalidProtocolBufferException e) {
	  logger.error(e.getMessage(), e);
	}
	return smartContractIdStr;
  }

  public static void main(String[] args) {
	SmartContractMessage build =
		SmartContractMessage.newBuilder().setTxid("t1").setGroupId("g1").build();
	SmartContractMessage build1 = SmartContractMessage.newBuilder().mergeFrom(build).build();

	build = SmartContractMessage.newBuilder().setTxid("t2").setGroupId("g2").build();

	System.out.println(build.getTxid() + " " + build.getGroupId());
	System.out.println(build1.getTxid() + " " + build1.getGroupId());
  }
}
