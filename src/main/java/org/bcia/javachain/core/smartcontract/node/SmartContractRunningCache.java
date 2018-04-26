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

import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.bcia.javachain.protos.node.SmartcontractShim.SmartContractMessage;

/**
 * 智能合约运行时状态
 *
 * @author wanliangbing
 * @date 2018/4/25
 * @company Dingxuan
 */
public class SmartContractRunningCache {

    private static final String SMART_CONTRACT_STATUS_NEW = "new";
    private static final String SMART_CONTRACT_STATUS_BUSY = "busy";
    private static final String SMART_CONTRACT_STATUS_READY = "ready";
    private static final String SMART_CONTRACT_STATUS_ERROR = "error";

    /** 保存智能合约的状态，智能合约的状态分为 1：new 2:busy 3:ready 4:error*/
    private static Map<String, String> smartContractIdAndStatusMap = Collections.synchronizedMap(new HashMap<String, String>());

    /** 保存智能合约的gRPC客户端*/
    private static Map<String, StreamObserver<SmartContractMessage>> smartContractIdAndStreamObserverMap =
            Collections.synchronizedMap(new HashMap<String, StreamObserver<SmartContractMessage>>());

    /**
     * 增加智能合约的gRPC客户端
     * @param smartContractId 智能合约编号
     * @param streamObserver 智能合约gRPC客户端
     */
    public static void addStreamObserver(String smartContractId, StreamObserver<SmartContractMessage> streamObserver) {
        smartContractIdAndStreamObserverMap.put(smartContractId, streamObserver);
    }

    /**
     * 获取智能合约的gRPC客户端
     * @param smartContractId 智能合约编号
     * @return
     */
    public static StreamObserver<SmartContractMessage> getStreamObserver(String smartContractId) {
        return smartContractIdAndStreamObserverMap.get(smartContractId);
    }

    /**
     * 更新智能合约的状态
     * @param smartContractId 智能合约编号
     * @param smartContractStatus 智能合约状态
     */
    public static void updateSmartContractStatus(String smartContractId, String smartContractStatus) {
        smartContractIdAndStatusMap.put(smartContractId, smartContractStatus);
    }

    /**
     * 获取智能合约的状态
     * @param smartContractId 智能合约编号
     */
    public static String getSmartContractStauts(String smartContractId) {
        return smartContractIdAndStatusMap.get(smartContractId);
    }

}
