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

import java.util.concurrent.locks.Lock;

/**
 * @author zhangmingyang
 * @Date: 2018/3/12
 * @company Dingxuan
 */
public class Main {
    public static  void  sendMess(Common.Envelope envelope){
        Message mes=new Message();
        mes.setEnvelope(envelope);
        PollingThread pollingThread=new PollingThread();
        pollingThread.start();
        //int i=1;
       // if(mes!=null){
            while(mes!=null)
            {
                PollingThread.queue.remove(mes);
                System.out.println("message num is: ");
                // i++;
                //有消息入队后激活轮询线程
                synchronized (Lock.class)
                {
                    Lock.class.notify();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        //}

    }
}
