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
package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.protos.ledger.rwset.Rwset;

/**
 * 私有交易数据
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class TxPvtData {

    private long seqInBlock;
    private Rwset.TxPvtReadWriteSet writeSet;

    public TxPvtData(long seqInBlock, Rwset.TxPvtReadWriteSet writeSet) {
        this.seqInBlock = seqInBlock;
        this.writeSet = writeSet;
    }

    public long getSeqInBlock() {
        return seqInBlock;
    }

    public void setSeqInBlock(long seqInBlock) {
        this.seqInBlock = seqInBlock;
    }

    public Rwset.TxPvtReadWriteSet getWriteSet() {
        return writeSet;
    }

    public void setWriteSet(Rwset.TxPvtReadWriteSet writeSet) {
        this.writeSet = writeSet;
    }

    /**
     * 判断writeSet是否含有ns、coll
     */
    public boolean has(String ns, String coll){
       if (writeSet == null){
           return false;
       }
       for(Rwset.NsPvtReadWriteSet nsData : writeSet.getNsPvtRwsetList()){
           if(ns != null && ns.equals(nsData.getNamespace())){
               for(Rwset.CollectionPvtReadWriteSet collData : nsData.getCollectionPvtRwsetList()){
                   if(coll != null && coll.equals(collData)){
                        return true;
                   }
               }
           }
       }
       return false;
    }
}
