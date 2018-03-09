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
package org.bcia.javachain.common.util.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ssc.essc.MockSigningIdentity;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;

import java.io.UnsupportedEncodingException;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/8/18
 * @company Dingxuan
 */
public class ProtoUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ProtoUtils.class);

    public static Smartcontract.SmartContractID unmarshalSmartcontractID(String strSmartContractID)
            throws UnsupportedEncodingException, InvalidProtocolBufferException {
        byte[] bytes = strSmartContractID.getBytes("UTF-8");
        Smartcontract.SmartContractID id = Smartcontract.SmartContractID.parseFrom(bytes);
        return id;
    }

    public static ProposalResponsePackage.Response getResponse(String strResponse)
            throws UnsupportedEncodingException, InvalidProtocolBufferException {
        byte[] bytes = strResponse.getBytes("UTF-8");
        ProposalResponsePackage.Response response = ProposalResponsePackage.Response.parseFrom(bytes);
        return response;
    }

    public static ProposalResponsePackage.ProposalResponse createProposalResponse(String header, String payload, ProposalResponsePackage.Response proResponse, String results, String events,
                                                                                  Smartcontract.SmartContractID scID, String visibility,
                                                                                  MockSigningIdentity signingEndorser) {
        log.info("Mock create proposalResponse...");
        log.info("Mock sining proposal...");
        signingEndorser.sign();
        ProposalResponsePackage.ProposalResponse.Builder prBuilder=ProposalResponsePackage.ProposalResponse.newBuilder();
        return prBuilder.build();
    }

    public static byte[] getBytesProposalResponse(ProposalResponsePackage.ProposalResponse proposalResponse) {
        log.info("Mock getBytesProposalResponse...");
        return proposalResponse.toByteArray();
    }

    public static ProposalResponsePackage.ProposalResponse getProposalResponse(byte[] prBytes) throws InvalidProtocolBufferException {
        return ProposalResponsePackage.ProposalResponse.parseFrom(prBytes);
    }
}
