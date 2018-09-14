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
package org.bcia.julongchain.consenter.common.broadcast;

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Grpc;
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.consenter.common.multigroup.ChainSupport;
import org.bcia.julongchain.consenter.common.server.ConsenterServer;
import org.bcia.julongchain.consenter.common.server.IBroadcastHandler;
import org.bcia.julongchain.consenter.consensus.singleton.Singleton;
import org.bcia.julongchain.consenter.entity.ConfigMessage;
import org.bcia.julongchain.consenter.entity.ConfigMsg;
import org.bcia.julongchain.consenter.entity.NormalMessage;
import org.bcia.julongchain.consenter.util.ConsenterConstants;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;

import java.util.Map;


/**
 * broadcast服务对消息的排队
 *
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
public class BroadcastHandler implements IBroadcastHandler {
    private static JulongChainLog log = JulongChainLogFactory.getLog(BroadcastHandler.class);
    private IGroupSupportRegistrar sm;

    public BroadcastHandler(IGroupSupportRegistrar sm) {
        this.sm = sm;
    }

    @Override
    public synchronized void handle(Common.Envelope envelope, StreamObserver<Ab.BroadcastResponse> responseObserver) throws ConsenterException {
        if (ConsenterConstants.SINGLETON.equals(GenesisConfigFactory.getGenesisConfig().getConsenter().getConsenterType())) {
            Common.GroupHeader groupHeader = null;
            ChainSupport chainSupport = null;
            boolean isConfig = false;
            String remoteAddr = ConsenterServer.serverCallCapture.get().getAttributes()
                    .get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString();
            try {
                Map<String, Object> map = sm.broadcastGroupSupport(envelope);
                groupHeader = (Common.GroupHeader) map.get(ConsenterConstants.GROUPHEADER);
                isConfig = (boolean) map.get(ConsenterConstants.ISCONFIG);
                chainSupport = (ChainSupport) map.get(ConsenterConstants.CHAINSUPPORT);
            } catch (InvalidProtocolBufferException e) {
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.INTERNAL_SERVER_ERROR).build());
                responseObserver.onCompleted();
            }
            try {
                chainSupport.getChain().waitReady();
            } catch (ConsenterException e) {
                log.warn(String.format("[group: %s] Rejecting broadcast of message from %s with SERVICE_UNAVAILABLE: rejected by Consenter: %s", groupHeader.getGroupId(), remoteAddr, e.getMessage()));
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SERVICE_UNAVAILABLE).build());
                responseObserver.onCompleted();
            }
            if (!isConfig) {
                //普通消息
                log.debug(String.format("[group: %s] Broadcast is processing normal message from %s with txid '%s' of type %s", groupHeader.getGroupId(), remoteAddr, groupHeader.getTxId(), groupHeader.getType()));
                long configSeq = 0;
                try {
                    configSeq = chainSupport.getProcessor().processNormalMsg(envelope);
                } catch (InvalidProtocolBufferException e) {
                     log.warn(String.format("[channel: %s] Rejecting broadcast of normal message from %s because of error: %s", groupHeader.getGroupId(),remoteAddr,e.getMessage()));
                    responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setInfo(e.getMessage()).build());
                    responseObserver.onCompleted();
                }
                Singleton singleton = null;
                singleton = Singleton.getInstance(chainSupport);
                NormalMessage normalMessage=new NormalMessage(configSeq,envelope);

                try {
                    singleton.pushToQueue(normalMessage);
                } catch (ValidateException e) {
                    responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SERVICE_UNAVAILABLE).build());
                    responseObserver.onCompleted();
                }
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SUCCESS).build());
                responseObserver.onCompleted();
            } else {
                //配置消息
                ConfigMsg configMsg = null;
                try {
                    log.info(String.format("[group: %s] Broadcast is processing config update message from %s", groupHeader.getGroupId(), remoteAddr));
                    configMsg = chainSupport.getProcessor().processConfigUpdateMsg(envelope);
                } catch (ConsenterException e) {
                    log.warn(String.format("[group: %s] Rejecting broadcast of config message from %s because of error: %s", groupHeader.getGroupId(), remoteAddr, e.getMessage()));
                }
                Singleton singleton = null;
                singleton = Singleton.getInstance(chainSupport);
                ConfigMessage configMessage=new ConfigMessage(configMsg.getConfigSeq(),configMsg.getConfig());
                try {
                    singleton.pushToQueue(configMessage);

                } catch (ValidateException e) {
                    throw new ConsenterException(e.getMessage());
                }
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SUCCESS).build());
            }


        } else {

        }
    }
}
