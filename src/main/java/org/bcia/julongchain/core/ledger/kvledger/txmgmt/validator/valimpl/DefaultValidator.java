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

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.IDB;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.PvtUpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.privacyenabledstate.UpdateBatch;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.txmgr.ITxManager;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.IValidator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.statebasedval.Validator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valinternal.Block;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valinternal.InternalValidator;
import org.bcia.julongchain.core.ledger.kvledger.txmgmt.validator.valinternal.PubAndHashUpdates;
import org.bcia.julongchain.protos.common.Common;

/**
 * 默认的验证器
 *
 * @author sunzongyu
 * @date 2018/04/20
 * @company Dingxuan
 */
public class DefaultValidator implements IValidator {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(DefaultValidator.class);

    private ITxManager txMgr;
    private InternalValidator val;

    public DefaultValidator(ITxManager txMgr, IDB db) {
        this.txMgr = txMgr;
        this.val = new Validator(db);
    }

    @Override
    public UpdateBatch validateAndPrepareBatch(BlockAndPvtData blockAndPvtData, boolean doMVCCValidation) throws LedgerException {
        Common.Block block = blockAndPvtData.getBlock();
        Common.Block.Builder builder = block.toBuilder();
        logger.debug("validateAndPrepareBatch() for block No." + block.getHeader().getNumber());
        logger.debug("preprocessing block");
        Block internalBlock = Helper.preprocessProtoBlock(txMgr, builder, doMVCCValidation);
        PubAndHashUpdates pubAndHashUpdates = val.validateAndPrepareBatch(internalBlock, doMVCCValidation);
        logger.debug("validating rwset...");
        PvtUpdateBatch pvtUpdates = Helper.validateAndPreparePvtBatch(internalBlock, blockAndPvtData.getBlockPvtData());
        logger.debug("postprocessing Proto block");
        Helper.postprocessProtoBlock(builder, internalBlock);
        logger.debug("validateAndPrepareBatch complete");

        blockAndPvtData.setBlock(builder.build());

        return new UpdateBatch(pubAndHashUpdates.getPubUpdates(), pubAndHashUpdates.getHashedUpdates(), pvtUpdates);
    }

    public ITxManager getTxMgr() {
        return txMgr;
    }

    public void setTxMgr(ITxManager txMgr) {
        this.txMgr = txMgr;
    }

    public InternalValidator getVal() {
        return val;
    }

    public void setVal(InternalValidator val) {
        this.val = val;
    }
}
