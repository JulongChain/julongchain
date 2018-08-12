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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.CompositeKey;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;

import java.util.List;

/**
 * 提供额外批量操作数据库接口
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public interface IBulkOptimizable {
    void loadCommittedVersions(List<CompositeKey> keys);

    LedgerHeight getCachedVersion(String ns, String key);

    void clearCachedVersions();
}
