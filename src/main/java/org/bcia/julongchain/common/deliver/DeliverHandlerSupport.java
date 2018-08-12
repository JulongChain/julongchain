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
package org.bcia.julongchain.common.deliver;

import com.google.protobuf.Message;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;

/**
 * @author zhangmingyang
 * @Date: 2018/6/4
 * @company Dingxuan
 */
public class DeliverHandlerSupport implements  IDeliverHandlerSupport{
    @Override
    public Message createStatusReply(Common.Status status) {
        Ab.DeliverResponse.Builder deliverResponseBuilder= Ab.DeliverResponse.newBuilder();
        deliverResponseBuilder.setStatus(status);
        return deliverResponseBuilder.build();
    }

    @Override
    public Message createBlockReply(Common.Block block) {
        Ab.DeliverResponse.Builder deliverResponseBuilder= Ab.DeliverResponse.newBuilder();
        deliverResponseBuilder.setBlock(block);
        return deliverResponseBuilder.build();
    }
}
