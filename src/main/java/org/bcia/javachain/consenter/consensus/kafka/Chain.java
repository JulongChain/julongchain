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
import org.bcia.javachain.protos.consenter.Kafka;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangdong
 * @Date: 2018/3/19
 * @company Shudun
 */
public class Chain implements IChain {

    @Autowired
    private DataMessageHandle dataMessageHandle;
    @Autowired
    private KafkaProduce kafkaProduce;
    @Autowired
    private KafkaSimpleConsumer kafkaSimpleConsumer;

    @Override
    public void order(Common.Envelope env, long configSeq) {
        this.orderHandle(env,configSeq,(long)0);
    }

    @Override
    public void configure(Common.Envelope config, long configSeq) {
       this.configureHandle(config,configSeq,(long)0);
    }

    @Override
    public void start() {
        //新启动一个线程，来处理消费消息
        Thread t = new Thread(new Runnable(){
            public void run(){
                startThread();
            }});
        t.start();
    }

    //Halt释放被分配给这个Chain的资源
    @Override
    public void halt() {

    }

    //调用kafka的客户端，实现kafka生产者
    //enqueue接受信息并返回真或假otheriwse验收
    public void enqueue(Kafka.KafkaMessage kafkaMessage){
       // 获取kafkaInfo，从配置文件获取（现在这里定义，TODO该成全局）
        KafkaTopicPartitionInfo kafkaInfo=new KafkaTopicPartitionInfo("test2",0);
        //创建生产者消息
        ProducerMessage message=dataMessageHandle.newProducerMessage(kafkaInfo,kafkaMessage.toByteArray());
        //调用生产者发送消息
        kafkaProduce.send(message);


    }
   //实现kafka消费者，start调用该方法
    public void processMessagesToBlocks(byte[] message){

    //解析数据，分类处理


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
        long maxReads = 300;
        String topic = "test2";
        int partitionID = 0;

        KafkaTopicPartitionInfo topicPartitionInfo = new KafkaTopicPartitionInfo(topic, partitionID);
        List<KafkaBrokerInfo> seeds = new ArrayList<KafkaBrokerInfo>();
        seeds.add(new KafkaBrokerInfo("10.0.20.91", 9092));
        seeds.add(new KafkaBrokerInfo("10.0.20.92", 9092));
        try {
            kafkaSimpleConsumer.run(maxReads, topicPartitionInfo, seeds);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

   //关掉生产者，消费者
    //halt方法调用该方法
    public void closeKafkaObjects(){

    }

    //order具体操作
    public void orderHandle(Common.Envelope env, Long configSeq,Long originalOffset) {
        //转换Kafka数据类型
        Kafka.KafkaMessage kafkaMessage=dataMessageHandle.newNormalMessage(env.toByteString(),configSeq,originalOffset);
        //调用enqueue()方法
        enqueue(kafkaMessage);

    }
    //configure具体操作
    public void configureHandle(Common.Envelope config, long configSeq,Long originalOffset) {
        //转换Kafka数据类型
        Kafka.KafkaMessage kafkaMessage=dataMessageHandle.newConfigMessage(config.toByteString(),configSeq,originalOffset);
        //调用enqueue()方法
        enqueue(kafkaMessage);
    }

    public static void main(String[] args) {
        Chain cc=new Chain();
        cc.start();
    }

}