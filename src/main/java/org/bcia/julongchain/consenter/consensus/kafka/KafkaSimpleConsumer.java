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

import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.BrokerEndPoint;
import kafka.common.ErrorMapping;
import kafka.common.OffsetAndMetadata;
import kafka.common.OffsetMetadataAndError;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.bcia.julongchain.consenter.util.Constant;
import org.bcia.julongchain.consenter.util.YamlLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * 类描述
 *
 * @author yangdong
 * @date 2018/3/19
 * @company Shudun
 */

public class KafkaSimpleConsumer {

    @Autowired
    private Chain chain;
    @Autowired
    private YamlLoader yamlLoader =new YamlLoader();
    Map map=(HashMap) yamlLoader.readYamlFile(Constant.ORDERER_CONFIG).get(Constant.KAFKA);

    // 最大重试次数
    private int maxRetryTimes = (int) ((HashMap)map.get(Constant.COMSUMER)).get(Constant.MAX_RETRY_TIMES);
    // 重试间隔时间
    private long retryIntervalMillis =(int) ((HashMap)map.get(Constant.COMSUMER)).get(Constant.RETRY_INTERVAL_MILLIS);
    // 缓存Topic/Partition对应的Broker连接信息
    private Map<KafkaTopicPartitionInfo, List<KafkaBrokerInfo>> replicaBrokers = new HashMap<KafkaTopicPartitionInfo, List<KafkaBrokerInfo>>();

    /**
     * 运行入口
     *
     * @param maxReads           最多读取记录数量
     * @param topicPartitionInfo 读取数据的topic分区信息
     * @param seedBrokers        连接topic分区的初始化连接信息
     * @throws Exception
     */
    public void run(long maxReads,
                    KafkaTopicPartitionInfo topicPartitionInfo,
                    List<KafkaBrokerInfo> seedBrokers) throws Exception {
        // 默认消费数据的偏移量是当前分区的最早偏移量值
        long whichTime = kafka.api.OffsetRequest.EarliestTime();

        // 构建client name及groupId
        String topic = topicPartitionInfo.topic;
        int partitionID = topicPartitionInfo.partitionID;
        String clientName = this.createClientName(topic, partitionID);
        String groupId = clientName;

        // 获取当前topic分区对应的分区元数据(主要包括leader节点的连接信息)
        PartitionMetadata metadata = this.findLeader(seedBrokers, topic, partitionID);

        // 校验元数据
        this.validatePartitionMetadata(metadata);


        // 连接leader节点构建具体的SimpleConsumer对象
        System.out.println(metadata.leader().host());
        System.out.println(metadata.leader().port());
        System.out.println(clientName);

        SimpleConsumer consumer = this.createSimpleConsumer(metadata.leader().host(),
                metadata.leader().port(), clientName);

        try {
            // 获取当前topic、当前consumer的消费数据offset偏移量
            int times = 0;
            long readOffSet = -1;
            while (true) {
                readOffSet = this.getLastOffSet(consumer, groupId, topic, partitionID, whichTime, clientName);
                if (readOffSet == -1) {
                    // 当返回为-1的时候，表示异常信息
                    if (times > this.maxRetryTimes) {
                        throw new RuntimeException("Fetch the last offset of those group:" + groupId + " occur exception");
                    }
                    // 先休眠，再重新构建Consumer连接
                    times++;
                    this.sleep();
                    consumer = this.createNewSimpleConsumer(consumer, topic, partitionID);
                    continue;
                }

                // 正常情况下，结束循环
                break;
            }
            System.out.println("The first read offset is:" + readOffSet);

            int numErrors = 0;
            boolean ever = maxReads <= 0;
            // 开始数据读取操作循环，当maxReads为非正数的时候，一直读取数据；当maxReads为正数的时候，最多读取maxReads条数据
            while (ever || maxReads > 0) {
                // 构建获取数据的请求对象， 给定获取数据对应的topic、partition、offset以及每次获取数据最多获取条数
                kafka.api.FetchRequest request = new FetchRequestBuilder()
                        .clientId(clientName)
                        .addFetch(topic, partitionID, readOffSet, 100000)
                        .build();

                // 发送请求到Kafka，并获得返回值
                FetchResponse response = consumer.fetch(request);

                // 如果返回对象表示存在异常，进行异常处理，并进行consumer重新连接的操作
                // 当异常连续出现次数超过5次的时候，程序抛出异常
                if (response.hasError()) {
                    String leaderBrokerHost = consumer.host();
                    numErrors++;
                    short code = response.errorCode(topic, partitionID);
                    System.out.println("Error fetching data from the Broker:" + leaderBrokerHost + " Reason:" + code);
                    if (numErrors > 5) {break;}
                    if (code == ErrorMapping.OffsetOutOfRangeCode()) {
                        // 异常表示是offset异常，重新获取偏移量即可
                        readOffSet = this.getLastOffSet(consumer, groupId, topic, partitionID, kafka.api.OffsetRequest.LatestTime(), clientName);
                        continue;
                    }
                    consumer.close();
                    consumer = null;

                    // 重新创建一个SimpleConsumer对象
                    consumer = this.createNewSimpleConsumer(consumer, topic, partitionID);
                    continue;
                }
                // 重置失败次数
                numErrors = 0;

                // 接收数据没有异常，那么开始对数据进行具体操作，eg: 打印
                long numRead = 0;
                for (MessageAndOffset messageAndOffset : response.messageSet(topic, partitionID)) {
                    // 校验偏移量
                    long currentOffset = messageAndOffset.offset();
                    if (currentOffset < readOffSet) {
                        System.out.println("Found and old offset:" + currentOffset + " Expection:" + readOffSet);
                        continue;
                    }

                    // 获取下一个读取数据开始的偏移量
                    readOffSet = messageAndOffset.nextOffset();
                    // 读取数据的value
                    ByteBuffer payload = messageAndOffset.message().payload();

                    byte[] bytes = new byte[payload.limit()];
                    payload.get(bytes);
                    System.out.println("value:"+currentOffset + ": " + new String(bytes, "UTF-8"));
                    //传给业务处理
                    chain.processMessagesToBlocks(bytes,currentOffset);
                    numRead++;
                    maxReads--;
                }

                // 更新偏移量
                consumer = this.updateOffset(consumer, topic, partitionID,
                        readOffSet, groupId, clientName, 0);

                // 如果没有读取数据，休眠一秒钟
                if (numRead == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        // nothings
                    }
                }
            }

            System.out.println("执行完成....");
        } finally {
            // 关闭资源
            if (consumer != null) {
                try {
                    consumer.close();
                } catch (Exception e) {
                    // nothings
                }
            }
        }
    }

    /**
     * 验证分区元数据，如果验证失败，直接抛出IllegalArgumentException异常
     *
     * @param metadata
     */
    private void validatePartitionMetadata(PartitionMetadata metadata) {
        if (metadata == null) {
            System.out.println("Can't find metadata for Topic and Partition. Exiting!!");
            throw new IllegalArgumentException("Can't find metadata for Topic and Partition. Exiting!!");
        }
        if (metadata.leader() == null) {
            System.out.println("Can't find Leader for Topic and Partition. Exiting!!");
            throw new IllegalArgumentException("Can't find Leader for Topic and Partition. Exiting!!");
        }
    }

    /**
     * Finding the Lead Broker for a Topic and Partition<br/>
     * 获取主题和分区对应的主Broker节点（即topic和分区id是给定参数的对应brokere节点的元数据）<br/>
     * 获取方式:
     *
     * @param brokers     Kafka集群连接参数，eg: {"hadoop-senior01" -> 9092, "hadoop-senior02" -> 9092}
     * @param topic       topic名称
     * @param partitionID 分区id
     * @return
     */
    public PartitionMetadata findLeader(
            List<KafkaBrokerInfo> brokers,
            String topic,
            int partitionID) {
        PartitionMetadata returnMetadata = null;

        for (KafkaBrokerInfo broker : brokers) {
            SimpleConsumer consumer = null;

            try {
                // 1. 创建简单的消费者连接对象
                consumer = new SimpleConsumer(broker.brokerHost, broker.brokerPort, 100000, 64 * 1024, "leaderLookUp");

                // 2. 构建获取参数的Topic名称参数集合
                List<String> topics = Collections.singletonList(topic);

                // 3. 构建请求参数
                TopicMetadataRequest request = new TopicMetadataRequest(topics);

                // 4. 请求数据，得到返回对象
                TopicMetadataResponse response = consumer.send(request);

                // 5. 获取返回值
                List<TopicMetadata> metadatas = response.topicsMetadata();

                // 6. 遍历返回值
                for (TopicMetadata metadata : metadatas) {
                    // 获取当前metadata对应的分区
                    String currentTopic = metadata.topic();
                    if (topic.equalsIgnoreCase(currentTopic)) {
                        // 遍历所有分区的原始数据 ==> 当前分区的元数据
                        for (PartitionMetadata part : metadata.partitionsMetadata()) {
                            if (part.partitionId() == partitionID) {
                                // 1. 找到对应的元数据
                                returnMetadata = part;

                                // 2. 更新备份节点的host数据
                                if (returnMetadata != null) {
                                    KafkaTopicPartitionInfo topicPartitionInfo = new KafkaTopicPartitionInfo(topic, partitionID);
                                    List<KafkaBrokerInfo> brokerInfos = this.replicaBrokers.get(topicPartitionInfo);
                                    if (brokerInfos == null) {
                                        brokerInfos = new ArrayList<KafkaBrokerInfo>();
                                    } else {
                                        brokerInfos.clear();
                                    }

                                    for (BrokerEndPoint replica : returnMetadata.replicas()) {
                                        brokerInfos.add(new KafkaBrokerInfo(replica.host(), replica.port()));
                                    }

                                    this.replicaBrokers.put(topicPartitionInfo, brokerInfos);
                                    return returnMetadata;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error communicating with Broker [" + broker.brokerHost + "] to find Leader for [" + topic + ", " + partitionID + "] Reason:" + e);
            } finally {
                if (consumer != null) {
                    try {
                        consumer.close();
                    } catch (Exception e) {
                        // nothings
                    }
                }
            }
        }

        // 没有找到，返回一个空值，默认情况下，不会返回该值
        return null;
    }

    /**
     * 获取当前groupID对应的consumer在对应的topic和partition中对应的offset偏移量
     *
     * @param consumer    消费者
     * @param groupId     消费者分区id
     * @param topic       所属的Topic
     * @param partitionID 所属的分区ID
     * @param whichTime   用于判断，当consumer从没有消费数据的时候，从当前topic的Partition的那个offset开始读取数据
     * @param clientName  client名称
     * @return 正常情况下，返回非负数，当出现异常的时候，返回-1
     */
    public long getLastOffSet(SimpleConsumer consumer, String groupId,
                              String topic, int partitionID,
                              long whichTime, String clientName) {
        // 1. 从ZK中获取偏移量，当zk的返回偏移量大于0的时候，表示是一个正常的偏移量
        long offset = this.getOffsetOfTopicAndPartition(consumer, groupId, clientName, topic, partitionID);
        if (offset > 0) {
            return offset;
        }

        // 2. 获取当前topic当前分区的数据偏移量
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitionID);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfoMap = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfoMap.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));

        OffsetRequest request = new OffsetRequest(requestInfoMap, kafka.api.OffsetRequest.CurrentVersion(), clientName);
        OffsetResponse response = consumer.getOffsetsBefore(request);

        if (response.hasError()) {
            System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partitionID));
            return -1;
        }

        // 获取偏移量
        long[] offsets = response.offsets(topic, partitionID);
        return offsets[0];
    }

    /**
     * 从保存consumer消费者offset偏移量的位置获取当前consumer对应的偏移量
     *
     * @param consumer    消费者
     * @param groupId     Group Id
     * @param clientName  client名称
     * @param topic       topic名称
     * @param partitionID 分区id
     * @return
     */
    public long getOffsetOfTopicAndPartition(SimpleConsumer consumer, String groupId, String clientName, String topic, int partitionID) {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitionID);
        List<TopicAndPartition> requestInfo = new ArrayList<TopicAndPartition>();
        requestInfo.add(topicAndPartition);
        OffsetFetchRequest request = new OffsetFetchRequest(groupId, requestInfo, 0, clientName);
        OffsetFetchResponse response = consumer.fetchOffsets(request);

        // 获取返回值
        Map<TopicAndPartition, OffsetMetadataAndError> returnOffsetMetadata = response.offsets();
        // 处理返回值
        if (returnOffsetMetadata != null && !returnOffsetMetadata.isEmpty()) {
            // 获取当前分区对应的偏移量信息
            OffsetMetadataAndError offset = returnOffsetMetadata.get(topicAndPartition);
            if (offset.error().equals(ErrorMapping.NoError())) {
                // 没有异常，表示是正常的，获取偏移量
                return offset.offset();
            } else {
                // 当Consumer第一次连接的时候(zk中不在当前topic对应数据的时候)，会产生UnknownTopicOrPartitionCode异常
                System.out.println("Error fetching data Offset Data the Topic and Partition. Reason: " + offset.error());
            }
        }

        // 所有异常情况直接返回0
        return 0;
    }

    /**
     * 根据给定参数获取一个新leader的分区元数据信息
     *
     * @param oldLeader
     * @param topic
     * @param partitionID
     * @return
     */
    private PartitionMetadata findNewLeaderMetadata(String oldLeader,
                                                    String topic,
                                                    int partitionID) {
        KafkaTopicPartitionInfo topicPartitionInfo = new KafkaTopicPartitionInfo(topic, partitionID);
        List<KafkaBrokerInfo> brokerInfos = this.replicaBrokers.get(topicPartitionInfo);
        for (int i = 0; i < 3; i++) {
            boolean gotoSleep = false;
            PartitionMetadata metadata = this.findLeader(brokerInfos, topic, partitionID);
            if (metadata == null) {
                gotoSleep = true;
            } else if (metadata.leader() == null) {
                gotoSleep = true;
            } else if (oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
                // leader切换过程中
                gotoSleep = true;
            } else {
                return metadata;
            }

            if (gotoSleep) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // nothings
                }
            }
        }

        System.out.println("Unable to find new leader after Broker failure. Exiting!!");
        throw new RuntimeException("Unable to find new leader after Broker failure. Exiting!!");
    }

    /**
     * 更新偏移量，当SimpleConsumer发生变化的时候，重新构造一个新的SimpleConsumer并返回
     *
     * @param consumer
     * @param topic
     * @param partitionID
     * @param readOffSet
     * @param groupId
     * @param clientName
     * @param times
     * @return
     * @throws RuntimeException 当更新失败的情况下
     */
    private SimpleConsumer updateOffset(SimpleConsumer consumer, String topic, int partitionID, long readOffSet, String groupId, String clientName, int times) {
        // 构建请求对象
        Map<TopicAndPartition, OffsetAndMetadata> requestInfoMap = new HashMap<TopicAndPartition, OffsetAndMetadata>();
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitionID);
//        requestInfoMap.put(topicAndPartition, new OffsetAndMetadata(readOffSet, OffsetAndMetadata.NoMetadata(), -1));
        kafka.javaapi.OffsetCommitRequest ocRequest = new OffsetCommitRequest(groupId, requestInfoMap, 0, clientName);
        // 提交修改偏移量的请求，并获取返回值
        kafka.javaapi.OffsetCommitResponse response = consumer.commitOffsets(ocRequest);

        // 根据返回值进行不同的操作
        if (response.hasError()) {
            short code = response.errorCode(topicAndPartition);
            if (times > this.maxRetryTimes) {
                throw new RuntimeException("Update the Offset occur exception," +
                        " the current response code is:" + code);
            }

            if (code == ErrorMapping.LeaderNotAvailableCode()) {
                // 当异常code为leader切换情况的时候，重新构建consumer对象
                // 操作步骤：先休眠一段时间，再重新构造consumer对象，最后重试
                try {
                    Thread.sleep(this.retryIntervalMillis);
                } catch (InterruptedException e) {
                    // nothings
                }
                PartitionMetadata metadata = this.findNewLeaderMetadata(consumer.host(),
                        topic, partitionID);
                this.validatePartitionMetadata(metadata);
                consumer = this.createSimpleConsumer(metadata.leader().host(),
                        metadata.leader().port(), clientName);
                // 重试
                consumer = updateOffset(consumer, topic, partitionID, readOffSet, groupId, clientName, times + 1);
            }

            if (code == ErrorMapping.RequestTimedOutCode()) {
                // 当异常为请求超时的时候，进行重新请求
                consumer = updateOffset(consumer, topic, partitionID, readOffSet, groupId, clientName, times + 1);
            }

            // 其他code直接抛出异常
            throw new RuntimeException("Update the Offset occur exception," +
                    " the current response code is:" + code);
        }

        // 返回修改后的consumer对象
        return consumer;
    }

    /**
     * 构建clientName根据主题名称和分区id
     *
     * @param topic
     * @param partitionID
     * @return
     */
    private String createClientName(String topic, int partitionID) {
        return "client_" + topic + "_" + partitionID;
    }

    /**
     * 根据一个老的consumer，重新创建一个consumer对象
     *
     * @param consumer
     * @param topic
     * @param partitionID
     * @return
     */
    private SimpleConsumer createNewSimpleConsumer(SimpleConsumer consumer, String topic, int partitionID) {
        // 重新获取新的leader节点
        PartitionMetadata metadata = this.findNewLeaderMetadata(consumer.host(),
                topic, partitionID);
        // 校验元数据
        this.validatePartitionMetadata(metadata);
        // 重新创建consumer的连接
        return this.createSimpleConsumer(metadata.leader().host(),
                metadata.leader().port(), consumer.clientId());
    }

    /**
     * 构建一个SimpleConsumer并返回
     *
     * @param host
     * @param port
     * @param clientName
     * @return
     */
    private SimpleConsumer createSimpleConsumer(String host, int port, String clientName) {
        return new SimpleConsumer(host, port, 100000, 64 * 1024, clientName);
    }

    /**
     * 休眠一段时间
     */
    private void sleep() {
        try {
            Thread.sleep(this.maxRetryTimes);
        } catch (InterruptedException e) {
            // nothings
        }
    }

    /**
     * 关闭对应资源
     *
     * @param consumer
     */
    private static void closeSimpleConsumer(SimpleConsumer consumer) {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (Exception e) {
                // nothings
            }
        }
    }

    /**
     * 从Kafka集群中获取指定topic的分区ID<br/>
     * 如果集群中不存在对应的topic，那么返回一个empty的集合
     *
     * @param brokers    Kafka集群连接参数，eg: {"hadoop-senior01" -> 9092, "hadoop-senior02" -> 9092}
     * @param topic      要获取ID对应的主题
     * @param soTimeout  过期时间
     * @param bufferSize 缓冲区大小
     * @param clientId   client连接ID
     * @return
     */
    public static List<Integer> fetchTopicPartitionIDs(List<KafkaBrokerInfo> brokers, String topic, int soTimeout, int bufferSize, String clientId) {
        Set<Integer> partitionIDs = new HashSet<Integer>();

        List<String> topics = Collections.singletonList(topic);

        // 连接所有的Kafka服务器，然后获取参数 ==> 遍历连接
        for (KafkaBrokerInfo broker : brokers) {
            SimpleConsumer consumer = null;

            try {
                // 构建简单消费者连接对象
                consumer = new SimpleConsumer(broker.brokerHost, broker.brokerPort, soTimeout, bufferSize, clientId);

                // 构建请求参数
                TopicMetadataRequest tmRequest = new TopicMetadataRequest(topics);

                // 发送请求
                TopicMetadataResponse response = consumer.send(tmRequest);

                // 获取返回结果
                List<TopicMetadata> metadatas = response.topicsMetadata();

                // 遍历返回结果，获取对应topic的结果值
                for (TopicMetadata metadata : metadatas) {
                    if (metadata.errorCode() == ErrorMapping.NoError()) {
                        // 没有异常的情况下才进行处理
                        if (topic.equals(metadata.topic())) {
                            // 处理当前topic对应的分区
                            for (PartitionMetadata part : metadata.partitionsMetadata()) {
                                partitionIDs.add(part.partitionId());
                            }
                            // 处理完成，结束循环
                            break;
                        }
                    }
                }
            } finally {
                // 关闭连接
                closeSimpleConsumer(consumer);
            }
        }

        // 返回结果
        return new ArrayList<Integer>(partitionIDs);
    }


}
