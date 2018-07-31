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
package org.bcia.julongchain.consenter.consensus.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.consenter.common.blockcutter.BlockCutter;
import org.bcia.julongchain.consenter.consensus.IChain;
import org.bcia.julongchain.consenter.entity.ChainEntity;
import org.bcia.julongchain.consenter.util.Constant;
import org.bcia.julongchain.consenter.util.YamlLoader;
import org.bcia.julongchain.consenter.util.Utils;
import org.bcia.julongchain.gossip.Node1;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Kafka;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangdong
 * @Date: 2018/3/19
 * @company Shudun
 */
public class Chain implements IChain {

    private static JavaChainLog log = JavaChainLogFactory.getLog(Node1.class);
    //TODO 使用自动注入报错，暂时new方式
    private DataMessageHandle dataMessageHandle=new DataMessageHandle();
    @Autowired
    private KafkaProduce kafkaProduce;
    //TODO 使用自动注入报错，暂时new方式
    private KafkaSimpleConsumer kafkaSimpleConsumer=new KafkaSimpleConsumer();
    @Autowired
    private YamlLoader yamlLoader =new YamlLoader();
    //TODO 不知如何获取
    private ChainEntity chain=new ChainEntity();
    Map map=(HashMap) YamlLoader.readYamlFile(Constant.ORDERER_CONFIG).get(Constant.KAFKA);
    BlockCutter blockCutter=new BlockCutter();

    @Override
    public void order(Common.Envelope env, long configSeq) {
        this.orderHandle(env,configSeq,(long)0);
    }

    @Override
    public void configure(Common.Envelope config, long configSeq) {
       this.configureHandle(config,configSeq,(long)0);
    }

    @Override
    public void waitReady() {

    }

    @Override
    public void start() {
        //新启动一个线程，来处理消费消息
        Thread t = new Thread(new Runnable(){
            @Override
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
        log.debug("[channel: %s] Enqueueing envelope...", chain.getChainID());
        String topic = (String)((HashMap)map.get(Constant.COMSUMER)).get(Constant.TOPIC);
        int partitionID = (int)((HashMap)map.get(Constant.COMSUMER)).get(Constant.PARTITION_ID);
        // 获取kafkaInfo，从配置文件获取（现在这里定义，TODO该成全局）
        KafkaTopicPartitionInfo kafkaInfo=new KafkaTopicPartitionInfo(topic,partitionID);
        //创建生产者消息
        ProducerMessage message=dataMessageHandle.newProducerMessage(kafkaInfo,kafkaMessage.toByteArray());
        //调用生产者发送消息
        kafkaProduce.send(message);


    }
   //实现kafka消费者，start调用该方法
    public void processMessagesToBlocks(byte[] message,Long offset) throws IOException {

        //解析数据，分类处理
        String json=new String(message);
        ObjectMapper mapper = new ObjectMapper();
        //1.转换ProducerMessage
        ProducerMessage producerMessage = mapper.readValue(json, ProducerMessage.class);
        //2.转换kafkaMessageByte字节数组
        byte[] kafkaMessageByte=producerMessage.getValue();
        //3.转换Kafka.KafkaMessage
        Kafka.KafkaMessage kafkaMessage=Kafka.KafkaMessage.parseFrom(kafkaMessageByte);

        //解压消费消息所携带的数据的类型，进入不同分支，处理消息
        switch(kafkaMessage.getTypeCase()){
            case REGULAR:        //orderer的正常消息
                processRegular(kafkaMessage.getRegular(),offset);
            case CONNECT:        //KAFKA与Orderer的连接消息
                processConnect(chain.getChainID());
            case TIME_TO_CUT:   //orderer的生产块事件消息
                processTimeToCut(kafkaMessage.getTimeToCut(),offset);
                log.debug("[channel: %s] Consenter for channel exiting", chain.getChainID());
        }
    }

    private Object processConnect(String channelName){
        log.debug("[channel: %s] It's a connect message - ignoring", channelName);
        return null;
    }



    //处理消息（重发消息，切块等）区分值三种（未知，配置，正常）的消息类型
    //processMessagesToBlocks()调用该方法
    public void processRegular(Kafka.KafkaMessageRegular  kafkaMessageRegular,Long receivedOffset){
          int seq = chain.sequence();
          Common.Envelope env= Common.Envelope.newBuilder().build();
        //TODO env赋值

        //处理NORMAL消息
        if(Kafka.KafkaMessageRegular.Class.NORMAL==kafkaMessageRegular.getClass_()){

            //校验偏移量
            if(Kafka.KafkaMessageRegular.newBuilder().getOriginalOffset()!=0){
                //TODO 考虑offset
                if(Kafka.KafkaMessageRegular.newBuilder().getOriginalOffset()<= chain.getLastOriginalOffsetProcessed()){
                    }
            }
          //校验seq
            if(Kafka.KafkaMessageRegular.newBuilder().getConfigSeq()<seq){
            Long configSeq= chain.processNormalMsg(env);
                orderHandle(env, configSeq, receivedOffset);
            }

            //这里的任何消息可能已经或可能没有被重新验证和重新排序，但它们在这里是绝对有效的
            //如果消息是预先lastoriginaloffsetprocessed重新验证，重新排序
            long offset= Kafka.KafkaMessageRegular.newBuilder().getOriginalOffset();
            if(offset == 0 ) {
                offset = chain.getLastOriginalOffsetProcessed();
            }
            //提交批处理
            commitNormalMsg(env, offset);
            //处理CONFIG消息
        }else if(Kafka.KafkaMessageRegular.Class.CONFIG==kafkaMessageRegular.getClass_()){
            //校验偏移量
            if(Kafka.KafkaMessageRegular.newBuilder().getOriginalOffset()!=0){
                //TODO 考虑offset
                if(Kafka.KafkaMessageRegular.newBuilder().getOriginalOffset()<= chain.getLastOriginalOffsetProcessed()){
                }
            }
            //校验seq
            if(Kafka.KafkaMessageRegular.newBuilder().getConfigSeq()<seq){
                Long configSeq= chain.processNormalMsg(env);
                configureHandle(env,configSeq, receivedOffset);
            }

            //这里的任何消息可能已经或可能没有被重新验证和重新排序，但它们在这里是绝对有效的
            //如果消息是预先lastoriginaloffsetprocessed重新验证，重新排序
            long offset= Kafka.KafkaMessageRegular.newBuilder().getOriginalOffset();
            if(offset == 0 ) {
                offset = chain.getLastOriginalOffsetProcessed();
            }
            //提交批处理
            commitConfigMsg(env, offset);
            //处理UNKNOWN消息
        }else if(Kafka.KafkaMessageRegular.Class.UNKNOWN==kafkaMessageRegular.getClass_()){
                //TODO 执行util.ChannelHeader()

        }
    }

    //批量处理Normal消息
    private void commitNormalMsg(Common.Envelope message,long newOffset){
        Common.Envelope []  batches={};
        boolean pending=false;
        //TODO orderer提供方法（go中返回值应该是两个数组类型和布尔类型）
       // Common.Envelope [] batches=blockCutter.ordered(message);
        log.debug("[channel: %s] Ordering results: items in batch = %d, pending = %v", chain.getChainID(), batches.length, pending);
        if (batches.length==0){
            chain.setLastOriginalOffsetProcessed(newOffset);
            if(chain.getTimer()==null){
                //TODO 等待一段时间
                //go中  chain.timer = time.After(chain.SharedConfig().BatchTimeout())
            }
        }
            chain.setTimer(null);
            long offset = newOffset;
            if(pending || batches.length>2  ){
                offset--;
            }else{
                chain.setLastOriginalOffsetProcessed(offset);
            }
            Common.Envelope [] block={};
            //TODO orderer提供方法(为block赋值)
            //block = chain.CreateNextBlock(batches[0])
            Kafka.KafkaMetadata.Builder kafkaMetadataBuilder=Kafka.KafkaMetadata.newBuilder();
            kafkaMetadataBuilder.setLastOffsetPersisted(newOffset);
            kafkaMetadataBuilder.setLastOriginalOffsetProcessed(chain.getLastOriginalOffsetProcessed());
            byte[]  metadata= Utils.MarshalOrPanic(kafkaMetadataBuilder.build());

            //TODO orderer提供方法(WriteBlock写块)
            //chain.WriteBlock(block, metadata)
            int lastCutBlockNumber= chain.getLastCutBlockNumber();
            lastCutBlockNumber++;
            log.debug("[channel: %s] Batch filled, just cut block %d - last persisted offset is now %d", chain.getChainID(), chain.getLastCutBlockNumber(), offset);

            if(batches.length==2){
                chain.setLastOriginalOffsetProcessed(newOffset);
                offset++;
                //TODO orderer提供方法(为block赋值)
                //block = chain.CreateNextBlock(batches[1]);
                 kafkaMetadataBuilder=Kafka.KafkaMetadata.newBuilder();
                kafkaMetadataBuilder.setLastOffsetPersisted(newOffset);
                kafkaMetadataBuilder.setLastOriginalOffsetProcessed(chain.getLastOriginalOffsetProcessed());
                metadata= Utils.MarshalOrPanic(kafkaMetadataBuilder.build());

                //TODO orderer提供方法(WriteBlock写块)
                //chain.WriteBlock(block, metadata)
                 lastCutBlockNumber= chain.getLastCutBlockNumber();
                lastCutBlockNumber++;
                log.debug("[channel: %s] Batch filled, just cut block %d - last persisted offset is now %d", chain.getChainID(), chain.getLastCutBlockNumber(), offset);
            }
        }



    //批量处理config消息
    private void commitConfigMsg(Common.Envelope message,long newOffset){
        log.debug("[channel: %s] Received config message", chain.getChainID());
        Common.Envelope [] batch={};
        //TODO orderer提供方法(为batch赋值)
        //batch = chain.BlockCutter().Cut()
        if(batch !=null){
            log.debug("[channel: %s] Cut pending messages into block", chain.getChainID());
            Common.Envelope [] block={};
            //TODO orderer提供方法(为block赋值)
            //block = chain.CreateNextBlock(batch)
            Kafka.KafkaMetadata.Builder kafkaMetadataBuilder=Kafka.KafkaMetadata.newBuilder();
            kafkaMetadataBuilder.setLastOffsetPersisted(newOffset);
            kafkaMetadataBuilder.setLastOriginalOffsetProcessed(chain.getLastOriginalOffsetProcessed());
            byte[]  metadata= Utils.MarshalOrPanic(kafkaMetadataBuilder.build());

            //TODO orderer提供方法(WriteBlock写块)
            //chain.WriteBlock(block, metadata)
            int lastCutBlockNumber= chain.getLastCutBlockNumber();
            lastCutBlockNumber++;
        }
        log.debug("[channel: %s] Creating isolated block for config message", chain.getChainID());
        chain.setLastOriginalOffsetProcessed(newOffset);
        Common.Envelope [] block={};
        //TODO orderer提供方法(为block赋值)
        //block = chain.CreateNextBlock(batch)
        Kafka.KafkaMetadata.Builder kafkaMetadataBuilder=Kafka.KafkaMetadata.newBuilder();
        kafkaMetadataBuilder.setLastOffsetPersisted(newOffset);
        kafkaMetadataBuilder.setLastOriginalOffsetProcessed(chain.getLastOriginalOffsetProcessed());
        byte[]  metadata= Utils.MarshalOrPanic(kafkaMetadataBuilder.build());

        //TODO orderer提供方法(WriteBlock写块)
        //chain.WriteBlock(block, metadata)
        int lastCutBlockNumber= chain.getLastCutBlockNumber();
        lastCutBlockNumber++;
        chain.setTimer(null);

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
   public void processTimeToCut(Kafka.KafkaMessageTimeToCut kafkaMessageTimeToCut,Long receivedOffset){
       long ttcNumber= kafkaMessageTimeToCut.getBlockNumber();
       log.debug("[channel: %s] It's a time-to-cut message for block %d", chain.getChainID(), ttcNumber);
       if(ttcNumber==chain.getLastCutBlockNumber()+1){
           chain.setTimer(null);
           log.debug("[channel: %s] Nil'd the timer", chain.getChainID());
           Common.Envelope [] batch={};
           //TODO orderer提供方法(为batch赋值)
            //batch=chain.BlockCutter().Cut();
            if(batch.length==0){
                 log.error("got right time-to-cut message (for block %d),"+
                        " no pending requests though; this might indicate a bug", chain.getLastCutBlockNumber()+1);
            }
           Common.Envelope [] block={};
           //TODO orderer提供方法(为block赋值)
           //block = chain.CreateNextBlock(batch)
           Kafka.KafkaMetadata.Builder kafkaMetadataBuilder=Kafka.KafkaMetadata.newBuilder();
           kafkaMetadataBuilder.setLastOffsetPersisted(receivedOffset);
           kafkaMetadataBuilder.setLastOriginalOffsetProcessed(chain.getLastOriginalOffsetProcessed());
           byte[]  metadata= Utils.MarshalOrPanic(kafkaMetadataBuilder.build());

           //TODO orderer提供方法(WriteBlock写块)
           //chain.WriteBlock(block, metadata)
           int lastCutBlockNumber= chain.getLastCutBlockNumber();
           lastCutBlockNumber++;
            log.debug("[channel: %s] Proper time-to-cut received, just cut block %d", chain.getChainID(), chain.getLastCutBlockNumber());
       }else if(ttcNumber > chain.getLastCutBlockNumber()+1){
           log.error("got larger time-to-cut message (%d) than allowed/expected (%d)"+
                           " - this might indicate a bug", ttcNumber, chain.getLastCutBlockNumber()+1);
       }
        log.debug("[channel: %s] Ignoring stale time-to-cut-message for block %d", chain.getChainID(), ttcNumber);
   }
    //1.创建生产者，消费者，分区消费者，父类消费者，2.调用消费者方法，调用方法
    //start方法调用该方法
    public void startThread(){
        int maxReads =(int) ((HashMap)map.get(Constant.COMSUMER)).get(Constant.MAX_READS);
        String topic = (String)((HashMap)map.get(Constant.COMSUMER)).get(Constant.TOPIC);
        int partitionID = (int)((HashMap)map.get(Constant.COMSUMER)).get(Constant.PARTITION_ID);

        KafkaTopicPartitionInfo topicPartitionInfo = new KafkaTopicPartitionInfo(topic, partitionID);
        List<KafkaBrokerInfo> seeds = new ArrayList<KafkaBrokerInfo>();
        Map serversMap= (HashMap)map.get(Constant.SERVER);
        for(int i=1;i<=serversMap.size();i++){
            String index="brokerHost"+i;
            String host=(String)serversMap.get(index);
            String[] hostArray=host.split(":");
            KafkaBrokerInfo kafkaInfo=new KafkaBrokerInfo(hostArray[0],Integer.parseInt(hostArray[1]));
            seeds.add(kafkaInfo);
        }
        try {
            kafkaSimpleConsumer.run((long)maxReads, topicPartitionInfo, seeds);
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
        Kafka.KafkaMessage kafkaMessage=dataMessageHandle.newNormalMessage(env.toByteArray(),configSeq,originalOffset);
        //调用enqueue()方法
        enqueue(kafkaMessage);

    }
    //configure具体操作
    public void configureHandle(Common.Envelope config, long configSeq,Long originalOffset) {
        //转换Kafka数据类型
        Kafka.KafkaMessage kafkaMessage=dataMessageHandle.newConfigMessage(config.toByteArray(),configSeq,originalOffset);
        //调用enqueue()方法
        enqueue(kafkaMessage);
    }

    public static void main(String[] args) {
        Chain cc=new Chain();
      // cc.start();
        cc.enqueue(null);
    }

}