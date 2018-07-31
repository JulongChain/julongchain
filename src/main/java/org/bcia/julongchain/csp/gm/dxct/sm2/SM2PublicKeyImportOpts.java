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

import org.bcia.julongchain.csp.intfs.opts.IKeyImportOpts;

/**
 * 类描述
 *
 * @author zhangmingyang
 * @date 2018/07/06
 * @company Dingxuan
 */
public class SM2PublicKeyImportOpts implements IKeyImportOpts {

    private boolean isEphemeral;
    public SM2PublicKeyImportOpts(boolean isEphemeral) {
        this.isEphemeral = isEphemeral;
    }
    @Override
    public String getAlgorithm() {
        return "SM2";
    }

    @Override
    public boolean isEphemeral() {
        return isEphemeral;
    }
}
