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
package org.bcia.julongchain.consenter.consensus.singleton;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.producer.Consumer;
import org.bcia.julongchain.common.util.producer.Producer;
import org.bcia.julongchain.consenter.common.multigroup.ChainSupport;
import org.bcia.julongchain.consenter.consensus.IChain;
import org.bcia.julongchain.consenter.consensus.IConsensusPlugin;
import org.bcia.julongchain.consenter.entity.BatchesMes;
import org.bcia.julongchain.consenter.entity.Message;
import org.bcia.julongchain.gossip.GossipService;
import org.bcia.julongchain.protos.common.Common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhangmingyang
 * @Date: 2018/3/7
 * @company Dingxuan
 */
public class Singleton implements IChain, IConsensusPlugin {
    private BlockingQueue<Message> blockingQueue;
    private Producer<Message> producer;
    private Consumer<Message> consumer;
    private static Singleton instance;
    private static JavaChainLog log = JavaChainLogFactory.getLog(Singleton.class);
    private static ChainSupport support;
    private Message normalMessage;
    private Message configMessage;

    public static Singleton getInstance(ChainSupport consenterSupport) {
        if(consenterSupport==null){
            return instance;
        }else {
           support=consenterSupport;
        }
         return instance;
    }

    @Override
    public void order(Common.Envelope env, long configSeq) {
        //排序处理普通消息
        Message message = new Message(configSeq, env);
        normalMessage = message;
    }

    @Override
    public void configure(Common.Envelope config, long configSeq) {
        Message message = new Message(configSeq, config);
        this.configMessage = message;
    }

    @Override
    public void waitReady() {
        return;
    }

    @Override
    public void start() {
        consumer.start();
    }

    @Override
    public void halt() {

    }

    @Override
    public IChain handleChain(ChainSupport consenterSupport, Common.Metadata metadata) {
        return new Singleton(consenterSupport);
    }


    public Singleton() {
    }


    public Singleton(ChainSupport consenterSupport) {
        support = consenterSupport;
        instance = this;
        blockingQueue = new LinkedBlockingQueue<>();
        producer = new Producer<Message>(blockingQueue);
        consumer = new Consumer<Message>(blockingQueue) {
            @Override
            public boolean consume(Message message) {
                try {
                    doProcess();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                } catch (LedgerException e) {
                    e.printStackTrace();
                } catch (ValidateException e) {
                    e.printStackTrace();
                } catch (PolicyException e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
    }

    //处理剪块操作
    public void doProcess() throws InvalidProtocolBufferException, LedgerException, ValidateException, PolicyException {
        long timer = 0;
        long seq = support.getSequence();
        if (configMessage == null) {
            // /普通消息
            if (normalMessage.getConfigSeq() < seq) {
                try {
                    support.getProcessor().processNormalMsg(normalMessage.getMessage());
                } catch (InvalidProtocolBufferException e) {
                    log.warn(String.format("Discarding bad normal message: %s", e.getMessage()));
                }
            }
            BatchesMes batchesMes = support.getCutter().ordered(normalMessage.getMessage());
            Common.Envelope[][] batches = batchesMes.getMessageBatches();
            if (batches== null && timer == 0) {
                timer = support.getLedgerResources().getMutableResources().getGroupConfig().getConsenterConfig().getBatchTimeout();
            }else {
                for (Common.Envelope[] env : batches) {
                    Common.Block block = support.createNextBlock(env);
                    support.writeBlock(block, null);

                    org.bcia.julongchain.protos.gossip.Message.Envelope envelope = GossipService.newGossipEnvelope(support.getGroupId(), block.getHeader().getNumber(), block);
                    GossipService.deliver(envelope);

                }
                if (batches.length > 0) {
                    timer = 0;
                }
            }

        } else {
            if (configMessage.getConfigSeq() < seq) {
                try {
                    support.getProcessor().processConfigMsg(configMessage.getMessage());
                } catch (ConsenterException e) {
                    log.error(e.getMessage());
                } catch (InvalidProtocolBufferException e) {
                    log.warn(String.format("Discarding bad config message: %s", e.getMessage()));
                } catch (ValidateException e) {
                    log.error(e.getMessage());
                } catch (PolicyException e) {
                    log.error(e.getMessage());
                }
            }
            //support.getCutter().ordered(configMessage.getMessage());
            Common.Envelope[] batch = support.getCutter().cut();
            if (batch != null) {
                Common.Block block = support.createNextBlock(batch);
                support.writeBlock(block, null);
            }else {
                Common.Block block = support.createNextBlock(new Common.Envelope[]{configMessage.getMessage()});
                support.writeConfigBlock(block, null);
            }

            timer = 0;

        }
        if (timer > 0) {
            timer = 0;
            Common.Envelope[] batch = support.getCutter().cut();
            if (batch.length == 0) {
                log.warn("Batch timer expired with no pending requests, this might indicate a bug");
            }
            log.debug("Batch timer expired, creating block");
            Common.Block block = support.createNextBlock(batch);
            support.writeBlock(block, null);

            org.bcia.julongchain.protos.gossip.Message.Envelope envelope = GossipService.newGossipEnvelope(support.getGroupId(), block.getHeader().getNumber(), block);
            GossipService.deliver(envelope);
        }

    }

    public boolean pushToQueue(Message message) throws ValidateException {
        ValidateUtils.isNotNull(message, "message can not be null");
        return producer.produce(message);
    }

    public Message getNormalMessage() {
        return this.normalMessage;
    }

    public Message getConfigMessage() {
        return this.configMessage;
    }
}
