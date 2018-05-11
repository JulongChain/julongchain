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

package org.bcia.javachain.common.policycheck;

import org.bcia.javachain.common.policycheck.common.IsSignaturePolicy_Type;

import java.util.List;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 28/04/18
 * @company Aisino
 */
public class SignaturePolicy_NOutOf extends IsSignaturePolicy_Type {
    //public static final String N = "protobuf:'varint,1,opt,name=n' json:'n,omitempty'";
    //public static final String Rule = "protobuf:'bytes,2,rep,name=rules' json:'rules,omitempty'";
    private int N;
    private List<IsSignaturePolicy_Type> Rules;

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public List<IsSignaturePolicy_Type> getRules() {
        return Rules;
    }

    public void setRules(List<IsSignaturePolicy_Type> rules) {
        Rules = rules;
    }
}