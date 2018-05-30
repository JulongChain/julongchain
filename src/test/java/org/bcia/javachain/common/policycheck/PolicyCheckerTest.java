package org.bcia.javachain.common.policycheck;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.policies.PolicyConstant;
import org.bcia.javachain.common.policies.PolicyManager;
import org.bcia.javachain.common.policycheck.bean.SignedProposal;
import org.bcia.javachain.common.policycheck.policies.ChannelPolicyManager;
import org.bcia.javachain.common.policycheck.policies.IChannelPolicyManagerGetter;
import org.bcia.javachain.common.util.proto.SignedData;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.mgmt.IMspPrincipalGetter;
import org.bcia.javachain.msp.mgmt.MspManager;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    IChannelPolicyManagerGetter iChannelPolicyManagerGetter;
    @Mock
    IIdentityDeserializer localMSP;
    @Mock
    MspPrincipal.MSPPrincipal principalGetter;*/


    @Before
    public void setUp() {


        System.out.println("setup...");
    }

    @Test
    public void checkPolicy() throws InvalidProtocolBufferException, PolicyException, UnsupportedEncodingException {
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new ChannelPolicyManager(),localMSP,principalGetter);
        policyChecker.checkPolicy("A", "Admins",new SignedProposal("Alicesaaa".getBytes(),"msg1".getBytes()));


    }

    @Test
    public void checkPolicyBySignedData() {
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new ChannelPolicyManager(),localMSP,principalGetter);
        List<SignedData> sd = new ArrayList<SignedData>();
        policyChecker.checkPolicyBySignedData("","admin",sd);
        policyChecker.checkPolicyBySignedData("A","",sd);
        policyChecker.checkPolicyBySignedData("A","admin",null);

    }

    @Test
    public void checkPolicyNoChannel() {
        String reader = "readers";
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        MspManager mspManager = new MspManager();
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new ChannelPolicyManager(),mspManager,principalGetter);
        SignedProposal signedProposal = new SignedProposal("Alicesaaa".getBytes(),"msg1".getBytes());
        policyChecker.checkPolicyNoChannel(reader,signedProposal);
    }


}