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

import static org.bcia.julongchain.protos.node.SmartContractShim.SmartContractMessage;

import io.grpc.stub.StreamObserver;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

/**
 * 智能合约运行时状态
 *
 * @author wanliangbing
 * @date 2018/4/25
 * @company Dingxuan
 */
public class SmartContractRunningUtil {

  private static final JavaChainLog logger =
      JavaChainLogFactory.getLog(SmartContractRunningUtil.class);

  public static final String SMART_CONTRACT_STATUS_NEW = "new";
  public static final String SMART_CONTRACT_STATUS_BUSY = "busy";
  public static final String SMART_CONTRACT_STATUS_READY = "ready";
  public static final String SMART_CONTRACT_STATUS_ERROR = "error";

  public static final String SMART_CONTRACT_STATUS_SEND_REGISTERED = "send_registered";
  public static final String SMART_CONTRACT_STATUS_SEND_READY = "send_ready";
  public static final String SMART_CONTRACT_STATUS_SEND_INIT = "send_init";
  public static final String SMART_CONTRACT_STATUS_SEND_TRANSACTION = "send_transaction";

  /** 保存智能合约的状态，智能合约的状态分为 1：new 2:busy 3:ready 4:error */
  private static Map<String, String> smartContractIdAndStatusMap =
      Collections.synchronizedMap(new HashMap<String, String>());

  /** 保存智能合约的gRPC客户端 */
  private static Map<String, StreamObserver<SmartContractMessage>>
      smartContractIdAndStreamObserverMap =
          Collections.synchronizedMap(new HashMap<String, StreamObserver<SmartContractMessage>>());

  /**
   * 增加智能合约的gRPC客户端
   *
   * @param smartContractId 智能合约编号
   * @param streamObserver 智能合约gRPC客户端
   */
  public static void addStreamObserver(
      String smartContractId, StreamObserver<SmartContractMessage> streamObserver) {
    logger.debug(
        String.format("add stream observer smartContractId[%s] streamObserver[]%s"),
        smartContractId,
        streamObserver.toString());
    smartContractIdAndStreamObserverMap.put(smartContractId, streamObserver);
  }

  /**
   * 获取智能合约的gRPC客户端
   *
   * @param smartContractId 智能合约编号
   */
  public static StreamObserver<SmartContractMessage> getStreamObserver(String smartContractId) {
    return smartContractIdAndStreamObserverMap.get(smartContractId);
  }

  /**
   * 更新智能合约的状态
   *
   * @param smartContractId 智能合约编号
   * @param smartContractStatus 智能合约状态
   */
  public static void updateSmartContractStatus(String smartContractId, String smartContractStatus) {
    logger.debug(
        String.format(
            "update smartContract status smartContractId[%s] smartContractStatus[%s]->[%s]",
            smartContractId, getSmartContractStauts(smartContractId), smartContractStatus));
    smartContractIdAndStatusMap.put(smartContractId, smartContractStatus);
  }

  /**
   * 获取智能合约的状态
   *
   * @param smartContractId 智能合约编号
   */
  public static String getSmartContractStauts(String smartContractId) {
    return smartContractIdAndStatusMap.get(smartContractId);
  }

  public static boolean checkSmartContractRunning(String smartContractId) {
    logger.info(
        SmartContractSupportService.smartContractIdAndStreamObserverMap.keySet().toString());
    StreamObserver<SmartContractMessage> smartContractMessageStreamObserver =
        SmartContractSupportService.smartContractIdAndStreamObserverMap.get(smartContractId);
    return smartContractMessageStreamObserver != null;
  }
}
