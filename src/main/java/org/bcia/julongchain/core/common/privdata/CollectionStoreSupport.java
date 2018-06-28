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
package org.bcia.julongchain.core.common.privdata;

import org.bcia.julongchain.core.ledger.IQueryExecutor;
import org.bcia.julongchain.msp.IIdentityDeserializer;
import org.bcia.julongchain.protos.common.Collection;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/15/18
 * @company Dingxuan
 */
public class CollectionStoreSupport implements IPrivDataSupport {
    public final static String COLLECTION_SEPARATOR = "~";
    public final static String COLLECTION_SUFFIX ="collection";

    @Override
    public IQueryExecutor getQueryExecotorForLedger(String groupID) {
        return null;
    }

    @Override
    public String getCollectionKVSKey(Collection.CollectionCriteria cc) {
        return null;
    }

    @Override
    public IIdentityDeserializer getIdentityDeserializer(String groupID) {
        return null;
    }

    @Override
    public String buildCollectionKVSKey(String smartContractname) {
        return smartContractname+ COLLECTION_SEPARATOR + COLLECTION_SUFFIX;
    }

    @Override
    public boolean isCollectionConfigKey(String key) {
        return key.contains(COLLECTION_SEPARATOR);
    }
}
