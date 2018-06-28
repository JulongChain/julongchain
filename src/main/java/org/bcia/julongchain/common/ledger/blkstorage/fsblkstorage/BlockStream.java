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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.BytesHexStrTranslate;

import java.util.AbstractMap;

/**
 * 操作block数据流
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockStream {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockStream.class);

    private String rootDir;
    private int currentFileNum;
    private int endFileNum;
    private BlockFileStream currentFileStream;

    public BlockStream() {
    }

    public BlockStream(String rootDir, int startFileNum, long startOffset, int endFileNum) throws LedgerException{
        BlockFileStream startFileStream = new BlockFileStream(rootDir, startFileNum, startOffset);
        this.rootDir = rootDir;
        this.currentFileNum = startFileNum;
        this.endFileNum = endFileNum;
        this.currentFileStream = startFileStream;
    }

    /**
     * 移动到下一个BlockfileStream
     */
    public void moveToNextBlockFileStream() throws LedgerException{
        currentFileStream.close();
        currentFileNum++;
        currentFileStream = new BlockFileStream(rootDir, currentFileNum, 0);
    }

    /**
     * 下一区块
     */
    public byte[] nextBlockBytes() throws LedgerException{
        return nextBlockBytesAndPlacementInfo().getKey();
    }

    /**
     * 下一区块并返回当前信息
     */
    public AbstractMap.SimpleEntry<byte[], BlockPlacementInfo> nextBlockBytesAndPlacementInfo() throws LedgerException{
        AbstractMap.SimpleEntry<byte[], BlockPlacementInfo> entry = currentFileStream.nextBlockBytesAndPlacementInfo();
        byte[] blockBytes = entry.getKey();
        logger.debug(String.format("Blockbytes [%s] read from file [%d]", BytesHexStrTranslate.bytesToHexFun1(blockBytes), currentFileNum));
        //当前文件无法读取出block
		boolean expected = ((blockBytes == null || blockBytes.length == 0) && (currentFileNum < endFileNum || endFileNum < 0));
        if(expected){
            logger.debug(String.format("Current file [%d] exhausted. Moving to next file", currentFileNum));
            moveToNextBlockFileStream();
            return nextBlockBytesAndPlacementInfo();
        }
        return entry;
    }

    /**
     * 关闭
     */
    public void close() throws LedgerException{
        try {
            currentFileStream.close();
        } catch (LedgerException e) {
            throw e;
        }
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

	public int getCurrentFileNum() {
		return currentFileNum;
	}

	public void setCurrentFileNum(int currentFileNum) {
		this.currentFileNum = currentFileNum;
	}

	public int getEndFileNum() {
        return endFileNum;
    }

    public void setEndFileNum(int endFileNum) {
        this.endFileNum = endFileNum;
    }

    public BlockFileStream getCurrentFileStream() {
        return currentFileStream;
    }

    public void setCurrentFileStream(BlockFileStream currentFileStream) {
        this.currentFileStream = currentFileStream;
    }
}
