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

    public static class Node {
        private String id;
        private String networkId;
        private String listenAddress;
        private String consenterAddress;
        private String smartContractListenAddress;
        private KeepAlive keepalive;
        private Gossip gossip;
        private Events events;
        private Tls tls;
        private String fileSystemPath;

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

        public String getSmartContractListenAddress() {
            return smartContractListenAddress;
        }

        public void setSmartContractListenAddress(String smartContractListenAddress) {
            this.smartContractListenAddress = smartContractListenAddress;
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

        public String getFileSystemPath() {
            return fileSystemPath;
        }

        public void setFileSystemPath(String fileSystemPath) {
            this.fileSystemPath = fileSystemPath;
        }
    }

    public static class VM {
        private String endpoint;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
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
        private String executetimeout;
        private String keepalive;
        private Map<String, String> system;
        private Map<String, String> systemPlugins;
        private Map<String, String> logging;

        public String getExecutetimeout() {
            return executetimeout;
        }

        public void setExecutetimeout(String executetimeout) {
            this.executetimeout = executetimeout;
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
        private State state;
        private Map<String, Boolean> history;

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
