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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.CompositeKey;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.stateleveldb.VersionedValue;

/**
 * 封装版本信息
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class VersionedKV {

    private CompositeKey compositeKey;
    private VersionedValue versionedValue;

    public VersionedKV(CompositeKey compositeKey, VersionedValue versionedValue) {
        this.compositeKey = compositeKey;
        this.versionedValue = versionedValue;
    }

    public CompositeKey getCompositeKey() {
        return compositeKey;
    }

    public void setCompositeKey(CompositeKey compositeKey) {
        this.compositeKey = compositeKey;
    }

    public VersionedValue getVersionedValue() {
        return versionedValue;
    }

    public void setVersionedValue(VersionedValue versionedValue) {
        this.versionedValue = versionedValue;
    }
}
