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
import org.bcia.javachain.core.ledger.TxPvtData;
import org.bcia.javachain.protos.common.Common;
import java.util.Map;

/**
 * 确认服务接口
 *
 * @author wanglei
 * @date 18-3-27
 * @company Dingxuan
 */
public class CommitterServer implements ICommiterServer{
    private static JavaChainLog log = JavaChainLogFactory.getLog(CommitterServer.class);

    @Override
    public void CommitWithPvtData(BlockAndPvtData blockAndPvtData) throws Exception {
        log.info("Call CommitterServer CommitWithPvtData !");
    }

    @Override
    public BlockAndPvtData GetPvtDataAndBlockByNum(Integer seqNumber) throws Exception {
        log.info("Call CommitterServer GetPvtDataAndBlockByNum return BlockAndPvtData=null!");

        return  null;
    }

    @Override
    public TxPvtData GetPvtDataByNum(Integer blockNumber, Map<String, Map<String, Boolean>> filter) throws Exception {
        log.info("Call CommitterServer GetPvtDataByNum return TxPvtData=null!");

        return null;
    }

    @Override
    public Integer LedgerHeight() throws Exception {
        log.info("Call CommitterServer LedgerHeight return Integer=0!");

        return 0;
    }

    @Override
    public Common.Block GetBlocks(Integer[] blockSeqs) throws Exception {
        log.info("Call CommitterServer GetBlocks return Common.Block=null!");

        return null;
    }

    @Override
    public void Close() {

    }
}
