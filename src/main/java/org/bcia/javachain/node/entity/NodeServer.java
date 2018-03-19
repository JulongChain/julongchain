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
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.node.common.helper.MockMSPManager;
import org.bcia.javachain.node.util.NodeConstant;
import org.springframework.stereotype.Component;

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

    public void start() {
        start(false);
    }

    public void start(boolean devMode) {
        log.info("node server start-----");
        if(devMode){
            log.info("start by devMode");
        }

        //检查当前的成员服务提供者类型，目前只支持CSP，即密码提供商
        int mspType = MockMSPManager.getLocalMSP().getType();
        if (mspType != NodeConstant.PROVIDER_CSP) {
            log.error("Unsupported msp type: " + mspType);
            return;
        }

        //获取当前的访问清单提供者
        IAclProvider aclProvider = AclManagement.getACLProvider();

        //初始化账本
        //ledgermgmt.Initialize(peer.ConfigTxProcessors)





    }

    public void status() {

    }

}
