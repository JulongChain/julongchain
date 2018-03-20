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

import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Conf;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.Consenter;
import org.bcia.javachain.consenter.common.blockcutter.BlockCutter;
import org.bcia.javachain.consenter.common.multigroup.MultiGroup;
import org.bcia.javachain.consenter.consensus.IChain;
import org.bcia.javachain.consenter.entity.BatchesMes;
import org.bcia.javachain.consenter.entity.ConfigMsg;
import org.bcia.javachain.protos.common.Common;
import org.springframework.beans.factory.annotation.Autowired;
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

    MultiGroup multiGroup=new MultiGroup();

    //BroadCastProcessor broadCastProcessor=new BroadCastProcessor();
    @Override
    public void order(Common.Envelope envelope, long configSeq) {

        BatchesMes batchesMes= blockCutter.ordered(envelope);
        //循环遍历batchesMes,
        log.info("循环遍历batchesMes");

    }

    @Override
    public void confgigure(Common.Envelope envelope, long configSeq) {
        //返回配置消息实体
       // ConfigMsg configMsg=  broadCastProcessor.processConfigMsg(envelope);
        //fabric中先将配置消息外的所有交易先打包成区块后,然后将配置区块单独成块
        Common.Envelope[]  batch=  blockCutter.cut();
        Common.Block block=multiGroup.createNextBlock(batch);
        multiGroup.writeBlock(block,null );

        //最后将配置消息单独打包区块
//        Common.Envelope[] message={configMsg.getConfig()};
//        Common.Block configblock= multiGroup.createNextBlock(message);
//        multiGroup.writeConfigBlock(configblock,null);
        log.info("this is confgigure");
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
        log.info("进入处理配置更新消息方法体");
        ConfigMsg configMsg=new ConfigMsg();
        configMsg.setConfig(env);
        return configMsg;
    }

    @Override
    public ConfigMsg processConfigMsg(Common.Envelope env) {
        log.info("进入处理配置消息方法体");
        return  processConfigUpdateMsg(env);
    }
}
