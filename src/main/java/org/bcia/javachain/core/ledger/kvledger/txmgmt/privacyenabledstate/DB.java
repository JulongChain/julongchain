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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.*;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.core.ledger.sceventmgmt.ISmartContractLifecycleEventListener;

import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public interface DB extends IVersionedDB {
    boolean IsBulkOptimizable();

    void LoadCommittedVersionsOfPubAndHashedKeys(List<CompositeKey> pubKeys, List<HashedCompositeKey> hashKeys);

    Height getCacheKeyHashVersion(String ns, String coll, byte[] keyHash) throws LedgerException;

    void clearCachedVersions();

    ISmartContractLifecycleEventListener getSmartcontractEventListener();

    VersionedValue getPrivateData(String ns, String coll) throws LedgerException;

    VersionedValue getValueHash(String ns, String coll, byte[] keyHash) throws LedgerException;

    Height getKeyHashVersion(String ns, String coll, byte[] keyHash) throws LedgerException;

    List<VersionedValue> getPrivateDataMultipleKeys(String ns, String coll, String[] keys) throws LedgerException;

    ResultsIterator getPrivateDataRangeScanIterator(String ns, String coll, String startKey, String endKey) throws LedgerException;

    ResultsIterator ExecuteQueryOnPrivateData(String ns, String coll, String query) throws LedgerException;

    void applyPrivacyAwareUpdates(UpdateBatch updates, Height height);
}
