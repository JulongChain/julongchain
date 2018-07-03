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
package org.bcia.julongchain.core.ssc.cssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.julongchain.common.config.IConfig;
import org.bcia.julongchain.common.config.IConfigManager;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.exception.SysSmartContractException;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.groupconfig.GroupConfigConstant;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policycheck.IPolicyChecker;
import org.bcia.julongchain.common.policycheck.PolicyChecker;
import org.bcia.julongchain.common.policycheck.policies.GroupPolicyManagerGetter;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.common.util.proto.EnvelopeHelper;
import org.bcia.julongchain.core.aclmgmt.AclManagement;
import org.bcia.julongchain.core.aclmgmt.resources.Resources;
import org.bcia.julongchain.core.node.ConfigManager;
import org.bcia.julongchain.core.node.util.NodeUtils;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.ssc.SystemSmartContractBase;
import org.bcia.julongchain.events.producer.BlockEvents;
import org.bcia.julongchain.events.producer.EventHelper;
import org.bcia.julongchain.msp.IMsp;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.msp.mgmt.MSPPrincipalGetter;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.common.Configtx;
import org.bcia.julongchain.protos.node.ProposalPackage;
import org.bcia.julongchain.protos.node.Query;
import org.bcia.julongchain.protos.node.ResourcesPackage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配置系统智能合约　Configure System Smart Contract,CSSC
 * Package cssc smartcontract configer provides functions to manage
 * configuration transactions as the network is being reconfigured. The
 * configuration transactions arrive from the ordering service to the committer
 * who calls this smartcontract. The smartcontract also provides peer configuration
 * services such as joining a chain or getting configuration data.
 *
 * @author sunianle
 * @date 3/5/18
 * @company Dingxuan
 */

@Component
public class CSSC extends SystemSmartContractBase {
    private static JavaChainLog log = JavaChainLogFactory.getLog(CSSC.class);

    public final static String JOIN_GROUP = "JoinGroup";

    public final static String GET_CONFIG_BLOCK = "GetConfigBlock";

    public final static String GET_GROUPS = "GetGroups";

    public final static String GET_CONFIG_TREE = "GetConfigTree";

    public final static String SIMULATE_CONFIG_TREE_UPDATE = "SimulateConfigTreeUpdate";

    private IPolicyChecker policyChecker;

    private IConfigManager configManager;

    public CSSC() {
        log.debug("Construct CSSC");
    }

    // Init is called once per chain when the chain is created.
    // This allows the smartcontract to initialize any variables on the ledger prior
    // to any transaction execution on the chain.
    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
        log.info("Init CSSC");
        IMsp localMSP = GlobalMspManagement.getLocalMsp();
        policyChecker=new PolicyChecker(new GroupPolicyManagerGetter(),localMSP,new MSPPrincipalGetter());
        configManager =new ConfigManager();
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
    public SmartContractResponse invoke(ISmartContractStub stub) {
        log.debug("Enter CSSC invoke function");
        List<byte[]> args = stub.getArgs();
        int size = args.size();
        if (size < 1) {
            return newErrorResponse(String.format("Incorrect number of arguments, %d", size));
        }
        String function = ByteString.copyFrom(args.get(0)).toStringUtf8();
        if (!GET_GROUPS.equals(function) && size < 2) {
            return newErrorResponse(String.format("Incorrect number of arguments, %d, %s", size, function));
        }
        log.debug("Invoke function:{}", function);
        // Handle ACL:
        // 1. get the signed proposal
        ProposalPackage.SignedProposal sp = stub.getSignedProposal();
        switch (function) {
            case JOIN_GROUP:
                byte[] blockBytes = args.get(1);
                if (blockBytes.length == 0) {
                    return newErrorResponse(String.format("Cannot join the channel <nil> configuration block provided"));
                }
                Common.Block block = null;
                String groupID = "";
                try {
                    block = BlockUtils.getBlockFromBlockBytes(blockBytes);
                } catch (InvalidProtocolBufferException e) {
                    return newErrorResponse(String.format("Get genesis block from block bytes failed,%s", e.getMessage()));
                }
                try {
                    groupID = BlockUtils.getGroupIDFromBlock(block);
                } catch (JavaChainException e) {
                    return newErrorResponse(String.format("\"JoinGroup\" request failed to extract group id from the block due to [%s]", e.getMessage()));
                }
                try {
                    validateConfigBlock(block);
                } catch (SysSmartContractException e) {
                    return newErrorResponse(String.format("\"JoinGroup\" request failed because of validation of configuration block:%s", e.getMessage()));
                }
                // 2. check local MSP Admins policy
                try {
                    policyChecker.checkPolicyNoGroup(MSPPrincipalGetter.Admins, sp);
                } catch (PolicyException e) {
                    log.error(e.getMessage());
                    return newErrorResponse(String.format("\"JoinGroup\" request failed authorization check for group [%s]:%s", groupID, e.getMessage()));
                }
                return joinGroup(groupID, block);
            case GET_CONFIG_BLOCK:
                // 2. check policy
                String groupName = ByteString.copyFrom(args.get(1)).toStringUtf8();
                try {
                    AclManagement.getACLProvider().checkACL(Resources.CSSC_GetConfigBlock, groupName, sp);
                } catch (JavaChainException e) {
                    return newErrorResponse(String.format("\"GET_CONFIG_BLOCK\" Authorization request failed %s: %s", groupName, e.getMessage()));
                }
                return getConfigBlock(groupName);
            case GET_CONFIG_TREE:
                String groupName2 = ByteString.copyFrom(args.get(1)).toStringUtf8();
                try {
                    AclManagement.getACLProvider().checkACL(Resources.CSSC_GetConfigTree, groupName2, sp);
                } catch (JavaChainException e) {
                    return newErrorResponse(String.format("\"GET_CONFIG_TREE\" Authorization request failed %s: %s", groupName2, e.getMessage()));
                }
                return getConfigTree(groupName2);
            case SIMULATE_CONFIG_TREE_UPDATE:
                String groupName3 = ByteString.copyFrom(args.get(1)).toStringUtf8();
                try {
                    AclManagement.getACLProvider().checkACL(Resources.CSSC_SimulateConfigTreeUpdate, groupName3, sp);
                } catch (JavaChainException e) {
                    return newErrorResponse(String.format("\"SIMULATE_CONFIG_TREE_UPDATE\" Authorization request failed %s: %s", groupName3, e.getMessage()));
                }
                return simulateConfigTreeUpdate(groupName3, args.get(2));
            case GET_GROUPS:
                // 2. check local MSP Members policy
                // TODO: move to ACLProvider once it will support chainless ACLs
                // 2. check local MSP Admins policy
                try {
                    policyChecker.checkPolicyNoGroup(MSPPrincipalGetter.Members, sp);
                } catch (JavaChainException e) {
                    log.error(e.getMessage());
                    return newErrorResponse(String.format("\"JoinGroup\" request failed authorization check :%s", e.getMessage()));
                }
                SmartContractResponse groups = getGroups();
                return groups;
            default:
                return newErrorResponse(String.format("Invalid Function %s", function));
        }
    }

    @Override
    public String getSmartContractStrDescription() {
        String description = "与配置相关的系统智能合约";
        return description;
    }

    /**
     * validateConfigBlock validate configuration block to see whenever it contains valid config transaction
     *
     * @param block
     * @throws SysSmartContractException
     */
    private void validateConfigBlock(Common.Block block) throws SysSmartContractException {
        Common.Envelope envelopeConfig = null;
        try {
            envelopeConfig = BlockUtils.extractEnvelope(block, 0);
        } catch (Exception e) {
            String msg = String.format("Failed to %s", e.getMessage());
            throw new SysSmartContractException(msg);
        }

        //TODO:modified by zhouhui for test
        //TODO:由于unmarshalEnvelopeOfType不好实现，临时将其修改为具体的ConfigEnvelope读取以便测试通过----------begin
        Configtx.ConfigEnvelope configEnv = null;
        try {
            configEnv = EnvelopeHelper.getConfigEnvelopeFrom(envelopeConfig);
        } catch (InvalidProtocolBufferException e) {
            String msg = String.format("Bad configuration envelope: %s", e.getMessage());
            throw new SysSmartContractException(msg);
        } catch (ValidateException e) {
            String msg = String.format("Bad configuration envelope: %s", e.getMessage());
            throw new SysSmartContractException(msg);
        }
//        Configtx.ConfigEnvelope configEnv = Configtx.ConfigEnvelope.newBuilder().build();
//        Common.GroupHeader header = null;
//        try {
//            header = EnvelopeHelper.unmarshalEnvelopeOfType(envelopeConfig, Common.HeaderType.CONFIG, configEnv);
//        } catch (JavaChainException e) {
//            String msg = String.format("Bad configuration envelope: %s", e.getMessage());
//            throw new SysSmartContractException(msg);
//        }
        //TODO:modified by zhouhui for test
        //TODO:由于unmarshalEnvelopeOfType不好实现，临时将其修改为具体的ConfigEnvelope读取以便测试通过----------end

        if (configEnv.getConfig() == null) {
            String msg = String.format("Nil config");
            throw new SysSmartContractException(msg);
        }
        if (configEnv.getConfig().getGroupTree() == null) {
            String msg = String.format("Nil config tree");
            throw new SysSmartContractException(msg);
        }
        if (configEnv.getConfig().getGroupTree().getChildsMap() == null) {
            String msg = String.format("No child map of config tree  are available");
            throw new SysSmartContractException(msg);
        }

        if (configEnv.getConfig().getGroupTree().getChildsMap().get(GroupConfigConstant.APPLICATION) == null) {
            String msg = String.format("Invalid configuration block, missing %s " +
                    "configuration map", GroupConfigConstant.APPLICATION);
            throw new SysSmartContractException(msg);
        }
    }

    /**
     * joinGroup will join the specified group in the configuration block.
     * Since it is the first block, it is the genesis block containing configuration
     * for this group, so we want to update the group object with this info
     *
     * @param groupID
     * @param block
     * @return
     */
    private SmartContractResponse joinGroup(String groupID, Common.Block block) {
        try {
            NodeUtils.createChainFromBlock(block);
        } catch (JavaChainException e) {
            return newErrorResponse(e.getMessage());
        }
        NodeUtils.initChain(groupID);
        BlockEvents events = null;
        boolean bCreated = false;
        try {
            events = EventHelper.createBlockEvents(block);
            bCreated = true;
            EventHelper.send(events);
        } catch (Exception e) {
            String msg = "";
            if (bCreated) {
                msg = String.format("Error processing block events for block number [%d]: %s",
                        block.getHeader().getNumber(), e.getMessage());
                log.error(msg);
            } else {
                msg = String.format("Group [%s] Error sending block event for block number [%d]: %s",
                        groupID, block.getHeader().getNumber(), e.getMessage());
                log.error(msg);
            }
        }
        return newSuccessResponse();
    }

    /**
     * Return the current configuration block for the specified chainID. If the
     * peer doesn't belong to the chain, return error
     *
     * @param groupID
     * @return
     */
    private SmartContractResponse getConfigBlock(String groupID) {
        Common.Block block = NodeUtils.getCurrentConfigBlock(groupID);
        if (block == null) {
            String msg = String.format("Unknown group ID,%s", groupID);
            return newErrorResponse(msg);
        }
        byte[] blockBytes = block.toByteArray();
        return newSuccessResponse(blockBytes);
    }

    /**
     * getConfigTree returns the current channel and resources configuration for the specified chainID.
     * If the peer doesn't belong to the chain, returns error
     *
     * @param groupID
     * @return
     */
    private SmartContractResponse getConfigTree(String groupID) {
        Configtx.Config groupConfig = configManager.getGroupConfig(groupID).getCurrentConfig();
        if (groupConfig == null) {
            return newErrorResponse(String.format("Unknown group ID,%s", groupID));
        }
        Configtx.Config resourceConfig = configManager.getResourceConfig(groupID).getCurrentConfig();
        if (resourceConfig == null) {
            return newErrorResponse(String.format("Unknown group ID,%s", groupID));
        }
        ResourcesPackage.RootConfigTree.Builder builder = ResourcesPackage.RootConfigTree.newBuilder();
        builder.setGroupConfig(groupConfig);
        builder.setResourcesConfig(resourceConfig);
        ResourcesPackage.RootConfigTree configTree = builder.build();
        byte[] configTreeBytes = configTree.toByteArray();
        return newSuccessResponse(configTreeBytes);
    }

    /**
     * 模拟执行配置树的更新
     *
     * @param groupID
     * @param envb
     * @return
     */
    private SmartContractResponse simulateConfigTreeUpdate(
            String groupID, byte[] envb
    ) {
        if (groupID == null || groupID.isEmpty()) {
            return newErrorResponse("Group ID must not be nil");
        }
        if (envb == null || envb.length == 0) {
            return newErrorResponse("Config delta bytes must not be nil");
        }
        Common.Envelope envlope = null;
        IConfig config = null;
        try {
            envlope = Common.Envelope.parseFrom(envb);
            config = supportByType(groupID, envlope);
            config.updateProposeConfig(envlope);
        } catch (InvalidProtocolBufferException e) {
            return newErrorResponse(e.getMessage());
        } catch (SysSmartContractException e) {
            return newErrorResponse(e.getMessage());
        } catch (JavaChainException e) {
            return newErrorResponse(e.getMessage());
        }
        return newSuccessResponse("Simulation is successful");
    }

    /**
     * @param groupID
     * @param env
     * @return
     * @throws SysSmartContractException
     */
    private IConfig supportByType(String groupID, Common.Envelope env) throws SysSmartContractException {
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(env.getPayload());
        } catch (InvalidProtocolBufferException e) {
            String msg = String.format("Failed unmarshaling payload: %s", e.getMessage());
            throw new SysSmartContractException(msg);
        }
        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            String msg = String.format("Failed unmarshaling payload header: %s", e.getMessage());
            throw new SysSmartContractException(msg);
        }
        switch (groupHeader.getType()) {
            case Common.HeaderType.CONFIG_UPDATE_VALUE:
                return configManager.getGroupConfig(groupID);
            case Common.HeaderType.NODE_RESOURCE_UPDATE_VALUE:
                return configManager.getResourceConfig(groupID);
        }
        String msg = String.format("Invalid payload header type: %d", groupHeader.getType());
        throw new SysSmartContractException(msg);
    }

    /**
     * getGroups returns information about all groups for this node
     *
     * @return
     */
    private SmartContractResponse getGroups() {
        List<Query.GroupInfo> groupInfoList = NodeUtils.getGroupsInfo();
        Query.GroupQueryResponse.Builder builder = Query.GroupQueryResponse.newBuilder().addAllGroups(groupInfoList);
        Query.GroupQueryResponse groupQueryResponse = builder.build();
        byte[] gqrBytes = groupQueryResponse.toByteArray();
        return newSuccessResponse(gqrBytes);
    }
}
