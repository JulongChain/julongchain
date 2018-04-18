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
package org.bcia.javachain.core.commiter;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ledger.BlockAndPvtData;
import org.bcia.javachain.core.ledger.ILedger;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.TxPvtData;
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.core.commiter.util.*;
import org.bcia.javachain.protos.common.Ledger;

import java.util.Map;

/**
 * 确认服务实现
 *
 * @author wanglei
 * @date 18/3/27
 * @company Dingxuan
 */
public class CommitterServer implements ICommiterServer{
    private static JavaChainLog log = JavaChainLogFactory.getLog(CommitterServer.class);

    private INodeLedger nodeLedger;

    private void preCommit(Common.Block block) throws  Exception{
        Boolean resultBool = Utils.IsConfigBlock(block);
        if(resultBool){
            // TODO:

        }
    }
    public void postCommit(Common.Block block) throws Exception {
    }

    @Override
    public void CommitWithPvtData(BlockAndPvtData blockAndPvtData) throws Exception {
        log.info("Call CommitterServer CommitWithPvtData !");

        preCommit( blockAndPvtData.getBlock() );

        //Committing block
        //TODO: wait liangbing complete CommitWithPvtData
        //nodeLedger.CommitWithPvtData( blockAndPvtData );

        //TODO:
        postCommit(blockAndPvtData.getBlock());
    }

    @Override
    public BlockAndPvtData GetPvtDataAndBlockByNum(long seqNumber) throws Exception {
        log.info("Call CommitterServer GetPvtDataAndBlockByNum return BlockAndPvtData=null!");

        BlockAndPvtData blockAndPvtData = null;
        //TODO: wait langbing  complete 'getPvtDataAndBlockByNum'
        //blockAndPvtData = nodeLedger.getPvtDataAndBlockByNum(seqNumber, null);

        return  blockAndPvtData;
    }

    @Override
    public TxPvtData GetPvtDataByNum(Integer blockNumber, Map<String, Map<String, Boolean>> filter) throws Exception {
        log.info("Call CommitterServer GetPvtDataByNum return TxPvtData=null!");

        return null;
    }

    @Override
    public long LedgerHeight() throws Exception {
        log.info("Call CommitterServer LedgerHeight return Integer=0!");
        Ledger.BlockchainInfo  blockchainInfo = null;
        //TODO: wait liangbing
        //blockchainInfo = Ledger.getBlockchainInfo();

        return blockchainInfo.getHeight();
    }

    @Override
    public Common.Block[] GetBlocks(long[] blockSeqs) throws Exception {
        log.info("Call CommitterServer GetBlocks return Common.Block=null!");
        Common.Block blocks[] = null;
        int bloksSeqsNum = blockSeqs.length;
        for (int index = 0; index < bloksSeqsNum; index++){
            Common.Block tempBlock = null;
            //TODO: wait liangbing
            //tempBlock = Ledger.GetBlockNumber( blockSeqs[index] );
            blocks[index] = tempBlock;
        }

        return blocks;
    }

    @Override
    public void Close() {

    }
}
