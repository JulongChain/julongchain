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
package org.bcia.julongchain.core.smartcontract.shim;

import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ITxSimulator;

import javax.naming.Context;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public class SmartContractProvider {

    private ITxSimulator txsim;

    /** GetContext returns a context for the supplied ledger, with the appropriate tx simulator
     *
     */
    public Context getContext(INodeLedger ledger) {
        return null;
    }

    public ITxSimulator getTxsim() {
        return txsim;
    }

    public void setTxsim(ITxSimulator txsim) {
        this.txsim = txsim;
    }
}
