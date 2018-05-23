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
 * 提供文件读写工具
 *
 * @author sunzongyu
 * @date 2018/04/26
 * @company Dingxuan
 */
public class Util {
    public static final LedgerException NOT_FOUND_ERROR_ITERATOR = new LedgerException("Not found iterator");

    public static Common.Block createNextBlock(IReader IReader, List<Common.Envelope> messages) throws LedgerException{
        long nextBlockNumber = 0;
        ByteString previousBlockHash = null;
        if(IReader.height() > 0){
            Iterator itr = IReader.iterator(Ab.SeekPosition.getDefaultInstance());
            try {
                itr.readyChain().take();
            } catch (InterruptedException e) {
                throw new LedgerException(e);
            }
            Map.Entry<QueryResult, Common.Status> entry = (Map.Entry<QueryResult, Common.Status>) itr.next();
            Common.Block block = null;
            try {
                block = Common.Block.parseFrom(((ByteString) entry.getKey()));
            } catch (InvalidProtocolBufferException e) {
                throw new LedgerException(e);
            }
            Common.Status status = entry.getValue();
            if(!status.equals(Common.Status.SUCCESS)){
                throw new LedgerException("Error seeking to newest block for group with non-zero height");
            }
            nextBlockNumber = block.getHeader().getNumber() + 1;
            previousBlockHash = block.getHeader().getPreviousHash();
        }

        Common.BlockData.Builder dataBuilder = Common.BlockData.newBuilder();

        messages.forEach((msg) -> {
            dataBuilder.addData(msg.toByteString());
        });
        Common.Block block = Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setNumber(nextBlockNumber)
                        .setPreviousHash(previousBlockHash)
                        .build())
                .setData(dataBuilder)
                .build();
        return block;
    }

    public static Common.Block getBlock(IReader IReader, long index) throws LedgerException{
        Iterator i = IReader.iterator(Ab.SeekPosition.newBuilder()
                .setSpecified(Ab.SeekSpecified.newBuilder()
                        .setNumber(index)
                        .build())
                .build());
        Object token = null;
        try {
            token = i.readyChain().take();
        } catch (InterruptedException e) {
            throw new LedgerException(e);
        }
        if(token != null){
            Map.Entry<QueryResult, Common.Status> entry = (Map.Entry<QueryResult, Common.Status>) i.next();
            ByteString blockByteString = (ByteString) entry.getKey();
            Common.Status status = entry.getValue();
            if(!status.equals(Common.Status.SUCCESS)){
                return null;
            }
            try {
                return Common.Block.parseFrom(blockByteString);
            } catch (InvalidProtocolBufferException e) {
                throw new LedgerException(e);
            }
        } else {
            return null;
        }
    }
}
