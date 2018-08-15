/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.common.ledger.util;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.genesis.GenesisBlockFactory;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.TxSimulationResults;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Ledger工具类
 *
 * @author sunzongyu
 * @date 2018/08/08
 * @company Dingxuan
 */
public class Utils {
	public static void main(String[] args) throws Exception {
		resetEnv();
	}

	public static void resetEnv() {
		String ledgerDir = "/var/julongchain/production";
		rm();
		rmi();
		rmrf(ledgerDir);
	}

	/**
	 * 执行外部Linux命令
	 * @param cmd	Linux命令
	 * @return	执行结果
	 */
	public static List<String> exce(String cmd) {
		System.out.println(cmd);
		List<String> result = null;
		try {
			Process process = null;
			result = new ArrayList<>();
			process = Runtime.getRuntime().exec(cmd);
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(process.getInputStream()));
			String line;
			while((line = reader.readLine()) != null) {
				result.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除docker镜像,不包含julongchain-baseimage
	 */
	public static void rmi() {
		List<String> cmdResult1 = exce("docker images");
		List<String> cmdResult2 = exce("docker images -q");
		int i = 1;
		for(; i < cmdResult1.size(); i++) {
			if (cmdResult1.get(i).contains("julongchain/julongchain-baseimage")) {
				continue;
			}
			String cmd = "docker rmi " + cmdResult2.get(i - 1);
			exce(cmd);
		}
	}

	/**
	 * 删除全部docker容器
	 */
	public static void rm() {
		exce("docker ps -aq").forEach(s -> exce("docker rm " + s));
	}

	/**
	 * 删除目标文件夹
	 * @param dir	目标文件夹了路径
	 */
	public static void rmrf(String dir) {
		String cmd = "rm -rf " + dir;
		exce(cmd);
	}

	/**
	 * 打印数组
	 */
	public void soutBytes(byte[] bytes, int length) {
		int i = 0;
		for (byte aByte : bytes) {
			i++;
			System.out.print(aByte + "\t");
			if(i > length){
				System.out.println();
				i = 0;
			}
		}
	}

	public static INodeLedger constructDefaultLedger() throws Exception {
		String groupID = "myGroup";
		String ns = "mycc";
		GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
		LedgerManager.initialize(null);
		Common.Block block = factory.getGenesisBlock(groupID);
		INodeLedger l = LedgerManager.createLedger(block);
		Common.Block block1 = constructDefaultBlock(l, block, groupID, ns);
		l.commit(block1);
		return l;
	}

	public static Common.Block constructDefaultBlock(INodeLedger l, Common.Block preBlock, String groupID, String namespace) throws Exception {
		return constructBlock(preBlock, groupID, Common.HeaderType.ENDORSER_TRANSACTION,
				constructTxSimulationResults(l, namespace, "txID", "key0", "value0").getPubReadWriteByteString(),
				constructTxSimulationResults(l, namespace, "txID", "key1", "value1").getPubReadWriteByteString(),
				constructTxSimulationResults(l, namespace, "txID", "key2", "value2").getPubReadWriteByteString(),
				constructTxSimulationResults(l, namespace, "txID", "key3", "value3").getPubReadWriteByteString(),
				constructTxSimulationResults(l, namespace, "txID", "妇产科", "妇产科").getPubReadWriteByteString()
		);
	}

	private static TxSimulationResults constructTxSimulationResults(INodeLedger l,String namespace, String txID, String key, String value) throws Exception{
		ITxSimulator simulator = l.newTxSimulator(txID);
		simulator.setState(namespace, key, value.getBytes(StandardCharsets.UTF_8));
		return simulator.getTxSimulationResults();
	}

	private static Common.Block constructBlock(Common.Block preBlock, String groupID, Common.HeaderType type, ByteString... rwsets) throws Exception {
		Common.BlockData.Builder builder = Common.BlockData.newBuilder();
		for (int i = 0; i < rwsets.length; i++) {
			//pub								//rwset		//txID				//type	//version	//groupID
			builder.addData(constructEnvelope(	rwsets[i], 	"txID" + i, 	type, 	1, 	groupID).toByteString());
		}
		Common.BlockData data = builder.build();

		Common.BlockHeader blockHeader = Common.BlockHeader.newBuilder()
				.setPreviousHash(ByteString.copyFrom(Util.getHashBytes(preBlock.getHeader().toByteArray())))
				.setNumber(preBlock.getHeader().getNumber() + 1)
				.setDataHash(ByteString.copyFrom(Util.getHashBytes(data.toByteArray())))
				.build();

		Common.BlockMetadata metadata = Common.BlockMetadata.newBuilder()
				.addMetadata(ByteString.EMPTY)
				.addMetadata(ByteString.EMPTY)
				.addMetadata(ByteString.EMPTY)
				.addMetadata(ByteString.EMPTY)
				.build();

		Common.Block block = Common.Block.newBuilder()
				.setHeader(blockHeader)
				.setData(data)
				.setMetadata(metadata)
				.build();

		return block;
	}

	private static Common.Envelope constructEnvelope(ByteString rwset, String txID, Common.HeaderType type, int version, String groupID) {
		Common.GroupHeader groupHeader = Common.GroupHeader.newBuilder()
				.setType(type.getNumber())
				.setTxId(txID)
				.setVersion(version)
				.setGroupId(groupID)
				.build();

		Common.SignatureHeader signatureHeader = Common.SignatureHeader.newBuilder()
				.setNonce(ByteString.copyFromUtf8("Nonce"))
				.setCreator(ByteString.copyFromUtf8("Creator"))
				.build();

		Common.Header header = Common.Header.newBuilder()
				.setGroupHeader(groupHeader.toByteString())
				.setSignatureHeader(signatureHeader.toByteString())
				.build();

		ProposalResponsePackage.Response response = ProposalResponsePackage.Response.newBuilder()
				.build();

		ProposalPackage.SmartContractAction resPayload = ProposalPackage.SmartContractAction.newBuilder()
				.setEvents(ByteString.copyFromUtf8("ProposalPackage.SmartContractAction Event"))
				.setResults(rwset)
				.setResponse(response)
				.build();

		ProposalResponsePackage.ProposalResponsePayload prPayload = ProposalResponsePackage.ProposalResponsePayload.newBuilder()
				.setExtension(resPayload.toByteString())
				.build();

		TransactionPackage.SmartContractEndorsedAction sceaPayload = TransactionPackage.SmartContractEndorsedAction.newBuilder()
				.setProposalResponsePayload(prPayload.toByteString())
				.build();

		TransactionPackage.SmartContractActionPayload scaPayload = TransactionPackage.SmartContractActionPayload.newBuilder()
				.setAction(sceaPayload)
				.build();

		TransactionPackage.TransactionAction transactionAction = TransactionPackage.TransactionAction.newBuilder()
				.setHeader(ByteString.copyFromUtf8("Transaction Header"))
				.setPayload(scaPayload.toByteString())
				.build();

		TransactionPackage.Transaction transaction = TransactionPackage.Transaction.newBuilder()
				.addActions(transactionAction)
				.build();

		Common.Payload payload = Common.Payload.newBuilder()
				.setHeader(header)
				.setData(transaction.toByteString())
				.build();

		Common.Envelope envelope = Common.Envelope.newBuilder()
				.setPayload(payload.toByteString())
				.setSignature(ByteString.copyFromUtf8("Envelope Signature"))
				.build();

		return envelope;
	}
}
