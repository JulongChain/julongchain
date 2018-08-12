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
package org.bcia.julongchain.core.node;

import java.util.List;
import java.util.Map;

/**
 * 节点配置对象（对应node.yaml文件）
 *
 * @author zhouhui
 * @date 2018/3/15
 * @company Dingxuan
 */
public class NodeConfig {
    /**
     * 当前对象对应的yaml文件路径
     */
    public static final String NODECONFIG_FILE_PATH = "node.yaml";

    private Node node;
    private VM vm;
    private SmartContract smartContract;
    private Ledger ledger;
    private Metrics metrics;

    public static class Node {
        private String id;
        private String networkId;
        private String listenAddress;
        private String consenterAddress;
        private String address;
        private String addressAutoDetect;
        private KeepAlive keepalive;
        private Gossip gossip;
        private Events events;
        private Tls tls;
        private int authenticationTimeWindow;
        private String fileSystemPath;
        private String mspConfigPath;
        private String localMspId;
        private Map<String, Integer> deliveryClient;
        private String localMspType;
        private Profile profile;
        private Handlers handlers;
        private String validatorPoolSize;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNetworkId() {
            return networkId;
        }

        public void setNetworkId(String networkId) {
            this.networkId = networkId;
        }

        public String getListenAddress() {
            return listenAddress;
        }

        public void setListenAddress(String listenAddress) {
            this.listenAddress = listenAddress;
        }

        public String getConsenterAddress() {
            return consenterAddress;
        }

        public void setConsenterAddress(String consenterAddress) {
            this.consenterAddress = consenterAddress;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddressAutoDetect() {
            return addressAutoDetect;
        }

        public void setAddressAutoDetect(String addressAutoDetect) {
            this.addressAutoDetect = addressAutoDetect;
        }

        public KeepAlive getKeepalive() {
            return keepalive;
        }

        public void setKeepalive(KeepAlive keepalive) {
            this.keepalive = keepalive;
        }

        public Gossip getGossip() {
            return gossip;
        }

        public void setGossip(Gossip gossip) {
            this.gossip = gossip;
        }

        public Events getEvents() {
            return events;
        }

        public void setEvents(Events events) {
            this.events = events;
        }

        public Tls getTls() {
            return tls;
        }

        public void setTls(Tls tls) {
            this.tls = tls;
        }

        public int getAuthenticationTimeWindow() {
            return authenticationTimeWindow;
        }

        public void setAuthenticationTimeWindow(int authenticationTimeWindow) {
            this.authenticationTimeWindow = authenticationTimeWindow;
        }

        public String getFileSystemPath() {
            return fileSystemPath;
        }

        public void setFileSystemPath(String fileSystemPath) {
            this.fileSystemPath = fileSystemPath;
        }

        public String getMspConfigPath() {
            return mspConfigPath;
        }

        public void setMspConfigPath(String mspConfigPath) {
            this.mspConfigPath = mspConfigPath;
        }

        public String getLocalMspId() {
            return localMspId;
        }

        public void setLocalMspId(String localMspId) {
            this.localMspId = localMspId;
        }

        public Map<String, Integer> getDeliveryClient() {
            return deliveryClient;
        }

        public void setDeliveryClient(Map<String, Integer> deliveryClient) {
            this.deliveryClient = deliveryClient;
        }

        public String getLocalMspType() {
            return localMspType;
        }

        public void setLocalMspType(String localMspType) {
            this.localMspType = localMspType;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public Handlers getHandlers() {
            return handlers;
        }

        public void setHandlers(Handlers handlers) {
            this.handlers = handlers;
        }

        public String getValidatorPoolSize() {
            return validatorPoolSize;
        }

        public void setValidatorPoolSize(String validatorPoolSize) {
            this.validatorPoolSize = validatorPoolSize;
        }
    }

    public static class VM {
        private String endpoint;
        private Docker docker;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public Docker getDocker() {
            return docker;
        }

        public void setDocker(Docker docker) {
            this.docker = docker;
        }
    }

    /**
     *
     */
    public static class SmartContract {
        private String instantiatePath;
        private String dockerFile;
        private String coreNodeAddress;
        private String coreNodeAddressPort;
        private Map<String, String> id;
        private String builder;
        private boolean pull;
        private Map<String, String> golang;
        private Map<String, String> car;
        private Map<String, String> java;
        private Map<String, String> node;
        private String startuptimeout;
        private String executetimeout;
        private String mode;
        private String keepalive;
        private Map<String, String> system;
        private Map<String, String> systemPlugins;
        private Map<String, String> logging;

        public Map<String, String> getId() {
            return id;
        }

        public void setId(Map<String, String> id) {
            this.id = id;
        }

        public String getBuilder() {
            return builder;
        }

        public void setBuilder(String builder) {
            this.builder = builder;
        }

        public boolean isPull() {
            return pull;
        }

        public void setPull(boolean pull) {
            this.pull = pull;
        }

        public Map<String, String> getGolang() {
            return golang;
        }

        public void setGolang(Map<String, String> golang) {
            this.golang = golang;
        }

        public Map<String, String> getCar() {
            return car;
        }

        public void setCar(Map<String, String> car) {
            this.car = car;
        }

        public Map<String, String> getJava() {
            return java;
        }

        public void setJava(Map<String, String> java) {
            this.java = java;
        }

        public Map<String, String> getNode() {
            return node;
        }

        public void setNode(Map<String, String> node) {
            this.node = node;
        }

        public String getStartuptimeout() {
            return startuptimeout;
        }

        public void setStartuptimeout(String startuptimeout) {
            this.startuptimeout = startuptimeout;
        }

        public String getExecutetimeout() {
            return executetimeout;
        }

        public void setExecutetimeout(String executetimeout) {
            this.executetimeout = executetimeout;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getKeepalive() {
            return keepalive;
        }

        public void setKeepalive(String keepalive) {
            this.keepalive = keepalive;
        }

        public Map<String, String> getSystem() {
            return system;
        }

        public void setSystem(Map<String, String> system) {
            this.system = system;
        }

        public Map<String, String> getSystemPlugins() {
            return systemPlugins;
        }

        public void setSystemPlugins(Map<String, String> systemPlugins) {
            this.systemPlugins = systemPlugins;
        }

        public Map<String, String> getLogging() {
            return logging;
        }

        public void setLogging(Map<String, String> logging) {
            this.logging = logging;
        }

        public String getInstantiatePath() {
            return instantiatePath;
        }

        public void setInstantiatePath(String instantiatePath) {
            this.instantiatePath = instantiatePath;
        }

        public String getCoreNodeAddress() {
            return coreNodeAddress;
        }

        public void setCoreNodeAddress(String coreNodeAddress) {
            this.coreNodeAddress = coreNodeAddress;
        }

        public String getDockerFile() {
            return dockerFile;
        }

        public void setDockerFile(String dockerFile) {
            this.dockerFile = dockerFile;
        }

        public String getCoreNodeAddressPort() {
            return coreNodeAddressPort;
        }

        public void setCoreNodeAddressPort(String coreNodeAddressPort) {
            this.coreNodeAddressPort = coreNodeAddressPort;
        }
    }

    public static class Ledger {
        private String blockchain;
        private State state;
        private Map<String, Boolean> history;

        public String getBlockchain() {
            return blockchain;
        }

        public void setBlockchain(String blockchain) {
            this.blockchain = blockchain;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public Map<String, Boolean> getHistory() {
            return history;
        }

        public void setHistory(Map<String, Boolean> history) {
            this.history = history;
        }
    }

    public static class Metrics {
        private boolean enabled;
        private String reporter;
        private int interval;
        private Map<String, String> statsReporter;
        private Map<String, String> promReporter;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getReporter() {
            return reporter;
        }

        public void setReporter(String reporter) {
            this.reporter = reporter;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public Map<String, String> getStatsReporter() {
            return statsReporter;
        }

        public void setStatsReporter(Map<String, String> statsReporter) {
            this.statsReporter = statsReporter;
        }

        public Map<String, String> getPromReporter() {
            return promReporter;
        }

        public void setPromReporter(Map<String, String> promReporter) {
            this.promReporter = promReporter;
        }
    }

    public static class KeepAlive {
        private int minInterval;
        private int clientInterval;
        private int clientTimeout;
        private int deliveryClientInterval;
        private int deliveryClientTimeout;

        public int getMinInterval() {
            return minInterval;
        }

        public void setMinInterval(int minInterval) {
            this.minInterval = minInterval;
        }

        public int getClientInterval() {
            return clientInterval;
        }

        public void setClientInterval(int clientInterval) {
            this.clientInterval = clientInterval;
        }

        public int getClientTimeout() {
            return clientTimeout;
        }

        public void setClientTimeout(int clientTimeout) {
            this.clientTimeout = clientTimeout;
        }

        public int getDeliveryClientInterval() {
            return deliveryClientInterval;
        }

        public void setDeliveryClientInterval(int deliveryClientInterval) {
            this.deliveryClientInterval = deliveryClientInterval;
        }

        public int getDeliveryClientTimeout() {
            return deliveryClientTimeout;
        }

        public void setDeliveryClientTimeout(int deliveryClientTimeout) {
            this.deliveryClientTimeout = deliveryClientTimeout;
        }
    }

    public static class Gossip {
        private String commiterAddress;
        private String consenterAddress;
        private String bootstrap;
        private boolean useLeaderElection;
        private boolean orgLeader;
        private String endpoint;
        private int maxBlockCountToStore;
        private int maxPropagationBurstLatency;
        private int maxPropagationBurstSize;
        private int propagateIterations;
        private int propagateNodeNum;
        private int pullInterval;
        private int pullNodeNum;
        private int requestStateInfoInterval;
        private int publishStateInfoInterval;
        private int stateInfoRetentionInterval;
        private int publishCertPeriod;
        private boolean skipBlockVerification;
        private int dialTimeout;
        private int connTimeout;
        private int recvBuffSize;
        private int sendBuffSize;
        private int digestWaitTime;
        private int requestWaitTime;
        private int responseWaitTime;
        private int aliveTimeInterval;
        private int aliveExpirationTimeout;
        private int reconnectInterval;
        private String externalEndpoint;
        private Map<String, Integer> election;
        private Map<String, Integer> privateData;

        public String getBootstrap() {
            return bootstrap;
        }

        public void setBootstrap(String bootstrap) {
            this.bootstrap = bootstrap;
        }

        public boolean isUseLeaderElection() {
            return useLeaderElection;
        }

        public void setUseLeaderElection(boolean useLeaderElection) {
            this.useLeaderElection = useLeaderElection;
        }

        public boolean isOrgLeader() {
            return orgLeader;
        }

        public void setOrgLeader(boolean orgLeader) {
            this.orgLeader = orgLeader;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public int getMaxBlockCountToStore() {
            return maxBlockCountToStore;
        }

        public void setMaxBlockCountToStore(int maxBlockCountToStore) {
            this.maxBlockCountToStore = maxBlockCountToStore;
        }

        public int getMaxPropagationBurstLatency() {
            return maxPropagationBurstLatency;
        }

        public void setMaxPropagationBurstLatency(int maxPropagationBurstLatency) {
            this.maxPropagationBurstLatency = maxPropagationBurstLatency;
        }

        public int getMaxPropagationBurstSize() {
            return maxPropagationBurstSize;
        }

        public void setMaxPropagationBurstSize(int maxPropagationBurstSize) {
            this.maxPropagationBurstSize = maxPropagationBurstSize;
        }

        public int getPropagateIterations() {
            return propagateIterations;
        }

        public void setPropagateIterations(int propagateIterations) {
            this.propagateIterations = propagateIterations;
        }

        public int getPropagateNodeNum() {
            return propagateNodeNum;
        }

        public void setPropagateNodeNum(int propagateNodeNum) {
            this.propagateNodeNum = propagateNodeNum;
        }

        public int getPullInterval() {
            return pullInterval;
        }

        public void setPullInterval(int pullInterval) {
            this.pullInterval = pullInterval;
        }

        public int getPullNodeNum() {
            return pullNodeNum;
        }

        public void setPullNodeNum(int pullNodeNum) {
            this.pullNodeNum = pullNodeNum;
        }

        public int getRequestStateInfoInterval() {
            return requestStateInfoInterval;
        }

        public void setRequestStateInfoInterval(int requestStateInfoInterval) {
            this.requestStateInfoInterval = requestStateInfoInterval;
        }

        public int getPublishStateInfoInterval() {
            return publishStateInfoInterval;
        }

        public void setPublishStateInfoInterval(int publishStateInfoInterval) {
            this.publishStateInfoInterval = publishStateInfoInterval;
        }

        public int getStateInfoRetentionInterval() {
            return stateInfoRetentionInterval;
        }

        public void setStateInfoRetentionInterval(int stateInfoRetentionInterval) {
            this.stateInfoRetentionInterval = stateInfoRetentionInterval;
        }

        public int getPublishCertPeriod() {
            return publishCertPeriod;
        }

        public void setPublishCertPeriod(int publishCertPeriod) {
            this.publishCertPeriod = publishCertPeriod;
        }

        public boolean isSkipBlockVerification() {
            return skipBlockVerification;
        }

        public void setSkipBlockVerification(boolean skipBlockVerification) {
            this.skipBlockVerification = skipBlockVerification;
        }

        public int getDialTimeout() {
            return dialTimeout;
        }

        public void setDialTimeout(int dialTimeout) {
            this.dialTimeout = dialTimeout;
        }

        public int getConnTimeout() {
            return connTimeout;
        }

        public void setConnTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
        }

        public int getRecvBuffSize() {
            return recvBuffSize;
        }

        public void setRecvBuffSize(int recvBuffSize) {
            this.recvBuffSize = recvBuffSize;
        }

        public int getSendBuffSize() {
            return sendBuffSize;
        }

        public void setSendBuffSize(int sendBuffSize) {
            this.sendBuffSize = sendBuffSize;
        }

        public int getDigestWaitTime() {
            return digestWaitTime;
        }

        public void setDigestWaitTime(int digestWaitTime) {
            this.digestWaitTime = digestWaitTime;
        }

        public int getRequestWaitTime() {
            return requestWaitTime;
        }

        public void setRequestWaitTime(int requestWaitTime) {
            this.requestWaitTime = requestWaitTime;
        }

        public int getResponseWaitTime() {
            return responseWaitTime;
        }

        public void setResponseWaitTime(int responseWaitTime) {
            this.responseWaitTime = responseWaitTime;
        }

        public int getAliveTimeInterval() {
            return aliveTimeInterval;
        }

        public void setAliveTimeInterval(int aliveTimeInterval) {
            this.aliveTimeInterval = aliveTimeInterval;
        }

        public int getAliveExpirationTimeout() {
            return aliveExpirationTimeout;
        }

        public void setAliveExpirationTimeout(int aliveExpirationTimeout) {
            this.aliveExpirationTimeout = aliveExpirationTimeout;
        }

        public int getReconnectInterval() {
            return reconnectInterval;
        }

        public void setReconnectInterval(int reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
        }

        public String getExternalEndpoint() {
            return externalEndpoint;
        }

        public void setExternalEndpoint(String externalEndpoint) {
            this.externalEndpoint = externalEndpoint;
        }

        public Map<String, Integer> getElection() {
            return election;
        }

        public void setElection(Map<String, Integer> election) {
            this.election = election;
        }

        public Map<String, Integer> getPrivateData() {
            return privateData;
        }

        public void setPrivateData(Map<String, Integer> privateData) {
            this.privateData = privateData;
        }

        public String getCommiterAddress() {
            return commiterAddress;
        }

        public void setCommiterAddress(String commiterAddress) {
            this.commiterAddress = commiterAddress;
        }

        public String getConsenterAddress() {
            return consenterAddress;
        }

        public void setConsenterAddress(String consenterAddress) {
            this.consenterAddress = consenterAddress;
        }
    }

    public static class Events {
        private String address;
        private int buffersize;
        private int timeout;
        private int timewindow;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getBuffersize() {
            return buffersize;
        }

        public void setBuffersize(int buffersize) {
            this.buffersize = buffersize;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getTimewindow() {
            return timewindow;
        }

        public void setTimewindow(int timewindow) {
            this.timewindow = timewindow;
        }
    }

    public static class Tls {
        private boolean enabled;
        private boolean clientAuthRequired;
        private String certFile;
        private String keyFile;
        private String rootCertFile;
        private String clientRootCAsFiles;
        private String clientKeyFile;
        private String clientCertFile;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isClientAuthRequired() {
            return clientAuthRequired;
        }

        public void setClientAuthRequired(boolean clientAuthRequired) {
            this.clientAuthRequired = clientAuthRequired;
        }

        public String getCertFile() {
            return certFile;
        }

        public void setCertFile(String certFile) {
            this.certFile = certFile;
        }

        public String getKeyFile() {
            return keyFile;
        }

        public void setKeyFile(String keyFile) {
            this.keyFile = keyFile;
        }

        public String getRootCertFile() {
            return rootCertFile;
        }

        public void setRootCertFile(String rootCertFile) {
            this.rootCertFile = rootCertFile;
        }

        public String getClientRootCAsFiles() {
            return clientRootCAsFiles;
        }

        public void setClientRootCAsFiles(String clientRootCAsFiles) {
            this.clientRootCAsFiles = clientRootCAsFiles;
        }

        public String getClientKeyFile() {
            return clientKeyFile;
        }

        public void setClientKeyFile(String clientKeyFile) {
            this.clientKeyFile = clientKeyFile;
        }

        public String getClientCertFile() {
            return clientCertFile;
        }

        public void setClientCertFile(String clientCertFile) {
            this.clientCertFile = clientCertFile;
        }
    }

    public static class Profile {
        private boolean enabled;
        private String listenAddress;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getListenAddress() {
            return listenAddress;
        }

        public void setListenAddress(String listenAddress) {
            this.listenAddress = listenAddress;
        }
    }

    public static class Handlers {
        private List<Map<String, String>> authFilters;
        private List<Map<String, String>> decorators;

        public List<Map<String, String>> getAuthFilters() {
            return authFilters;
        }

        public void setAuthFilters(List<Map<String, String>> authFilters) {
            this.authFilters = authFilters;
        }

        public List<Map<String, String>> getDecorators() {
            return decorators;
        }

        public void setDecorators(List<Map<String, String>> decorators) {
            this.decorators = decorators;
        }
    }

    public static class Docker {
        private Map<String, String> tls;
        private boolean attachStdout;
        private HostConfig hostConfig;

        public Map<String, String> getTls() {
            return tls;
        }

        public void setTls(Map<String, String> tls) {
            this.tls = tls;
        }

        public boolean isAttachStdout() {
            return attachStdout;
        }

        public void setAttachStdout(boolean attachStdout) {
            this.attachStdout = attachStdout;
        }

        public HostConfig getHostConfig() {
            return hostConfig;
        }

        public void setHostConfig(HostConfig hostConfig) {
            this.hostConfig = hostConfig;
        }
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public VM getVm() {
        return vm;
    }

    public void setVm(VM vm) {
        this.vm = vm;
    }

    public SmartContract getSmartContract() {
        return smartContract;
    }

    public void setSmartContract(SmartContract smartContract) {
        this.smartContract = smartContract;
    }

    public Ledger getLedger() {
        return ledger;
    }

    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    public static class HostConfig {
        private String NetworkMode;
        private String dns;
        private Map<String, String> logConfig;
        private long memory;

        public String getNetworkMode() {
            return NetworkMode;
        }

        public void setNetworkMode(String networkMode) {
            NetworkMode = networkMode;
        }

        public String getDns() {
            return dns;
        }

        public void setDns(String dns) {
            this.dns = dns;
        }

        public Map<String, String> getLogConfig() {
            return logConfig;
        }

        public void setLogConfig(Map<String, String> logConfig) {
            this.logConfig = logConfig;
        }

        public long getMemory() {
            return memory;
        }

        public void setMemory(long memory) {
            this.memory = memory;
        }
    }

    public static class State {
        private String stateDatabase;
        private Map<String, String> couchDBConfig;

        public String getStateDatabase() {
            return stateDatabase;
        }

        public void setStateDatabase(String stateDatabase) {
            this.stateDatabase = stateDatabase;
        }

        public Map<String, String> getCouchDBConfig() {
            return couchDBConfig;
        }

        public void setCouchDBConfig(Map<String, String> couchDBConfig) {
            this.couchDBConfig = couchDBConfig;
        }
    }


}
