package org.bcia.javachain.csp.gm;

/**
 * Copyright BCIA. All Rights Reserved.
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

/**
 * @author zhanglin
 * @purpose Define the interface, DecrypterOpts
 * @date 2018-01-25
 * @company Dingxuan
 */

public class GmFactoryOpts implements IGmFactoryOpts {
    private int secLevel;
    private String hashFamily;
    private boolean bEphemeral;
    private boolean bDummyKeyStore;
    private String keyStorePath;
    private boolean bDefaultCsp;

    public GmFactoryOpts(int secLevel,String hashFamily,boolean bEphemeral,
                         boolean bDummyKeyStore,String keyStorePath,boolean bDefaultCsp){
        this.secLevel=secLevel;
        this.hashFamily=hashFamily;
        this.bEphemeral=bEphemeral;
        this.bDummyKeyStore=bDummyKeyStore;
        this.keyStorePath=keyStorePath;
        this.bDefaultCsp=bDefaultCsp;
    }

    @Override
    public int getSecLevel() {
        return secLevel;
    }

    @Override
    public String getHashFamily() {
        return hashFamily;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public String getKeyStorePath() {
        return null;
    }

    @Override
    public boolean isDummyKeystore() {
        return false;
    }

    @Override
    public String getProviderName() {
        return null;
    }

    @Override
    public String getProviderDescription() {
        return null;
    }

    @Override
    public boolean isDefaultCsp() {
        return bDefaultCsp;
    }
}
