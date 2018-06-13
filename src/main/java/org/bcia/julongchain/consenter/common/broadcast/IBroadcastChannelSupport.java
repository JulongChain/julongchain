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

import org.bcia.julongchain.consenter.consensus.IProcessor;
import org.bcia.julongchain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/3/12
 * @company Dingxuan
 */
public interface IBroadcastChannelSupport extends IProcessor {
    /**
     * 排序方法,
     * @param envelope 信封格式数据
     * @param configSeq 配置序列
     */
  void   order(Common.Envelope envelope,long configSeq);
    /**
     * 排序方法,
     * @param envelope 信封格式数据
     * @param configSeq 配置序列
     */
  void   confgigure(Common.Envelope envelope,long configSeq);
}
