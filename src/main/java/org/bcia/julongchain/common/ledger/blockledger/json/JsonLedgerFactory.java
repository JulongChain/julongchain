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
import org.bcia.julongchain.common.ledger.blockledger.IFactory;
import org.bcia.julongchain.common.ledger.blockledger.ReadWriteBase;
import org.bcia.julongchain.common.ledger.util.IoUtil;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json账本工厂
 *
 * @author sunzongyu
 * @date 2018/04/27
 * @company Dingxuan
 */
public class JsonLedgerFactory implements IFactory {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(JsonLedgerFactory.class);

    private String directory;
    private Map<String, ReadWriteBase> ledgers;

    public JsonLedgerFactory(){}

    public JsonLedgerFactory(String directory) throws LedgerException{
        logger.debug("Initializing json ledger at: " + directory);
        //创建目录
        File file = new File(directory);
        if (!file.exists()){
            file.mkdir();
        }
        //初始化
        this.directory = directory;
        this.ledgers = new HashMap<>();

        File[] infos = file.listFiles();
        if (infos != null) {
            for(File info : infos){
                if(!info.isDirectory()){
                    continue;
                }
                String name = info.getName();
                String groupID;
                if(name.startsWith(JsonLedger.GROUP_DIRECTORY_FORMAT_STRING)){
                     groupID = info.getName().substring(JsonLedger.GROUP_DIRECTORY_FORMAT_STRING.length(), info.getName().length());
                } else {
                    continue;
                }
                getOrCreate(groupID);
            }
        }
    }

    @Override
    public synchronized ReadWriteBase getOrCreate(String groupID) throws LedgerException {
        logger.debug("Starting create json ledger using group id " + groupID);
        ReadWriteBase l = ledgers.get(groupID);
        if(l != null){
            logger.debug("Group id " + groupID + " is already exists");
            return l;
        }
        String directory = this.directory + File.separator + JsonLedger.GROUP_DIRECTORY_FORMAT_STRING + groupID;
        logger.debug(String.format("Initializing group %s at: %s", groupID, directory));
        if (!IoUtil.createDirIfMissing(directory)) {
            String errMsg = "Can not create dir " + directory;
            logger.error(errMsg);
            throw new LedgerException(errMsg);
        }
        ReadWriteBase group = newGroup(directory);
        ledgers.put(groupID, group);
        logger.debug("Finished create json ledger");
        return group;
    }

    private ReadWriteBase newGroup(String directory){
        JsonLedger jl = new JsonLedger();
        jl.setDirectory(directory);
        jl.setPrinter(JsonFormat.printer());
        jl.initializeBlockHeight();
        logger.debug("Initialized to block height " + (jl.getHeight() - 1));
        return jl;
    }

    @Override
    public synchronized List<String> groupIDs() throws LedgerException {
        return new ArrayList<>(ledgers.keySet());
    }

    @Override
    public void close() throws LedgerException {
        //nothing to do
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Map<String, ReadWriteBase> getLedgers() {
        return ledgers;
    }

    public void setLedgers(Map<String, ReadWriteBase> ledgers) {
        this.ledgers = ledgers;
    }
}
