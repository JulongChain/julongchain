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
package org.bcia.julongchain.consenter.common.msgprocessor;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policies.policy.IPolicy;
import org.bcia.julongchain.common.policies.IPolicyManager;
import org.bcia.julongchain.common.util.proto.SignedData;
import org.bcia.julongchain.protos.common.Common;

import java.util.List;

/**
 * @author zhangmingyang
 * @Date: 2018/5/25
 * @company Dingxuan
 */
public class SigFilter implements IRule {
    private static JavaChainLog log = JavaChainLogFactory.getLog(SigFilter.class);
    private String policyName;
    private IPolicyManager support;

    public SigFilter(String policyName, IPolicyManager iPolicyManager) {
        this.policyName = policyName;
        this.support = iPolicyManager;
    }

    @Override
    public void apply(Common.Envelope message) {
        List<SignedData> signedData =null;

        try {
            signedData = SignedData.asSignedData(message);

            IPolicy policy = support.getPolicy(policyName);
            if (policy == null) {
                throw new ValidateException(String.format("could not find policy %s", policyName));
            }
            policy.evaluate(signedData);
        } catch (ValidateException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (PolicyException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }

}
