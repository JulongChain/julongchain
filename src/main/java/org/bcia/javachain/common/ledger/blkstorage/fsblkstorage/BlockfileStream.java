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
package org.bcia.javachain.common.ledger.blkstorage.fsblkstorage;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.util.Util;

import java.io.*;
import java.util.AbstractMap;

/**
 * 操作block文件
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockfileStream {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockfileStream.class);

    private Integer fileNum;
    private File file;
    private InputStream reader;
    private Long currentOffset;
    private boolean init = false;

    public Integer getFileNum() {
        return fileNum;
    }

    public void setFileNum(Integer fileNum) {
        this.fileNum = fileNum;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public InputStream getReader() {
        return reader;
    }

    public void setReader(InputStream reader) {
        this.reader = reader;
    }

    public Long getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(Long currentOffset) {
        this.currentOffset = currentOffset;
    }

    /**
     * 初始化
     */
    public BlockfileStream newBlockFileStream(String rootDir, int fileNum, long startOffset) throws LedgerException{
        //根据rootDir获取filePath
        String filePath = null;
        filePath = BlockfileMgr.deriveBlockfilePath(rootDir, fileNum);
        this.fileNum = fileNum;
        this.file = new File(filePath);
        try {
            this.reader = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new LedgerException(e);
        }
        this.currentOffset = startOffset;
        if(currentOffset > file.length()){
            throw new LedgerException("Current offset is out of file");
        }
        logger.debug(String.format("newBlockFileStream(): filePath=[%s], startOffset=[%d]", filePath, startOffset));
        init = true;
        return this;
    }

    /**
     * 读取区块字节
     */
    public AbstractMap.SimpleEntry<byte[], BlockPlacementInfo> nextBlockBytesAndPlacementInfo() throws LedgerException{
        if(!init){
            throw new LedgerException("Block file stream is not init.");
        }
        byte[] lenBytes = new byte[8];
        boolean moreContentAvailable = true;
        //当前读取位置为文件结尾
        if(currentOffset == file.length()){
            logger.debug(String.format("Finished reading file number [%d]", fileNum));
            return null;
        }
        long remainingBytes = file.length() - currentOffset;
        //代表block长度的部分占8字节
        //剩余文件长度<8时抛出异常
        int peekBytes = 8;
        if(remainingBytes < peekBytes){
            moreContentAvailable = false;
            logger.debug(String.format("Remaining bytes=[%d], nothing to read", remainingBytes));
            return null;
        }
        logger.debug(String.format("Remaining bytes=[%d], Going to peek [%d] bytes", remainingBytes, peekBytes));
        //读取8字节,并解析为block长度
        try {
            reader.read(lenBytes, currentOffset.intValue(), peekBytes);
        } catch (Throwable e) {
            throw new LedgerException(e);
        }
        long length = Util.bytesToLong(lenBytes, 0, peekBytes);
        //根据解析的block长度,计算剩余长度
        //长度不足则抛出异常
        long expectedBytes = length + peekBytes;
        if(expectedBytes > remainingBytes){
            logger.debug(String.format("At least [%d] bytes expected. Remaing bytes [%d]", expectedBytes, remainingBytes));
            throw new LedgerException("unexpected end of blockfile");
        }
        //读取block
        byte[] blockBytes = new byte[(int) length];
        try {
            //从8开始读取
//            reader.skip(peekBytes);
            reader.read(blockBytes);
        } catch (Throwable e) {
            throw new LedgerException(e);
        }
        //组装BlockPlacementInfo对象
        BlockPlacementInfo blockPlacementInfo = new BlockPlacementInfo();
        blockPlacementInfo.setFileNum(fileNum);
        blockPlacementInfo.setBlockStartOffset(currentOffset);
        blockPlacementInfo.setBlockBytesOffset(currentOffset + 8);
        //读取完成后,向后移动
        currentOffset = 8 + length;

        AbstractMap.SimpleEntry<byte[], BlockPlacementInfo> entry = new AbstractMap.SimpleEntry<>(blockBytes, blockPlacementInfo);
        return entry;
    }

    /**
     * 下一区块
     */
    public byte[] nextBlockBytes() throws LedgerException {
        return nextBlockBytesAndPlacementInfo().getKey();
    }

    /**
     * 关闭文件
     */
    public void close() throws LedgerException{
        if(!init){
            throw new LedgerException("Block file stream is not init.");
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new LedgerException(e);
        }
    }
}
