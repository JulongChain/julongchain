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
import org.bcia.javachain.protos.consenter.Configuration;
import org.springframework.core.annotation.Order;
import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/3/1 *
 * @company Dingxuan
 */
public interface IConsensue {
     IChain handleChain(IConsenterSupport consenterSupport, Common.Metadata metadata) throws IllegalAccessException, InstantiationException;


//    public interface Chain {
//        public void order(Common.Envelope env, long configSeq);
//        public void configure(Common.Envelope config,long configSeq);
//
//        /**
//         * 未定义方法Errored() <-chan struct{}
//         */
//        public void start();
//        public void halt();
//
//    }
//    public interface IConsenterSupport {
//        /**
//         * crypto.LocalSigner
//         */
//        public interface LocalSigner {
//            public Common.SignatureHeader newSignatureHeader();
//
//            public byte[] sign(byte[] message);
//        }
//
//        /**
//         * msgprocessor.Processor 接口
//         */
//        public interface processor {
//            public int classfiyMsg(Common.GroupHeader chdr);
//
//            public long processNormalMsg(Common.Envelope env);
//
//            /**
//             * 返回env 和 configSeq ,之后转换为实体
//             */
//            public Object processConfigUpdateMsg(Common.Envelope env);
//
//            /**
//             * 返回类型为(*cb.Envelope, uint64, error) 之后做作补充
//             */
//            public Object processConfigMsg(Common.Envelope env);
//        }
//
//
//        public receiver blockCutter();
//        public Orderer sharedConfig();
//        public Common.Block createNextBlock(Common.Envelope[] messages);
//        public void  writeBlock(Common.Block block,byte[] encodedMetadataValue);
//        public void writeConfig(Common.Block block,byte[] encodedMetadataValue);
//        public long sequence();
//        public String chainID();
//        public long height();
//
//
//    }
//
//
//
//
//    public interface receiver {
//        public void ordered(Common.Envelope[][] messagesBatches, boolean pending);
//
//        public Common.Envelope[] Cut();
//    }
//
//    public interface Orderer {
//        public String ConsensusType();
//
//        public Configuration.BatchSize BatchSize();
//
//        /**
//         * 缺失 time.Duration
//         */
//
//        public long maxChannelsCount();
//
//        public String[] KafkaBrokers();
//
//         public Map<String,Org> Organizations();
//       public  OrdererCapabilities Capabilities();
//
//    }
//    public interface Org{
//        public String name();
//        public String mspid();
//    }
//
//    public interface  OrdererCapabilities{
//        public boolean setChannelModPolicyDuringCreate();
//        public boolean resubmission();
//        public void supported();
//    }

}
