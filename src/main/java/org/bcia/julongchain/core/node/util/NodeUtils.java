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
package org.bcia.julongchain.core.node.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.JavaChainException;
import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.resourceconfig.IResourcesConfigBundle;
import org.bcia.julongchain.common.util.proto.BlockUtils;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.julongchain.node.Node;
import org.bcia.julongchain.node.entity.Group;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.node.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 节点提供的一些全局静态函数，可用于创建账本、初始化账本、获取msp id、获取配置区块等，可供系统智能合约等调用。
 *
 * @author sunianle
 * @date 3/14/18
 * @company Dingxuan
 */
public class NodeUtils {
    private static final JavaChainLog log = JavaChainLogFactory.getLog(NodeUtils.class);

    public static INodeLedger getLedger(String groupId) {
//        if (StringUtils.isBlank(groupId)) {
//            return null;
//        }
//
//        PolicyNode node = null;
//        try {
//            node = PolicyNode.getInstance();
//        } catch (NodeException e) {
//            log.error(e.getMessage(), e);
//        }
//
//        if (node != null) {
//            Map<String, Group> groupMap = node.getGroupMap();
//            Group group = groupMap.get(groupId);
//            if (group != null) {
//                return group.getGroupSupport().getNodeLedger();
//            }
//        }
//        return null;

        INodeLedger l = null;
        try {
            LedgerManager.initialize(null);
            l = LedgerManager.openLedger(groupId);
        } catch (LedgerException e) {
            e.printStackTrace();
        }

        return l;
    }

    /**
     * getMSPIDs returns the ID of each application MSP defined on this group
     *
     * @param groupId
     * @return
     */
    public static String[] getMspIDs(String groupId) {
        if (StringUtils.isBlank(groupId)) {
            return null;
        }

        Node node = null;
        try {
            node = Node.getInstance();
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
        }

        if (node != null) {
            Map<String, Group> groupMap = node.getGroupMap();
            Group group = groupMap.get(groupId);
            if (group != null) {

                //TODO:是否是取应用--》组织
                return group.getGroupSupport().getMspIds();
            }
        }

        return null;
    }

    /**
     * CreateChainFromBlock creates a new chain from config block
     *
     * @param block
     * @throws JavaChainException
     */
    public static void createChainFromBlock(Common.Block block) throws JavaChainException {
        String groupId = BlockUtils.getGroupIDFromBlock(block);
        INodeLedger ledger = LedgerManager.createLedger(block);

        Node node = null;
        try {
            node = Node.getInstance();
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
        }

        if (node != null) {
            try {
                node.createGroup(groupId, ledger, block);
            } catch (InvalidProtocolBufferException e) {
                log.error(e.getMessage(), e);
                throw new JavaChainException(e);
            }
        }
    }

    /**
     * initChain takes care to initialize chain after peer joined, for example deploys system CCs
     *
     * @param groupId
     */
    public static void initChain(String groupId) {
        Node node = null;
        try {
            node = Node.getInstance();
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
        }

        if (node != null && node.getGroupCallback() != null) {
            node.getGroupCallback().onGroupInitialized(groupId);
        }
    }

    /**
     * getCurrConfigBlock returns the cached config block of the specified chain.
     * Note that this call returns nil if chain cid has not been created.
     *
     * @param groupId
     * @return
     */
    public static Common.Block getCurrentConfigBlock(String groupId) {
        if (StringUtils.isBlank(groupId)) {
            return null;
        }

        Node node = null;
        try {
            node = Node.getInstance();
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
        }

        if (node != null) {
            Map<String, Group> groupMap = node.getGroupMap();
            Group group = groupMap.get(groupId);
            if (group != null) {
                return group.getBlock();
            }
        }

        return null;
    }

    public static List<Query.GroupInfo> getGroupsInfo() {
        List<Query.GroupInfo> groupInfoList = new ArrayList<Query.GroupInfo>();

        try {
            Node node = Node.getInstance();
            Map<String, Group> groupMap = node.getGroupMap();

            for (String groupId: groupMap.keySet()) {
                Query.GroupInfo groupInfo = Query.GroupInfo.newBuilder().setGroupId(groupId).build();
                groupInfoList.add(groupInfo);
                log.info("GroupId: " + groupId);
            }
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
        }

        return groupInfoList;
    }

    public static IResourcesConfigBundle getResourcesConfigBundle(String groupId) {
        Node node = null;
        try {
            node = Node.getInstance();
        } catch (NodeException e) {
            log.error(e.getMessage(), e);
        }

        if (node != null) {
            Map<String, Group> groupMap = node.getGroupMap();
            Group group = groupMap.get(groupId);
            if (group != null) {
                return group.getGroupSupport().getResourcesConfigBundle();
            }
        }

        return null;
    }
}
