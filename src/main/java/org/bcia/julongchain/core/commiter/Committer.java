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
package org.bcia.julongchain.core.commiter;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.CommitterException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.commiter.util.CommitterUtils;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.TxPvtData;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 确认服务实现
 *
 * @author wanglei zhouhui
 * @date 18/3/27
 * @company Dingxuan
 */
public class Committer implements ICommitter {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Committer.class);

    public interface IConfigBlockEventer {
        void event(Common.Block block) throws CommitterException;
    }

    private INodeLedger nodeLedger;
    private IConfigBlockEventer eventer;

    public Committer(INodeLedger nodeLedger) {
        this.nodeLedger = nodeLedger;
        this.eventer = new IConfigBlockEventer() {
            @Override
            public void event(Common.Block block) throws CommitterException {

            }
        };
    }

    public Committer(INodeLedger nodeLedger, IConfigBlockEventer eventer) {
        this.nodeLedger = nodeLedger;
        this.eventer = eventer;
    }

    /**
     * 预提交区块
     *
     * @param block
     * @throws CommitterException
     * @throws InvalidProtocolBufferException
     * @throws ValidateException
     */
    private void preCommit(Common.Block block) throws CommitterException, InvalidProtocolBufferException,
            ValidateException {
        if (CommitterUtils.isConfigBlock(block)) {
            log.info("get a config block");

            if (eventer != null) {
                eventer.event(block);
            }
        }
    }

    @Override
    public void commitWithPrivateData(BlockAndPvtData blockAndPvtData) throws CommitterException {
        log.info("commitWithPrivateData");
        try {
            //提交前处理
            preCommit(blockAndPvtData.getBlock());

            //正式提交账本
            nodeLedger.commitWithPvtData(blockAndPvtData);

            //提交后处理
            postCommit(blockAndPvtData.getBlock());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new CommitterException(e);
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new CommitterException(e);
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw new CommitterException(e);
        }
    }

    private void postCommit(Common.Block block) throws CommitterException {
        //TODO：创建区块事件
    }

    @Override
    public BlockAndPvtData getPrivateDataAndBlockByNum(long seqNumber) throws CommitterException {
        return null;
    }

    @Override
    public TxPvtData[] getPrivateDataByNum(long blockNumber, Map<String, Map<String, Boolean>> filter) throws CommitterException {
        return new TxPvtData[0];
    }

    @Override
    public long ledgerHeight() throws CommitterException {
        try {
            Ledger.BlockchainInfo blockchainInfo = nodeLedger.getBlockchainInfo();
            return blockchainInfo.getHeight();
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw new CommitterException(e);
        }
    }

    @Override
    public List<Common.Block> getBlocks(long[] blockSeqs) {
        List<Common.Block> blockList = new ArrayList<Common.Block>();

        for (long seqNum : blockSeqs) {
            Common.Block block = null;
            try {
                block = nodeLedger.getBlockByNumber(seqNum);
            } catch (LedgerException e) {
                log.error(e.getMessage(), e);
            }

            if (block != null) {
                blockList.add(block);
            }
        }

        return blockList;
    }

    @Override
    public void close() {
        nodeLedger.close();
    }

}
