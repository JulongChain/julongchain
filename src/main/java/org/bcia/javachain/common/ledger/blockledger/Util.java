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
package org.bcia.javachain.common.ledger.blockledger;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;

import java.util.List;
import java.util.Map;

/**
 * 提供账本读写工具
 *
 * @author sunzongyu
 * @date 2018/04/26
 * @company Dingxuan
 */
public class Util {
    public static final LedgerException NOT_FOUND_ERROR_ITERATOR = new LedgerException("Not found iterator");
    public static final Object CLOSED_CHAIN = "closed chain";
    public static final Object READY_CHAIN = "ready chain";

    public static Common.Block createNextBlock(IReader IReader, List<Common.Envelope> messages) throws LedgerException{
        long nextBlockNumber = 0;
        ByteString previousBlockHash = null;
        if(IReader.height() > 0){
            IIterator itr = IReader.iterator(Ab.SeekPosition.getDefaultInstance());
            synchronized (itr.getLock()){
                try {
                    itr.getLock().wait();
                } catch (InterruptedException e) {
                    throw new LedgerException(e);
                }
            }
            Map.Entry<QueryResult, Common.Status> entry = (Map.Entry<QueryResult, Common.Status>) itr.next();
            Common.Block block =  (Common.Block) entry.getKey().getObj();
            Common.Status status = entry.getValue();
            if(!status.equals(Common.Status.SUCCESS)){
                throw new LedgerException("Error seeking to newest block for group with non-zero height");
            }
            nextBlockNumber = block.getHeader().getNumber() + 1;
            previousBlockHash = block.getHeader().getPreviousHash();
        }

        Common.BlockData.Builder dataBuilder = Common.BlockData.newBuilder();

        if (messages != null) {
            messages.forEach((msg) -> {
                dataBuilder.addData(msg.toByteString());
            });
        }

        Common.Block block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setNumber(nextBlockNumber)
                        .setPreviousHash(previousBlockHash == null ? ByteString.EMPTY : previousBlockHash)
                        .build())
                .setData(dataBuilder)
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();

        return block;
    }

    public static Common.Block getBlock(IReader IReader, long index) throws LedgerException {
        IIterator i = IReader.iterator(Ab.SeekPosition.newBuilder()
                .setSpecified(Ab.SeekSpecified.newBuilder()
                        .setNumber(index)
                        .build())
                .build());
        Object token = null;
        //判断是否需要阻塞进程
        i.readyChain();
        Map.Entry<QueryResult, Common.Status> entry = (Map.Entry<QueryResult, Common.Status>) i.next().getObj();
        Common.Block block = (Common.Block) entry.getKey().getObj();
        Common.Status status = entry.getValue();
        if (!Common.Status.SUCCESS.equals(status)) {
            return null;
        }
        return block;
    }
}
