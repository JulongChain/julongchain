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
package org.bcia.julongchain.consenter.consensus;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.consenter.entity.ConfigMsg;
import org.bcia.julongchain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/3/7
 * @company Dingxuan
 */
public interface IProcessor {
    /**
     * 判断是否为配置消息,如果是配置消息返回true;否则返回false
     * @param chdr
     * @return
     */
    boolean classfiyMsg(Common.GroupHeader chdr);

    /**
     * 根据当前配置检查消息的有效性,fa
     * @param env
     * @return
     */
     long processNormalMsg(Common.Envelope env) throws InvalidProtocolBufferException;

    /**
     * 将尝试将配置更新应用于当前配置，如果成功返回结果配置消息和configSeq，则从中计算配置
     * 如果配置更新消息无效，则返回错误
     * @param env
     * @return
     */
    ConfigMsg processConfigUpdateMsg(Common.Envelope env) throws ConsenterException, InvalidProtocolBufferException, ValidateException;

    /**
     * 接收'ORDERER_TX'或'CONFIG`类型的消息，解压嵌入其中的ConfigUpdate信封，
     * 并调用ProcessConfigUpdateMsg生成与原始消息类型相同的新Config消息.
     *
     * @param env
     * @return
     */
    ConfigMsg processConfigMsg(Common.Envelope env) throws ConsenterException, InvalidProtocolBufferException, ValidateException, PolicyException;
}
