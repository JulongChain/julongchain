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
package org.bcia.julongchain.common.ledger.blockledger.ram;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.protos.common.Common;

import java.util.AbstractMap;

/**
 * 内存账本迭代器
 *
 * @author sunzongyu
 * @date 2018/04/28
 * @company Dingxuan
 */
public class RamCursor implements IIterator {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(RamCursor.class);

    private SimpleList list;

    public RamCursor(){}

    public RamCursor(SimpleList list){
        this.list = list;

    }

    @Override
    public QueryResult next() throws LedgerException {
        //当存在next时，返回next
        //当不存在next时阻塞进程，等待append block
        while (true) {
            if(list.getNext() != null){
                list = list.getNext();
                return new QueryResult(new AbstractMap.SimpleImmutableEntry(new QueryResult(list.getBlock()), Common.Status.SUCCESS));
            }
            synchronized (RamLedger.getLock()){
                logger.debug("Waiting for block number append");
                try {
                    RamLedger.getLock().wait();
                } catch (InterruptedException e) {
                    throw new LedgerException(e);
                }
            }
        }
    }

    @Override
    public void readyChain() throws LedgerException {
        //RamLedger中在next时进行了判断是否长度足够，故nothing to do
    }

    @Override
    public void close() throws LedgerException {
        //nothing to do
    }

    public SimpleList getList() {
        return list;
    }

    public void setList(SimpleList list) {
        this.list = list;
    }

}
