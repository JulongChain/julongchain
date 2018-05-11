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
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.groupconfig.GroupConfigBundle;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.proto.BlockUtils;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.core.node.GroupSupport;
import org.bcia.javachain.core.node.util.ConfigTxUtils;
import org.bcia.javachain.csp.factory.IFactoryOpts;
import org.bcia.javachain.csp.gm.GmFactoryOpts;
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
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfigFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
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
//        NodeConfig config = null;
//        try {
//            config = NodeConfigFactory.loadNodeConfig();
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


//        CspConfig
//        Mgmt.loadLocalMspWithType()


    }

//    public void mockInitialize() {
//        LedgerMgmt.InitializeTestEnvWithCustomProcessors();
//    }
//
//    private Map<String, Chain>
//
//
//    func MockInitialize() {
//        ledgermgmt.InitializeTestEnvWithCustomProcessors(null);
//        chains.list = nil
//        chains.list = make(map[string] * chain)
//        chainInitializer = func(string) {
//            return
//        }
//    }
//
//    private static class Chain {
//        private chainSupport cs;
//        private Common.Block block;
//        private Committer committer;
//
//    }
//
//    private static class chainSupport {
//        private BundleSource bundleSource;
//        private IResourcesConfig resources;
//        private IApplicationConfig applicationConfig;
//        private INodeLedger ledger;
//        private FileLedger fileLedger;
//    }

    /**
     * 实例化
     *
     * @param callback
     */
    public void initialize(InitializeCallback callback) {
        this.initializeCallback = callback;

        try {
            LedgerManager.initialize(null);

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
                    }
                }
            }
        } catch (LedgerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Group createGroup(String groupId, INodeLedger nodeLedger, Common.Block configBlock) throws
            InvalidProtocolBufferException, LedgerException, ValidateException {
        //从账本中获取群组配置
        Configtx.Config groupConfig = ConfigTxUtils.retrievePersistedGroupConfig(nodeLedger);

        GroupConfigBundle groupConfigBundle = null;
        if (groupConfig != null) {
            groupConfigBundle = new GroupConfigBundle();
        } else {
            Common.Envelope envelope = BlockUtils.extractEnvelope(configBlock, 0);


        }

        GroupSupport groupSupport = new GroupSupport();

        Group group = new Group();
        groupMap.put(groupId, group);

        return group;
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
