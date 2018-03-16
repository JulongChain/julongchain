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
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.localmsp.ILocalSigner;
import org.bcia.javachain.common.localmsp.impl.LocalSigner;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.consenter.common.broadcast.BroadCastClient;
import org.bcia.javachain.node.common.helper.EnvelopeHelper;
import org.bcia.javachain.node.common.client.BroadcastClient;
import org.bcia.javachain.node.common.client.DeliverClient;
import org.bcia.javachain.node.common.client.IBroadcastClient;
import org.bcia.javachain.node.common.client.IDeliverClient;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 节点群组
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
@Component
public class NodeGroup implements StreamObserver<Ab.BroadcastResponse> {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeGroup.class);

    public NodeGroup createGroup(String ip, int port, String groupId) {
//        try {
//            createGroup(ip, port, groupId, null);
//        } catch (NodeException e) {
//            e.printStackTrace();
//        }

        NodeGroup group = new NodeGroup();


        BroadCastClient broadCastClient = new BroadCastClient();
        try {
            broadCastClient.send(ip, port, Common.Envelope.newBuilder().build(), this);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
//
////        Block block = Block.newBuilder().build();
////
////
////
////        Marshaller<Block> marshaller = ProtoUtils.marshaller(Block.getDefaultInstance());
////        InputStream is = marshaller.stream(block);
////        is = new ByteArrayInputStream(ByteStreams.toByteArray(is));
//
//
//        return group;
        return null;
    }

    public void createGroup(String ip, int port, String groupId, String groupFile) throws NodeException {
        if (!FileUtils.isExists(groupFile)) {
            log.error("groupFile is not exists");
            throw new NodeException("Group File is not exists");
        }

        Common.Envelope envelope = null;
        try {
            byte[] bytes = FileUtils.readFileBytes(groupFile);
            envelope = Common.Envelope.parseFrom(bytes);
        } catch (IOException e) {
            throw new NodeException("Can not read Group File");
        }

        ILocalSigner signer = new LocalSigner();
        Common.Envelope signedEnvelope = EnvelopeHelper.sanityCheckAndSignConfigTx(envelope, groupId, signer);
        IBroadcastClient broadcastClient = new BroadcastClient(ip, port);
        broadcastClient.send(signedEnvelope, new StreamObserver<Ab.BroadcastResponse>() {
            @Override
            public void onNext(Ab.BroadcastResponse value) {
                log.info("Broadcast onNext");
                //收到响应消息，判断是否是200消息
                if (Common.Status.SUCCESS.equals(value.getStatus())) {
                    getGenesisBlockThenWrite(ip, port, groupId);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(), t);
            }

            @Override
            public void onCompleted() {
                log.info("Broadcast completed");
            }
        });
    }

    private void getGenesisBlockThenWrite(String ip, int port, String groupId) {
        log.info("getGenesisBlock begin");
        IDeliverClient deliverClient = new DeliverClient(ip, port);
        deliverClient.getSpecifiedBlock(groupId, 0L, new StreamObserver<Ab.DeliverResponse>() {
            @Override
            public void onNext(Ab.DeliverResponse value) {
                log.info("Deliver onNext");
                if (value.hasBlock()) {
                    Common.Block block = value.getBlock();
                    FileUtils.writeFileBytes(groupId + ".block", block.toByteArray());
                } else {
                    log.info("Deliver status:" + value.getStatus().getNumber());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(), t);
            }

            @Override
            public void onCompleted() {
                log.info("Deliver onCompleted");
            }
        });


    }

    public NodeGroup joinGroup(String blockPath) {
        NodeGroup group = new NodeGroup();

        return group;
    }

    @Override
    public void onNext(Ab.BroadcastResponse value) {
        //如果服务器创建成功，则可继续获取创世区块
        if (Common.Status.SUCCESS.equals(value.getStatus())) {
            log.info("We got 200. then we can deliver now");

//            DeliverClient deliverClient = new DeliverClient();
//            try {
//                deliverClient.send(ip, port, queryMessage);
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            }

        }else{
            log.info("Wrong broadcast status: " + value.getStatusValue());
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error(t.getMessage(), t);
    }

    @Override
    public void onCompleted() {

    }

    /**
     *  更新群组配置 V0.25
     */
    public NodeGroup updateGroup(String ip, int port, String groupId) {
        NodeGroup group = new NodeGroup();

        BroadCastClient broadCastClient = new BroadCastClient();
        try {
            //broadCastClient.send(ip, port, groupId, this);
            broadCastClient.send(ip, port, Common.Envelope.newBuilder().build(), this);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return group;
    }
}
