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

import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.groupconfig.config.IConsenterConfig;
import org.bcia.julongchain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/18
 * @company Dingxuan
 */
public class SizeFilter implements IRule {
    private IConsenterConfig support;

    public SizeFilter(IConsenterConfig support) {
        this.support = support;
    }

    public IConsenterConfig getSupport() {
        return support;
    }

    public void setSupport(IConsenterConfig support) {
        this.support = support;
    }
    @Override
    public void apply(Common.Envelope message) {
       int maxBytes= support.getBatchSize().getAbsoluteMaxBytes();
          int size=  messageByteSize(message);
        if (size>maxBytes){
            try {
                throw new ConsenterException(String.format("message payload is %d bytes and exceeds maximum allowed %d bytes", size, maxBytes));
            } catch (ConsenterException e) {
                e.printStackTrace();
            }
        }
    }

    public static int messageByteSize(Common.Envelope message){
        return message.getPayload().size()+message.getSignature().size();
    }
}
