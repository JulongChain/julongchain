package org.bcia.julongchain.common.policycheck;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.policycheck.policies.GroupPolicyManagerGetter;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.common.util.proto.TxUtils;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.msp.mgmt.IMspPrincipalGetter;
import org.bcia.julongchain.msp.mgmt.MSPPrincipalGetter;
import org.bcia.julongchain.msp.mgmt.Msp;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 23/05/18
 * @company Aisino
 */
public class PolicyCheckerTest {
  /*  @Mock
    PolicyManager mockPolicyManager;
    @Mock
    IGroupPolicyManagerGetter iChannelPolicyManagerGetter;
    @Mock
    IIdentityDeserializer localMSP;
    @Mock
    MspPrincipal.MSPPrincipal principalGetter;*/


    @Before
    public void setUp() {


        System.out.println("setup...");
    }

    @Test
    public void checkPolicy() throws InvalidProtocolBufferException, JulongChainException, UnsupportedEncodingException {
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        Msp msp = new Msp();
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new GroupPolicyManagerGetter(),msp,principalGetter);
        ProposalPackage.SignedProposal sp = TxUtils.mockSignedEndorserProposalOrPanic("",
                SmartContractPackage.SmartContractSpec.newBuilder().build());
        policyChecker.checkPolicy("A", "Admins",sp);


    }

    @Test
    public void checkPolicyBySignedData() throws PolicyException {
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new GroupPolicyManagerGetter(),localMSP,principalGetter);
        List<SignedData> sd = new ArrayList<SignedData>();
        SignedData sd1 = new SignedData("A".getBytes(),"id1".getBytes(),"A".getBytes());
        SignedData sd2 = new SignedData("B".getBytes(),"id2".getBytes(),"B".getBytes());
        SignedData sd3 = new SignedData("C".getBytes(),"id3".getBytes(),"C".getBytes());
        sd.add(sd1);
        sd.add(sd2);
        sd.add(sd3);
        policyChecker.checkPolicyBySignedData("myGroup","Admins",sd);


    }

    @Test
    public void checkPolicyNoChannel() throws JulongChainException {
        IIdentityDeserializer localmsp = GlobalMspManagement.getLocalMsp();
        IMspPrincipalGetter principalGetter = new MSPPrincipalGetter();//mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new GroupPolicyManagerGetter(),localmsp,principalGetter);
        ProposalPackage.SignedProposal sp = TxUtils.mockSignedEndorserProposalOrPanic("",
                SmartContractPackage.SmartContractSpec.newBuilder().build());
        policyChecker.checkPolicyNoGroup("Admins",sp);

    }


}