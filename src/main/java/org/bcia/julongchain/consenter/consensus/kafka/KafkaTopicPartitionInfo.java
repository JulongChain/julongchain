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

/**
 * 类描述
 *
 * @author yangdong
 * @date 2018/3/19
 * @company Shudun
 */

public class KafkaTopicPartitionInfo {
    // 主题名称
    public final String topic;
    // 分区id
    public final int partitionID;

    /**
     * 构造函数
     *
     * @param topic       主题名称
     * @param partitionID 分区id
     */
    public KafkaTopicPartitionInfo(String topic, int partitionID) {
        this.topic = topic;
        this.partitionID = partitionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){return true;}
        if (o == null || getClass() != o.getClass()){return false;}

        KafkaTopicPartitionInfo that = (KafkaTopicPartitionInfo) o;

        if (partitionID != that.partitionID){ return false;}
        return topic != null ? topic.equals(that.topic) : that.topic == null;

    }

    @Override
    public int hashCode() {
        int result = topic != null ? topic.hashCode() : 0;
        result = 31 * result + partitionID;
        return result;
    }
}

