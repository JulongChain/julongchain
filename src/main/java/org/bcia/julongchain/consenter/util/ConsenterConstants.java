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
package org.bcia.julongchain.consenter.util;

/**
 * consenter中使用到的常量
 *
 * @author zhangmingyang
 * @Date: 2018/3/2
 * @company Dingxuan
 */
public class ConsenterConstants {
    public static final String VERSION = "version";
    public static final String START = "start";
    public static final String BENCHMARK = "benchmark";
    public static final String ORDERER_CONFIG = "consenter.yaml";
    public static final String SINGLETON = "Singleton";
    public static final String KAFKA = "kafka";
    public static final String COMSUMER = "comumer";
    public static final String MAX_READS = "maxReads";
    public static final String TOPIC = "topic";

    public static final String PARTITION_ID = "partitionID";
    public static final String SERVER = "server";
    public static final String MAX_RETRY_TIMES = "maxRetryTimes";
    public static final String RETRY_INTERVAL_MILLIS = "retryIntervalMillis";
    public static final String ZOOKEEPER = "zookeeper";

    public static final int MSGVERSION = 0;
    public static final int EPOCH = 0;

    public static final String TIMEWONDW = "timeWindow";

    public static final String ISCONFIG = "isConfig";
    public static final String GROUPHEADER = "groupHeader";
    public static final String CHAINSUPPORT = "chainsupport";

    public static final int METADATA_SIZE = 4;
}
