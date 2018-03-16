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
package org.bcia.javachain.core.ledger.ledgermgmt;

import org.bcia.javachain.core.ledger.INodeLedger;

/**
 * closableLedger extends from actual validated ledger and overwrites the Close method
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public class ClosableLedger {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public INodeLedger getNodeLedger() {
        return nodeLedger;
    }

    public void setNodeLedger(INodeLedger nodeLedger) {
        this.nodeLedger = nodeLedger;
    }

    private INodeLedger nodeLedger;

    /** Close closes the actual ledger and removes the entries from opened ledgers map
     *
     */
    public void close() {

    }

    public void closeWithoutLock() {

    }

}
