/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.smartcontract.node;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.protos.node.SmartContractShim;

import java.util.ArrayList;
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
public class TransactionRunningUtil {

  private static JulongChainLog logger =
      JulongChainLogFactory.getLog(TransactionRunningUtil.class);

  public static final String TX_STATUS_START = "start";
  public static final String TX_STATUS_COMPLETE = "complete";
  public static final String TX_STATUS_ERROR = "error";

  /** 保存交易对应的智能合约编号 */
  private static Map<String, String> txIdAndSmartContractIdMap =
      Collections.synchronizedMap(new HashMap<String, String>());

  /** 保存交易的状态 */
  private static Map<String, String> txIdAndStatusMap =
      Collections.synchronizedMap(new HashMap<String, String>());

  private static Map<String, SmartContractShim.SmartContractMessage> txIdAndMessageMap =
      Collections.synchronizedMap(new HashMap<String, SmartContractShim.SmartContractMessage>());

  public static void addTxMessage(
      String smartContractId,
      String txId,
      SmartContractShim.SmartContractMessage smartContractMessage) {
    String composite = composite(smartContractId, txId);
    txIdAndMessageMap.put(composite, smartContractMessage);
  }

  public static boolean checkTxId(String txId) {
    return txIdAndSmartContractIdMap.containsKey(txId);
  }

  public static SmartContractShim.SmartContractMessage getTxMessage(
      String smartContractId, String txId) {
    return txIdAndMessageMap.get(composite(smartContractId, txId));
  }

  public static void clearMap(String smartContractId, String txId) {
    txIdAndMessageMap.remove(composite(smartContractId, txId));
    txIdAndMessageMap.remove(composite(CommConstant.LSSC, txId));
    txIdAndMessageMap.remove(composite(CommConstant.ESSC, txId));
    txIdAndSmartContractIdMap.remove(txId);
    txIdAndStatusMap.remove(composite(smartContractId, txId));
    txIdAndStatusMap.remove(composite(CommConstant.LSSC, txId));
    txIdAndStatusMap.remove(composite(CommConstant.ESSC, txId));
  }

  public static String composite(String smartContractId, String txId) {
    ArrayList<String> strings = Lists.newArrayList(smartContractId, txId);
    String composite = StringUtils.join(strings, "||");

    return composite;
  }

  /**
   * 添加txId对应的smartContractId
   *
   * @param txId 交易号
   * @param smartContractId 智能合约编号
   */
  public static void addTxId(String txId, String smartContractId) {
    logger.debug(String.format("add txId txId[%s] smartContractId[%s]", txId, smartContractId));
    txIdAndSmartContractIdMap.put(txId, smartContractId);
  }

  /**
   * 根据交易号获取智能合约编号
   *
   * @param txId 交易号
   * @return
   */
  public static String getSmartContractIdByTxId(String txId) {
    return txIdAndSmartContractIdMap.get(txId);
  }

  /**
   * 更新交易运行状态
   *
   * @param txId 交易号
   * @param status 交易运行的状态
   */
  public static void updateTxStatus(String smartContractId, String txId, String status) {
    logger.debug(
        String.format(
            "update txStatus txId[%s] status[%s]->[%s]",
            txId, getTxStatusById(smartContractId, txId), status));
    txIdAndStatusMap.put(composite(smartContractId, txId), status);
  }

  /**
   * 获取交易运行状态
   *
   * @param txId
   */
  public static String getTxStatusById(String smartContractId, String txId) {
	  return txIdAndStatusMap.get(composite(smartContractId, txId));
  }
}
