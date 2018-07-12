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
package org.bcia.julongchain.msp.config;

import java.util.Map;

/**
 * 类描述
 *
 * @author zhangmingyang
 * @date 2018/06/29
 * @company Dingxuan
 */
public class Config {
    public static final String Config_FILE_PATH = "config.yaml";

    public Map<String,String> organizationalUnitIdentifiers;
    public NodeOus nodeOUs;
    public static class NodeOus{
        public  String isEnable;
        public Map<String,String> clientOUIdentifier;
        public Map<String,String>  peerOUIdentifier;

        public String getIsEnable() {
            return isEnable;
        }

        public void setIsEnable(String isEnable) {
            this.isEnable = isEnable;
        }

        public Map<String, String> getClientOUIdentifier() {
            return clientOUIdentifier;
        }

        public void setClientOUIdentifier(Map<String, String> clientOUIdentifier) {
            this.clientOUIdentifier = clientOUIdentifier;
        }

        public Map<String, String> getPeerOUIdentifier() {
            return peerOUIdentifier;
        }

        public void setPeerOUIdentifier(Map<String, String> peerOUIdentifier) {
            this.peerOUIdentifier = peerOUIdentifier;
        }
    }

    public Map<String, String> getOrganizationalUnitIdentifiers() {
        return organizationalUnitIdentifiers;
    }

    public void setOrganizationalUnitIdentifiers(Map<String, String> organizationalUnitIdentifiers) {
        this.organizationalUnitIdentifiers = organizationalUnitIdentifiers;
    }

    public NodeOus getNodeOUs() {
        return nodeOUs;
    }

    public void setNodeOUs(NodeOus nodeOUs) {
        this.nodeOUs = nodeOUs;
    }
}
