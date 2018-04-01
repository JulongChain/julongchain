package org.bcia.javachain.core.ssc.lssc;

import com.google.protobuf.ByteString;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.common.util.proto.TxUtils;
import org.bcia.javachain.core.smartcontract.shim.impl.MockStub;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractStub;
import org.bcia.javachain.core.ssc.essc.ESSC;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/29/18
 * @company Dingxuan
 */
public class LSSCTest extends BaseJunit4Test {
    @Autowired
    private LSSC lssc;
    @Mock
    private SmartContractStub stub;
    @Test
    public void init() {
        Response response=lssc.init(stub);
        assertThat(response.getStatus(),is(Response.Status.SUCCESS));
    }

    @Test
    public void testInstalls(){
        MockStub mockStub = new MockStub("LSSC", lssc);
        String path="/opt/git/javachain/src/main/java/org/bcia/javachain/examples/smartcontract/java/smartcontract_example02";
        testInstall("example02","1.0",path,"","Alice",mockStub);
    }

    public void testInstall(String smartcontractName,String version,String path,
                            String expectErrorMsg,String caller,
                            MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        Smartcontract.SmartContractDeploymentSpec cds = constructDeploySpec(smartcontractName, path, version, initArgs, false);
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.INSTALL));
        args0.add(ByteString.copyFrom(cdsBytes));
        Smartcontract.SmartContractSpec spec=Smartcontract.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec, caller.getBytes(), "msg1".getBytes());
        Response res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);
        assertThat(res.getStatus(),is(Response.Status.SUCCESS));
    }

    private Smartcontract.SmartContractDeploymentSpec constructDeploySpec(String smartcontractName, String path, String version, List<String> initArgs, boolean bCreateFS) {
        Smartcontract.SmartContractDeploymentSpec spec=Smartcontract.SmartContractDeploymentSpec.newBuilder().
                setCodePackage(ByteString.copyFromUtf8("testcds")).build();
        return spec;
    }


    @Test
    public void invoke() {

    }
}