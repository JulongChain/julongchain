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
package org.bcia.julongchain.core.ledger.kvledger.history.historydb;

import com.google.protobuf.Timestamp;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.ledger.IResultsIterator;
import org.bcia.julongchain.common.ledger.blkstorage.IBlockStore;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.NsRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.TxRwSet;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.statedb.QueryResult;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.queryresult.KvQueryResult;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.iq80.leveldb.DBIterator;

import java.io.IOException;
import java.util.Map;

/**
 * 查询HistoryDB result
 *
 * @author sunzongyu
 * @date 2018/04/04
 * @company Dingxuan
 */
public class HistoryScanner implements IResultsIterator {
    private static final JavaChainLog  logger = JavaChainLogFactory.getLog(HistoryScanner.class);

    /**
     * HistoryDB key头部 包含namespace, key
     */
    private byte[] compositePartialKey;
    private String nameSpace;
    private String key;
    private DBIterator dbItr;
    private IBlockStore blockStore;
    private long blockNum;
    private long tranNum;
    private String ledgerID;

    public HistoryScanner(byte[] compositePartialKey,
                          String nameSpace,
                          String key,
                          DBIterator dbItr,
                          IBlockStore blockStore,
                          String ledgerID){
        this.compositePartialKey = compositePartialKey;
        this.nameSpace = nameSpace;
        this.key = key;
        this.dbItr = dbItr;
        this.blockStore = blockStore;
        this.ledgerID = ledgerID;
    }

    @Override
    public QueryResult next() throws LedgerException {
        if(!dbItr.hasNext()){
            return null;
        }
        Map.Entry<byte[], byte[]> entry = dbItr.next();
        byte[] historyKey = HistoryDBHelper.removeLedgerIDFromHistoryKey(ledgerID, entry.getKey());

	    //key:ns~key~blockNo~tranNo
        blockNum = HistoryDBHelper.splitCompositeHistoryKeyForBlockNum(historyKey);
        tranNum = HistoryDBHelper.splitCompositeHistoryKeyForTranNum(historyKey);
        logger.debug(String.format("Found history record for namespace: %s, key: %s. BlockNum: %d, TranNum: %d", nameSpace, key, blockNum, tranNum));

        Common.Envelope tranEnvelope = blockStore.retrieveTxByBlockNumTranNum(blockNum, tranNum);
//        QueryResult queryResult = getKeyModificationFromTran(tranEnvelope, nameSpace, key);
        QueryResult queryResult = getKvVersion(blockNum, tranNum);
//        logger.debug("Found history key value for namespace=[{}], key=[{}] from transaction=[{}]", nameSpace, key, ((KvQueryResult.KeyModification) queryResult.getObj()).getTxId());

        return queryResult;
    }

    @Override
    public void close() throws LedgerException {
	    try {
		    dbItr.close();
	    } catch (IOException e) {
	    	logger.error("Got error when close dbItr");
	    	throw new RuntimeException(e);
	    }
    }

    private QueryResult getKvVersion(long blockNum, long tranNum){
	    KvRwset.Version version = KvRwset.Version.newBuilder()
			    .setBlockNum(blockNum)
			    .setTxNum(tranNum)
			    .build();
	    return new QueryResult(version);
    }

    private QueryResult getKeyModificationFromTran(Common.Envelope envelope, String ns, String key) throws LedgerException {
        try {
            logger.debug("Entering getKeyModificationFromTran() with namespace=[{}], key=[{}]", ns, key);
            Common.Payload payload = ProtoUtils.getPayload(envelope);
            TransactionPackage.Transaction tx = ProtoUtils.getTransaction(payload.getData());
            ProposalPackage.SmartContractAction respPayload = ProtoUtils.getSCAction(tx.getActions(0));
            Common.GroupHeader chdr = ProtoUtils.unMarshalGroupHeader(payload.getHeader().getGroupHeader());
            String txID = chdr.getTxId();
            Timestamp timestamp = chdr.getTimestamp();

            TxRwSet txRwSet = new TxRwSet();
            if (respPayload != null) {
                txRwSet.fromProtoBytes(respPayload.getResults());
            }
            for (NsRwSet nsRwSet : txRwSet.getNsRwSets()) {
                if(nameSpace.equals(nsRwSet.getNameSpace())){
                    for (KvRwset.KVWrite kvWrite : nsRwSet.getKvRwSet().getWritesList()) {
                        if (key.equals(kvWrite.getKey())) {
                            return new QueryResult(KvQueryResult.KeyModification.newBuilder()
                                    .setTxId(txID)
                                    .setValue(kvWrite.getValue())
                                    .setTimestamp(timestamp)
                                    .setIsDelete(kvWrite.getIsDelete())
                                    .build());
                        }
                    }
                    throw new LedgerException("Key not found in namespace's writeSet");
                }
            }
            throw new LedgerException("Namespace not found in transaction's RWSets");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new LedgerException(e);
        }
    }

    public byte[] getCompositePartialKey() {
        return compositePartialKey;
    }

    public void setCompositePartialKey(byte[] compositePartialKey) {
        this.compositePartialKey = compositePartialKey;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DBIterator getDbItr() {
        return dbItr;
    }

    public void setDbItr(DBIterator dbItr) {
        this.dbItr = dbItr;
    }

    public IBlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(IBlockStore blockStore) {
        this.blockStore = blockStore;
    }

    public long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(long blockNum) {
        this.blockNum = blockNum;
    }

    public long getTranNum() {
        return tranNum;
    }

    public void setTranNum(long tranNum) {
        this.tranNum = tranNum;
    }

	public String getLedgerID() {
		return ledgerID;
	}

	public void setLedgerID(String ledgerID) {
		this.ledgerID = ledgerID;
	}
}
