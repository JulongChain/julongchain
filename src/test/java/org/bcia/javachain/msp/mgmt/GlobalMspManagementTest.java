package org.bcia.javachain.msp.mgmt;

import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.GmFactoryOpts;
import org.bcia.javachain.msp.mspconfig.MspConfig;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.bcia.javachain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/4/12
 * @company Dingxuan
 */
public class GlobalMspManagementTest {

    @Test
    public void loadLocalMspWithType() throws FileNotFoundException {
        String localmspdir="E:\\msp";
        String mspID="bciamsp";
        String mspType="GmSoftMsp";
        List<IFactoryOpts> optsList=new ArrayList<IFactoryOpts>();
        MspConfig mspConfig=loadMspConfig();
        String symmetrickey = mspConfig.node.getCsp().getGm().getSymmetricKey();
        String sign = mspConfig.node.getCsp().getGm().getSign();
        String hash = mspConfig.node.getCsp().getGm().getHash();
        String asymmetric = mspConfig.node.getCsp().getGm().getAsymmetric();
        String privateKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPrivateKeyStore();
        String publicKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPublicKeyStore();
        //new GmCspConfig(symmetrickey,asymmetric,hash,sign,publicKeyPath,privateKeyPath);
        optsList.add(new GmFactoryOpts(symmetrickey,asymmetric,hash,sign,publicKeyPath,privateKeyPath));
        GlobalMspManagement.loadLocalMspWithType(localmspdir,optsList,mspID,mspType);
        GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity().sign("123".getBytes());
    }

    @Test
    public void loadlocalMsp() {
    }

    @Test
    public void getLocalMsp() {
       Identity signer= (Identity) GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
       byte[] signdata=signer.sign("123".getBytes());
       signer.verify("123".getBytes(), signdata);
    }

    @Test
    public void getIdentityDeserializer() {

    }

    @Test
    public void getManagerForChain() {
    }
}