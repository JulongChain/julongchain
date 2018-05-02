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

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.PrunePolicy;
import org.bcia.javachain.common.ledger.ResultsIterator;
import org.bcia.javachain.core.ledger.*;
import org.bcia.javachain.core.ledger.kvledger.KvLedger;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.List;

/**
 * closableLedger extends from actual validated ledger and overwrites the Close method
 *
 * @author wanliangbing
 * @date 2018/3/15
 * @company Dingxuan
 */
public class ClosableLedger extends KvLedger {

    private String id;

    private INodeLedger nodeLedger;

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

    public void closeWithoutLock() {
        nodeLedger.close();
    }
}
