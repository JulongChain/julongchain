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
       /* PolicyParser policyParser = new PolicyParser();
        String p = "or('zuoxuesong1.admin',or(and('zuoxuesong2.member','zuoxuesong3.member'),'zuoxuesong1.member'))";

        policyParser.fromString(p);*/
        String policy = "OR(AND('A.member', 'B.member'), OR('C.admin', 'D.member'))";
        //String policy = "OR('A.member', AND('B.member', 'C.member'))";

        String[] roles = new String[1];
        roles[0] = "peerorg2.member";
        PolicyParser.fromString(policy);
        //System.out.println(checkPolicyStr(policy));
    }
}