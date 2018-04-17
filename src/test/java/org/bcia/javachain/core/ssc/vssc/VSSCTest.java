package org.bcia.javachain.core.ssc.vssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.common.util.proto.ProposalResponseUtils;
import org.bcia.javachain.common.util.proto.ProposalUtils;
import org.bcia.javachain.common.util.proto.TxUtils;
import org.bcia.javachain.core.smartcontract.shim.impl.MockStub;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractResponse;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractStub;
import org.bcia.javachain.core.ssc.essc.MockMSP;
import org.bcia.javachain.core.ssc.essc.MockMspManager;
import org.bcia.javachain.core.ssc.essc.MockSigningIdentity;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * VSSC单元测试类
 *
 * @author sunianle
 * @date 4/16/18
 * @company Dingxuan
 */
public class VSSCTest extends BaseJunit4Test {
    @Autowired
    private VSSC vssc;
    @Mock
    private SmartContractStub stub;

    @Test
    public void init() {
        SmartContractResponse smartContractResponse = vssc.init(stub);
        assertThat(smartContractResponse.getStatus(), is(SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void invoke() {
        MockStub mockStub = new MockStub("VSSC", vssc);
        SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        assertThat(smartContractResponse.getStatus(), is(SmartContractResponse.Status.SUCCESS));

        Common.Envelope tx= null;
        try {
            tx = createTx(false);
        } catch (JavaChainException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        byte[] envBytes=TxUtils.getBytesEnvelope(tx);
        String mspid="snl";
        byte[] policyBytes=getSignedByMSPMemberPolicy(mspid);

        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8("dv"));
        args.add(ByteString.copyFrom(envBytes));
        args.add(ByteString.copyFrom(policyBytes));

        SmartContractResponse smartContractResponse2 =mockStub.mockInvoke("1",args);
        assertThat(smartContractResponse2.getStatus(), is(SmartContractResponse.Status.SUCCESS));
    }

    private byte[] getSignedByMSPMemberPolicy(String mspid) {
        String policy="rw";
        return policy.getBytes();
    }

    /**
     * 创建模拟的交易
     * @param bEndorsedByDuplicatedIdentity
     * @return
     * @throws JavaChainException
     * @throws InvalidProtocolBufferException
     */
    private Common.Envelope createTx(boolean bEndorsedByDuplicatedIdentity) throws JavaChainException, InvalidProtocolBufferException {
        //创建SmartContractID
        Smartcontract.SmartContractID smartContractID=Smartcontract.SmartContractID.newBuilder().
                                                       setName("foo").setVersion("v1").build();
        //创建SmartContractSpec
        Smartcontract.SmartContractSpec spec=Smartcontract.SmartContractSpec.newBuilder().
                                                   setSmartContractId(smartContractID).build();
        //创建SmartContractInvocationSpec
        Smartcontract.SmartContractInvocationSpec invokeSpec=Smartcontract.SmartContractInvocationSpec.newBuilder().
                                                   setSmartContractSpec(spec).build();
        String creator="snl";
        String groupID="testGroup";
        //创建Proposal
        ProposalPackage.Proposal proposal=ProposalUtils.createProposalFromInvocationSpec(
                                 Common.HeaderType.ENDORSER_TRANSACTION, groupID,
                                 invokeSpec,creator.getBytes());
        //创建response
        SmartContractResponse smartContractResponse =new SmartContractResponse(SmartContractResponse.Status.SUCCESS,null,null);
        //先使用一个模拟的签名实体，后面和msp对接
        MockMSP localMSP = MockMspManager.getLocalMSP();
        MockSigningIdentity signingEndorser =localMSP.getDefaultSigningIdentity();
        String results="res";
        //生成一个ProposalResponse
        ProposalResponsePackage.ProposalResponse presp=ProposalResponseUtils.buildProposalResponse(
                proposal.getHeader().toByteArray(),
                proposal.getPayload().toByteArray(),
                smartContractResponse,
                results.getBytes(),
                null,
                smartContractID,
                null,
                signingEndorser
        );
        //构建发送给Conserter的Envelope
        Common.Envelope env=null;
        //两个重复实体签名
        if(bEndorsedByDuplicatedIdentity){
            env=EnvelopeHelper.createSignedTxEnvelope(proposal,signingEndorser,presp,presp);
        }
        //单个实体签名
        else{
            env=EnvelopeHelper.createSignedTxEnvelope(proposal,signingEndorser,presp);
        }
        return env;
    }
}