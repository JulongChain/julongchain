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
package org.bcia.javachain.consenter.common.broadcast.mq;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangmingyang
 * @Date: 2018/3/13
 * @company Dingxuan
 */
public class ProduceConsumer {
    public static class Product {
        private int name; //产品名称,编号

        @Override
        public String toString() {
            return "Product:" + name;
        }
    }

    //仓库类,主要逻辑在这里实现
    public static class WareHouse {
        private static Queue<Product> products = new LinkedList<Product>(); //产品队列
        private final int MAX = 200; //仓库最大容量
        private static int currentCount = 0; //当前仓储量
        private static int name = 1; //产品编号
        private static Lock lock = new ReentrantLock(); //自定义锁对象
        private static Condition condition = lock.newCondition();
        //生产产品
        public void produce(Product product, int amount) {
            lock.lock();
            try {
                while (currentCount + amount > MAX) { //队列满
                    System.out.println(Thread.currentThread().getName() + "生产后的产品总量大于承载能力, wait");
                    try {
                        condition.await(); //进入等待
                        System.out.println(Thread.currentThread().getName() + "Get signal");
                    } catch (InterruptedException e) {
                        System.out.println(e.getStackTrace());
                    }
                }
                for (int i = 0; i < amount; i++) {
                    product.name = name++; //设置产品编号
                    products.add(product); //向队列中加入产品
                    currentCount++; //仓储数量增加
                }
                System.out.println(Thread.currentThread().getName() + "生产了 " + amount + " 个商品, 现在库存为: " + currentCount);
                condition.signalAll(); //通知消费者
                System.out.println(Thread.currentThread().getName() + " signalAll...");
            } finally {
                lock.unlock();
            }
        }

        //消费产品
        public void consume(int amount) {
            lock.lock();
            try {
                while (currentCount < amount) { //商品不够本次消费
                    System.out.println(Thread.currentThread().getName() + "要消费数量为: " + amount + "仓储数量: " + currentCount + " 仓储数量不足, wait");
                    try {
                        condition.await(); //进入等待
                        System.out.println(Thread.currentThread().getName() + "Get signal");
                    } catch (InterruptedException e) {
                    }
                }
                for (int i = 0; i < amount; i++) {
                    Product product = products.poll();
                    currentCount--; //减仓储
                }
                System.out.println(Thread.currentThread().getName() + "消费了 " + amount + " 个商品, 现在库存为: " + currentCount);
                condition.signalAll(); //通知生产者
                System.out.println(Thread.currentThread().getName() + "signalAll...");
            } finally {
                lock.unlock();
            }
        }
    }

    //生产者类
    public static class Producer implements Runnable {
        @Override
        public void run() {
            int amount = (int) (Math.random() * 100); //最多生产仓储量的一半
            Product product = new Product();
            WareHouse wareHouse = new WareHouse();
            wareHouse.produce(product, amount);
        }
    }

    //消费者类
    public static class Consumer implements  Runnable{
        @Override
        public void run() {
            int amount = (int) (Math.random() * 100); //最多生产仓储量的一半
            WareHouse wareHouse = new WareHouse();
            wareHouse.consume(amount);
        }
    }

    public static void main(String[] args) {
        //生产者线程池
        ExecutorService producerPool = Executors.newFixedThreadPool(1);
        ExecutorService consumerPool = Executors.newSingleThreadExecutor();
        int i = 0;
        while (true) {
            Producer producer = new Producer();
            producerPool.execute(producer);
            Consumer consumer = new Consumer();
            consumerPool.execute(consumer);
            if (i++ > 200) {
                break;
            }
        }
    }
}
