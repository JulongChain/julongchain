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
package org.bcia.julongchain.node.common.client;

import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

/**
 * 投递客户端接口
 *
 * @author zhouhui
 * @date 2018/3/7
 * @company Dingxuan
 */
public interface IDeliverClient {
    /**
     * 向Consenter发送一个信封格式的消息
     *
     * @param envelope         消息信封对象
     * @param responseObserver 回调
     */
    void send(Common.Envelope envelope, StreamObserver<Ab.DeliverResponse> responseObserver);

    /**
     * 获取特定的区块
     *
     * @param groupId
     * @param blockNumber
     * @param responseObserver
     */
    void getSpecifiedBlock(String groupId, long blockNumber, StreamObserver<Ab.DeliverResponse>
            responseObserver);

    /**
     * 获取最早的区块
     *
     * @param groupId
     * @param responseObserver
     */
    void getOldestBlock(String groupId, StreamObserver<Ab.DeliverResponse> responseObserver);

    /**
     * 获取最新的区块
     *
     * @param groupId
     * @param responseObserver
     */
    void getNewestBlock(String groupId, StreamObserver<Ab.DeliverResponse> responseObserver);

    /**
     * 关闭客户端
     */
    void close();
}
