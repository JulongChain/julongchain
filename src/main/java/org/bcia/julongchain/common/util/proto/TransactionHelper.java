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
package org.bcia.julongchain.common.util.proto;

import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.common.util.ValidateUtils;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.TransactionPackage;
import org.bouncycastle.util.Arrays;

/**
 * 交易帮助类
 *
 * @author zhouhui
 * @date 2018/09/30
 * @company Dingxuan
 */
public class TransactionHelper {
    private static JulongChainLog log = JulongChainLogFactory.getLog(TransactionHelper.class);

    /**
     * 构造单个交易的Transaction对象
     *
     * @param proposalPayload   提案原始的负载
     * @param identity          身份对象
     * @param endorserResponses 提案响应
     * @return
     * @throws ValidateException
     * @throws NodeException
     */
    public static TransactionPackage.Transaction buildSingleTransaction(
            ProposalPackage.SmartContractProposalPayload proposalPayload, ISigningIdentity identity,
            ProposalResponsePackage.ProposalResponse... endorserResponses) throws ValidateException, NodeException {
        ValidateUtils.isNotNull(proposalPayload, "ProposalPayload can not be null");

        /**
         * 交易结构
         * Transaction
         *      \_ TransactionAction (1...n)
         *         |\_ Header (1)
         *          \_ SmartContractActionPayload (1)
         *             |\_ SmartContractProposalPayload (1)
         *              \_ SmartContractEndorsedAction (1)
         *                 |\_ Endorsement (1...n)
         *                  \_ ProposalResponsePayload
         *                      \_ SmartContractAction
         */
        ProposalPackage.SmartContractProposalPayload.Builder clearProposalPayloadBuilder = proposalPayload.toBuilder();
        clearProposalPayloadBuilder.clearTransientMap();
        ProposalPackage.SmartContractProposalPayload clearProposalPayload = clearProposalPayloadBuilder.build();

        TransactionPackage.SmartContractActionPayload.Builder actionPayloadBuilder = TransactionPackage
                .SmartContractActionPayload.newBuilder();
        actionPayloadBuilder.setSmartContractProposalPayload(clearProposalPayload.toByteString());
        actionPayloadBuilder.setAction(buildSmartContractEndorsedAction(endorserResponses));
        TransactionPackage.SmartContractActionPayload actionPayload = actionPayloadBuilder.build();

        TransactionPackage.TransactionAction.Builder transactionActionBuilder = TransactionPackage.TransactionAction
                .newBuilder();
        transactionActionBuilder.setHeader(EnvelopeHelper.buildSignatureHeader(identity).toByteString());
        transactionActionBuilder.setPayload(actionPayload.toByteString());

        TransactionPackage.Transaction.Builder transactionBuilder = TransactionPackage.Transaction.newBuilder();
        transactionBuilder.addActions(transactionActionBuilder);
        return transactionBuilder.build();
    }

    /**
     * 构造SmartContractEndorsedAction对象，合并所有的背书结果及背书者名单
     *
     * @param endorserResponses
     * @return
     * @throws ValidateException
     */
    public static TransactionPackage.SmartContractEndorsedAction buildSmartContractEndorsedAction(
            ProposalResponsePackage.ProposalResponse... endorserResponses) throws ValidateException {
        ValidateUtils.isNotNull(endorserResponses, "EndorserResponses can not be null");

        TransactionPackage.SmartContractEndorsedAction.Builder endorsedActionBuilder = TransactionPackage
                .SmartContractEndorsedAction.newBuilder();

        byte[] bytes = null;
        for (int i = 0; i < endorserResponses.length; i++) {
            ProposalResponsePackage.ProposalResponse endorserResponse = endorserResponses[i];

            if (endorserResponse.getResponse().getStatus() != Common.Status.SUCCESS_VALUE) {
                String msg = "EndorserResponse status error: " + i + "," + endorserResponse.getResponse().getStatus();
                log.warn(msg);

                //不符合要求，跳过
                continue;
            }

            if (bytes == null) {
                //遇到第一个合法背书的负载，记录下来
                bytes = endorserResponse.getResponse().getPayload().toByteArray();
                endorsedActionBuilder.setProposalResponsePayload(endorserResponse.getResponse().getPayload());
            } else {
                //其他背书内容必须与第一个合法背书的内容一致
                if (Arrays.compareUnsigned(bytes, endorserResponse.getResponse().getPayload().toByteArray()) != 0) {
                    String msg = "Should be same payload";
                    log.error(msg);
                    throw new ValidateException(msg);
                }
            }

            //背书信息合并(累加的方式)
            endorsedActionBuilder.addEndorsements(endorserResponse.getEndorsement());
        }

        if (bytes == null) {
            //没有遇到过合法的背书
            String msg = "There is not valid endorser response";
            log.error(msg);
            throw new ValidateException(msg);
        }

        return endorsedActionBuilder.build();
    }
}
