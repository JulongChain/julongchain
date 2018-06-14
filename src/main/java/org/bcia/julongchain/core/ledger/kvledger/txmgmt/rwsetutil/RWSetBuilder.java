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
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.TxSimulationResults;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读写集合
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class RWSetBuilder {
    private Map<String, NsPubRwBuilder> pubRwBuilderMap = new HashMap<>();
    private Map<String, NsPvtRwBuilder> pvtRwBuilderMap = new HashMap<>();

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
        RangeQueryKey key = new RangeQueryKey(rqi.getStartKey(), rqi.getEndKey(), rqi.getItrExhausted());
        if(nsPubRwBuilder.getRangeQueriesMap().get(key) == null){
            nsPubRwBuilder.getRangeQueriesMap().put(key, rqi);
            nsPubRwBuilder.getRangeQueryKeys().add(key);
        }
    }

    /**
     * 向Hash读集合添加
     */
    public void addToHashedReadSet(String ns, String coll, String key, Height version) throws LedgerException{
        KvRwset.KVReadHash kvReadHash = RwSetUtil.newPvtKVReadHash(key, version);
        CollHashRwBuilder chrwb = getOrCreateCollHashedRwBuilder(ns, coll);
        chrwb.getReadMap().put(key, kvReadHash);
    }

    /**
     * 向私有且Hash写集合添加
     */
    public void addToPvtAndHashedWriteSet(String ns, String coll, String key, ByteString value) throws LedgerException{
        KvRwset.KVWrite kvWrite = RwSetUtil.newKVWrite(key, value);
        KvRwset.KVWriteHash kvWriteHash = RwSetUtil.newPvtKVWriteHash(key, value);
        getOrCreateCollPvtRwBuilder(ns, coll).getWriteMap().put(key, kvWrite);
        getOrCreateCollHashedRwBuilder(ns, coll).getWriteMap().put(key, kvWriteHash);
    }

    /**
     * 获取共有数据(public data + hashed private data)
     * 获取私有rwset
     */
    public TxSimulationResults getTxSimulationResults() throws LedgerException{
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
        return new TxSimulationResults(pubDataProto, pvtDataProto, 0);
    }

    private void setPvtCollectionHash(String ns, String coll, ByteString pvtDataProto) throws LedgerException {
        CollHashRwBuilder collHashBuilder = getOrCreateCollHashedRwBuilder(ns, coll);
        //TODO SM3 Hash
        collHashBuilder.setPvtDataHash(Util.getHashBytes(pvtDataProto.toByteArray()));
    }

    /**
     * 返回RWSet
     */
    private TxRwSet getTxReadWriteSet(){
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
    private TxPvtRwSet getTxPvtReadWriteSet(){
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
    private NsPubRwBuilder getOrCreateNsPubRwBuilder(String ns){
        NsPubRwBuilder nsPubRwBuilder = pubRwBuilderMap.get(ns);
        if(nsPubRwBuilder == null){
            nsPubRwBuilder = new NsPubRwBuilder(ns);
            pubRwBuilderMap.put(ns, nsPubRwBuilder);
        }
        return nsPubRwBuilder;
    }

    /**
     * 根据ns获取nsPvtRwBuilder
     */
    private NsPvtRwBuilder getOrCreateNsPvtRwBuilder(String ns){
        NsPvtRwBuilder nsPvtRwBuilder = pvtRwBuilderMap.get(ns);
        if(nsPvtRwBuilder == null){
            nsPvtRwBuilder = new NsPvtRwBuilder(ns);
            pvtRwBuilderMap.put(ns, nsPvtRwBuilder);
        }
        return nsPvtRwBuilder;
    }

    private CollHashRwBuilder getOrCreateCollHashedRwBuilder(String ns, String coll){
        NsPubRwBuilder nsPubRwBuilder = getOrCreateNsPubRwBuilder(ns);
        CollHashRwBuilder collHashRwBuilder = nsPubRwBuilder.getCollHashRwBuilders().get(coll);
        if(collHashRwBuilder == null){
            collHashRwBuilder = new CollHashRwBuilder(coll, null);
            nsPubRwBuilder.getCollHashRwBuilders().put(coll, collHashRwBuilder);
        }
        return collHashRwBuilder;
    }

    private CollPvtRwBuilder getOrCreateCollPvtRwBuilder(String ns, String coll){
        NsPvtRwBuilder nsPvtRwBuilder = getOrCreateNsPvtRwBuilder(ns);
        CollPvtRwBuilder collHashRwBuilder = nsPvtRwBuilder.getCollPvtRwBuilders().get(coll);
        if(collHashRwBuilder == null){
            collHashRwBuilder = new CollPvtRwBuilder(coll);
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
