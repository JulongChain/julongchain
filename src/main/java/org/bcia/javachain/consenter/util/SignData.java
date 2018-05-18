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
package org.bcia.javachain.consenter.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ConsenterException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.protos.common.Common;

/**
 * @author zhangmingyang
 * @Date: 2018/5/9
 * @company Dingxuan
 */
public class SignData {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SignData.class);
    private byte[] data;

    private byte[] identity;

    private byte[] signature;

    public SignData(byte[] data, byte[] identity, byte[] signature) {
        this.data = data;
        this.identity = identity;
        this.signature = signature;
    }

    public static SignData asSignedData(Common.Envelope envelope) throws ConsenterException, InvalidProtocolBufferException {
        if (envelope == null) {
            log.error("No signatures for nil Envelope");
            throw new ConsenterException("No signatures for nil Envelope");
        }
        Common.Payload payload = Common.Payload.parseFrom(envelope.getPayload());
        if (payload.getHeader() == null) {
            throw new ConsenterException("Missing Header");
        }
        Common.SignatureHeader signatureHeader = Common.SignatureHeader.parseFrom(payload.getHeader().getSignatureHeader());

        signatureHeader.toByteArray();

        return new SignData(payload.toByteArray(), signatureHeader.getCreator().toByteArray(), envelope.getSignature().toByteArray());
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getIdentity() {
        return identity;
    }

    public byte[] getSignature() {
        return signature;
    }
}
