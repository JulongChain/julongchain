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
package org.bcia.julongchain.core.smartcontract;

import org.bcia.julongchain.protos.node.SmartContractShim;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/18
 * @company Dingxuan
 */
public class NextStateInfo {

    private SmartContractShim.SmartContractMessage msg;
    private Boolean sendToCC;
    private Boolean sendSync;

    public SmartContractShim.SmartContractMessage getMsg() {
        return msg;
    }

    public void setMsg(SmartContractShim.SmartContractMessage msg) {
        this.msg = msg;
    }

    public Boolean getSendToCC() {
        return sendToCC;
    }

    public void setSendToCC(Boolean sendToCC) {
        this.sendToCC = sendToCC;
    }

    public Boolean getSendSync() {
        return sendSync;
    }

    public void setSendSync(Boolean sendSync) {
        this.sendSync = sendSync;
    }
}
