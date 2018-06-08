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
package org.bcia.julongchain.common.util.producer;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.util.concurrent.BlockingQueue;

/**
 * 生产消费者模型之消费者
 *
 * @author zhouhui
 * @date 2018/05/19
 * @company Dingxuan
 */
public abstract class Consumer<T> extends Thread {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Consumer.class);
    private BlockingQueue<T> queue;

    public Consumer(BlockingQueue<T> queue) {
        super("eventsConsumer");
        this.queue = queue;
    }

    @Override
    public void run() {
        log.info("Consumer start-----");
        try {
            while (true) {
                T t = queue.take();
                if (t != null) {
                    consume(t);
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 消费该产品
     *
     * @param t
     * @return
     */
    public abstract boolean consume(T t);

}
