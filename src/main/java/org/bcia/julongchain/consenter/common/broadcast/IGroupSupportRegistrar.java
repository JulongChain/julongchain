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
package org.bcia.julongchain.consenter.common.broadcast;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.protos.common.Common;

import java.util.Map;

/**
 * IGroupSupportRegistrar接口
 * 定义broadcastGroupSupport方法,实现对消息的分类
 * @author zhangmingyang
 * @Date: 2018/5/8
 * @company Dingxuan
 */
public interface IGroupSupportRegistrar {
    /**
     * 消息分类处理
     * @param msg
     * @return
     * @throws InvalidProtocolBufferException
     */
    Map<String,Object> broadcastGroupSupport(Common.Envelope msg) throws InvalidProtocolBufferException;
}
