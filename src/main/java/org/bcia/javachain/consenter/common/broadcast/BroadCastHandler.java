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
package org.bcia.javachain.consenter.common.broadcast;

import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;
import org.springframework.stereotype.Component;

/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
@Component
public class BroadCastHandler {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BroadCastHandler.class);
   BroadCastProcessor processor=new BroadCastProcessor();
   Main main=new Main();
    public void handle(Common.Envelope envelope,StreamObserver<Ab.BroadcastResponse> responseObserver){
      //解析处理消息,获取消息head,是否为配置消息,

        long configSeq= processor.processNormalMsg(envelope);
        //排序并生成区块写入账本
        processor.order(envelope,configSeq);
        //main.sendMess(envelope);
        log.info("i'm broadhandle");
        //根据不同的消息处理结果,返回给客户端或sdk对应的状态值
       responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatusValue(200).build());
    }
}
