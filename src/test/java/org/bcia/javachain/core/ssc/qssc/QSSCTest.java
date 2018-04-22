package org.bcia.javachain.core.ssc.qssc;

import com.google.protobuf.ByteString;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.core.aclmgmt.MockAclProvider;
import org.bcia.javachain.core.aclmgmt.resources.Resources;
import org.bcia.javachain.core.smartcontract.shim.ISmartContract;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.smartcontract.shim.impl.MockStub;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
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
    @Mock
    private ISmartContractStub stub;

    @Test
    public void init() {
        ISmartContract.SmartContractResponse smartContractResponse = qssc.init(stub);
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void invoke() {
        MockStub mockStub = new MockStub(CommConstant.QSSC, qssc);
        mockStub.mockInit("1",new LinkedList<ByteString>());
        testQueryGetTransactionByID(mockStub);
        testQueryGetGroupInfo(mockStub);
        testQueryGetBlockByNumber(mockStub);
        testQueryGetBlockByHash(mockStub);
        testQueryNonexistentFunction(mockStub);
        testQueryGeneratedBlock(mockStub);
    }

    private void testQueryGeneratedBlock(MockStub mockStub) {
    }

    private void testQueryNonexistentFunction(MockStub mockStub) {
    }

    private void testQueryGetBlockByHash(MockStub mockStub) {
    }

    private void testQueryGetBlockByNumber(MockStub mockStub) {
    }

    private void testQueryGetGroupInfo(MockStub mockStub) {

    }

    private void testQueryGetTransactionByID(MockStub mockStub) {
        String groupid="mytestgroupid";
        String path="var/hyperledger/test2/";
        setupTestLedger(groupid,path);
        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8(QSSC.GET_TRANSACTION_BY_ID));
        args.add(ByteString.copyFromUtf8(groupid));
        args.add(ByteString.copyFromUtf8("1"));
        ProposalPackage.SignedProposal sp=resetProvider(Resources.QSSC_GetTransactionByID,groupid,null);
        ISmartContract.SmartContractResponse res =mockStub.mockInvokeWithSignedProposal("1",args,sp);
        //因一些对象为空，部分接口尚未调通
        assertThat(res.getStatus(),not(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    private ProposalPackage.SignedProposal resetProvider(String qssc_getTransactionByID,
                                                              String groupid,
                                                              ProposalPackage.SignedProposal prop) {
        return null;
    }

    private void setupTestLedger(String groupid, String path) {
        MockAclProvider aclProvider=new MockAclProvider();
        aclProvider.reset();
        //待与周辉的接口进行对接，需要建立账本，以支持后续的查询
    }

    private Common.Block addBlockForTesting(){
        return null;
    }
}