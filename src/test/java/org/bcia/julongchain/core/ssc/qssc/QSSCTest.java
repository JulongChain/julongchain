package org.bcia.julongchain.core.ssc.qssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.util.BytesHexStrTranslate;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.core.aclmgmt.MockAclProvider;
import org.bcia.julongchain.core.aclmgmt.resources.Resources;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.node.util.NodeUtils;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.smartcontract.shim.impl.MockStub;
import org.bcia.julongchain.csp.gm.dxct.sm3.SM3;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

/**
 * QSSC单元测试
 *
 * @author sunianle
 * @date 4/16/18
 * @company Dingxuan
 */
public class QSSCTest extends BaseJunit4Test {
    @Autowired
    private QSSC qssc;
    private MockStub mockStub;

    @Before
    public void beforeTest(){
        mockStub = new MockStub(CommConstant.QSSC, qssc);
    }

    @Test
    public void init() {
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void testQueryGetGroupInfo() {
        String groupid="mytestgroupid1";
        String path="/var/julongchain/test1/";
        setupTestLedger(groupid,path);

        //正常情况
        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8(QSSC.GET_GROUP_INFO));
        args.add(ByteString.copyFromUtf8(groupid));
        ProposalPackage.SignedProposal sp=resetProvider(Resources.QSSC_GetChainInfo,groupid,null);
        ISmartContract.SmartContractResponse res =mockStub.mockInvokeWithSignedProposal("1",args,sp);
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        try {
            Ledger.BlockchainInfo info = Ledger.BlockchainInfo.parseFrom(res.getPayload());
            System.out.println(info.getHeight());
            assertNotNull(info);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        //参数个数不正确
        List<ByteString> args2= new LinkedList<ByteString>();
        args2.add(ByteString.copyFromUtf8(QSSC.GET_GROUP_INFO));
        ISmartContract.SmartContractResponse res2 =mockStub.mockInvoke("2",args2);
        assertThat(res2.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res2.getMessage(),is("Incorrect number of arguments, 1)"));

        //错误的groupid
        List<ByteString> args3= new LinkedList<ByteString>();
        args3.add(ByteString.copyFromUtf8(QSSC.GET_GROUP_INFO));
        args3.add(ByteString.copyFromUtf8("fakegroupid"));
        ISmartContract.SmartContractResponse res3 =mockStub.mockInvoke("3",args3);
        assertThat(res3.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res3.getMessage(),is("Invalid group ID fakegroupid"));
    }

    @Test
    public void testQueryGetTransactionByID(){
        String groupid="mytestgroupid2";
        String path="/var/julongchain/test2/";
        setupTestLedger(groupid,path);

        //不存在的交易


        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8(QSSC.GET_TRANSACTION_BY_ID));
        args.add(ByteString.copyFromUtf8(groupid));
        args.add(ByteString.copyFromUtf8("1"));
        ProposalPackage.SignedProposal sp=resetProvider(Resources.QSSC_GetTransactionByID,groupid,null);
        ISmartContract.SmartContractResponse res =mockStub.mockInvokeWithSignedProposal("1",args,sp);
        assertThat("GetTransactionByID should have failed with invalid txid: 1",
                res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res.getMessage(),is("Transaction with id 1 not found"));

        //参数个数不对
        List<ByteString> args2= new LinkedList<ByteString>();
        args2.add(ByteString.copyFromUtf8(QSSC.GET_TRANSACTION_BY_ID));
        args2.add(ByteString.copyFromUtf8(groupid));
        args2.add(ByteString.EMPTY);
        ISmartContract.SmartContractResponse res2 =mockStub.mockInvoke("2",args2);
        //因一些对象为空，部分接口尚未调通
        assertThat("GetTransactionByID should have failed with invalid txid: empty",
                res2.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res2.getMessage(),is("Transaction ID must not be empty."));

        // Test with wrong number of parameters
        List<ByteString> args3= new LinkedList<ByteString>();
        args3.add(ByteString.copyFromUtf8(QSSC.GET_TRANSACTION_BY_ID));
        args3.add(ByteString.copyFromUtf8(groupid));
        ISmartContract.SmartContractResponse res3 =mockStub.mockInvoke("3",args3);
        assertThat(res3.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        String msg=String.format("Missing 3rd argument for %s",QSSC.GET_TRANSACTION_BY_ID);
        assertThat(res3.getMessage(),is(msg));

        //测试正确的查询
        try {
            commitBlock(groupid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ByteString> args4= new LinkedList<ByteString>();
        args4.add(ByteString.copyFromUtf8(QSSC.GET_TRANSACTION_BY_ID));
        args4.add(ByteString.copyFromUtf8(groupid));
        args4.add(ByteString.copyFromUtf8("1"));
        ISmartContract.SmartContractResponse res4 =mockStub.mockInvoke("4",args4);
        assertThat(res4.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        System.out.println(res4.getPayload());
        try {
            TransactionPackage.ProcessedTransaction trans = TransactionPackage.ProcessedTransaction.parseFrom(res4.getPayload());
            assertNotNull(trans);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryGetBlockByNumber(){
        String groupid="mytestgroupid3";
        String path="/var/julongchain/test3/";
        setupTestLedger(groupid,path);

        // block number 0 (genesis block) would already be present in the ledger
        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_NUMBER));
        args.add(ByteString.copyFromUtf8(groupid));
        args.add(ByteString.copyFromUtf8("0"));
        ProposalPackage.SignedProposal sp=resetProvider(Resources.QSSC_GetBlockByNumber,groupid,null);
        ISmartContract.SmartContractResponse res =mockStub.mockInvokeWithSignedProposal("1",args,sp);
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        try {
            Common.Block block = Common.Block.parseFrom(res.getPayload());
            assertNotNull(block);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


        // block number 1 should not be present in the ledger
        List<ByteString> args2= new LinkedList<ByteString>();
        args2.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_NUMBER));
        args2.add(ByteString.copyFromUtf8(groupid));
        args2.add(ByteString.copyFromUtf8("1"));
        ISmartContract.SmartContractResponse res2 =mockStub.mockInvoke("2",args2);
        assertThat(res2.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res2.getMessage(),startsWith("Block number 1 not found"));


        // block number cannot be empty
        List<ByteString> args3= new LinkedList<ByteString>();
        args3.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_NUMBER));
        args3.add(ByteString.copyFromUtf8(groupid));
        args3.add(ByteString.EMPTY);
        ISmartContract.SmartContractResponse res3 =mockStub.mockInvoke("3",args3);
        assertThat(res3.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res3.getMessage(),is("Block number must not be empty."));
    }

    @Test
    public void testQueryGetBlockByHash() {
        String groupid="mytestgroupid4";
        String path="/var/julongchain/test4/";
        setupTestLedger(groupid,path);


        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_HASH));
        args.add(ByteString.copyFromUtf8(groupid));
        args.add(ByteString.copyFromUtf8("0"));
        ProposalPackage.SignedProposal sp=resetProvider(Resources.QSSC_GetBlockByHash,groupid,null);
        ISmartContract.SmartContractResponse res =mockStub.mockInvokeWithSignedProposal("1",args,sp);
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res.getMessage(),is("Block with hash 0 not found"));


        List<ByteString> args2= new LinkedList<ByteString>();
        args2.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_HASH));
        args2.add(ByteString.copyFromUtf8(groupid));
        args2.add(ByteString.EMPTY);
        ISmartContract.SmartContractResponse res2 =mockStub.mockInvoke("2",args2);
        assertThat(res2.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res2.getMessage(),is("Block hash must not be empty."));

        try {
            commitBlock(groupid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ByteString> args3= new LinkedList<ByteString>();
        args3.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_HASH));
        args3.add(ByteString.copyFromUtf8(groupid));
        String hash="76e93d22cf474ae445d0407ceaf01a4b0e61187265ea6728191d86daa9745995";
        byte[] hashBytes=BytesHexStrTranslate.toBytes(hash);
        args3.add(ByteString.copyFrom(hashBytes));
        ISmartContract.SmartContractResponse res3 =mockStub.mockInvoke("3",args3);
        assertThat(res3.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        try {
            Common.Block block = Common.Block.parseFrom(res3.getPayload());
            assertNotNull(block);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryGetBlockByTxID() {
        String groupid="mytestgroupid5";
        String path="/var/julongchain/test5/";
        setupTestLedger(groupid,path);
        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_TX_ID));
        args.add(ByteString.copyFromUtf8(groupid));
        args.add(ByteString.EMPTY);
        ProposalPackage.SignedProposal sp=resetProvider(Resources.QSSC_GetBlockByTxID,groupid,null);
        ISmartContract.SmartContractResponse res =mockStub.mockInvokeWithSignedProposal("1",args,sp);
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        assertThat(res.getMessage(),is("Raw TxID must not be empty."));

        try {
            commitBlock(groupid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ByteString> args2= new LinkedList<ByteString>();
        args2.add(ByteString.copyFromUtf8(QSSC.GET_BLOCK_BY_TX_ID));
        args2.add(ByteString.copyFromUtf8(groupid));
        args2.add(ByteString.copyFromUtf8("1"));
        ProposalPackage.SignedProposal sp2=resetProvider(Resources.QSSC_GetBlockByTxID,groupid,null);
        ISmartContract.SmartContractResponse res2 =mockStub.mockInvokeWithSignedProposal("1",args2,sp2);
        assertThat(res2.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        try {
            Common.Block block = Common.Block.parseFrom(res2.getPayload());
            assertNotNull(block);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryGeneratedBlock() {

    }

    @Test
    public void testQueryNonexistentFunction() {

    }


    public ProposalPackage.SignedProposal resetProvider(String qssc_getTransactionByID,
                                                              String groupid,
                                                              ProposalPackage.SignedProposal prop) {
        return null;
    }

    private void setupTestLedger(String groupid, String path) {
        MockAclProvider aclProvider=new MockAclProvider();
        aclProvider.reset();
        //先删除账本
        FileUtils.deleteDir(new File(LedgerConfig.getRootPath()));
        //待与周辉的接口进行对接，需要建立账本，以支持后续的查询
        try {
            Node.getInstance().mockInitialize();
            Node.getInstance().mockCreateGroup(groupid);
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    private Common.Block addBlockForTesting(){
        return null;
    }

    public void commitBlock(String groupID) throws Exception {
        INodeLedger targetLedger = NodeUtils.getLedger(groupID);
        long i = 0;
        while(true){
            if(targetLedger.getBlockByNumber(i)==null){
                break;
            }
            i++;
        }
        System.out.println("Start Block Number is " + i);
        long startTime = System.currentTimeMillis();
        Common.BlockData data = null;
        ByteString preHash = ByteString.copyFrom(new SM3().hash(targetLedger.getBlockByNumber(i - 1).getData().toByteArray()));
        for (int j = 0; j < 9; j++) {
            BlockAndPvtData bap = new BlockAndPvtData();

            Common.Envelope envelope = Common.Envelope.newBuilder()
                    .setPayload(Common.Payload.newBuilder()
                            .setHeader(Common.Header.newBuilder()
                                    .setGroupHeader(Common.GroupHeader.newBuilder()
                                            .setTxId(String.valueOf(j))
                                            .build().toByteString())
                                    .build())
                            .build().toByteString())
                    .build();
            System.out.println();
            System.out.println("Enter envelope " + String.valueOf(j) + " with txID " + j);
            System.out.println();
            Common.Envelope envelope2 = Common.Envelope.newBuilder()
                    .setPayload(Common.Payload.newBuilder()
                            .setHeader(Common.Header.newBuilder()
                                    .setGroupHeader(Common.GroupHeader.newBuilder()
                                            .setTxId(String.valueOf(j * 10))
                                            .build().toByteString())
                                    .build())
                            .build().toByteString())
                    .build();
            System.out.println();
            System.out.println("Enter envelope " + String.valueOf(j + 1) + " with txID " + j * 10);
            System.out.println();
            data = Common.BlockData.newBuilder()
                    .addData(envelope.toByteString())
                    .addData(envelope2.toByteString())
                    .build();

            bap.setBlock(Common.Block.newBuilder()
                    .setHeader(Common.BlockHeader.newBuilder()
                            .setNumber(i + j)
                            .setDataHash(ByteString.copyFrom(new SM3().hash(data.toByteArray())))
                            .setPreviousHash(preHash)
                            .build())
                    .setData(data)
                    .setMetadata(Common.BlockMetadata.newBuilder()
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .build())
                    .build());
            soutBytes(bap.getBlock().toByteArray());
            System.out.println();
            preHash = ByteString.copyFrom(new SM3().hash(data.toByteArray()));
            System.out.println("PreHash:"+ BytesHexStrTranslate.bytesToHexFun1(preHash.toByteArray()));
            targetLedger.commitWithPvtData(bap);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗时: " + String.valueOf(endTime - startTime) + "ms");
    }

    private static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
        }
    }
}