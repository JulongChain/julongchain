/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.julongchain.common.policycheck.bean;

import org.bcia.julongchain.common.policies.SignaturePolicy;
import org.bcia.julongchain.protos.common.MspPrincipal;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 02/05/18
 * @company Aisino
 *
 */
@Deprecated
public class SignaturePolicyEnvelope {
    private int version;
    private SignaturePolicy rule;
    private MspPrincipal[] iIdentitys;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public SignaturePolicy getRule() {
        return rule;
    }

    public void setRule(SignaturePolicy rule) {
        this.rule = rule;
    }

    public MspPrincipal[] getiIdentitys() {
        return iIdentitys;
    }

    public void setiIdentitys(MspPrincipal[] iIdentitys) {
        this.iIdentitys = iIdentitys;
    }
}
