/**
 * Copyright Aisino. All Rights Reserved.
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
package org.bcia.julongchain.common.policycheck;

import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.protos.node.ProposalPackage;

import java.util.List;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 02/05/18
 * @company Aisino
 */
public interface IPolicyChecker {
    /**
     * CheckPolicy检查通过签名的提议是否有效
     * 传递通道上的策略。
     * @param groupID
     * @param policyName
     * @param signedProposal
     * @throws PolicyException
     */
    void checkPolicy(String groupID, String policyName, ProposalPackage.SignedProposal signedProposal)throws PolicyException;

    /**
     * CheckPolicyBySignedData checks that the passed signed data is valid with the respect to
     * passed policy on the passed channel.
     * CheckPolicyBySignedData检查传入的签名数据是否有效
     * 传递通道上的策略。
     * @param groupID
     * @param policyName
     * @param signedDatas
     * @throws PolicyException
     */
    void checkPolicyBySignedData(String groupID, String policyName, List<SignedData> signedDatas)throws PolicyException;

    /**
     * CheckPolicyNoChannel checks that the passed signed proposal is valid with the respect to
     * passed policy on the local MSP.
     * CheckPolicyNoChannel检查通过签名的提议是否有效
     * 在当地的MSP上通过政策。
     * @param policyName
     * @param signedProposal
     * @throws PolicyException
     */
    void checkPolicyNoGroup(String policyName, ProposalPackage.SignedProposal signedProposal)throws PolicyException;
}
