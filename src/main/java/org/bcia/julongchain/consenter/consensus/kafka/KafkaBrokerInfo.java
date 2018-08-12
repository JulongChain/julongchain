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
 * @date 2018/6/7
 * @company Shudun
 */

public class KafkaBrokerInfo {
    // 主机名
    public  String brokerHost;
    // 端口号
    public  int brokerPort;

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public int getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(int brokerPort) {
        this.brokerPort = brokerPort;
    }

    /**
     * 构造方法
     *
     * @param brokerHost Kafka服务器主机或者IP地址
     * @param brokerPort 端口号
     */
    public KafkaBrokerInfo(String brokerHost, int brokerPort) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
    }
    /**
     * 构造方法， 使用默认端口号9092进行构造
     *
     * @param brokerHost
     */
    public KafkaBrokerInfo(String brokerHost) {
        this(brokerHost, 9092);
    }
}


