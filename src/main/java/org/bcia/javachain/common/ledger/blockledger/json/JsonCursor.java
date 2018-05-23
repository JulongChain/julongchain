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
package org.bcia.javachain.common.ledger.blockledger.json;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blockledger.Iterator;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.IQueryResult;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.protos.common.Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap;

/**
 * json账本迭代器
 *
 * @author sunzongyu
 * @date 2018/04/28
 * @company Dingxuan
 */
public class JsonCursor implements Iterator {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(JsonCursor.class);

    private JsonLedger jl;
    private long blockNum;
    private Channel<Object> channel;

    public JsonCursor(){}

    public JsonCursor(JsonLedger jl, long blockNum){
        this.blockNum = blockNum;
        this.jl = jl;
    }

    @Override
    public IQueryResult next() throws LedgerException {
        while (true) {
            try {
                Common.Block block = jl.readBlock(blockNum);
                if(block == null){
                    return (IQueryResult) new AbstractMap.SimpleImmutableEntry<IQueryResult, Common.Status>(null, Common.Status.SERVICE_UNAVAILABLE);
                }
                blockNum++;
                return (IQueryResult) new AbstractMap.SimpleImmutableEntry<IQueryResult, Common.Status>((IQueryResult) block.toByteString(), Common.Status.SUCCESS);
            } catch (FileNotFoundException e) {
                if (channel != null) {
                    channel.add(new Object());
                }
            }
            try {
                channel.take();
            } catch (InterruptedException e) {
                throw new LedgerException(e);
            }
        }
    }

    @Override
    public Channel<Object> readyChain() {
        if(!new File(jl.blockFileName(blockNum)).exists()){
            return this.channel;
        }
        Channel<Object> channel = new Channel<>();
        channel.close();
        return channel;
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

    public Channel<Object> getChannel() {
        return channel;
    }

    public void setChannel(Channel<Object> channel) {
        this.channel = channel;
    }
}
