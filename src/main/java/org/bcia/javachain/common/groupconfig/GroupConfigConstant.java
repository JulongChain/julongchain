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
package org.bcia.javachain.common.groupconfig;

/**
 * 群组配置常量
 *
 * @author zhouhui
 * @date 2018/3/9
 * @company Dingxuan
 */
public class GroupConfigConstant {

    public static final String POLICY_READERS = "Readers";

    public static final String POLICY_WRITERS = "Writers";

    public static final String POLICY_ADMINS = "Admins";

    public static final String CAPABILITIES = "Capabilities";

    public static final String MSP = "MSP";

    public static final String GROUP = "Group";

    public static final String APPLICATION = "Application";

    public static final String CONSENTER = "Consenter";

    public static final String CONSORTIUMS = "Consortiums";

    public static final String ANCHOR_NODES = "AnchorNodes";

    public static final String CONSORTIUM = "Consortium";

    public static final String HASHING_ALGORITHM = "HashingAlgorithm";

    public static final String BLOCK_DATA_HASHING_STRUCTURE = "BlockDataHashingStructure";

    public static final String CONSENTER_ADDRESSES = "ConsenterAddresses";

    public static final String CONSENSUS_TYPE = "ConsensusType";
    public static final String CONSENSUS_TYPE_SINGLETON = "singleton";
    public static final String CONSENSUS_TYPE_KAFKA = "kafka";

    public static final String BATCH_SIZE = "BatchSize";

    public static final String BATCH_TIMEOUT = "BatchTimeout";

    public static final String GROUP_RESTRICTIONS = "GroupRestrictions";

    public static final String KAFKA_BROKERS = "KafkaBrokers";

    public static final String GROUP_CREATION_POLICY = "GroupCreationPolicy";

    public static final String APP_PRIVATE_DATA_EXPERIMENTAL = "appPrivateDataExperimental";
    public static final String APP_RESOURCE_TREE_EXPERIMENTAL = "appResourceTreeExperimental";
    public static final String APP_FORBID_DUPLICATE_TXID = "appForbidDuplicateTxid";
    public static final String APP_VALIDATION = "appValidation";

    public static final String CONSENTER_PREDICTABLE_GROUP_TEMPLATE = "consenterPredictableGroupTemplate";
    public static final String CONSENTER_RESUBMISSION = "consenterResubmission";
    public static final String CONSENTER_EXPIRATION = "consenterExpiration";

    public static final int DEFAULT_BLOCK_DATA_HASHING_WIDTH = Integer.MAX_VALUE;

    public static final String CONSENTER_ADMINS_POLICY_NAME = "/Group/Consenter/Admins";
    public static final String DEFAULT_HASHING_ALGORITHM_NAME = "SM3";

    public static final String BLOCK_VALIDATION_POLICY = "BlockValidation";


}
