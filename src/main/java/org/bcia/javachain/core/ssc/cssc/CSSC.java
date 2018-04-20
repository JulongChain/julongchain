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
package org.bcia.javachain.core.ssc.cssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.config.IConfig;
import org.bcia.javachain.common.config.IConfigManager;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.core.aclmgmt.AclManagement;
import org.bcia.javachain.core.aclmgmt.resources.Resources;
import org.bcia.javachain.core.node.ConfigFactory;
import org.bcia.javachain.core.policy.IPolicyChecker;
import org.bcia.javachain.core.policy.PolicyFactory;
import org.bcia.javachain.core.smartcontract.shim.impl.SmartContractResponse;
import org.bcia.javachain.core.smartcontract.shim.intfs.ISmartContractStub;
import org.bcia.javachain.core.ssc.SystemSmartContractBase;
import org.bcia.javachain.msp.mgmt.Principal;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配置系统智能合约　Configure System Smart Contract,CSSC
 * Package cssc smartcontract configer provides functions to manage
 * configuration transactions as the network is being reconfigured. The
 * configuration transactions arrive from the ordering service to the committer
 * who calls this smartcontract. The smartcontract also provides peer configuration
 * services such as joining a chain or getting configuration data.
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */

@Component
public class CSSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CSSC.class);

    public final static String JOIN_GROUP="JoinGroup";

    public final static String GET_CONFIG_BLOCK="GetConfigBlock";

    public final static String GET_GROUPS="GetGroups";

    public final static String GET_CONFIG_TREE="GetConfigTree";

    public final static String SIMULATE_CONFIG_TREE_UPDATE="SimulateConfigTreeUpdate";

    private IPolicyChecker policyChecker;

    private IConfigManager configManager;

    public CSSC(){
        log.debug("Construct CSSC");
    }

    // Init is called once per chain when the chain is created.
    // This allows the smartcontract to initialize any variables on the ledger prior
    // to any transaction execution on the chain.
    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
        log.info("Init CSSC");
        policyChecker= PolicyFactory.getPolicyChecker();
        configManager= ConfigFactory.getConfigManager();
        return newSuccessResponse();
    }

    // Invoke is called for the following:
    // # to process joining a chain (called by app as a transaction proposal)
    // # to get the current configuration block (called by app)
    // # to update the configuration block (called by committer)
    // Peer calls this function with 2 arguments:
    // # args[0] is the function name, which must be JoinChain, GetConfigBlock or
    // UpdateConfigBlock
    // # args[1] is a configuration Block if args[0] is JoinChain or
    // UpdateConfigBlock; otherwise it is the chain id
    // TODO: Improve the scc interface to avoid marshal/unmarshal args
    @Override
    public SmartContractResponse invoke(ISmartContractStub stub){
        log.debug("Enter CSSC invoke function");
        List<byte[]> args = stub.getArgs();
        int size=args.size();
        if(size<1){
            return newErrorResponse(String.format("Incorrect number of arguments, %d",size));
        }
        String function= ByteString.copyFrom(args.get(0)).toStringUtf8();
        if(function!=GET_GROUPS && size<2){
            return newErrorResponse(String.format("Incorrect number of arguments, %d",size));
        }
        log.debug("Invoke function:{}",function);
        // Handle ACL:
        // 1. get the signed proposal
        ProposalPackage.SignedProposal sp = stub.getSignedProposal();
        switch(function){
            case JOIN_GROUP:
                byte[] blockBytes=args.get(1);
                if(blockBytes.length==0){
                    return newErrorResponse(String.format("Cannot join the channel <nil> configuration block provided"));
                }
                Common.Block block=null;
                String groupID="";
                try {
                    block= BlockUtils.getBlockFromBlockBytes(blockBytes);
                } catch (InvalidProtocolBufferException e) {
                    return newErrorResponse(String.format("Get genesis block from block bytes failed,%s",e.getMessage()));
                }
                try {
                    groupID=BlockUtils.getGroupIDFromBlock(block);
                } catch (JavaChainException e) {
                    return newErrorResponse(String.format("\"JoinGroup\" request failed to extract group id from the block due to [%s]",e.getMessage()));
                }
                if(validateConfigBlock(block)==false){
                    return newErrorResponse(String.format("\"JoinGroup\" request failed because of validation of configuration block"));
                }
                // 2. check local MSP Admins policy
//                if(policyChecker.checkPolicyNoGroup(Principal.Admins,sp)==false){
//                    return newErrorResponse(String.format("\"JoinGroup\" request failed authorization check for group [%s]",groupID));
//                }
                return joinGroup(groupID,block);
            case GET_CONFIG_BLOCK:
                // 2. check policy
                String groupName=ByteString.copyFrom(args.get(1)).toStringUtf8();
                if(AclManagement.getACLProvider().checkACL(Resources.CSSC_GetConfigBlock,groupName,sp)==false){
                    return newErrorResponse(String.format("\"GET_CONFIG_BLOCK\" Authorization request failed %s: %s",groupName,Resources.CSSC_GetConfigBlock));
                }
                return getConfigBlock(groupName);
            case GET_CONFIG_TREE:
                String groupName2=ByteString.copyFrom(args.get(1)).toStringUtf8();
                if(AclManagement.getACLProvider().checkACL(Resources.CSSC_GetConfigTree,groupName2,sp)==false){
                    return newErrorResponse(String.format("\"GET_CONFIG_TREE\" Authorization request failed %s: %s",groupName2,Resources.CSSC_GetConfigTree));
                }
                return getConfigTree(groupName2);
            case SIMULATE_CONFIG_TREE_UPDATE:
                String groupName3=ByteString.copyFrom(args.get(1)).toStringUtf8();
                if(AclManagement.getACLProvider().checkACL(Resources.CSSC_SimulateConfigTreeUpdate,groupName3,sp)==false){
                    return newErrorResponse(String.format("\"GET_CONFIG_TREE\" Authorization request failed %s: %s",groupName3,Resources.CSSC_SimulateConfigTreeUpdate));
                }
                return simulateConfigTreeUpdate(groupName3,args.get(2));
            case GET_GROUPS:
                // 2. check local MSP Members policy
                // TODO: move to ACLProvider once it will support chainless ACLs
                // 2. check local MSP Admins policy
//                if(policyChecker.checkPolicyNoGroup(Principal.Members,sp)==false){
//                    return newErrorResponse(String.format("\"GetGroups\" request failed authorization check"));
//                }
                return getGroups();
            default:
                return newErrorResponse(String.format("Invalid Function %s",function));
        }
    }

    @Override
    public String getSmartContractStrDescription() {
        return "与配置相关的系统智能合约";
    }

    //validateConfigBlock validate configuration block to see whenever it's contains valid config transaction
    private boolean validateConfigBlock(Common.Block block){
         return true;
    }

    // joinChain will join the specified chain in the configuration block.
    // Since it is the first block, it is the genesis block containing configuration
    // for this chain, so we want to update the Chain object with this info
    private SmartContractResponse joinGroup(String groupID, Common.Block block){
        return null;
    }

    // Return the current configuration block for the specified chainID. If the
    // peer doesn't belong to the chain, return error
    private SmartContractResponse getConfigBlock(String groupID){
        return null;
    }

    // getConfigTree returns the current channel and resources configuration for the specified chainID.
    // If the peer doesn't belong to the chain, returns error
    private SmartContractResponse getConfigTree(String groupID){
        return null;
    }

    private SmartContractResponse simulateConfigTreeUpdate(
            String groupID,byte[] envb
    ){
        return null;
    }

    private IConfig supportByType(byte[] groupID, Common.Envelope env){
        return null;
    }

    // getGroups returns information about all channels for this peer
    private SmartContractResponse getGroups(){
        return null;
    }
}
