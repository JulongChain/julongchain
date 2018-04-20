/**
 * Copyright DingXuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.msp.util;

import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.GmFactoryOpts;
import org.bcia.javachain.msp.mspconfig.MspConfig;
import org.bcia.javachain.protos.msp.MspConfigPackage;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.bcia.javachain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * 构造msp管理中所需要的数据
 *
 * @author zhangmingyang
 * @Date: 2018/4/12
 * @company Dingxuan
 */
public class MspConfigHelper {

    private static final String WINDOWS = "win";

    /**
     * 读取配置文件构建csp选项list
     *
     * @return
     */
    public static List<IFactoryOpts> buildFactoryOpts() {
        List<IFactoryOpts> optsList = new ArrayList<IFactoryOpts>();
        MspConfig mspConfig = null;
        try {
            mspConfig = loadMspConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String symmetrickey = mspConfig.node.getCsp().getGm().getSymmetricKey();
        String sign = mspConfig.node.getCsp().getGm().getSign();
        String hash = mspConfig.node.getCsp().getGm().getHash();
        String asymmetric = mspConfig.node.getCsp().getGm().getAsymmetric();
        String privateKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPrivateKeyStore();
        String publicKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPublicKeyStore();
        optsList.add(new GmFactoryOpts(symmetrickey, asymmetric, hash, sign, publicKeyPath, privateKeyPath));
        return optsList;
    }

    /**
     * 构建mspconfig
     *
     * @return
     */
    public static MspConfigPackage.MSPConfig buildMspConfig(String mspPath,String mspId) {

        //拼接文件路径
        String cacertDir = filePath(mspPath, LoadLocalMspFiles.CACERTS);
        String keystoreDir = filePath(mspPath, LoadLocalMspFiles.KEYSTORE);
        String signcertDir = filePath(mspPath, LoadLocalMspFiles.SIGNCERTS);
        String admincertDir = filePath(mspPath, LoadLocalMspFiles.ADMINCERTS);
        String intermediatecertsDir = filePath(mspPath, LoadLocalMspFiles.INTERMEDIATECERTS);
        String crlsDir = filePath(mspPath, LoadLocalMspFiles.CRLSFOLDER);
        String configFile = filePath(mspPath, LoadLocalMspFiles.CONFIGFILENAME);
        String tlscacertDir = filePath(mspPath, LoadLocalMspFiles.TLSCACERTS);
        String tlsintermediatecertsDir = filePath(mspPath, LoadLocalMspFiles.TLSINTERMEDIATECERTS);

        //读取文件内容,获取文件夹中文件字符串列表
        LoadLocalMspFiles loadLocalMspFiles = new LoadLocalMspFiles();
        List<String> cacert = loadLocalMspFiles.getPemMaterialFromDir(cacertDir);
        List<String> keystore = loadLocalMspFiles.getPemMaterialFromDir(keystoreDir);
        List<String> signcert = loadLocalMspFiles.getPemMaterialFromDir(signcertDir);
        List<String> admincert = loadLocalMspFiles.getPemMaterialFromDir(admincertDir);
        List<String> intermediatecerts = loadLocalMspFiles.getPemMaterialFromDir(intermediatecertsDir);
        List<String> crls = loadLocalMspFiles.getPemMaterialFromDir(crlsDir);
        List<String> configFileContent = loadLocalMspFiles.getPemMaterialFromDir(configFile);
        List<String> tlscacert = loadLocalMspFiles.getPemMaterialFromDir(tlscacertDir);
        List<String> tlsintermediatecerts = loadLocalMspFiles.getPemMaterialFromDir(tlsintermediatecertsDir);

        //构建mspconfig
        MspConfigPackage.MSPConfig mspConfig = MspConfigBuilder.buildMspConfig(mspId,cacert, keystore, signcert,
                admincert, intermediatecerts, crls, configFileContent, tlscacert, tlsintermediatecerts);
        return mspConfig;
    }


    /**
     * 拼接字符串
     *
     * @param prefixDir
     * @param suffixDir
     * @return
     */
    public static String filePath(String prefixDir, String suffixDir) {
        String absolutePath;
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith(WINDOWS)) {
            absolutePath = prefixDir + "\\" + suffixDir;
        } else {
            absolutePath = prefixDir + "//" + suffixDir;
        }
        return absolutePath;
    }

}
