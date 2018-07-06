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
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.*;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.CompositeKey;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;

import java.util.List;

/**
 * 管理pvtdata方法
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public interface IDB extends IVersionedDB {
    boolean isBulkOptimizable();

    void loadCommittedVersionsOfPubAndHashedKeys(List<CompositeKey> pubKeys, List<HashedCompositeKey> hashKeys) throws LedgerException;

    LedgerHeight getCacheKeyHashVersion(String ns, String coll, byte[] keyHash) throws LedgerException;

    void clearCachedVersions();

    ISmartContractLifecycleEventListener getSmartcontractEventListener();

    VersionedValue getPrivateData(String ns, String coll, String key) throws LedgerException;

    VersionedValue getValueHash(String ns, String coll, byte[] keyHash) throws LedgerException;

    LedgerHeight getKeyHashVersion(String ns, String coll, byte[] keyHash) throws LedgerException;

    List<VersionedValue> getPrivateDataMultipleKeys(String ns, String coll, List<String> keys) throws LedgerException;

    IResultsIterator getPrivateDataRangeScanIterator(String ns, String coll, String startKey, String endKey) throws LedgerException;

    IResultsIterator executeQueryOnPrivateData(String ns, String coll, String query) throws LedgerException;

    void applyPrivacyAwareUpdates(UpdateBatch updates, LedgerHeight height) throws LedgerException;
}
