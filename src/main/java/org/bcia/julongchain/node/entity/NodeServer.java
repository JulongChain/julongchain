/**
 * Copyright Dingxuan. 2017 All Rights Reserved.
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
package org.bcia.julongchain.node.entity;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.gossip.GossipService;
import org.bcia.julongchain.common.exception.GossipException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.*;
import org.bcia.julongchain.core.aclmgmt.AclManagement;
import org.bcia.julongchain.core.aclmgmt.IAclProvider;
import org.bcia.julongchain.core.admin.AdminServer;
import org.bcia.julongchain.core.endorser.Endorser;
import org.bcia.julongchain.core.events.DeliverEventsServer;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.core.node.NodeConfig;
import org.bcia.julongchain.core.node.NodeConfigFactory;
import org.bcia.julongchain.core.node.grpc.EventGrpcServer;
import org.bcia.julongchain.core.node.grpc.NodeGrpcServer;
import org.bcia.julongchain.core.ssc.ISystemSmartContractManager;
import org.bcia.julongchain.core.ssc.SystemSmartContractManager;
import org.bcia.julongchain.events.producer.EventHubServer;
import org.bcia.julongchain.events.producer.EventsServerConfig;
import org.bcia.julongchain.gossip.GossipServiceUtil;
import org.bcia.julongchain.msp.mgmt.GlobalMspManagement;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.util.NodeConstant;
import org.bcia.julongchain.protos.common.Common;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 节点服务
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class NodeServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeServer.class);

    private static final String PID_FILE_NAME = "node.pid";

    private String cachedEndpoint;

    private Node node;

    private ISystemSmartContractManager systemSmartContractManager;

    public NodeServer(Node node) {
        this.node = node;

        this.systemSmartContractManager = SpringContext.getInstance().getBean(SystemSmartContractManager.class);
    }

    public void start() throws NodeException {
        start(false);
    }

    public void start(boolean devMode) throws NodeException {
        log.info("node server start-----");
        if (devMode) {
            log.info("start by devMode");
        }

        NodeConfig nodeConfig = NodeConfigFactory.getNodeConfig();

        //检查当前的成员服务提供者类型，目前只支持CSP，即密码提供商
        int mspType = GlobalMspManagement.getLocalMsp().getType();
        if (mspType != NodeConstant.PROVIDER_CSP) {
            log.error("Unsupported msp type: " + mspType);
            return;
        }

        log.info("begin to start node, current version： " + NodeConstant.CURRENT_VERSION);

        //获取当前的访问清单提供者
        IAclProvider aclProvider = AclManagement.getACLProvider();

        //初始化账本
//        LedgerMgmt.initialize();
//        ledgermgmt.Initialize(peer.ConfigTxProcessors)
        try {
            LedgerManager.initialize(null);
        } catch (LedgerException e) {
            e.printStackTrace();
        }


        String nodeEndpoint = null;

//        if(StringUtils.isNotBlank(cachedEndpoint)){
//            nodeEndpoint = cachedEndpoint;
//        }else{
//            nodeEndpoint = NodeConfiguration.getLocalAddress()
//        }
        //读取终端地址
        //读取配置文件，形成serverConfig对象
        //TODO:如何将Grpc服务与serverConfig关联

        //启动Node主服务(Grpc Server1)
        startNodeGrpcServer(nodeConfig);

        //启动事件处理服务(Grpc Server2)
        startEventGrpcServer(nodeConfig);

        //创建智能合约支持服务
        //创建Gossip服务
        systemSmartContractManager.registerSysSmartContracts();

        //初始化系统智能合约
        initSysSmartContracts();

        node.initialize(new Node.IGroupCallback() {
            @Override
            public void onGroupInitialized(String groupId) {
                systemSmartContractManager.deploySysSmartContracts(groupId);
            }

            @Override
            public void onGroupsReady(List<String> groupIds) {
                // if (groupIds != null && groupIds.size() > 0) {
                //     startGossipService(groupIds, nodeConfig);
                // }
                try {
                    startGossipService();
                } catch (NodeException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        //记录进程号到文件
        recordPid(nodeConfig);
    }

  // 启动gossip，定时读取数据
  private void startGossipService() throws NodeException {
    //  查询群组
    try {
      List<String> ledgerIds = LedgerManager.getLedgerIDs();
      if (CollectionUtils.isNotEmpty(ledgerIds)) {
        node.setLedgerIds(ledgerIds);
      }
    } catch (LedgerException e) {
      log.error(e.getMessage(), e);
      throw new NodeException(e.getMessage(), e);
    }

    // 启动gossip
    try {
      GossipService gossipService = GossipServiceUtil.startCommitterGossip();
      new Thread() {
        public void run() {
          while (true) {

            // 处理当前所有的群组
            List<String> ledgerIds = node.getLedgerIds();
            log.info("当前所有群组：" + ledgerIds.toString());
            for (String ledgerId : ledgerIds) {
              log.info("开始检查群组[" + ledgerId + "] 是否有新的区块");
              long blockHeight = 0l;
              try {
                blockHeight = LedgerManager.openLedger(ledgerId).getBlockchainInfo().getHeight();
                log.info("当前群组[" + ledgerId + "] 的区块高度是：" + blockHeight);
                if (blockHeight == 0l) {
                  log.info("群组高度为0，退出处理");
                  continue;
                }
                Object data = GossipServiceUtil.getData(gossipService, ledgerId, blockHeight);
                if (data == null) {
                  log.info("当前群组[" + ledgerId + "]" + "没有新的区块[" + blockHeight + "]");
                  continue;
                }
                log.info("当前群组[" + ledgerId + "]" + "发现新的区块[" + blockHeight + "]");
                String blockByteStr = (String) data;
                byte[] blockBytes = BytesHexStrTranslate.toBytes(blockByteStr);
                log.info("开始打印区块==========================================");
                log.info(blockByteStr);
                log.info("结束打印区块==========================================");
                log.info("开始转换区块");
                Common.Block block = Common.Block.parseFrom(blockBytes);
                BlockAndPvtData blockAndPvtData = new BlockAndPvtData(block, null, null);
                log.info("完成转换区块");
                log.info("开始保存区块");
                LedgerManager.openLedger(ledgerId).commitWithPvtData(blockAndPvtData);
                log.info("完成保存区块");
              } catch (LedgerException e) {
                log.error(e.getMessage(), e);
              } catch (GossipException e) {
                log.error(e.getMessage(), e);
              } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage(), e);
              }
            }

            // 1s查询一次
            try {
              Thread.sleep(10000);
            } catch (Exception e) {
              log.error(e.getMessage(), e);
            }
          }
        }
      }.start();
    } catch (GossipException e) {
      log.error(e.getMessage(), e);
      throw new NodeException(e.getMessage(), e);
    }
  }

    private void initSysSmartContracts() {
        log.info("Init system smart contracts");
        systemSmartContractManager.deploySysSmartContracts("");
    }

    /**
     * 开启Node节点主Grpc服务
     *
     * @param nodeConfig
     * @throws NodeException
     */
    private void startNodeGrpcServer(NodeConfig nodeConfig) throws NodeException {
        //从配置中获取要监听的地址和端口
        NetAddress address = null;
        try {
            String listenAddress = nodeConfig.getNode().getListenAddress();
            address = new NetAddress(listenAddress);
        } catch (Exception e) {
            throw new NodeException(e);
        }

        final NodeGrpcServer nodeGrpcServer = new NodeGrpcServer(address.getPort());
        //绑定背书服务
        nodeGrpcServer.bindEndorserServer(new Endorser(null));
        //绑定投递事件服务
        nodeGrpcServer.bindDeliverEventsServer(new DeliverEventsServer());
        //绑定管理服务
        nodeGrpcServer.bindAdminServer(new AdminServer());

        new Thread() {
            @Override
            public void run() {
                try {
                    nodeGrpcServer.start();
                    nodeGrpcServer.blockUntilShutdown();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }.start();
    }

    /**
     * 开启事件Grpc服务
     *
     * @param nodeConfig
     * @throws NodeException
     */
    private void startEventGrpcServer(NodeConfig nodeConfig) throws NodeException {
        //从配置中获取要监听的地址和端口
        NodeConfig.Events eventsConfig = null;
        NetAddress address = null;
        try {
            eventsConfig = nodeConfig.getNode().getEvents();
            String eventAddress = eventsConfig.getAddress();
            address = new NetAddress(eventAddress);
        } catch (Exception e) {
            throw new NodeException(e);
        }

        EventGrpcServer eventGrpcServer = new EventGrpcServer(address.getPort());

        EventsServerConfig serverConfig = new EventsServerConfig(eventsConfig.getBuffersize(), eventsConfig
                .getTimeout(), eventsConfig.getTimewindow(), null);
        //绑定事件服务
        eventGrpcServer.bindEventHubServer(new EventHubServer(serverConfig));

        new Thread() {
            @Override
            public void run() {
                try {
                    eventGrpcServer.start();
                    eventGrpcServer.blockUntilShutdown();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }.start();
    }

    private void startGossipService(List<String> groupIds, NodeConfig nodeConfig) {
//        String groupId, String nodeId, String nodeAddress, String consenterId,
//                String consenterAddress
        final String nodeId = nodeConfig.getNode().getId();

        final String consenterId = UUID.randomUUID().toString();
        final String consenterAddress = nodeConfig.getNode().getConsenterAddress();

        for (int i = 0; i < groupIds.size(); i++) {
            String groupId = groupIds.get(i);
            final String nodeAddress = "127.0.0.1:" + (50000 + i);

            new Thread() {
                @Override
                public void run() {
                    try {
                        node.getGossipManager().addGossipService(groupId, nodeId, nodeAddress, consenterId, consenterAddress);

                        try {
                            long blockHeight = LedgerManager.openLedger(groupId).getBlockchainInfo().getHeight();

                            while (true) {
                                Common.Block block = node.getGossipManager().acquireMessage(groupId, blockHeight);
                                if (block != null) {
                                    BlockAndPvtData blockAndPvtData = new BlockAndPvtData(block, new HashMap<>(), null);

                                    node.getGroupMap().get(groupId).getCommiter().commitWithPrivateData(blockAndPvtData);
                                }

                                Thread.sleep(nodeConfig.getNode().getGossip().getPullInterval());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }

                    } catch (GossipException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }.start();
        }
    }

    /**
     * 记录进程号
     *
     * @param nodeConfig
     */
    private void recordPid(NodeConfig nodeConfig) {
        //从配置中获取系统路径
        String fileSystemPath = nodeConfig.getNode().getFileSystemPath();
        File file = new File(fileSystemPath);
        file.mkdirs();
        try {
            FileUtils.writeFileBytes(fileSystemPath + CommConstant.PATH_SEPARATOR + PID_FILE_NAME, getProcessId()
                    .getBytes(CommConstant.DEFAULT_CHARSET));
        } catch (IOException e) {
            //TODO 如果没写成功，影响业务吗?暂时不抛异常
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取当前进程id
     *
     * @return
     */
    private String getProcessId() {
        // get name representing the running Java virtual machine.
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        log.info("process name: " + processName);

        String processId = processName.split("@")[0];
        log.info("process id: " + processId);
        return processId;
    }

    public void status() {

    }
}