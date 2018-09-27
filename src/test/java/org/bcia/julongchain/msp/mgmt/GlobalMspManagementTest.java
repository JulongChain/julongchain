package org.bcia.julongchain.msp.mgmt;

import org.bcia.julongchain.common.exception.MspException;
import org.bcia.julongchain.common.exception.VerifyException;
import org.bcia.julongchain.csp.factory.CspOptsManager;
import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mspconfig.MspConfig;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.bcia.julongchain.msp.mspconfig.MspConfigFactory.loadMspConfig;
import static org.junit.Assert.assertEquals;

/**
 * 全局msp管理测试
 *
 * @author zhangmingyang
 * @Date: 2018/4/12
 * @company Dingxuan
 */
public class GlobalMspManagementTest {

    @Test
    public void loadLocalMspWithType() throws FileNotFoundException, VerifyException, MspException {
        String testData = "test data";
        MspConfig mspConfig = loadMspConfig();
        String mspConfigDir = mspConfig.getNode().getMspConfigPath();
        String mspId = mspConfig.getNode().getLocalMspId();
        String defaultOpts = mspConfig.getNode().getCsp().getDefaultValue();

        CspOptsManager cspOptsManager = CspOptsManager.getInstance();
        cspOptsManager.addAll(defaultOpts, mspConfig.getNode().getCsp().getFactoryOpts());
        List<IFactoryOpts> optsList = cspOptsManager.getFactoryOptsList();
        IMsp msp = GlobalMspManagement.loadLocalMspWithType(mspConfigDir, optsList, defaultOpts, mspId);
        byte[] signData = msp.getDefaultSigningIdentity().sign(testData.getBytes());
        msp.getDefaultSigningIdentity().getIdentity().verify(testData.getBytes(), signData);

    }

    @Test
    public void getLocalMsp() throws VerifyException {
        String testData = "test data";
        ISigningIdentity signer = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] signdata = signer.sign(testData.getBytes());
        signer.getIdentity().verify(testData.getBytes(), signdata);
    }

}