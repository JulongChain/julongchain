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
package org.bcia.julongchain.gossip.comm;

import org.bcia.julongchain.gossip.common.IMessageAcceptor;

/**
 * class description
 *
 * @author wanliangbing
 * @date 18-7-25
 * @company Dingxuan
 */
public class Channel {

    private IMessageAcceptor pred;
    private org.bcia.julongchain.core.smartcontract.shim.helper.Channel<Object> ch;

    public IMessageAcceptor getPred() {
        return pred;
    }

    public void setPred(IMessageAcceptor pred) {
        this.pred = pred;
    }

    public org.bcia.julongchain.core.smartcontract.shim.helper.Channel<Object> getCh() {
        return ch;
    }

    public void setCh(org.bcia.julongchain.core.smartcontract.shim.helper.Channel<Object> ch) {
        this.ch = ch;
    }
}
