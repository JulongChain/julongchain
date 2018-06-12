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
package org.bcia.julongchain.core.ledger.customtx;

import org.bcia.julongchain.common.exception.InvalidTxException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.protos.common.Common;

/**
 * Processor allows to generate simulation results during commit time for custom transactions.
 * A custom processor may represent the information in a propriety fashion and can use this process to translate
 * the information into the form of `TxSimulationResults`. Because, the original information is signed in a
 * custom representation, an implementation of a `Processor` should be cautious that the custom representation
 * is used for simulation in an deterministic fashion and should take care of compatibility cross fabric versions.
 * 'initializingLedger' true indicates that either the transaction being processed is from the genesis block or the ledger is
 * synching the state (which could happen during peer startup if the statedb is found to be lagging behind the blockchain).
 * In the former case, the transactions processed are expected to be valid and in the latter case, only valid transactions
 * are reprocessed and hence any validation can be skipped.
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public interface IProcessor {

    void generateSimulationResults(Common.Envelope txEnvelop, ITxSimulator simulator, boolean initializingLedger) throws LedgerException, InvalidTxException;

}
