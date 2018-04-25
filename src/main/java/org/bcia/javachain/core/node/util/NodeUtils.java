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
package org.bcia.javachain.core.node.util;

import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.core.ledger.INodeLedger;
import org.bcia.javachain.core.smartcontract.shim.ISmartContract;
import org.bcia.javachain.protos.common.Common;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/14/18
 * @company Dingxuan
 */
public class NodeUtils {
    public static INodeLedger getLedger(String groupID){
        return null;
    }

    /**
     *  getMSPIDs returns the ID of each application MSP defined on this group
     * @param groupID
     * @return
     */
    public static String[] getMspIDs(String groupID){
        return null;
    }

    /**
     * CreateChainFromBlock creates a new chain from config block
     * @param block
     * @throws JavaChainException
     */
    public static void createChainFromBlock(Common.Block block) throws JavaChainException{

    }

    /**
     * initChain takes care to initialize chain after peer joined, for example deploys system CCs
     * @param groupID
     */
    public static void initChain(String groupID) {
    }

    /**
     * getCurrConfigBlock returns the cached config block of the specified chain.
     * Note that this call returns nil if chain cid has not been created.
     * @param groupID
     * @return
     */
    public static Common.Block getCurrentConfigBlock(String groupID) {
        return null;
    }
}
