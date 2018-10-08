package org.bcia.julongchain.csp.gm.dxct;

/**
 * Copyright Dingxuan. All Rights Reserved.
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

import org.bcia.julongchain.csp.factory.IFactoryOpts;
import org.bcia.julongchain.csp.gm.dxct.util.GmCspConstant;

import java.util.List;
import java.util.Map;

/**
 * 国密工厂选项
 *
 * @author zhanglin, zhangmingyang
 * @date 2018-01-25
 * @company Dingxuan
 */

public class GmFactoryOpts implements IGmFactoryOpts {
    private String symmetricKeyType;
    private String asymmetricKeyType;
    private String hashType;
    private String signType;
    private String keyStorePath;
    private boolean bDefaultCsp;

    public GmFactoryOpts() {
    }

    @Override
    public String getProviderName() {
        return PROVIDER_GM;
    }

    @Override
    public String getProviderDescription() {
        return keyStorePath;
    }

    @Override
    public String getKeyStore() {
        return keyStorePath;
    }

    @Override
    public void parseFrom(Map<String, String> optMap) {
        this.symmetricKeyType = optMap.get(GmCspConstant.SYSMETRICKEY_TYPE);
        this.asymmetricKeyType = optMap.get(GmCspConstant.ASYMMETRICKEY_TYPE);
        this.hashType = optMap.get(GmCspConstant.HASH_TYPE);
        this.signType = optMap.get(GmCspConstant.SIGN_TYPE);
        this.keyStorePath = optMap.get(GmCspConstant.KEYSTROE_PATH);
    }

    @Override
    public int getSecLevel() {
        return 0;
    }

    @Override
    public String getHashFamily() {
        return null;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }

    @Override
    public boolean isDummyKeystore() {
        return bDefaultCsp;
    }

    public String getSymmetricKeyType() {
        return symmetricKeyType;
    }

    public String getAsymmetricKeyType() {
        return asymmetricKeyType;
    }

    public String getHashType() {
        return hashType;
    }

    public String getSignType() {
        return signType;
    }
}
