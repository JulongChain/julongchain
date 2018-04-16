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
package org.bcia.javachain.core.node;

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

    private Logging logging;
    private Node node;
    private VM vm;
    private SmartContract smartContract;
    private Ledger ledger;
    private Metrics metrics;

    public static class Logging {
        private String level;
        private String cauthdsl;
        private String gossip;
        private String grpc;
        private String ledger;
        private String msp;
        private String policies;
        private String nodeGossip;
        private String format;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getCauthdsl() {
            return cauthdsl;
        }

        public void setCauthdsl(String cauthdsl) {
            this.cauthdsl = cauthdsl;
        }

        public String getGossip() {
            return gossip;
        }

        public void setGossip(String gossip) {
            this.gossip = gossip;
        }

        public String getGrpc() {
            return grpc;
        }

        public void setGrpc(String grpc) {
            this.grpc = grpc;
        }

        public String getLedger() {
            return ledger;
        }

        public void setLedger(String ledger) {
            this.ledger = ledger;
        }

        public String getMsp() {
            return msp;
        }

        public void setMsp(String msp) {
            this.msp = msp;
        }

        public String getPolicies() {
            return policies;
        }

        public void setPolicies(String policies) {
            this.policies = policies;
        }

        public String getNodeGossip() {
            return nodeGossip;
        }

        public void setNodeGossip(String nodeGossip) {
            this.nodeGossip = nodeGossip;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    public static class Node {
        private String id;
        private String networkId;
        private String listenAddress;
        private String address;
        private String addressAutoDetect;
        private String gomaxprocs;
        private KeepAlive keepalive;
        private Gossip gossip;
        private Events events;
        private Tls tls;
        private Authentication authentication;
        private String fileSystemPath;
        private CSP csp;
        private String mspConfigPath;
        private String localMspId;
        private DeliveryClient deliveryclient;
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

        public String getGomaxprocs() {
            return gomaxprocs;
        }

        public void setGomaxprocs(String gomaxprocs) {
            this.gomaxprocs = gomaxprocs;
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

        public Authentication getAuthentication() {
            return authentication;
        }

        public void setAuthentication(Authentication authentication) {
            this.authentication = authentication;
        }

        public String getFileSystemPath() {
            return fileSystemPath;
        }

        public void setFileSystemPath(String fileSystemPath) {
            this.fileSystemPath = fileSystemPath;
        }

        public CSP getCsp() {
            return csp;
        }

        public void setCsp(CSP csp) {
            this.csp = csp;
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

        public DeliveryClient getDeliveryclient() {
            return deliveryclient;
        }

        public void setDeliveryclient(DeliveryClient deliveryclient) {
            this.deliveryclient = deliveryclient;
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
    }

    public static class SmartContract {
        private Id id;
        private String builder;
        private String pull;
        private String endpoint;
//        private String endpoint;
//        private String endpoint;
//        private String endpoint;
//        private String endpoint;
//        private String endpoint;
//        private String endpoint;
//        private String endpoint;
    }

    public static class Ledger {
    }

    public static class Metrics {
    }

    private static class KeepAlive {
    }

    private static class Gossip {
    }

    private static class Events {
    }

    private static class Tls {
    }

    private static class Authentication {
    }

    private static class CSP {
    }

    private static class DeliveryClient {
    }

    private static class Profile {
    }

    private static class Handlers {
    }

    private static class Docker {
    }

    private static class Id {
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
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
}
