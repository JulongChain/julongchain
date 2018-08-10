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
package org.bcia.julongchain.msp.mgmt;

import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.csp.factory.CspOptsManager;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2PrivateKeyImportOpts;
import org.bcia.julongchain.csp.gm.dxct.sm2.SM2SignerOpts;
import org.bcia.julongchain.csp.intfs.IKey;
import org.bcia.julongchain.msp.IIdentity;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.signer.NodeSigner;
import org.bcia.julongchain.msp.util.LoadLocalMspFiles;

import static org.bcia.julongchain.csp.factory.CspManager.getDefaultCsp;

/**
 * 签名身份实体
 *
 * @author zhangmingyang
 * @date 2018/07/04
 * @company Dingxuan
 */
public class SignIdentity implements ISigningIdentity {

    private static JulongChainLog log = JulongChainLogFactory.getLog(SignIdentity.class);

    private Identity identity;
    private NodeSigner nodeSigner;
    private Msp msp;

    public SignIdentity(Identity identity, NodeSigner nodeSigner, Msp msp) {
        this.identity = identity;
        this.nodeSigner = nodeSigner;
        this.msp = msp;
    }

    @Override
    public byte[] sign(byte[] msg) {
        byte[] signvalue = null;
        try {
            //获取默认的工厂选项的密钥存储路经
            String sk_path = CspOptsManager.getInstance().getDefaultFactoryOpts().getKeyStore();
            byte[] skBytes = new LoadLocalMspFiles().getSkFromDir(sk_path).get(0);
            //TODO 后续判断是哪一种工厂选项,根据具体的签名类型,构造对应的密钥导入选项
            IKey privateKey = getDefaultCsp().keyImport(skBytes, new SM2PrivateKeyImportOpts(true));
            signvalue = msp.getCsp().sign(privateKey, msg, new SM2SignerOpts());
            log.info("Signvalue is ok");
        } catch (JulongChainException e) {
            log.error(e.getMessage());
        }
        return signvalue;
    }

    @Override
    public IIdentity getIdentity() {
        return this.identity;
    }

    public NodeSigner getNodeSigner() {
        return nodeSigner;
    }

    public Msp getMsp() {
        return msp;
    }
}
