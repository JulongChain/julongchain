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
package org.bcia.julongchain.msp.util;

import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.julongchain.msp.mspconfig.MspConfig;
import org.bcia.julongchain.protos.msp.MspConfigPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.bcia.julongchain.msp.mspconfig.MspConfigFactory.loadMspConfig;

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
     * 构建mspconfig
     *
     * @return
     */
    public static MspConfigPackage.MSPConfig buildMspConfig(String mspPath,String mspId) {

        //拼接文件路径
        String cacertDir = filePath(mspPath, LoadLocalMspFiles.CACERTS);
        //TODO 密钥文件不装载
        String signcertDir = filePath(mspPath, LoadLocalMspFiles.SIGNCERTS);
        String admincertDir = filePath(mspPath, LoadLocalMspFiles.ADMINCERTS);
        String intermediatecertsDir = filePath(mspPath, LoadLocalMspFiles.INTERMEDIATECERTS);
        String crlsDir = filePath(mspPath, LoadLocalMspFiles.CRLSFOLDER);
        String configFile = filePath(mspPath, LoadLocalMspFiles.CONFIGFILENAME);
        String tlscacertDir = filePath(mspPath, LoadLocalMspFiles.TLSCACERTS);
        String tlsintermediatecertsDir = filePath(mspPath, LoadLocalMspFiles.TLSINTERMEDIATECERTS);

        //原有fabric证书
        LoadLocalMspFiles loadLocalMspFiles = new LoadLocalMspFiles();;

        //国密证书部分
        List<byte[]> caCerts=loadLocalMspFiles.getCertFromDir(cacertDir);
        List<byte[]> signCerts=loadLocalMspFiles.getCertFromDir(signcertDir);
        List<byte[]> adminCerts=loadLocalMspFiles.getCertFromDir(admincertDir);
        List<byte[]> intermediateCerts=loadLocalMspFiles.getCertFromDir(intermediatecertsDir);
        List<byte[]> CRLs=loadLocalMspFiles.getCertFromDir(crlsDir);
        List<byte[]> configFileContents=loadLocalMspFiles.getCertFromDir(configFile);
        List<byte[]> tlscaCerts=loadLocalMspFiles.getCertFromDir(tlscacertDir);
        List<byte[]> tlsintermediateCerts=loadLocalMspFiles.getCertFromDir(tlsintermediatecertsDir);
        MspConfigPackage.MSPConfig.Builder mspConfigBuilder=MspConfigBuilder.mspConfigBuilder(mspId,caCerts, signCerts,
                adminCerts, intermediateCerts, CRLs, configFileContents, tlscaCerts, tlsintermediateCerts);
        return mspConfigBuilder.build();
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
            absolutePath = prefixDir + "/" + suffixDir;
        }
        return absolutePath;
    }

}
