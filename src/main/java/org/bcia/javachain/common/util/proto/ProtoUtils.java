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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.ssc.essc.MockSigningIdentity;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.GmCspFactory;
import org.bcia.javachain.csp.gm.GmFactoryOpts;
import org.bcia.javachain.csp.gm.RngOpts;
import org.bcia.javachain.csp.intfs.ICsp;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static org.bcia.javachain.common.util.proto.ProposalUtils.computeProposalTxID;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/8/18
 * @company Dingxuan
 */
public class ProtoUtils {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ProtoUtils.class);

    public static Smartcontract.SmartContractID unmarshalSmartcontractID(byte[] smartContractIDBytes)
            throws UnsupportedEncodingException, InvalidProtocolBufferException {
        Smartcontract.SmartContractID id = Smartcontract.SmartContractID.parseFrom(smartContractIDBytes);
        return id;
    }

    public static ProposalResponsePackage.ProposalResponse createProposalResponse(byte[] header, byte[] payload, ProposalResponsePackage.Response proResponse, byte[] results, byte[] events,
                                                                                  Smartcontract.SmartContractID scID, byte[] visibility,
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

    // GetEnvelopeFromBlock gets an envelope from a block's Data field.
    public static Common.Envelope getEnvelopeFromBlock(String block)
            throws UnsupportedEncodingException, InvalidProtocolBufferException {
        byte[] bytes = block.getBytes("UTF-8");
        return Common.Envelope.parseFrom(bytes);
    }

    public static Common.Payload getPayload(Common.Envelope envelope)
            throws InvalidProtocolBufferException{
        ByteString byteString = envelope.getPayload();
        Common.Payload payload = Common.Payload.parseFrom(byteString);
        return payload;
    }

    public static Common.GroupHeader unMarshalGroupHeader(ByteString groupHeader) throws InvalidProtocolBufferException {
        return Common.GroupHeader.parseFrom(groupHeader);
    }

    public static TransactionPackage.Transaction getTransaction(ByteString data) throws InvalidProtocolBufferException {
        return TransactionPackage.Transaction.parseFrom(data);
    }

    public static ProposalResponsePackage.Response getResponse(byte[] byteResponse)
                                                   throws InvalidProtocolBufferException {
        ProposalResponsePackage.Response response = ProposalResponsePackage.Response.parseFrom(byteResponse);
        return response;
    }
}
