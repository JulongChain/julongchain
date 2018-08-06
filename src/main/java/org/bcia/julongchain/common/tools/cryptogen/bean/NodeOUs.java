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

/**
 * config.yaml 节点数据类
 *
 * @author chenhao, yegangcheng
 * @date 2018/7/4
 * @company Excelsecu
 */
public class NodeOUs {
    private boolean enable;
    private OrgUnitIdentifiersConfig clientOUIdentifier;
    private OrgUnitIdentifiersConfig nodeOUIdentifier;

    public NodeOUs() {

    }

    public OrgUnitIdentifiersConfig getClientOUIdentifier() {
        return clientOUIdentifier;
    }

    public OrgUnitIdentifiersConfig getNodeOUIdentifier() {
        return nodeOUIdentifier;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setClientOUIdentifier(OrgUnitIdentifiersConfig clientOUIdentifier) {
        this.clientOUIdentifier = clientOUIdentifier;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setNodeOUIdentifier(OrgUnitIdentifiersConfig nodeOUIdentifier) {
        this.nodeOUIdentifier = nodeOUIdentifier;
    }
}
