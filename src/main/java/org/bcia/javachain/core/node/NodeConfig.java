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
 * @author
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
    }

    public static class Node {
        private String address;
        private String addressAutoDetect;

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
    }

    public static class SmartContract {
    }

    public static class Ledger {
    }

    public static class Metrics {
    }
}
