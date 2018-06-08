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
package org.bcia.javachain.consenter.common.msgprocessor;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ConsenterException;
import org.bcia.javachain.common.exception.PolicyException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/25
 * @company Dingxuan
 */
public class RuleSet implements IRule {
    private IRule[] rules;

    @Override
    public void apply(Common.Envelope message) throws ConsenterException, InvalidProtocolBufferException {
        for (IRule rule : rules) {
            rule.apply(message);
        }
    }

    public IRule[] getRules() {
        return rules;
    }

    public void setRules(IRule[] rules) {
        this.rules = rules;
    }

    public RuleSet(IRule[] rules) {
        this.rules = rules;
    }

}
