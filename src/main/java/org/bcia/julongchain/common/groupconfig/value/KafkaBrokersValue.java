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
package org.bcia.julongchain.common.groupconfig.value;

import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.protos.consenter.Configuration;

/**
 * Kafka服务器配置项
 *
 * @author zhouhui
 * @date 2018/5/11
 * @company Dingxuan
 */
public class KafkaBrokersValue extends StandardConfigValue {
    public KafkaBrokersValue(String[] brokers) {
        this.key = GroupConfigConstant.KAFKA_BROKERS;

        Configuration.KafkaBrokers.Builder kafkaBrokersBuilder = Configuration.KafkaBrokers.newBuilder();
        if (brokers != null && brokers.length > 0) {
            for (String broker : brokers) {
                kafkaBrokersBuilder.addBrokers(broker);
            }
        }

        this.value = kafkaBrokersBuilder.build();
    }
}
