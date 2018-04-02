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
package org.bcia.javachain.consenter.common.broadcast;




import org.bcia.javachain.protos.common.Common;

import java.util.Queue;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.locks.Lock;

/**
 * @author zhangmingyang
 * @Date: 2018/3/12
 * @company Dingxuan
 */
public class PollingThread extends Thread implements Runnable {
  //  public static Queue<Message> queue = new LinkedTransferQueue<Message>();
  public static Queue<Message> queue = new LinkedTransferQueue<Message>();
    @Override
    public void run() {
        while (true) {
            while (!queue.isEmpty()) {
                //queue.poll().display();
                queue.poll().display();
            }
            //把队列中的消息全部打印完之后让线程阻塞
            synchronized (Lock.class)
            {
                try {
                    Lock.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
