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
package org.bcia.javachain.consenter.common.broadcast;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.Consenter;
import org.bcia.javachain.consenter.common.blockcutter.BlockCutter;
import org.bcia.javachain.consenter.consensus.IChain;
import org.bcia.javachain.consenter.entity.BatchesMes;
import org.bcia.javachain.consenter.entity.ConfigMsg;
import org.bcia.javachain.protos.common.Common;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * broadcast服务对消息的处理
 *
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
@Component
public class BroadCastProcessor implements IBroadcastChannelSupport {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BroadCastProcessor.class);
    BlockCutter blockCutter=new BlockCutter();
    @Override
    public void order(Common.Envelope envelope, long configSeq) {

        BatchesMes batchesMes= blockCutter.ordered(envelope);
        //循环遍历batchesMes,
        log.info("循环遍历batchesMes");

    }

    @Override
    public void confgigure(Common.Envelope envelope, long configSeq) {

    }

    @Override
    public boolean classfiyMsg(Common.GroupHeader chdr) {
        if (Common.HeaderType.CONFIG_VALUE == chdr.getType()) {
            return  true;
        }else if(Common.HeaderType.CONFIG_UPDATE_VALUE == chdr.getType()){
            return  true;
        }else if(Common.HeaderType.CONSENTER_TRANSACTION_VALUE == chdr.getType()){
            return  true;
        }else {
            return false;
        }
    }

    @Override
    public long processNormalMsg(Common.Envelope env) {
        log.info("处理普通消息，并返回long类型的配置序列");
        return 0;
    }

    @Override
    public ConfigMsg processConfigUpdateMsg(Common.Envelope env) {

        return null;
    }

    @Override
    public Object processConfigMsg(Common.Envelope env) {
        return null;
    }
}
