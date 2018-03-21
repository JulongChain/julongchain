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
package org.bcia.javachain.core.common.privdata;

import org.bcia.javachain.core.ledger.IQueryExecutor;
import org.bcia.javachain.msp.IIdentityDeserializer;
import org.bcia.javachain.protos.common.Collection;

/**
 * 类描述
 *  IPrivDataSupport is an interface used to inject dependencies
 * @author sunianle
 * @date 3/15/18
 * @company Dingxuan
 */
public interface IPrivDataSupport {
    //getQueryExecotorForLedger returns a query executor for the specified group
    IQueryExecutor getQueryExecotorForLedger(String groupID);
    // GetCollectionKVSKey returns the name of the collection
    // given the collection criteria
    String getCollectionKVSKey(Collection.CollectionCriteria cc);
    // GetIdentityDeserializer returns an IdentityDeserializer
    // instance for the specified chain
    IIdentityDeserializer getIdentityDeserializer(String groupID);
}
