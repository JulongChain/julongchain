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
package org.bcia.julongchain.consenter.entity;

import org.bcia.julongchain.protos.common.Common;

/**
 * 消息类,对接收消息进行统一封装
 *
 * @author zhangmingyang
 * @Date: 2018/6/11
 * @company Dingxuan
 */
public class Message {
    private long configSeq;
    private Common.Envelope message;

    public Message(long configSeq, Common.Envelope message) {
        this.configSeq = configSeq;
        this.message = message;
    }

    public long getConfigSeq() {
        return configSeq;
    }

    public Common.Envelope getMessage() {
        return message;
    }
}
