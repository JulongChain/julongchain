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
import java.util.concurrent.TimeUnit;

/**
 * 生产消费者模型之生产者
 *
 * @author zhouhui
 * @date 2018/05/19
 * @company Dingxuan
 */
public class Producer<T> {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Producer.class);
    /**
     * 生产一个产品的超时时间
     */
    private static final long PRODUCE_TIMEOUT = 3000;
    /**
     * 可阻塞队列，充当内存缓冲区，以保证内存缓冲区不够时，消费者线程阻塞,而内存缓存区满时，生产者线程阻塞
     */
    private BlockingQueue<T> queue;

    public Producer(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * 生产某产品
     *
     * @param t
     * @return
     */
    public boolean produce(T t) {
        try {
            return queue.offer(t, PRODUCE_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
