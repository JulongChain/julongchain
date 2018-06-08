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
 * 操作TxPvtRwSet辅助类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class TxPvtRwSet {
    private List<NsPvtRwSet> nsPvtRwSets = new ArrayList<>();

    public List<NsPvtRwSet> getNsPvtRwSets() {
        return nsPvtRwSets;
    }

    public void setNsPvtRwSets(List<NsPvtRwSet> nsPvtRwSets) {
        this.nsPvtRwSets = nsPvtRwSets;
    }

    /**
     * 将TxPvtRwSet转换为proto中TxPvtReadWriteSet
     */
    public Rwset.TxPvtReadWriteSet toProtoMsg(){
        Rwset.TxPvtReadWriteSet.Builder builder = Rwset.TxPvtReadWriteSet.newBuilder()
                .setDataModel(Rwset.TxReadWriteSet.DataModel.KV);
        for(NsPvtRwSet nsPvtRwSet : nsPvtRwSets){
            builder.addNsPvtRwset(nsPvtRwSet.toProtoMsg());
        }
        return builder.build();
    }

    /**
     * 获取TxPvtReadWriteSet的ByteString
     */
    public ByteString toProtoBytes(){
        Rwset.TxPvtReadWriteSet protoMsg = null;
        protoMsg = toProtoMsg();
        return protoMsg.toByteString();
    }

    /**
     * 将ByteString转换为TxPvtRwSet
     */
    public void formProtoBytes(ByteString protoBytes) throws LedgerException{
        Rwset.TxPvtReadWriteSet protoMsg = null;
        TxPvtRwSet txPvtRwSetTemp = null;
        try {
            protoMsg = Rwset.TxPvtReadWriteSet.parseFrom(protoBytes);
        } catch (InvalidProtocolBufferException e) {
            throw new LedgerException("Got error when getting TxPvtReadWriteSet from protoBytes: " + e);
        }
        txPvtRwSetTemp = RwSetUtil.txPvtRwSetFromProtoMsg(protoMsg);
        nsPvtRwSets = txPvtRwSetTemp.getNsPvtRwSets();
    }
}
