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
package org.bcia.javachain.core.ledger;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.javachain.common.ledger.util.leveldbhelper.LevelDBProvider;
import org.bcia.javachain.core.ledger.kvledger.KvLedger;
import org.bcia.javachain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.core.ssc.SystemSmartContractDescriptor;
import org.bcia.javachain.csp.gm.dxct.sm3.SM3;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.TransactionPackage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class LedgerManagerTest {
    public static String groupID = "mytestgroupid2";
    public static byte[] b1 = null;
    public static byte[] b2 = null;
    INodeLedger l = null;
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @After
    public void after(){
//        System.out.println(((KvLedger) l).getLedgerID());
    }

    @Test
    public void delete() throws Exception{
        System.out.println(deleteDir(new File(LedgerConfig.getRootPath())));
    }

    @Test
    public void createLedger() throws Exception {
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        System.out.println(deleteDir(new File(LedgerConfig.getRootPath())));
        long before = System.currentTimeMillis();
        LedgerManager.initialize(null);
        Common.Block block = factory.getGenesisBlock("myGroup");
        l = LedgerManager.createLedger(block);
        block = factory.getGenesisBlock("mytestgroupid2");
        l = LedgerManager.createLedger(block);
        long after = System.currentTimeMillis();
        System.out.println("耗时： " + (after - before));
//        l = LedgerManager.createLedger(block);
//        List<String> list = LedgerManager.getLedgerIDs();
//        list.forEach((s) -> {
//            System.out.println(s);
//        });
//        ITxSimulator simulator = l.newTxSimulator("MyGroup");
//        simulator.
    }

    @Test
    public void openLedger() throws Exception{
        LedgerManager.initialize(null);
        String ledgerId1 = "myGroup";
        String ledgerId2 = "mytestgroupid2";
        l = LedgerManager.openLedger(ledgerId2);
        Ledger.BlockchainInfo bcInfo = l.getBlockchainInfo();
        Common.Block lastBlock = l.getBlockByNumber(bcInfo.getHeight() - 1);
        System.out.println(bcInfo.getHeight() - 1);
        System.out.println(lastBlock);
//        System.out.println(l.getTransactionByID("8"));
    }

    @Test
    public void commitBlock() throws Exception{
        //重置路径
        System.out.println(deleteDir(new File(LedgerConfig.getRootPath())));
        //初始化账本
        LedgerManager.initialize(null);
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        l = LedgerManager.createLedger(factory.getGenesisBlock("myGroup"));
        //构建交易模拟集
        TxSimulationResults results = constructTxSimulationResults();
        //构建区块
        Common.Block block = constructBlock(l.getBlockByNumber(0), results);
        //构建私有数据集
        TxPvtData txPvtData = new TxPvtData();
        txPvtData.setSeqInBlock(0);
        txPvtData.setWriteSet(results.getPrivateReadWriteSet());
        BlockAndPvtData bap = new BlockAndPvtData();
        bap.setBlock(block);
        bap.setBlockPvtData(new HashMap<Long, TxPvtData>(){{
            put((long) 0, txPvtData);
        }});
        //提交区块及私有数据
        l.commitWithPvtData(bap);
    }

    private TxSimulationResults constructTxSimulationResults() throws Exception{
        TxPvtData txPvtData = new TxPvtData();

        ITxSimulator simulator = l.newTxSimulator("txid");
        simulator.setState("myGroup", "key", "value pub".getBytes());
        simulator.setPrivateData("myGroup", "coll", "key", "value pvt".getBytes());
        return simulator.getTxSimulationResults();
    }

    private Common.Block constructBlock(Common.Block preBlock, TxSimulationResults txSimulationResults) throws Exception{
        Rwset.TxReadWriteSet rwset = txSimulationResults.getPublicReadWriteSet();

        Common.BlockData data = Common.BlockData.newBuilder()
                //pub
                .addData(constructEnvelope(rwset.toByteString(), "1", 1, "myGroup").toByteString())
                .build();

        Common.BlockHeader blockHeader = Common.BlockHeader.newBuilder()
                .setPreviousHash(preBlock.getHeader().getDataHash())
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

    private Common.Envelope constructEnvelope(ByteString rwset, String txID, int version, String groupID) throws Exception {
        Common.GroupHeader groupHeader = Common.GroupHeader.newBuilder()
                .setType(Common.HeaderType.ENDORSER_TRANSACTION_VALUE)
                .setTxId(txID)
                .setVersion(version)
                .setGroupId(groupID)
                .build();

        Common.SignatureHeader signatureHeader = Common.SignatureHeader.newBuilder()
                .setNonce(ByteString.copyFromUtf8("nonce"))
                .setCreator(ByteString.copyFromUtf8("creator"))
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

    @Test
    public void showBlocks() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger("mytestgroupid2");
        for (int i = 0;; i++) {
            Common.Block block = l.getBlockByNumber(i);
            if(block == null){
                break;
            }
            System.out.println(block);
        }
    }

    @Test
    public void newTxSimulator() throws Exception {
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println(l.getBlockchainInfo());
    }

    @Test
    public void getTxById()throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        Common.Payload payload = Common.Payload.parseFrom(l.getTransactionByID("4").getTransactionEnvelope().getPayload());
        Common.GroupHeader header = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        System.out.println(header);
        System.out.println(header.getTxId());
    }

    @Test
    public void getBlockByNumber() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println(l.getBlockByNumber(3));
    }

    @Test
    public void getChainInfo() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println(l.getBlockchainInfo());
    }
    
    @Test
    public void getBlockIterator() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger("myGroup");
        IResultsIterator blocksIterator = l.getBlocksIterator(0);
        int i = 0;
        while (true) {
            System.out.println(++i);
            System.out.println(blocksIterator.next());
        }
    }

    @Test
    public void showfs() throws Exception{
        File file = new File(LedgerConfig.getBlockStorePath() + File.separator + "chains/myGroup/blockfile000000");
        FileInputStream is = new FileInputStream(file);
        byte[] b = new byte[(int) file.length()];
        is.read(b);
        soutBytes(b);
//        byte[] b = new byte[70];
//        is.skip(46);
//        is.read(b);
//        System.out.println(b[0]);
//        Common.Envelope envelope = Common.Envelope.parseFrom(b);
//        System.out.println(envelope);
    }

    @Test
    public void getBlockByTxId() throws Exception {
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger(groupID);
        System.out.println("Result " + l.getBlockByTxID("80"));
    }

    @Test
    public void showStates() throws Exception{
        LevelDBProvider provider = LevelDBProvider.newProvider(LedgerConfig.getStateLevelDBPath());
        Iterator<Map.Entry<byte[], byte[]>> iterator = provider.getIterator(null);
        while(iterator.hasNext()){
            Map.Entry<byte[], byte[]> entry = iterator.next();
            byte[] key = entry.getKey();
            byte[] value = entry.getValue();
            System.out.println(new String(key));
            System.out.println(new String(value));
        }
    }

    @Test
    public void testGetLedgerIDs() throws Exception{
        LedgerManager.initialize(null);
        Assert.assertSame(LedgerManager.getLedgerIDs().size(), 2);
        Assert.assertEquals(LedgerManager.getLedgerIDs().get(0), "myGroup");
        Assert.assertEquals(LedgerManager.getLedgerIDs().get(1), groupID);
    }

    private static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
        }
    }
}
