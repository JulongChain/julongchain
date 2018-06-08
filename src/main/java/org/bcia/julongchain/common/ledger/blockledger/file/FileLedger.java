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
package org.bcia.julongchain.common.ledger.blockledger.file;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blockledger.*;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;
import org.bcia.julongchain.protos.consenter.Ab;

/**
 * 文件账本
 *
 * @author sunzongyu
 * @date 2018/04/26
 * @company Dingxuan
 */
public class FileLedger extends ReadWriteBase {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(FileLedger.class);
    private static final Object lock = new Object();

    private IFileLedgerBlockStore blockStore;

    public FileLedger(){}

    public FileLedger(IFileLedgerBlockStore blockStore){
        this.blockStore = blockStore;
    }

    /**
     * 获取迭代器
     */
    @Override
    public IIterator iterator(Ab.SeekPosition startType) throws LedgerException  {
        long startingBlockNumber;
        switch (startType.getTypeCase().getNumber()){
            case Ab.SeekPosition.OLDEST_FIELD_NUMBER:
                startingBlockNumber = 0;
                break;
            case Ab.SeekPosition.NEWEST_FIELD_NUMBER:
                Ledger.BlockchainInfo info = blockStore.getBlockchainInfo();
                startingBlockNumber = info.getHeight() - 1;
                break;
            case Ab.SeekPosition.SPECIFIED_FIELD_NUMBER:
                startingBlockNumber = startType.getSpecified().getNumber();
                long height = height();
                if(startingBlockNumber > height){
                    throw Util.NOT_FOUND_ERROR_ITERATOR;
                }
                break;
            default:
                throw Util.NOT_FOUND_ERROR_ITERATOR;
        }
        IResultsIterator iterator = blockStore.retrieveBlocks(startingBlockNumber);
        return new FileLedgerIterator(this, startingBlockNumber, iterator);
    }

    /**
     *
     */
    @Override
    public long height() throws LedgerException {
        return blockStore.getBlockchainInfo().getHeight();
    }

    @Override
    public void append(Common.Block block) throws LedgerException{
        blockStore.addBlock(block);
        synchronized (lock){
            lock.notifyAll();
        }
    }

    public IFileLedgerBlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(IFileLedgerBlockStore blockStore) {
        this.blockStore = blockStore;
    }

    public static Object getLock() {
        return lock;
    }
}
