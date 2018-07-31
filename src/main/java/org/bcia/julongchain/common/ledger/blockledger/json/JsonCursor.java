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
package org.bcia.julongchain.common.ledger.blockledger.json;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.protos.common.Common;

import java.io.File;
import java.util.AbstractMap;

/**
 * json账本迭代器
 *
 * @author sunzongyu
 * @date 2018/04/28
 * @company Dingxuan
 */
public class JsonCursor implements IIterator {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(JsonCursor.class);

    private JsonLedger jl;
    private long blockNum;

    public JsonCursor(){}

    public JsonCursor(JsonLedger jl, long blockNum){
        this.blockNum = blockNum;
        this.jl = jl;
    }

    @Override
    public QueryResult next() throws LedgerException {
        while (true) {
            AbstractMap.SimpleImmutableEntry<Common.Block, Boolean> entry = jl.readBlock(blockNum);
            Common.Block block = entry.getKey();
            boolean found = entry.getValue();
            if(found){
                if(block == null){
                    return new QueryResult(new AbstractMap.SimpleImmutableEntry<QueryResult, Common.Status>(null, Common.Status.SERVICE_UNAVAILABLE));
                }
                blockNum++;
                return new QueryResult(new AbstractMap.SimpleImmutableEntry( new QueryResult(block), Common.Status.SUCCESS));
            }
            synchronized (JsonLedger.getLock()) {
                logger.debug("Waiting for block append");
                try {
                    JsonLedger.getLock().wait();
                } catch (InterruptedException e) {
                    throw new LedgerException(e);
                }
            }
        }
    }

    @Override
    public void readyChain() throws LedgerException {
        synchronized (JsonLedger.getLock()) {
            if(!new File(jl.blockFileName(blockNum)).exists()){
                try {
                    JsonLedger.getLock().wait();
                } catch (InterruptedException e) {
                    throw new LedgerException(e);
                }
            }

        }
    }

    @Override
    public void close() throws LedgerException {
        //nothing to do
    }

    public JsonLedger getJl() {
        return jl;
    }

    public void setJl(JsonLedger jl) {
        this.jl = jl;
    }

    public long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(long blockNum) {
        this.blockNum = blockNum;
    }

}
