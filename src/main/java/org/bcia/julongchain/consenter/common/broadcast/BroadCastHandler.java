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
import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.multigroup.ChainSupport;
import org.bcia.julongchain.consenter.common.server.IHandler;
import org.bcia.julongchain.consenter.consensus.singleton.Singleton;
import org.bcia.julongchain.consenter.entity.ConfigMsg;
import org.bcia.julongchain.consenter.entity.Message;
import org.bcia.julongchain.consenter.util.Constant;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.bcia.julongchain.tools.configtxgen.entity.GenesisConfigFactory;

import java.io.IOException;
import java.util.Map;


/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
public class BroadCastHandler implements IHandler {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BroadCastHandler.class);
    private IGroupSupportRegistrar sm;

    public BroadCastHandler(IGroupSupportRegistrar sm) {
        this.sm = sm;
    }

    @Override
    public void handle(Common.Envelope envelope, StreamObserver<Ab.BroadcastResponse> responseObserver) throws IOException {
        if (Constant.SINGLETON.equals(GenesisConfigFactory.getGenesisConfig().getConsenter().getConsenterType())) {
            Common.GroupHeader groupHeader = null;
            ChainSupport chainSupport = null;
            boolean isConfig = false;
            try {
                Map<String, Object> map = sm.broadcastGroupSupport(envelope);
                groupHeader = (Common.GroupHeader) map.get("chdr");
                isConfig = (boolean) map.get("isConfig");
                chainSupport = (ChainSupport) map.get("cs");
            } catch (InvalidProtocolBufferException e) {
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.INTERNAL_SERVER_ERROR).build());
            }
            try {
                chainSupport.getChain().waitReady();
            } catch (ConsenterException e) {
                log.warn(String.format("[channel: %s] Rejecting broadcast of message from %s with SERVICE_UNAVAILABLE: rejected by Consenter: %s", groupHeader.getGroupId()));
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SERVICE_UNAVAILABLE).build());
            }
            if (!isConfig) {
                //普通消息
//                log.debug(String.format("[channel: %s] Broadcast is processing normal message from %s with txid '%s' of type %s", groupHeader.getGroupId(), groupHeader.getTxId(), groupHeader.getType()));
                long configSeq = 0;
                try {
                    configSeq=  chainSupport.getProcessor().processNormalMsg(envelope);
                } catch (InvalidProtocolBufferException e) {
                   // log.warn(String.format("[channel: %s] Rejecting broadcast of normal message from %s because of error: %s", groupHeader.getGroupId()));
                    responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setInfo(e.getMessage()).build());
                }
                //
                Singleton singleton = null;
                singleton = Singleton.getInstance(chainSupport);
                singleton.order(envelope,configSeq);
                try {
                    singleton.pushToQueue(singleton.getNormalMessage());
                } catch (ValidateException e) {
                    responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SERVICE_UNAVAILABLE).build());
                }
                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SUCCESS).build());
            }else {
                //配置消息
                ConfigMsg  configMsg = null;
                try {
                    log.info("process the configMsg");
                    configMsg=chainSupport.getProcessor().processConfigUpdateMsg(envelope);
                } catch (ConsenterException e) {
                    log.warn(String.format("[channel: %s] Rejecting broadcast of config message because of error: %s", groupHeader.getGroupId(), e.getMessage()));
                } catch (ValidateException e) {
                    log.error(e.getMessage());
                }
                Singleton singleton = null;
                singleton = Singleton.getInstance(chainSupport);
                singleton.configure(configMsg.getConfig(),configMsg.getConfigSeq());
                try {
                    singleton.pushToQueue(singleton.getConfigMessage());
                } catch (ValidateException e) {
                   throw new IOException(e.getMessage());
                }

                responseObserver.onNext(Ab.BroadcastResponse.newBuilder().setStatus(Common.Status.SUCCESS).build());
            }


        } else {

        }
    }
}
