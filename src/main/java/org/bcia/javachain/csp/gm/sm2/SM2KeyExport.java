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
package org.bcia.javachain.csp.gm.sm2;

import org.bcia.javachain.consenter.util.LoadYaml;
import org.bcia.javachain.csp.gm.sm2.util.SM2KeyUtil;
import org.bcia.javachain.csp.intfs.IKey;
import org.bouncycastle.util.encoders.Base64;

import java.util.HashMap;

/**
 * @author zhangmingyang
 * @Date: 2018/3/27
 * @company Dingxuan
 */
public class SM2KeyExport implements IKey {
    public SM2 sm2;

    public SM2KeyExport() {
        sm2 = new SM2();
    }

    @Override
    public byte[] toBytes() {
        //根据nodeid获取私钥,路径可通过配置文件中读取
        HashMap map = (HashMap) LoadYaml.readYamlFile("gmcsp.yaml");
        String privateKeyPath = (String) ((HashMap) ((HashMap) ((HashMap) ((HashMap) map.get("node")).get("csp")).get("gm")).get("fileKeyStore")).get("privateKeyStore");
        try {
            String privateKeyStr = SM2KeyUtil.readFile(privateKeyPath);
            byte[] privateKey = Base64.decode(privateKeyStr);
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        return true;
    }

    @Override
    public IKey getPublicKey() {
        return new SM2PublicKeyExport();
    }
}
