/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.consenter.common.server;

import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

import java.io.IOException;

/**
 * IBroadcastHandler，提供给boradcast
 * 服务处理接收到的消息
 * @author zhangmingyang
 * @Date: 2018/6/4
 * @company Dingxuan
 */
public interface IBroadcastHandler {
   void handle(Common.Envelope envelope, StreamObserver<Ab.BroadcastResponse> responseObserver) throws ConsenterException;
}
