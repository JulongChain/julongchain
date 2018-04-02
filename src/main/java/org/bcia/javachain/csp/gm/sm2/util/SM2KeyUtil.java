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
package org.bcia.javachain.csp.gm.sm2.util;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.csp.gm.sm2.SM2Key;
import org.bcia.javachain.csp.gm.sm2.SM2KeyExport;
import org.bcia.javachain.csp.gm.sm2.SM2KeyGenOpts;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.csp.intfs.opts.IKeyGenOpts;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author zhangmingyang
 * @Date: 2018/4/2
 * @company Dingxuan
 */
public class SM2KeyUtil {
    /**
     * 生成非对称密钥文件
     * @param k
     * @param privatePath
     * @param publicKey
     * @param opts
     */
    public static void keyFileGen(IKey k, String privatePath, String publicKey, IKeyGenOpts opts) {
        if(opts instanceof SM2KeyGenOpts){
            SM2Key sm2Key= (SM2Key) k;
            SM2KeyFileGen sm2KeyFileGen=new SM2KeyFileGen(sm2Key);
            sm2KeyFileGen.privateKeyFileGen(privatePath);
            sm2KeyFileGen.publicKeyFileGen(publicKey);
        }
    }

    /**
     * 获取非对称密钥文件
     * @param opts
     * @return
     * @throws JavaChainException
     */
    public static IKey getKey(IKeyGenOpts opts) throws JavaChainException {
        if(opts instanceof SM2KeyGenOpts){
            return new SM2KeyExport();
        }
        return null;
    }
}
