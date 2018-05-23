package org.bcia.javachain.core.ssc.lssc;

import com.google.protobuf.ByteString;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.ledger.util.IoUtil;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.common.util.proto.TxUtils;
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.core.smartcontract.shim.ISmartContract;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.smartcontract.shim.impl.MockStub;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

/**
 * LSSC系统智能合约的单元测试类
 *
 * @author sunianle,liuxiong
 * @date 3/29/18
 * @company Dingxuan
 */
public class LSSCTest extends BaseJunit4Test {
    @Autowired
    private LSSC lssc;
    private MockStub mockStub;

    @Before
    public void beforeTest(){
        mockStub = new MockStub(CommConstant.LSSC, lssc);
    }

    @Test
    public void init() {
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void testInstalls(){
        String path="src/main/java/org/bcia/javachain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
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

        Smartcontract.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartcontractName, path, version, initArgs, false);
        } catch (SysSmartContractException e) {
            e.printStackTrace();
            return;
        }
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.INSTALL));
        args0.add(ByteString.copyFrom(cdsBytes));
        Smartcontract.SmartContractSpec spec=Smartcontract.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec, caller.getBytes(), "msg1".getBytes());
        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);
        //尚未完全实现调通全部逻辑，返回内部错误
//        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
        //TODO write by sunzongyu. Create file $SmartContractInstallPath(/home/bica/javachain/lssc)/example02.1.0 success
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void testDeploys(){
        String path="src/main/java/org/bcia/javachain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        testDeploy("example02","1.0",path,"","Alice",mockStub);
    }

    public void testDeploy(String smartcontractName,String version,String path,
                            String expectErrorMsg,String caller,
                            MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        Smartcontract.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartcontractName, path, version, initArgs, false);
        } catch (SysSmartContractException e) {
            e.printStackTrace();
            return;
        }
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.DEPLOY));
        args0.add(ByteString.copyFromUtf8("test"));
        args0.add(ByteString.copyFrom(cdsBytes));
        Smartcontract.SmartContractSpec spec=Smartcontract.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec, caller.getBytes(), "msg1".getBytes());

        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);
        //尚未完全实现调通全部逻辑，返回内部错误
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testUpgrades(){
        String path="src/main/java/org/bcia/javachain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        testUpgrade("example02","1.0",path,"","Alice",mockStub);
    }

    public void testUpgrade(String smartcontractName,String version,String path,
                           String expectErrorMsg,String caller,
                           MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        Smartcontract.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartcontractName, path, version, initArgs, false);
        } catch (SysSmartContractException e) {
            e.printStackTrace();
            return;
        }
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.UPGRADE));
        args0.add(ByteString.copyFromUtf8("test"));
        args0.add(ByteString.copyFrom(cdsBytes));
        Smartcontract.SmartContractSpec spec=Smartcontract.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec, caller.getBytes(), "msg1".getBytes());

        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);
        //尚未安全实现调通全部逻辑，返回内部错误
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
    }

    private Smartcontract.SmartContractDeploymentSpec constructDeploySpec(String smartcontractName, String path, String version, List<String> initArgs, boolean bCreateFS) throws SysSmartContractException {
//        Smartcontract.SmartContractDeploymentSpec spec=Smartcontract.SmartContractDeploymentSpec.newBuilder().
//                setCodePackage(ByteString.copyFromUtf8("testcds")).build();
        Smartcontract.SmartContractID smartContractID = Smartcontract.SmartContractID.newBuilder().
                setName(smartcontractName).setPath(path).setVersion(version).build();
        Smartcontract.SmartContractInput.Builder inputBuilder= Smartcontract.SmartContractInput.newBuilder();
        for (String initArg:initArgs) {
            ByteString arg = ByteString.copyFromUtf8(initArg);
            inputBuilder.addArgs(arg);
        }
        Smartcontract.SmartContractInput input=inputBuilder.build();
        Smartcontract.SmartContractSpec.Builder builder=Smartcontract.SmartContractSpec.newBuilder();
        builder.setType(Smartcontract.SmartContractSpec.Type.JAVA);
        builder.setSmartContractId(smartContractID);
        builder.setInput(input);
        Smartcontract.SmartContractSpec spec=builder.build();

        Map<String, File> map = IoUtil.getFileRelativePath(path);
        byte[] tarBytes=null;
        try {
            tarBytes= IoUtil.tarWriter(map, 1024);
            File file=new File("/opt/2.tar");
            FileOutputStream stream=new FileOutputStream(file);
            stream.write(tarBytes);
            stream.close();
        } catch (Exception e) {
            String msg=String.format("Create tar file for %s failed:%s",smartcontractName,e.getMessage());
            throw new SysSmartContractException(msg);
        }


        Smartcontract.SmartContractDeploymentSpec depSpec = Smartcontract.SmartContractDeploymentSpec.newBuilder()
             .setSmartContractSpec(spec).setCodePackage(ByteString.copyFrom(tarBytes)).build();
        if(bCreateFS){
            //后面填充
        }
        return depSpec;
    }

    @Test
    public void testStarts(){
        String path="src/main/java/org/bcia/javachain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        testStart("example02","1.0",path,"","Alice",mockStub);
    }

    public void testStart(String smartcontractName,String version,String path,
                            String expectErrorMsg,String caller,
                            MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        Smartcontract.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartcontractName, path, version, initArgs, false);
        } catch (SysSmartContractException e) {
            e.printStackTrace();
            return;
        }
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.GET_SC_INFO));
        args0.add(ByteString.copyFromUtf8("testGroup"));
        args0.add(ByteString.copyFromUtf8("test"));
//        args0.add(ByteString.copyFrom(cdsBytes));
        Smartcontract.SmartContractSpec spec=Smartcontract.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec, caller.getBytes(), "msg1".getBytes());

        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);
        //尚未安全实现调通全部逻辑，返回内部错误
        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void invoke() throws Exception{
        File file = new File("testFile12112121231312313131");
        if(!file.exists()){
            System.out.println(file.createNewFile());
        }
    }

    public void putSC2FS(){

    }
}