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
package org.bcia.javachain.consenter.common.blockcutter;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.consenter.consensus.IReceiver;
import org.bcia.javachain.consenter.entity.BatchesMes;
import org.bcia.javachain.protos.common.Common;
import org.springframework.stereotype.Component;

/**
 * @author zhangmingyang
 * @Date: 2018/3/15
 * @company Dingxuan
 */
@Component
public class BlockCutter implements IReceiver {
    private static JavaChainLog log = JavaChainLogFactory.getLog(BlockCutter.class);
    //BlockCutter blockCutter=new BlockCutter();
    @Override
    public BatchesMes ordered(Common.Envelope msg) {
        log.info("this is blockCutter's ordered method!!!");
       // blockCutter.cut();
        BatchesMes mes=new BatchesMes();
        return  mes;
    }

    @Override
    public Common.Envelope[] cut() {
        log.info("this is blockCutter'cut method!!");
        return new Common.Envelope[0];
    }
}
