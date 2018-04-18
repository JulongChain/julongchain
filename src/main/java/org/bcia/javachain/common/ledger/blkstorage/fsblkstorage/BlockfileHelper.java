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

import com.sun.corba.se.impl.encoding.BufferManagerReadGrow;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;

import java.io.File;
import java.util.List;

/**
 * 提供在区块文件中检索区块信息的方法
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockfileHelper {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockfileHelper.class);
    private static final String BLOCK_FILE_PREFIX = "blockfile_";

    /**
     * 根据区块文件, 组织结点信息
     */
    public static CheckpointInfo constructCheckpointInfoFromBlockFiles(String rootDir) throws LedgerException {
        logger.debug("Retrieving checkpoint info from block files");
        int lastFileNum;
        int numBlocksInFile;
        long endOffsetLastBlock;
        long lastBlockNum = 0;
        byte[] lastBlockBytes;
        Common.Block lastBlock;
        List<Object> list;
        File fileInfo;
        lastFileNum = retrieveLastFileSuffix(rootDir);
        logger.debug("Last file number found = %d", lastFileNum);
        //没有找到文件
        if(lastFileNum == -1){
            logger.debug("File not found");
            CheckpointInfo cpInfo = new CheckpointInfo();
            cpInfo.setLastestFileChunkSuffixNum(0);
            cpInfo.setLatestFileChunksize(0);
            cpInfo.setChainEmpty(true);
            cpInfo.setLastBlockNumber((long) 0);
            return cpInfo;
        }
        fileInfo = getFileInfoOrPanic(rootDir,lastFileNum);
        logger.debug(String.format("Last block file info: Filename=[%s]", fileInfo.getName()));
        list = BlockfileMgr.scanForLastCompleteBlock(rootDir, lastFileNum, (long) 0);
        lastBlockBytes = (byte[]) list.get(BlockfileMgr.LAST_BLOCK_BYTES);
        endOffsetLastBlock = (long) list.get(BlockfileMgr.CURRENT_OFFSET);
        numBlocksInFile = (int) list.get(BlockfileMgr.NUM_BLOCKS);

        if(numBlocksInFile == 0 && lastFileNum > 0){
            int secondLastFileNum = lastFileNum - 1;
            fileInfo = getFileInfoOrPanic(rootDir, secondLastFileNum);
            logger.debug(String.format("Second last block file info: FileName=[%s]", fileInfo.getName()));
            lastBlockBytes = (byte[]) BlockfileMgr.scanForLastCompleteBlock(rootDir, secondLastFileNum, (long) 0).get(BlockfileMgr.LAST_BLOCK_BYTES);
        }

        if(lastBlockBytes != null){
            lastBlock = BlockSerialization.deserializeBlock(lastBlockBytes);
            lastBlockNum = lastBlock.getHeader().getNumber();
        }
        //组装产品Info
        CheckpointInfo cpInfo = new CheckpointInfo();
        cpInfo.setLastBlockNumber(lastBlockNum);
        cpInfo.setLatestFileChunksize((int) endOffsetLastBlock);
        cpInfo.setLastestFileChunkSuffixNum(lastFileNum);
        cpInfo.setChainEmpty(lastFileNum == 0 && numBlocksInFile == 0);
        return cpInfo;
    }

    /**
     * 检索最近的区块文件号
     */
    public static Integer retrieveLastFileSuffix(String rootDir) throws LedgerException{
        logger.debug("retrieveLastFileSuffix()");
        int biggestFileNum = -1;
        File[] filesInfo;
        try {
            filesInfo = new File(rootDir).listFiles();
        } catch (Exception e) {
            throw new LedgerException("File to list files in " + rootDir);
        }
        for (int i = 0; filesInfo != null && i < filesInfo.length; i++) {
            File file =  filesInfo[i];
            String name = file.getName();
            if(file.isDirectory() || !isBlockFileName(name)){
                logger.debug("Skipping file name " + name);
                continue;
            }
            String fileSuffix = null;
            fileSuffix = name.substring(0, BLOCK_FILE_PREFIX.length());
            int fileNum = Integer.valueOf(fileSuffix);
            if(fileNum > biggestFileNum){
                biggestFileNum = fileNum;
            }
        }
        logger.debug("retrieveLastFileSuffix() - biggestFileNum = " + biggestFileNum);
        return biggestFileNum;
    }

    /**
     * 判断文件是否以BLOCK_FILE_PREFIX起始
     */
    public static Boolean isBlockFileName(String name) {
        return name.startsWith(BLOCK_FILE_PREFIX);
    }

    /**
     * 获取文件
     */
    public static File getFileInfoOrPanic(String rootDir, Integer fileNum) throws LedgerException{
        String filePath = BlockfileMgr.deriveBlockfilePath(rootDir, fileNum);
        File fileInfo = new File(filePath);
        if (!fileInfo.exists()){
            throw new LedgerException("Fail to retrieving file for file num " + fileNum);
        }
        return fileInfo;
    }
}
