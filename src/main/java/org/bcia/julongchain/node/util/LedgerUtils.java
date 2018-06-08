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
package org.bcia.julongchain.node.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Ledger;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/26
 * @company Dingxuan
 */
public class LedgerUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(LedgerUtils.class);

    /**
     * 获取当前账本的最新配置区块
     *
     * @param nodeLedger
     * @return
     * @throws LedgerException
     * @throws ValidateException
     * @throws InvalidProtocolBufferException
     */
    public static Common.Block getConfigBlockFromLedger(INodeLedger nodeLedger) throws LedgerException,
            ValidateException, InvalidProtocolBufferException {
        //从账本中获取当前链的信息，如高度等
        Ledger.BlockchainInfo blockchainInfo = nodeLedger.getBlockchainInfo();
        ValidateUtils.isNotNull(blockchainInfo, "blockchainInfo can not be null");

        //从账本中获取最新的区块
        Common.Block lastBlock = nodeLedger.getBlockByNumber(blockchainInfo.getHeight() - 1);

        //通过最新的区块解析出最新配置区块的索引
        long configBlockIndex = BlockUtils.getLastConfigIndexFromBlock(lastBlock);
        //获取最新配置区块
        Common.Block lastConfigBlock = nodeLedger.getBlockByNumber(configBlockIndex);

        return lastConfigBlock;
    }
}
