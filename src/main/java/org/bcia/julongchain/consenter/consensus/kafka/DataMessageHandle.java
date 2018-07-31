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
package org.bcia.julongchain.consenter.consensus.kafka;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.protos.consenter.Kafka;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 类描述
 *
 * @author yangdong
 * @date 2018/3/19
 * @company Shudun
 */

public class DataMessageHandle {

    @Autowired
    private ProducerMessage producerMessage;

    //转换kafka连接消息
    public Kafka.KafkaMessage newConnectMessage(){
        Kafka.KafkaMessageConnect.Builder kafkaConectMessage= Kafka.KafkaMessageConnect.newBuilder();
        kafkaConectMessage.setPayload(null);
        Kafka.KafkaMessage.Builder kafkaBuilder=Kafka.KafkaMessage.newBuilder();
        kafkaBuilder.setConnect(kafkaConectMessage);
        return kafkaBuilder.build();
    }

    //转换Enqueue（普通）方法所用消息格式
    public Kafka.KafkaMessage newNormalMessage(byte [] payload, long configSeq, long originalOffset){

        // 组织KafkaMessageRegular
        Kafka.KafkaMessageRegular.Builder kafkaRegularBuilder=Kafka.KafkaMessageRegular.newBuilder();
        kafkaRegularBuilder.setPayload(ByteString.copyFrom(payload));
        kafkaRegularBuilder.setConfigSeq(configSeq);
        kafkaRegularBuilder.setOriginalOffset(originalOffset);
        kafkaRegularBuilder.setClass_(Kafka.KafkaMessageRegular.Class.NORMAL);
        // 组织KafkaMessage
        Kafka.KafkaMessage.Builder kafkaBuilder=Kafka.KafkaMessage.newBuilder();
        kafkaBuilder.setRegular(kafkaRegularBuilder);
        return kafkaBuilder.build();

    }

    //转换Enqueue（配置）方法所用消息格式
    public Kafka.KafkaMessage newConfigMessage(byte [] config, long configSeq, long originalOffset){
        // 组织KafkaMessageRegular
        Kafka.KafkaMessageRegular.Builder kafkaRegularBuilder=Kafka.KafkaMessageRegular.newBuilder();
        kafkaRegularBuilder.setPayload(ByteString.copyFrom(config));
        kafkaRegularBuilder.setConfigSeq(configSeq);
        kafkaRegularBuilder.setOriginalOffset(originalOffset);
        kafkaRegularBuilder.setClass_(Kafka.KafkaMessageRegular.Class.CONFIG);
        // 组织KafkaMessage
        Kafka.KafkaMessage.Builder kafkaBuilder=Kafka.KafkaMessage.newBuilder();
        kafkaBuilder.setRegular(kafkaRegularBuilder);
        return kafkaBuilder.build();
    }

    //转换数据切块方法
    public Kafka.KafkaMessage newTimeToCutMessage(long blockNumber){

        Kafka.KafkaMessageTimeToCut.Builder kafkaTimeToCut=Kafka.KafkaMessageTimeToCut.newBuilder();
        kafkaTimeToCut.setBlockNumber(blockNumber);

        Kafka.KafkaMessage.Builder kafkaBuilder=Kafka.KafkaMessage.newBuilder();
        kafkaBuilder.setTimeToCut(kafkaTimeToCut);
        return kafkaBuilder.build();
    }

    //生产者消息转换
    public ProducerMessage newProducerMessage(KafkaTopicPartitionInfo kafkaInfo,byte [] pld){
        producerMessage.setTopic(kafkaInfo.topic);
        producerMessage.setKey(kafkaInfo.partitionID);
        producerMessage.setValue(pld);
        return producerMessage;
    }


}
