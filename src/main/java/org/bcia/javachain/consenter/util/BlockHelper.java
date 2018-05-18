/**
 * Copyright DingXuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.consenter.util;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.csp.gm.sm3.SM3HashOpts;
import org.bcia.javachain.protos.common.Common;

import static org.bcia.javachain.csp.factory.CspManager.getDefaultCsp;

/**
 * @author zhangmingyang
 * @Date: 2018/5/10
 * @company Dingxuan
 */
public class BlockHelper {

    byte[] previousHash;
    byte[] dataHash;
    long number;


    public BlockHelper(byte[] previousHash, byte[] dataHash, long number) {
        this.previousHash = previousHash;
        this.dataHash = dataHash;
        this.number = number;
    }

    public static Common.Block createBlock(long seqNum, byte[] previousHash) {
        Common.Block.Builder block = Common.Block.newBuilder();
        Common.BlockHeader.Builder blockHeader = Common.BlockHeader.newBuilder()
                .setNumber(seqNum)
                .setPreviousHash(ByteString.copyFrom(previousHash));

        Common.BlockData.Builder blockData = Common.BlockData.newBuilder();
        Common.BlockMetadata.Builder metaData = Common.BlockMetadata.newBuilder();
        byte[][] metadataContents;

        //TODO 封装append函数,BlockMetaIndex_name
//        for (int i = 0; i <; i++) {
//
//        }

        block.setHeader(blockHeader);
        block.setData(blockData);
        block.setMetadata(metaData);
        return block.build();
    }


    public static byte[] hash(byte[] data) {
        byte[] digest = new byte[0];
        try {
            digest = getDefaultCsp().hash(data, new SM3HashOpts());
        } catch (JavaChainException e) {
            e.printStackTrace();
        }
        return digest;
    }
}
