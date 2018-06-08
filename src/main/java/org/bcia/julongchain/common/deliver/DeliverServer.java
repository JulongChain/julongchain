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
package org.bcia.julongchain.common.deliver;

/**
 * @author zhangmingyang
 * @Date: 2018/5/29
 * @company Dingxuan
 */
public class DeliverServer {
    private IDeliverSupport support;
    private IPolicyChecker policyChecker;
    private ISend send;

    public DeliverServer(IDeliverSupport support, IPolicyChecker policyChecker, ISend send) {
        this.support = support;
        this.policyChecker = policyChecker;
        this.send = send;
    }

    public IDeliverSupport getSupport() {
        return support;
    }

    public IPolicyChecker getPolicyChecker() {
        return policyChecker;
    }

    public ISend getSend() {
        return send;
    }


}
