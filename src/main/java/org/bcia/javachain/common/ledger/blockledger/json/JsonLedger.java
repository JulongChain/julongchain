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
package org.bcia.javachain.common.ledger.blockledger.json;

import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.ledger.blockledger.Iterator;
import org.bcia.javachain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.javachain.common.ledger.blockledger.Util;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.smartcontract.shim.helper.Channel;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;

import java.io.*;

/**
 * Json账本
 *
 * @author sunzongyu
 * @date 2018/04/27
 * @company Dingxuan
 */
public class JsonLedger extends ReadWriteBase {
    public static final String GROUP_DIRECTORY_FORMAT_STRING  = "chain_";
    public static final String BLOCK_FILE_FORMAT_STRING  = "block_%020d.json";

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(JavaChainLog.class);

    private String directory;
    private long height;
    private ByteString lashHash;
    private Channel<Object> channle;
    private JsonFormat.Printer printer;

    public void initializeBlockHeight(){
        File dir = new File(directory);
        File[] infos = dir.listFiles();
        long nextNumber = 0;
        for(File info : infos){
            if(info.isDirectory()){
                continue;
            }
            String name = info.getName();
            long number = 0;
            if(name.startsWith(GROUP_DIRECTORY_FORMAT_STRING)){
                number = Long.parseLong(name.substring(GROUP_DIRECTORY_FORMAT_STRING.length(), name.length()));
            } else {
                continue;
            }
            if(number != nextNumber){
                logger.error("Missing block " + nextNumber + " in chain");
            }
            nextNumber++;
        }
        height = nextNumber;
        if(height == 0){
            return;
        }
        Common.Block block = null;
        try {
            block = readBlock(height - 1);
        } catch (FileNotFoundException e) {
            logger.error(String.format("Block %d was in directory listing but error reading", height - 1));
        }
        if(block == null){
            logger.error("Error reading block " + (height - 1));
        }
        lashHash = block.getHeader().getDataHash();
    }

    public synchronized Common.Block readBlock(long number) throws FileNotFoundException{
        String name = blockFileName(number);
        try{
            BufferedReader reader = new BufferedReader(new FileReader(name));
            StringBuffer blockJsonBuffer = new StringBuffer("");
            String s = "";
            while((s = reader.readLine()) != null){
                blockJsonBuffer.append(s + "\n");
            }
            Common.Block.Builder builder = Common.Block.newBuilder();
            JsonFormat.parser().merge(blockJsonBuffer.toString(), builder);
            logger.debug("Read block " + builder.getHeader().getNumber());
            return builder.build();
        } catch (FileNotFoundException e){
            throw e;
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public Iterator iterator(Ab.SeekPosition startPosition) throws LedgerException {
        switch (startPosition.getTypeCase().getNumber()){
            case Ab.SeekPosition.OLDEST_FIELD_NUMBER:
                return new JsonCursor(this, 0);
            case Ab.SeekPosition.NEWEST_FIELD_NUMBER:
                return new JsonCursor(this, height - 1);
            case Ab.SeekPosition.SPECIFIED_FIELD_NUMBER:
                if(Ab.SeekSpecified.NUMBER_FIELD_NUMBER > height){
                    throw Util.NOT_FOUND_ERROR_ITERATOR;
                }
                return new JsonCursor(this, Ab.SeekSpecified.NUMBER_FIELD_NUMBER);
            default:
                throw Util.NOT_FOUND_ERROR_ITERATOR;
        }
    }

    @Override
    public long height() throws LedgerException {
        return height;
    }

    @Override
    public void append(Common.Block block) throws LedgerException {
        if(block.getHeader().getNumber() != height){
            throw new LedgerException("Block number should have been " + height + " but was " + block.getHeader().getNumber());
        }
        if(!lashHash.equals(block.getHeader().getDataHash())){
            throw new LedgerException("Block number should have right hash");
        }
        writeBlock(block);
        lashHash = block.getHeader().getDataHash();
        height++;
        channle.close();
        channle = new Channel<>();
    }

    public synchronized void writeBlock(Common.Block block) throws LedgerException{
        String name = blockFileName(block.getHeader().getNumber());
        File file = new File(name);
        BufferedWriter writer = null;
        try {
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(JsonFormat.printer().print(block));
            writer.flush();
            logger.debug("Wrote block " + block.getHeader().getNumber());
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

    public ByteString getLashHash() {
        return lashHash;
    }

    public void setLashHash(ByteString lashHash) {
        this.lashHash = lashHash;
    }

    public JsonFormat.Printer getPrinter() {
        return printer;
    }

    public void setPrinter(JsonFormat.Printer printer) {
        this.printer = printer;
    }

    public Channel<Object> getChannle() {
        return channle;
    }

    public void setChannle(Channel<Object> channle) {
        this.channle = channle;
    }
}
