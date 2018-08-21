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
package org.bcia.julongchain.common.ledger.blockledger.json;

import com.google.protobuf.util.JsonFormat;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.blockledger.IIterator;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.util.AbstractMap;
import java.util.Arrays;

import static org.bcia.julongchain.core.ledger.util.Util.*;

/**
 * Json账本
 *
 * @author sunzongyu
 * @date 2018/04/27
 * @company Dingxuan
 */
public class JsonLedger extends ReadWriteBase {
    private static JulongChainLog log = JulongChainLogFactory.getLog(JsonLedger.class);
    private static final Object LOCK = new Object();
    public static final String GROUP_DIRECTORY_FORMAT_STRING  = "chain_";
    public static final String BLOCK_FILE_FORMAT_STRING  = "block_%020d.json";

    private String directory;
    private long height;
    private byte[] lastHash;
    private JsonFormat.Printer printer;

    /**
     * 初始化链
     */
    public void initializeBlockHeight(){
        File dir = new File(directory);
        File[] infoes = dir.listFiles();
		if (infoes == null) {
			return;
		}
        long nextNumber = 0;
        //迭代目录中所有文件
        //排除不合法文件并获取最大区块号
        for(File info : infoes){
            if(info.isDirectory()){
                continue;
            }
            String name = info.getName();
            long number;
            if(name.startsWith(GROUP_DIRECTORY_FORMAT_STRING)){
                number = Long.parseLong(name.substring(GROUP_DIRECTORY_FORMAT_STRING.length()));
            } else {
                continue;
            }
            if(number != nextNumber){
                log.error("Missing block " + nextNumber + " in chain");
            }
            nextNumber++;
        }
        //没有区块时直接返回
        height = nextNumber;
        if(height == 0){
            return;
        }
        //读取最大的区块
        AbstractMap.SimpleImmutableEntry<Common.Block, Boolean> entry = readBlock(height - 1);
        Common.Block block = entry.getKey();
        boolean found = entry.getValue();
        if (!found) {
            log.error(String.format("Block %d was in directory listing but error reading", height - 1));
        }
        if(block == null){
            log.error("Error reading block " + (height - 1));
        } else {
            lastHash = block.getHeader().getDataHash().toByteArray();
        }
    }


    public synchronized AbstractMap.SimpleImmutableEntry<Common.Block, Boolean> readBlock(long number){
        String name = blockFileName(number);
        BufferedReader reader;
        //没有对应文件，返回null, false
        try{
            reader = new BufferedReader(new FileReader(name));
        } catch (FileNotFoundException e) {
            return new AbstractMap.SimpleImmutableEntry(null, false);
        }

        try{
            StringBuilder blockJsonBuffer = new StringBuilder();
            String s;
            while((s = reader.readLine()) != null){
                blockJsonBuffer.append(s).append("\n");
            }
            Common.Block.Builder builder = Common.Block.newBuilder();
            JsonFormat.parser().merge(blockJsonBuffer.toString(), builder);
            log.debug("Read block " + builder.getHeader().getNumber());
            //成功读取，返回true
            return new AbstractMap.SimpleImmutableEntry(builder.build(), true);
        } catch (Exception e){
            //读取时出现异常，返回null, true
            return new AbstractMap.SimpleImmutableEntry(null, true);
        } finally {
            try {
				reader.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public IIterator iterator(Ab.SeekPosition startPosition) throws LedgerException {
        switch (startPosition.getTypeCase().getNumber()){
            case Ab.SeekPosition.OLDEST_FIELD_NUMBER:
                return new JsonCursor(this, 0);
            case Ab.SeekPosition.NEWEST_FIELD_NUMBER:
                return new JsonCursor(this, height - 1);
            case Ab.SeekPosition.SPECIFIED_FIELD_NUMBER:
                if(startPosition.getSpecified().getNumber() > height){
                	throw new LedgerException("Not found iterator");
                }
                return new JsonCursor(this, startPosition.getSpecified().getNumber());
            default:
				throw new LedgerException("Not found iterator");
        }
    }

    @Override
    public long height() {
        return height;
    }

    @Override
    public void append(Common.Block block) throws LedgerException {
        if(block.getHeader().getNumber() != height){
            throw new LedgerException("Block number should have been " + height + " but was " + block.getHeader().getNumber());
        }
		byte[] preHash = block.getHeader().getPreviousHash().toByteArray();
		if(lastHash != null && !Arrays.equals(lastHash, preHash)){
            throw new LedgerException(String.format("Block should's previous hash is [%s]\n but last hash is [%s]",
					Hex.toHexString(preHash),
					Hex.toHexString(lastHash)));
        }
        writeBlock(block);
        lastHash = getHashBytes(block.getHeader().toByteArray());
        height++;
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    private synchronized void writeBlock(Common.Block block) throws LedgerException{
        String name = blockFileName(block.getHeader().getNumber());
        File file = new File(name);
        BufferedWriter writer;
        try {
            if (!IoUtil.createFileIfMissing(name)) {
                String errMsg = "Can not create file " + name;
                log.error(errMsg);
                throw new LedgerException(errMsg);
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(JsonFormat.printer().print(block));
            writer.flush();
            log.debug("Wrote block " + block.getHeader().getNumber());
            writer.close();
        } catch (IOException e) {
            throw new LedgerException(e);
        }
    }

    public String blockFileName(long number){
        return directory + File.separator + String.format(BLOCK_FILE_FORMAT_STRING, number);
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public JsonFormat.Printer getPrinter() {
        return printer;
    }

    public void setPrinter(JsonFormat.Printer printer) {
        this.printer = printer;
    }

    public static Object getLock() {
        return LOCK;
    }

	public byte[] getLastHash() {
		return lastHash;
	}

	public void setLastHash(byte[] lastHash) {
		this.lastHash = lastHash;
	}
}
