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
import org.bcia.julongchain.protos.node.TransactionPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/26
 * @company Dingxuan
 */
public class TransactionVO implements IProtoVO<TransactionPackage.Transaction> {
    private List<TransactionActionVO> transactionActionVOList;

    @Override
    public void parseFrom(TransactionPackage.Transaction transaction) throws InvalidProtocolBufferException,
            ValidateException {
        ValidateUtils.isNotNull(transaction, "transaction can not be null");
        ValidateUtils.isNotNull(transaction.getActionsList(), "transaction.action can not be null");

        this.transactionActionVOList = new ArrayList<>();
        if (transaction.getActionsList() != null) {
            for (TransactionPackage.TransactionAction action : transaction.getActionsList()) {
                TransactionActionVO transactionActionVO = new TransactionActionVO();
                transactionActionVO.parseFrom(action);
                this.transactionActionVOList.add(transactionActionVO);
            }
        }
    }

    @Override
    public TransactionPackage.Transaction toProto() {
        TransactionPackage.Transaction.Builder builder = TransactionPackage.Transaction.newBuilder();
        if (transactionActionVOList != null) {
            for (TransactionActionVO transactionActionVO : transactionActionVOList) {
                builder.addActions(transactionActionVO.toProto());
            }
        }
        return builder.build();
    }

    public List<TransactionActionVO> getTransactionActionVOList() {
        return transactionActionVOList;
    }
}
