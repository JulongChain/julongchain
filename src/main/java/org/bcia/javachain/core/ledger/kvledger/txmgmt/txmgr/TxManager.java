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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.BlockAndPvtData;
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.kvledger.Recoverable;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.common.Common;

import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public interface TxManager extends Recoverable {

    IQueryExecutor newQueryExecutor(String txid) throws LedgerException;
    ITxSimulator newTxSimulator(String txid) throws LedgerException;
    void validateAndPrepare(BlockAndPvtData blockAndPvtData, Boolean doMVCCValidation) throws LedgerException;
    Height getLastSavepoint() throws LedgerException;
    long shouldRecover() throws LedgerException;
    void commitLostBlock(BlockAndPvtData blockAndPvtData) throws LedgerException;
    void commit() throws LedgerException;
    void rollback() throws LedgerException;
    void shutdown() throws LedgerException;
}
