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
package org.bcia.javachain.consenter.consensus.kafka;

import org.bcia.javachain.consenter.consensus.IChain;
import org.bcia.javachain.protos.common.Common;

/**
 * @author yangdong
 * @Date: 2018/3/19
 * @company Shudun
 */
public class Chain implements IChain {
    @Override
    public void order(Common.Envelope env, long configSeq) {
    }

    @Override
    public void configure(Common.Envelope config, long configSeq) {

    }

    @Override
    public void start() {

    }

    //Halt释放被分配给这个Chain的资源
    @Override
    public void halt() {

    }

    //调用kafka的客户端，实现kafka生产者
    public void enqueue(){

    }
   //实现kafka消费者，start调用该方法
    public void processMessagesToBlocks(){

    }
    //处理消息（重发消息，切块等）区分值三种（未知，配置，正常）的消息类型
    //processMessagesToBlocks()调用该方法
    public void processRegular(){

    }
    //到时间切块方法
    //processMessagesToBlocks()调用该方法
    public void sendTimeToCut(){

    }
    //发送连接消息
    //processMessagesToBlocks()调用该方法
    public void sendConnectMessage(){

    }
   //到时间切块
   //processMessagesToBlocks()调用该方法
   public void  processTimeToCut(){

   }
    //1.创建生产者，消费者，分区消费者，父类消费者，2.调用消费者方法，调用方法
    //start方法调用该方法
    public void startThread(){

    }

   //关掉生产者，消费者
    //halt方法调用该方法
    public void closeKafkaObjects(){

    }

    //order具体操作
    public void orderHandle(Common.Envelope env, long configSeq,Long originalOffset) {

    }


}