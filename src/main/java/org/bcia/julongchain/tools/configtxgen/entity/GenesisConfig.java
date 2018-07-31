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
package org.bcia.julongchain.tools.configtxgen.entity;

import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.PolicyConstant;

import java.util.Map;

/**
 * 创世配置对象(与configtx.yaml文件匹配)
 *
 * @author zhouhui
 * @date 2018/3/8
 * @company Dingxuan
 */
public class GenesisConfig {
    private static JavaChainLog log = JavaChainLogFactory.getLog(GenesisConfig.class);

    /**
     * 默认对象
     */
    private static final GenesisConfig DEFAULT_INSTANCE;
    /**
     * 当前对象对应的yaml文件路径
     */
    public static final String CONFIGTX_FILE_PATH = "configtx.yaml";

    private static final String DEFAULT_CONSENTER_TYPE = "Singleton";
    private static final String DEFAULT_CONSENTER_ADDRESS = "127.0.0.1:7050";
    private static final long DEFAULT_BATCH_TIMEOUT = 2L;

    private static final int DEFAULT_MAX_MESSAGE_COUNT = 10;
    private static final int DEFAULT_MAX_ABSOLUTE_BYTE = 10 * 1024 * 1024;
    private static final int DEFAULT_MAX_PREFERRED_BYTE = 512 * 1024;

    private static final String DEFAULT_KAFKA_ADDRESS = "127.0.0.1:9092";

    private static final String DEFAULT_MSG_TYPE = "csp";
    public static final String DEFAULT_ADMIN_PRINCIPAL = "Role.ADMIN";

    private static final String DEFAULT_MOD_POLICY = PolicyConstant.GROUP_APP_ADMINS;

    static {
        //给默认对象赋值
        Consenter defaultConsenter = new Consenter();
        defaultConsenter.setConsenterType(DEFAULT_CONSENTER_TYPE);
        defaultConsenter.setAddresses(new String[]{DEFAULT_CONSENTER_ADDRESS});
        defaultConsenter.setBatchTimeout(DEFAULT_BATCH_TIMEOUT);

        BatchSize defaultBatchSize = new BatchSize();
        defaultBatchSize.setMaxMessageCount(DEFAULT_MAX_MESSAGE_COUNT);
        defaultBatchSize.setAbsoluteMaxBytes(DEFAULT_MAX_ABSOLUTE_BYTE);
        defaultBatchSize.setPreferredMaxBytes(DEFAULT_MAX_PREFERRED_BYTE);
        defaultConsenter.setBatchSize(defaultBatchSize);

        Kafka defaultKafka = new Kafka();
        defaultKafka.setBrokers(new String[]{DEFAULT_KAFKA_ADDRESS});
        defaultConsenter.setKafka(defaultKafka);

        DEFAULT_INSTANCE = new GenesisConfig();
        DEFAULT_INSTANCE.setConsenter(defaultConsenter);
    }

    private Map<String, Profile> profiles;
    private Organization[] organizations;
    private Application application;
    private Consenter consenter;
    private Map<String, Map<String, Boolean>> capabilities;
    private Resources resources;

    /**
     * 补全实例中其他属性
     */
    public void completeInstance() {
        if (organizations != null && organizations.length > 0) {
            for (Organization org : organizations) {
                org.completeInstance();
            }
        }

        if (consenter != null) {
            consenter.completeInstance();
        }
    }

    /**
     * 获取补全的Profile
     */
    public Profile getCompletedProfile(String profileName) {
        if (profiles != null) {
            Profile profile = profiles.get(profileName);
            if (profile != null) {
                profile.completeInstance();
                return profile;
            }
        }

        return null;
    }

    public static class Profile {
        private String consortium;
        private Application application;
        private Consenter consenter;
        private Map<String, Consortium> consortiums;
        private Map<String, Boolean> capabilities;

        public void completeInstance() {
            if (application != null) {
                application.completeInstance();
            }

            if (consenter != null) {
                consenter.completeInstance();
            }

            if (consortiums != null && !consortiums.isEmpty()) {
                for (Consortium consortium : consortiums.values()) {
                    consortium.completeInstance();
                }
            }
        }

        public String getConsortium() {
            return consortium;
        }

        public void setConsortium(String consortium) {
            this.consortium = consortium;
        }

        public Application getApplication() {
            return application;
        }

        public void setApplication(Application application) {
            this.application = application;
        }

        public Consenter getConsenter() {
            return consenter;
        }

        public void setConsenter(Consenter consenter) {
            this.consenter = consenter;
        }

        public Map<String, Consortium> getConsortiums() {
            return consortiums;
        }

        public void setConsortiums(Map<String, Consortium> consortiums) {
            this.consortiums = consortiums;
        }

        public Map<String, Boolean> getCapabilities() {
            return capabilities;
        }

        public void setCapabilities(Map<String, Boolean> capabilities) {
            this.capabilities = capabilities;
        }
    }

    public static class Organization {
        private String name;
        private String id;
        private String mspDir;
        private String mspType;
        private String adminPrincipal;
        private AnchorNode[] anchorNodes;

        /**
         * 补齐实例中空缺的属性值
         */
        public void completeInstance() {
            if (StringUtils.isBlank(mspType)) {
                mspType = DEFAULT_MSG_TYPE;
                log.info("Missing mspType, use default: " + mspType);
            }

            if (StringUtils.isBlank(adminPrincipal)) {
                adminPrincipal = DEFAULT_ADMIN_PRINCIPAL;
                log.info("Missing adminPrincipal, use default: " + adminPrincipal);
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMspDir() {
            return mspDir;
        }

        public void setMspDir(String mspDir) {
            this.mspDir = mspDir;
        }

        public String getMspType() {
            return mspType;
        }

        public void setMspType(String mspType) {
            this.mspType = mspType;
        }

        public String getAdminPrincipal() {
            return adminPrincipal;
        }

        public void setAdminPrincipal(String adminPrincipal) {
            this.adminPrincipal = adminPrincipal;
        }

        public AnchorNode[] getAnchorNodes() {
            return anchorNodes;
        }

        public void setAnchorNodes(AnchorNode[] anchorNodes) {
            this.anchorNodes = anchorNodes;
        }
    }

    public static class Application {
        private Organization[] organizations;
        private Map<String, Boolean> capabilities;
        private Resources resources;

        /**
         * 补齐实例中空缺的属性值
         */
        public void completeInstance() {
            if (organizations != null && organizations.length > 0) {
                for (Organization org : organizations) {
                    org.completeInstance();
                }
            }

            if (resources != null) {
                resources.completeInstance();
            }
        }

        public Organization[] getOrganizations() {
            return organizations;
        }

        public void setOrganizations(Organization[] organizations) {
            this.organizations = organizations;
        }

        public Map<String, Boolean> getCapabilities() {
            return capabilities;
        }

        public void setCapabilities(Map<String, Boolean> capabilities) {
            this.capabilities = capabilities;
        }

        public Resources getResources() {
            return resources;
        }

        public void setResources(Resources resources) {
            this.resources = resources;
        }
    }

    public static class Consenter {
        private String consenterType;
        private String[] addresses;
        private long batchTimeout;
        private BatchSize batchSize;
        private Kafka kafka;
        private Organization[] organizations;
        private long maxGroups;
        private Map<String, Boolean> capabilities;

        /**
         * 补齐实例中空缺的属性值
         */
        public void completeInstance() {
            if (StringUtils.isBlank(consenterType)) {
                consenterType = DEFAULT_CONSENTER_TYPE;
                log.info("Missing mspType, use default: " + consenterType);
            }

            if (addresses == null || addresses.length < 1) {
                addresses = new String[]{DEFAULT_CONSENTER_ADDRESS};
            }

            if (batchTimeout == 0L) {
                batchTimeout = DEFAULT_BATCH_TIMEOUT;
            }

            if (batchSize.getMaxMessageCount() == 0) {
                batchSize.setMaxMessageCount(DEFAULT_MAX_MESSAGE_COUNT);
            }

            if (batchSize.getAbsoluteMaxBytes() == 0) {
                batchSize.setAbsoluteMaxBytes(DEFAULT_MAX_ABSOLUTE_BYTE);
            }

            if (batchSize.getPreferredMaxBytes() == 0) {
                batchSize.setPreferredMaxBytes(DEFAULT_MAX_PREFERRED_BYTE);
            }

            if (batchSize.getPreferredMaxBytes() == 0) {
                batchSize.setPreferredMaxBytes(DEFAULT_MAX_PREFERRED_BYTE);
            }

            if (kafka == null || kafka.getBrokers() == null || kafka.getBrokers().length < 1) {
                kafka = new Kafka();
                kafka.setBrokers(new String[]{DEFAULT_KAFKA_ADDRESS});
            }

            if (organizations != null && organizations.length > 0) {
                for (Organization org : organizations) {
                    org.completeInstance();
                }
            }
        }

        public String getConsenterType() {
            return consenterType;
        }

        public void setConsenterType(String consenterType) {
            this.consenterType = consenterType;
        }

        public String[] getAddresses() {
            return addresses;
        }

        public void setAddresses(String[] addresses) {
            this.addresses = addresses;
        }

        public long getBatchTimeout() {
            return batchTimeout;
        }

        public void setBatchTimeout(long batchTimeout) {
            this.batchTimeout = batchTimeout;
        }

        public BatchSize getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(BatchSize batchSize) {
            this.batchSize = batchSize;
        }

        public Kafka getKafka() {
            return kafka;
        }

        public void setKafka(Kafka kafka) {
            this.kafka = kafka;
        }

        public Organization[] getOrganizations() {
            return organizations;
        }

        public void setOrganizations(Organization[] organizations) {
            this.organizations = organizations;
        }

        public long getMaxGroups() {
            return maxGroups;
        }

        public void setMaxGroups(long maxGroups) {
            this.maxGroups = maxGroups;
        }

        public Map<String, Boolean> getCapabilities() {
            return capabilities;
        }

        public void setCapabilities(Map<String, Boolean> capabilities) {
            this.capabilities = capabilities;
        }
    }

    public static class Resources {
        private String defaultModPolicy;

        /**
         * 补齐实例中空缺的属性值
         */
        public void completeInstance() {
            if (StringUtils.isBlank(defaultModPolicy)) {
                defaultModPolicy = DEFAULT_MOD_POLICY;
            }
        }

        public String getDefaultModPolicy() {
            return defaultModPolicy;
        }

        public void setDefaultModPolicy(String defaultModPolicy) {
            this.defaultModPolicy = defaultModPolicy;
        }
    }

    public static class Consortium {
        private Organization[] organizations;

        /**
         * 补齐实例中空缺的属性值
         */
        public void completeInstance() {
            if (organizations != null && organizations.length > 0) {
                for (Organization org : organizations) {
                    org.completeInstance();
                }
            }
        }

        public Organization[] getOrganizations() {
            return organizations;
        }

        public void setOrganizations(Organization[] organizations) {
            this.organizations = organizations;
        }
    }

    public static class AnchorNode {
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class BatchSize {
        private int maxMessageCount;
        private int absoluteMaxBytes;
        private int preferredMaxBytes;

        public int getMaxMessageCount() {
            return maxMessageCount;
        }

        public void setMaxMessageCount(int maxMessageCount) {
            this.maxMessageCount = maxMessageCount;
        }

        public int getAbsoluteMaxBytes() {
            return absoluteMaxBytes;
        }

        public void setAbsoluteMaxBytes(int absoluteMaxBytes) {
            this.absoluteMaxBytes = absoluteMaxBytes;
        }

        public int getPreferredMaxBytes() {
            return preferredMaxBytes;
        }

        public void setPreferredMaxBytes(int preferredMaxBytes) {
            this.preferredMaxBytes = preferredMaxBytes;
        }
    }

    public static class Kafka {
        private String[] brokers;

        public String[] getBrokers() {
            return brokers;
        }

        public void setBrokers(String[] brokers) {
            this.brokers = brokers;
        }
    }

    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, Profile> profiles) {
        this.profiles = profiles;
    }

    public Organization[] getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Organization[] organizations) {
        this.organizations = organizations;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Consenter getConsenter() {
        return consenter;
    }

    public void setConsenter(Consenter consenter) {
        this.consenter = consenter;
    }

    public Map<String, Map<String, Boolean>> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Map<String, Boolean>> capabilities) {
        this.capabilities = capabilities;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }
}