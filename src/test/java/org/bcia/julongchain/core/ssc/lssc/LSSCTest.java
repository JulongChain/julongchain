package org.bcia.julongchain.core.ssc.lssc;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.BaseJunit4Test;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.SysSmartContractException;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.common.util.proto.TxUtils;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContract;
import org.bcia.julongchain.core.smartcontract.shim.impl.MockStub;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
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
        String path="src/main/java/org/bcia/julongchain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());

        testInstall("mycc","1.0",path,"","Alice",mockStub);  //  TESTED  OK

        testInstall("example02","1.0",path,"","Alice",mockStub);  //  TESTED  OK

        testInstall("example02-2","1.0",path,"","Alice",mockStub); //  TESTED  OK

        //region  TESTED  invalid ChaincodeName
        testInstall("example02.java","0",path,
                 "Execute install failed, [SysSmartContract]InvalidChaincodeNameErr:example02.java","Alice",mockStub);
        //endregion

        //region  TESTED  EmptySmartContractName
        testInstall("","0",path,
                 "Execute install failed, [SysSmartContract]EmptySmartContractNameErr","Alice",mockStub);
        //endregion

        // region  TESTED   invalid version
        testInstall("example02","1.0-alpha{}+001",path,
                "Execute install failed, [SysSmartContract]InvalidChaincodeVersionErr:1.0-alpha{}+001","Alice",mockStub);
        //endregion
    }

    public void testInstall(String smartContractName,String version,String path,
                            String expectErrorMsg,String caller,
                            MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        SmartContractPackage.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartContractName, path, version, initArgs, false);
        } catch (SysSmartContractException e) {
            e.printStackTrace();
            return;
        }
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.INSTALL));
        args0.add(ByteString.copyFrom(cdsBytes));
        SmartContractPackage.SmartContractSpec spec=SmartContractPackage.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = null;
        try {
            signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec);
        } catch (JavaChainException e) {
            e.printStackTrace();
            return;
        }
        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);
        //尚未完全实现调通全部逻辑，返回内部错误
//        assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.INTERNAL_SERVER_ERROR));


        if(expectErrorMsg =="") //According to  status  or  message
        {
            assertThat(res.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        }
        else
        {
            assertThat(res.getMessage(), is(expectErrorMsg));
        }

    }

    @Test
    public void testDeploys(){
        String path="src/main/java/org/bcia/julongchain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
          testDeploy("example02","1.0",path,"","Alice",mockStub);//  TESTED  OK

        /*  testDeploy("example02","1.0",path,
                "ExecuteDeployOrUpgrade failed,[SysSmartContract]Get smartContract example02 from localstorage failed:java.io.FileNotFoundException: /var/julongchain/production/example02.1.0 (No such file or directory)",
                "Alice",mockStub);//  TESTED    Uninstall example02
                */

        //region  TESTED    invalid SCname
        testDeploy("example02.java","1.0",path,
                "ExecuteDeployOrUpgrade failed,[SysSmartContract]InvalidChaincodeNameErr:example02.java",
                "Alice",mockStub);
        //endregion


        //region  TESTED    invalid version
        testDeploy("example02","1{}0",path,
                "ExecuteDeployOrUpgrade failed,[SysSmartContract]InvalidChaincodeVersionErr:1{}0",
                "Alice",mockStub);
        //endregion


        //region  TESTED    blank SCname
        testDeploy("","1.0",path,
                "ExecuteDeployOrUpgrade failed,[SysSmartContract]EmptySmartContractNameErr",
                "Alice",mockStub);
        //endregion


        //region  TESTED   blank version
        testDeploy("example02","",path,
                "ExecuteDeployOrUpgrade failed,[SysSmartContract]EmptySmartContractVersionErr",
                "Alice",mockStub);
        //endregion


    }

    public void testDeploy(String smartContractName,String version,String path,
                            String expectErrorMsg,String caller,
                            MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        SmartContractPackage.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartContractName, path, version, initArgs, false);
        } catch (SysSmartContractException e) {
            e.printStackTrace();
            return;
        }
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.DEPLOY));
        args0.add(ByteString.copyFromUtf8("test"));
        args0.add(ByteString.copyFrom(cdsBytes));
        SmartContractPackage.SmartContractSpec spec=SmartContractPackage.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = null;
        try {
            signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec);
        } catch (JavaChainException e) {
            e.printStackTrace();
            return;
        }

        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);

        if(expectErrorMsg=="") //According to  status  or  message
        {
            //尚未完全实现调通全部逻辑，返回内部错误
            assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        }
        else
        {
            assertThat(res.getMessage(),is(expectErrorMsg));
        }


    }

    @Test
    public void testUpgrades(){
        String path="src/main/java/org/bcia/julongchain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
            testUpgrade("mycc","1.0",path,"","Alice",mockStub);  //  TESTED  OK

      //  testUpgrade("example02","1.0",path,"123","Alice",mockStub);   //   T   invalid version
    }

    public void testUpgrade(String smartContractName,String version,String path,
                           String expectErrorMsg,String caller,
                           MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        SmartContractPackage.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartContractName, path, version, initArgs, false);
        } catch (SysSmartContractException e) {
            e.printStackTrace();
            return;
        }
        byte[] cdsBytes = ProtoUtils.marshalOrPanic(cds);
        List<ByteString> args0 = new LinkedList<ByteString>();
        args0.add(ByteString.copyFromUtf8(LSSC.UPGRADE));
        args0.add(ByteString.copyFromUtf8("test"));
        args0.add(ByteString.copyFrom(cdsBytes));
        SmartContractPackage.SmartContractSpec spec=SmartContractPackage.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = null;
        try {
            signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec);
        } catch (JavaChainException e) {
            e.printStackTrace();
            return;
        }

        ISmartContract.SmartContractResponse res = mockStub.mockInvokeWithSignedProposal("1", args0, signedProp);

        if(expectErrorMsg=="") //According to  status  or  message
        {
            //尚未完全实现调通全部逻辑，返回内部错误
            assertThat(res.getStatus(),is(ISmartContract.SmartContractResponse.Status.SUCCESS));
        }
        else
        {
            assertThat(res.getMessage(),is(expectErrorMsg));
        }

    }

    private SmartContractPackage.SmartContractDeploymentSpec constructDeploySpec(String smartContractName, String path, String version, List<String> initArgs, boolean bCreateFS) throws SysSmartContractException {
//        SmartContractPackage.SmartContractDeploymentSpec spec=SmartContractPackage.SmartContractDeploymentSpec.newBuilder().
//                setCodePackage(ByteString.copyFromUtf8("testcds")).build();
        SmartContractPackage.SmartContractID smartContractID = SmartContractPackage.SmartContractID.newBuilder().
                setName(smartContractName).setPath(path).setVersion(version).build();
        SmartContractPackage.SmartContractInput.Builder inputBuilder= SmartContractPackage.SmartContractInput.newBuilder();
        for (String initArg:initArgs) {
            ByteString arg = ByteString.copyFromUtf8(initArg);
            inputBuilder.addArgs(arg);
        }
        SmartContractPackage.SmartContractInput input=inputBuilder.build();
        SmartContractPackage.SmartContractSpec.Builder builder=SmartContractPackage.SmartContractSpec.newBuilder();
        builder.setType(SmartContractPackage.SmartContractSpec.Type.JAVA);
        builder.setSmartContractId(smartContractID);
        builder.setInput(input);
        SmartContractPackage.SmartContractSpec spec=builder.build();

        Map<String, File> map = IoUtil.getFileRelativePath(path);
        byte[] tarBytes=null;
        byte[] gzipBytes = null;
        try {
            tarBytes= IoUtil.tarWriter(map, 1024);
            gzipBytes = IoUtil.gzipWriter(tarBytes);
            //File file=new File("/opt/2.tar");
            //FileOutputStream stream=new FileOutputStream(file);
            //stream.write(tarBytes);
            //stream.close();
        } catch (Exception e) {
            String msg=String.format("Create tar file for %s failed:%s",smartContractName,e.getMessage());
            throw new SysSmartContractException(msg);
        }


        SmartContractPackage.SmartContractDeploymentSpec depSpec = SmartContractPackage.SmartContractDeploymentSpec.newBuilder()
             .setSmartContractSpec(spec).setCodePackage(ByteString.copyFrom(gzipBytes)).build();
        if(bCreateFS){
            //后面填充
        }
        return depSpec;
    }

    @Test
    public void testStarts(){
        String path="src/main/java/org/bcia/julongchain/examples/smartcontract/java/smartcontract_example02";
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        testStart("example02","1.0",path,"","Alice",mockStub);
    }

    public void testStart(String smartContractName,String version,String path,
                            String expectErrorMsg,String caller,
                            MockStub mockStub){
        List<String> initArgs=new LinkedList<String>();
        initArgs.add("init");
        initArgs.add("a");
        initArgs.add("100");
        initArgs.add("b");
        initArgs.add("200");

        SmartContractPackage.SmartContractDeploymentSpec cds = null;
        try {
            cds = constructDeploySpec(smartContractName, path, version, initArgs, false);
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
        SmartContractPackage.SmartContractSpec spec=SmartContractPackage.SmartContractSpec.newBuilder().build();
        ProposalPackage.SignedProposal signedProp = null;
        try {
            signedProp = TxUtils.mockSignedEndorserProposalOrPanic("testGroup", spec);
        } catch (JavaChainException e) {
            e.printStackTrace();
            return;
        }

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