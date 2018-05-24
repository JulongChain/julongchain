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
package org.bcia.javachain.core.commiter;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.core.ledger.util.TxValidationFlags;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.TransactionPackage;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/23
 * @company Dingxuan
 */
public class CommitterValidator implements ICommitterValidator {
    private ICommitterSupport committerSupport;
    private IVsscValidator vsscValidator;

    public CommitterValidator(ICommitterSupport committerSupport) {
        this.committerSupport = committerSupport;

        this.vsscValidator = new VsscValidator(committerSupport);
    }

    @Override
    public void validate(Common.Block block) throws ValidateException {
        ValidateUtils.isNotNull(block, "block can not be null");
        ValidateUtils.isNotNull(block.getData(), "block.data can not be null");

        TxValidationFlags txValidationFlags = new TxValidationFlags(block.getData().getDataCount());

        BlockValidationResult result = new BlockValidationResult();
        if (block.getData().getDataList() != null && block.getData().getDataCount() > 0) {
            for (int i = 0; i < block.getData().getDataCount(); i++) {
                BlockValidationRequest request = new BlockValidationRequest(block, block.getData().getData(i)
                        .toByteArray(), i, this);
                validateTx(request, result);
            }
        }


    }

    private void validateTx(BlockValidationRequest request, BlockValidationResult result) {
        if (request.getData() == null) {
            result.setTxIndex(request.getTxIndex());
            return;
        }

        Common.Envelope envelope = null;
        try {
            envelope = Common.Envelope.parseFrom(request.getData());
        } catch (InvalidProtocolBufferException e) {
            result.setTxIndex(request.getTxIndex());
            result.setTxValidationCode(TransactionPackage.TxValidationCode.INVALID_OTHER_REASON);
            return;
        }




    }

    private boolean chainExists(String chain) {
        //TODO:yao shi xian
        return true;
    }
}
