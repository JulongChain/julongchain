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
package org.bcia.julongchain.node.entity;

import org.bcia.julongchain.common.exception.NodeException;
import org.bcia.julongchain.common.groupconfig.IGroupConfigBundle;
import org.bcia.julongchain.common.groupconfig.config.IApplicationConfig;
import org.bcia.julongchain.common.groupconfig.config.IApplicationOrgConfig;
import org.bcia.julongchain.common.ledger.blockledger.file.FileLedger;
import org.bcia.julongchain.common.resourceconfig.IResourcesConfigBundle;
import org.bcia.julongchain.core.commiter.ICommitter;
import org.bcia.julongchain.core.ledger.INodeLedger;
import org.bcia.julongchain.protos.common.Common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 群组对象
 *
 * @author zhouhui
 * @date 2018/4/27
 * @company Dingxuan
 */
public class Group {
    /**
     * 群组Id
     */
    private String groupId;
    /**
     * 群组配置集
     */
    private IGroupConfigBundle groupConfigBundle;
    /**
     * 资源配置集
     */
    private IResourcesConfigBundle resourcesConfigBundle;
    /**
     * 应用配置
     */
    private IApplicationConfig applicationConfig;
    /**
     * 节点账本
     */
    private INodeLedger nodeLedger;
    /**
     * Consenter账本
     */
    private FileLedger fileLedger;
    /**
     * 配置区块（最新有效的）
     */
    private Common.Block configBlock;
    /**
     * 提交者
     */
    private ICommitter commiter;

    /**
     * 获取当前的MspId列表
     *
     * @return
     * @throws NodeException
     */
    public String[] getMspIds() {
        if (applicationConfig != null) {
            Map<String, IApplicationOrgConfig> applicationOrgConfigs = applicationConfig.getApplicationOrgConfigs();

            if (applicationOrgConfigs != null) {
                Iterator<Map.Entry<String, IApplicationOrgConfig>> iterator = applicationOrgConfigs.entrySet()
                        .iterator();

                List<String> mspIdList = new ArrayList<String>();
                while (iterator.hasNext()) {
                    Map.Entry<String, IApplicationOrgConfig> entry = iterator.next();
                    IApplicationOrgConfig orgConfig = entry.getValue();

                    mspIdList.add(orgConfig.getMspId());
                }

                String[] mspIds = new String[mspIdList.size()];
                for (int i = 0; i < mspIdList.size(); i++) {
                    mspIds[i] =  mspIdList.get(i);
                }

                return mspIds;
            }
        }

        return null;
    }

    public long sequence() {
        if (resourcesConfigBundle != null && resourcesConfigBundle.getConfigtxValidator() != null) {
            return resourcesConfigBundle.getConfigtxValidator().getSequence();
        }

        return 0;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public IGroupConfigBundle getGroupConfigBundle() {
        return groupConfigBundle;
    }

    public void setGroupConfigBundle(IGroupConfigBundle groupConfigBundle) {
        this.groupConfigBundle = groupConfigBundle;
    }

    public IResourcesConfigBundle getResourcesConfigBundle() {
        return resourcesConfigBundle;
    }

    public void setResourcesConfigBundle(IResourcesConfigBundle resourcesConfigBundle) {
        this.resourcesConfigBundle = resourcesConfigBundle;
    }

    public IApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public void setApplicationConfig(IApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public INodeLedger getNodeLedger() {
        return nodeLedger;
    }

    public void setNodeLedger(INodeLedger nodeLedger) {
        this.nodeLedger = nodeLedger;
    }

    public FileLedger getFileLedger() {
        return fileLedger;
    }

    public void setFileLedger(FileLedger fileLedger) {
        this.fileLedger = fileLedger;
    }

    public Common.Block getConfigBlock() {
        return configBlock;
    }

    public void setConfigBlock(Common.Block configBlock) {
        this.configBlock = configBlock;
    }

    public ICommitter getCommiter() {
        return commiter;
    }

    public void setCommiter(ICommitter commiter) {
        this.commiter = commiter;
    }
}
