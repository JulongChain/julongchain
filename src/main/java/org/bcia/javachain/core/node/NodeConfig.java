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
}
