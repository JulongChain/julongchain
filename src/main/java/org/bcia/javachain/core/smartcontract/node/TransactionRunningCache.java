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
package org.bcia.javachain.core.smartcontract.node;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/25
 * @company Dingxuan
 */
public class TransactionRunningCache {

    private static final JavaChainLog logger = JavaChainLogFactory.getLog(TransactionRunningCache.class);

    private static final String TX_STATUS_START = "start";
    private static final String TX_STATUS_COMPLETE = "complete";
    private static final String TX_STATUS_ERROR = "error";

    /** 保存交易对应的智能合约编号*/
    private static Map<String, String> txIdAndSmartContractIdMap = Collections.synchronizedMap(new HashMap<String, String>());

    /** 保存交易的状态*/
    private static Map<String, String> txIdAndStatusMap = Collections.synchronizedMap(new HashMap<String, String>());

    /**
     * 添加txId对应的smartContractId
     * @param txId 交易号
     * @param smartContractId 智能合约编号
     */
    public static void addTxId(String txId, String smartContractId) {
        txIdAndSmartContractIdMap.put(txId, smartContractId);
    }

    /**
     * 根据交易号获取智能合约编号
     * @param txId 交易号
     * @return
     */
    public static String getSmartContractIdByTxId(String txId) {
        return txIdAndSmartContractIdMap.get(txId);
    }

    /**
     * 更新交易运行状态
     * @param txId 交易号
     * @param status 交易运行的状态
     */
    public static void updateTxStatus(String txId, String status) {
        txIdAndStatusMap.put(txId, status);
    }

    /**
     * 获取交易运行状态
     * @param txId
     */
    public static String getTxStatusById(String txId) {
        return txIdAndStatusMap.get(txId);
    }

}
