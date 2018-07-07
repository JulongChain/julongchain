package org.bcia.julongchain.common.policycheck.cauthdsl;

import org.bcia.julongchain.common.exception.PolicyException;
import org.junit.Test;

import static org.bcia.julongchain.common.policycheck.cauthdsl.PolicyParser.checkPolicyStr;
import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 11/06/18
 * @company Aisino
 */
public class PolicyParserTest {

    @Test
    public void fromString() throws PolicyException {

        String policy = "OR(AND('A.member', 'B.member'), OR('C.admin', 'D.member'))";
        String str = PolicyParser.checkPolicyStr(policy);
        assertTrue(String.valueOf(str.equals("outof(ID,1,outof(ID,2,'A.member','B.member'),outof(ID,1,'C.admin','D.member'))")),true);
        //assertSame(str,"outof(ID,1,outof(ID,2,'A.member','B.member'),outof(ID,1,'C.admin','D.member'))");
        PolicyParser.fromString(policy);
    }
}