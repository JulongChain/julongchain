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
package org.bcia.julongchain.core.smartcontract;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.common.smartcontractprovider.SmartContractContext;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.protos.node.SmartContractShim;
import org.springframework.stereotype.Component;

/**
 * 智能合约执行器
 *
 * @author zhouhui
 * @date 2018/3/22
 * @company Dingxuan
 */
@Component
public class SmartContractExecutor {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SmartContractExecutor.class);

    private SmartContractSupport scSupport = new SmartContractSupport();

    /**
     * 执行智能合约
     *
     * @param scContext 智能合约上下文
     * @param spec      智能合约规格（部署/执行）
     * @return
     */
    public Object[] execute(SmartContractContext scContext, Object spec) throws
            SmartContractException {
        //TODO:测试数据
        long timeout = 3000;

        int msgType = 0;
        //spec必须为SmartContractDeploymentSpec或者SmartContractInvocationSpec实例
        if (spec instanceof SmartContractPackage.SmartContractDeploymentSpec) {
            //消息类型为初始化
            msgType = SmartContractShim.SmartContractMessage.Type.INIT_VALUE;
        } else if (spec instanceof SmartContractPackage.SmartContractInvocationSpec) {
            //消息类型为交易
            msgType = SmartContractShim.SmartContractMessage.Type.TRANSACTION_VALUE;
        } else {
            log.error("Unsupported spec");
            throw new SmartContractException("Unsupported spec");
        }

        //启动智能合约
        SmartContractPackage.SmartContractInput scInput = scSupport.launch(scContext, spec);
        if (scInput == null) {
            log.error("launch smart contract fail");
            throw new SmartContractException("launch smart contract fail");
        }

//        List<ByteString> argsList = scInput.getArgsList();
//
//        for (ByteString byteString : argsList) {
//            System.out.println(byteString.toStringUtf8());
//        }

        //TODO:SmartContractInput是否需要再处理?

        SmartContractShim.SmartContractMessage scMessage = buildSmartContractMessage(msgType, scInput.toByteArray(),
                scContext.getTxID(), scContext.getChainID(), scContext.getSignedProposal());
        //执行智能合约
        SmartContractShim.SmartContractMessage responseMessage = scSupport.execute(scContext, scMessage, timeout);

//        try {
//            Query.GroupQueryResponse groupQueryResponse = null;
//            try {
//                groupQueryResponse = Query.GroupQueryResponse.parseFrom(responseMessage.getPayload());
//                for(Query.GroupInfo groupInfo : groupQueryResponse.getGroupsList()){
//                    log.info("groupInfo-----" + groupInfo.getGroupId());
//                }
//            } catch (InvalidProtocolBufferException e) {
//                log.error(e.getMessage(), e);
//            }
//        }catch (Exception ex){
//            log.error(ex.getMessage(), ex);
//        }

        //判断返回结果
        if (responseMessage != null) {
            //完成时返回负载
            if (responseMessage.getType().equals(SmartContractShim.SmartContractMessage.Type.COMPLETED)) {
                ProposalResponsePackage.Response response = null;
                try {
                    response = ProposalResponsePackage.Response.parseFrom(responseMessage.getPayload());
                    log.info(response.getMessage());
                } catch (InvalidProtocolBufferException e) {
                    log.error(e.getMessage(), e);
                    throw new SmartContractException("Wrong Response");
                }
                return new Object[]{response, responseMessage.getSmartContractEvent()};
            } else {
                throw new SmartContractException("execute smart contract fail: " + responseMessage.getPayload().toStringUtf8());
            }
        }

        throw new SmartContractException("Unknown error");
    }

    /**
     * 构造智能合约消息对象
     *
     * @param msgType 消息类型
     * @param payload 消息负载
     * @param txId    交易ID
     * @param groupId 群组ID
     * @return
     */
    private SmartContractShim.SmartContractMessage buildSmartContractMessage(int msgType, byte[] payload, String txId, String groupId, ProposalPackage.SignedProposal signedProposal) {

        SmartContractShim.SmartContractMessage.Builder scMessageBuilder = SmartContractShim.SmartContractMessage
                .newBuilder();

        scMessageBuilder.setTypeValue(msgType);
        scMessageBuilder.setPayload(ByteString.copyFrom(payload));
        scMessageBuilder.setTxid(txId);
        scMessageBuilder.setGroupId(groupId);


        // Common.GroupHeader groupHeader = Common.GroupHeader.newBuilder().setType(Common.HeaderType.ENDORSER_TRANSACTION.getNumber()).build();
        // Common.Header header = Common.Header.newBuilder().setGroupHeader(groupHeader.toByteString()).build();
        // ProposalPackage.Proposal proposal = ProposalPackage.Proposal.newBuilder().setHeader(header
        //         .toByteString()).build();
        // ProposalPackage.SignedProposal signedProposal = ProposalPackage.SignedProposal.newBuilder()
        //         .setProposalBytes(proposal.toByteString()).build();

        scMessageBuilder.setProposal(signedProposal);

        return scMessageBuilder.build();
    }
}
