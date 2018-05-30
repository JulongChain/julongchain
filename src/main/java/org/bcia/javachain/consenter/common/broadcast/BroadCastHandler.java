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
import org.bcia.javachain.consenter.common.multigroup.MultiGroup;
import org.bcia.javachain.consenter.entity.ConfigMsg;
import org.bcia.javachain.consenter.util.Constant;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

import static org.bcia.javachain.tools.configtxgen.entity.GenesisConfigFactory.loadGenesisConfig;

/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
@Component
public class BroadCastHandler {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BroadCastHandler.class);
    BroadCastProcessor processor = new BroadCastProcessor();
    MultiGroup multiGroup = new MultiGroup();
    /**
     * 处理发来Envelope格式消息
     *
     * @param envelope
     * @param responseObserver
     * @throws Exception
     */
    public void handle(Common.Envelope envelope, StreamObserver<Ab.BroadcastResponse> responseObserver) throws Exception {
        //解析处理消息,获取消息head,是否为配置消息,
        Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
        Common.GroupHeader groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        LinkedBlockingQueue<Common.Envelope> queue = new LinkedBlockingQueue<>();
        queue.put(envelope);
        if (Constant.SINGLETON.equals(loadGenesisConfig().getConsenter().getConsenterType())) {
            log.info("进入Singleton方式排序处理");
            boolean isconfigMes = processor.classfiyMsg(groupHeader);
            if (!isconfigMes) {
                long configSeq = processor.processNormalMsg(envelope);
                //排序并生成区块写入账本
                processor.order(envelope, configSeq);
                log.info("i'm broadhandle");
                //根据不同的消息处理结果,返回给客户端或sdk对应的状态值
                //blockCutter.ordered();
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatusValue(200).build());
            } else {
                //通过配置更新消息,获取conifg消息和配置序列
                ConfigMsg configMsg = processor.processConfigUpdateMsg(envelope);
               // log.info(envelope.getSignature().toStringUtf8());
               byte[] signData=envelope.getSignature().toByteArray();
                Common.SignatureHeader signatureHeader=Common.SignatureHeader.parseFrom(payload.getHeader().getSignatureHeader());
                byte[] creator=signatureHeader.getCreator().toByteArray();
                String userId=signatureHeader.getNodeid();
                System.out.println("userId:"+userId);
                //首先从区块链上查询是否存在相同的公钥,如果不存在,说明该消息不合法,存在相同的可继续对消息进行验证签名

                //boolean verfiy=   sm2impl.verfiy(userId,payload.toByteArray(),SM2.byte2ECpoint(creator),signData);
                //System.out.println("对消息的验签结果为："+verfiy);
                //将信封格式数据和排序序列发送给排序服务
                processor.confgigure(configMsg.getConfig(), configMsg.getConfigSeq());
                //排序服务对配置消息进行排序,切割成块
                // processor.processConfigMsg()

                Common.Envelope[] message = {configMsg.getConfig()};
                Common.Block configblock = multiGroup.createNextBlock(message);
                multiGroup.writeConfigBlock(configblock, null);
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatusValue(200).build());
            }
        }else {
            log.info("kafka排序");
        }



    }
}
