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

import org.bcia.javachain.common.ledger.blkstorage.IndexConfig;
import org.bcia.javachain.common.ledger.util.leveldbhelper.DBHandle;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/8
 * @company Dingxuan
 */
public class BlockfileMgr {

    public static final String BLOCKFILE_PREFIX = "blockfile";

    private String rootDir;
    private Conf conf;
    private DBHandle db;
    private Index index;
    private CheckpointInfo cpInfo;
    private BlockfileWriter currentFileWriter;
    private Ledger.BlockchainInfo bcInfo;

    BlockfileMgr newBlockfileMgr(String id, Conf conf, IndexConfig indexConfig, DBHandle dbHandle) {
        return null;
    }

    void syncCpInfoFromFS(String rootDir, CheckpointInfo cpInfo) {
        return;
    }

    String deriveBlockfilePath(String rootDir, Integer suffixNum) {
        return null;
    }

    void close() {

    }

    void moveToNextFile() {

    }

    void addBlock(Common.Block block) {

    }

    void syncIndex() {

    }

    Ledger.BlockchainInfo getBlockchainInfo() {
        return null;
    }

    void updateCheckpoint(CheckpointInfo cpInfo) {

    }

    void updateBlockchainInfo(byte[] latestBlockHash, Common.Block block) {

    }

    Common.Block retrieveBlockByHash(byte[] blockHash) {
        return null;
    }

    Common.Block retrieveBlockByNumber(Long blockNum) {
        return null;
    }

    Common.Block retrieveBlockByTxID(String txID) {
        return null;
    }

    TransactionPackage.TxValidationCode retrieveTxValidationCodeByTxID(String txID) {
        return null;
    }

    Common.BlockHeader retrieveBlockHeaderByNumber(String blockNum) {
        return null;
    }

    BlocksItr retrieveBlocks(Long startNum) {
        return null;
    }

    Common.Envelope retrieveTransactionByID(String txID) {
        return null;
    }

    Common.Envelope retrieveTransactionByBlockNumTranNum(Long blockNum, Long tranNum) {
        return null;
    }

    Common.Block fetchBlock(FileLocPointer lp ) {
        return null;
    }

    Common.Envelope fetchTransactionEnvelope(FileLocPointer lp) {
        return null;
    }

    byte[] fetchBlockBytes(FileLocPointer lp) {
        return null;
    }

    byte[] fetchRawBytes(FileLocPointer lp) {
        return null;
    }

    /** Get the current checkpoint information that is stored in the database
     *
     * @return
     */
    CheckpointInfo loadCurrentInfo() {
        return null;
    }

    void saveCurrentInfo(CheckpointInfo checkpointInfo, Boolean sync) {
        return;
    }

    /** scanForLastCompleteBlock scan a given block file and detects the last offset in the file
     * after which there may lie a block partially written (towards the end of the file in a crash scenario).
     */
    void scanForLastCompleteBlock(String rootDir, Integer fileNum, Long startingOffset) {
        return;
    }

    public static String getBlockfilePrefix() {
        return BLOCKFILE_PREFIX;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public Conf getConf() {
        return conf;
    }

    public void setConf(Conf conf) {
        this.conf = conf;
    }

    public DBHandle getDb() {
        return db;
    }

    public void setDb(DBHandle db) {
        this.db = db;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public CheckpointInfo getCpInfo() {
        return cpInfo;
    }

    public void setCpInfo(CheckpointInfo cpInfo) {
        this.cpInfo = cpInfo;
    }

    public BlockfileWriter getCurrentFileWriter() {
        return currentFileWriter;
    }

    public void setCurrentFileWriter(BlockfileWriter currentFileWriter) {
        this.currentFileWriter = currentFileWriter;
    }

    public Ledger.BlockchainInfo getBcInfo() {
        return bcInfo;
    }

    public void setBcInfo(Ledger.BlockchainInfo bcInfo) {
        this.bcInfo = bcInfo;
    }
}
