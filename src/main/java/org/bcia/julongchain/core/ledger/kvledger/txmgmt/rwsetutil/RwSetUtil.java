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
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.List;


/**
 * 读写集转换工具类
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class RwSetUtil {
    /**
     * 在TxReadWriteSet中获取TxRwSet
     */
    public static TxRwSet txRwSetFromProtoMsg(Rwset.TxReadWriteSet protoMsg) throws LedgerException{
        TxRwSet txRwSet = new TxRwSet();
        NsRwSet nsRwSet = null;
        for(Rwset.NsReadWriteSet nsRwSetProtoMsg : protoMsg.getNsRwsetList()){
            nsRwSet = nsRwSetFromProtoMsg(nsRwSetProtoMsg);
            txRwSet.getNsRwSets().add(nsRwSet);
        }
        return txRwSet;
    }

    /**
     * 在NsReadWriteSet中获取NsRwSet
     */
    public static NsRwSet nsRwSetFromProtoMsg(Rwset.NsReadWriteSet protoMsg) throws LedgerException{
        NsRwSet nsRwSet = null;
        try {
            nsRwSet = new NsRwSet(protoMsg.getNamespace(), KvRwset.KVRWSet.parseFrom(protoMsg.getRwset()));
        } catch (InvalidProtocolBufferException e) {
            throw new LedgerException(e);
        }

        CollHashedRwSet collHashedRwSet = null;
        for(Rwset.CollectionHashedReadWriteSet collHashedRwSetProtoMsg : protoMsg.getCollectionHashedRwsetList()){
            collHashedRwSet = collHashedRwSetFromProtoMsg(collHashedRwSetProtoMsg);
            nsRwSet.getCollHashedRwSets().add(collHashedRwSet);
        }
        return nsRwSet;
    }

    /**
     * 在TxPvtReadWriteSet中获取TxPvtRwSet
     */
    public static TxPvtRwSet txPvtRwSetFromProtoMsg(Rwset.TxPvtReadWriteSet protoMsg) throws LedgerException{
        TxPvtRwSet txPvtRwSet = new TxPvtRwSet();
        NsPvtRwSet nsPvtRwSet = null;
        for(Rwset.NsPvtReadWriteSet nsPvtRwSetProtoMsg : protoMsg.getNsPvtRwsetList()){
            nsPvtRwSet = nsPvtRwSetFromProtoMsg(nsPvtRwSetProtoMsg);
            txPvtRwSet.getNsPvtRwSets().add(nsPvtRwSet);
        }
        return txPvtRwSet;
    }

    /**
     * 在NsPvtReadWriteSet中获取NsPvtRwSet
     */
    public static NsPvtRwSet nsPvtRwSetFromProtoMsg(Rwset.NsPvtReadWriteSet protoMsg) throws LedgerException{
        NsPvtRwSet nsPvtRwSet = new NsPvtRwSet(protoMsg.getNamespace());
        for(Rwset.CollectionPvtReadWriteSet collPvtRwSetProtoMsg : protoMsg.getCollectionPvtRwsetList()){
            CollPvtRwSet collPvtRwSet = collPvtRwSetFromProtoMsg(collPvtRwSetProtoMsg);
            nsPvtRwSet.getCollPvtRwSets().add(collPvtRwSet);
        }
        return nsPvtRwSet;
    }

    /**
     * 在CollectionPvtReadWriteSet中获取CollPvtRwSet
     */
    public static CollPvtRwSet collPvtRwSetFromProtoMsg(Rwset.CollectionPvtReadWriteSet protoMsg) throws LedgerException{
        try {
            return new CollPvtRwSet(protoMsg.getCollectionName(), KvRwset.KVRWSet.parseFrom(protoMsg.getRwset()));
        } catch (InvalidProtocolBufferException e) {
            throw new LedgerException("Got error when getting KVRWSet from protoMsg: " + e);
        }
    }

    /**
     * 在CollectionHashedReadWriteSet中获取CollHashedRwSet
     */
    public static CollHashedRwSet collHashedRwSetFromProtoMsg(Rwset.CollectionHashedReadWriteSet protoMsg) throws LedgerException{
        try {
            return new CollHashedRwSet(protoMsg.getCollectionName(),
                    protoMsg.getPvtRwsetHash(),
                    KvRwset.HashedRWSet.parseFrom(protoMsg.getHashedRwset()));
        } catch (InvalidProtocolBufferException e) {
            throw new LedgerException("Got error when getting HashedRwSet from protoMsg: " + e);
        }
    }

    /**
     * 构建KVRead
     */
    public static KvRwset.KVRead newKVRead(String key, Height version){
        return KvRwset.KVRead.newBuilder()
                .setKey(key)
                .setVersion(newProtoVersion(version))
                .build();
    }

    /**
     * 通过version.Height构建proto中Version
     */
    public static KvRwset.Version newProtoVersion(Height height){
        if(height == null){
            return null;
        }
        return KvRwset.Version.newBuilder()
                .setBlockNum(height.getBlockNum())
                .setTxNum(height.getTxNum())
                .build();
    }

    /**
     * 通过proto中Version构建version.Height
     */
    public static Height newVersion(KvRwset.Version protoVersion){
        if(protoVersion == null){
            return null;
        }
        return new Height(protoVersion.getBlockNum(), protoVersion.getTxNum());
    }

    /**
     * 构建KVWrite
     */
    public static KvRwset.KVWrite newKVWrite(String key, ByteString value){
        return KvRwset.KVWrite.newBuilder()
                .setKey(key)
                .setIsDelete(value.size() == 0)
                .setValue(value)
                .build();
    }

    /**
     * 构建KVReadHash
     */
    public static KvRwset.KVReadHash newPvtKVReadHash(String key, Height version) throws LedgerException {
        return KvRwset.KVReadHash.newBuilder()
                // TODO: 5/31/18 SM3 Hash
                .setKeyHash(ByteString.copyFrom(Util.getHashBytes(key.getBytes())))
                .setVersion(newProtoVersion(version))
                .build();
    }

    /**
     * 构建KVWriteHash
     */
    public static KvRwset.KVWriteHash newPvtKVWriteHash(String key, ByteString value) throws LedgerException{
        KvRwset.KVWriteHash.Builder builder = KvRwset.KVWriteHash.newBuilder();
        // TODO: 5/31/18 SM3 Hash
        ByteString keyHash = ByteString.copyFrom(Util.getHashBytes(key.getBytes()));
        ByteString valueHash = ByteString.EMPTY;
        if (value.size() != 0) {
            valueHash = ByteString.copyFrom(Util.getHashBytes(value.toByteArray()));
        }
        if(value.size() != 0){
            valueHash = ByteString.copyFrom(Util.getHashBytes(value.toByteArray()));
        }
        return builder
                .setKeyHash(keyHash)
                .setValueHash(valueHash)
                .setIsDelete(value.size() == 0)
                .build();
    }

    /**
     * 构建KVRWSet对象
     */
    public static KvRwset.KVRWSet newKVRWSet(List<KvRwset.KVRead> reads, List<KvRwset.KVWrite> writes, List<KvRwset.RangeQueryInfo> rangeQueriesInfo){
        KvRwset.KVRWSet.Builder builder = KvRwset.KVRWSet.newBuilder();

        if (reads != null) {
            for(KvRwset.KVRead read : reads){
                builder.addReads(read);
            }
        }

        if (writes != null) {
            for(KvRwset.KVWrite write : writes){
                builder.addWrites(write);
            }
        }

        if (rangeQueriesInfo != null) {
            for(KvRwset.RangeQueryInfo info : rangeQueriesInfo){
                builder.addRangeQueriesInfo(info);
            }
        }

        return builder.build();
    }

    public static KvRwset.HashedRWSet newHashedRWSet(List<KvRwset.KVReadHash> readSet, List<KvRwset.KVWriteHash> writeSet){
        KvRwset.HashedRWSet.Builder builder = KvRwset.HashedRWSet.newBuilder();

        if (readSet != null) {
            for(KvRwset.KVReadHash read : readSet){
                builder.addHashedReads(read);
            }
        }

        if (writeSet != null) {
            for(KvRwset.KVWriteHash write : writeSet){
                builder.addHashedWrites(write);
            }
        }

        return builder.build();
    }

}

