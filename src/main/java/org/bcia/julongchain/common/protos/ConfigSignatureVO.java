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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/29
 * @company Dingxuan
 */
public class ConfigSignatureVO implements IProtoVO<Configtx.ConfigSignature> {
    private Common.SignatureHeader signatureHeader;
    private ByteString signature;

    @Override
    public void parseFrom(Configtx.ConfigSignature configSignature) throws InvalidProtocolBufferException,
            ValidateException {
        ValidateUtils.isNotNull(configSignature, "configSignature can not be null");
        ValidateUtils.isNotNull(configSignature.getSignatureHeader(), "configSignature.getSignatureHeader can not " +
                "be null");

        this.signatureHeader = Common.SignatureHeader.parseFrom(configSignature.getSignatureHeader());
        this.signature = configSignature.getSignature();
    }

    @Override
    public Configtx.ConfigSignature toProto() {
        Configtx.ConfigSignature.Builder builder = Configtx.ConfigSignature.newBuilder();

        builder.setSignatureHeader(signatureHeader.toByteString());
        builder.setSignature(signature);

        return builder.build();
    }

    public Common.SignatureHeader getSignatureHeader() {
        return signatureHeader;
    }

    public ByteString getSignature() {
        return signature;
    }
}
