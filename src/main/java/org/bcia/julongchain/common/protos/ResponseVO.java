/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.julongchain.common.protos;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;

/**
 * 提案响应中的响应对象
 *
 * @author zhouhui
 * @date 2018/09/30
 * @company Dingxuan
 */
public class ResponseVO implements IProtoVO<ProposalResponsePackage.Response> {
    private int status;
    private String message;


    @Override
    public void parseFrom(ProposalResponsePackage.Response response) throws InvalidProtocolBufferException,
            ValidateException {


    }

    @Override
    public ProposalResponsePackage.Response toProto() {
        return null;
    }
}
