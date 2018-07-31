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

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.localmsp.impl.LocalSigner;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.protos.consenter.AtomicBroadcastGrpc;

import java.util.concurrent.TimeUnit;

/**
 * 投递客户端实现
 *
 * @author zhouhui
 * @date 2018/3/7
 * @company Dingxuan
 */
public class DeliverClient implements IDeliverClient {
    private static JavaChainLog log = JavaChainLogFactory.getLog(DeliverClient.class);

    /**
     * IP地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;

    private ManagedChannel managedChannel;

    public DeliverClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void send(Common.Envelope envelope, StreamObserver<Ab.DeliverResponse> responseObserver) {
        managedChannel =
                NettyChannelBuilder.forAddress(host, port).maxInboundMessageSize(CommConstant.MAX_GRPC_MESSAGE_SIZE)
                        .usePlaintext().build();
        AtomicBroadcastGrpc.AtomicBroadcastStub stub = AtomicBroadcastGrpc.newStub(managedChannel);
        StreamObserver<Common.Envelope> envelopeStreamObserver = stub.deliver(responseObserver);
        envelopeStreamObserver.onNext(envelope);
    }

    @Override
    public void getSpecifiedBlock(String groupId, long blockNumber, StreamObserver<Ab.DeliverResponse>
            responseObserver) {
        Ab.SeekPosition seekPosition = buildSpecifiedPosition(blockNumber);
        Common.Envelope envelope = createSeekSignedEnvelope(groupId, seekPosition);
        send(envelope, responseObserver);
    }

    @Override
    public void getOldestBlock(String groupId, StreamObserver<Ab.DeliverResponse> responseObserver) {
        Ab.SeekPosition seekPosition = buildOldestPosition();
        Common.Envelope envelope = createSeekSignedEnvelope(groupId, seekPosition);
        send(envelope, responseObserver);
    }

    @Override
    public void getNewestBlock(String groupId, StreamObserver<Ab.DeliverResponse> responseObserver) {
        Ab.SeekPosition seekPosition = buildNewestPosition();
        Common.Envelope envelope = createSeekSignedEnvelope(groupId, seekPosition);
        send(envelope, responseObserver);
    }

    @Override
    public void close() {
        log.info("DeliverClient close");

        managedChannel.shutdown();
//        try {
//            managedChannel.awaitTermination(1000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            log.error(e.getMessage(), e);
//        }
    }

    private Common.Envelope createSeekSignedEnvelope(String groupId, Ab.SeekPosition seekPosition) {
        //构造SeekInfo对象
        Ab.SeekInfo.Builder seekInfoBuilder = Ab.SeekInfo.newBuilder();
        seekInfoBuilder.setStart(seekPosition);
        seekInfoBuilder.setStop(seekPosition);
        seekInfoBuilder.setBehavior(Ab.SeekInfo.SeekBehavior.BLOCK_UNTIL_READY);
        Ab.SeekInfo seekInfo = seekInfoBuilder.build();

        return EnvelopeHelper.buildSignedEnvelope(Common.HeaderType.CONFIG_UPDATE_VALUE, 0, groupId, new LocalSigner(),
                seekInfo, 0L);
    }

    private Ab.SeekPosition buildSpecifiedPosition(long blockNumber) {
        //构造SeekSpecified对象
        Ab.SeekSpecified.Builder seekSpecifiedBuilder = Ab.SeekSpecified.newBuilder();
        seekSpecifiedBuilder.setNumber(blockNumber);
        Ab.SeekSpecified seekSpecified = seekSpecifiedBuilder.build();

        //构造SeekPosition对象
        Ab.SeekPosition.Builder seekPositionBuilder = Ab.SeekPosition.newBuilder();
        seekPositionBuilder.setSpecified(seekSpecified);
        return seekPositionBuilder.build();
    }

    private Ab.SeekPosition buildNewestPosition() {
        //构造SeekNewest对象
        Ab.SeekNewest.Builder seekNewestBuilder = Ab.SeekNewest.newBuilder();
        Ab.SeekNewest seekNewest = seekNewestBuilder.build();

        //构造SeekPosition对象
        Ab.SeekPosition.Builder seekPositionBuilder = Ab.SeekPosition.newBuilder();
        seekPositionBuilder.setNewest(seekNewest);
        return seekPositionBuilder.build();
    }

    private Ab.SeekPosition buildOldestPosition() {
        //构造SeekOldest对象
        Ab.SeekOldest.Builder seekOldestBuilder = Ab.SeekOldest.newBuilder();
        Ab.SeekOldest seekOldest = seekOldestBuilder.build();

        //构造SeekPosition对象
        Ab.SeekPosition.Builder seekPositionBuilder = Ab.SeekPosition.newBuilder();
        seekPositionBuilder.setOldest(seekOldest);
        return seekPositionBuilder.build();
    }
}
