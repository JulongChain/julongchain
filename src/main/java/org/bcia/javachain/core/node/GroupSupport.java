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
package org.bcia.javachain.core.node;

import org.bcia.javachain.common.exception.NodeException;
import org.bcia.javachain.common.groupconfig.IGroupConfigBundle;
import org.bcia.javachain.common.groupconfig.config.IApplicationConfig;
import org.bcia.javachain.common.resourceconfig.IResourcesConfigBundle;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.protos.common.Configtx;

/**
 * 群组支持对象
 *
 * @author zhouhui
 * @date 2018/4/26
 * @company Dingxuan
 */
public class GroupSupport {
    private IResourcesConfigBundle resourcesConfigBundle;
    private IGroupConfigBundle groupConfigBundle;
    private IApplicationConfig applicationConfig;
    private INodeLedger nodeLedger;

    //TODO:需要加入
    //private FileLedger fileLedger;

    public void apply(Configtx.ConfigEnvelope configEnvelope) throws NodeException{

    }

    public INodeLedger getNodeLedger() {
        return nodeLedger;
    }

    public String[] getMspIds(){
        return null;
    }

    public long sequence(){
        return 0;
    }


}
