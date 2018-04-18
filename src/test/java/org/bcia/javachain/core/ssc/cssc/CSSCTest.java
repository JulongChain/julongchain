package org.bcia.javachain.core.ssc.cssc;

import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractResponse;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractStub;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * CSSC的单元测试类
 *
 * @author sunianle
 * @date 4/4/18
 * @company Dingxuan
 */
public class CSSCTest {
    @Autowired
    private CSSC cssc;
    @Mock
    private SmartContractStub stub;

    @Test
    public void init() {
        SmartContractResponse smartContractResponse = cssc.init(stub);
        assertThat(smartContractResponse.getStatus(), is(SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void invoke() {

    }
}