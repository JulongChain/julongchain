package org.bcia.javachain.core.ssc.essc;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.util.Utils;
import org.bcia.javachain.common.util.proto.ProposalUtils;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.core.smartcontract.shim.impl.Handler;
import org.bcia.javachain.core.smartcontract.shim.impl.MockStub;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractStub;
import org.bcia.javachain.node.entity.MockCrypto;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/8/18
 * @company Dingxuan
 */
public class ESSCTest extends BaseJunit4Test {
    @Autowired
    ESSC essc;
    @Mock
    private SmartContractStub stub;

    @Test
    public void init() {
        Response response = essc.init(stub);
        assertThat(response.getStatus(), is(Response.Status.SUCCESS));
    }

    @Test
    public void invoke() {
        try {
            //Response successResponse=new Response(Response.Status.SUCCESS,"OK",ByteString.copyFromUtf8("payload").toByteArray());
            //successResponse.
            ProposalResponsePackage.Response successResponse = ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();

            MockStub mockStub = new MockStub("ESSC", essc);
            // Initialize ESCC supplying the identity of the signer
            List<ByteString> args0 = new LinkedList<ByteString>();
            args0.add(ByteString.copyFromUtf8("DEFAULT"));
            args0.add(ByteString.copyFromUtf8("PEER"));
            Response res = mockStub.mockInit("1", args0);
            if (res.getStatus() != Response.Status.SUCCESS) {
                System.out.printf("Init failded,%s\n", res.getMessage());
            }


            //success test 1: invocation with mandatory args only
            Smartcontract.SmartContractID smartContractID = Smartcontract.SmartContractID.newBuilder().setName("foo").setVersion("1.0").build();
            Smartcontract.SmartContractInput input = Smartcontract.SmartContractInput.newBuilder().addArgs(ByteString.copyFromUtf8("some"))
                    .addArgs(ByteString.copyFromUtf8("args")).build();
            Smartcontract.SmartContractSpec smartContractSpec = Smartcontract.SmartContractSpec.newBuilder().
                    setSmartContractId(smartContractID).
                    setType(Smartcontract.SmartContractSpec.Type.JAVA).
                    setInput(input).
                    build();
            Smartcontract.SmartContractInvocationSpec invocationSpec = Smartcontract.SmartContractInvocationSpec.newBuilder().
                    setSmartContractSpec(smartContractSpec).build();
            MockSigningIdentity sId = MockMspManager.getLocalMSP().getDefaultSigningIdentity();
            byte[] sIdBytes = sId.serialize();
            byte[] nonce = MockCrypto.getRandomNonce();

            long beginTime = System.currentTimeMillis();

            String txID=ProposalUtils.computeProposalTxID(sIdBytes, nonce);
            long endTime = System.currentTimeMillis();

            System.out.println(txID + ",耗时"+ (endTime-beginTime) + "ms");

            ProposalPackage.Proposal proposal = ProposalUtils.buildSmartContractProposal(Common.HeaderType.ENDORSER_TRANSACTION,
                    Utils.getTestGroupID(), txID, invocationSpec, nonce, sIdBytes, null);
            String simRes="simulation_result";
            

            List<ByteString> args1 = new LinkedList<ByteString>();
            args1.add(0, ByteString.copyFromUtf8(""));
            args1.add(1,proposal.getHeader());
            args1.add(2,proposal.getPayload());
            args1.add(3,smartContractID.toByteString());
            args1.add(4,successResponse.toByteString());
            args1.add(5,ByteString.copyFromUtf8(simRes));
            Response res1 = mockStub.mockInvoke("1", args1);
            if (res1.getStatus() != Response.Status.SUCCESS) {
                System.out.printf("Invoke failded,%s\n", res.getMessage());
            }
            assertThat(res1.getStatus(),is(Response.Status.SUCCESS));


            //Failed path: Not enough parameters
            List<ByteString> args2 = new LinkedList<ByteString>();
            args2.add(0, ByteString.copyFromUtf8("test"));
            Response res2=mockStub.mockInvoke("1", args2);
            Response.Status status2 = res2.getStatus();
            assertThat(status2,is(Response.Status.INTERNAL_SERVER_ERROR));

            // Failed path: header is empty
            List<ByteString> args3 = new LinkedList<ByteString>();
            args3.add(0, ByteString.copyFromUtf8("test"));
            args3.add(1,ByteString.copyFromUtf8(""));
            args3.add(2,proposal.getPayload());
            args3.add(3,smartContractID.toByteString());
            args3.add(4,successResponse.toByteString());
            args3.add(5,ByteString.copyFromUtf8(simRes));
            Response res3=mockStub.mockInvoke("1", args3);
            Response.Status status3= res3.getStatus();
            assertThat(status3,is(Response.Status.INTERNAL_SERVER_ERROR));
        } catch (JavaChainException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getSmartContractStrDescription() {
        String description = essc.getSmartContractStrDescription();
        String expectedResult = "与背书相关的系统智能合约";
        assertThat(description, is(expectedResult));
    }

    @Test
    public void transformProtobuf() {
        try {
            ProposalResponsePackage.Response successResponse = ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();
            ByteString byteString = successResponse.toByteString();
            ProposalResponsePackage.Response transformedResponse = ProposalResponsePackage.Response.parseFrom(byteString);
            assertThat(transformedResponse.getStatus(),is(200));

            ProposalResponsePackage.Response successResponse2 = ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();
            ByteString byteString2 = successResponse.toByteString();
            String string2=byteString2.toStringUtf8();
            ByteString transformByteString=ByteString.copyFromUtf8(string2);
            ProposalResponsePackage.Response transformedResponse2 = ProposalResponsePackage.Response.parseFrom(transformByteString);
            assertThat(transformedResponse2.getStatus(),not(200));

            ProposalResponsePackage.Response successResponse3= ProposalResponsePackage.Response.newBuilder().
                    setStatus(200).setMessage("OK").setPayload(ByteString.copyFromUtf8("payload")).build();
            ByteString byteString3 = successResponse.toByteString();
            byte[] byteArray3 = byteString3.toByteArray();
            ProposalResponsePackage.Response transformedResponse3 = ProposalResponsePackage.Response.parseFrom(byteArray3);
            assertThat(transformedResponse3.getStatus(),is(200));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


}