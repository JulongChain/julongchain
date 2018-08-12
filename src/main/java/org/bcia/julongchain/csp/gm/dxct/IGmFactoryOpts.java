package org.bcia.julongchain.csp.gm.dxct;

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

import org.bcia.julongchain.csp.factory.IFactoryOpts;

/**
 * @author zhanglin
 * @purpose Define the interface, IGmFactoryOpts
 * @date 2018-01-25
 * @company Dingxuan
 */

// The IGmFactoryOpts contains options for the GmFactory.
public interface IGmFactoryOpts extends IFactoryOpts {

    //Algorithm options.
    int getSecLevel();
    String getHashFamily();

    // Keystore options.
    boolean isEphemeral();
    String getKeyStorePath();
    boolean isDummyKeystore();

}
