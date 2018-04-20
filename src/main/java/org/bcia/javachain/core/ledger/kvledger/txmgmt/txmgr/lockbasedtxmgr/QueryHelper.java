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
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.RWSetBuilder;

import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class QueryHelper {
    private LockBasedTxMgr txMgr;
    private RWSetBuilder rwSetBuilder;
    private List<ResultsItr> itrs;
    private boolean doneInvoked;

    public byte[] getState(String ns, String key) throws LedgerException{
        checkDone();
        return null;
    }

    private void checkDone() throws LedgerException{
        if(!doneInvoked){
            throw new LedgerException("This instance should not be used after calling Done()");
        }
    }

    public LockBasedTxMgr getTxMgr() {
        return txMgr;
    }

    public void setTxMgr(LockBasedTxMgr txMgr) {
        this.txMgr = txMgr;
    }

    public RWSetBuilder getRwSetBuilder() {
        return rwSetBuilder;
    }

    public void setRwSetBuilder(RWSetBuilder rwSetBuilder) {
        this.rwSetBuilder = rwSetBuilder;
    }

    public List<ResultsItr> getItrs() {
        return itrs;
    }

    public void setItrs(List<ResultsItr> itrs) {
        this.itrs = itrs;
    }

    public boolean isDoneInvoked() {
        return doneInvoked;
    }

    public void setDoneInvoked(boolean doneInvoked) {
        this.doneInvoked = doneInvoked;
    }
}
