package org.bcia.julongchain.consenter.consensus.kafka;

import java.util.ArrayList;
import java.util.List;

public class JavaKafkaSimpleConsumerAPITest {

    public static void main(String[] args) {
        KafkaSimpleConsumer example = new KafkaSimpleConsumer();

        long maxReads = 300;
        String topic = "test2";
        int partitionID = 0;

        KafkaTopicPartitionInfo topicPartitionInfo = new KafkaTopicPartitionInfo(topic, partitionID);
        List<KafkaBrokerInfo> seeds = new ArrayList<KafkaBrokerInfo>();
        seeds.add(new KafkaBrokerInfo("10.0.20.91", 9092));

        try {
            example.run(maxReads, topicPartitionInfo, seeds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取该topic所属的所有分区ID列表
        System.out.println(example.fetchTopicPartitionIDs(seeds, topic, 100000, 64 * 1024, "client-id"));
    }
}
