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
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.core.commiter.util.CommitterUtils;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;

import java.util.ArrayList;
import java.util.List;

/**
 * 提交者实现
 *
 * @author wanglei zhouhui
 * @date 2018/03/27
 * @company Dingxuan
 */
public class Committer implements ICommitter {
    private static JulongChainLog log = JulongChainLogFactory.getLog(Committer.class);

    /**
     * 配置区块更新的监听
     */
    public interface IConfigBlockListener {
        /**
         * 配置区块更改事件
         *
         * @param configBlock
         * @throws CommitterException
         */
        void onConfigBlockChanged(Common.Block configBlock) throws CommitterException;
    }

    /**
     * 节点账本
     */
    private INodeLedger nodeLedger;
    /**
     * 配置区块更新监听
     */
    private IConfigBlockListener configBlockListener;

    /**
     * 构造函数，无配置区块更新监听
     *
     * @param nodeLedger
     */
    public Committer(INodeLedger nodeLedger) {
        this(nodeLedger, null);
    }

    /**
     * 构造函数
     *
     * @param nodeLedger
     * @param configBlockListener
     */
    public Committer(INodeLedger nodeLedger, IConfigBlockListener configBlockListener) {
        this.nodeLedger = nodeLedger;
        this.configBlockListener = configBlockListener;
    }

    /**
     * 提交区块之前操作
     *
     * @param block
     * @throws CommitterException
     * @throws InvalidProtocolBufferException
     * @throws ValidateException
     */
    private void preCommit(Common.Block block) throws CommitterException, InvalidProtocolBufferException,
            ValidateException {
        if (CommitterUtils.isConfigBlock(block)) {
            log.info("Get a config block");

            if (configBlockListener != null) {
                configBlockListener.onConfigBlockChanged(block);
            }
        }
    }

    @Override
    public void commitWithPrivateData(BlockAndPvtData blockAndPvtData) throws CommitterException, ValidateException {
        log.info("commitWithPrivateData");
        ValidateUtils.isNotNull(blockAndPvtData, "BlockAndPvtData can not be null");
        ValidateUtils.isNotNull(blockAndPvtData.getBlock(), "BlockAndPvtData.getBlock can not be null");

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
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw new CommitterException(e);
        }
    }

    /**
     * 提交区块之后操作
     *
     * @param block
     * @throws CommitterException
     */
    private void postCommit(Common.Block block) throws CommitterException {
        //TODO：暂无要处理的事件
    }

    @Override
    public BlockAndPvtData getPrivateDataAndBlockByNum(long seqNumber) throws CommitterException {
        try {
            return nodeLedger.getPvtDataAndBlockByNum(seqNumber, null);
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
            throw new CommitterException(e);
        }
    }

    @Override
    public long getLedgerHeight() throws CommitterException {
        try {
            Ledger.BlockchainInfo blockchainInfo = nodeLedger.getBlockchainInfo();
            return blockchainInfo.getHeight();
        } catch (Exception e) {
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
