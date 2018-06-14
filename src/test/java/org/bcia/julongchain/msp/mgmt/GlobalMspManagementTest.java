package org.bcia.julongchain.msp.mgmt;

import org.bcia.julongchain.common.exception.VerifyException;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.julongchain.msp.mspconfig.MspConfig;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.bcia.julongchain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/4/12
 * @company Dingxuan
 */
public class GlobalMspManagementTest {

    @Test
    public void loadLocalMspWithType() throws FileNotFoundException, VerifyException {
        String localmspdir = "D:\\msp";
        String mspID = "DEFAULT";
        String mspType = "GMMSP";
        List<IFactoryOpts> optsList = new ArrayList<IFactoryOpts>();
        MspConfig mspConfig = loadMspConfig();
        GmFactoryOpts factoryOpts=new GmFactoryOpts();
        factoryOpts.parseFrom(mspConfig.getNode().getCsp().getFactoryOpts().get("gm"));
        optsList.add(factoryOpts);
        GlobalMspManagement.loadLocalMspWithType(localmspdir, optsList, mspID, mspType);
        byte[] signData = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity().sign("123".getBytes());
        GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity().verify("123".getBytes(), signData);

    }

    @Test
    public void loadlocalMsp() {
    }

    @Test
    public void getLocalMsp() throws VerifyException {
        Identity signer = (Identity) GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] signdata = signer.sign("123".getBytes());
        signer.verify("123".getBytes(), signdata);
    }

    @Test
    public void getIdentityDeserializer() {

    }

    @Test
    public void getManagerForChain() {
    }
}