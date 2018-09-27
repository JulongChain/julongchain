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
import org.apache.activemq.thread.SchedulerTimerTask;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.common.util.producer.Consumer;
import org.bcia.julongchain.common.util.producer.Producer;
import org.bcia.julongchain.consenter.common.multigroup.ChainSupport;
import org.bcia.julongchain.consenter.consensus.IChain;
import org.bcia.julongchain.consenter.consensus.IConsensusPlugin;
import org.bcia.julongchain.consenter.entity.BatchesMes;
import org.bcia.julongchain.consenter.entity.Message;
import org.bcia.julongchain.consenter.entity.NormalMessage;
import org.bcia.julongchain.protos.common.Common;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * 简单排序插件
 *
 * @author zhangmingyang
 * @Date: 2018/3/7
 * @company Dingxuan
 */
public class Singleton implements IChain, IConsensusPlugin {
    private static JulongChainLog log = JulongChainLogFactory.getLog(Singleton.class);
    private BlockingQueue<Message> blockingQueue;
    private Producer<Message> producer;
    private Consumer<Message> consumer;
    private static Singleton instance;
    private static ChainSupport support;
    private volatile boolean needDelay;

    public static Singleton getInstance(ChainSupport consenterSupport) {
        synchronized (Singleton.class) {
            if (consenterSupport == null) {
                return instance;
            } else {
                support = consenterSupport;
            }
            return instance;
        }
    }

    @Override
    public void order(Common.Envelope env, long configSeq) {
    }

    @Override
    public void configure(Common.Envelope config, long configSeq) {
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
    public IChain handleChain(ChainSupport consenterSupport, Common.Metadata metadata) throws ConsenterException {
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
                    doProcess(message);
                } catch (ConsenterException e) {
                    log.error(e.getMessage());
                }
                return true;
            }
        };
    }

    /**
     * 区块处理
     *
     * @param message
     * @throws ConsenterException
     */
    public void doProcess(Message message) throws ConsenterException {

        long seq = support.getSequence();

        if (message instanceof NormalMessage) {
            if (message.getConfigSeq() < seq) {
                try {
                    support.getProcessor().processNormalMsg(message.getMessage());
                } catch (InvalidProtocolBufferException e) {
                    log.warn(String.format("Discarding bad normal message: %s", e.getMessage()));
                    throw new ConsenterException(e);
                }
            }
            BatchesMes batchesMes = support.getCutter().ordered(message.getMessage());
            Common.Envelope[][] batches = batchesMes.getMessageBatches();
            if (batches == null && !needDelay) {
                needDelay = true;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (needDelay) {
                            needDelay = false;
                            log.info("");
                            Common.Envelope[] batch = support.getCutter().cut();
                            if (null == batch || batch.length == 0) {
                                log.warn("Batch timer expired with no pending requests, this might indicate a bug");
                                return;
                            }
                            log.debug("Batch timer expired, creating block");
                            Common.Block block = support.createNextBlock(batch);
                            support.writeBlock(block, null);
                            return;
                        }
                    }
                }, support.getLedgerResources().getMutableResources().getGroupConfig().getConsenterConfig().getBatchTimeout());

            }
            if (batches == null) {

            } else {
                for (Common.Envelope[] env : batches) {
                    log.info("Ready to cut the batches....");
                    Common.Block block = support.createNextBlock(env);
                    support.writeBlock(block, null);
                    log.info("Write the Block finished");
                }
                if (batches.length > 0) {
                    needDelay = false;
                }
            }

        } else {
            if (message.getConfigSeq() < seq) {
                try {
                    support.getProcessor().processConfigMsg(message.getMessage());
                } catch (ConsenterException e) {
                    log.error(e.getMessage());
                    throw new ConsenterException(e);
                    //return;
                }
            }
            Common.Envelope[] batch = support.getCutter().cut();
            if (batch.length != 0) {
                Common.Block block = support.createNextBlock(batch);
                support.writeBlock(block, null);
            } else {
                Common.Block block = support.createNextBlock(new Common.Envelope[]{message.getMessage()});
                support.writeConfigBlock(block, null);
            }
            needDelay = false;
        }

    }

    /**
     * 消息放入队列
     *
     * @param message
     * @return
     * @throws ValidateException
     */
    public boolean pushToQueue(Message message) throws ValidateException {
        ValidateUtils.isNotNull(message, "message can not be null");
        return producer.produce(message);
    }
}
