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

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.protos.common.Common;

/**
 * 规则接口
 *
 * @author zhangmingyang
 * @Date: 2018/5/9
 * @company Dingxuan
 */
public interface IRule {
    /**
     * Apply将规则应用于给定的Envelope，或者成功或返回错误
     * @param message
     * @throws ConsenterException
     * @throws InvalidProtocolBufferException
     */
    void apply(Common.Envelope message) throws ConsenterException, InvalidProtocolBufferException;
}
