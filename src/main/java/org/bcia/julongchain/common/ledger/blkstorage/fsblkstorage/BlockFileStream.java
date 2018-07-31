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
import org.bcia.julongchain.core.ledger.util.Util;

import java.io.*;
import java.util.AbstractMap;

/**
 * 操作block文件的流方法
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockFileStream {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockFileStream.class);

    private int fileNum;
    private BlockFileReader reader;
    private File file;
    private long currentOffset;
    private boolean init;

    public BlockFileStream(String rootDir, int fileNum, long startOffset) throws LedgerException{
        //根据rootDir获取filePath
        String filePath = BlockFileManager.deriveBlockfilePath(rootDir, fileNum);
        this.fileNum = fileNum;
        this.file = new File(filePath);
		this.reader = new BlockFileReader(filePath);
        this.currentOffset = startOffset;
        if(this.currentOffset > this.file.length()){
        	logger.error("Current offset is out of file");
            throw new LedgerException("Current offset is out of file");
        }
        logger.debug(String.format("newBlockFileStream(): filePath=[%s], startOffset=[%d]", filePath, startOffset));
        this.init = true;
    }

    /**
     * 读取区块字节
     */
    public AbstractMap.SimpleEntry<byte[], BlockPlacementInfo> nextBlockBytesAndPlacementInfo() throws LedgerException{
        if(!init){
            throw new LedgerException("Block file stream is not init.");
        }
        //当前读取位置为文件结尾
        if(currentOffset == file.length()){
            logger.debug(String.format("Finished reading file number [%d]", fileNum));
            return new AbstractMap.SimpleEntry<>(null, null);
        }
        long remainingBytes = file.length() - currentOffset;
        //代表block长度的部分占8字节
        //剩余文件长度<8时抛出异常
        if(remainingBytes < BlockFileManager.PEEK_BYTES_LEN){
        	String errMsg = String.format("Remaining bytes length =[%d], we need at least [%d] bytes to get block", remainingBytes, BlockFileManager.PEEK_BYTES_LEN);
            logger.error(errMsg);
            throw new LedgerException(errMsg);
        }
        logger.debug(String.format("Remaining bytes=[%d], Going to peek [%d] bytes", remainingBytes, BlockFileManager.PEEK_BYTES_LEN));
        //读取8字节,并解析为block长度
		byte[] lenBytes = reader.read(currentOffset, BlockFileManager.PEEK_BYTES_LEN);
        long length = Util.bytesToLong(lenBytes, 0, BlockFileManager.PEEK_BYTES_LEN);
        //根据解析的block长度,计算剩余长度
        //长度不足则抛出异常
        long expectedBytes = length + BlockFileManager.PEEK_BYTES_LEN;
        if(expectedBytes > remainingBytes){
            logger.error(String.format("At least [%d] bytes expected. Remaing bytes [%d]", expectedBytes, remainingBytes));
            throw new LedgerException("unexpected end of blockfile");
        }
        //读取block, 跳过前8位长度位
        byte[] blockBytes = reader.read(currentOffset + BlockFileManager.PEEK_BYTES_LEN, length);
        //组装BlockPlacementInfo对象
        BlockPlacementInfo blockPlacementInfo = new BlockPlacementInfo(fileNum, currentOffset, currentOffset + BlockFileManager.PEEK_BYTES_LEN);
        //读取完成后,向后移动
        currentOffset += (BlockFileManager.PEEK_BYTES_LEN + length);

        return new AbstractMap.SimpleEntry<>(blockBytes, blockPlacementInfo);
    }

    /**
     * 下一区块
     */
    public byte[] nextBlockBytes() throws LedgerException {
        return nextBlockBytesAndPlacementInfo().getKey();
    }

    /**
     * 文件读取对象
     */
    public void close() throws LedgerException{
        if(!init){
            throw new LedgerException("Block file stream is not init.");
        }
		reader.close();
    }

    public int getFileNum() {
        return fileNum;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

	public void setFileNum(int fileNum) {
		this.fileNum = fileNum;
	}

	public BlockFileReader getReader() {
		return reader;
	}

	public void setReader(BlockFileReader reader) {
		this.reader = reader;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public long getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(long currentOffset) {
        this.currentOffset = currentOffset;
    }
}
