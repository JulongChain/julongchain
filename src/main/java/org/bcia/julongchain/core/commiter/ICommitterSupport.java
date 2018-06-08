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
package org.bcia.julongchain.core.commiter;

import org.bcia.julongchain.common.exception.CommitterException;
import org.bcia.julongchain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.julongchain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.msp.IMspManager;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public interface ICommitterSupport {
    void acquire(long n) throws CommitterException;

    void release(long n);

    INodeLedger getLedger();

    IMspManager getMspManager();

    void apply(Configtx.ConfigEnvelope configtx) throws CommitterException;

    String[] getMspIds(String groupId);

    IApplicationCapabilities getCapabilities();

    ISmartContractDefinition getSmartContractByName(String groupId, String scName);
}
