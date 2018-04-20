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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valinternal;

import com.google.protobuf.ByteString;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.CollHashedRwSet;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.NsRwSet;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.TxRwSet;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/19
 * @company Dingxuan
 */
public class Transaction {
    private int indexInBlock;
    private String id;
    private TxRwSet rwSet;
    private TransactionPackage.TxValidationCode validationCode;

    public boolean containsPvtWrites(){
        for(NsRwSet ns : rwSet.getNsRwSets()){
            for(CollHashedRwSet coll : ns.getCollHashedRwSets()){
                if(coll.getPvtRwSetHash() != null){
                    return true;
                }
            }
        }
        return false;
    }

    public ByteString retrieveHash(String ns, String coll){
        if (null == rwSet) {
            return null;
        }
        for(NsRwSet nsData : rwSet.getNsRwSets()){
            for(CollHashedRwSet collData : nsData.getCollHashedRwSets()){
                if(collData.getCollectionName() == coll){
                    return collData.getPvtRwSetHash();
                }
            }
        }
        return null;
    }

    public int getIndexInBlock() {
        return indexInBlock;
    }

    public void setIndexInBlock(int indexInBlock) {
        this.indexInBlock = indexInBlock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TxRwSet getRwSet() {
        return rwSet;
    }

    public void setRwSet(TxRwSet rwSet) {
        this.rwSet = rwSet;
    }

    public TransactionPackage.TxValidationCode getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(TransactionPackage.TxValidationCode validationCode) {
        this.validationCode = validationCode;
    }
}
