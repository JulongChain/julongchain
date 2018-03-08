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

import org.bcia.javachain.consenter.consensus.IChain;
import org.bcia.javachain.protos.common.Common;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author zhangmingyang
 * @Date: 2018/3/8
 * @company Dingxuan
 */
@Repository
public class BroadCastProcessor implements IChain {
    @Override
    public void order(Common.Envelope env, long configSeq) {
        System.out.println("this is order method");
    }

    @Override
    public void configure(Common.Envelope config, long configSeq) {

    }

    @Override
    public void start() {

    }

    @Override
    public void halt() {

    }
}
