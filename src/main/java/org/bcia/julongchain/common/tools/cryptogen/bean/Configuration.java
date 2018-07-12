/**
 * Copyright BCIA. All Rights Reserved.
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
package org.bcia.julongchain.common.tools.cryptogen.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * MSP config.yaml 数据类
 *
 * @author chenhao, yegangcheng
 * @date 2018/7/4
 * @company Excelsecu
 */
public class Configuration {
    private NodeOUs nodeOUs;

    public Configuration() {
    }

    public NodeOUs getNodeOUs() {
        return nodeOUs;
    }

    public void setNodeOUs(NodeOUs nodeOUs) {
        this.nodeOUs = nodeOUs;
    }


    public Map getPropertyMap() {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> nodeOUsMap = new HashMap<>();

        Map<String, Object> clientOUIdentifierMap = new HashMap<>();
        clientOUIdentifierMap.put("Certificate", nodeOUs.getClientOUIdentifier().getCertificate());
        clientOUIdentifierMap.put("OrganizationalUnitIdentifier", nodeOUs.getClientOUIdentifier().getOrganizationalUnitIdentifier());
        Map<String, Object> nodeOUIdentifierMap = new HashMap<>();
        nodeOUIdentifierMap.put("Certificate", nodeOUs.getNodeOUIdentifier().getCertificate());
        nodeOUIdentifierMap.put("OrganizationalUnitIdentifier", nodeOUs.getNodeOUIdentifier().getOrganizationalUnitIdentifier());

        nodeOUsMap.put("Enable", nodeOUs.getEnable());
        nodeOUsMap.put("ClientOUIdentifier", clientOUIdentifierMap);
        nodeOUsMap.put("NodeOUIdentifier", nodeOUIdentifierMap);

        map.put("NodeOUs", nodeOUsMap);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Configuration parse(Map<String, Object> map) {
        Configuration configuration = new Configuration();
        configuration.nodeOUs = new NodeOUs();
        Map<String, Object> nodeOUsMap = (Map<String, Object>) map.get("NodeOUs");
        configuration.nodeOUs.setEnable((Boolean) nodeOUsMap.get("Enable"));

        configuration.nodeOUs.setClientOUIdentifier(new OrgUnitIdentifiersConfig());
        Map<String, String> clientOUIdentifierMap = (Map<String, String>) nodeOUsMap.get("ClientOUIdentifier");
        configuration.nodeOUs.getClientOUIdentifier().setCertificate(clientOUIdentifierMap.get("Certificate"));
        configuration.nodeOUs.getClientOUIdentifier().setOrganizationalUnitIdentifier(clientOUIdentifierMap.get("OrganizationalUnitIdentifier"));

        configuration.nodeOUs.setNodeOUIdentifier(new OrgUnitIdentifiersConfig());
        Map<String, String> nodeOUIdentifierMap = (Map<String, String>) nodeOUsMap.get("NodeOUIdentifier");
        configuration.nodeOUs.getNodeOUIdentifier().setCertificate(nodeOUIdentifierMap.get("Certificate"));
        configuration.nodeOUs.getNodeOUIdentifier().setOrganizationalUnitIdentifier(nodeOUIdentifierMap.get("OrganizationalUnitIdentifier"));

        return configuration;
    }
}
