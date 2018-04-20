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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.statebasedval;


import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.DB;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.HashedCompositeKey;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.CollHashedRwSet;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.NsRwSet;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.CompositeKey;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valinternal.Block;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valinternal.InternalValidator;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valinternal.PubAndHashUpdates;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valinternal.Transaction;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class Validator implements InternalValidator {
    private DB db;

    public static Validator newValidator(DB db){
        Validator validator = new Validator();
        validator.setDb(db);
        return validator;
    }

    public void preLoadCommittedVersionOfRSet(Block block){
        List<CompositeKey> pubKeys = new ArrayList<>();
        List<HashedCompositeKey> hashedKeys = new ArrayList<>();
        Map<CompositeKey, Object> pubKeysMap = new HashMap<>();
        Map<HashedCompositeKey, Object> hashedKeyMap = new HashMap<>();
        for(Transaction tx : block.getTxs()){
            for(NsRwSet nsRwSet : tx.getRwSet().getNsRwSets()){
                for(KvRwset.KVRead kvRead : nsRwSet.getKvRwSet().getReadsList()){
                    CompositeKey compositeKey = new CompositeKey();
                    compositeKey.setNamespace(nsRwSet.getNameSpace());
                    compositeKey.setKey(kvRead.getKey());
                    if(!pubKeysMap.containsKey(compositeKey)){
                        pubKeysMap.put(compositeKey, null);
                        pubKeys.add(compositeKey);
                    }
                }
                for(CollHashedRwSet col : nsRwSet.getCollHashedRwSets()){
                    for(KvRwset.KVWriteHash kvHashedRead : col.getHashedRwSet().getHashedWritesList()){
                        HashedCompositeKey hashedCompositeKey = new HashedCompositeKey();
                        hashedCompositeKey.setNamespace(nsRwSet.getNameSpace());
                        hashedCompositeKey.setCollectionName(col.getCollectionName());
                        hashedCompositeKey.setKeyHash(new String(kvHashedRead.getKeyHash().toByteArray()));
                        if(!hashedKeyMap.containsKey(hashedCompositeKey)){
                            hashedKeyMap.put(hashedCompositeKey, null);
                            hashedKeys.add(hashedCompositeKey);
                        }
                    }
                }
            }
        }

        if(pubKeys.size() > 0 || hashedKeys.size() > 0){
            db.loadCommittedVersionsOfPubAndHashedKeys(pubKeys, hashedKeys);
        }
    }

    @Override
    public PubAndHashUpdates validateAndPrepareBatch(Block block, boolean doMVCCValidation) {
        preLoadCommittedVersionOfRSet(block);
//        PubAndHashUpdates updates =
        return null;
    }

    public DB getDb() {
        return db;
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
