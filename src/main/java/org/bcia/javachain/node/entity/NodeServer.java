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
package org.bcia.javachain.node.entity;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.core.aclmgmt.AclManagement;
import org.bcia.javachain.core.aclmgmt.IAclProvider;
import org.bcia.javachain.core.admin.AdminServer;
import org.bcia.javachain.core.endorser.Endorser;
import org.bcia.javachain.core.events.DeliverEventsServer;
import org.bcia.javachain.core.events.EventGrpcServer;
import org.bcia.javachain.core.events.EventHubServer;
import org.bcia.javachain.core.node.NodeGrpcServer;
import org.bcia.javachain.core.ssc.ISystemSmartContractManager;
import org.bcia.javachain.core.ssc.SystemSmartContractManager;
import org.bcia.javachain.node.Node;
import org.bcia.javachain.node.common.helper.MockMSPManager;
import org.bcia.javachain.node.util.NodeConstant;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 节点服务
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
@Component
public class NodeServer {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeServer.class);

    private String cachedEndpoint;

    private Node node;

    public NodeServer() {
    }

    public NodeServer(Node node) {
        this.node = node;
    }

    public void start() {
        start(false);
    }

    public void start(boolean devMode) {
        log.info("node server start-----");
        if (devMode) {
            log.info("start by devMode");
        }

        //检查当前的成员服务提供者类型，目前只支持CSP，即密码提供商
        int mspType = MockMSPManager.getLocalMSP().getType();
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
        int port = 7051;
        final NodeGrpcServer nodeGrpcServer = new NodeGrpcServer(port);
        //绑定背书服务
        nodeGrpcServer.bindEndorserServer(new Endorser(null));
        //绑定投递事件服务
        nodeGrpcServer.bindDeliverEventsServer(new DeliverEventsServer());
        //绑定管理服务
        nodeGrpcServer.bindAdminServer(new AdminServer());

        //启动事件处理服务(Grpc Server2)
        EventGrpcServer eventGrpcServer = new EventGrpcServer(7053);
        //绑定事件服务
        eventGrpcServer.bindEventHubServer(new EventHubServer());

//        ISmartContractProvider smartContractProvider = new SmartContractProvider();
//        smartContractProvider

        //创建智能合约支持服务
        //创建Gossip服务

        //初始化系统智能合约
        initSysSmartContracts();

//        LedgerMgmt.getLedgerIDs()

        new Thread(){
            @Override
            public void run() {
                try {
                    nodeGrpcServer.start();
                    nodeGrpcServer.blockUntilShutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                try {
                    eventGrpcServer.start();
                    eventGrpcServer.blockUntilShutdown();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private void initSysSmartContracts() {
        log.info("Init system smart contracts");

        ISystemSmartContractManager systemSmartContractManager = new SystemSmartContractManager();
        systemSmartContractManager.deploySysSmartContracts("");
    }

    public void status() {

    }

}
