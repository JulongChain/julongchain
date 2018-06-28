/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage;

/**
 * 封装block位置信息
 *
 * @author sunzongyu
 * @date 2018/3/8
 * @company Dingxuan
 */
public class BlockPlacementInfo {

    private int fileNum;
    private long blockStartOffset;
    private long blockBytesOffset;

    public BlockPlacementInfo() {
    }

    public BlockPlacementInfo(int fileNum, long blockStartOffset, long blockBytesOffset) {
        this.fileNum = fileNum;
        this.blockStartOffset = blockStartOffset;
        this.blockBytesOffset = blockBytesOffset;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public long getBlockStartOffset() {
        return blockStartOffset;
    }

    public void setBlockStartOffset(long blockStartOffset) {
        this.blockStartOffset = blockStartOffset;
    }

    public long getBlockBytesOffset() {
        return blockBytesOffset;
    }

    public void setBlockBytesOffset(long blockBytesOffset) {
        this.blockBytesOffset = blockBytesOffset;
    }
}
