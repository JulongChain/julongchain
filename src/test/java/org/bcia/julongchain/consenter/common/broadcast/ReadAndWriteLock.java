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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 类描述
 *
 * @author zhangmingyang
 * @date 2018/09/13
 * @company Dingxuan
 */
public class ReadAndWriteLock {
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public static void main(String[] args) {
        final ReadAndWriteLock lock = new ReadAndWriteLock();
        // 建N个线程，同时读
        ExecutorService service = Executors.newCachedThreadPool();
        //读线程池1里边开启------线程1
        service.execute(new Runnable() {
            @Override
            public void run() {
                lock.readFile(Thread.currentThread());
            }
        });
        //读线程池1里边开启------线程2
        service.execute(new Runnable() {
            @Override
            public void run() {
                lock.readFile(Thread.currentThread());
            }
        });
        // 建N个线程，同时写
        ExecutorService service1 = Executors.newCachedThreadPool();
        //写线程池1里边开启------线程1
        service1.execute(new Runnable() {
            @Override
            public void run() {
                lock.writeFile(Thread.currentThread());
            }
        });
        //写线程池1里边开启------线程2
        service1.execute(new Runnable() {
            @Override
            public void run() {
                lock.writeFile(Thread.currentThread());
            }
        });

    }
    // 读操作
    public void readFile(Thread thread){
        lock.readLock().lock();
        boolean readLock = lock.isWriteLocked();
        if(!readLock){
            System.out.println("当前为读锁！");
        }
        try{
            for(int i=0; i<5; i++){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(thread.getName() + ":正在进行读操作……");
            }
            System.out.println(thread.getName() + ":读操作完毕！");
        }finally{
            System.out.println("释放读锁！");
            lock.readLock().unlock();
        }
    }
    // 写操作
    public void writeFile(Thread thread){
        lock.writeLock().lock();
        boolean writeLock = lock.isWriteLocked();
        if(writeLock){
            System.out.println("当前为写锁！");
        }
        try{
            for(int i=0; i<5; i++){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(thread.getName() + ":正在进行写操作……");
            }
            System.out.println(thread.getName() + ":写操作完毕！");
        }finally{
            System.out.println("释放写锁！");
            lock.writeLock().unlock();
        }
    }
}
