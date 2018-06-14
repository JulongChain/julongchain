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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 范围查询辅助类
 *
 * @author sunzongyu
 * @date 2018/04/18
 * @company Dingxuan
 */
public class RangeQueryResultsHelper {
    private static final JavaChainLog logger  = JavaChainLogFactory.getLog(RangeQueryResultsHelper.class);

    private List<KvRwset.KVRead> pendingResults = new ArrayList<>();
    private MerkleTree mt;
    private int maxDegree;
    private boolean hashingEnable;

    public RangeQueryResultsHelper(boolean hashingEnable, int maxDegree) throws LedgerException {
        this.maxDegree = maxDegree;
        this.hashingEnable = hashingEnable;
        if(hashingEnable){
            this.mt = new MerkleTree(maxDegree);
        }
    }

    public void addResult(KvRwset.KVRead kvRead) throws LedgerException{
        logger.debug("Adding a result");
        pendingResults.add(kvRead);
        if(hashingEnable && pendingResults.size() > maxDegree){
            logger.debug("Processing the accumulated results");
            processPendingResults();
        }
    }

    public KvRwset.QueryReadsMerkleSummary getMerkleSummary(){
        if(!hashingEnable){
            return null;
        }
        return mt.getSummery();
    }

    public void processPendingResults() throws LedgerException{
        byte[] b = serializeKVReads(pendingResults);
        pendingResults.clear();
        mt.update(Util.getHashBytes(b));
    }

    private byte[] serializeKVReads(List<KvRwset.KVRead> list){
       return setKvReads(KvRwset.QueryReads.newBuilder(), list).build().toByteArray();
    }

    private KvRwset.QueryReads.Builder setKvReads(KvRwset.QueryReads.Builder builder, List<KvRwset.KVRead> list){
        for (int i = 0; i < list.size(); i++) {
            builder.setKvReads(i, list.get(i));
        }
        return builder;
    }

    public Map.Entry<List<KvRwset.KVRead>, KvRwset.QueryReadsMerkleSummary> done() throws LedgerException{
    	if(!hashingEnable || mt.isEmpty()){
    		return new AbstractMap.SimpleEntry<>(pendingResults, null);
	    }
	    if (0 != pendingResults.size()) {
		    logger.debug("Processing the pending results");
		    try {
			    processPendingResults();
		    } catch (LedgerException e) {
			    return new AbstractMap.SimpleEntry<>(pendingResults, null);
		    }
	    }
	    mt.done();
        return new AbstractMap.SimpleEntry<>(pendingResults, mt.getSummery());
    }

    public List<KvRwset.KVRead> getPendingResults() {
        return pendingResults;
    }

    public void setPendingResults(List<KvRwset.KVRead> pendingResults) {
        this.pendingResults = pendingResults;
    }

    public MerkleTree getMt() {
        return mt;
    }

    public void setMt(MerkleTree mt) {
        this.mt = mt;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public void setMaxDegree(int maxDegree) {
        this.maxDegree = maxDegree;
    }

    public boolean isHashingEnable() {
        return hashingEnable;
    }

    public void setHashingEnable(boolean hashingEnable) {
        this.hashingEnable = hashingEnable;
    }
}
