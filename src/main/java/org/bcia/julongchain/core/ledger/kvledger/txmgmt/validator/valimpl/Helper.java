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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valimpl;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.exception.InvalidTxException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.core.ledger.ITxSimulator;
import org.bcia.julongchain.core.ledger.TxPvtData;
import org.bcia.julongchain.core.ledger.TxSimulationResults;
import org.bcia.julongchain.core.ledger.customtx.CustomTx;
import org.bcia.julongchain.core.ledger.customtx.IProcessor;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.PvtUpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil.*;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.txmgr.ITxManager;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valinternal.Block;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valinternal.Transaction;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.version.LedgerHeight;
import org.bcia.julongchain.core.ledger.util.TxValidationFlags;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.ledger.rwset.Rwset;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.util.Map;

/**
 * 辅助验证器
 *
 * @author sunzongyu
 * @date 2018/04/20
 * @company Dingxuan
 */
public class Helper {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(Helper.class);

    public static PvtUpdateBatch validateAndPreparePvtBatch(Block block, Map<Long, TxPvtData> pvtData) throws LedgerException{
        PvtUpdateBatch pvtUpdates = new PvtUpdateBatch();
        for(Transaction tx : block.getTxs()){
            if(!TransactionPackage.TxValidationCode.VALID.equals(tx.getValidationCode())){
                continue;
            }
            if(!tx.containsPvtWrites()){
                continue;
            }
            TxPvtData txPvData = pvtData.get((long) tx.getIndexInBlock());
            if(txPvData == null){
                continue;
            }
            if(requiresPvtdataValidation(txPvData)){
                validatePvtdata(tx, txPvData);
            }
            TxPvtRwSet pvtRwSet = RwSetUtil.txPvtRwSetFromProtoMsg(txPvData.getWriteSet());
            addPvtRWSetToPvtUpdateBatch(pvtRwSet, pvtUpdates, new LedgerHeight(block.getNum(), tx.getIndexInBlock()));
        }
        return pvtUpdates;
    }

    private static boolean requiresPvtdataValidation(TxPvtData tx){
        return true;
    }

    private static void validatePvtdata(Transaction tx, TxPvtData pvtData)throws LedgerException{
        if(pvtData.getWriteSet() == null){
            return;
        }
        for(Rwset.NsPvtReadWriteSet nsPvtRwSet : pvtData.getWriteSet().getNsPvtRwsetList()){
            for(Rwset.CollectionPvtReadWriteSet collPvtdata : nsPvtRwSet.getCollectionPvtRwsetList()){
                //TODO SM3 hash
                ByteString collPvtdataHash = ByteString.copyFrom(Util.getHashBytes(collPvtdata.getRwset().toByteArray()));
                ByteString hashInPubdata = tx.retrieveHash(nsPvtRwSet.getNamespace(), collPvtdata.getCollectionName());
                if(!collPvtdataHash.equals(hashInPubdata)){
                    throw new LedgerException("Hash of pvt data mismatch corresponding hash in pub data");
                }
            }
        }
    }

    public static Block preprocessProtoBlock(ITxManager txMgr, Common.Block.Builder blockBuilder, boolean doMVCCValidation) throws LedgerException {
        Common.Block block = blockBuilder.build();
        Block b = new Block(block.getHeader().getNumber());
        TxValidationFlags txsFilter = TxValidationFlags.fromByteString(block.getMetadata().getMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber()));
        if(txsFilter.length() == 0){
            txsFilter = new TxValidationFlags(block.getData().getDataList().size());
            Common.BlockMetadata.Builder blockMetadataBuilder = block.getMetadata().toBuilder().setMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber(), txsFilter.toByteString());
            blockBuilder.setMetadata(blockMetadataBuilder);
            block = blockBuilder.build();
        }
        int txIndex = 0;
        for (; txIndex < block.getData().getDataList().size(); txIndex++) {
            byte[] envBytes = block.getData().getDataList().get(txIndex).toByteArray();
            Common.Envelope env;
            Common.GroupHeader gh;
            Common.Payload payload;
            try {
                env = ProtoUtils.getEnvelopeFromBlock(envBytes);
                payload = ProtoUtils.getPayload(env);
                gh = ProtoUtils.unMarshalGroupHeader(payload.getHeader().getGroupHeader());
            } catch (Exception e) {
                throw new LedgerException(e);
            }
            if(txsFilter.isInValid(txIndex)){
                logger.debug(String.format("Group [%s]: Block [%d] Transaction index [%d] TxID [%s]" +
                        " marked as invalid by committer. Reason code [%s]",
                        gh.getGroupId(),
                        block.getHeader().getNumber(),
                        txIndex,
                        gh.getTxId(),
                        txsFilter.flag(txIndex)));
                continue;
            }
            TxRwSet txRwSet = null;
            Common.HeaderType txType = Common.HeaderType.forNumber(gh.getType());
            logger.debug("txType " + txType);
            if(Common.HeaderType.ENDORSER_TRANSACTION.equals(txType)){
                ProposalPackage.SmartContractAction respPayload;
                try {
                    respPayload = Util.getActionFromEnvelope(ByteString.copyFrom(envBytes));
                } catch (Exception e) {
                    txsFilter.setFlag(txIndex, TransactionPackage.TxValidationCode.NIL_TXACTION);
                    continue;
                }
                txRwSet = new TxRwSet();
                try {
	                if (respPayload != null) {
		                txRwSet.fromProtoBytes(respPayload.getResults());
	                }
                } catch (Exception e) {
                    txsFilter.setFlag(txIndex, TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    continue;
                }
            } else {
                Rwset.TxReadWriteSet rwSetProto = null;
                try {
                    rwSetProto = processNonEndorserTx(env, gh.getTxId(), txType, txMgr, !doMVCCValidation);
                } catch (InvalidTxException e) {
                    // TODO: 6/11/18 无效交易跳过
					txsFilter.setFlag(txIndex, TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
					continue;
                }
                if(rwSetProto != null){
                    txRwSet = RwSetUtil.txRwSetFromProtoMsg(rwSetProto);
                }
            }
            if(txRwSet != null){
                Transaction tx = new Transaction(txIndex, gh.getTxId(), txRwSet, null);
                tx.setIndexInBlock(txIndex);
                tx.setId(gh.getTxId());
                tx.setRwSet(txRwSet);
                b.getTxs().add(tx);
            }
        }
        return b;
    }

    private static Rwset.TxReadWriteSet processNonEndorserTx(Common.Envelope txEnv, String txID, Common.HeaderType txType, ITxManager txMgr, boolean synchingState) throws LedgerException, InvalidTxException {
        logger.debug(String.format("Performing custom processing for transaction [txid=%s], [txtype=%s]", txID, txType));
        IProcessor processor = CustomTx.getProcessor(txType);
        logger.debug("Processor for custom tx processing " + processor);
        if(processor == null){
        	return null;
        }
        ITxSimulator sim;
        TxSimulationResults simRes;
        sim = txMgr.newTxSimulator(txID);
        try {
            processor.generateSimulationResults(txEnv, sim, synchingState);
            simRes = sim.getTxSimulationResults();
        } catch (LedgerException e) {
            sim.done();
            throw e;
        }
        sim.done();
        return simRes.getPublicReadWriteSet();
    }

    public static void postprocessProtoBlock(Common.Block.Builder blockBuilder, Block validatedBlock) {
        TxValidationFlags txsFilter = TxValidationFlags.fromByteString(blockBuilder.getMetadata().getMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber()));
        for(Transaction tx : validatedBlock.getTxs()){
            txsFilter.setFlag(tx.getIndexInBlock(), tx.getValidationCode());
        }
        Common.BlockMetadata.Builder blockMetadataBuilder = blockBuilder.getMetadata().toBuilder();
        blockMetadataBuilder.setMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber(), txsFilter.toByteString());
        blockBuilder.setMetadata(blockMetadataBuilder);
    }

    private static void addPvtRWSetToPvtUpdateBatch(TxPvtRwSet pvtRwSet, PvtUpdateBatch pvtUpdateBatch, LedgerHeight ver) throws LedgerException {
        for(NsPvtRwSet ns : pvtRwSet.getNsPvtRwSets()){
            for(CollPvtRwSet coll : ns.getCollPvtRwSets()){
                for(KvRwset.KVWrite kvWrite : coll.getKvRwSet().getWritesList()){
                    if(!kvWrite.getIsDelete()){
                        pvtUpdateBatch.getMap().put(ns.getNameSpace(), coll.getCollectionName(), kvWrite.getKey(), kvWrite.getValue().toByteArray(), ver);
                    } else {
                        pvtUpdateBatch.getMap().delete(ns.getNameSpace(), coll.getCollectionName(), kvWrite.getKey(), ver);
                    }
                }
            }
        }
    }
}
