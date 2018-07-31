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
package org.bcia.julongchain.core.ssc.essc;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.proto.ProposalResponseUtils;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.ssc.SystemSmartContractBase;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.ISigningIdentity;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.protos.node.ProposalResponsePackage;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 背书系统智能合约　Endorse System Smart Contract,ESSC
 * 实现默认的背书策略，对proposal哈希值和读写集合做签名
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */
@Component
public class ESSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(ESSC.class);

    /**
     * Init is called once when the smartcontract started the first time
     * @param stub
     * @return
     */
    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
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
     * @return 整理后的（marshalled）proposal response
     */
    @Override
    public SmartContractResponse invoke(ISmartContractStub stub) {
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
            return newErrorResponse("Serialized header object is empty");
        }

        //handle the proposal payload
        byte[] payloadBytes=args.get(2);
        if(payloadBytes.length==0){
            return newErrorResponse("Serialized smartcontract proposal payload object is empty");
        }

        //handle the smartcontractID
        byte[] smartContractIDBytes=args.get(3);
        if(smartContractIDBytes.length==0){
            return newErrorResponse("SmartcontractID is empty");
        }

        SmartContractPackage.SmartContractID smartContractID=null;
        try {
            smartContractID = ProtoUtils.unmarshalSmartcontractID(smartContractIDBytes);
        }catch(Exception e){
            return newErrorResponse(String.format("Unmarshal SmartcontractID failed:%s", e.getMessage()));
        }

        // handle executing smartcontract result
        // Status code < shim.ERRORTHRESHOLD can be endorsed
        //String strResponse=args.get(4);
        byte[] responseBytes = args.get(4);
        if(responseBytes.length==0){
            return newErrorResponse("SmartContractResponse of smartcontract executing is empty");
        }
        ProposalResponsePackage.Response proResponse=null;
        try {
            proResponse=ProtoUtils.getResponse(responseBytes);
        } catch (Exception e) {
            return newErrorResponse(String.format("Failed to get SmartContractResponse of executing smartcontract: %s",e.getMessage()));
        }

        if(proResponse.getStatus()>= SmartContractResponse.Status.ERRORTHRESHOLD.getCode()){
            return newErrorResponse(String.format("Status code less than %d will be endorsed, received status code: %d",
                    SmartContractResponse.Status.ERRORTHRESHOLD.getCode(),proResponse.getStatus()));
        }


        //handle simulation results
		// TODO: 7/2/18 install时会因为不需要指定groupID导致获取simulator失败
        byte[] resultBytes=args.get(5);
		if (resultBytes.length == 0) {
			log.warn("Simulation results is empty");
//            return newErrorResponse("Simulation results is empty");
		}

        // Handle serialized events if they have been provided
        // they might be nil in case there's no events but there
        // is a visibility field specified as the next arg
        byte[] eventBytes=null;
        if(size>6  && args.get(6).length!=0){
            eventBytes=args.get(6);
        }

        // Handle payload visibility (it's an optional argument)
        // currently the julongchain only supports full visibility: this means that
        // there are no restrictions on which parts of the proposal payload will
        // be visible in the final transaction; this default approach requires
        // no additional instructions in the PayloadVisibility field; however
        // the julongchain may be extended to encode more elaborate visibility
        // mechanisms that shall be encoded in this field (and handled
        // appropriately by the peer)
        byte[] visibilityBytes=null;
        if(size>7){
            visibilityBytes=args.get(7);
        }

        // obtain the default signing identity for this peer; it will be used to sign this proposal response
        IMsp localMSP = GlobalMspManagement.getLocalMsp();
        if(localMSP==null){
            return newErrorResponse("Local MSP is empty");
        }
        ISigningIdentity signingEndorser =localMSP.getDefaultSigningIdentity();
        if(signingEndorser==null){
            return newErrorResponse("Could not obtain the default signing identity");
        }
        ProposalResponsePackage.ProposalResponse proposalResponse=null;
        try {
            proposalResponse= ProposalResponseUtils.buildProposalResponse(headerBytes, payloadBytes,
                                               proResponse, resultBytes,
                                               eventBytes, smartContractID,
                                               visibilityBytes, signingEndorser);
        }catch(Exception e){
            return newErrorResponse(String.format("Create ProposalResponse failed:%s",e.getMessage()));
        }

        byte [] prBytes=null;
        try {
            // marshall the proposal response so that we return its bytes
            prBytes=ProtoUtils.getBytesProposalResponse(proposalResponse);
        }catch(Exception e){
            return newErrorResponse(String.format("Get bytes proposalResponse failed:%s",e.getMessage()));
        }

        ProposalResponsePackage.ProposalResponse pResp=null;
        try {
            pResp=ProtoUtils.getProposalResponse(prBytes);
        }catch(Exception e){
            return newErrorResponse(String.format("GetProposalResponse failed:%s",e.getMessage()));
        }

        if(pResp==null){
            return newErrorResponse("GetProposalResponse get empty SmartContractResponse");
        }

        log.debug("ESSC exits successfully");
        return newSuccessResponse(prBytes);
    }

    @Override
    public String getSmartContractStrDescription() {
        String description="与背书相关的系统智能合约";
        return description;
    }

}