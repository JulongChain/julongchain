package org.bcia.javachain.common.policycheck;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.policies.PolicyConstant;
import org.bcia.javachain.common.policies.PolicyManager;
import org.bcia.javachain.common.policycheck.bean.SignedProposal;
import org.bcia.javachain.common.policycheck.policies.ChannelPolicyManager;
import org.bcia.javachain.common.policycheck.policies.IChannelPolicyManagerGetter;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.msp.mgmt.IMspPrincipalGetter;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.UnsupportedEncodingException;
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
        List mock1 = mock(List.class);
        //ChannelPolicyManager iChannelPolicyManagerGetter = mock(ChannelPolicyManager.class);
        //iChannelPolicyManagerGetter.manager("123");
        IIdentityDeserializer localMSP = mock(IIdentityDeserializer.class);
        IMspPrincipalGetter principalGetter = mock(IMspPrincipalGetter.class);
        PolicyChecker policyChecker = new PolicyChecker(new ChannelPolicyManager(),localMSP,principalGetter);
        policyChecker.checkPolicy("A", "Admins",new SignedProposal("Alicesaaa".getBytes("ISO-8859-1"),"msg1".getBytes("ISO-8859-1")));


    }

    @Test
    public void checkPolicyBySignedData() {

    }

    @Test
    public void checkPolicyNoChannel() {
    }


}