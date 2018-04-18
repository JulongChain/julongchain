/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.lockbasedtxmgr;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.StateListener;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.TxMgr;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.common.Common;

import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class LockBasedTxMgr implements TxMgr {

    public static LockBasedTxMgr newLockBasedTxMgr(String ledgerID,
                                                   DB db,
                                                   Map<String, StateListener> stateListeners){
        return null;
    }

    @Override
    public IQueryExecutor newQueryExecutor() throws LedgerException {
        return null;
    }

    @Override
    public ITxSimulator newTxSimulator() throws LedgerException {
        return null;
    }

    @Override
    public void validateAndPrepare(Common.Block block, Boolean doMVCCValidation) throws LedgerException {

    }

    @Override
    public Height heightGetLastSavepoint() throws LedgerException {
        return null;
    }

    @Override
    public Boolean shouldRecover(Long lastAvailableBlock) throws LedgerException {
        return null;
    }

    @Override
    public void commitLostBlock(Common.Block block) throws LedgerException {

    }

    @Override
    public void commit() throws LedgerException {

    }

    @Override
    public void rollback() throws LedgerException {

    }

    @Override
    public void shutdown() throws LedgerException {

    }
}
