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

import org.bcia.julongchain.common.exception.ConsenterException;
import org.bcia.julongchain.protos.common.Common;

/**
 * 链接口,实现该接口扩展排序插件
 * @author zhangmingyang
 * @Date: 2018/3/7
 * @company Dingxuan
 */
public interface IChain {

    /**
     * 普通消息排序
     * @param env
     * @param configSeq
     */
    void order(Common.Envelope env, long configSeq);

    /**
     * 配置消息排序
     * @param config
     * @param configSeq
     */
    void configure(Common.Envelope config, long configSeq);

    /**
     * 排序前准备
     * @throws ConsenterException
     */
    void waitReady() throws ConsenterException;

    /**
     * 排序插件启动线程
     */
    void start();

    /**
     *暂停释放为此链分配的资源
     */
    void halt();
}
