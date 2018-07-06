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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valinternal;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.HashedUpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.PubUpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.CollHashedRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.NsRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.TxRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

/**
 * 共有hash数据更新包
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class PubAndHashUpdates {
    private PubUpdateBatch pubUpdates;
    private HashedUpdateBatch hashedUpdates;

    public PubAndHashUpdates() {
        this.pubUpdates = new PubUpdateBatch();
        this.hashedUpdates = new HashedUpdateBatch();
    }

    public void applyWriteSet(TxRwSet txRwSet, LedgerHeight txHeight) throws LedgerException  {
        for(NsRwSet nsRwSet : txRwSet.getNsRwSets()){
            String ns = nsRwSet.getNameSpace();
            for(KvRwset.KVWrite kvWrite : nsRwSet.getKvRwSet().getWritesList()){
                if(kvWrite.getIsDelete()){
                    pubUpdates.getBatch().delete(ns, kvWrite.getKey(), txHeight);
                } else {
                    pubUpdates.getBatch().put(ns, kvWrite.getKey(), kvWrite.getValue().toByteArray(), txHeight);
                }
            }

            for(CollHashedRwSet collHashedRwSet : nsRwSet.getCollHashedRwSets()){
                String coll = collHashedRwSet.getCollectionName();
                for(KvRwset.KVWriteHash hashedWrite : collHashedRwSet.getHashedRwSet().getHashedWritesList()){
                    if(hashedWrite.getIsDelete()){
                        hashedUpdates.delete(ns, coll, hashedWrite.getKeyHash().toByteArray(), txHeight);
                    } else {
                        hashedUpdates.put(ns, coll, hashedWrite.getKeyHash().toByteArray(), hashedWrite.getValueHash().toByteArray(), txHeight);
                    }
                }
            }
        }
    }

    public PubUpdateBatch getPubUpdates() {
        return pubUpdates;
    }

    public void setPubUpdates(PubUpdateBatch pubUpdates) {
        this.pubUpdates = pubUpdates;
    }

    public HashedUpdateBatch getHashedUpdates() {
        return hashedUpdates;
    }

    public void setHashedUpdates(HashedUpdateBatch hashedUpdates) {
        this.hashedUpdates = hashedUpdates;
    }
}
