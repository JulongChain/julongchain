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
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.kvledger.Recoverable;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.common.Common;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public interface TxMgr extends Recoverable {

    IQueryExecutor newQueryExecutor(String txid) throws LedgerException;
    ITxSimulator newTxSimulator(String txid) throws LedgerException;
    void validateAndPrepare(Common.Block block, Boolean doMVCCValidation) throws LedgerException;
    Height heightGetLastSavepoint() throws LedgerException;
    long shouldRecover(Long lastAvailableBlock) throws LedgerException;
    void commitLostBlock(Common.Block block) throws LedgerException;
    void commit() throws LedgerException;
    void rollback() throws LedgerException;
    void shutdown() throws LedgerException;
}
