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
package org.bcia.javachain.consenter.consensus.singleton;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.consensus.IChain;
import org.bcia.javachain.consenter.consensus.IConsensue;
import org.bcia.javachain.consenter.consensus.IConsenterSupport;
import org.bcia.javachain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/3/7
 * @company Dingxuan
 */
public class Singleton implements IChain, IConsensue{
    private static JavaChainLog log = JavaChainLogFactory.getLog(Singleton.class);
    private IConsenterSupport support;

    private Message sendChan;

    class Message {
        long configSeq;
        Common.Envelope normalMsg;
        Common.Envelope configMsg;
    }

    @Override
    public void order(Common.Envelope env, long configSeq) {

    }

    @Override
    public void configure(Common.Envelope config, long configSeq) {

    }

    @Override
    public void waitReady() {

    }

    @Override
    public void start() {
        main();
    }

    @Override
    public void halt() {

    }

    public static void main()  {
        for (; ; ) {
            //获取configMsg,判断是否为配置消息
        }
    }

    @Override
    public IChain handleChain(IConsenterSupport consenterSupport, Common.Metadata metadata) {
        return new Singleton(consenterSupport);
    }

    public Singleton(IConsenterSupport support) {
        this.support = support;
    }
}
