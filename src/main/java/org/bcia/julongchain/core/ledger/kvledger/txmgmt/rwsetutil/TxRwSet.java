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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅助操作TxRwSet
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class TxRwSet {
   private List<NsRwSet> nsRwSets = new ArrayList<>();

   public List<NsRwSet> getNsRwSets() {
      return nsRwSets;
   }

   public void setNsRwSets(List<NsRwSet> nsRwSets) {
      this.nsRwSets = nsRwSets;
   }

    /**
     * 将TxRwSet转换为proto中TxReadWriteSet
     */
    public Rwset.TxReadWriteSet toProtoMsg(){
        Rwset.TxReadWriteSet.Builder builder = Rwset.TxReadWriteSet.newBuilder()
                .setDataModel(Rwset.TxReadWriteSet.DataModel.KV);
        Rwset.NsReadWriteSet nsRwSetProtoMsg = null;
        for(NsRwSet nsRwSet : nsRwSets){
            nsRwSetProtoMsg = nsRwSet.toProtoMsg();
            builder.addNsRwset(nsRwSetProtoMsg);
        }
        return builder.build();
    }

    /**
     * 获取TxReadWriteSet的ByteString
     */
    public ByteString toProtoBytes(){
        Rwset.TxReadWriteSet protoMsg = null;
        protoMsg = toProtoMsg();
        return protoMsg.toByteString();
    }

    /**
     * 将ByteString转换为TxRwSet
     */
    public void fromProtoBytes(ByteString protoBytes) throws LedgerException {
        Rwset.TxReadWriteSet protoMsg = null;
        TxRwSet txRwSetTemp = null;
        try {
            protoMsg = Rwset.TxReadWriteSet.parseFrom(protoBytes);
        } catch (InvalidProtocolBufferException e) {
            throw new LedgerException("Got error when getting TxReadWriteSet from protoBytes: " + e);
        }
        txRwSetTemp = RwSetUtil.txRwSetFromProtoMsg(protoMsg);
        nsRwSets = txRwSetTemp.getNsRwSets();
    }
}
