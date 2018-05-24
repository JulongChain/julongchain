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

package org.bcia.javachain.common.policycheck.bean;

import org.bcia.javachain.common.policycheck.policies.IChannelPolicyManagerGetter;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.protos.common.MspPrincipal;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 14/05/18
 * @company Aisino
 */
public class PolicyChecker {
    IChannelPolicyManagerGetter IChannelPolicyManagerGetter;
    IIdentity localMSP;
    MspPrincipal principalGetter;

    public IChannelPolicyManagerGetter getIChannelPolicyManagerGetter() {
        return IChannelPolicyManagerGetter;
    }

    public void setIChannelPolicyManagerGetter(IChannelPolicyManagerGetter IChannelPolicyManagerGetter) {
        this.IChannelPolicyManagerGetter = IChannelPolicyManagerGetter;
    }

    public IIdentity getLocalMSP() {
        return localMSP;
    }

    public void setLocalMSP(IIdentity localMSP) {
        this.localMSP = localMSP;
    }

    public MspPrincipal getPrincipalGetter() {
        return principalGetter;
    }

    public void setPrincipalGetter(MspPrincipal principalGetter) {
        this.principalGetter = principalGetter;
    }
}
