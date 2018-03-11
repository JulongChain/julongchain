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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb;

import org.bcia.javachain.common.exception.LedgerException;

/**
 * VersionedDBProvider provides an instance of an versioned DB
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public interface VersionedDBProvider {

    /** GetDBHandle returns a handle to a VersionedDB
     *
     * @param id
     * @return
     * @throws LedgerException
     */
    VersionedDB getDBHandle(String id) throws LedgerException;

    /** Close closes all the VersionedDB instances and releases any resources held by VersionedDBProvider
     *
     */
    void close();

}
