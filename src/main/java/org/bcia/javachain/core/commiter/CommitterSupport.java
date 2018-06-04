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
package org.bcia.javachain.core.commiter;

import org.bcia.javachain.common.exception.CommitterException;
import org.bcia.javachain.common.groupconfig.capability.IApplicationCapabilities;
import org.bcia.javachain.common.resourceconfig.ISmartContractDefinition;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.node.GroupSupport;
import org.bcia.javachain.core.node.INodeSupport;
import org.bcia.javachain.core.node.NodeSupport;
import org.bcia.javachain.msp.IMspManager;
import org.bcia.javachain.protos.common.Configtx;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/06/03
 * @company Dingxuan
 */
public class CommitterSupport implements ICommitterSupport {
    private GroupSupport groupSupport;
    private INodeSupport nodeSupport;

    public CommitterSupport(GroupSupport groupSupport) {
        this.groupSupport = groupSupport;

        this.nodeSupport = new NodeSupport();
    }

    @Override
    public void acquire(long n) throws CommitterException {

    }

    @Override
    public void release(long n) {

    }

    @Override
    public INodeLedger getLedger() {
        return groupSupport.getNodeLedger();
    }

    @Override
    public IMspManager getMspManager() {
        return null;
    }

    @Override
    public void apply(Configtx.ConfigEnvelope configtx) throws CommitterException {

    }

    @Override
    public String[] getMspIds(String groupId) {
        return groupSupport.getMspIds();
    }

    @Override
    public IApplicationCapabilities getCapabilities() {
        return null;
    }

    @Override
    public ISmartContractDefinition getSmartContractByName(String groupId, String scName) {
        return nodeSupport.getSmartContractByName(groupId, scName);
    }
}
