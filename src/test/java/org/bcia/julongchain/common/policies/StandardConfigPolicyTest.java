package org.bcia.julongchain.common.policies;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.common.policies.config.*;
import org.bcia.julongchain.protos.common.Policies;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 标准配置策略测试类
 *
 * @author zhouhui
 * @date 2018/4/14
 * @company Dingxuan
 */
public class StandardConfigPolicyTest {
    private IConfigPolicy implicitMetaAllPolicy;
    private IConfigPolicy implicitMetaAnyPolicy;
    private IConfigPolicy implicitMetaMajorityPolicy;
    private IConfigPolicy signaturePolicy;

    @Before
    public void setUp() throws Exception {
        implicitMetaAllPolicy = new ImplicitMetaAllPolicy(GroupConfigConstant.POLICY_WRITERS);
        implicitMetaAnyPolicy = new ImplicitMetaAnyPolicy(GroupConfigConstant.POLICY_READERS);
        implicitMetaMajorityPolicy = new ImplicitMetaMajorityPolicy(GroupConfigConstant.POLICY_ADMINS);

        signaturePolicy = new SignaturePolicy(GroupConfigConstant.POLICY_ADMINS, Policies.SignaturePolicyEnvelope
                .newBuilder().build());
    }

    @Test
    public void getKey() {
        assertEquals(GroupConfigConstant.POLICY_WRITERS, implicitMetaAllPolicy.getKey());
        assertEquals(GroupConfigConstant.POLICY_READERS, implicitMetaAnyPolicy.getKey());
        assertEquals(GroupConfigConstant.POLICY_ADMINS, implicitMetaMajorityPolicy.getKey());
        assertEquals(GroupConfigConstant.POLICY_ADMINS, signaturePolicy.getKey());
    }

    @Test
    public void getValue() throws InvalidProtocolBufferException {
        Policies.Policy allPolicy = implicitMetaAllPolicy.getValue();
        Policies.Policy anyPolicy = implicitMetaAnyPolicy.getValue();
        Policies.Policy majorityPolicy = implicitMetaMajorityPolicy.getValue();
        Policies.Policy signPolicy = signaturePolicy.getValue();

        assertEquals(Policies.Policy.PolicyType.IMPLICIT_META_VALUE, allPolicy.getType());
        assertEquals(Policies.Policy.PolicyType.IMPLICIT_META_VALUE, anyPolicy.getType());
        assertEquals(Policies.Policy.PolicyType.IMPLICIT_META_VALUE, majorityPolicy.getType());
        assertEquals(Policies.Policy.PolicyType.SIGNATURE_VALUE, signPolicy.getType());

        Policies.ImplicitMetaPolicy implicitMetaPolicyAll = Policies.ImplicitMetaPolicy.parseFrom(allPolicy.getValue());
        Policies.ImplicitMetaPolicy implicitMetaPolicyAny = Policies.ImplicitMetaPolicy.parseFrom(anyPolicy.getValue());
        Policies.ImplicitMetaPolicy implicitMetaPolicyMajority = Policies.ImplicitMetaPolicy.parseFrom(majorityPolicy.getValue());
        Policies.SignaturePolicyEnvelope signaturePolicyEnvelope = Policies.SignaturePolicyEnvelope.parseFrom(signPolicy.getValue());

        assertEquals(GroupConfigConstant.POLICY_WRITERS, implicitMetaPolicyAll.getSubPolicy());
        assertEquals(GroupConfigConstant.POLICY_READERS, implicitMetaPolicyAny.getSubPolicy());
        assertEquals(GroupConfigConstant.POLICY_ADMINS, implicitMetaPolicyMajority.getSubPolicy());

        assertEquals(Policies.ImplicitMetaPolicy.Rule.ALL, implicitMetaPolicyAll.getRule());
        assertEquals(Policies.ImplicitMetaPolicy.Rule.ANY, implicitMetaPolicyAny.getRule());
        assertEquals(Policies.ImplicitMetaPolicy.Rule.MAJORITY, implicitMetaPolicyMajority.getRule());
    }
}