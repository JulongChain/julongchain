package org.bcia.javachain.core.ssc.essc;

import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.core.smartcontract.shim.impl.Handler;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractStub;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/8/18
 * @company Dingxuan
 */
public class ESSCTest extends BaseJunit4Test {
    @Autowired
    ESSC essc;
    @Mock
    private SmartContractStub stub;
    @Test
    public void init() {
        Response response = essc.init(stub);
        assertThat(response.getStatus(),is(Response.Status.SUCCESS));
    }

    @Test
    public void invoke() {

    }

    @Test
    public void getSmartContractStrDescription() {
        String description=essc.getSmartContractStrDescription();
        String expectedResult="与背书相关的系统智能合约";
        assertThat(description,is(expectedResult));
    }


}