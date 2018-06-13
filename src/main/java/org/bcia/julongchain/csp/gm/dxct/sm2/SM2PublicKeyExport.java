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
package org.bcia.julongchain.csp.gm.dxct.sm2;

import org.bcia.julongchain.consenter.util.LoadYaml;
import org.bcia.julongchain.csp.gm.dxct.sm2.util.SM2KeyUtil;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.FileNotFoundException;
import java.util.HashMap;

import static org.bcia.julongchain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/3/27
 * @company Dingxuan
 */
public class SM2PublicKeyExport extends SM2KeyExport {
    public SM2PublicKeyExport() {
    }

    @Override
    public byte[] toBytes() {
        //根据nodeid获取私钥,路径可通过配置文件中读取
       // URL url = SM2PublicKeyExport.class.getClassLoader().getResource("publickey.pem");
        String publicKeyPath = "";
        try {
            publicKeyPath = loadMspConfig().getNode().getCsp().getFactoryOpts().get("gm").get("publicKeyStore");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String publicKeyStr = null;
        try {
            publicKeyStr = SM2KeyUtil.readFile(publicKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] publicKey = Base64.decode(publicKeyStr);
        System.out.println("publicKey："+ Hex.toHexString(publicKey));
        return publicKey;

    }

    @Override
    public byte[] ski() {
        return new byte[0];
    }

    @Override
    public boolean isSymmetric() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

}
