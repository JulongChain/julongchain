/*
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
package org.bcia.julongchain.core.ledger.customtx;

import org.bcia.julongchain.common.exception.InvalidTxException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.protos.common.Common;

/**
 * 交易处理器借口
 * 对交易的处理以模拟结果集(TxSimulatorResults)的形式展现
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public interface IProcessor {

    void generateSimulationResults(Common.Envelope txEnvelop, ITxSimulator simulator, boolean initializingLedger) throws LedgerException, InvalidTxException;

}
