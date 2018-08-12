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
package org.bcia.julongchain.core.ledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.protos.common.Common;

import java.util.List;
import java.util.Map;

/**
 * 账本服务提供者
 *
 * @author sunzongyu
 * @date 2018/04/02
 * @company Dingxuan
 */
public interface INodeLedgerProvider {

    void initialize(Map<String, IStateListener> stateListeners);

    /**
     * 根据创世区块创建账本
     */
    INodeLedger create(Common.Block genesisBlock) throws LedgerException;

    /**
     * 打开已经存在的账本
     */
    INodeLedger open(String ledgerID) throws LedgerException;

    /**
     * 判断账本是否存在
     */
    Boolean exists(String ledgerID) throws LedgerException;

    /**
     * 列举所有账本
     */
    List<String> list() throws LedgerException;

    /**
     * 关闭账本
     */
    void close() throws LedgerException;

    void recoverUnderConstructionLedger() throws LedgerException;

}
