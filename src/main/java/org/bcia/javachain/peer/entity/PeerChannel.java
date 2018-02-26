/**
 * Copyright DingXuan. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.bcia.javachain.peer.entity;

import com.google.common.io.ByteStreams;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.protobuf.ProtoUtils;
import org.bcia.javachain.orderer.broadcast.BroadCastClient;
import org.bcia.javachain.protos.common.Common.Block;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Peer节点通道功能
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class PeerChannel {

    public PeerChannel createChannel(String ip, int port, String channelId){

        BroadCastClient client = new BroadCastClient();
        try {
            client.send(ip, port, channelId);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Block block = Block.newBuilder().build();
//
//
//
//        Marshaller<Block> marshaller = ProtoUtils.marshaller(Block.getDefaultInstance());
//        InputStream is = marshaller.stream(block);
//        is = new ByteArrayInputStream(ByteStreams.toByteArray(is));


        return null;

    }
}
