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
package org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valimpl;

import com.google.protobuf.ByteString;
import com.google.rpc.Help;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.TxPvtData;
import org.bcia.javachain.core.ledger.TxSimulationResults;
import org.bcia.javachain.core.ledger.customtx.CustomTx;
import org.bcia.javachain.core.ledger.customtx.IProcessor;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.privacyenabledstate.PvtUpdateBatch;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.rwsetutil.*;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.txmgr.TxManager;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valinternal.Block;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.validator.valinternal.Transaction;
import org.bcia.javachain.core.ledger.kvledger.txmgmt.version.Height;
import org.bcia.javachain.core.ledger.util.TxValidationFlags;
import org.bcia.javachain.core.ledger.util.Util;
import org.bcia.javachain.csp.gm.sm3.SM3;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.ledger.rwset.Rwset;
import org.bcia.javachain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.TransactionPackage;

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
        PvtUpdateBatch pvtUpdates = PvtUpdateBatch.newPvtUpdateBatch();
        for(Transaction tx : block.getTxs()){
            if(!TransactionPackage.TxValidationCode.VALID.equals(tx.getValidationCode())){
                continue;
            }
            if(!tx.containsPvtWrites()){
                continue;
            }
            TxPvtData txPvData = pvtData.get(tx.getIndexInBlock());
            if(txPvData == null){
                continue;
            }
            if(requiresPvtdataValidation(txPvData)){
                validatePvtdata(tx, txPvData);
            }
            TxPvtRwSet pvtRwSet = RwSetUtil.txPvtRwSetFromProtoMsg(txPvData.getWriteSet());
            addPvtRWSetToPvtUpdateBatch(pvtRwSet, pvtUpdates, Height.newHeight(block.getNum(), tx.getIndexInBlock()));
        }
        return pvtUpdates;
    }

    public static boolean requiresPvtdataValidation(TxPvtData tx){
        return true;
    }

    public static void validatePvtdata(Transaction tx, TxPvtData pvtData)throws LedgerException{
        if(pvtData.getWriteSet() == null){
            return;
        }
        for(Rwset.NsPvtReadWriteSet nsPvtRwSet : pvtData.getWriteSet().getNsPvtRwsetList()){
            for(Rwset.CollectionPvtReadWriteSet collPvtdata : nsPvtRwSet.getCollectionPvtRwsetList()){
                ByteString collPvtdataHash = ByteString.copyFrom(new SM3().hash(collPvtdata.getRwset().toByteArray()));
                ByteString hashInPubdata = tx.retrieveHash(nsPvtRwSet.getNamespace(), collPvtdata.getCollectionName());
                if(!collPvtdataHash.equals(hashInPubdata)){
                    throw new LedgerException("Hash of pvt data mismatch corresponding hash in pub data");
                }
            }
        }
    }

    public static Block preprocessProtoBlock(TxManager txMgr, Common.Block block, boolean doMVCCValidation) throws LedgerException {
        Block b = new Block();
        b.setNum(block.getHeader().getNumber());
        TxValidationFlags txsFilter = TxValidationFlags.fromByteString(block.getMetadata().getMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber()));
        if(txsFilter.length() == 0){
            txsFilter = new TxValidationFlags(block.getData().getDataList().size());
            block.getMetadata().toBuilder().setMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber(), txsFilter.toByteString());
        }
        int i = 0;
        for (; i < block.getData().getDataList().size(); i++) {
            byte[] envBytes = block.getData().getDataList().get(i).toByteArray();
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
            if(txsFilter.isInValid(i)){
                logger.debug(String.format("Gtoup [%s]: Block [%d] Transaction index [%d] TxID [%d]" +
                        " marked as invalid by committer. Reason code [%s]", gh.getGroupId(),
                        block.getHeader().getNumber(),
                        i,
                        gh.getTxId(),
                        txsFilter.flag(i)));
                continue;
            }
            TxRwSet txRwSet = null;
            Common.HeaderType txType = Common.HeaderType.forNumber(gh.getType());
            logger.debug("txType " + txType);
            if(Common.HeaderType.ENDORSER_TRANSACTION.equals(txType)){
                ProposalPackage.SmartContractAction respPayload = null;
                try {
                    respPayload = Util.getActionFromEnvelope(ByteString.copyFrom(envBytes));
                } catch (Exception e) {
                    txsFilter.setFlag(i, TransactionPackage.TxValidationCode.NIL_TXACTION);
                    continue;
                }
                txRwSet = new TxRwSet();
                try {
                    txRwSet.fromProtoBytes(respPayload.toByteString());
                } catch (Exception e) {
                    txsFilter.setFlag(i, TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    continue;
                }
            } else {
                Rwset.TxReadWriteSet rwSetProto = null;
                try {
                    rwSetProto = processNonEndorserTx(env, gh.getTxId(), txType, txMgr, !doMVCCValidation);
                } catch (Exception e) {
                    //todo catch invalidException
                    txsFilter.setFlag(i, TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
                    continue;
                }
                if(rwSetProto != null){
                    txRwSet = RwSetUtil.txRwSetFromProtoMsg(rwSetProto);
                }
            }
            if(txRwSet != null){
                Transaction tx = new Transaction();
                tx.setIndexInBlock(i);
                tx.setId(gh.getTxId());
                tx.setRwSet(txRwSet);
                b.getTxs().add(tx);
            }
        }
        return b;
    }

    public static Rwset.TxReadWriteSet processNonEndorserTx(Common.Envelope txEnv, String txID, Common.HeaderType txType, TxManager txMgr, boolean synchingState) throws LedgerException {
        logger.debug(String.format("Performing custom processing for transaction [txid=%s], [txtype=%s]", txID, txType));
        IProcessor processor = CustomTx.getProcessor(txType);
        logger.debug("Processor for custom tx processing " + processor);
        if(processor == null){
            return null;
        }
        ITxSimulator sim = null;
        TxSimulationResults simRes = null;
        sim = txMgr.newTxSimulator(txID);
        try {
            processor.generateSimulationResults(txEnv, sim, synchingState);
            simRes = sim.getTxSimulationResults();
        } catch (LedgerException e) {
            sim.done();
        }
        return simRes.getPublicReadWriteSet();
    }

    public static void postprocessProtoBlock(Common.Block block, Block validatedBlock) throws LedgerException {
        TxValidationFlags txsFilter = TxValidationFlags.fromByteString(block.getMetadata().getMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber()));
        for(Transaction tx : validatedBlock.getTxs()){
            txsFilter.setFlag(tx.getIndexInBlock(), tx.getValidationCode());
        }
        block.getMetadata().toBuilder().setMetadata(Common.BlockMetadataIndex.TRANSACTIONS_FILTER.getNumber(), txsFilter.toByteString());
    }

    public static void addPvtRWSetToPvtUpdateBatch(TxPvtRwSet pvtRwSet, PvtUpdateBatch pvtUpdateBatch, Height ver) throws LedgerException {
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
