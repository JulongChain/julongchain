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
package org.bcia.javachain.common.policycheck;

import org.bcia.javachain.common.exception.MspException;
import org.bcia.javachain.common.policycheck.bean.SignedProposal;
import org.bcia.javachain.common.util.proto.SignedData;

import java.util.List;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 02/05/18
 * @company Aisino
 */
public interface IPolicyChecker {
    void checkPolicy(String channelID, String policyName, SignedProposal signedProposal) throws MspException;
    void checkPolicyBySignedData(String channelID, String policyName, List<SignedData> signedDatas);
    void checkPolicyNoChannel(String policyName,SignedProposal signedProposal) throws MspException;
}
