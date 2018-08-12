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
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class TransactionActionVO implements IProtoVO<TransactionPackage.TransactionAction> {
    private Common.SignatureHeader signatureHeader;
    private SmartContractActionPayloadVO smartContractActionPayloadVO;

    @Override
    public void parseFrom(TransactionPackage.TransactionAction transactionAction) throws
            InvalidProtocolBufferException, ValidateException {
        ValidateUtils.isNotNull(transactionAction, "transactionAction can not be null");
        ValidateUtils.isNotNull(transactionAction.getHeader(), "transactionAction.header can not be null");
        ValidateUtils.isNotNull(transactionAction.getPayload(), "transactionAction.payload can not be null");

        this.signatureHeader = Common.SignatureHeader.parseFrom(transactionAction.getHeader());

        this.smartContractActionPayloadVO = new SmartContractActionPayloadVO();
        TransactionPackage.SmartContractActionPayload smartContractActionPayload = TransactionPackage
                .SmartContractActionPayload.parseFrom(transactionAction.getPayload());
        this.smartContractActionPayloadVO.parseFrom(smartContractActionPayload);
    }

    @Override
    public TransactionPackage.TransactionAction toProto() {
        TransactionPackage.TransactionAction.Builder builder = TransactionPackage.TransactionAction.newBuilder();
        builder.setHeader(signatureHeader.toByteString());
        builder.setPayload(smartContractActionPayloadVO.toProto().toByteString());

        return builder.build();
    }

    public Common.SignatureHeader getSignatureHeader() {
        return signatureHeader;
    }

    public SmartContractActionPayloadVO getSmartContractActionPayloadVO() {
        return smartContractActionPayloadVO;
    }
}
