/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.julongchain.common.policycheck.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *  用在PolicyParser里，将策略解析到多叉树数中
 * @author yuanjun
 * @date 12/06/18
 * @company Aisino
 */
public class PolicyNode {
    public StringBuffer msg = new StringBuffer("");
    public PolicyNode parent;
    public List<PolicyNode> sons = new ArrayList<PolicyNode>();
    public boolean value = false;

    public String getMsg() {
        return msg.toString();
    }
    public void setMsg(StringBuffer msg){
        this.msg = msg;
    }
    @Override
    public String toString() {
        return msg.toString() +
                ",flag="+value+
                ",sons="+sons.size();
    }
    public String n2str() {
        if ("or".equals(msg.toString().trim().toLowerCase()) || "and".equals(msg.toString().trim().toLowerCase()) ) {
            String nstr = "";
            for (PolicyNode node : sons) {
                nstr += node.n2str()+",";
            }
            return "outof(ID,"+(msg.toString().toLowerCase().equals("or")?"1":sons.size())+","
                    +nstr.substring(0, nstr.length()-1)
                    + ")";
        }
        return msg.toString();
    }
}
