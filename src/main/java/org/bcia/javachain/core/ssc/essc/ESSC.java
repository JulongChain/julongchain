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
package org.bcia.javachain.core.ssc.essc;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.ProtoUtils;
import org.bcia.javachain.core.smartcontract.shim.impl.Response;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.core.ssc.SystemSmartContractManager;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 背书系统智能合约　Endorse System Smart Contract,ESSC
 *
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class ESSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ESSC.class);

    @Override
    public Response init(ISmartContractStub stub) {
        log.info("Successfully initialized ESSC");
        return newSuccessResponse();
    }

    /**
     * 为特定的proposal进行背书
     * 对输入进行签名，并返回背书结果
     *  Note that Node calls this function with 4 mandatory arguments (and 2 optional ones):
     * args[0] - function name (not used now)
     * args[1] - serialized Header object
     * args[2] - serialized ChaincodeProposalPayload object
     * args[3] - SmartcontractID of executing smartcontract
     * args[4] - result of executing smartcontract
     * args[5] - binary blob of simulation results
     * args[6] - serialized events
     * args[7] - payloadVisibility
     * @param stub
     * @return
     */
    @Override
    public Response invoke(ISmartContractStub stub) {
        log.debug("Enter ESSC invoke function");
        //List<String> args = stub.getStringArgs();
        List<byte[]> args = stub.getArgs();
        int size=args.size();
        if(size<6){
            return newErrorResponse(String.format("Incorrect number of arguments (expected a minimum of 5, provided %d)",args.size()));
        }
        else if(size>8){
            return newErrorResponse(String.format("Incorrect number of arguments (expected a maximum of 7, provided %d)",args.size()));
        }
        log.debug("ESSC starts:{} args",args.size());

        //handle the header
        byte[] headerBytes=args.get(1);
        if(headerBytes.length==0){
            return newErrorResponse("Serialized header object is null");
        }

        //handle the proposal payload
        byte[] payloadBytes=args.get(2);
        if(payloadBytes.length==0){
            return newErrorResponse("Serialized SmartcontractProposalPayload object is null");
        }

        //handle the smartcontractID
        byte[] smartContractIDBytes=args.get(3);
        if(smartContractIDBytes.length==0){
            return newErrorResponse("SmartcontractID is null");
        }

        Smartcontract.SmartContractID smartContractID;
        try {
            smartContractID = ProtoUtils.unmarshalSmartcontractID(smartContractIDBytes);
        }catch(Exception e){
            return newErrorResponse(String.format("Unmarshal SmartcontractID failed:%s", e.getMessage()));
        }

        // handle executing chaincode result
        // Status code < shim.ERRORTHRESHOLD can be endorsed
        //String strResponse=args.get(4);
        byte[] byteResponse = args.get(4);
        if(byteResponse.length==0){
            return newErrorResponse("Response of smartcontract executing is null");
        }
        ProposalResponsePackage.Response proResponse;
        try {
            //proResponse=ProtoUtils.getResponse(strResponse);
            proResponse=ProtoUtils.getResponse(byteResponse);
        } catch (Exception e) {
            return newErrorResponse(String.format("Failed to get Response of executing smartcontract: %s",e.getMessage()));
        }

        if(proResponse.getStatus()>=Response.Status.ERRORTHRESHOLD.getCode()){
            return newErrorResponse(String.format("Status code less than %d will be endorsed, received status code: %d",
                    Response.Status.ERRORTHRESHOLD.getCode(),proResponse.getStatus()));
        }


        //handle simulation results
        byte[] resultBytes=args.get(5);
        if(resultBytes.length==0){
            return newErrorResponse("Simulation results is null");
        }

        // Handle serialized events if they have been provided
        // they might be nil in case there's no events but there
        // is a visibility field specified as the next arg
        byte[] eventBytes=null;
        if(size>6  && args.get(6)!=null){
            eventBytes=args.get(6);
        }

        // Handle payload visibility (it's an optional argument)
        // currently the fabric only supports full visibility: this means that
        // there are no restrictions on which parts of the proposal payload will
        // be visible in the final transaction; this default approach requires
        // no additional instructions in the PayloadVisibility field; however
        // the fabric may be extended to encode more elaborate visibility
        // mechanisms that shall be encoded in this field (and handled
        // appropriately by the peer)
        byte[] visibilityBytes=null;
        if(size>7){
            visibilityBytes=args.get(7);
        }

        // obtain the default signing identity for this peer; it will be used to sign this proposal response
        MockMSP localMSP = MockMspManager.getLocalMSP();
        if(localMSP==null){
            return newErrorResponse("Local MSP is null");
        }
        MockSigningIdentity signingEndorser =localMSP.getDefaultSigningIdentity();
        if(signingEndorser==null){
            return newErrorResponse("Could not obtain the default signing identity");
        }
        ProposalResponsePackage.ProposalResponse proposalResponse;
        try {
            proposalResponse = ProtoUtils.createProposalResponse(headerBytes, payloadBytes,
                    proResponse, resultBytes,
                    eventBytes, smartContractID,
                    visibilityBytes, signingEndorser);
        }catch(Exception e){
            return newErrorResponse(String.format("Create ProposalResponse failed:%s",e.getMessage()));
        }

        byte [] prBytes;
        try {
            prBytes=ProtoUtils.getBytesProposalResponse(proposalResponse);
        }catch(Exception e){
            return newErrorResponse(String.format("Get bytes proposalResponse failed:%s",e.getMessage()));
        }

        ProposalResponsePackage.ProposalResponse pResp;
        try {
            pResp=ProtoUtils.getProposalResponse(prBytes);
        }catch(Exception e){
            return newErrorResponse(String.format("GetProposalResponse failed:%s",e.getMessage()));
        }

        if(pResp==null){
            return newErrorResponse("GetProposalResponse get empty Response");
        }

        log.debug("ESSC exits successfully");
        return newSuccessResponse(prBytes);
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与背书相关的系统智能合约";
    }

}