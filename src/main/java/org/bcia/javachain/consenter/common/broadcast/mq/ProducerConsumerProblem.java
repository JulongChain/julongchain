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

import java.util.concurrent.Semaphore;

import static java.lang.System.out;

/**
 * @author zhangmingyang
 * @Date: 2018/3/13
 * @company Dingxuan
 */
public class ProducerConsumerProblem {
    //初始容量
    private static final int N = 10;

    /***
     * full 产品容量
     * empty 空余容量
     * mutex 读写锁
     */
    private static Semaphore full,empty,mutex;
    //记录当前的产品数量
    private static volatile int count = 0 ;

    static {
        /**
         * full 初始化0个产品
         * empty 初始化有N个空余位置放置产品
         * mutex 初始化每次最多只有一个线程可以读写
         * */
        full = new Semaphore(0);
        empty = new Semaphore(N);
        mutex = new Semaphore(1);
    }


    public static void main(String []args){
        //生产线线程
        new Thread(new Producer()).start();
        //消费者线程
        new Thread(new Consumer()).start();
    }

    //生产者类
    static class Producer implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    empty.acquire();//等待空位
                    mutex.acquire();//等待读写锁
                    count++;
                    out.println("生产者生产了一个，还剩："+count);
                    mutex.release();//释放读写锁
                    full.release();//放置产品
                    //随机休息一段时间，让生产者线程有机会抢占读写锁
                    Thread.sleep(((int)Math.random())%10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //消费者类
    static class Consumer implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    full.acquire();//等待产品
                    mutex.acquire();//等待读写锁
                    count--;
                    out.println("消费者消费了一个，还剩："+count);
                    mutex.release();//释放读写锁
                    empty.release();//释放空位
                    //随机休息一段时间，让消费者线程有机会抢占读写锁
                    Thread.sleep(((int)Math.random())%10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
