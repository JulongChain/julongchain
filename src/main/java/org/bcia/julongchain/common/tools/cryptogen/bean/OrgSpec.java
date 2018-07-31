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

import java.util.ArrayList;
import java.util.List;

/**
 * crypto-config.yaml 节点配置数据类
 *
 * @author chenhao, liuxifeng
 * @date 2018/4/20
 * @company Excelsecu
 */
public class OrgSpec {

    private String name = "";
    private String domain = "";
    private boolean enableNodeOUs = false;
    private NodeSpec ca = new NodeSpec();
    private NodeTemplate template = new NodeTemplate();
    private List<NodeSpec> specs = new ArrayList<>();
    private UserSpec users = new UserSpec();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isEnableNodeOUs() {
        return enableNodeOUs;
    }

    public void setEnableNodeOUs(boolean enableNodeOUs) {
        this.enableNodeOUs = enableNodeOUs;
    }

    public NodeSpec getCa() {
        return ca;
    }

    public void setCa(NodeSpec ca) {
        this.ca = ca;
    }

    public NodeTemplate getTemplate() {
        return template;
    }

    public void setTemplate(NodeTemplate template) {
        this.template = template;
    }

    public List<NodeSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<NodeSpec> specs) {
        this.specs = specs;
    }

    public UserSpec getUsers() {
        return users;
    }

    public void setUsers(UserSpec users) {
        this.users = users;
    }
}
