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
package org.bcia.julongchain.common.ledger.blockledger;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

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

    /**
     * 获取新的区块
     * 根据当前账本中最新区块的编号获取下一个区块编号
     * @param reader 账本
     * @param messages 区块Data
     */
    public static Common.Block createNextBlock(IReader reader, List<Common.Envelope> messages) throws LedgerException{
        long nextBlockNumber = 0;
        ByteString previousBlockHash = null;
        if(reader.height() > 0){
            //当当前账本中有数据时，应从最新的区块开始读取
            Ab.SeekPosition startPosition = Ab.SeekPosition.newBuilder()
                    .setNewest(Ab.SeekNewest.getDefaultInstance())
                    .build();
            IIterator itr = reader.iterator(startPosition);
            Map.Entry<QueryResult, Common.Status> entry = (Map.Entry<QueryResult, Common.Status>) itr.next().getObj();
            Common.Block block =  (Common.Block) entry.getKey().getObj();
            Common.Status status = entry.getValue();
            if(!status.equals(Common.Status.SUCCESS)){
                throw new LedgerException("Error seeking to newest block for group with non-zero height");
            }
            nextBlockNumber = block.getHeader().getNumber() + 1;
            previousBlockHash = block.getHeader().getDataHash();
        }
        //添加区块Data
        Common.BlockData.Builder dataBuilder = Common.BlockData.newBuilder();
        if (messages != null) {
            messages.forEach((msg) -> {
                dataBuilder.addData(msg.toByteString());
            });
        }
        //组装区块
        return Common.Block.newBuilder()
                .setHeader(Common.BlockHeader.newBuilder()
                        .setNumber(nextBlockNumber)
                        .setPreviousHash(previousBlockHash == null ? ByteString.EMPTY : previousBlockHash)
		                .setDataHash(ByteString.copyFrom(org.bcia.julongchain.core.ledger.util.Util.getHashBytes(dataBuilder.build().toByteArray())))
                        .build())
                .setData(dataBuilder)
                .setMetadata(Common.BlockMetadata.newBuilder()
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .addMetadata(ByteString.EMPTY)
                        .build())
                .build();
    }

    /**
     * 获取区块
     * 当前账本中存在所需区块，直接返回
     * 当前账本中不存在所需区块，阻塞进程并等待append
     * @param IReader 账本
     * @param index 区块编号
     */
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
