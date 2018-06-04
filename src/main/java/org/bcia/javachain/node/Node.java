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
package org.bcia.javachain.node;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.cli.ParseException;
import org.bcia.javachain.common.exception.*;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.groupconfig.GroupConfigBundle;
import org.bcia.javachain.common.groupconfig.IGroupConfigBundle;
import org.bcia.javachain.common.groupconfig.LogSanityChecks;
import org.bcia.javachain.common.groupconfig.config.IApplicationConfig;
import org.bcia.javachain.common.ledger.IResultsIterator;
import org.bcia.javachain.common.ledger.blockledger.IFileLedgerBlockStore;
import org.bcia.javachain.common.ledger.blockledger.file.FileLedger;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.resourceconfig.IResourcesConfigBundle;
import org.bcia.javachain.common.resourceconfig.ResourcesConfigBundle;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.core.commiter.*;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.customtx.IProcessor;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.core.node.ConfigtxProcessor;
import org.bcia.javachain.core.node.GroupSupport;
import org.bcia.javachain.core.node.NodeConfig;
import org.bcia.javachain.core.node.NodeConfigFactory;
import org.bcia.javachain.core.node.util.ConfigTxUtils;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.dxct.GmFactoryOpts;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.msp.mspconfig.MspConfig;
import org.bcia.javachain.node.cmd.INodeCmd;
import org.bcia.javachain.node.cmd.factory.NodeCmdFactory;
import org.bcia.javachain.node.common.helper.ConfigTreeHelper;
import org.bcia.javachain.node.entity.Group;
import org.bcia.javachain.node.util.LedgerUtils;
import org.bcia.javachain.node.util.NodeConstant;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.common.Configuration;
import org.bcia.javachain.protos.common.Ledger;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfigFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.bcia.javachain.msp.mspconfig.MspConfigFactory.loadMspConfig;

/**
 * 节点对象
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class Node {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Node.class);

    private static final String SAMPLE_DEVMODE_SOLO_PROFILE = "SampleDevModeSolo";

    /**
     * 单例模式，确保系统智能合约能够调用到独一实例
     */
    private static Node instance;

    /**
     * 对命令行的支持
     */
    private INodeCmd nodeCmd;

    private GroupSupport groupSupport;

    private Map<String, Group> groupMap = new ConcurrentHashMap<String, Group>();

    private InitializeCallback initializeCallback;

    private Node() throws NodeException {
        init();
    }

    public static Node getInstance() throws NodeException {
        if (instance == null) {
            synchronized (Node.class) {
                if (instance == null) {
                    instance = new Node();
                }
            }
        }

        return instance;
    }

    /**
     * 执行命令行
     *
     * @param args
     * @return
     * @throws ParseException
     * @throws NodeException
     */
    public INodeCmd execCmd(String[] args) throws ParseException, NodeException {
        log.info("Node Command Start");

        if (args.length <= 0) {
            log.warn("Node command need more args-----");
            return null;
        }

        int cmdWordCount;//记录命令单词数量
        String command = args[0];
        if (args.length == 1 && !NodeConstant.VERSION.equalsIgnoreCase(command)) {
            log.warn("Node " + command + " need more args-----");
            return null;
        } else if (args.length == 1 && NodeConstant.VERSION.equalsIgnoreCase(command)) {
            //只有version命令只有一个单词，其余都是"命令+子命令"的形式,如"node server start"
            cmdWordCount = 1;
            nodeCmd = NodeCmdFactory.getInstance(this, command, null);
        } else {
            cmdWordCount = 2;
            String subCommand = args[1];
            nodeCmd = NodeCmdFactory.getInstance(this, command, subCommand);
        }

        if (nodeCmd != null) {
            String[] cleanArgs = new String[args.length - cmdWordCount];
            System.arraycopy(args, cmdWordCount, cleanArgs, 0, cleanArgs.length);
            //只传给子命令正式的参数值
            log.info("Node Command begin");
            nodeCmd.execCmd(cleanArgs);
        }

        log.info("Node Command end");

        return nodeCmd;
    }

    /**
     * 初始化
     */
    private void init() throws NodeException {
        NodeConfig config = NodeConfigFactory.getNodeConfig();
//        try {
//            config = NodeConfigFactory.getNodeConfig();
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new NodeException(e);
//        }

        //初始化MSP
//        String mspConfigDir = config.getNode().getMspConfigPath();
//        String mspId = config.getNode().getLocalMspId();
//        String mspType = config.getNode().getLocalMspType();

//        String mspConfigDir = "D:\\msp";
//        String mspId = "myMspId";
//        String mspType = "csp";

//        if (!FileUtils.isExists(mspConfigDir)) {
//            throw new NodeException("MspConfigPath is not exists");
//        }

        try {
            List<IFactoryOpts> optsList = new ArrayList<IFactoryOpts>();
            MspConfig mspConfig = loadMspConfig();
            String mspConfigDir = mspConfig.getNode().getMspConfigPath();
            String mspId = mspConfig.getNode().getLocalMspId();
            String mspType = mspConfig.getNode().getLocalMspType();
//        String mspId = "myMspId";
//        String mspType = "csp";

            String symmetrickey = mspConfig.node.getCsp().getGm().getSymmetricKey();
            String sign = mspConfig.node.getCsp().getGm().getSign();
            String hash = mspConfig.node.getCsp().getGm().getHash();
            String asymmetric = mspConfig.node.getCsp().getGm().getAsymmetric();
            String privateKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPrivateKeyStore();
            String publicKeyPath = mspConfig.node.getCsp().getGm().getFileKeyStore().getPublicKeyStore();
            //new GmCspConfig(symmetrickey,asymmetric,hash,sign,publicKeyPath,privateKeyPath);
            optsList.add(new GmFactoryOpts(symmetrickey, asymmetric, hash, sign, publicKeyPath, privateKeyPath));

            GlobalMspManagement.loadLocalMspWithType(mspConfigDir, optsList, mspId, mspType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NodeException(e);
        }
    }

    /**
     * 实例化
     *
     * @param callback
     */
    public void initialize(InitializeCallback callback) {
        this.initializeCallback = callback;

        try {
            IProcessor configtxProcessor = new ConfigtxProcessor();
            Map<Common.HeaderType, IProcessor> configtxProcessorMap = new HashMap<>();
            configtxProcessorMap.put(Common.HeaderType.CONFIG, configtxProcessor);
            configtxProcessorMap.put(Common.HeaderType.NODE_RESOURCE_UPDATE, configtxProcessor);

            LedgerManager.initialize(configtxProcessorMap);

            List<String> ledgerIDs = LedgerManager.getLedgerIDs();
            if (ledgerIDs != null && ledgerIDs.size() > 0) {
                for (String ledgerId : ledgerIDs) {
                    log.info("ledgerId-----$" + ledgerId);

                    try {
                        INodeLedger nodeLedger = LedgerManager.openLedger(ledgerId);

                        Common.Block configBlock = LedgerUtils.getConfigBlockFromLedger(nodeLedger);

                        Group group = createGroup(ledgerId, nodeLedger, configBlock);
                        groupMap.put(ledgerId, group);
                        log.info("ledgerId-----$" + ledgerId);

                        if (callback != null) {
                            callback.onGroupInitialized(ledgerId);
                        }
                    } catch (LedgerException e) {
                        log.error(e.getMessage(), e);
                    } catch (ValidateException e) {
                        log.error(e.getMessage(), e);
                    } catch (InvalidProtocolBufferException e) {
                        log.error(e.getMessage(), e);
                    } catch (PolicyException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Group createGroup(String groupId, INodeLedger nodeLedger, Common.Block configBlock) throws
            InvalidProtocolBufferException, LedgerException, ValidateException, PolicyException {
        //从账本中获取群组配置
        Configtx.Config groupConfig = ConfigTxUtils.retrievePersistedGroupConfig(nodeLedger);

        IGroupConfigBundle groupConfigBundle = null;
        if (groupConfig != null) {
            try {
                groupConfigBundle = new GroupConfigBundle(groupId, groupConfig);
            } catch (PolicyException e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        } else {
            Common.Envelope envelope = BlockUtils.extractEnvelope(configBlock, 0);
            groupConfigBundle = GroupConfigBundle.parseFrom(envelope);
        }

        LogSanityChecks.logPolicy(groupConfigBundle);

        //Gossip TODO

        IResourcesConfigBundle.Callback trustedRootsCallback = new IResourcesConfigBundle.Callback() {
            @Override
            public void call(IResourcesConfigBundle bundle) {
                updateTrustedRoots(bundle);
            }
        };

        IResourcesConfigBundle.Callback mspCallback = new IResourcesConfigBundle.Callback() {
            @Override
            public void call(IResourcesConfigBundle bundle) {
//                GlobalMspManagement
                //TODO:
            }
        };

        IApplicationConfig applicationConfig = groupConfigBundle.getGroupConfig().getApplicationConfig();

        final GroupSupport groupSupport = new GroupSupport();
        groupSupport.setApplicationConfig(applicationConfig);
        groupSupport.setNodeLedger(nodeLedger);
        groupSupport.setFileLedger(new FileLedger(new IFileLedgerBlockStore() {
            @Override
            public void addBlock(Common.Block block) throws LedgerException {
            }

            @Override
            public Ledger.BlockchainInfo getBlockchainInfo() throws LedgerException {
                return nodeLedger.getBlockchainInfo();
            }

            @Override
            public IResultsIterator retrieveBlocks(long startBlockNumber) throws LedgerException {
                return nodeLedger.getBlocksIterator(startBlockNumber);
            }
        }));

        IResourcesConfigBundle.Callback nodeSingletonCallback = new IResourcesConfigBundle.Callback() {
            @Override
            public void call(IResourcesConfigBundle bundle) {
                IApplicationConfig config = bundle.getGroupConfigBundle().getGroupConfig().getApplicationConfig();
                groupSupport.setApplicationConfig(config);
                groupSupport.setGroupConfigBundle(bundle.getGroupConfigBundle());
            }
        };

        Configtx.Config resConfig = Configtx.Config.getDefaultInstance();
        if (applicationConfig != null && applicationConfig.getCapabilities() != null && applicationConfig
                .getCapabilities().isResourcesTree()) {
            //从账本中获取群组配置
            resConfig = ConfigTxUtils.retrievePersistedGroupConfig(nodeLedger);
        }

        List<IResourcesConfigBundle.Callback> callbackList = new ArrayList<>();
        callbackList.add(trustedRootsCallback);
        callbackList.add(mspCallback);
        callbackList.add(nodeSingletonCallback);

        ResourcesConfigBundle resourcesConfigBundle = new ResourcesConfigBundle(groupId, resConfig,
                groupConfigBundle, callbackList);

        ICommitterValidator committerValidator = new CommitterValidator(new CommitterSupport(groupSupport));

        ICommitter committer = new Committer(nodeLedger, new Committer.IConfigBlockEventer() {
            @Override
            public void event(Common.Block block) throws CommitterException {
                try {
                    String groupIDFromBlock = BlockUtils.getGroupIDFromBlock(block);
                    onConfigBlockChanged(groupIDFromBlock, block);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new CommitterException(e);
                }
            }
        });

        //TODO
        Configuration.ConsenterAddresses consenterAddresses = groupConfigBundle.getGroupConfig().getConsenterAddresses();
        if (consenterAddresses == null || consenterAddresses.getAddressesCount() <= 0) {
            throw new ValidateException("consenterAddresses can not be null");
        }

        //TODO:Gossip

        groupSupport.setGroupConfigBundle(groupConfigBundle);
        groupSupport.setResourcesConfigBundle(resourcesConfigBundle);

        Group group = new Group();
        group.setGroupSupport(groupSupport);
        group.setBlock(configBlock);
        group.setCommiter(committer);

        groupMap.put(groupId, group);

        return group;
    }

    private void onConfigBlockChanged(String groupIDFromBlock, Common.Block newBlock) {
        Group group = groupMap.get(groupIDFromBlock);
        if (group != null) {
            group.setBlock(newBlock);
        }
    }

    private void updateTrustedRoots(IResourcesConfigBundle bundle) {
        //TODO:
    }

    public Map<String, Group> getGroupMap() {
        return groupMap;
    }

    public InitializeCallback getInitializeCallback() {
        return initializeCallback;
    }

    public interface InitializeCallback {
        void onGroupInitialized(String groupId);
    }

    //MockInitialize resets chains for test env
    public void mockInitialize() {
        try {
            new LedgerManager().initializeTestEnvWithCustomProcessors(null);
        } catch (LedgerException e) {
            e.printStackTrace();
        }
//        chains.list = nil
//        chains.list = make(map[string]*chain)
//        chainInitializer = func(string) { return }
    }

    public void mockCreateGroup(String groupId) throws NodeException {
        Group group = groupMap.get(groupId);

        if (group == null || group.getGroupSupport() == null || group.getGroupSupport().getNodeLedger() == null) {
            GenesisConfig.Profile profile = null;
            try {
                profile = GenesisConfigFactory.loadGenesisConfig().getCompletedProfile
                        (SAMPLE_DEVMODE_SOLO_PROFILE);
                Configtx.ConfigTree groupTree = ConfigTreeHelper.buildGroupTree(profile);

                GenesisBlockFactory genesisBlockFactory = new GenesisBlockFactory(groupTree);
                Common.Block genesisBlock = genesisBlockFactory.getGenesisBlock(groupId);

                INodeLedger nodeLedger = LedgerManager.createLedger(genesisBlock);

                Group newGroup = new Group();
                GroupSupport groupSupport = new GroupSupport();
                groupSupport.setNodeLedger(nodeLedger);
                newGroup.setGroupSupport(groupSupport);
                groupMap.put(groupId, newGroup);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new NodeException(e);
            } catch (LedgerException e) {
                log.error(e.getMessage(), e);
                throw new NodeException(e);
            } catch (JavaChainException e) {
                log.error(e.getMessage(), e);
                throw new NodeException(e);
            }
        }
    }

}
