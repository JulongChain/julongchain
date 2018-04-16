package org.bcia.javachain.core.ssc.cssc;

import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractStub;
import org.bcia.javachain.core.ssc.essc.ESSC;
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
        Response response = cssc.init(stub);
        assertThat(response.getStatus(), is(Response.Status.SUCCESS));
    }

    @Test
    public void invoke() {

    }
}