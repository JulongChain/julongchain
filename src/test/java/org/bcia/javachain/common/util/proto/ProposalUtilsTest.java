package org.bcia.javachain.common.util.proto;

import org.bcia.javachain.common.exception.JavaChainException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/28
 * @company Dingxuan
 */
public class ProposalUtilsTest {

    @Test
    public void buildSignedProposal() {
    }

    @Test
    public void buildSmartContractProposal() {
    }

    @Test
    public void buildProposalPayload() {
    }

    @Test
    public void computeProposalTxID() throws JavaChainException {
        long beginTime = System.currentTimeMillis();
        String txId = ProposalUtils.computeProposalTxID("zhouhui".getBytes(), "12345678907879887908".getBytes());
        long endTime = System.currentTimeMillis();

        System.out.println(txId + ",耗时"+ (endTime-beginTime) + "ms");
    }
}