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
package org.bcia.javachain.consenter.consensus;

import org.bcia.javachain.protos.common.Common;

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
     long processNormalMsg(Common.Envelope env);

    /**
     * 返回env 和 configSeq ,之后转换为实体
     */
     Object processConfigUpdateMsg(Common.Envelope env);

    /**
     * 返回类型为(*cb.Envelope, uint64, error) 之后做作补充
     */
     Object processConfigMsg(Common.Envelope env);
}
