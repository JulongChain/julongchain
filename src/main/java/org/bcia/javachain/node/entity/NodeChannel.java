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
package org.bcia.javachain.node.entity;

import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.broadcast.BroadCastClient;
import org.bcia.javachain.consenter.deliver.DeliverClient;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.orderer.Ab;
import org.springframework.stereotype.Component;

/**
 * 节点通道
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
@Component
public class NodeChannel {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeChannel.class);

    private String channelId;

    public NodeChannel createChannel(String ip, int port, String channelId) {
        NodeChannel channel = new NodeChannel();
        channel.setChannelId(channelId);

        BroadCastClient broadCastClient = new BroadCastClient();
        try {
            broadCastClient.send(ip, port, channelId, new StreamObserver<Ab.BroadcastResponse>(){

                @Override
                public void onNext(Ab.BroadcastResponse value) {
                    //如果服务器创建成功，则可继续获取创世区块
                    if(Common.Status.SUCCESS.equals(value.getStatus())){
                        DeliverClient deliverClient = new DeliverClient();
                        try {
                            deliverClient.send(ip, port, queryMessage);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }

                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error(t.getMessage(), t);
                }

                @Override
                public void onCompleted() {

                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }




//        Block block = Block.newBuilder().build();
//
//
//
//        Marshaller<Block> marshaller = ProtoUtils.marshaller(Block.getDefaultInstance());
//        InputStream is = marshaller.stream(block);
//        is = new ByteArrayInputStream(ByteStreams.toByteArray(is));


        return channel;
    }



    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
