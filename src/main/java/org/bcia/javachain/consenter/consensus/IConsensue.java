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
    public Chain HandleChain(ConsenterSupport consenterSupport, Common.Metadata metadata);


    public interface Chain {
        public void Order(Common.Envelope env, long configSeq);
        public void Configure(Common.Envelope config,long configSeq);

        /**
         * 未定义方法Errored() <-chan struct{}
         */
        public void Start();
        public void Halt();

    }
    public interface ConsenterSupport {
        /**
         * crypto.LocalSigner
         */
        public interface LocalSigner {
            public Common.SignatureHeader NewSignatureHeader();

            public byte[] Sign(byte[] message);
        }

        /**
         * msgprocessor.Processor 接口
         */
        public interface Processor {
            public int ClassfiyMsg(Common.GroupHeader chdr);

            public long ProcessNormalMsg(Common.Envelope env);

            /**
             * 返回env 和 configSeq ,之后转换为实体
             */
            public Object ProcessConfigUpdateMsg(Common.Envelope env);

            /**
             * 返回类型为(*cb.Envelope, uint64, error) 之后做作补充
             */
            public Object ProcessConfigMsg(Common.Envelope env);
        }


        public Receiver BlockCutter();
        public Orderer SharedConfig();
        public Common.Block CreateNextBlock(Common.Envelope[] messages);
        public void  WriteBlock(Common.Block block,byte[] encodedMetadataValue);
        public void WriteConfig(Common.Block block,byte[] encodedMetadataValue);
        public long Sequence();
        public String ChainID();
        public long Height();



    }




    public interface Receiver {
        public void Ordered(Common.Envelope[][] messagesBatches, boolean pending);

        public Common.Envelope[] Cut();
    }

    public interface Orderer {
        public String ConsensusType();

        public Configuration.BatchSize BatchSize();

        /**
         * 缺失 time.Duration
         */

        public long MaxChannelsCount();

        public String[] KafkaBrokers();

         public Map<String,Org> Organizations();
       public  OrdererCapabilities Capabilities();

    }
    public interface Org{
        public String Name();
        public String MSPID();
    }

    public interface  OrdererCapabilities{
        public boolean SetChannelModPolicyDuringCreate();
        public boolean Resubmission();
        public void Supported();
    }

}
