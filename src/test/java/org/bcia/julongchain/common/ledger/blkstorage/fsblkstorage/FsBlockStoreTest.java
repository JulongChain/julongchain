package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.ledger.blkstorage.BlockStorage;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.ledger.blkstorage.IndexConfig;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/06/13
 * @company Dingxuan
 */
public class FsBlockStoreTest {
	FsBlockStoreProvider blkStoreProvider;
	IBlockStore blockStore;
	static String groupID = "test id";
	static String txID = "txID";
	static byte[] rwset = new byte[]{(byte) 18 , (byte) 29 , (byte) 10 , (byte) 7 , (byte) 116 , (byte) 101 , (byte) 115 ,
			(byte) 116 , (byte) 32 , (byte) 105 , (byte) 100 , (byte) 18 , (byte) 18 , (byte) 26 , (byte) 16 ,
			(byte) 10 , (byte) 3 , (byte) 107 , (byte) 101 , (byte) 121 , (byte) 26 , (byte) 9 , (byte) 112 ,
			(byte) 117 , (byte) 98 , (byte) 32 , (byte) 118 , (byte) 97 , (byte) 108 , (byte) 117 , (byte) 101};
	static byte[] dataHash = new byte[]{(byte) 92 , (byte) 121 , (byte) 99 , (byte) 67 , (byte) 102 , (byte) 84 , (byte) -127 ,
			(byte) 17 , (byte) -120 , (byte) -104 , (byte) -26 , (byte) -50 , (byte) 38 , (byte) 108 , (byte) -127 ,
			(byte) -128 , (byte) 0 , (byte) 116 , (byte) 7 , (byte) 97 , (byte) -73 , (byte) -11 , (byte) -60 ,
			(byte) 97 , (byte) 12 , (byte) -118 , (byte) 24 , (byte) -28 , (byte) -95 , (byte) -105 , (byte) -65 , (byte) 71};

	@Before
	public void before() throws Exception {
		String[] attrsToIndex = {
				BlockStorage.INDEXABLE_ATTR_BLOCK_HASH,
				BlockStorage.INDEXABLE_ATTR_BLOCK_NUM,
				BlockStorage.INDEXABLE_ATTR_TX_ID,
				BlockStorage.INDEXABLE_ATTR_BLOCK_NUM_TRAN_NUM,
				BlockStorage.INDEXABLE_ATTR_BLOCK_TX_ID,
				BlockStorage.INDEXABLE_ATTR_TX_VALIDATION_CODE
		};
		IndexConfig indexConfig = new IndexConfig(attrsToIndex);
		//文件系统初始化参数
		blkStoreProvider =
				new FsBlockStoreProvider(new Config(LedgerConfig.getBlockStorePath(), LedgerConfig.getMaxBlockfileSize()), indexConfig);
		blockStore = blkStoreProvider.openBlockStore(groupID);
		//groupID : test id, key : key, value : value
	}

	@Test
	public void addBlock() throws Exception {
//		Common.Block block = constructBlock(null);
//		blockStore.addBlock(block);
	}

	@Test
	public void getBlockchainInfo() throws Exception {
		Ledger.BlockchainInfo info = blockStore.getBlockchainInfo();
		Assert.assertSame(info.getHeight(), (long) 1);
		Assert.assertEquals(info.getCurrentBlockHash(), ByteString.copyFrom(dataHash));
		Assert.assertEquals(info.getPreviousBlockHash(), ByteString.EMPTY);
	}

	@Test
	public void retrieveBlockByHash() throws Exception {
		Common.Block block = blockStore.retrieveBlockByHash(dataHash);
		Assert.assertEquals(block, constructBlock(null));
	}

	@Test
	public void retrieveBlockByNumber() throws Exception {
		Common.Block block = blockStore.retrieveBlockByNumber(1);
		Assert.assertEquals(block, constructBlock(null));
	}

	@Test
	public void retrieveTxByID() throws Exception {
		Common.Envelope envelope = blockStore.retrieveTxByID(txID);
		Assert.assertEquals(envelope, constuctDefaultEnvelope(Common.HeaderType.ENDORSER_TRANSACTION, txID, groupID, construceResults()));
	}

	@Test
	public void retrieveTxByBlockNumTranNum() throws Exception {
		Common.Envelope envelope = blockStore.retrieveTxByBlockNumTranNum(0, 0);
		Assert.assertEquals(envelope, constuctDefaultEnvelope(Common.HeaderType.ENDORSER_TRANSACTION, txID, groupID, construceResults()));
	}

	@Test
	public void retrieveBlockByTxID() throws Exception {
		Common.Block block = blockStore.retrieveBlockByTxID(txID);
		Assert.assertEquals(block, constructBlock(null));
	}

	@Test
	public void retrieveTxValidationCodeByTxID() throws Exception {
		TransactionPackage.TxValidationCode txValidationCode = blockStore.retrieveTxValidationCodeByTxID(txID);
		Assert.assertEquals(txValidationCode, TransactionPackage.TxValidationCode.forNumber(0));
	}

	public static Common.Block constructBlock(Common.Block preBlock) throws Exception{
		Common.BlockData data = Common.BlockData.newBuilder()
				.addData(constuctDefaultEnvelope(Common.HeaderType.ENDORSER_TRANSACTION, txID, groupID, construceResults()).toByteString())
				.build();

		Common.BlockHeader blockHeader = Common.BlockHeader.newBuilder()
				.setPreviousHash(preBlock == null ? ByteString.EMPTY : preBlock.getHeader().getDataHash())
				.setNumber(preBlock == null ? 0 : preBlock.getHeader().getNumber() + 1)
				.setDataHash(ByteString.copyFrom(Util.getHashBytes(data.toByteArray())))
				.build();

		Common.BlockMetadata metadata = Common.BlockMetadata.newBuilder()
				.addMetadata(ByteString.EMPTY)
				.addMetadata(ByteString.EMPTY)
				.addMetadata(ByteString.copyFrom(new byte[]{(byte) 0}))
				.addMetadata(ByteString.EMPTY)
				.build();

		return Common.Block.newBuilder()
				.setHeader(blockHeader)
				.setData(data)
				.setMetadata(metadata)
				.build();

	}

	private static ByteString construceResults(){
		return ByteString.copyFrom(rwset);
	}

	private static Common.Envelope constuctDefaultEnvelope(Common.HeaderType type,
	                                                String txID,
	                                                String groupID,
	                                                ByteString results){
		return constructEnvelope(type,
				txID,
				1,
				groupID,
				ByteString.copyFromUtf8("nonce"),
				ByteString.copyFromUtf8("creator"),
				ByteString.copyFromUtf8("txHeader"),
				ByteString.copyFromUtf8("event"),
				results,
				ProposalResponsePackage.Response.getDefaultInstance(),
				ByteString.copyFromUtf8("signature")
		);
	}

	private static Common.Envelope constructEnvelope(Common.HeaderType type,
	                                          String txID,
	                                          int version,
	                                          String groupID,
	                                          ByteString nonce,
	                                          ByteString creator,
	                                          ByteString txHeader,
	                                          ByteString event,
	                                          ByteString results,
	                                          ProposalResponsePackage.Response response,
	                                          ByteString signature) {
		Common.GroupHeader groupHeader = Common.GroupHeader.newBuilder()
				.setType(type.getNumber())
				.setTxId(txID)
				.setVersion(version)
				.setGroupId(groupID)
				.build();

		Common.SignatureHeader signatureHeader = Common.SignatureHeader.newBuilder()
				.setNonce(nonce)
				.setCreator(creator)
				.build();

		Common.Header header = Common.Header.newBuilder()
				.setGroupHeader(groupHeader.toByteString())
				.setSignatureHeader(signatureHeader.toByteString())
				.build();

		ProposalPackage.SmartContractAction resPayload = ProposalPackage.SmartContractAction.newBuilder()
				.setEvents(event)
				.setResults(results)
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
				.setHeader(txHeader)
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
				.setSignature(signature)
				.build();

		return envelope;
	}
}