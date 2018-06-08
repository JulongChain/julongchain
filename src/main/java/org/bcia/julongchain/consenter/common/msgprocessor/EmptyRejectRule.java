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
package org.bcia.julongchain.consenter.common.msgprocessor;

import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/25
 * @company Dingxuan
 */
public class EmptyRejectRule implements IRule {
    private static JavaChainLog log = JavaChainLogFactory.getLog(EmptyRejectRule.class);
    private IRule emptyRejectRule;

    public EmptyRejectRule() {
    }

    public EmptyRejectRule(IRule emptyRejectRule) {
        this.emptyRejectRule = emptyRejectRule;
    }

    @Override
    public void apply(Common.Envelope message) {
        if(message.getPayload()==null){
            try {
                throw  new ValidateException("Message was empty");
            } catch (ValidateException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public IRule getEmptyRejectRule() {
        return emptyRejectRule;
    }

    public void setEmptyRejectRule(IRule emptyRejectRule) {
        this.emptyRejectRule = emptyRejectRule;
    }
}
