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
package org.bcia.julongchain.core.ledger.kvledger;

import org.bcia.julongchain.common.exception.LedgerException;
import org.bcia.julongchain.core.ledger.BlockAndPvtData;

/**
 * 恢复账本接口
 *
 * @author sunzongyu
 * @date 2018/4/9
 * @company Dingxuan
 */
public interface IRecoverable {

    /** ShouldRecover return whether recovery is need.
     * If the recovery is needed, this method also returns the block number to start recovery from.
     * lastAvailableBlock is the max block number that has been committed to the block storage
     */
    long shouldRecover() throws LedgerException;

    /** CommitLostBlock recommits the block
     *
     */
    void commitLostBlock(BlockAndPvtData blockAndPvtData) throws LedgerException;

}
