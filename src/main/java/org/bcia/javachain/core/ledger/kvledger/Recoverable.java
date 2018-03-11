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
package org.bcia.javachain.core.ledger.kvledger;

import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.protos.common.Common;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public interface Recoverable {

    /** ShouldRecover return whether recovery is need.
     * If the recovery is needed, this method also returns the block number to start recovery from.
     * lastAvailableBlock is the max block number that has been committed to the block storage
     */
    Boolean shouldRecover(Long lastAvailableBlock) throws LedgerException;

    /** CommitLostBlock recommits the block
     *
     * @param block
     * @throws LedgerException
     */
    void commitLostBlock(Common.Block block) throws LedgerException;

}
