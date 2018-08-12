/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.common.smartcontractprovider;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.exception.JulongChainException;
import org.bcia.julongchain.common.log.JulongChainLog;
import org.bcia.julongchain.common.log.JulongChainLogFactory;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.SignedScDepSpec;

/**
 * 解析SmartcontractPackage工具类
 *
 * @author sunzongyu
 * @date 2018/05/09
 * @company Dingxuan
 */
public class SmartContractPackageUtil {
    private static JulongChainLog log = JulongChainLogFactory.getLog(SmartContractPackageUtil.class);

    /**
     * 从Envelope中解析处GroupHeader
     * @param env
     * @return
     * @throws JulongChainException
     */
    public static Common.GroupHeader extractGroupHeaderFromEnvelope(Common.Envelope env) throws JulongChainException {
        try {
            Common.Payload payload = Common.Payload.parseFrom(env.getPayload());
            return Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            log.error("Fail to unmarshal");
            throw new JulongChainException(e);
        }
    }

    /**
     * 从Envelope中解析处SignedSmartContractDeploymentSpec
     * @param env
     * @return
     * @throws JulongChainException
     */
    public static SignedScDepSpec.SignedSmartContractDeploymentSpec extractSignedSmartContractDeploymentSpecFromEnvelope(Common.Envelope env) throws JulongChainException {
        try {
            Common.Payload payload = Common.Payload.parseFrom(env.getPayload());
            return SignedScDepSpec.SignedSmartContractDeploymentSpec.parseFrom(payload.getData());
        } catch (InvalidProtocolBufferException e) {
            log.error("Fail to unmarshal");
            throw new JulongChainException(e);
        }
    }
}
