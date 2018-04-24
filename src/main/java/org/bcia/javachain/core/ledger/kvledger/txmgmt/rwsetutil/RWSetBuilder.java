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
import org.bcia.javachain.common.ledger.util.Util;
import org.bcia.javachain.core.ledger.TxSimulationResults;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class RWSetBuilder {
    Map<String, NsPubRwBuilder> pubRwBuilderMap = new HashMap<>();
    Map<String, NsPvtRwBuilder> pvtRwBuilderMap = new HashMap<>();

    /**
     * 向读集合添加
     */
    public void addToReadSet(String ns, String key, Height version){
        NsPubRwBuilder nsPubRwBuilder = getOrCreateNsPubRwBuilder(ns);
        nsPubRwBuilder.getReadMap().put(key, RwSetUtil.newKVRead(key, version));
    }

    /**
     * 向写集合添加
     */
    public void addToWriteSet(String ns, String key, ByteString value){
        NsPubRwBuilder nsPubRwBuilder = getOrCreateNsPubRwBuilder(ns);
        nsPubRwBuilder.getWriteMap().put(key, RwSetUtil.newKVWrite(key, value));
    }

    /**
     * 向检索集合添加
     */
    public void addToRangeQuerySet(String ns, KvRwset.RangeQueryInfo rqi){
        NsPubRwBuilder nsPubRwBuilder = getOrCreateNsPubRwBuilder(ns);
        RangeQueryKey key = new RangeQueryKey();
        key.setStartKey(rqi.getStartKey());
        key.setEndKey(rqi.getEndKey());
        key.setItrExhausted(rqi.getItrExhausted());
        if(nsPubRwBuilder.getRangeQueriesMap().get(key) == null){
            nsPubRwBuilder.getRangeQueriesMap().put(key, rqi);
            nsPubRwBuilder.getRangeQueryKeys().add(key);
        }
    }

    /**
     * 向Hash读集合添加
     */
    public void addToHashedReadSet(String ns, String coll, String key, Height version){
        KvRwset.KVReadHash kvReadHash = RwSetUtil.newPvtKVReadHash(key, version);
        CollHashRwBuilder chrwb = getOrCreateCollHashedRwBuilder(ns, coll);
        chrwb.getReadMap().put(key, kvReadHash);
    }

    /**
     * 向私有且Hash写集合添加
     */
    public void addToPvtAndHashedWriteSet(String ns, String coll, String key, ByteString value){
        KvRwset.KVWrite kvWrite = RwSetUtil.newKVWrite(key, value);
        KvRwset.KVWriteHash kvWriteHash = RwSetUtil.newPvtKVWriteHash(key, value);
        CollPvtRwBuilder cprwb = getOrCreateCollPvtRwBuilder(ns, coll);
        CollHashRwBuilder chrwb = getOrCreateCollHashedRwBuilder(ns, coll);
        cprwb.getWriteMap().put(key, kvWrite);
    }

    /**
     * 获取共有数据(public data + hashed private data)
     * 获取私有rwset
     */
    public TxSimulationResults getTxSimulationResults(){
        TxPvtRwSet pvtData = getTxPvtReadWriteSet();
        Rwset.TxReadWriteSet pubDataProto = null;
        Rwset.TxPvtReadWriteSet pvtDataProto = null;
        if(pvtData != null){
            pvtDataProto = pvtData.toProtoMsg();
            for(Rwset.NsPvtReadWriteSet ns : pvtDataProto.getNsPvtRwsetList()){
                for(Rwset.CollectionPvtReadWriteSet coll : ns.getCollectionPvtRwsetList()){
                    setPvtCollectionHash(ns.getNamespace(), coll.getCollectionName(), coll.getRwset());
                }
            }
        }

        //计算rwset
        TxRwSet pubSet = getTxReadWriteSet();
        if(pubSet != null){
             pubDataProto = pubSet.toProtoMsg();
        }
        TxSimulationResults results = new TxSimulationResults();
        results.setPublicReadWriteSet(pubDataProto);
        results.setPrivateReadWriteSet(pvtDataProto);
        return results;
    }

    public void setPvtCollectionHash(String ns, String coll, ByteString pvtDataProto){
        CollHashRwBuilder collHashBuilder = getOrCreateCollHashedRwBuilder(ns, coll);
        //TODO 获取pvtDataProto Hash
        collHashBuilder.setPvtDataHash(null);
    }

    /**
     * 返回RWSet
     */
    public TxRwSet getTxReadWriteSet(){
        List<NsPubRwBuilder> sortedNsPubBuilders = Util.getValuesBySortedKeys(pubRwBuilderMap);
        List<NsRwSet> nsPubRwSets = new ArrayList<>();
        for(NsPubRwBuilder nsPubRwBuilder : sortedNsPubBuilders){
            nsPubRwSets.add(nsPubRwBuilder.build());
        }
        TxRwSet txRwSet = new TxRwSet();
        txRwSet.setNsRwSets(nsPubRwSets);
        return txRwSet;
    }

    /**
     * 返回私有RWSet
     */
    public TxPvtRwSet getTxPvtReadWriteSet(){
        List<NsPvtRwBuilder> sortedNsPvtBuliders = Util.getValuesBySortedKeys(pvtRwBuilderMap);
        List<NsPvtRwSet> nsPvtRwSets = new ArrayList<>();
        for(NsPvtRwBuilder nsPvtRwBuilder : sortedNsPvtBuliders){
            nsPvtRwSets.add(nsPvtRwBuilder.build());
        }
        if(nsPvtRwSets.size() == 0){
            return null;
        }
        TxPvtRwSet txPvtRwSet = new TxPvtRwSet();
        txPvtRwSet.setNsPvtRwSets(nsPvtRwSets);
        return txPvtRwSet;
    }

    /**
     * 根据ns获取nsPubRwBuilder
     */
    public NsPubRwBuilder getOrCreateNsPubRwBuilder(String ns){
        NsPubRwBuilder nsPubRwBuilder = pubRwBuilderMap.get(ns);
        if(nsPubRwBuilder == null){
            nsPubRwBuilder = new NsPubRwBuilder();
            nsPubRwBuilder.setNameSpace(ns);
            nsPubRwBuilder.setRangeQueryKeys(null);
            pubRwBuilderMap.put(ns, nsPubRwBuilder);
        }
        return nsPubRwBuilder;
    }

    /**
     * 根据ns获取nsPvtRwBuilder
     */
    public NsPvtRwBuilder getOrCreateNsPvtRwBuilder(String ns){
        NsPvtRwBuilder nsPvtRwBuilder = pvtRwBuilderMap.get(ns);
        if(nsPvtRwBuilder == null){
            nsPvtRwBuilder = new NsPvtRwBuilder();
            nsPvtRwBuilder.setCollectionName(ns);
            pvtRwBuilderMap.put(ns, nsPvtRwBuilder);
        }
        return nsPvtRwBuilder;
    }

    public CollHashRwBuilder getOrCreateCollHashedRwBuilder(String ns, String coll){
        NsPubRwBuilder nsPubRwBuilder = getOrCreateNsPubRwBuilder(ns);
        CollHashRwBuilder collHashRwBuilder = nsPubRwBuilder.getCollHashRwBuilders().get(coll);
        if(collHashRwBuilder == null){
            collHashRwBuilder = new CollHashRwBuilder();
            collHashRwBuilder.setCollName(coll);
            collHashRwBuilder.setPvtDataHash(null);
            nsPubRwBuilder.getCollHashRwBuilders().put(coll, collHashRwBuilder);
        }
        return collHashRwBuilder;
    }

    public CollPvtRwBuilder getOrCreateCollPvtRwBuilder(String ns, String coll){
        NsPvtRwBuilder nsPvtRwBuilder = getOrCreateNsPvtRwBuilder(ns);
        CollPvtRwBuilder collHashRwBuilder = nsPvtRwBuilder.getCollPvtRwBuilders().get(coll);
        if(collHashRwBuilder == null){
            collHashRwBuilder = new CollPvtRwBuilder();
            collHashRwBuilder.setCollectionName(coll);
            nsPvtRwBuilder.getCollPvtRwBuilders().put(coll, collHashRwBuilder);
        }
        return collHashRwBuilder;
    }

    public Map<String, NsPubRwBuilder> getPubRwBuilderMap() {
        return pubRwBuilderMap;
    }

    public void setPubRwBuilderMap(Map<String, NsPubRwBuilder> pubRwBuilderMap) {
        this.pubRwBuilderMap = pubRwBuilderMap;
    }

    public Map<String, NsPvtRwBuilder> getPvtRwBuilderMap() {
        return pvtRwBuilderMap;
    }

    public void setPvtRwBuilderMap(Map<String, NsPvtRwBuilder> pvtRwBuilderMap) {
        this.pvtRwBuilderMap = pvtRwBuilderMap;
    }
}
