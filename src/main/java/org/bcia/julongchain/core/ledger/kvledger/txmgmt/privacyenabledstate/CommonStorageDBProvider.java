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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.IVersionedDBProvider;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedLevelDBProvider;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;

/**
 * CommonStorageDB操作类
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class CommonStorageDBProvider implements IDBPorvider {
    private IVersionedDBProvider vdbProvider;

    public static CommonStorageDBProvider NewCommonStorageDBProvider() throws LedgerException {
        IVersionedDBProvider vdbProvider = null;
        if(LedgerConfig.isCouchDBEnable()){
            //CouchDB is not enable
        } else {
            vdbProvider = new VersionedLevelDBProvider();
        }
        CommonStorageDBProvider provider = new CommonStorageDBProvider();
        provider.setVdbProvider(vdbProvider);
        return provider;
    }

    @Override
    public IDB getDBHandle(String id) throws LedgerException {
        IVersionedDB vdb = vdbProvider.getDBHandle(id);
        return new CommonStorageDB(vdb);
    }

    @Override
    public void close() throws LedgerException {
        vdbProvider.close();
    }

    public IVersionedDBProvider getVdbProvider() {
        return vdbProvider;
    }

    public void setVdbProvider(IVersionedDBProvider vdbProvider) {
        this.vdbProvider = vdbProvider;
    }
}
