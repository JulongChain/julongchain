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
import org.bcia.julongchain.common.ledger.blockledger.IFactory;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内存账本工厂
 *
 * @author sunzongyu
 * @date 2018/04/28
 * @company Dingxuan
 */
public class RamLedgerFactory implements IFactory {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(RamLedgerFactory.class);

    private int maxSize;
    private Map<String, ReadWriteBase> ledgers;

    public RamLedgerFactory(int maxSize){
        this.maxSize = maxSize;
        this.ledgers = new HashMap<>();
    }

    @Override
    public synchronized ReadWriteBase getOrCreate(String groupID) throws LedgerException {
        logger.debug("Starting create ledger using group id " + groupID);
        ReadWriteBase l = ledgers.get(groupID);
        if(l != null){
            logger.debug("Group " + groupID + " is already exists");
            return l;
        }
        l = newGroup(maxSize);
        ledgers.put(groupID, l);
        logger.debug("Finished create ledger");
        return l;
    }

    private ReadWriteBase newGroup(int maxSize){
        Common.Block preGenesis = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setNumber(~(long) 0)
                        .build())
                .build();
        RamLedger rl = new RamLedger(maxSize, 1, new SimpleList(null, preGenesis), null);
        rl.setNewest(rl.getOldest());
        logger.debug("Creating new group, pre genesis block num is " + preGenesis.getHeader().getNumber());
        return rl;
    }

    @Override
    public synchronized List<String> groupIDs() throws LedgerException {
        return new ArrayList<>(ledgers.keySet());
    }

    @Override
    public void close() throws LedgerException {
        //nothing to do
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Map<String, ReadWriteBase> getLedgers() {
        return ledgers;
    }

    public void setLedgers(Map<String, ReadWriteBase> ledgers) {
        this.ledgers = ledgers;
    }
}
