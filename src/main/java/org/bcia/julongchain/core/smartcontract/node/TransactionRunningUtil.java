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
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.ledger.rwset.kvrwset.KvRwset;
import org.bcia.julongchain.protos.node.SmartContractShim;

import java.util.*;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/4/25
 * @company Dingxuan
 */
public class TransactionRunningUtil {

  private static final JavaChainLog logger =
      JavaChainLogFactory.getLog(TransactionRunningUtil.class);

  public static final String TX_STATUS_START = "start";
  public static final String TX_STATUS_COMPLETE = "complete";
  public static final String TX_STATUS_ERROR = "error";

  /** 保存交易对应的智能合约编号 */
  private static Map<String, String> txIdAndSmartContractIdMap =
      Collections.synchronizedMap(new HashMap<String, String>());

  /** 保存交易的状态 */
  private static Map<String, String> txIdAndStatusMap =
      Collections.synchronizedMap(new HashMap<String, String>());

  public static Map<String, List<KvRwset.KVRead>> getTxIdAndKvReadMap() {
    return txIdAndKvReadMap;
  }

  private static Map<String, List<KvRwset.KVRead>> txIdAndKvReadMap =
      Collections.synchronizedMap(new HashMap<String, List<KvRwset.KVRead>>());

  private static Map<String, SmartContractShim.SmartContractMessage> txIdAndMessageMap =
      Collections.synchronizedMap(new HashMap<String, SmartContractShim.SmartContractMessage>());

  private static Map<String, List<KvRwset.KVWrite>> txIdAndKvWriteMap =
      Collections.synchronizedMap(new HashMap<String, List<KvRwset.KVWrite>>());

  public static void addTxMessage(
      String smartContractId,
      String txId,
      SmartContractShim.SmartContractMessage smartContractMessage) {
    txIdAndMessageMap.put(composite(smartContractId, txId), smartContractMessage);
  }

  public static boolean checkTxId(String txId) {
    return txIdAndSmartContractIdMap.containsKey(txId);
  }

  public static SmartContractShim.SmartContractMessage getTxMessage(
      String smartContractId, String txId) {
    return txIdAndMessageMap.get(composite(smartContractId, txId));
  }

  public static String composite(String smartContractId, String txId) {
    ArrayList<String> strings = Lists.newArrayList(smartContractId, txId);
    String composite = StringUtils.join(strings, "||");
    return composite;
  }

  public static void addKvRead(String smartContractId, String txId, KvRwset.KVRead kvRead) {
    String composite = composite(smartContractId, txId);
    if (txIdAndKvReadMap.get(composite) == null) {
      List<KvRwset.KVRead> list = new ArrayList<KvRwset.KVRead>();
      list.add(kvRead);
      txIdAndKvReadMap.put(composite, list);
    } else {
      txIdAndKvReadMap.get(composite).add(kvRead);
    }
  }

  public static List<KvRwset.KVRead> getKvReads(String txId) {
    String scId = txIdAndSmartContractIdMap.get(txId);
    return txIdAndKvReadMap.get(composite(scId, txId));
  }

  public static void addKvWrite(String smartContractId, String txId, KvRwset.KVWrite kvWrite) {
    String composite = composite(smartContractId, txId);
    if (txIdAndKvWriteMap.get(composite) == null) {
      List<KvRwset.KVWrite> list = new ArrayList<KvRwset.KVWrite>();
      list.add(kvWrite);
      txIdAndKvWriteMap.put(composite, list);
    } else {
      txIdAndKvWriteMap.get(composite).add(kvWrite);
    }
  }

  public static List<KvRwset.KVWrite> getKvWrites(String txId) {
    String scId = txIdAndSmartContractIdMap.get(txId);
    return txIdAndKvWriteMap.get(composite(scId, txId));
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
