package org.bcia.javachain.core.ssc;

import org.bcia.javachain.BaseJunit4Test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/7/18
 * @company Dingxuan
 */
public class SystemSmartContractManagerTest extends BaseJunit4Test {
    @Autowired
    SystemSmartContractManager manager;
    @Test
    public void registerSysSmartContracts() {
        manager.registerSysSmartContracts();
    }

    @Test
    public void deploySysSmartContracts() {
    }

    @Test
    public void deDeploySysSmartContracts() {
    }

    @Test
    public void isSysSmartContract() {
    }

    @Test
    public void isWhitelisted() {
    }

    @Test
    public void getSystemSmartContract() {
    }
}