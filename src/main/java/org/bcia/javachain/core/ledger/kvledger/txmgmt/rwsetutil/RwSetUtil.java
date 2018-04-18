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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.common.Collection;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

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
    public static TxRwSet txRwSetFromProtoMsg(Rwset.TxReadWriteSet protoMsg){
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
    public static NsRwSet nsRwSetFromProtoMsg(Rwset.NsReadWriteSet protoMsg){
        NsRwSet nsRwSet = new NsRwSet();
        nsRwSet.setNameSpace(protoMsg.getNamespace());
        nsRwSet.setKvRwSet(KvRwset.KVRWSet.newBuilder().build());

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
    public static TxPvtRwSet txPvtRwSetFromProtoMsg(Rwset.TxPvtReadWriteSet protoMsg){
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
    public static NsPvtRwSet nsPvtRwSetFromProtoMsg(Rwset.NsPvtReadWriteSet protoMsg){
        NsPvtRwSet nsPvtRwSet = new NsPvtRwSet();
        nsPvtRwSet.setNameSpace(protoMsg.getNamespace());
        for(Rwset.CollectionPvtReadWriteSet collPvtRwSetProtoMsg : protoMsg.getCollectionPvtRwsetList()){
            CollPvtRwSet collPvtRwSet = collPvtRwSetFromProtoMsg(collPvtRwSetProtoMsg);
            nsPvtRwSet.getCollPvtRwSets().add(collPvtRwSet);
        }
        return nsPvtRwSet;
    }

    /**
     * 在CollectionPvtReadWriteSet中获取CollPvtRwSet
     */
    public static CollPvtRwSet collPvtRwSetFromProtoMsg(Rwset.CollectionPvtReadWriteSet protoMsg){
        CollPvtRwSet collPvtRwSet = new CollPvtRwSet();
        collPvtRwSet.setCollectionName(protoMsg.getCollectionName());
        try {
            collPvtRwSet.setKvRwSet(KvRwset.KVRWSet.parseFrom(protoMsg.getRwset()));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Got error when getting KVRWSet from protoMsg: " + e);
        }
        return collPvtRwSet;
    }

    /**
     * 在CollectionHashedReadWriteSet中获取CollHashedRwSet
     */
    public static CollHashedRwSet collHashedRwSetFromProtoMsg(Rwset.CollectionHashedReadWriteSet protoMsg){
        CollHashedRwSet collHashedRwSet = new CollHashedRwSet();
        collHashedRwSet.setCollectionName(protoMsg.getCollectionName());
        collHashedRwSet.setPvtRwSetHash(protoMsg.getPvtRwsetHash());
        try {
            collHashedRwSet.setHashedRwSet(KvRwset.HashedRWSet.parseFrom(protoMsg.getHashedRwset()));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Got error when getting HashedRwSet from protoMsg: " + e);
        }
        return collHashedRwSet;
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
        Height height = new Height();
        height.setBlockNum(protoVersion.getBlockNum());
        height.setTxNum(protoVersion.getTxNum());
        return height;
    }

    /**
     * 构建KVWrite
     */
    public static KvRwset.KVWrite newKVWrite(String key, ByteString value){
        return KvRwset.KVWrite.newBuilder()
                .setKey(key)
                .setIsDelete(value == null)
                .setValue(value)
                .build();
    }

    /**
     * 构建KVReadHash
     */
    public static KvRwset.KVReadHash newPvtKVReadHash(String key, Height version){
        return KvRwset.KVReadHash.newBuilder()
//                获取key的Hash
//                .setKeyHash()
                .setVersion(newProtoVersion(version))
                .build();
    }

    /**
     * 构建KVWriteHash
     */
    public static KvRwset.KVWriteHash newPvtKVWriteHash(String key, ByteString value){
        KvRwset.KVWrite kvWrite = newKVWrite(key, value);
        KvRwset.KVWriteHash.Builder builder = KvRwset.KVWriteHash.newBuilder();
//        获取key的Hash
        ByteString keyHash = null;
        ByteString valueHash = null;
        if(!kvWrite.getIsDelete()){
//        获取value的Hash
            builder.setValueHash(valueHash);
        }
        return builder
                .setKeyHash(keyHash)
                .setValueHash(valueHash)
                .setIsDelete(kvWrite.getIsDelete())
                .build();
    }
}

