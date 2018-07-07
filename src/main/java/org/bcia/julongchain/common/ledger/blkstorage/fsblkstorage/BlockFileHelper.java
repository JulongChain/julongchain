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
import org.bcia.julongchain.protos.common.Common;

import java.io.File;
import java.util.List;

/**
 * 提供在区块文件中检索区块信息的方法
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class BlockFileHelper {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(BlockFileHelper.class);
    private static final String BLOCK_FILE_PREFIX = "blockfile_";

    /**
     * 根据区块文件, 组织检查点信息
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
        //检索最近添加的区块文件
        lastFileNum = retrieveLastFileSuffix(rootDir);
        logger.debug(String.format("Last file number found = %d", lastFileNum));
        //没有找到文件, 初始化检查点信息
        if(lastFileNum == -1){
            logger.debug("File not found");
            return new CheckpointInfo(0, 0, true, 0);
        }
        //获取最近添加的区块文件
        fileInfo = getFileInfo(rootDir,lastFileNum);
        logger.debug(String.format("Last block file info: Filename=[%s]", fileInfo.getName()));
        //获取最新区块信息
        list = BlockFileManager.scanForLastCompleteBlock(rootDir, lastFileNum, (long) 0);
        //区块信息
        lastBlockBytes = (byte[]) list.get(BlockFileManager.LAST_BLOCK_BYTES);
        //位置
        endOffsetLastBlock = (long) list.get(BlockFileManager.CURRENT_OFFSET);
        //文件中区块数量
        numBlocksInFile = (int) list.get(BlockFileManager.NUM_BLOCKS);
        //最新区块文件中没有区块, 则倒数第二区块文件的最新区块为最新区块
        if(numBlocksInFile == 0 && lastFileNum > 0){
            int secondLastFileNum = lastFileNum - 1;
            fileInfo = getFileInfo(rootDir, secondLastFileNum);
            logger.debug(String.format("Second last block file info: FileName=[%s]", fileInfo.getName()));
            lastBlockBytes = (byte[]) BlockFileManager.scanForLastCompleteBlock(rootDir, secondLastFileNum, (long) 0).get(BlockFileManager.LAST_BLOCK_BYTES);
        }
        //解析区块
        if(lastBlockBytes != null){
            lastBlock = BlockSerialization.deserializeBlock(lastBlockBytes);
            lastBlockNum = lastBlock.getHeader().getNumber();
        }
        //组装检查点
        return new CheckpointInfo(lastFileNum, (int) endOffsetLastBlock, lastFileNum == 0 && numBlocksInFile == 0, lastBlockNum);
    }

    /**
     * 检索区块文件号最大的文件编号
     */
    private static int retrieveLastFileSuffix(String rootDir) throws LedgerException{
        logger.debug("retrieveLastFileSuffix()");
        int biggestFileNum = -1;
        File[] filesInfo;
        try {
            //获取rootdir中所有文件
            filesInfo = new File(rootDir).listFiles();
        } catch (Exception e) {
            throw new LedgerException("File to list files in " + rootDir);
        }
        for (int i = 0; filesInfo != null && i < filesInfo.length; i++) {
            File file =  filesInfo[i];
            String name = file.getName();
            //跳过文件名错误的文件和目录文件
            if(file.isDirectory() || !isBlockFileName(name)){
                logger.debug("Skipping file name " + name);
                continue;
            }
            //截取文件编号
            String fileSuffix = name.substring(BLOCK_FILE_PREFIX.length());
            int fileNum = Integer.valueOf(fileSuffix);
            if(fileNum > biggestFileNum){
                biggestFileNum = fileNum;
            }
        }
        logger.debug("retrieveLastFileSuffix() - biggestFileNum = " + biggestFileNum);
        return biggestFileNum;
    }

    /**
     * 判断文件是否以BLOCK_FILE_PREFIX(blockfile)起始
     */
    private static Boolean isBlockFileName(String name) {
        return name.startsWith(BLOCK_FILE_PREFIX);
    }

    /**
     * 根据区块文件编号, 获取文件
     */
    private static File getFileInfo(String rootDir, int fileNum) throws LedgerException{
        String filePath = BlockFileManager.deriveBlockfilePath(rootDir, fileNum);
        File fileInfo = new File(filePath);
        if (!fileInfo.exists()){
            throw new LedgerException("Fail to retrieving file for file num " + fileNum);
        }
        return fileInfo;
    }
}
