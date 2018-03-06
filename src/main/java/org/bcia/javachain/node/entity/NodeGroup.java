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

import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.FileUtils;
import org.bcia.javachain.consenter.common.broadcast.BroadCastClient;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.consenter.Ab;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        NodeGroup group = new NodeGroup();








        BroadCastClient broadCastClient = new BroadCastClient();
        try {
            broadCastClient.send(ip, port, groupId, this);
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


        return group;
    }

    public NodeGroup createGroup(String ip, int port, String groupId, String groupFile) throws NodeException {
        if(!FileUtils.isExists(groupFile)){
            log.error("groupFile is not exists");
            throw new NodeException("Group File is not exists");
        }

        byte[] bytes = null;
        Common.Envelope envelope = null;
        try {
//            bytes = FileUtils.readFileBytes(groupFile);

            MethodDescriptor.Marshaller<Common.Envelope> marshaller = ProtoUtils.marshaller(Common.Envelope.getDefaultInstance());
            envelope = marshaller.parse(new FileInputStream(groupFile));

        } catch (IOException e) {
            throw new NodeException("Can not read Group File");
        }



//        InputStream is = marshaller.stream(block);
//        is = new ByteArrayInputStream(ByteStreams.toByteArray(is));



//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(new File(groupFile));
//
//            int buffer_size = 4096;
//            byte[] bytes = new Byte[buffer_size];
//
//
//            int len;
//            for(int i=0;len = fis.read(bytes, 0, buffer_size), ;len < buffer_size)
//
//
//
//
//            fis.re
//
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//        FileUtils.readFileToByteArray(groupFile)
//




        return null;
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

        }
    }

    @Override
    public void onError(Throwable t) {
        log.error(t.getMessage(), t);
    }

    @Override
    public void onCompleted() {

    }
}
