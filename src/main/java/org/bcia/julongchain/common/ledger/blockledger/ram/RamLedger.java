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
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.blockledger.Util;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

/**
 * 内存账本
 *
 * @author sunzongyu
 * @date 2018/04/28
 * @company Dingxuan
 */
public class RamLedger extends ReadWriteBase {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(RamLedger.class);
    private static final Object lock = new Object();

    private int maxSize;
    private int size;
    private SimpleList oldest;
    private SimpleList newest;

    public RamLedger(){}

    public RamLedger(int maxSize, int size, SimpleList oldest, SimpleList newest){
        this.maxSize = maxSize;
        this.size = size;
        this.oldest = oldest;
        this.newest = newest;
    }

    @Override
    public IIterator iterator(Ab.SeekPosition startPosition) throws LedgerException {
        logger.debug("Starting get cursor");
        SimpleList list = null;
        Common.Block block = null;
        switch (startPosition.getTypeCase().getNumber()){
            case Ab.SeekPosition.OLDEST_FIELD_NUMBER:
                logger.debug("Getting OLDEST block");
                SimpleList oldest = this.oldest;
                block = Common.Block.newBuilder()
                        .setHeader(Common.BlockHeader.newBuilder()
                                .setNumber(oldest.getBlock().getHeader().getNumber() - 1)
                                .build())
                        .build();
                list = new SimpleList(oldest, block);
                break;
            case Ab.SeekPosition.NEWEST_FIELD_NUMBER:
                logger.debug("Getting NEWEST block");
                SimpleList newest = this.newest;
                block = Common.Block.newBuilder()
                        .setHeader(Common.BlockHeader.newBuilder()
                                .setNumber(newest.getBlock().getHeader().getNumber() - 1)
                                .build())
                        .build();
                list = new SimpleList(newest, block);
                break;
            case Ab.SeekPosition.SPECIFIED_FIELD_NUMBER:
                oldest = this.oldest;
                newest = this.newest;
                long specified = startPosition.getSpecified().getNumber();
                logger.debug("Attempting to return block " + specified);

                if(specified < oldest.getBlock().getHeader().getNumber()
                        || specified > newest.getBlock().getHeader().getNumber() + 1){
                    logger.debug(String.format("Returning error iterator because specified seek was %d with oldest %d and newest %d",
                            specified, oldest.getBlock().getHeader().getNumber(), newest.getBlock().getHeader().getNumber()));
                    throw Util.NOT_FOUND_ERROR_ITERATOR;
                }

                if(specified == oldest.getBlock().getHeader().getNumber()){
                    block = Common.Block.newBuilder()
                            .setHeader(Common.BlockHeader.newBuilder()
                                    .setNumber(oldest.getBlock().getHeader().getNumber() - 1)
                                    .build())
                            .build();
                    list = new SimpleList(oldest, block);
                    break;
                }

                list = oldest;
                while (list.getBlock().getHeader().getNumber() < specified - 1) {
                    list = list.getNext();
                }
            default:
                break;
        }
        RamCursor cursor = new RamCursor(list);
        long blockNum = list.getBlock().getHeader().getNumber() + 1;

        if(blockNum == ~(long) 0){
            logger.debug("Pass pre genesis block");
            cursor.next();
            blockNum++;
        }
        logger.debug("Finished create ram ledger cursor");
        return cursor;
    }

    @Override
    public long height() throws LedgerException {
        return this.newest.getBlock().getHeader().getNumber() + 1;
    }

    @Override
    public void append(Common.Block block) throws LedgerException {
        if(block.getHeader().getNumber() != this.newest.getBlock().getHeader().getNumber() + 1){
            throw new LedgerException(String.format("Block number should have been %d but was %d", this.newest.getBlock().getHeader().getNumber() + 1, block.getHeader().getNumber()));
        }
        if(this.newest.getBlock().getHeader().getNumber() + 1 != 0){
            if(!block.getHeader().getPreviousHash().equals(this.newest.getBlock().getHeader().getDataHash())){
                throw new LedgerException("Block have had wrong previous hash");
            }
        }

        appendBlock(block);
    }

    private void appendBlock(Common.Block block){
        this.newest.setNext(new SimpleList(null, block));
        this.newest = this.newest.getNext();
        this.size++;
        if(this.size > this.maxSize){
            logger.debug("RAM ledger max size about to be exceeded, removing oldest itm: " + this.oldest.getBlock().getHeader().getNumber());
            this.oldest = oldest.getNext();
            this.size--;
        }
        synchronized (lock){
            lock.notifyAll();
        }
        logger.debug("Appending block " + this.newest.getBlock().getHeader().getNumber() + " success");
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public SimpleList getOldest() {
        return oldest;
    }

    public void setOldest(SimpleList oldest) {
        this.oldest = oldest;
    }

    public SimpleList getNewest() {
        return newest;
    }

    public void setNewest(SimpleList newest) {
        this.newest = newest;
    }

    public static Object getLock() {
        return lock;
    }
}
